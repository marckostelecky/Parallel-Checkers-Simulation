import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Offline implements Serializable{
	boolean firstMove = true;
	ArrayList<Integer> moves = new ArrayList<Integer>();
	String turn = "";
	int whitePieces;
	int redPieces;
	JButton[][] boardSquares = new JButton[8][8];
	String message = "";
	
	public void setBoard(JButton[][] board){
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[i].length; j++){
				boardSquares[j][i] = board[j][i];
			}
		}
	}
	

	
	public JButton[][] getBoard(){
		return boardSquares;
	}
	
	public String getMessage(){
		return message;
	}
	
	
	public void newGame() {
		moves.clear();
		firstMove = true;
		whitePieces = 12;
		redPieces = 12;
		turn = "White";
		message = "White Moves First";
		for (int ii = 0; ii < boardSquares.length; ii++) {
			for (int jj = 0; jj < boardSquares[ii].length; jj++) {
				ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
				boardSquares[jj][ii].setIcon(icon);
				boardSquares[jj][ii].setName(null);
				if ((jj % 2 == 1 && ii % 2 == 1) || (jj % 2 == 0 && ii % 2 == 0)) {
				} else {
					if ((ii < 3)) {
						Icon piece = new ImageIcon("redPiece.png");
						boardSquares[jj][ii].setIcon(piece);
						boardSquares[jj][ii].setName("Red");
					}
					if ((ii >= 5)) {
						Icon piece = new ImageIcon("whitePiece.png");
						boardSquares[jj][ii].setIcon(piece);
						boardSquares[jj][ii].setName("White");
					}
				}
			}
		}
	}
	
	public void play(JButton btn, int j, int i){
		// if a king of either color is selected
		if ((Objects.equals(btn.getName(), "WhiteKing") || Objects.equals(btn.getName(), "RedKing"))) {
			moveKing(btn, j, i);

			// if a king was selected to move
		} else if (firstMove == false && (Objects
				.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "WhiteKing")
				|| Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "RedKing"))) {
			moveKing(btn, j, i);

			// if a normal piece is selected
		} else {
			move(btn, j, i);
		}
	}
	
	// move handler for kings
	// allows for backwards moves and jumps
	public void moveKing(JButton btn, int j, int i) {
		// checks if first piece selected
		if (firstMove) { // TODO check if players piece
			firstMove = false;
			btn.setBorderPainted(true);
			moves.add(j);
			moves.add(i);

			// if move location is selected
		} else if (!firstMove) {
			if (validKingMove(j, i, btn)) {
				repaintMove(j, i, btn);
			} else if (checkKingJump(j, i, btn)) {
				repaintJump(j, i, btn);
			}

			// if move is not valid, reset instance variables
			else {
				message = "That Is Not A Legal Move";
				boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
				moves.clear();
				firstMove = true;
			}

		} else {
			message = "You May Only Move Your Own Piece!";
		}

		// check for winner
		if (checkWin()) {
			clearBoard();
		}
	}
	
	// Method for handling checking if king jumps are valid
		// (j,i) coordinates of location to jump to
		// JButton btn, button of location to jump
		public boolean checkKingJump(int j, int i, JButton btn) {
			boolean valid = false;
			if ((i + 2 == moves.get(1) || i - 2 == moves.get(1)) && (j + 2 == moves.get(0) || j - 2 == moves.get(0))
					&& (btn.getName() == null)) {
				valid = true;
			}
			return valid;
		}
		
		// Method for checking if king moves are valid
		// allows forwards and backwards movement
		// (j,i) - coordinates of location to move
		// JButton btn - button of location to move
		public boolean validKingMove(int j, int i, JButton btn) {
			boolean valid = false;
			// check if kings move is valid
			if ((i + 1 == moves.get(1) || i - 1 == moves.get(1)) && (j + 1 == moves.get(0) || j - 1 == moves.get(0))
					&& (btn.getName() == null)) {
				valid = true;
			}

			return valid;
		}
		
		// primary method for controlling moves of non-kings
		// calls methods to check if move is valid and
		// calls method to update the board after the move
		// manages array to store previously selected piece to move
		// JButton btn - button slected
		// (j,i) - coordinates of button selected
		public void move(JButton btn, int j, int i) {
			// checks if first piece selected
			if (firstMove && Objects.equals(turn, btn.getName())) {
				firstMove = false;
				btn.setBorderPainted(true);
				moves.add(j);
				moves.add(i);

				// if move location is selected
			} else if (!firstMove) {
				if (validMove(j, i, btn)) {
					repaintMove(j, i, btn);
				} else if (checkJump(j, i, btn)) {
					repaintJump(j, i, btn);
				}

				// if move is not valid, reset instance variables
				else {
					message = "That Is Not A Legal Move";
					boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
					moves.clear();
					firstMove = true;
				}
				// if piece is moved to either end of the board
				// promotes piece to king
				if (i == 0 || i == 7) {
					promoteKing(btn, i);
				}
			} else {
				message = "You May Only Move Your Own Piece!";
			}

		}
		
		// called when a non-king jump occurs
		// updates game board and removes piece jumped
		// piece to be removed is calculated by the average between starting
		// and ending locations
		// (j,i) - coordinates of location to be jumped to
		// JButton btn - button of jumping location
		public void repaintJump(int j, int i, JButton btn) {
			Icon piece;
			boolean available = false;

			// White's turn
			if (Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "White")) {

				// updates piece location after jump
				piece = new ImageIcon("whitePiece.png");
				boardSquares[j][i].setIcon(piece);
				boardSquares[j][i].setName("White");

				// Removes old piece location
				ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
				boardSquares[moves.get(0)][moves.get(1)].setIcon(icon);
				boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
				boardSquares[moves.get(0)][moves.get(1)].setName(null);

				// removes opponents piece that was jumped
				boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].setIcon(icon);
				boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].setName(null);
				moves.clear();
				redPieces -= 1;
				firstMove = true;
				// checks if another jump is available
				// if not changes turn
				try {
					available = checkAvailableMove(j, i, btn);
				} catch (IndexOutOfBoundsException e) {
					available = false;
				}
				if (!available) {

					// resets instance variables for opponents turn

					turn = "Red";
					message = "Red's Turn";
				}

				// Red's turn
			} else if (Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "Red")) {

				// updates piece location after jump
				piece = new ImageIcon("redPiece.png");
				boardSquares[j][i].setIcon(piece);
				boardSquares[j][i].setName("Red");

				// Removes old piece location
				ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
				boardSquares[moves.get(0)][moves.get(1)].setIcon(icon);
				boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
				boardSquares[moves.get(0)][moves.get(1)].setName(null);

				// removes opponents piece that was jumped
				boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].setIcon(icon);
				boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].setName(null);
				moves.clear();
				whitePieces -= 1;
				firstMove = true;

				// checks if another jump is available
				// if not changes turn
				try {
					available = checkAvailableMove(j, i, btn);
				} catch (IndexOutOfBoundsException e) {
					available = false;
				}
				if (!available) {

					// resets instance variables for opponents turn

					turn = "White";
					message = "White's Turn";
				}
				// red king turn
			} else if (Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "RedKing")) {

				// updates piece location after jump
				piece = new ImageIcon("redKing.png");
				boardSquares[j][i].setIcon(piece);
				boardSquares[j][i].setName("RedKing");

				// Removes old piece location
				ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
				boardSquares[moves.get(0)][moves.get(1)].setIcon(icon);
				boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
				boardSquares[moves.get(0)][moves.get(1)].setName(null);

				// removes opponents piece that was jumped
				boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].setIcon(icon);
				boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].setName(null);
				moves.clear();
				whitePieces -= 1;
				firstMove = true;

				// checks if another jump is available
				// if not changes turn
				try {
					available = checkAvailableMove(j, i, btn);
				} catch (IndexOutOfBoundsException e) {
					available = false;
				}
				if (!available) {

					// resets instance variables for opponents turn

					turn = "White";
					message = "White's Turn";
				}
				// white king turn
			} else if (Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "WhiteKing")) {

				// updates piece location after jump
				piece = new ImageIcon("whiteKing.png");
				boardSquares[j][i].setIcon(piece);
				boardSquares[j][i].setName("WhiteKing");

				// Removes old piece location
				ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
				boardSquares[moves.get(0)][moves.get(1)].setIcon(icon);
				boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
				boardSquares[moves.get(0)][moves.get(1)].setName(null);

				// removes opponents piece that was jumped
				boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].setIcon(icon);
				boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].setName(null);
				moves.clear();
				redPieces -= 1;
				firstMove = true;

				// checks if available jumps
				// if not changes turn
				try {
					available = checkAvailableMove(j, i, btn);
				} catch (IndexOutOfBoundsException e) {
					available = false;
				}
				if (!available) {

					// resets instance variables for opponents turn

					turn = "Red";
					message = "Red's Turn";
				}
			}
		}
		
		// checks if there are any available moves after a jump
			// (j,i) - coordinates of jump location
			// JButton btn - button of jumping location
			private boolean checkAvailableMove(int j, int i, JButton btn) {
				boolean valid = false;
				String currentTurn = btn.getName();

				if (Objects.equals(currentTurn, "White")) {
					if ((Objects.equals(boardSquares[j - 1][i - 1].getName(), "Red")
							|| (Objects.equals(boardSquares[j - 1][i - 1].getName(), "RedKing")))
							&& boardSquares[j - 2][i - 2].getName() == null) {
						valid = true;
					} else if ((Objects.equals(boardSquares[j + 1][i - 1].getName(), "Red")
							|| (Objects.equals(boardSquares[j + 1][i - 1].getName(), "RedKing")))
							&& boardSquares[j + 2][i - 2].getName() == null) {
						valid = true;
					}
				}

				else if (Objects.equals(currentTurn, "Red")) {
					if ((Objects.equals(boardSquares[j - 1][i + 1].getName(), "White")
							|| (Objects.equals(boardSquares[j - 1][i + 1].getName(), "WhiteKing")))
							&& boardSquares[j - 2][i + 2].getName() == null) {
						valid = true;
					} else if ((Objects.equals(boardSquares[j + 1][i + 1].getName(), "White")
							|| (Objects.equals(boardSquares[j + 1][i + 1].getName(), "WhiteKing")))
							&& boardSquares[j + 2][i + 2].getName() == null) {
						valid = true;
					}

				} else if (Objects.equals(currentTurn, "WhiteKing")) {
					if ((Objects.equals(boardSquares[j - 1][i - 1].getName(), "Red")
							|| (Objects.equals(boardSquares[j - 1][i - 1].getName(), "RedKing")))
							&& boardSquares[j - 2][i - 2].getName() == null) {
						valid = true;
					} else if ((Objects.equals(boardSquares[j + 1][i - 1].getName(), "Red")
							|| (Objects.equals(boardSquares[j + 1][i - 1].getName(), "RedKing")))
							&& boardSquares[j + 2][i - 2].getName() == null) {
						valid = true;
					} else if ((Objects.equals(boardSquares[j - 1][i + 1].getName(), "Red")
							|| (Objects.equals(boardSquares[j - 1][i + 1].getName(), "RedKing")))
							&& boardSquares[j - 2][i + 2].getName() == null) {
						valid = true;
					} else if ((Objects.equals(boardSquares[j + 1][i + 1].getName(), "Red")
							|| (Objects.equals(boardSquares[j + 1][i + 1].getName(), "RedKing")))
							&& boardSquares[j + 2][i + 2].getName() == null) {
						valid = true;
					}

				} else if (Objects.equals(currentTurn, "RedKing")) {
					if ((Objects.equals(boardSquares[j - 1][i - 1].getName(), "White")
							|| (Objects.equals(boardSquares[j - 1][i - 1].getName(), "WhiteKing")))
							&& boardSquares[j - 2][i - 2].getName() == null) {
						valid = true;
					} else if ((Objects.equals(boardSquares[j + 1][i - 1].getName(), "White")
							|| (Objects.equals(boardSquares[j + 1][i - 1].getName(), "WhiteKing")))
							&& boardSquares[j + 2][i - 2].getName() == null) {
						valid = true;
					} else if ((Objects.equals(boardSquares[j - 1][i + 1].getName(), "White")
							|| (Objects.equals(boardSquares[j - 1][i + 1].getName(), "WhiteKing")))
							&& boardSquares[j - 2][i + 2].getName() == null) {
						valid = true;
					} else if ((Objects.equals(boardSquares[j + 1][i + 1].getName(), "White")
							|| (Objects.equals(boardSquares[j + 1][i + 1].getName(), "WhiteKing")))
							&& boardSquares[j + 2][i + 2].getName() == null) {
						valid = true;
					}
				}

				return valid;
			}
			
			// updates game board after non jump move
			// handles king and non-king moving
			// uses previous piece location from <Integer> moves(x,y)
			// (j,i) - coordinates of location to be moved to
			public void repaintMove(int j, int i, JButton btn) {
				Icon piece;

				// White's turn
				if (Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "White")) {

					// updates piece location after jump
					piece = new ImageIcon("whitePiece.png");
					boardSquares[j][i].setIcon(piece);
					boardSquares[j][i].setName("White");

					// Removes old piece location
					ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
					boardSquares[moves.get(0)][moves.get(1)].setIcon(icon);
					boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
					boardSquares[moves.get(0)][moves.get(1)].setName(null);

					// resets instance variables for opponents turn
					moves.clear();
					firstMove = true;
					turn = "Red";
					message = "Red's Turn";

					// Red's turn
				} else if (Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "Red")) {

					// updates piece location after jump
					piece = new ImageIcon("redPiece.png");
					boardSquares[j][i].setIcon(piece);
					boardSquares[j][i].setName("Red");

					// Removes old piece location
					ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
					boardSquares[moves.get(0)][moves.get(1)].setIcon(icon);
					boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
					boardSquares[moves.get(0)][moves.get(1)].setName(null);

					// resets instance variables for next turn
					moves.clear();
					firstMove = true;
					turn = "White";
					message = "White's Turn";
				} else if (Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "RedKing")) {

					// updates piece location after jump
					piece = new ImageIcon("redKing.png");
					boardSquares[j][i].setIcon(piece);
					boardSquares[j][i].setName("RedKing");

					// Removes old piece location
					ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
					boardSquares[moves.get(0)][moves.get(1)].setIcon(icon);
					boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
					boardSquares[moves.get(0)][moves.get(1)].setName(null);

					// resets instance variables for opponents turn
					moves.clear();
					firstMove = true;
					turn = "White";
					message = "White's Turn";
				} else if (Objects.equals(boardSquares[moves.get(0)][moves.get(1)].getName(), "WhiteKing")) {

					// updates piece location after jump
					piece = new ImageIcon("whiteKing.png");
					boardSquares[j][i].setIcon(piece);
					boardSquares[j][i].setName("WhiteKing");

					// Removes old piece location
					ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
					boardSquares[moves.get(0)][moves.get(1)].setIcon(icon);
					boardSquares[moves.get(0)][moves.get(1)].setBorderPainted(false);
					boardSquares[moves.get(0)][moves.get(1)].setName(null);

					// resets instance variables for opponents turn
					moves.clear();
					firstMove = true;
					turn = "Red";
					message = "Red's Turn";
				}
			}
			
			
			// checks if a non-jump move is valid
			// only checks for non-king pieces
			// (j,i) - coordinates of location to move to
			public boolean validMove(int j, int i, JButton btn) {
				boolean valid = false;
				// check if whites move is valid
				// checks to see if the target location is
				// an adjacent black square
				// and not already occupied by a piece
				if (Objects.equals(turn, "White") && (i + 1 == moves.get(1)) && (j + 1 == moves.get(0) || j - 1 == moves.get(0))
						&& (btn.getName() == null)) {
					valid = true;
					// checks if reds move is valid
				} else if (i - 1 == moves.get(1) && (j + 1 == moves.get(0) || j - 1 == moves.get(0))
						&& (btn.getName() == null)) {
					valid = true;
				}

				return valid;
			}
			
			// check if a non-king jump is valid
			public boolean checkJump(int j, int i, JButton btn) {
				boolean valid = false;
				// white's turn
				// checks if the target location is 2 squares
				// away from the starting destination adjacently
				// also checks if the middle space is occupied
				// by an opposing game piece
				if (Objects.equals(turn, "White") && (i + 2 == moves.get(1)) && (j + 2 == moves.get(0) || j - 2 == moves.get(0))
						&& (btn.getName() == null)
						&& (Objects.equals(boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].getName(), "Red")
								|| Objects.equals(boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].getName(),
										"RedKing"))) {
					valid = true;
					// red's turn
				} else if (i - 2 == moves.get(1) && (j + 2 == moves.get(0) || j - 2 == moves.get(0)) && (btn.getName() == null)
						&& (Objects.equals(boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].getName(), "White")
								|| Objects.equals(boardSquares[(moves.get(0) + j) / 2][(moves.get(1) + i) / 2].getName(),
										"WhiteKing"))) {
					valid = true;
				}
				return valid;
			}
			
			// check if there is a winner
			// if whitePieces or redPieces = 0,
			// returns true and opens dialog window
			public boolean checkWin() {
				boolean valid = false;
				if (whitePieces == 0) {
					//TODO JOptionPane.showMessageDialog(gui, "Red is the Winner!!!");
					message = "Red is the Winner!!";
					valid = true;
				} else if (redPieces == 0) {
					message = "White is the Winner!!";
					//TODO JOptionPane.showMessageDialog(gui, "White is the Winner!!!");
					valid = true;
				}
				return valid;
			}
			
			// promoting kings when they reach the end of the board
			// changes icon and button name to king
			public void promoteKing(JButton btn, int i) {
				Icon piece;
				if (Objects.equals(btn.getName(), "White") && i == 0) {
					piece = new ImageIcon("whiteKing.png");
					btn.setIcon(piece);
					btn.setName("WhiteKing");
				} else if (Objects.equals(btn.getName(), "Red") && i == 7) {
					piece = new ImageIcon("redKing.png");
					btn.setIcon(piece);
					btn.setName("RedKing");
				}
			}
			
			// clears board after a win
			public void clearBoard() {
				for (int ii = 0; ii < boardSquares.length; ii++) {
					for (int jj = 0; jj < boardSquares[ii].length; jj++) {
						ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
						boardSquares[jj][ii].setIcon(icon);
					}
				}
			}
			
}
