/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  File name     :  MazeProblem.java
 *  Purpose       :  Provides a class describing a Maze Problem and methods to solve
 *  @author       :  Sebastian Grasso / Brett Derham
 *  Date          :  2019-03-01 
 *  Description   :  Artificial Intelligence responsible for playing the game of Nim! Implements the alpha-beta-pruning 
 *                   mini-max search algorithm
 *                   Includes the following:
 *                   
 *                  public int choose(int remaining)
 *                   // Returns the integer indicating the action the agent should take from the given state
 *                   
 *                    private int alphaBetaMinimax (GameTreeNode node, int alpha, int beta, boolean isMax, Map<GameTreeNode, Integer> visited)
 *                   // Constructs the minimax game tree by the tenets of alpha-beta pruning with memoization for repeated states.
 *                   
 *                   class GameTreeNode
 *                   // GameTreeNode to manage the Nim game tree
 *                   
 *                   GameTreeNode (int remaining, int action, boolean isMax)
 *                   // Constructs a new GameTreeNode with the given number of stones remaining in the pile, and the action that led to it.
 *                    We also initialize an empty ArrayList of children that can be added-to during search, and a placeholder score of -1 
 *                    to be updated during search.
 *                   
 *                  public Map<Integer, Integer> getActions(int max)
 *                   // Method used to determine the possible actions from the current node when that node is expanded
 *                
 *                   
 *  Warnings      :  None
 *  Exceptions    :  None
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  Revision History
 *  ---------------
 *            Rev      Date     Modified by:  Reason for change/modification
 *           -----  ----------  ------------  -----------------------------------------------------------
 *  @version 1.0.0  2019-03-01  Authors       Finished homework assignment two
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

package nim;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.HashMap;

/**
 * Artificial Intelligence responsible for playing the game of Nim!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class NimPlayer {
    
    private final int MAX_REMOVAL;
    
    NimPlayer (int MAX_REMOVAL) {
        this.MAX_REMOVAL = MAX_REMOVAL;
    }
    
    
    /**
     * 
     * @param   remaining   Integer representing the amount of stones left in the pile
     * @return  An int action representing the number of stones to remove in the range
     *          of [1, MAX_REMOVAL]
     */
    public int choose(int remaining) {
        GameTreeNode root = new GameTreeNode(remaining, 0, true);
        Map <GameTreeNode, Integer> memoBank = new HashMap<GameTreeNode, Integer>();
        int action = 1;    
        
    	alphaBetaMinimax(root, Integer.MAX_VALUE, Integer.MIN_VALUE, true, memoBank);
    	
    	for  (int i = 0; i < root.children.size(); i++) {
    		GameTreeNode curr = root.children.get(i);
    	    if (curr.score == 1) {
    	    	action = curr.action;
    	    } 
    	}
    	return action;
   }
    
    /**
     * Constructs the minimax game tree by the tenets of alpha-beta pruning with
     * memoization for repeated states.
     * @param   node    The root of the current game sub-tree
     * @param   alpha   Smallest minimax score possible
     * @param   beta    Largest minimax score possible
     * @param   isMax   Boolean representing whether the given node is a max (true) or min (false) node
     * @param   visited Map of GameTreeNodes to their minimax scores to avoid repeating large subtrees
     * @return  Minimax score of the given node + [Side effect] constructs the game tree originating
     *          from the given node
     */
    private int alphaBetaMinimax (GameTreeNode node, int alpha, int beta, boolean isMax, Map<GameTreeNode, Integer> visited) {
        
    	Stack<GameTreeNode> frontier = new Stack<GameTreeNode>();
    	frontier.add(node);
    	
      	while (frontier.isEmpty() == false) {
    		
      		GameTreeNode curr = frontier.pop();
			
      		Map<Integer,Integer> moves = curr.getActions(MAX_REMOVAL);    		
  		    	
    		if (curr.isGoal()) {  				
    			if (isMax) {
    				curr.score = 1;
    			}
    			else curr.score = 0; 
    			return curr.score;
    		}
    		
	        for (Map.Entry<Integer, Integer> actions : moves.entrySet()) {          	
	        	GameTreeNode child = new GameTreeNode(actions.getKey(),actions.getValue(), !curr.isMax); 
	        	visited.put(child, child.score); //right spot for this?   
	       		curr.children.add(child);	
	       		if (curr.isMax) {
	       			if (visited.containsKey(child)) {
	       				child.score = visited.get(child);	
	       			}
	       			else {
	       				child.score = Math.max(child.score, alphaBetaMinimax(child, alpha, beta, false, visited));
	       				alpha = Math.max(alpha, child.score);
	       			}    	        	
    	        	if (alpha <= beta) {
    	    	       	frontier.push(child);
    	    	    }
    	        	return child.score;    	        		    	                	        	
	       		}
	      		else {
	      			if (visited.containsKey(child)) {
	      				child.score = visited.get(child);
	      			}
	      			else {
	      				child.score = Math.min(child.score, alphaBetaMinimax(child, alpha, beta, true, visited));
	      				beta = Math.min(beta, child.score);
	      			}
	      			if (alpha <= beta) {
   	    	       		frontier.push(child);
    	    	    }
    	    	   return child.score;    	        		    	               	        	
    	        }
	        }
      	} 
    	return 0;
}
    
    

    
/**    
 * GameTreeNode to manage the Nim game tree.
 */
class GameTreeNode {
    
    int remaining, action, score;
    boolean isMax;
    ArrayList<GameTreeNode> children;
    
    /**
     * Constructs a new GameTreeNode with the given number of stones
     * remaining in the pile, and the action that led to it. We also
     * initialize an empty ArrayList of children that can be added-to
     * during search, and a placeholder score of -1 to be updated during
     * search.
     * 
     * @param   remaining   The Nim game state represented by this node: the #
     *          of stones remaining in the pile
     * @param   action  The action (# of stones removed) that led to this node
     * @param   isMax   Boolean as to whether or not this is a maxnode
     */
    GameTreeNode (int remaining, int action, boolean isMax) {
        this.remaining = remaining;
        this.action = action;
        this.isMax = isMax;
        children = new ArrayList<>();
        score = -1;
    }
    
    @Override
    public boolean equals (Object other) {
        return other instanceof GameTreeNode 
            ? remaining == ((GameTreeNode) other).remaining && 
              isMax == ((GameTreeNode) other).isMax &&
              action == ((GameTreeNode) other).action
            : false;
    }
    
    @Override
    public int hashCode () {
        return remaining + ((isMax) ? 1 : 0);
    }
    
    /**
     * Method used to determine the possible actions from the current node when that node is expanded
     * 
     * @param   max   Integer representing the max number of stones that can be removed
     * @return  possActions   Returns a HashMap of all the possible actions from the current node
     */
    public Map<Integer, Integer> getActions(int max){
    	int r = this.remaining;
    	Map<Integer, Integer> possActions = new HashMap<Integer, Integer>();
    	for (int stoneRemoval = 1; stoneRemoval <= max && stoneRemoval <= r; stoneRemoval++) {
    		possActions.put(stoneRemoval, r - stoneRemoval);
    	}
    	return possActions;
    	
    }
    
    public boolean isGoal() {	
    	return (this.remaining == 0);    	
    }
    
}   
}
