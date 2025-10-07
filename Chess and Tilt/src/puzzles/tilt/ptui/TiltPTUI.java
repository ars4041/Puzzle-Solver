package puzzles.tilt.ptui;

import puzzles.common.Observer;
import puzzles.tilt.TiltException;
import puzzles.tilt.model.TiltModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * TiltPTUI
 * This class contains the textual view and controller for the Tilt puzzle within
 * the Model-View-Controller design pattern.
 *
 * @author Ryan O'Malley
 * @github cro5058
 */
public class TiltPTUI implements Observer<TiltModel, String> {

    /** Constants */
    // Help message
    public static final String HELP =
    """
    h(int)              -- hint next move
    l(oad) filename     -- load new puzzle file
    t(ilt) {N|S|E|W}    -- tilt the board in the given direction
    q(uit)              -- quit the game
    r(eset)             -- reset the current game""";
    // Command line prompt
    public static final String PROMPT = "> ";

    /** Fields */
    // TiltModel
    private TiltModel model;

    /** Constructor */
    public TiltPTUI(String filename) {
        // Create the new TiltModel with the given filename
        this.model = new TiltModel();

        // Add self as an observer of the model
        this.model.addObserver(this);

        // Load the file into the model
        this.model.loadFile(filename);
    }

    /**
     * Runs the main game loop.
     * Displays the help message, then starts looping.
     * Updates the model according to user input.
     */
    public void gameLoop() {
        // Before entering the actual game loop, display the help message.
        System.out.println(HELP);

        // Get ready to read user input
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            // Game loop
            boolean run = true;
            while (run) {
                // Prompt the user for input
                System.out.print(PROMPT);

                // If the input is not null...
                String input;
                if ((input = br.readLine()) != null) {

                    // Split the input into tokens
                    String[] tokens = input.strip().split("\\s+");
                    int n = tokens.length;

                    // React to the user's input
                    switch (tokens[0]) {
                        // hint
                        case "h":
                        case "H":
                            this.model.hint();
                            break;

                        // load
                        case "l":
                        case "L":
                            // Load the file
                            if (n > 1) {
                                this.model.loadFile(tokens[1]);
                            } else {
                                System.out.println("Input a file name.");
                            }
                            break;

                        // tilt
                        case "t":
                        case "T":
                            if (n > 1) {
                                this.model.tilt(tokens[1]);
                            } else {
                                System.out.println("Input a tilt direction.");
                            }
                            break;

                        // quit
                        case "q":
                        case "Q":
                            // Break out of the loop
                            run = false;
                            break;

                        // reset
                        case "r":
                        case "R":
                            this.model.reset();
                            break;

                        // for invalid commands, display the help menu
                        default:
                            System.out.println(HELP);
                    }
                }
                // If the user gave null input, quit.
                else {
                    run = false;
                }
            }
        }
        // If there was a problem setting up the BufferedReader...
        catch (IOException ioe) {
            // Indicate problem
            throw new TiltException("Error setting up the game loop.");
        }
    }

    /**
     * Updates the view within TiltPTUI.
     *
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param message optional data the model can send to the observer
     *
     */
    @Override
    public void update(TiltModel model, String message) {
        // Display the relevant messages
        System.out.println(message);

        // Display the board to standard output
        this.model.printBoard();
    }

    /**
     * The main method.
     */
    public static void main(String[] args) {
        // Check that the arguments are the correct length
        if (args.length != 1) {
            System.out.println("Usage: java TiltPTUI filename");
            return;
        }

        // If so, save the filename
        String filename = args[0];

        // Check if the file pointed to by filename exists
        File file = new File(filename);

        // If so...
        if (file.exists()) {
            // Create new TiltPTUI object and load in the file
            TiltPTUI ptui = new TiltPTUI(filename);

            // Start the game loop
            ptui.gameLoop();
        }
        // Otherwise...
        else {
            // Display an error message and quit
            System.out.println("Failed to load file " + filename);
            System.exit(1);
        }
    }
}
