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
    public int choose (int remaining) {
        GameTreeNode root = new GameTreeNode(remaining, 0, true);
        Map <GameTreeNode, Integer> memoBank = new HashMap<GameTreeNode, Integer>();
                
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
  		    	
    		if (curr.isGoal(visited)) { 
    			//retrace through every node above goal node and score the path
    			//node score = off # of actions between node and goal
    			//adding to visited <GameTreeNode, score>
    			
    			GameTreeNode retrace = curr;
    			while (retrace.isMax == false) {
    				
    				
    				
    				retrace = retrace;
    			}
    			
    		}	
    	
            Map<Integer,GameTreeNode> moves = curr.getActions(MAX_REMOVAL);

            //when expanding, after a node is generated and added to the frontier
            //the node should also be added to curr.Children (arrayList of children)
    	
            
    	
    	
    	
    	
    	}
    	
    		
    	
    return 1;	
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
    
    public Map<Integer, GameTreeNode> getActions(int max){
    	int r = this.remaining;
    	Map<Integer, GameTreeNode> possActions = new HashMap<Integer, GameTreeNode>();
    	for (int stoneRemoval = 1; stoneRemoval <= max && stoneRemoval <= r; stoneRemoval++) {
    		GameTreeNode temp = new GameTreeNode(r-stoneRemoval, stoneRemoval, false);
    		possActions.put(temp.action, temp);
    	}
    	return possActions;
    	
    }
    
    public boolean isGoal(Map<GameTreeNode, Integer> visited) {	
    	return (this.remaining == 0 || visited.containsKey(this));    	
    }
      
        
    
	}   
}
