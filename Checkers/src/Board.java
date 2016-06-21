import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.*;

//import checkers.Board;

public class Board implements Serializable{

	private final JPanel gui = new JPanel(new BorderLayout(3, 3));
	private JButton[][] boardSquares = new JButton[8][8];
	private JPanel checkersBoard;
	private final JLabel message = new JLabel("Let's Play Checkers!");
	private static final String COLS = "01234567";
	Border selected = new LineBorder(Color.YELLOW, 4);
	boolean firstMove = true;
	ArrayList<Integer> moves = new ArrayList<Integer>();
	String turn = "";
	int whitePieces;
	int redPieces;
	Offline off = new Offline();
	TwoPlayerOnline con = new TwoPlayerOnline("10.134.213.188", 9991);
	JFrame popUp = new JFrame();
	static JFrame f;
	JPanel text = new JPanel();

	public Board() {
		initializeGui();
	}

	public final void initializeGui() {
		// set up the main GUI
		gui.setBorder(new EmptyBorder(5, 5, 5, 5));
		JToolBar tools = new JToolBar();
		tools.setFloatable(false);
		gui.add(tools, BorderLayout.PAGE_START);

		JButton twoPlayerOnlineButton = new JButton("2-Player Online");
		twoPlayerOnlineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					twoPlayerOnConnect();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JButton singlePlayerButton = new JButton("Single Player");
		singlePlayerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				singlePlayer();
			}
		});

		JButton hadoopButton = new JButton("Hadoop");
		// TODO hadoopButton.addActionListener(new showBoard());

		// Starts new two player offline game
		JButton twoPlayerOfflineButton = new JButton("2-Player Offline");
		twoPlayerOfflineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				twoPlayerOff();
			}
		});

		tools.add(singlePlayerButton);
		tools.add(twoPlayerOfflineButton);
		tools.add(twoPlayerOnlineButton);
		tools.add(hadoopButton);
		tools.addSeparator();
		tools.add(message);

		gui.add(new JLabel("?"), BorderLayout.LINE_START);

		checkersBoard = new JPanel(new GridLayout(0, 9));
		checkersBoard.setBorder(new LineBorder(Color.BLACK));
		gui.add(checkersBoard);

		// create the squares
		Insets buttonMargin = new Insets(0, 0, 0, 0);
		for (int ii = 0; ii < boardSquares.length; ii++) {
			for (int jj = 0; jj < boardSquares[ii].length; jj++) {
				JButton b = new JButton();
				b.setMargin(buttonMargin);
				b.setBorder(selected);
				b.setBorderPainted(false);
				// icon placeholder, keeps squares correct size
				ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)); // TODO
				b.setIcon(icon);
				if ((jj % 2 == 1 && ii % 2 == 1) || (jj % 2 == 0 && ii % 2 == 0)) {
					b.setBackground(Color.RED);
				} else {
					b.setBackground(Color.BLACK);
				}
				boardSquares[jj][ii] = b;
			}
		}

		// label horizontal axis
		checkersBoard.add(new JLabel(""));
		for (int ii = 0; ii < 8; ii++) {
			checkersBoard.add(new JLabel(COLS.substring(ii, ii + 1), SwingConstants.CENTER));
		}
		// label vertical axis and add squares to gui
		for (int ii = 0; ii < 8; ii++) {
			for (int jj = 0; jj < 8; jj++) {
				switch (jj) {
				case 0:
					checkersBoard.add(new JLabel("" + (ii), SwingConstants.CENTER));
				default:
					checkersBoard.add(boardSquares[jj][ii]);
				}
			}
		}
	}

	public void repaintBoard(JButton[][] board) {
		for (int ii = 0; ii < board.length; ii++) {
			for (int jj = 0; jj < board[ii].length; jj++) {
				boardSquares[jj][ii].setName(board[jj][ii].getName());
				boardSquares[jj][ii].setIcon(board[jj][ii].getIcon());
			}
		}
	}

	public void createButtons() {
		for (int ii = 0; ii < boardSquares.length; ii++) {
			for (int jj = 0; jj < boardSquares[ii].length; jj++) {
				final int tempJ = jj;
				final int tempI = ii;
				boardSquares[jj][ii].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JButton btn = (JButton) e.getSource();
						off.play(btn, tempJ, tempI);
						boardSquares = off.getBoard();
						repaintBoard(boardSquares);
						message.setText(off.getMessage());
						if (off.checkWin()) {
							JOptionPane.showMessageDialog(gui, off.getMessage());

						}

					}
				});
			}
		}
	}

	// possibly use later
	public final JComponent getCheckersBoard() {
		return checkersBoard;
	}

	// possibly use later
	public final JComponent getGui() {
		return gui;
	}

	// creates new two player offline game
	public void twoPlayerOff() {

		off.setBoard(boardSquares);
		off.newGame();
		boardSquares = off.getBoard();
		repaintBoard(boardSquares);

		createButtons();

	}

	public void twoPlayerOnPlay(String player2) throws ClassNotFoundException, IOException {

		con.newGame(player2);
		off.setBoard(boardSquares);
		off.newGame();
		boardSquares = off.getBoard();
		repaintBoard(boardSquares);

	}

	public void twoPlayerOnConnect() throws UnknownHostException, IOException, ClassNotFoundException {
		final JList<Object> list = new JList<Object>(con.newPlayer());
		list.setVisibleRowCount(-1);

		JButton connect = new JButton("Connect to Player");
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					twoPlayerOnPlay(list.getSelectedValue().toString());
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JList<Object> list = new JList<Object>(con.getPlayerList());
					text.add(list);

				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		text.add(list);
		text.add(connect);
		text.add(refresh);
		popUp.add(text);
		popUp.pack();
		popUp.setVisible(true);

	}

	public void singlePlayer() {
		//new Checkers(9);
		popUp.getContentPane().removeAll();
		f.getContentPane().removeAll();
		new Checkers(9, "127.0.0.1", 2597);
	}

	public static void main(String[] args) {
		Runnable r = new Runnable() {

			public void run() {
				Board cb = new Board();

				f = new JFrame("Checkers Man");
				f.add(cb.getGui());
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setLocationByPlatform(true);

				// ensures the frame is the minimum size it needs to be
				// in order display the components within it
				f.pack();
				// ensures the minimum size is enforced.
				f.setMinimumSize(f.getSize());
				f.setVisible(true);
			}

		};

		SwingUtilities.invokeLater(r);
	}

}

