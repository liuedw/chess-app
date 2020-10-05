package demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static javax.swing.JOptionPane.*;

public class BoardGUI {
    // Carries out all the internal data like the ChessPosition
    public ProgramStateMachine psm;

    private final JFrame frame;
    private JSplitPane gui;
    private static JPanel chessBoard;

    // A panel allowing users to save, load, start a new game, and contains some helpful information
    private JPanel sidebar;

    // A 2D array of buttons representing the chessboard
    private final JButton[][] board = new JButton[8][8];

    // Panels with helpful information
    private JPanel turnDisplay;
    private JPanel selectedDisplay;

    // Values indicating the status of the chess bot
    private boolean botThinking;
    private boolean botGame;

    // The chess pieces
    private static Map<String, ImageIcon> assets;

    private final Color beige = new Color(235,236,208);
    private final Color green = new Color(119,149,86);
    private final Color background = new Color(49,46, 43);
    private final Color buttonColor = new Color(39, 37, 34);

    /**
     * Creates a new BoardGUI class
     * @param frame The JFrame object that the BoardGUI will be put in.
     */
    public BoardGUI(JFrame frame) {
        this.frame = frame;
        psm = new ProgramStateMachine(false);

        try {
            String[] pieces = {"bp", "bb", "bn", "bk", "br", "bq", "bk", "wp", "wb", "wn", "wk", "wr", "wq", "wk"};
            assets = new HashMap<>();
            for (String piece : pieces) {
                assets.put(piece, new ImageIcon(new URL("http://images.chesscomfiles.com/chess-themes/pieces/neo/64/" + piece + ".png")));
            }
        } catch (Exception e) {
            System.err.println("Piece not found");
        }
        initializeBoard();
        initializeSideBar();
        initializeGUI();
        initializeGame();
    }

    /**
     * Returns a JSplitPane denoting the gui.
     * @return A JSplitPane that represents the chess board and a side bar with some additional functionality.
     */
    public JSplitPane getGUI() {
        return gui;
    }

    private void initializeGUI() {
        // Place the chessboard and sidebar in a nice formatted container
        JPanel boardConstrain = new JPanel(new GridBagLayout());
        boardConstrain.add(sidebar);
        boardConstrain.setBackground(background);
        chessBoard.setBackground(background);
        boardConstrain.add(chessBoard);

        gui = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, boardConstrain);
        gui.setOneTouchExpandable(true);
        gui.setDividerLocation(200);
    }

    private void initializeSideBar() {
        // Set side bar dimensions
        sidebar = new JPanel(new GridLayout(5, 1));
        sidebar.setMinimumSize(new Dimension(200, 50));
        sidebar.setPreferredSize(new Dimension(200, 50));

        // Implements a feature to display the current player's turn
        turnDisplay = new TurnDisplay();
        turnDisplay.setBackground(Color.WHITE);
        sidebar.add(turnDisplay);

        // Option to allow a person to start a new game with a human or computer
        JButton newGame = new JButton("New Game");
        formatSideButton(newGame);
        newGame.addActionListener((ActionEvent e) -> {
            String[] choices = {"Against A Person", "Against A Bot", "Cancel"};
            int response = JOptionPane.showOptionDialog(frame, "Who would you like to play with?", "New Game",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[2]);
            botGame = false;
            // Sorry about the nested switch statements, didn't realize it looked really messy until too late
            // Opens a dialogue box allowing players to choose an opponent to play
            switch (response) {
                case 0:  // Start a new game with another person
                    initializeGame();
                    break;
                case 1:  // Start a new game with a computer
                    String[] colors = {"White", "Black", "Cancel"};
                    int choice = JOptionPane.showOptionDialog(frame, "Which side would you like to play?", "Play Against A Bot",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, colors, colors[2]);
                    botGame = true;
                    switch (choice) {
                        case 0:  // Play as white against the computer
                            psm.setUserColor(Player.WHITE);
                            initializeGame();
                            break;
                        case 1:  // Play as black against the computer
                            psm.setUserColor(Player.BLACK);
                            initializeGame();
                            break;
                        default:  // Cancel
                            break;
                    }
                    break;
                default:  // Cancel
                    break;
            }
        });
        sidebar.add(newGame);

        // Option to allow a player to load a game and then play against a computer or person
        // Players will be able to select a file without typing anything
        JButton load = new JButton("Load Game");
        formatSideButton(load);
        load.addActionListener((ActionEvent e) -> {  // Prompt user to select a file to load save data from
            JFileChooser fc = new JFileChooser();
            int result = fc.showOpenDialog(frame);
            File f = null;
            if (result == JFileChooser.APPROVE_OPTION) {  // File was found
                f = fc.getSelectedFile();
            }
            if (f != null) {  // Does nothing if file not found
                String[] choices = {"Against A Person", "Against A Bot", "Cancel"};
                int response = JOptionPane.showOptionDialog(frame, "Who would you like to play with?", "New Game",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[2]);
                botGame = false;
                switch (response) {
                    case 0:
                        try {  // Play loaded file with a person
                            loadGame(f.getPath());
                        } catch (IOException ioException) {  // Theoretically should never happen unless file somehow disappears
                            System.err.println("File has disappeared!");
                            ioException.printStackTrace();
                        }
                        break;
                    case 1:  // Play loaded file with computer
                        String[] colors = {"White", "Black", "Cancel"};
                        int choice = JOptionPane.showOptionDialog(frame, "Which side would you like to play?", "Play Against A Bot",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, colors, colors[2]);
                        botGame = true;
                        switch (choice) {
                            case 0:  // Play as white
                                psm.setUserColor(Player.WHITE);
                                try {
                                    loadGame(f.getPath());
                                } catch (IOException ioException) {
                                    System.err.println("File has disappeared!");
                                    ioException.printStackTrace();
                                }
                                break;
                            case 1:  // Play as black
                                psm.setUserColor(Player.BLACK);
                                try {
                                    loadGame(f.getPath());
                                } catch (IOException ioException) {
                                    System.err.println("File has disappeared!");
                                    ioException.printStackTrace();
                                }
                                break;
                            default:  // Cancel load game
                                break;
                        }
                        break;
                    default:  // Cancel laod game
                        break;
                }
            }
        });
        sidebar.add(load);

        // Opens a window to allow user to save a game somewhere
        JButton save = new JButton("Save Game");
        formatSideButton(save);
        save.addActionListener((ActionEvent j) -> {
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(frame);
            File f = null;
            if (result == JFileChooser.APPROVE_OPTION) {
                f = fc.getSelectedFile();
            }
            if (f != null) {
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(f);
                } catch (FileNotFoundException e) {  // I'm really not sure how this could possibly happen
                    e.printStackTrace();
                }
                try {  // Save the game to the specified file
                    ChessSerializer.serialize(fout, psm.getChessPosition());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        sidebar.add(save);

        // Small QoL feature that allows users to see the currently selected piece at bottom left
        selectedDisplay = new SelectedDisplay();
        sidebar.add(selectedDisplay);
    }

    // Initializes the chess board including all the buttons that represent the board grid
    private void initializeBoard() {

        // The grid representing the panel representing the chessboard
        chessBoard = new JPanel(new GridLayout(0, 9)) {
            public final Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension(
                            (int)d.getWidth(),(int)d.getHeight());
                } else if (c.getWidth() > d.getWidth() &&
                        c.getHeight() > d.getHeight()) {
                    prefSize = c.getSize();
                } else {
                    prefSize = d;
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                // the smaller of the two sizes
                int s = (Math.min(w, h));
                return new Dimension(s,s);
            }
        };

        // Add formatted buttons to the chessboard 1 at a time
        boolean dark = true;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                JButton button = new JButton();
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setBackground(dark ? green : beige);
                button.setFocusPainted(false);
                board[x][y] = button;

                // A easier to read representation of the board (i.e. A1)
                String coordinate = (char) (x + 'A') + Integer.toString(8 - y) + "";
                button.setActionCommand(coordinate);
                button.addActionListener((ActionEvent e) -> {

                    // Assign "To" or "From" part of a move object
                    setMove(e.getActionCommand());

                    // Highlight the currently selected piece in the chessboard
                    if (e.getActionCommand().equals(MoveCommand.source)) {
                        button.setBorder(BorderFactory.createLineBorder(Color.RED));
                    } else {
                        button.setBorder(null);
                    }

                    // We have a whole move (to and from)... proceed to make the move
                    if (MoveCommand.bothSelected()) {
                        attemptMove(null);

                        // Bot makes it's move next if playing with a bot
                        botMove();

                        turnDisplay.repaint();
                    }
                    // Move has been made so pieces are no longer shown as selected in GUI
                    selectedDisplay.repaint();
                });
                dark = !dark;
            }
            dark = !dark;
        }

        // Add labels to denote the coordinates on the chessboard
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H"};
        chessBoard.add(new JLabel());
        for (int i = 0; i < 8; i++) {
            JLabel label = new JLabel(letters[i], SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            chessBoard.add(label);
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j == 0) {
                    JLabel label = new JLabel("" + (9 - i - 1), SwingConstants.CENTER);
                    label.setForeground(Color.WHITE);
                    label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                    chessBoard.add(label);
                }
                chessBoard.add(board[j][i]);
            }
        }

    }

    private void setMove(String s) {
        if (MoveCommand.source == null) {
            MoveCommand.source = s;
        } else if (MoveCommand.source.equals(s)) {
            MoveCommand.source = null;
        } else {
            MoveCommand.dest = s;
        }
    }

    // Loads a new game from a specified file path
    private void loadGame(String path) throws IOException {
        psm.loadGame(path, botGame ? "pvb" : "pvp");
        Board loadBoard = psm.getChessPosition().getBoard();
        // Add pieces to the GUI chessboard 1 at a time
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = loadBoard.getPiece(i , j);
                if (p == null) {
                    board[i][j].setIcon(null);
                } else {
                    String name = p.getShortHand();
                    board[i][j].setIcon(assets.get(name));
                }
            }
        }
        switchBoard(true);
        // Bot makes the first move if loaded onto the bot's turn
        botMove();
    }

    // Attempt to make a move in the chess game
    // Move m will only NOT be null if the bot is making a move
    private void attemptMove(Move m) {
        ChessPosition cp = psm.getChessPosition();
        if (m == null) {  // If a human is making a move
            m = BoardCoord.move(MoveCommand.source, MoveCommand.dest);
        }
        if (!cp.possibleMoves().contains(m)) {
            System.err.println("Invalid move");
        } else {  // Make the move
            psm.move(m);
            int[] source = MoveCommand.toCoord(MoveCommand.source);
            int[] dest = MoveCommand.toCoord(MoveCommand.dest);

            // Updates the GUI chessboard
            JButton oldPos = board[source[0]][source[1]];
            board[dest[0]][dest[1]].setIcon(oldPos.getIcon());
            board[source[0]][source[1]].setIcon(null);

            // Check for checkmate or stalemate after a move is made and the winner if exists
            if (psm.getChessPosition().possibleMoves().isEmpty()) {
                Player oldPlayer = psm.getChessPosition().getPlayer().equals(Player.WHITE) ? Player.BLACK : Player.WHITE;
                if (psm.getChessPosition().isInCheck(oldPlayer)) {
                    endGame("Stalemate");
                }
                endGame(oldPlayer.equals(Player.WHITE) ? "Black" : "White");
            }
        }

        // Remove red outlining from the piece that was just moved
        int[] coordinates = MoveCommand.toCoord(MoveCommand.source);
        board[coordinates[0]][coordinates[1]].setBorder(null);
        MoveCommand.clear();
    }

    // The game is over, takes a string representing the game state (black wins/white wins/draw)
    private void endGame(String p) {
        String endMessage = (p.equals("Stalemate") ? "Draw!" : p + " wins!") + "\nWould you like to play again?";

        // New popup message with information about the winner and what to do next
        int choice = JOptionPane.showConfirmDialog(frame, endMessage , "Game Over", JOptionPane.YES_NO_OPTION);
        System.out.println(p + " wins!");
        for (JButton[] buttonArr : board) {
            for (JButton button : buttonArr) {
                button.setDisabledIcon(button.getIcon());
                switchBoard(false);
            }
        }

        if (choice == NO_OPTION) {  // Check if user is done playing
            choice = JOptionPane.showConfirmDialog(frame, "Would you like to exit?" , "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == YES_OPTION) {
                System.exit(0);
            }
        }
    }

    // Starts a new game with pieces in initial positions
    private void initializeGame() {
        // Set up pawns
        for (int i = 0; i < 8; i++) {
            board[i][1].setIcon(assets.get("bp"));
            board[i][6].setIcon(assets.get("wp"));
        }
        // Set up everything else
        for (int i = 0; i < 8; i++) {
            switch(i) {
                case 0:
                case 7:
                    board[i][0].setIcon(assets.get("br"));
                    board[i][7].setIcon(assets.get("wr"));
                    break;
                case 1:
                case 6:
                    board[i][0].setIcon(assets.get("bn"));
                    board[i][7].setIcon(assets.get("wn"));
                    break;
                case 2:
                case 5:
                    board[i][0].setIcon(assets.get("bb"));
                    board[i][7].setIcon(assets.get("wb"));
                    break;
                case 3:
                    board[i][0].setIcon(assets.get("bq"));
                    board[i][7].setIcon(assets.get("wq"));
                    break;
                case 4:
                    board[i][0].setIcon(assets.get("bk"));
                    board[i][7].setIcon(assets.get("wk"));
            }
        }

        // Ensure the rest of the board is clear (if starting more than 1 game)
        for (int i = 0; i < 8; i++) {
            for (int j = 2; j < 6; j++) {
                board[i][j].setIcon(null);
            }
        }
        switchBoard(true);
        psm.startGame(botGame ? "pvb" : "pvp");
        botMove();
    }

    // Turns the chessboard on or off
    // When on, any member can be clicked
    // When off, the board cannot be clicked
    private void switchBoard(boolean on) {
        for (JButton[] buttonArr : board) {
            for (JButton button : buttonArr) {
                button.setDisabledIcon(button.getIcon());
                button.setEnabled(on);
            }
        }
        turnDisplay.repaint();
    }

    // Calls on the chessbot to make a move
    private void botMove() {
        if (botGame && !psm.getChessPosition().getPlayer().equals(psm.getUser())) {
            // Done on a different thread so that the board doesn't freeze while the bot is thinking
            Thread t = new Thread(() -> {
                botThinking = true;
                switchBoard(false);
                Move m = psm.getBot().getBestMove((psm.getChessPosition()));
                MoveCommand.source = m.toString().substring(0, 2);
                MoveCommand.dest = m.toString().substring(m.toString().length() - 2);
                attemptMove(psm.getBot().getBestMove(psm.getChessPosition()));
                botThinking = false;
                if (!psm.getChessPosition().possibleMoves().isEmpty()) {
                    switchBoard(true);
                }
            });
            t.start();
        }
    }

    // Makes the buttons on the side bar look nice
    private void formatSideButton(JButton b) {
        b.setFocusPainted(false);
        b.setMargin(new Insets(0, 0, 0, 0));

        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        b.setBackground(buttonColor);
        b.setForeground(Color.WHITE);
    }

    // A class that allows a TurnDisplay object to display each player's turns
    private class TurnDisplay extends JPanel {
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            super.paintComponent(g);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(buttonColor);
            if (botThinking) {
                String message = "Bot is thinking...";
                int x = (getWidth() - fm.stringWidth(message)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(message, x, y);
            } else {
                String turn = psm.getChessPosition().getPlayer() == Player.WHITE ? "White" : "Black";
                int x = (getWidth() - fm.stringWidth(turn)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(turn, x, y - (fm.getHeight() / 2));
                x = (getWidth() - fm.stringWidth("To Move")) / 2;
                g2.drawString("To Move", x, y + (fm.getHeight() / 2));
            }
        }
    }

    // A class to allow a SelectedDisplay object show the piece a player has clicked
    private class SelectedDisplay extends JPanel {
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            super.paintComponent(g);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(buttonColor);
            String message = "Currently Selected Piece";
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(message, x, y - (fm.getHeight() * 5) / 2);

            setBackground(g2);
        }

        private void setBackground(Graphics2D g2) {
            if (MoveCommand.source != null) {
                int[] coordinates = MoveCommand.toCoord(MoveCommand.source);
                int sum = coordinates[0] + coordinates[1];
                this.setBackground(sum % 2 == 0 ? green : beige);
                Piece piece = psm.getChessPosition().getBoard().getPiece(coordinates[0], coordinates[1]);
                if (piece != null) {
                    ImageIcon icon = assets.get(piece.getShortHand());
                    g2.drawImage(icon.getImage(), (getWidth() - icon.getIconWidth()) / 2, (getHeight() - icon.getIconHeight()) / 2, this);
                } else {
                    this.setBackground(buttonColor);
                    g2.drawImage(null,0, 0, null);
                }
            } else {
                this.setBackground(buttonColor);
                g2.drawImage(null,0, 0, null);
            }
        }
    }
}
