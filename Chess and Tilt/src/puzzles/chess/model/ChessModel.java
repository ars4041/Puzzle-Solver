package puzzles.chess.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * The model for a solitaire chess game.
 *
 * @author Aidan Sanderson
 */
public class ChessModel {

    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();

    /** The file that the current config was read from. */
    private File file;

    /** the current configuration */
    private ChessConfig currentConfig;

    /**
     * Has the first selection been made for capturing?
     */
    private boolean firstSelectionMade;

    /**
     * Constructor for a new chessModel that creates its current configuration from a given file.
     *
     * @param file The file to read from for creating the chess config.
     * @throws IOException If the file is not readable or found.
     */
    public ChessModel(File file) throws IOException {
            this.file = file;
            this.currentConfig = new ChessConfig(file);
            this.firstSelectionMade = false;
    }

    /**
     * Gets the current chess configuration
     *
     * @return The current chess configuration.
     */
    public ChessConfig getConfig() {
        return this.currentConfig;
    }

    /**
     * If there is a reachable solution from the current config, find the next move, updates the config, and alerts the observers
     * that the hint was a success, otherwise alerts the observers that there is no solution.
     */
    public void hint() {
        Solver solver = new Solver();
        solver.solve(this.currentConfig);
        LinkedList<Configuration> path = solver.getPath();
        if (path == null) {
            alertObservers("No solution...");
        }
        else if (path.size() > 1) {
            Configuration newConfig = path.get(1);
            this.currentConfig = (ChessConfig) newConfig;
            alertObservers("Hint was a success!");
        }
        else if (path.size() == 1) {
            alertObservers("Solution found already!");
        }
        else {
            alertObservers("No solution...");
        }
    }

    /**
     * Loads in a new chess configuration for the models current config from a given file if it is readable and alerts
     * the observers that the load was successful, otherwise alerts the observers that the load was not successful.
     *
     * @param fileName The file to read from for creating the new config for the model.
     */
    public void load(String fileName) {
        File newFile = new File(fileName);
        if (!newFile.exists() || !newFile.canRead()) {
            alertObservers("Can't read file: " + fileName);
        }
        else {
            try {
                this.currentConfig = new ChessConfig(newFile);
                this.file = newFile;
                alertObservers("Loaded: " + newFile.getName());
            }
            catch (IOException e) {
                try {
                    this.currentConfig = new ChessConfig(this.file);
                    alertObservers("Can't read file: " + newFile.getName());
                }
                catch (IOException e2) {
                    System.out.println("Can't read file: " + newFile.getName());
                    System.exit(1);
                }
            }
        }
    }

    /**
     * Is the current selection spot valid? For it to be valid there must be a piece there.
     * If it is not valid, alerts the observers that it is invalid.
     *
     * @param row The row to look at.
     * @param col The column to look at.
     * @return true if valid, false otherwise.
     */
    public boolean isValidSelection(int row, int col){
        String selection = this.currentConfig.getGrid()[row][col];
        if (selection.equals("K") || selection.equals("Q") || selection.equals("R") || selection.equals("B") || selection.equals("N") || selection.equals("P")) {
            if (!this.firstSelectionMade) {
                firstSelectionMade = true;
                alertObservers("Selected (" + row + ", " + col + ")");
            }
            else {
                this.firstSelectionMade = false;
            }
            return true;
        }
        this.firstSelectionMade = false;
        alertObservers("Invalid selection!");;
        return false;
    }

    /**
     * Sets the first selection as been made already or not.
     *
     * @param value true or false for if the first selection has been made.
     */
    public void setFirstSelection(boolean value) {
        this.firstSelectionMade = value;
    }

    /**
     * Attempts to move a piece from a spot to another spot that also has a piece there. If that move is possible,
     * sets the models configuration to a new configuration after the move, and alerts the observers that the selection was a success,
     * otherwise alerts the observers that the selection was invalid.
     *
     * @param fromRow The row of the piece to move from.
     * @param fromCol The column of the piece to move from.
     * @param toRow The row of the piece to move to.
     * @param toCol The column of the piece to move to.
     */
    public void select(int fromRow, int fromCol, int toRow, int toCol) {
        List<Configuration> possibleConfigs = this.currentConfig.scan(fromRow, fromCol);
        ChessConfig newConfig = new ChessConfig(this.currentConfig, fromRow, fromCol, toRow, toCol);
        if (possibleConfigs.contains(newConfig)) {
            boolean check = true;
            ChessConfig compareConfig;
            for (Configuration config : possibleConfigs) {
                if (config.equals(newConfig)) {
                    compareConfig = (ChessConfig) config;
                    if (newConfig.strictEquals(compareConfig)) {
                        check = false;
                        this.currentConfig = newConfig;
                        alertObservers("Captured from (" + fromRow + "," + fromCol + ") to ("+ toRow + "," + toCol + ")");
                    }
                }
            }
            if (check) {
                alertObservers("Illegal move!");
            }
        }
        else {
            alertObservers("Illegal move!");
        }
    }

    /**
     * Ends the program.
     */
    public void quit() {
        System.exit(0);
    }

    /**
     * Resets the models current configuration to the initial state of the config.
     */
    public void reset() {
        try {
            this.currentConfig = new ChessConfig(this.file);
            alertObservers("Reset successful!");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }
}
