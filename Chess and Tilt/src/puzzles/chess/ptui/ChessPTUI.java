package puzzles.chess.ptui;

import puzzles.chess.model.ChessModel;
import puzzles.common.Observer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * A PTUI for a game of solitaire chess.
 *
 * @author Aidan Sanderson
 */
public class ChessPTUI implements Observer<ChessModel, String> {

    /** The model for the solitaire chess game. */
    private ChessModel model;

    /** The name of the initial file used to create the initial chess config for the model. */
    private String fileName;

    /** has the from selection already been made? */
    private boolean fromSelection;

    /** The row of the spot to move from. */
    private int fromRow;

    /** The column of the spot to move from. */
    private int fromCol;

    /** The row of the spot to move to. */
    private int toRow;

    /** The column of the spot to move to. */
    private int toCol;

    /** Is there a current selection process on going? */
    private boolean isSelection;

    /**
     * Constructor for a PTUI of solitaire chess.
     *
     * @param file The file used to create the initial chess config for the model.
     * @throws IOException An exception if the file is not readable or found.
     */
    public ChessPTUI(File file) throws IOException {
       this.model = new ChessModel(file);
       this.model.addObserver(this);
       this.fileName = file.getName();
       this.fromSelection = false;
       this.isSelection = false;
    }

    /**
     * Prints the models current config and a menu of option for the user to select from.
     */
    public void printHelp() {
        System.out.print(this.model.getConfig());
        System.out.println("(h)int -- hint next move");
        System.out.println("(l)oad -- load new puzzle file");
        System.out.println("(s)elect -- select cell at r, c");
        System.out.println("(q)uit -- quit the game");
        System.out.println("(r)eset -- reset the current game");
    }

    /**
     * The process of selecting a spot to move a piece from, and a spot to move it to. The row and column numbers are for the spots,
     * and if they are valid and both have been selected the model will make the move and the PTUI indicates success, otherwise the PTUI indicates failure.
     */
    public void selectionProcess(int row, int col) {
        boolean isValid = this.model.isValidSelection(row, col);
        if (isValid && !this.fromSelection) {
            this.fromSelection = true;
            this.fromRow = row;
            this.fromCol = col;
        }
        else if (isValid && this.fromSelection) {
            this.toRow = row;
            this.toCol = col;
            this.fromSelection = false;
            this.model.select(this.fromRow,this.fromCol,this.toRow,this.toCol);
        }
        else {
            this.fromSelection = false;
        }
    }

    /**
     * Loops the game of solitaire chess. Every loop has the user select and action, and then the model is called to compute
     * whatever action is selected, and finally the PTUI updates for that action and prints the new model config.
     */
    public void game()  {
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean gameCheck = true;
        boolean inputCheck = true;
        System.out.print("Loaded: " + this.fileName);
        printHelp();
        System.out.print("Selection: ");
        while (gameCheck) {
            while (inputCheck) {
                input = scanner.nextLine();
                switch (input.substring(0,1)) {
                    case "h":
                        inputCheck = false;
                        this.isSelection = false;
                        if (input.equals("hint") || input.equals("h")) {
                            this.model.setFirstSelection(false);
                            this.model.hint();
                            break;
                        }
                    case "l":
                        inputCheck = false;
                        this.isSelection = false;
                        if (input.startsWith("load") || input.startsWith("l ")) {
                            this.model.setFirstSelection(false);
                            if (input.startsWith("load")) {
                                try {
                                    String newFileName = input.substring(5);
                                    this.model.load(newFileName);
                                } catch (StringIndexOutOfBoundsException e) {
                                    update(this.model, "Invalid selection, try again...");
                                }
                            }
                            else {
                                try {
                                    String newFileName = input.substring(2);
                                    this.model.load(newFileName);
                                } catch (StringIndexOutOfBoundsException e) {
                                    update(this.model, "Invalid selection, try again...");
                                }
                            }
                            break;
                        }
                    case "s":
                        inputCheck = false;
                        if (input.startsWith("select") || input.startsWith("s ")) {
                            if (!isSelection) {
                                this.fromSelection = false;
                                this.model.setFirstSelection(false);
                            }
                            this.isSelection = true;
                            try {
                                int row = Integer.parseInt(input.substring(2, 3));
                                int col = Integer.parseInt(input.substring(4, 5));
                                selectionProcess(row, col);
                            } catch (Exception e) {
                                update(this.model, "Invalid selection!");
                            }
                            break;
                        }
                    case "q":
                        if (input.equals("quit") || input.equals("q")) {
                            inputCheck = false;
                            this.model.quit();
                            break;
                        }
                    case "r":
                        inputCheck = false;
                        this.isSelection = false;
                        this.model.setFirstSelection(false);
                        if (input.equals("reset") || input.equals("r")) {
                            this.model.reset();
                            break;
                        }
                    default:
                        this.isSelection = false;
                        this.model.setFirstSelection(false);
                        System.out.println();
                        System.out.print("Invalid selection, try again...");
                        printHelp();
                        System.out.print("Selection: ");
                        break;
                }
            }
            inputCheck = true;
        }
    }

    /**
     * Prints a message given from the model, and prints the current config of the updates model.
     *
     * @param model the object that wishes to inform this object about something that has happened.
     * @param message optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel model, String message) {
            System.out.println();
            System.out.print(message);
            System.out.print(this.model.getConfig());
            System.out.print("Selection: ");
    }

    /**
     * runs an instance of the PTUI and initializes from a given file name in the command line if that argument is valid,
     * otherwise prints that the file can't be read and ends the program.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ChessPTUI filename");
        }
        else {
            try {
                File file = new File(args[0]);
                ChessPTUI ptui = new ChessPTUI(file);
                ptui.game();
            }
            catch (IOException e) {
                System.out.println("Can't read file: " + args[0]);
                System.exit(1);
            }
        }
    }
}
