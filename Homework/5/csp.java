package csp;

import java.time.LocalDate;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSP {
	
	 /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
    	ArrayList<Meeting> meetings = new ArrayList<>();
        for (int i = 0; i < nMeetings; i++) {
        	Meeting newMeeting = new Meeting(rangeStart, rangeEnd);
        	meetings.add(newMeeting);
        }
                
        ArrayList<DateConstraint> toAdd = new ArrayList<>();  
        for (DateConstraint i : constraints) {
        	if (i.arity() == 2) {
            	BinaryDateConstraint newConstraint = flip((BinaryDateConstraint)i);
            	toAdd.add(newConstraint);
        	}
        }
        for (DateConstraint i: toAdd) { // done separately to avoid concurrent modification error
        	constraints.add(i);
        }
        
        Set<UnaryDateConstraint> unaryConstraints = new HashSet<>();
        Set<BinaryDateConstraint> binaryConstraints = new HashSet<>();

        for (DateConstraint i: constraints) { 
        	if (i.arity() == 1) {
        		unaryConstraints.add((UnaryDateConstraint)i);
        	} 
        	else {
        		binaryConstraints.add((BinaryDateConstraint)i);
        	}
        }
        
        for (UnaryDateConstraint i : unaryConstraints) {
        	nodeConsistency(meetings.get(i.L_VAL), i);
        	if (meetings.get(i.L_VAL).dateRangeEmpty()) {
        		return null;
        	}
        }

        for (BinaryDateConstraint i : binaryConstraints) {
        	arcConsistency(meetings.get(i.L_VAL), meetings.get( ((BinaryDateConstraint)i).R_VAL), i);
        	if (meetings.get(i.L_VAL).dateRangeEmpty()) {
        		return null;
        	}
        }

        ArrayList<LocalDate> assignment = new ArrayList<LocalDate>();
        for (int i = 0; i < nMeetings; i++) {
        	assignment.add(null);
        }
        
        ArrayList<LocalDate> solution = backtrack(meetings, constraints, assignment, 0);
        return solution;
    }
    
	/**
	 * Flips a binary constraint into a unary constraint
	 * @param int index of meeting that has been assigned
	 * @param LocalDate that meeting has been assigned to
	 * @param Set<DateConstraints> to adjust
	 * @return Set<DateConstraints> where binary constraints containing the assigned meeting have been changed to unary constraints 
	 */
	public static BinaryDateConstraint flip(BinaryDateConstraint i) {
		String[] operators = {"==", "!=", "<", "<=", ">", ">="};
		ArrayList<String> OPERATORS = new ArrayList<>(Arrays.asList(operators));
		
		String[] opposite_operators = {"==", "!=", ">", ">=", "<", "<="};
		ArrayList<String> OPPOSITE_OPERATORS = new ArrayList<>(Arrays.asList(opposite_operators));
		
		BinaryDateConstraint newConstraint = new BinaryDateConstraint(i.R_VAL, OPPOSITE_OPERATORS.get(OPERATORS.indexOf(i.OP)), i.L_VAL);
		
		return newConstraint;
	}
      
    /**
	 * Method that prunes invalid values from a variables domain
	 * @param meeting Meeting being constrained
	 * @param constraint UnaryConstraint to check consistency of
	 */
	public static void nodeConsistency(Meeting meeting, UnaryDateConstraint constraint) {	
		ArrayList<LocalDate> bad = new ArrayList<>();
		
		for (LocalDate i : meeting.dateRange) {
			if (!metConstraint(i, constraint, constraint.R_VAL)) {
				bad.add(i);
			}
		}
		
		for (LocalDate i : bad) {
			meeting.dateRange.remove(i);
		}
	}
	
	/**
	 * Prunes invalid values from tail's domain (assumes tail will be on left side of constraint)
	 * @param Meeting tail
	 * @param Meeting head
	 * @param BinaryDateConstraint to satisfy
	 */
	public static void arcConsistency(Meeting tail, Meeting head, BinaryDateConstraint constraint) {
		ArrayList<LocalDate> good = new ArrayList<>();
		for (LocalDate i : tail.dateRange) {
			for ( LocalDate j : head.dateRange) {
				if (metConstraint(i, constraint, j)) {
					good.add(i);
					break; 
				}
			} 
		}
		tail.dateRange = new ArrayList<>();
		for (LocalDate i : good) {
			tail.dateRange.add(i);
		}
	}
	
	/**
     * Tests whether a given solution to a CSP satisfies all constraints or not
     * @param solution Full instantiation of variables to assigned values, indexed by variable
     * @param constraints The set of constraints the solution must satisfy
     * @return true if all constraints are satisfied, false otherwise
     */
    public static boolean testSolution (List<LocalDate> solution, Set<DateConstraint> constraints) {
        for (DateConstraint i : constraints) {
        	LocalDate leftDate = solution.get(i.L_VAL),
                      rightDate = (i.arity() == 1) ? ((UnaryDateConstraint) i).R_VAL : solution.get(((BinaryDateConstraint) i).R_VAL);
            if (leftDate == null || rightDate == null) {
            	continue;
            }               
            if (!metConstraint(leftDate, i, rightDate)) {
            	return false;
            }
        }
        return true;
    }
	
	/**
     * Given two dates, returns whether the dates satisfy the given constraint
     * @param LocalDate date on left side of equality
     * @param DateConstraint holding constraint operator
     * @param LocalDate date on right side of equality
     */
    public static boolean metConstraint(LocalDate leftDate, DateConstraint constraint, LocalDate rightDate) {
    	 boolean isSatisfied = false;
         switch (constraint.OP) {
         case "==": if (leftDate.isEqual(rightDate)) { isSatisfied  = true; } break;
         case "!=": if (!leftDate.isEqual(rightDate)) { isSatisfied  = true; } break;
         case ">": if (leftDate.isAfter(rightDate)) { isSatisfied  = true; } break;
         case "<": if (leftDate.isBefore(rightDate)) { isSatisfied  = true; } break;
         case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate)) { isSatisfied  = true; } break;
         case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) { isSatisfied  = true; } break;
         }
         return isSatisfied;
     }
    
    
    /**
     * Returns solution to a CSP using backtracking
     * @param ArrayList<Meeting> holds variables, their domains, and their relevant constraints
     * @param Set<DateConstraint> constraints to be satisfied
     * @return List<LocalDate> assignment of dates to meetings
     */
   public static ArrayList<LocalDate> backtrack(ArrayList<Meeting> meetings, Set<DateConstraint> constraints, ArrayList<LocalDate> assignment, int index) {
   	if (testSolution(assignment, constraints) && !assignment.contains(null)) {
   		return assignment;
	    }
   	Meeting newMeetings = meetings.get(index);
   	for (LocalDate i : newMeetings.dateRange) {
   		assignment.set(index, i);
   		if (testSolution(assignment, constraints)) {
   			ArrayList<LocalDate> solution = backtrack(meetings, constraints, assignment, index+1);
   			if (solution != null) {
   				return solution;
	    	}
   		}
   		assignment.set(index, null);
	    }
	    return null;
	}
    
    private static class Meeting {
        
        ArrayList<LocalDate> dateRange = new ArrayList<LocalDate>();
        
        Meeting (LocalDate rangeStart, LocalDate rangeEnd ) {
            while (rangeStart.isBefore(rangeEnd)) {
            	dateRange.add(rangeStart);
            	rangeStart = rangeStart.plusDays(1);
            }
            dateRange.add(rangeStart);
        }
        
        public boolean dateRangeEmpty() {
        	return dateRange.size() == 0;
        }        
    }

    
}
