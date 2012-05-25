import java.io.*;
import java.util.*;

public class ChessPuzzle {
	public static void main(String[] args) {
		int[][] b = new int[8][8];
		
		/*
		 * WHITE
		 * Pawn   = 1
		 * Bishop = 2
		 * Knight = 3
		 * Rook   = 4
		 * Queen  = 5
		 * King   = 6
		 * 
		 * BLACK
		 * Pawn   = 7
		 * Bishop = 8
		 * Knight = 9
		 * Rook   = 10
		 * Queen  = 11
		 * King   = 12
		 */
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				b[i][j] = 0;
			}
		}
		
		// White pieces
		b[0][6] = 1;
		b[1][6] = 1;
		b[2][6] = 1;
		b[3][6] = 1;
		b[4][6] = 1;
		b[5][6] = 1;
		b[6][6] = 1;
		b[7][6] = 1;
		b[0][7] = 4;
		b[1][7] = 3;
		b[2][7] = 2;
		b[3][7] = 5;
		b[4][7] = 6;
		b[5][7] = 2;
		b[6][7] = 3;
		b[7][7] = 4;
		
		// Black pieces
		b[0][0] = 10;
		b[1][0] = 9;
		b[2][0] = 8;
		b[3][0] = 11;
		b[4][0] = 12;
		b[5][0] = 8;
		b[6][0] = 9;
		b[7][0] = 10;
		b[0][1] = 7;
		b[1][1] = 7;
		b[2][1] = 7;
		b[3][1] = 7;
		b[4][1] = 7;
		b[5][1] = 7;
		b[6][1] = 7;
		b[7][1] = 7;
		
		ChessState game = new ChessState(b);
		ChessState potentialPlayerMove = null;
		
		while (true) {
			potentialPlayerMove = null;
			
			while (potentialPlayerMove == null) {
				System.out.println(game);
				System.out.println("The move: (x,y) -> (i,j) is entered x, y, i, j one number at a time.");
				System.out.println("So... x <enter> y <enter> i <enter> j <enter>");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String input = null;
				int x = 0;
				int y = 0;
				int i = 0;
				int j = 0; 
				try {
					input = br.readLine();
					x = Integer.parseInt(input);
					input = br.readLine();
					y = Integer.parseInt(input);
					input = br.readLine();
					i = Integer.parseInt(input);
					input = br.readLine();
					j = Integer.parseInt(input);
				} catch (IOException e) {
					System.out.println("IO error trying to read your move");
					System.exit(0);
				}
				if (x < 0 || x > 7 || y < 0 || y > 7 || i < 0 || i > 7 || j < 0 || j > 7) {
					System.out.println("Please use numbers 0-7 inclusive");
					continue;
				}
				potentialPlayerMove = new ChessState(game.grid);
				potentialPlayerMove.grid[i][j] = game.grid[x][y];
				potentialPlayerMove.grid[x][y] = 0;
				
				// Is this move legal?
				Vector<ChessState> legalMoves = game.getSuccessors("white");
				if (!legalMoves.contains(potentialPlayerMove)) {
					System.out.println("That's not a legal move.");
					potentialPlayerMove = null;
				}
			}
			game = potentialPlayerMove;
			System.out.println(game);
			
			
			
			ChessState nullGame = game.bestBlackMove(4, "null move");
			ChessState alphaBetaGame = game.bestBlackMove(4, "alpha-beta");
			System.out.println("alpha-beta looked at " + alphaBetaGame.nodesVisited + " nodes");
			System.out.println("null move looked at  " + nullGame.nodesVisited + " nodes");
			if (alphaBetaGame.equals(nullGame)) {
				System.out.println("alpha-beta == null game");
			} else {
				System.out.println("ALPHABETA != NULL GAME");
				System.out.println("alpha-beta");
				System.out.println(alphaBetaGame);
				System.out.println("null move");
				System.out.println(nullGame);
			}
			game = alphaBetaGame;
		}
	}
}
