package puzzles.tilt.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.tilt.TiltException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class TiltModel {

    /** Constant */
    // Message to display if the board was PREVIOUSLY solved.
    public static final String ALREADY_SOLVED = "Already solved!";
    // Message to display if the board was JUST solved.
    public static final String BOARD_SOLVED = "Board solved!";
    
    /** Fields */
    // The collection of observers of this model
    private final List<Observer<TiltModel, String>> observers = new LinkedList<>();
    // The current configuration
    private TiltConfig currentConfig;
    // The current file (for reset purposes)
    private String currentFile;
    // Whether the game is still going on
    private boolean gameOver;
    // Path from the current configuration to the solution (for hint purposes)
    private LinkedList<Configuration> currentSolutionPath;

    /** Constructor */
    public TiltModel() {

    }

    /**
     * Loads the file with the name filename.
     *
     * @param filename the name of a file to load, as a String.
     */
    public boolean loadFile(String filename) {
        // Try to make a file based on the filename
        File file = new File(filename);

        // Pass it to the file-based version of this method
        return loadFile(file);
    }

    /**
     * Loads the file pointed to by file.
     *
     * @param file the File to load.
     */
    public boolean loadFile(File file) {
        try {
            // Try to load in the file from the filename as the currentConfig
            this.currentConfig = TiltConfig.loadFile(file, false);

            // Save the current filename as the currentFile in case we need to reset
            this.currentFile = file.getAbsolutePath();

            // Reset the solution path -- no hints yet for this file
            this.currentSolutionPath = null;

            // If successful, alert observers that it was loaded.
            alertObservers("Loaded: " + file.getName());

            // The game is not over yet
            if (!currentConfig.isSolution()) {
                this.gameOver = false;
            }
            else {
                // However, if this board is already solved, then it is.
                this.gameOver = true;
                alertObservers(ALREADY_SOLVED);
            }

            // Return true to indicate successful loading
            return true;
        }
        catch (TiltException te) {
            // If not successful, alert observers of failure.
            alertObservers("Failed to load: " + file.getName());

            // Return false to indicate unsuccessful loading.
            return false;
        }
    }

    /**
     * Displays the board to standard output.
     */
    public void printBoard() {
        System.out.println(this.currentConfig);
    }

    /**
     * Gets the value at a given cell in the grid.
     */
    public char getGridCell(int row, int col) {
        return this.currentConfig.getGridCell(row, col);
    }

    /**
     * Resets the board by reloading the file pointed to by this.currentFile.
     */
    public void reset() {
        try {
            // Reset the board with the current file
            this.currentConfig = TiltConfig.loadFile(this.currentFile, false);

            // Reset the hint path
            this.currentSolutionPath = null;

            // The game is not over yet
            if (!currentConfig.isSolution()) {
                this.gameOver = false;
            }
            else {
                // However, if this board is already solved, then it is.
                this.gameOver = true;
                alertObservers(ALREADY_SOLVED);
            }

            // If successful, alert observers that the reset was successful.
            alertObservers("Reset puzzle!");
        }
        catch (TiltException te) {
            // If not successful, alert observers of failure.
            alertObservers("Failed to reset.");
        }

    }

    /**
     * Method to tilt the board. This method also deletes any path to the solution that is saved,
     * because after tilting, the path may no longer be valid.
     *
     * @param direction the direction to tilt the board.
     */
    public void tilt(String direction) {
        // If the game is over, you can't tilt.
        if (this.gameOver) {
            // Alert the observer that it is over. Board will not change.
            alertObservers(ALREADY_SOLVED);
            return;
        }

        // Save a space for the updated board
        TiltConfig updatedBoard = null;

        // Tilt the board depending on the input direction
        switch(direction) {
            case "N":
                updatedBoard = this.currentConfig.northTiltConfig();
                break;
            case "S":
                updatedBoard = this.currentConfig.southTiltConfig();
                break;
            case "E":
                updatedBoard = this.currentConfig.eastTiltConfig();
                break;
            case "W":
                updatedBoard = this.currentConfig.westTiltConfig();
                break;
            default:
                // Bad direction. Alert observers and don't change anything.
                alertObservers("Illegal tilt direction.");
                return;
        }

        // Check if the updated board is null
        if (updatedBoard == null) {
            // If so, alert observers and don't change anything
            alertObservers("Illegal move. A blue slider will fall through the hole!");
        }
        // Check if the updated board is a solution
        else if (updatedBoard.isSolution()) {
            // Erase the hint path because it is no longer valid.
            this.currentSolutionPath = null;

            // If so, alert observers and signal the game is over.
            this.currentConfig = updatedBoard;
            alertObservers(BOARD_SOLVED);
            this.gameOver = true;
        }
        // Otherwise...
        else {
            // Erase the hint path because it is no longer valid.
            this.currentSolutionPath = null;

            // Perform the tilt without a message.
            this.currentConfig = updatedBoard;
            alertObservers("");
        }
    }

    /**
     * Method to get a hint.
     * If the game is not over, this method gets a path to this board's solution and
     * automatically updates the board's current state to the state from the hint.
     * This method also saves the path to the solution for future reference,
     * in case the user asks for multiple hints in a row.
     */
    public void hint() {
        // If the game is over, you can't get a hint.
        if (this.gameOver) {
            alertObservers(ALREADY_SOLVED);
            return;
        }

        // Otherwise, if there is no hint path yet, generate one.
        if (this.currentSolutionPath == null) {
            // Set up a new Solver
            Solver solver = new Solver();

            // Solve the puzzle with currentConfig as the starting Configuration
            solver.solve(this.currentConfig);

            // Get the solution path and save it to currentSolutionPath
            this.currentSolutionPath = solver.getPath();
        }

        // Check to see if the path is still null after generating. If so...
        if (this.currentSolutionPath == null) {
            // There is no solution. Keep board the same and inform user.
            alertObservers("No solution!");
        }
        // Otherwise, Get the next step (the first config will always be the same as the current one)
        else if (this.currentSolutionPath.size() > 1) {
            // Update the board to the next step in the solution path
            this.currentConfig = (TiltConfig) this.currentSolutionPath.remove(1);

            // Inform user of update
            alertObservers("Next step!");

            // Check if the puzzle is solved with this hint
            if (this.currentConfig.isSolution()) {
                alertObservers(BOARD_SOLVED);
                this.currentSolutionPath = null;
                this.gameOver = true;
            }
        }
        // If the user solved the puzzle by generating hints...
        else {
            // Alert the observer and remember that the game is over.
            alertObservers(ALREADY_SOLVED);
            this.gameOver = true;
        }

    }

    /**
     * Gets the size of the board.
     *
     * @return an integer corresponding to the length of the board
     * (which is also equal to the width)
     */
    public int getBoardSize() {
        return this.currentConfig.getBoardSize();
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<TiltModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     *
     * @param data the data to send to the observers, as a String.
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }
}
