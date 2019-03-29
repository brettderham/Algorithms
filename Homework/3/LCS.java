package lcs;

import java.util.Set;

public class LCS {
    
    /**
     * memoCheck is used to verify the state of your tabulation after
     * performing bottom-up and top-down DP. Make sure to set it after
     * calling either one of topDownLCS or bottomUpLCS to pass the tests!
     */
    public static int[][] memoCheck;
    
    // -----------------------------------------------
    // Shared Helper Methods
    // -----------------------------------------------
    
    // [!] TODO: Add your shared helper methods here!
    

    // -----------------------------------------------
    // Bottom-Up LCS
    // -----------------------------------------------
    
    /**
     * Bottom-up dynamic programming approach to the LCS problem, which
     * solves larger and larger subproblems iterative using a tabular
     * memoization structure.
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return The longest common subsequence between rStr and cStr +
     *         [Side Effect] sets memoCheck to refer to table
     */
    // Returns length of LCS for X[0..m-1], Y[0..n-1] 
    public static Set<String> bottomUpLCS (String rStr, String cStr) {
    	int[][] lcs = new int [rStr.length()+1][cStr.length()+1];
    	int m = rStr.length();
    	int n = cStr.length();
    	String[][] solution = new String[m][n];
    	for (int i = 0; i <= n; i++) {
			lcs[0][i] = 0;
			solution[0][i] = "0";
		}

		// if B is null then LCS of A, B =0
		for (int i = 0; i <= m; i++) {
			lcs[i][0] = 0;
			solution[i][0] = "0";
		}
		
		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				if (rStr.charAt(m - 1) == cStr.charAt(n - 1)) {
					lcs[i][j] = lcs[i - 1][j - 1] + 1;
					solution[i][j] = "diagonal";
				} else {
					lcs[i][j] = Math.max(lcs[i - 1][j], lcs[i][j - 1]);
					if (lcs[i][j] == lcs[i - 1][j]) {
						solution[i][j] = "top";
					} else {
						solution[i][j] = "left";
					}
				}
			}
		}
		
		String x = solution[m][n];
		String answer = "";
		while (x != "0") {
			if (solution[m][n] == "diagonal") {
				answer = rStr.charAt(m - 1) + answer;
				m--;
				n--;
			} else if (solution[m][n] == "left") {
				n--;
			} else if (solution[m][n] == "top") {
				m--;
			}
			x = solution[m][n];
		}
		System.out.println(answer);
		
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				System.out.print(" " + lcs[i][j]);
			}
			System.out.println();
		}
		return lcs[m][n];
    }
    // [!] TODO: Add any bottom-up specific helpers here!
    
    
    // -----------------------------------------------
    // Top-Down LCS
    // -----------------------------------------------
    
    /**
     * Top-down dynamic programming approach to the LCS problem, which
     * solves smaller and smaller subproblems recursively using a tabular
     * memoization structure.
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return The longest common subsequence between rStr and cStr +
     *         [Side Effect] sets memoCheck to refer to table  
     */
    public static Set<String> topDownLCS (String rStr, String cStr) {
	  	 
        int[][] memo = new int [rStr.length()+1][cStr.length()+1];
    
    	int longestSubstring = lcsRecursiveHelper(rStr, rStr.length(), cStr, cStr.length(), memo);
    	memoCheck = memo;
    	
    	for (int[] x : memo)
    	{
    	   for (int y : x)
    	   {
    	        System.out.print(y + " ");
    	   }
    	   System.out.println();
    	}  	
        throw new UnsupportedOperationException();
    }
    
    /**
     * Completes the memoization table using top-down dynamic programming
     * @param rStr The String along the memoization table's rows
     * @param rInd The current letter's index in rStr
     * @param cStr The String along the memoization table's cols
     * @param cInd The current letter's index in cStr
     * @param memo The memoization table
     */
    static int lcsRecursiveHelper (String rStr, int rInd, String cStr, int cInd, int[][] memo) {
        
    	// base case  
        if (rInd == 0 || cInd == 0) { 
            return 0; 
        } 
  
        // check if its been computed  
        if (memo[rInd][cInd] != 0) { 
            return memo[rInd][cInd]; 
        } 
  
        // if chars match, store the char and recurse on diagonal  
        if (rStr.charAt(rInd - 1) == cStr.charAt(cInd - 1)) { 
            memo[rInd][cInd] = 1 + lcsRecursiveHelper(rStr, rInd - 1, cStr, cInd - 1, memo); 
  
            return memo[rInd][cInd]; 
        } 
        //if chars don't match, save solution for avoiding future work
        else { 
  
            memo[rInd][cInd] = Math.max(lcsRecursiveHelper(rStr, rInd, cStr, cInd - 1, memo), 
                    lcsRecursiveHelper(rStr, rInd - 1, cStr, cInd, memo)); 
  
            return memo[rInd][cInd]; 
        } 
    } 
}
