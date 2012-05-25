import java.util.*;

// Unit test for moving two black turns in a row, and scoring in general.

// Does not handle "En passant" or castling

public class ChessState {
	/*
	 * WHITE
	 * Pawn   = 1
	 * Bishop = 2
	 * Knight = 3
	 * Rook   = 4
	 * Queen  = 5
	 * King   = 6
	 */
	public int[][] grid;
	public ChessState parent = null;
	private String tPlayer = null;
	public int nodesVisited = 0;
	
	public ChessState bestWhiteMove(final int MAX_DEPTH, String method) {
		nodesVisited = 0;
		
		if (method.equals("minimax")) {
			parent = null;
			ChessState bestMoveEndState = maxState(this, 0, MAX_DEPTH);
			ChessState returnMe = bestMoveEndState.getOneBelowTopParent();
			returnMe.nodesVisited = nodesVisited;
			return returnMe;
		} else if (method.equals("alpha-beta")) {
			parent = null;
			ChessState bestMoveEndState = maxStateABq(this, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, MAX_DEPTH);
			ChessState returnMe = bestMoveEndState.getOneBelowTopParent();
			returnMe.nodesVisited = nodesVisited;
			return returnMe;
		} else if (method.equals("null move")) {
			ChessState bestTwoBlackMoveState = bestDoubleMove("black");
			//System.out.println("value of two black moves in a row: " + (bestTwoBlackMoveState.getWhiteScore() - getWhiteScore()));
			
			parent = null;
			ChessState bestMoveEndState = maxStateAB(this, bestTwoBlackMoveState.getWhiteScore(), Integer.MAX_VALUE, 0, MAX_DEPTH);
			ChessState returnMe = bestMoveEndState.getOneBelowTopParent();
			returnMe.nodesVisited = nodesVisited;
			return returnMe;
		} else {
			System.out.println("Bad search method");
			System.exit(0);
			return null;
		}
	}
	
	public ChessState bestBlackMove(final int MAX_DEPTH, String method) {
		nodesVisited = 0;
		
		if (method.equals("minimax")) {
			parent = null;
			ChessState bestMoveEndState = minState(this, 0, MAX_DEPTH);
			ChessState returnMe = bestMoveEndState.getOneBelowTopParent();
			returnMe.nodesVisited = nodesVisited;
			return returnMe;
		} else if (method.equals("alpha-beta")) {
			parent = null;
			ChessState bestMoveEndState = minStateABq(this, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, MAX_DEPTH);
			ChessState returnMe = bestMoveEndState.getOneBelowTopParent();
			returnMe.nodesVisited = nodesVisited;
			return returnMe;
		} else if (method.equals("null move")) {
			ChessState bestTwoWhiteMoveState = bestDoubleMove("white");
			//System.out.println("value of two black moves in a row: " + (bestTwoBlackMoveState.getWhiteScore() - getWhiteScore()));
			
			parent = null;
			ChessState bestMoveEndState = minStateAB(this, bestTwoWhiteMoveState.getWhiteScore(), Integer.MAX_VALUE, 0, MAX_DEPTH);
			ChessState returnMe = bestMoveEndState.getOneBelowTopParent();
			returnMe.nodesVisited = nodesVisited;
			return returnMe;
		} else {
			System.out.println("Bad search method");
			System.exit(0);
			return null;
		}
	}
	
	/*
	 * Used by bestWhiteMove() to help with the null move heuristic
	 */
	public ChessState bestDoubleMove(String player) {
		if (!player.equals("white") && !player.equals("black")) {
			System.out.println("bad player");
			System.exit(0);
		}
		Vector<ChessState> successors = getSuccessors(player);
		// If we have no children, this isn't real chess anymore
		if (successors.size() == 0) {
			System.out.println("Black has no possible moves.");
			System.exit(0);
		}
		Iterator<ChessState> iter = successors.iterator();
		
		ChessState tState = null;
		int bestSoFar = 0;
		if (player.equals("black")) {
			bestSoFar = Integer.MAX_VALUE;
		} else {
			bestSoFar = Integer.MIN_VALUE;
		}
		ChessState bestState = null;
		while (iter.hasNext()) {
			tState = iter.next();
			Vector<ChessState> successors2 = tState.getSuccessors(player);
			// If there are no moves after this, forget it
			if (successors.size() == 0) {
				System.out.println(player + " has no possible moves.");
				System.exit(0);
			}
			Iterator<ChessState> iter2 = successors2.iterator();
			ChessState tState2 = null;
			while (iter2.hasNext()) {
				tState2 = iter2.next();
				if (player.equals("black")){
					if (tState2.getWhiteScore() < bestSoFar) {
						bestSoFar = tState2.getWhiteScore();
						bestState = tState2;
					}
				} else {
					if (tState2.getWhiteScore() > bestSoFar) {
						bestSoFar = tState2.getWhiteScore();
						bestState = tState2;
					}
				}
			}
		}
		return bestState;
	}
	
	/*
	 * Used by bestWhiteMove() to help with the null move heuristic
	 */
	public ChessState bestTwoBlackMoves() {
		Vector<ChessState> successors = getSuccessors("black");
		// If we have no children, this isn't real chess anymore
		if (successors.size() == 0) {
			System.out.println("Black has no possible moves.");
			System.exit(0);
		}
		Iterator<ChessState> iter = successors.iterator();
		
		ChessState tState = null;
		int worstSoFar = Integer.MAX_VALUE;
		ChessState worstState = null;
		while (iter.hasNext()) {
			tState = iter.next();
			Vector<ChessState> successors2 = tState.getSuccessors("black");
			// If black has no moves after this, forget it
			if (successors.size() == 0) {
				System.out.println("Black has no possible moves.");
				System.exit(0);
			}
			Iterator<ChessState> iter2 = successors2.iterator();
			ChessState tState2 = null;
			while (iter2.hasNext()) {
				tState2 = iter2.next();
				if (tState2.getWhiteScore() < worstSoFar) {
					worstSoFar = tState2.getWhiteScore();
					worstState = tState2;
				}
			}
		}
		return worstState;
	}
	
	private ChessState getOneBelowTopParent() {
		ChessState tState = this;
		if (tState == null || tState.parent == null) {
			System.out.println("Couldn't make a move!");
			System.exit(0);
		}
		while (tState.parent.parent != null) {
			tState = tState.parent;
		}
		return tState;
	}
	
	private ChessState maxStateAB(ChessState state, int alpha, int beta, int depth, final int MAX_DEPTH) {
		nodesVisited++;
		
		// We are at a "leaf node," and are not allowed to look at its children
		if (depth == MAX_DEPTH) {
			return state;
		}
		
		int max = Integer.MIN_VALUE;
		ChessState maxState = null;
		
		Vector<ChessState> successors = state.getSuccessors("white");
		// If we have no children, we just have to return.
		if (successors.size() == 0) {
			return state;
		}
		Iterator<ChessState> iter = successors.iterator();
		
		ChessState tState = null;
		while (iter.hasNext()) {
			tState = iter.next();
			// Ask min what he would do in response to this move
			ChessState localMinState = minStateAB(tState, alpha, beta, depth +1, MAX_DEPTH);
			// If the end result of tState's move was the best so far, remember it.
			if (localMinState.getWhiteScore() > max) {
				max = localMinState.getWhiteScore();
				maxState = localMinState;
			}
			
			// If this is more than beta, it means that MIN has already found a node it will
			// prefer over this path, so just quit this.
			if (max >= beta) {
				return maxState;
			}
			// alpha is the best (biggest) guaranteed value we've found so far.
			// We'll give this to MIN so it knows when to quit looking.
			alpha = Math.max(alpha, max);
		}
		return maxState;
	}
	
	private ChessState maxStateABq(ChessState state, int alpha, int beta, int depth, final int MAX_DEPTH) {
		nodesVisited++;
		
		int max = Integer.MIN_VALUE;
		// If we are at MAX_DEPTH and none of its successors are captures, then we
		// will return state, but we must check first.
		ChessState maxState = state;
		
		Vector<ChessState> successors = state.getSuccessors("white"); 
		
		
		// If we have no children, we just have to return.
		if (successors.size() == 0) {
			return state;
		}
		Iterator<ChessState> iter = successors.iterator();
		
		ChessState tState = null;
		while (iter.hasNext()) {
			tState = iter.next();
			// Keep going down the tree IF... the depth is small enough, or if we are taking a piece here 
			if (depth < MAX_DEPTH || Math.abs(tState.getWhiteScore() - state.getWhiteScore()) > 15) {
				// Ask min what he would do in response to this move
				ChessState localMinState = minStateABq(tState, alpha, beta, depth +1, MAX_DEPTH);
				// If the end result of tState's move was the best so far, remember it.
				if (localMinState.getWhiteScore() > max) {
					max = localMinState.getWhiteScore();
					maxState = localMinState;
				}

				// If this is more than beta, it means that MIN has already found a node it will
				// prefer over this path, so just quit this.
				if (max >= beta) {
					return maxState;
				}
				// alpha is the best (biggest) guaranteed value we've found so far.
				// We'll give this to MIN so it knows when to quit looking.
				alpha = Math.max(alpha, max);
			}
		}
		return maxState;
	}
	
	private ChessState minStateABq(ChessState state, int alpha, int beta, int depth, final int MAX_DEPTH) {
		nodesVisited++;
		
		int min = Integer.MAX_VALUE;
		ChessState minState = state;
		
		Vector<ChessState> successors = state.getSuccessors("black");
		// If we have no children, we just have to return.
		if (successors.size() == 0) {
			return state;
		}
		Iterator<ChessState> iter = successors.iterator();
		
		ChessState tState = null;
		while (iter.hasNext()) {
			tState = iter.next();
			// Keep going down the tree IF... the depth is small enough, or if we are taking a piece here 
			if (depth < MAX_DEPTH || Math.abs(tState.getWhiteScore() - state.getWhiteScore()) > 15) {
				ChessState localMaxState = maxStateABq(tState, alpha, beta, depth + 1, MAX_DEPTH);
				if (localMaxState.getWhiteScore() < min) {
					min = localMaxState.getWhiteScore();
					minState = localMaxState;
				}
				// If this is less than alpha, it means that MAX has already found a node it will
				// prefer over this path, so just quit this.
				if (min <= alpha) {
					return minState;
				}
				// beta is the best (smallest) guaranteed value we've found so far.
				// We'll give this to MAX so it knows when to quit looking.
				beta = Math.min(beta, min);
			}
		}
		return minState;
	}
	
	private ChessState minStateAB(ChessState state, int alpha, int beta, int depth, final int MAX_DEPTH) {
		nodesVisited++;
		
		int min = Integer.MAX_VALUE;
		
		// We are at a "leaf node," and are not allowed to look at its children
		if (depth == MAX_DEPTH) {
			return state;
		}
		ChessState minState = null;
		
		Vector<ChessState> successors = state.getSuccessors("black");
		// If we have no children, we just have to return.
		if (successors.size() == 0) {
			return state;
		}
		Iterator<ChessState> iter = successors.iterator();
		
		ChessState tState = null;
		while (iter.hasNext()) {
			tState = iter.next();
			ChessState localMaxState = maxStateAB(tState, alpha, beta, depth + 1, MAX_DEPTH);
			if (localMaxState.getWhiteScore() < min) {
				min = localMaxState.getWhiteScore();
				minState = localMaxState;
			}
			// If this is less than alpha, it means that MAX has already found a node it will
			// prefer over this path, so just quit this.
			if (min <= alpha) {
				return minState;
			}
			// beta is the best (smallest) guaranteed value we've found so far.
			// We'll give this to MAX so it knows when to quit looking.
			beta = Math.min(beta, min);

		}
		return minState;
	}
	
	private ChessState maxState(ChessState state, int depth, final int MAX_DEPTH) {
		nodesVisited++;
		
		// We are at a "leaf node," and are not allowed to look at its children
		if (depth == MAX_DEPTH) {
			return state;
		}
		
		int max = Integer.MIN_VALUE;
		ChessState maxState = null;
		
		Vector<ChessState> successors = state.getSuccessors("white");
		// If we have no children, we just have to return.
		if (successors.size() == 0) {
			return state;
		}
		Iterator<ChessState> iter = successors.iterator();
		
		ChessState tState = null;
		while (iter.hasNext()) {
			tState = iter.next();
			ChessState localMinState = minState(tState, depth +1, MAX_DEPTH);
			if (localMinState.getWhiteScore() > max) {
				max = localMinState.getWhiteScore();
				maxState = localMinState;
			}
		}
		return maxState;
	}
	
	private ChessState minState(ChessState state, int depth, final int MAX_DEPTH) {
		nodesVisited++;
		
		// We are at a "leaf node," and are not allowed to look at its children
		if (depth == MAX_DEPTH) {
			return state;
		}
		
		int min = Integer.MAX_VALUE;
		ChessState minState = null;
		
		Vector<ChessState> successors = state.getSuccessors("black");
		// If we have no children, we just have to return.
		if (successors.size() == 0) {
			return state;
		}
		Iterator<ChessState> iter = successors.iterator();
		
		ChessState tState = null;
		while (iter.hasNext()) {
			tState = iter.next();
			ChessState localMaxState = maxState(tState, depth + 1, MAX_DEPTH);
			if (localMaxState.getWhiteScore() < min) {
				min = localMaxState.getWhiteScore();
				minState = localMaxState;
			}
		}
		return minState;
	}
	
	private ChessState getBestPossibleWhiteBoard() {
		int[][] b = new int[8][8];
		
		b[0][0] = 0; b[0][1] = 0; b[0][2] = 0; b[0][3] = 0; b[0][4] = 0; b[0][5] = 0; b[0][6] = 0; b[0][7] = 0;
		b[1][0] = 0; b[1][1] = 0; b[1][2] = 0; b[1][3] = 0; b[1][4] = 0; b[1][5] = 0; b[1][6] = 0; b[1][7] = 0;
		for (int i = 2; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				b[i][j] = 0;
			}
		}
		b[6][0] = 1; b[6][1] = 1; b[6][2] = 1; b[6][3] = 1; b[6][4] = 1; b[6][5] = 1; b[6][6] = 1; b[6][7] = 1;
		b[7][0] = 4; b[7][1] = 3; b[7][2] = 2; b[7][3] = 5; b[7][4] = 6; b[7][5] = 2; b[7][6] = 3; b[7][7] = 4;
		
		return new ChessState(b);
	}
	
	private ChessState getBestPossibleBlackBoard() {
		int[][] b = new int[8][8];
		
		b[0][0] = 0; b[0][1] = 0; b[0][2] = 0; b[0][3] = 0; b[0][4] = 0; b[0][5] = 0; b[0][6] = 0; b[0][7] = 0;
		b[1][0] = 0; b[1][1] = 0; b[1][2] = 0; b[1][3] = 0; b[1][4] = 0; b[1][5] = 0; b[1][6] = 0; b[1][7] = 0;
		for (int i = 2; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				b[i][j] = 0;
			}
		}
		b[6][0] = 1; b[6][1] = 1; b[6][2] = 1; b[6][3] = 1; b[6][4] = 1; b[6][5] = 1; b[6][6] = 1; b[6][7] = 1;
		b[7][0] = 4; b[7][1] = 3; b[7][2] = 2; b[7][3] = 5; b[7][4] = 6; b[7][5] = 2; b[7][6] = 3; b[7][7] = 4;
		
		return new ChessState(b);
	}
	
	/*public int getBlackScore() {
		int score = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				switch (grid[i][j]) {
				case 0:            ;	break;
				case 1:  score -= 1;	break;
				case 2:  score -= 3;	break;
				case 3:  score -= 3;	break;
				case 4:  score -= 5;	break;
				case 5:  score -= 9;	break;
				case 6:  score -= 5000; break;	// Having our king is more important than having his, so maybe add to this value
				case 7:  score += 1;	break;
				case 8:  score += 3;	break;
				case 9:  score += 3;	break;
				case 10: score += 5;	break;
				case 11: score += 9;	break;
				case 12: score += 10000;	break;
				}
			}
		}
		return score;
	}*/
	
	public int getWhiteScore() {
		int score = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == 1) {
					score += 10 + (6-j);
				} else if (grid[i][j] == 2) {
					score += 30;
				} else if (grid[i][j] == 3) {
					score += 30;
				} else if (grid[i][j] == 4) {
					score += 50;
				} else if (grid[i][j] == 5) {
					score += 90;
				} else if (grid[i][j] == 6) {
					score += 1000;
				} else if (grid[i][j] == 7) {
					score -= 10 + (j - 1);
				} else if (grid[i][j] == 8) {
					score -= 30;
				} else if (grid[i][j] == 9) {
					score -= 30;
				} else if (grid[i][j] == 10) {
					score -= 50;
				} else if (grid[i][j] == 11) {
					score -= 90;
				} else if (grid[i][j] == 12) {
					score -= 1000;
				}
			}
		}
		return score;
	}
	
	public ChessState(int[][] g) {
		parent = null;
		
		grid = new int[8][8];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = g[i][j];
			}
		}
	}
	
	public String toString() {
		String returnMe = "";
		for (int i = 0; i < grid.length; i ++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[j][i] == 0) {
					returnMe += "_";
				} else if (grid[j][i] == 1) {
					returnMe += "P";
				} else if (grid[j][i] == 2) {
					returnMe += "B";
				} else if (grid[j][i] == 3) {
					returnMe += "N";
				} else if (grid[j][i] == 4) {
					returnMe += "R";
				} else if (grid[j][i] == 5) {
					returnMe += "Q";
				} else if (grid[j][i] == 6) {
					returnMe += "K";
				} else if (grid[j][i] == 7) {
					returnMe += "p";
				} else if (grid[j][i] == 8) {
					returnMe += "b";
				} else if (grid[j][i] == 9) {
					returnMe += "n";
				} else if (grid[j][i] == 10) {
					returnMe += "r";
				} else if (grid[j][i] == 11) {
					returnMe += "q";
				} else if (grid[j][i] == 12) {
					returnMe += "k";
				}
				returnMe += " ";
			}
			returnMe += "\n";
		}
		return returnMe;
	}
	
	private ChessState getBlankChild() {
		ChessState returnMe = new ChessState(grid);
		returnMe.parent = this;
		return returnMe;
	}
	
	private boolean isMyPiece(int piece) {
		if (tPlayer == "white") {
			if (piece > 0 && piece < 7) {
				return true;
			}
		} else {
			if (piece > 6 && piece <= 12) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isHisPiece(int piece) {
		if (tPlayer == "white") {
			if (piece > 6 && piece <= 12) {
				return true;
			}
		} else {
			if (piece > 0 && piece < 7) {
				return true;
			}
		}
		return false;
	}
	
	public Vector<ChessState> getSuccessors(String player) {
		tPlayer = player;
		int MY_PAWN; int MY_BISHOP; int MY_KNIGHT; int MY_ROOK; int MY_QUEEN; int MY_KING;
		int HIS_PAWN; int HIS_BISHOP; int HIS_KNIGHT; int HIS_ROOK; int HIS_QUEEN; int HIS_KING;
		int SPECIAL_PAWN_ROW; int PAWN_DIRECTION; int PAWN_PROMOTION_ROW;
		
		if (player == "white") {
			MY_PAWN		= 1;
			MY_BISHOP	= 2;
			MY_KNIGHT	= 3;
			MY_ROOK		= 4;
			MY_QUEEN	= 5;
			MY_KING		= 6;
			
			HIS_PAWN	= 7;
			HIS_BISHOP	= 8;
			HIS_KNIGHT	= 9;
			HIS_ROOK	= 10;
			HIS_QUEEN	= 11;
			HIS_KING	= 12;
			
			PAWN_PROMOTION_ROW = 0;
			SPECIAL_PAWN_ROW = 6;
			PAWN_DIRECTION = -1;
		} else {
			if (player != "black") {
				System.out.println("Wrong player name");
				System.exit(0);
			}
			MY_PAWN		= 7;
			MY_BISHOP	= 8;
			MY_KNIGHT	= 9;
			MY_ROOK		= 10;
			MY_QUEEN	= 11;
			MY_KING		= 12;
			
			HIS_PAWN	= 1;
			HIS_BISHOP	= 2;
			HIS_KNIGHT	= 3;
			HIS_ROOK	= 4;
			HIS_QUEEN	= 5;
			HIS_KING	= 6;
			
			PAWN_PROMOTION_ROW = 7;
			SPECIAL_PAWN_ROW = 1;
			PAWN_DIRECTION = 1;
		}
		Vector<ChessState> successors = new Vector<ChessState>();
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				// PAWNS
				if (grid[i][j] == MY_PAWN) {
					// First move may be two ahead
					if (j == SPECIAL_PAWN_ROW) {
						if (grid[i][j+PAWN_DIRECTION] == 0 && grid[i][j+2*PAWN_DIRECTION] == 0) {
							ChessState child = getBlankChild();
							child.grid[i][j+2*PAWN_DIRECTION] = MY_PAWN;		// Place the piece
							child.grid[i][j] = 0;		// Erase previous location
							if (!child.isLikeAncestor()) {
								successors.add(child);
							}
						}
					}
					// Pawn is moving one space up
					if (grid[i][j+PAWN_DIRECTION] == 0) {
						ChessState child = getBlankChild();
						if (j+PAWN_DIRECTION == PAWN_PROMOTION_ROW) {
							child.grid[i][j+PAWN_DIRECTION] = MY_QUEEN;		// Queen me!
						} else {
							child.grid[i][j+PAWN_DIRECTION] = MY_PAWN;		// Place the piece
						}
						child.grid[i][j] = 0;		// Erase previous location
						if (!child.isLikeAncestor()) {
							successors.add(child);
						}
					}
					// Pawn is able to take a piece to its left
					if (i > 0) {
						if (isHisPiece(grid[i-1][j+PAWN_DIRECTION])) {
							ChessState child = getBlankChild();
							if (j+PAWN_DIRECTION == PAWN_PROMOTION_ROW) {
								child.grid[i-1][j+PAWN_DIRECTION] = MY_QUEEN;	// Queen me!
							} else {
								child.grid[i-1][j+PAWN_DIRECTION] = MY_PAWN;	// Place the piece
							}
							child.grid[i][j] = 0;		// Erase previous location
							if (!child.isLikeAncestor()) {
								successors.add(child);
							}
						}
					}
					// Pawn is able to take a piece to its right
					if (i < 7) {
						if (isHisPiece(grid[i+1][j+PAWN_DIRECTION])) {
							ChessState child = getBlankChild();
							if (j+PAWN_DIRECTION == PAWN_PROMOTION_ROW) {
								child.grid[i+1][j+PAWN_DIRECTION] = MY_QUEEN;	// Queen me!
							} else {
								child.grid[i+1][j+PAWN_DIRECTION] = MY_PAWN;	// Place the piece
							}
							child.grid[i][j] = 0;			// Erase previous location
							if (!child.isLikeAncestor()) {
								successors.add(child);
							}
						}
					}
				}
				
				// BISHOPS
				if (grid[i][j] == MY_BISHOP) {
					int iOffset = 0;
					int jOffset = 0;
					
					for (int x = 0; x < 4; x++) {
						switch (x) {
						case 0:	iOffset = 1; jOffset = 1; break;
						case 1:	iOffset = 1; jOffset = -1; break;
						case 2:	iOffset = -1; jOffset = 1; break;
						case 3:	iOffset = -1; jOffset = -1; break;
						}
						
						for (int y = 1; y < 8; y++) {
							int ti = i+iOffset*y;
							int tj = j+jOffset*y;
							// If we're off the map, or we've hit our own piece, quit this direction
							if (ti > 7 || ti < 0 || tj > 7 || tj < 0 || isMyPiece(grid[ti][tj])) {
								break;
							}
							// If we hit a black piece, replace it, but don't keep going
							if (isHisPiece(grid[ti][tj])) {
								ChessState child = getBlankChild();
								child.grid[ti][tj] = MY_BISHOP;		// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
								break;
							}
							// Another empty space
							if (grid[ti][tj] == 0) {
								ChessState child = getBlankChild();
								child.grid[ti][tj] = MY_BISHOP;		// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
							}
						}
					}
				}

				// KNIGHTS
				if (grid[i][j] == MY_KNIGHT) {
					int iOffset = 0;
					int jOffset = 0;
					
					for (int x = 0; x < 8; x++) {
						switch (x) {
						case 0:	iOffset = -2; jOffset = 1; break;
						case 1:	iOffset = -2; jOffset = -1; break;
						case 2:	iOffset = -1; jOffset = 2; break;
						case 3:	iOffset = -1; jOffset = -2; break;
						case 4:	iOffset = 1; jOffset = 2; break;
						case 5:	iOffset = 1; jOffset = -2; break;
						case 6:	iOffset = 2; jOffset = 1; break;
						case 7:	iOffset = 2; jOffset = -1; break;
						}
						
						if (i+iOffset >= 0 && i+iOffset <= 7 && j+jOffset <= 7 && j+jOffset >= 0) {
							if (!isMyPiece(grid[i+iOffset][j+jOffset])) {
								ChessState child = getBlankChild();
								child.grid[i+iOffset][j+jOffset] = MY_KNIGHT;	// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
							}
						}
					}
				}
				// ROOKS
				if (grid[i][j] == MY_ROOK) {
					int iOffset = 0;
					int jOffset = 0;
					
					for (int x = 0; x < 4; x++) {
						switch (x) {
						case 0:	iOffset = 1; jOffset = 0; break;
						case 1:	iOffset = -1; jOffset = 0; break;
						case 2:	iOffset = 0; jOffset = 1; break;
						case 3:	iOffset = 0; jOffset = -1; break;
						}
						
						for (int y = 1; y < 8; y++) {
							int ti = i+iOffset*y;
							int tj = j+jOffset*y;
							// If we're off the map, or we've hit our own piece, quit this direction
							if (ti > 7 || ti < 0 || tj > 7 || tj < 0 || isMyPiece(grid[ti][tj])) {
								break;
							}
							// If we hit a black piece, replace it, but don't keep going
							if (isHisPiece(grid[ti][tj])) {
								ChessState child = getBlankChild();
								child.grid[ti][tj] = MY_ROOK;		// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
								break;
							}
							// Another empty space
							if (grid[ti][tj] == 0) {
								ChessState child = getBlankChild();
								child.grid[ti][tj] = MY_ROOK;		// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
							}
						}
					}
				}
				// QUEEN
				if (grid[i][j] == MY_QUEEN) {
					int iOffset = 0;
					int jOffset = 0;
					
					for (int x = 0; x < 8; x++) {
						switch (x) {
						case 0:	iOffset = 1; jOffset = 0; break;
						case 1:	iOffset = -1; jOffset = 0; break;
						case 2:	iOffset = 0; jOffset = 1; break;
						case 3:	iOffset = 0; jOffset = -1; break;
						case 4:	iOffset = 1; jOffset = 1; break;
						case 5:	iOffset = 1; jOffset = -1; break;
						case 6:	iOffset = -1; jOffset = 1; break;
						case 7:	iOffset = -1; jOffset = -1; break;
						}
						
						for (int y = 1; y < 8; y++) {
							int ti = i+iOffset*y;
							int tj = j+jOffset*y;
							// If we're off the map, or we've hit our own piece, quit this direction
							if (ti > 7 || ti < 0 || tj > 7 || tj < 0 || isMyPiece(grid[ti][tj])) {
								break;
							}
							// If we hit a black piece, replace it, but don't keep going
							if (isHisPiece(grid[ti][tj])) {
								ChessState child = getBlankChild();
								child.grid[ti][tj] = MY_QUEEN;		// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
								break;
							}
							// Another empty space
							if (grid[ti][tj] == 0) {
								ChessState child = getBlankChild();
								child.grid[ti][tj] = MY_QUEEN;		// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
							}
						}
					}
				}
				// KING
				if (grid[i][j] == MY_KING) {
					int iOffset = 0;
					int jOffset = 0;
					
					for (int x = 0; x < 8; x++) {
						switch (x) {
						case 0:	iOffset = 1; jOffset = 0; break;
						case 1:	iOffset = -1; jOffset = 0; break;
						case 2:	iOffset = 0; jOffset = 1; break;
						case 3:	iOffset = 0; jOffset = -1; break;
						case 4:	iOffset = 1; jOffset = 1; break;
						case 5:	iOffset = 1; jOffset = -1; break;
						case 6:	iOffset = -1; jOffset = 1; break;
						case 7:	iOffset = -1; jOffset = -1; break;
						}
						
						for (int y = 1; y < 2; y++) {
							int ti = i+iOffset*y;
							int tj = j+jOffset*y;
							// If we're off the map, or we've hit our own piece, quit this direction
							if (ti > 7 || ti < 0 || tj > 7 || tj < 0 || isMyPiece(grid[ti][tj])) {
								break;
							}
							// If we hit a black piece, replace it, but don't keep going
							if (isHisPiece(grid[ti][tj])) {
								ChessState child = getBlankChild();
								child.grid[ti][tj] = MY_KING;		// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
								break;
							}
							// Another empty space
							if (grid[ti][tj] == 0) {
								ChessState child = getBlankChild();
								child.grid[ti][tj] = MY_KING;		// Place the piece
								child.grid[i][j] = 0;		// Erase previous location
								if (!child.isLikeAncestor()) {
									successors.add(child);
								}
							}
						}
					}
				}
			}
		}
		
		return successors;
	}
	
	/*
	 * This is used to avoid adding duplicate states if we
	 * can help it.  This method works for both kinds of states
	 * because each has its own particular .equals method.
	 */
	private boolean isLikeAncestor() {
		ChessState tState = parent;
		while (tState != null) {
			if (tState.equals(this)) {
				return true;
			}
			tState = tState.parent;
		}
		return false;
	}
	
	public boolean equals(Object o) {
		ChessState c = (ChessState)o;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] != c.grid[i][j]) {
					return false;
				}
			}
		}
		
		return true;
	}
}
