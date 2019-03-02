package nim;

import java.util.ArrayList;
import java.util.Comparator;
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
    public int choose (int remaining) {
        GameTreeNode root = new GameTreeNode(remaining, 0, true);
        Map <GameTreeNode, Integer> memoBank = new HashMap<GameTreeNode, Integer>();
        int action = 0;
                
    	alphaBetaMinimax(root, Integer.MAX_VALUE, Integer.MIN_VALUE, true, memoBank);
    	
    	for  (int i = 0; i < root.children.size(); i++) {
            GameTreeNode curr = root.children.get(i);
            if (curr.score == 1) {
                return curr.action;
            } else {
                return 1;
            }
        }	
    	//The call to alphaBetaMinMinimax returns an int for a score and
    	//generates our SearchTree. Implement code to select a certain action
    	//path from our tree that will lead to victory
    	
    	throw new UnsupportedOperationException();
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
    		GameTreeNode curr = frontier.remove(0);
			Map<Integer,Integer> moves = curr.getActions(MAX_REMOVAL);    		
  		    	
    		if (curr.isGoal(visited)) {  				
    			if (visited.containsKey(curr)) {
					curr.score=visited.get(curr);
			    }    			
    			if (curr.remaining == 0) {
    				if (isMax) {
    					curr.score = 1;
    				}
    				else curr.score =0; 
    			}
    		return curr.score;
    		} else if (isMax) {    	            
    	        for (Map.Entry<Integer, Integer> actions : moves.entrySet()) {          	
    	        	GameTreeNode temp = new GameTreeNode(actions.getKey(),actions.getValue(), !curr.isMax);            	
    	               	
    	        	if (visited.containsKey(temp)==false) {
    	        		frontier.add(temp);
    	        		curr.children.add(temp);
    	            		
    	        		temp.score = Math.max(0, alphaBetaMinimax(temp, alpha, beta, false, visited));
    	        		alpha = Math.max(alpha, temp.score);
    	        		if (beta <= alpha) 
    	        			break;
    	        		visited.put(temp, temp.score);
    	        		return temp.score;
    	            }
    	        }
    	    } else {
    	         for (Map.Entry<Integer, Integer> actions : moves.entrySet()) {          	
    	        	 GameTreeNode temp = new GameTreeNode(actions.getKey(),actions.getValue(), !curr.isMax);            	
        	               	
    	        	 if (visited.containsKey(temp)==false) {
    	        		 frontier.add(temp);
    	        		 curr.children.add(temp);
        	            		
    	        		 temp.score = Math.min(1, alphaBetaMinimax(temp, alpha, beta, true, visited));
    	        		 beta = Math.min(beta, temp.score);
    	        		 if (beta <= alpha) 
        	            	break;
    	        		 visited.put(temp, temp.score);
    	        		 return temp.score;    	            	
    	        	 }
    	          }
    	    }
    }	   			
    	    	
    return node.score;
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
    
    public Map<Integer, Integer> getActions(int max){
    	int r = this.remaining;
    	Map<Integer, Integer> possActions = new HashMap<Integer, Integer>();
    	for (int stoneRemoval = 1; stoneRemoval <= max && stoneRemoval <= r; stoneRemoval++) {
    		possActions.put(stoneRemoval, r - stoneRemoval);
    	}
    	return possActions;
    	
    }
    
    public boolean isGoal(Map<GameTreeNode, Integer> visited) {	
    	return (this.remaining == 0 || visited.containsKey(this));    	
    }
    
}   
}
