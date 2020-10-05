package demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

// a state machine used to model the welcome screen and the pvp chess screen
public class ProgramStateMachine extends StateMachine {

    private boolean commandLineGame = true;
    /**
     * both of these will be null if the user isn't playing against a bot
     */
    private ChessBot bot = null;
    private Player user = null;

    private static final String invalidCommand = "Invalid command. Type help to show all commands.";
    private static final String errorOccured = "An error has occured. Enable debug mode to see stack trace.";

    private static final String welcomeMsg =
            "*****************************************************************\n" +
                    "                  Welcome to the chess game!\n" +
                    "This is a all-console chess game made for a cse331-20su homework.\n" +
                    "     Credits goes to: Caiwei Tian, Brian Yao, and Jiuru Li\n" +
                    "*****************************************************************";

    private static final String terminalSizeMsg =
            "NOTICE: Make sure you are running this with a terminal with monospace\n" +
                    "font, at least a width of 64 characters and a height of 36 lines.";

    private static final String chooseBoardHelp =
            "start pvp               -- start a new game and control both sides.\n" +
                    "start pvb               -- start a new game against a bot.\n" +
                    "load <filepath> pvp     -- load a game from a file and control both sides.\n" +
                    "load <filepath> pvb     -- load a game from a file and play against a bot.\n" +
                    "exit|quit               -- exit this program.\n" +
                    "enable|disable <attr>   -- enable or disable an attribute.\n" +
                    "help                    -- show this message.\n" +
                    "manual                  -- show a more detailed manual.";

    private static final String pvpHelp =
            "move|mv <coord> <coord> -- move a chess piece. e.g. mv d2 d4\n" +
                    "save <filepath>         -- save current game to a file.\n" +
                    "exit|quit               -- exit this program.\n" +
                    "return                  -- return to welcome screen.\n" +
                    "help                    -- show this message.\n" +
                    "setply <num>            -- sets the search depth for the bot (3 by default, don't recommend higher).\n"+
                    "getply                  -- outputs the ply the bot is currently searching to";

    private static final String manual =
            "move|mv <coordinate> <coordinate>\n" +
                    "   This command moves a chess piece from one coordinate to\n" +
                    "       another coordinate.\n" +
                    "   A coordinate is a combination of a letter and a number.\n" +
                    "   For example, \"move d4 d6\" moves the piece at column D,\n" +
                    "       4th row to column D, 6th row.\n" +
                    "   In this program, d1, D1, 1d, 1D are all acceptable.\n" +
                    "   * Only available in a game\n\n" +
                    "save <filepath>\n" +
                    "   Save current game to a given file.\n" +
                    "   * Only available in a game\n\n" +
                    "return\n" +
                    "   Return to the welcome screen.\n" +
                    "   * Only available in a game\n\n" +
                    "start pvp\n" +
                    "   Start a standard chess game and control both sides.\n" +
                    "   * Only available in welcome screen\n\n" +
                    "start pvb\n" +
                    "   Start a standard chess game and play against a bot.\n" +
                    "   * Only available in welcome screen\n\n" +
                    "load <filepath> pvp\n" +
                    "   Load a previous game from a file and control both sides.\n" +
                    "   * Only available in welcome screen\n\n" +
                    "load <filepath> pvb\n" +
                    "   Load a previous game from a file and play against a bot.\n" +
                    "   * Only available in welcome screen\n\n" +
                    "exit|quit\n" +
                    "   Exit the program.\n\n" +
                    "enable|disable <attribute>\n" +
                    "   This command enables or disables an attribute in the program.\n" +
                    "   Available attributes are:\n" +
                    "       show_help - this attribute decides whether the program will\n" +
                    "           show help message everytime an input prompt appears\n" +
                    "       debug_mode - this attribute decides whether the program\n" +
                    "           will print stack traces on failures";

    private final String[] playerText = { "white", "black" };

    public static ChessPosition chessPosition;

    private Scanner consoleIn;
    private boolean showHelp = true;
    private boolean isDebug = false;

    /**
     * returns a board in starting position
     */
    private static ChessPosition standardBoard() {
        ChessPosition chess = new ChessPosition(Player.WHITE);
        /* Starting position
         * R N B Q K B N R
         * P P P P P P P P
         *   #   #   #   #
         * #   #   #   #
         *   #   #   #   #
         * #   #   #   #
         * P P P P P P P P
         * R N B Q K B N R
         */
        // White
        chess.populate(Piece.WROOK, 0, 7);
        chess.populate(Piece.WKNIGHT, 1, 7);
        chess.populate(Piece.WBISHOP, 2, 7);
        chess.populate(Piece.WQUEEN, 3, 7);
        chess.populate(Piece.WKING, 4, 7);
        chess.populate(Piece.WBISHOP, 5, 7);
        chess.populate(Piece.WKNIGHT, 6, 7);
        chess.populate(Piece.WROOK, 7, 7);
        for (int i = 0; i < 8; i++) {
            chess.populate(Piece.WPAWN, i, 6);
        }
        // Black
        chess.populate(Piece.BROOK, 0, 0);
        chess.populate(Piece.BKNIGHT, 1, 0);
        chess.populate(Piece.BBISHOP, 2, 0);
        chess.populate(Piece.BQUEEN, 3, 0);
        chess.populate(Piece.BKING, 4, 0);
        chess.populate(Piece.BBISHOP, 5, 0);
        chess.populate(Piece.BKNIGHT, 6, 0);
        chess.populate(Piece.BROOK, 7, 0);
        for (int i = 0; i < 8; i++) {
            chess.populate(Piece.BPAWN, i, 1);
        }
        return chess;
    }

    /**
     * turns given attr on or off depending on val (affects UI)
     */
    private boolean setAttribute(String attr, boolean val) {
        if (attr.equals("show_help")) {
            showHelp = val;
        } else if (attr.equals("debug_mode")) {
            isDebug = val;
        } else {
            return false;
        }
        return true;
    }

    /**
     * check if cmd is from a collection of common commands
     *  if so, execute the command and return true
     */
    private boolean checkCommonCmds(String cmd, Scanner s) {
        if (cmd.equals("enable")) {
            return setAttribute(s.next(), true);
        } else if (cmd.equals("disable")) {
            return setAttribute(s.next(), false);

        } else if (cmd.equals("exit") || cmd.equals("quit")) {
            shutdown();
        } else if (cmd.equals("manual")) {
            System.out.println(manual);
        } else {
            return false;
        }
        return true;
    }

    // use a loop to ask user confirm to a yes/no question
    private boolean confirm() {
        while (true) {
            String next = consoleIn.nextLine();
            if (next.toLowerCase().equals("yes") || next.toLowerCase().equals("y")) {
                return true;
            } else if (next.toLowerCase().equals("no") || next.toLowerCase().equals("n")) {
                return false;
            } else {
                System.out.print("please answer with [yes/no/y/n]: ");
            }
        }
    }

    // check if std in is closed
    private Scanner getLineScanner(Scanner stdin) {
        try {
            return new Scanner(stdin.nextLine());
        } catch (NoSuchElementException e) {
            if (isDebug) {
                e.printStackTrace();
            }
            System.out.println("standard input is closed. aborting...");
            return null;
        }
    }

    /**
     * add possible game states to the machine
     */
    private void initializeStates() {
        // the main menu state
        addState("choose board", () -> {
            // onEnter
            if (commandLineGame) {
                System.out.println(welcomeMsg);
            }
            if (showHelp) {
                System.out.println(chooseBoardHelp);
            }
        }, () -> {
            // onUpdate
            System.out.println("What to do here?");
            System.out.print("> ");

            Scanner s = getLineScanner(consoleIn);
            if (s == null) {
                shutdown();
                return;
            }

            // start handling commands from here
            try {
                String next = s.next();
                if (next.equals("start")) {
                    //TODO
                    String gameType = "";
                    if (s.hasNext()) {
                        gameType = s.next();
                    } else {
                        System.out.println("Invalid option supplied to 'play'");
                        transitionTo("choose board");
                    }
                    startGame(gameType);
                } else if (next.equals("load")) {
                    String fileName = "";
                    if (s.hasNext()) {
                        fileName = s.next();
                        String gameType = "";
                        if (s.hasNext()) {
                            gameType = s.next();
                            loadGame(fileName, gameType);
                        } else {
                            System.out.println("Invalid option supplied to 'play'");
                            transitionTo("choose board");
                        }
                    } else {
                        System.out.println("Invalid option supplied to 'play'");
                        transitionTo("choose board");
                    }

                } else if (next.equals("help")) {
                    System.out.println(chooseBoardHelp);

                } else if (!checkCommonCmds(next, s)) {
                    System.out.println(invalidCommand);
                }
            } catch (NoSuchElementException e) {
                if (isDebug) {
                    e.printStackTrace();
                }
                System.out.println(invalidCommand);
            } catch (Exception e) {
                if (isDebug) {
                    e.printStackTrace();
                }
                System.out.println(errorOccured);
            }
        }, null);

        // the game state (one turn)
        addState("pvp", () -> {
            // onEnter
            // display the board here
            System.out.println("K = King, N = Knight, Q = Queen, B = Bishop, R = Rook, P = Pawn");
            System.out.println("Upper-case letters represents white; lower-case letters");
            System.out.println("represents black");
            System.out.println();
            BoardPresent.present(System.out, chessPosition.getBoard());

            if (showHelp) {
                System.out.println(pvpHelp);
            }
        }, () -> {
            // onUpdate
            // first check for checkmate and stalemate
            if (chessPosition.possibleMoves().isEmpty()) {
                if (chessPosition.isInCheck(chessPosition.getPlayer())) {
                    System.out.println("Checkmate! Game is set!");
                    int curPlayer = chessPosition.getPlayer().ordinal();
                    System.out.println("The winner is " + playerText[(curPlayer + 1) % 2] + ".");
                } else {
                    System.out.println("Stalemate! It's a draw!");
                }
                // return to main menu
                transitionTo("choose board");

            } else if (bot != null && chessPosition.getPlayer() != user) {
                //we know the user is playing against a bot, so let the bot move and then continue
                System.out.println("Bot is thinking...\n");
                Move botMove = bot.getBestMove(chessPosition);
                System.out.println("Bot moves " + botMove + "\n");
                chessPosition = chessPosition.move(botMove);
                transitionTo("pvp");
            } else {

                System.out.println("It\'s " + playerText[chessPosition.getPlayer().ordinal()] + "\'s turn, what to do here?");
                System.out.print("> ");

                Scanner s = getLineScanner(consoleIn);
                if (s == null) {
                    shutdown();
                    return;
                }

                // handle commands here
                try {
                    String next = s.next();
                    if (next.equals("move") || next.equals("mv")) {
                        String from = s.next();
                        String to = s.next();
                        if (to.toLowerCase().equals("to")) {
                            to = s.next();
                        }
                        Move m;
                        try {
                            m = BoardCoord.move(from, to);
                        } catch (IllegalArgumentException e) {
                            System.out.println("There is an ill-formatted position.");
                            return;
                        }
                        if (!chessPosition.possibleMoves().contains(m)) {
                            System.out.println("This is not a valid move.");
                        } else {
                            chessPosition = chessPosition.move(m);
                            // display the board, then the command interface again
                            //TODO
                            transitionTo("pvp");
                        }

                    } else if (next.equals("save")) {
                        String filename = s.nextLine().trim();
                        File f = new File(filename);

                        if (f.exists()) {
                            try {
                                System.out.print(filename + " already exists! override it? [yes/no/y/n]: ");
                                if (confirm()) {
                                    if (!f.delete()) {
                                        System.out.println("failed to delete " + filename);
                                        return;
                                    }
                                }
                            } catch (NoSuchElementException e) {
                                System.out.println();
                                System.out.println("standard input is closed. aborting...");
                                shutdown();
                                return;
                            }
                        }

                        FileOutputStream fout = null;
                        try {
                            fout = new FileOutputStream(filename);
                            ChessSerializer.serialize(fout, chessPosition);
                        } catch (IOException e) {
                            if (isDebug) {
                                e.printStackTrace();
                            }
                            System.out.println(errorOccured);
                        } finally {
                            if (fout != null) {
                                fout.close();
                            }
                        }

                    } else if (next.equals("setply")) {
                        int ply;
                        try {
                            ply = Integer.parseInt(s.next());
                            if (ply <= 0) {
                                System.out.println("ply must be a positive integer");
                            } else {
                                bot.setPly(ply);
                                System.out.println("Bot's ply set to " + ply + "\n");
                            }
                        } catch (Exception e) {
                            System.out.println("Bad format to 'setply' command");
                        }

                    } else if (next.equals("getply")) {
                        System.out.println("Bot is searching " + bot.getPly() + " moves ahead in the game\n");
                    } else if (next.equals("return")) {
                        bot = null;
                        user = null;
                        transitionTo("choose board");

                    } else if (next.equals("help")) {
                        System.out.println(pvpHelp);

                    } else if (!checkCommonCmds(next, s)) {
                        System.out.println(invalidCommand);
                    }
                } catch (NoSuchElementException e) {
                    if (isDebug) {
                        e.printStackTrace();
                    }
                    System.out.println(invalidCommand);
                } catch (Exception e) {
                    if (isDebug) {
                        e.printStackTrace();
                    }
                    System.out.println(errorOccured);
                }
            }
        }, null);
    }

    /**
     * starts a game
     *
     * @param gameType one of pvp or pvb, depending whether user is playing alone or against bot
     */
    public void startGame(String gameType) {
        chessPosition = standardBoard();
        if (gameType.equalsIgnoreCase("pvb")) {
            selectUserColor();
        } else if (gameType.equalsIgnoreCase("pvp")) {
            if (commandLineGame) {
                transitionTo("pvp");
            }
        } else {
            System.out.println("Invalid option supplied to 'play' command");
        }
    }

    /**
     * loads a saved game
     *
     * @param fileName the file to load from
     * @param gameType one of pvp or pvb, depending whether user is playing alone or against bot
     * @throws IOException if there is an error closing a FileInputStream that gets created (not caused
     * by error reading file; that prints an error message with more details)
     */
    public void loadGame(String fileName, String gameType) throws IOException {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(fileName);
            chessPosition = ChessSerializer.deserialize(fin);
            if (gameType.equalsIgnoreCase("pvb")) {
                selectUserColor();
            } else if (gameType.equalsIgnoreCase("pvp")) {
                if (commandLineGame) {
                    transitionTo("pvp");
                }
            } else {
                System.out.println("Invalid option supplied to 'load' command");
            }

        } catch (IOException e) {
            if (isDebug) {
                e.printStackTrace();
            }
            System.out.println("error reading or locating file: " + fileName);
        } catch (SerializationException e) {
            if (isDebug) {
                e.printStackTrace();
            }
            System.out.println("given save file is ill-formatted.");
        } finally {
            if (fin != null) {
                //annoyingly, this last part can throw an IOException
                fin.close();
            }
        }
    }

    /**
     * runs an input loop asking the user to select a color, white or black, by entering 'w' or 'b'
     */
    private void selectUserColor() {
        String choice = "";
        if (commandLineGame) {
            Scanner lineScanner;

            while (true) {
                System.out.println("Would you like to play as white or black? Enter 'w' or 'b' (case-insensitive) to choose");
                System.out.print(">");
                lineScanner = getLineScanner(consoleIn);
                if (lineScanner != null && lineScanner.hasNext()) {
                    choice = lineScanner.next();
                }
                if (choice.equalsIgnoreCase("w") || choice.equalsIgnoreCase("b")) {
                    choice = choice.toLowerCase();
                    break;
                } else if (lineScanner != null && !checkCommonCmds(choice, lineScanner)) {
                    System.out.println(invalidCommand);
                }
            }
            user = choice.equals("w") ? Player.WHITE : Player.BLACK;
        }
        bot = new ChessBot();
        if (commandLineGame) {
            transitionTo("pvp");
        }
    }

    public ProgramStateMachine(boolean GUIGame) {
        this.commandLineGame = false;
        initializeStates();
    }

    /**
     * constructs a new ProgramStateMachine to handle state transitions of the program
     */
    public ProgramStateMachine() {
        this(true, false);
    }

    /**
     * constructs a new ProgramStateMachine to handle state transitions of the program with
     * given parameters
     *
     * @param showHelp true iff help menu should be shown on every prompt
     * @param isDebug true iff we want more verbose debug messages
     */
    public ProgramStateMachine(boolean showHelp, boolean isDebug) {
        this.showHelp = showHelp;
        this.isDebug = isDebug;

        initializeStates();
    }

    /**
     * starts the game
     */
    public void start() {
        consoleIn = new Scanner(System.in);
        System.out.println(terminalSizeMsg);
        if (commandLineGame) {
            transitionTo("pvp");
        }
    }

    /////////////////////////////////////
    // Getter Methods used by BoardGUI //
    /////////////////////////////////////
    public ChessPosition getChessPosition() {
        return chessPosition;
    }

    public void move(Move m) {
        chessPosition = chessPosition.move(m);
        if (commandLineGame) {
            transitionTo("pvp");
        }
    }

    public void setUserColor(Player p) {
        user = p;
    }

    public ChessBot getBot() {
        return bot;
    }

    public Player getUser() {
        return user;
    }
}