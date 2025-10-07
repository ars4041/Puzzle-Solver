package puzzles.tilt.model;

import puzzles.common.solver.Configuration;
import puzzles.tilt.TiltException;
import puzzles.tilt.TiltTiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import static puzzles.tilt.solver.Tilt.INVALID_FILE_MESSAGE;

/**
 * TiltConfig class - Represents a given board within a Tilt puzzle.
 *
 * @author Ryan O'Malley
 * @github cro5058
 */
public class TiltConfig implements Configuration, TiltTiles {

    /** Static */
    // Dimension of the board (board is always a square)
    private static int N;
    // Row of the exit hole
    private static int exitRow;
    // Column of the exit hole
    private static int exitCol;

    /** Fields */
    // Grid including the blockers, sliders, empty spaces, and exit hole
    private char[][] grid;
    // Number of green sliders remaining on the board
    private int numGreen;


    /** Constructor */
    public TiltConfig(char[][] grid, int numGreen) {
        // Do not set exitRow or exitCol here because they are static

        // Set the grid equal to the grid passed in
        this.grid = grid;

        // Set the number of green sliders equal to the amount passed in
        this.numGreen = numGreen;
    }

    /** Copy constructor */
    public TiltConfig(TiltConfig other) {
        // Copy over the grid
        this.grid = new char[N][N];
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                this.grid[row][col] = other.getGridCell(row, col);
            }
        }
        
        // Copy over the number of greens remaining
        this.numGreen = other.getNumGreen();
    }

    /**
     * Create a TiltConfig from a file.
     *
     * @param filename the name of the file containing the TiltConfig to load in.
     * @param print whether to print out the board line by line if it is loaded in.
     * @return the newly created TiltConfig.
     */
    public static TiltConfig loadFile(String filename, boolean print) throws TiltException {
        // Try to make a file based on the filename
        File file = new File(filename);

        // Pass it to the file-based version of this method
        return loadFile(file, print);
    }

    /**
     * Create a TiltConfig from a file.
     *
     * @param file the file containing the TiltConfig to load in.
     * @param print whether to print out the board line by line if it is loaded in.
     * @return the newly created TiltConfig.
     */
    public static TiltConfig loadFile(File file, boolean print) throws TiltException {
        // Open file, read in the board, and print it out line by line
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Read first line of the file (dimension of board) and convert to int
            TiltConfig.N = Integer.parseInt(br.readLine());

            // Read the next lines of the file to save as the grid
            char[][] grid = new char[N][N];

            // Keep track of the number of green sliders
            int numGreen = 0;

            // For every row in the board (line in the file)...
            for (int row = 0; row < N; row++) {
                // Read in the line
                String thisRow = br.readLine();

                // Print out the line if desired
                if (print) {
                    System.out.println(thisRow);
                }

                // Split the line on spaces to get individual tiles
                String[] tiles = thisRow.strip().split("\\s+");

                // Save tiles into the grid + identify exit location
                for (int col = 0; col < N; col++) {
                    char thisTile = tiles[col].charAt(0);

                    // If this tile is the exit hole...
                    if (thisTile == EXIT) {
                        // Save its coordinates
                        TiltConfig.exitRow = row;
                        TiltConfig.exitCol = col;
                    }
                    // If this tile is a green slider...
                    else if (thisTile == GREEN) {
                        numGreen++;
                    }

                    // Save the tile into the grid
                    grid[row][col] = thisTile;
                }
            }

            // Return the newly created TiltConfig
            return new TiltConfig(grid, numGreen);
        }
        catch (Exception e) {
            throw new TiltException(INVALID_FILE_MESSAGE);
        }
    }

    /** Accessor for the number of green sliders on the board */
    public int getNumGreen() {
        return this.numGreen;
    }

    /**
     * Get a cell/tile of the grid
     *
     * @return the char that is at the board at position (row, col)
     */
    public char getGridCell(int row, int col) {
        return this.grid[row][col];
    }

    /**
     * Set a cell/tile of the grid.
     *
     * @param row the row of the cell to change
     * @param col the column of the cell to change
     * @param tile the char to put at that (row, col) position
     */
    public void setGridCell(int row, int col, char tile) {
        this.grid[row][col] = tile;
    }

    /** Set the number of greens */
    public void setNumGreen(int numGreen) {
        this.numGreen = numGreen;
    }

    /** Accessor for board size */
    public int getBoardSize() {
        return N;
    }

    /**
     * Tests whether this TiltConfig is a solution to the puzzle.
     * This TiltConfig is a solution if there are no green sliders remaining on the board.
     *
     * @return true if this puzzle is a solution, false otherwise.
     */
    @Override
    public boolean isSolution() {
        // A configuration is a solution if there are no green sliders on the board
        return this.numGreen == 0;
    }

    /**
     * Simulates tilting the board north, if this is a legal move.
     *
     * @return the TiltConfig to the north of this one, else null if the config is illegal.
     */
    public TiltConfig northTiltConfig() {
        // Make a copy of the current configuration
        TiltConfig northNeighbor = new TiltConfig(this);

        // Fast legality check
        for (int row = exitRow + 1; row < N; row++) {
            // If a blocker is protecting the exit hole...
            if (northNeighbor.getGridCell(row, exitCol) == BLOCKER) {
                // This move will be safe
                break;
            }
            // If we see a blue slider without a blocker covering it...
            if (northNeighbor.getGridCell(row, exitCol) == BLUE) {
                // This move is illegal, stop immediately.
                return null;
            }
        }

        // For every column in the grid...
        for (int col = 0; col < N; col++) {
            // For every row in the column...
            for (int row = 0; row < N; row++) {
                // If the current tile is a slider
                if (northNeighbor.getGridCell(row, col) == BLUE ||
                        northNeighbor.getGridCell(row, col) == GREEN) {
                    // Save the original color of the slider
                    char sliderType = northNeighbor.getGridCell(row, col);

                    // Remember the topmost row the slider can go to
                    int topBlankRow = -1;

                    // For every row above this row in the column...
                    for (int upperRow = row - 1; upperRow >= 0; upperRow--) {
                        // If the cell is a blank...
                        if (northNeighbor.getGridCell(upperRow, col) == BLANK) {
                            // Remember it as a space this slider can go to
                            topBlankRow = upperRow;
                        }
                        // Else if the cell is the exit hole...
                        else if (sliderType == GREEN && northNeighbor.getGridCell(upperRow, col) == EXIT) {
                            // Save it as the top blank row
                            topBlankRow = upperRow;

                            // We have found the topmost row the slider can go to. We are done looping.
                            break;
                        }
                        // Otherwise, the row above is a blocker.
                        else {
                            // Nothing else to do in this loop.
                            break;
                        }
                    }

                    // Edit the board as needed for this slider
                    // If the slider was able to move...
                    if (topBlankRow > -1) {
                        // If a green slider was able to move to the exit row...
                        if (sliderType == GREEN && northNeighbor.getGridCell(topBlankRow, col) == EXIT) {
                            // Set the slider's original position to a blank in the copy grid
                            northNeighbor.setGridCell(row, col, BLANK);

                            // Subtract 1 from the north neighbor's number of greens
                            northNeighbor.setNumGreen(northNeighbor.getNumGreen() - 1);
                        }
                        else {
                            // Set the top blank row equal to the type of the slider that moved in the copy grid
                            northNeighbor.setGridCell(topBlankRow, col, sliderType);

                            // Set the slider's original position to a blank in the copy grid
                            northNeighbor.setGridCell(row, col, BLANK);
                        }
                    }
                }
            }
        }
        // Finally, return the successfully created north tilt configuration.
        return northNeighbor;
    }

    /**
     * Simulates tilting the board south, if this is a legal move.
     *
     * @return the TiltConfig to the south of this one, else null if the config is illegal.
     */
    public TiltConfig southTiltConfig() {
        // Make a copy of this configuration
        TiltConfig southNeighbor = new TiltConfig(this);

        // Fast legality check
        for (int row = exitRow - 1; row >= 0; row--) {
            // If a blocker is protecting the exit hole...
            if (southNeighbor.getGridCell(row, exitCol) == BLOCKER) {
                // This move will be safe
                break;
            }
            // If we see a blue slider without a blocker covering it...
            if (southNeighbor.getGridCell(row, exitCol) == BLUE) {
                // This move is illegal, stop immediately.
                return null;
            }
        }

        // If the move was legal, do the rest of the columns.
        // For every column in the grid...
        for (int col = 0; col < N; col++) {
            // For every row in the column...
            for (int row = N - 1; row >= 0; row--) {
                // If the current tile is a slider
                if (southNeighbor.getGridCell(row, col) == BLUE ||
                        southNeighbor.getGridCell(row, col) == GREEN) {

                    // Save the original color of the slider
                    char sliderType = southNeighbor.getGridCell(row, col);

                    // Remember the bottommost row the slider can go to
                    int bottomBlankRow = -1;
                    // For every row below this row in the column...
                    for (int lowerRow = row + 1; lowerRow < N; lowerRow++) {
                        // If the cell is a blank...
                        if (southNeighbor.getGridCell(lowerRow, col) == BLANK) {
                            // Remember it as a space this slider can go to
                            bottomBlankRow = lowerRow;
                        }
                        // Else if the cell is the exit hole...
                        else if (sliderType == GREEN && southNeighbor.getGridCell(lowerRow, col) == EXIT) {
                            // Save it as the top blank row
                            bottomBlankRow = lowerRow;

                            // We have found the topmost row the slider can go to. We are done looping.
                            break;
                        }
                        // Otherwise, the row above is a blocker.
                        else {
                            // Nothing else to do in this loop.
                            break;
                        }
                    }

                    // Edit the board as needed for this slider
                    // If the slider was able to move...
                    if (bottomBlankRow > -1) {
                        // If a green slider was able to move to the exit row...
                        if (sliderType == GREEN && southNeighbor.getGridCell(bottomBlankRow, col) == EXIT) {
                            // Set the slider's original position to a blank in the copy grid
                            southNeighbor.setGridCell(row, col, BLANK);

                            // Subtract 1 from the south neighbor's number of greens
                            southNeighbor.setNumGreen(southNeighbor.getNumGreen() - 1);
                        }
                        else {
                            // Set the top blank row equal to the type of the slider that moved in the copy grid
                            southNeighbor.setGridCell(bottomBlankRow, col, sliderType);

                            // Set the slider's original position to a blank in the copy grid
                            southNeighbor.setGridCell(row, col, BLANK);
                        }
                    }
                }
            }
        }

        // Finally, return the successfully created south tilt configuration.
        return southNeighbor;
    }

    /**
     * Simulates tilting the board east, if this is a legal move.
     *
     * @return the TiltConfig to the east of this one, else null if the config is illegal.
     */
    public TiltConfig eastTiltConfig() {
        // Make a copy of this configuration
        TiltConfig eastNeighbor = new TiltConfig(this);

        // Fast legality check
        for (int col = exitCol - 1; col >= 0; col--) {
            // If a blocker is protecting the exit hole...
            if (eastNeighbor.getGridCell(exitRow, col) == BLOCKER) {
                // This move will be safe
                break;
            }
            // If we see a blue slider without a blocker covering it...
            if (eastNeighbor.getGridCell(exitRow, col) == BLUE) {
                // This move is illegal, stop immediately.
                return null;
            }
        }

        // For every row in the grid...
        for (int row = 0; row < N; row++) {
            // For every column in the row (right to left)...
            for (int col = N - 1; col >= 0; col--) {
                // If the current tile is a slider
                if (eastNeighbor.getGridCell(row, col) == BLUE ||
                        eastNeighbor.getGridCell(row, col) == GREEN) {

                    // Save the original color of the slider
                    char sliderType = eastNeighbor.getGridCell(row, col);

                    // Remember the rightmost column the slider can go to
                    int rightmostCol = -1;

                    // For every column to the right of this one in the grid...
                    for (int rightCol = col + 1; rightCol < N; rightCol++) {
                        // If the cell is a blank...
                        if (eastNeighbor.getGridCell(row, rightCol) == BLANK) {
                            // Remember it as a space this slider can go to
                            rightmostCol = rightCol;
                        }
                        // Else if the cell is the exit hole...
                        else if (sliderType == GREEN && eastNeighbor.getGridCell(row, rightCol) == EXIT) {
                            // Save it as the rightmost row this slider can go to
                            rightmostCol = rightCol;

                            // We have found the rightmost column the slider can go to. We are done looping.
                            break;
                        }
                        // Else, the cell is a blocker.
                        else {
                            break;
                        }
                    }

                    // Edit the board as needed for this slider
                    // If the slider was able to move...
                    if (rightmostCol > -1) {
                        // If a green slider was able to move to the exit...
                        if (sliderType == GREEN && eastNeighbor.getGridCell(row, rightmostCol) == EXIT){
                            // Set the slider's original position to a blank in the copy grid
                            eastNeighbor.setGridCell(row, col, BLANK);

                            // Subtract 1 from the east neighbor's number of greens
                            eastNeighbor.setNumGreen(eastNeighbor.getNumGreen() - 1);
                        }
                        else {
                            // Set the rightmost column equal to the type of the slider that moved in the copy grid
                            eastNeighbor.setGridCell(row, rightmostCol, sliderType);

                            // Set the slider's original position to a blank in the copy grid
                            eastNeighbor.setGridCell(row, col, BLANK);
                        }
                    }
                }
            }
        }

        // Finally, return the successfully created east tilt configuration.
        return eastNeighbor;
    }

    /**
     * Simulates tilting the board west, if this is a legal move.
     *
     * @return the TiltConfig to the west of this one, else null if the config is illegal.
     */
    public TiltConfig westTiltConfig() {
        // Make a copy of this configuration
        TiltConfig westNeighbor = new TiltConfig(this);

        // Fast legality check
        for (int col = exitCol + 1; col < N; col++) {
            // If a blocker is protecting the exit hole...
            if (westNeighbor.getGridCell(exitRow, col) == BLOCKER) {
                // This move will be safe
                break;
            }
            // If we see a blue slider without a blocker covering it...
            if (westNeighbor.getGridCell(exitRow, col) == BLUE) {
                // This move is illegal, stop immediately.
                return null;
            }
        }

        // For every row in the grid...
        for (int row = 0; row < N; row++) {
            // For every column in the row (right to left)...
            for (int col = 0; col < N; col++) {
                // If the current tile is a slider
                if (westNeighbor.getGridCell(row, col) == BLUE ||
                        westNeighbor.getGridCell(row, col) == GREEN) {
                    // Save the original color of the slider
                    char sliderType = westNeighbor.getGridCell(row, col);

                    // Remember the leftmost column the slider can go to
                    int leftmostCol = -1;

                    // For every column to the left of this one in the grid...
                    for (int leftCol = col - 1; leftCol >= 0; leftCol--) {
                        // If the cell is a blank...
                        if (westNeighbor.getGridCell(row, leftCol) == BLANK) {
                            // Remember it as a space this slider can go to
                            leftmostCol = leftCol;
                        }
                        // Else if the cell is the exit hole...
                        else if (sliderType == GREEN && westNeighbor.getGridCell(row, leftCol) == EXIT) {
                            // Save it as the leftmost column this slider can go to
                            leftmostCol = leftCol;

                            // We have found the leftmost column the slider can go to. We are done looping.
                            break;
                        }
                        // Else, the cell is a blocker.
                        else {
                            break;
                        }
                    }

                    // Edit the board as needed for this slider
                    // If the slider was able to move...
                    if (leftmostCol > -1) {
                        if (sliderType == GREEN && westNeighbor.getGridCell(row, leftmostCol) == EXIT) {
                            // Set the slider's original position to a blank in the copy grid
                            westNeighbor.setGridCell(row, col, BLANK);

                            // Subtract 1 from the west neighbor's number of greens
                            westNeighbor.setNumGreen(westNeighbor.getNumGreen() - 1);
                        }
                        else {
                            // Set the leftmost column equal to the type of the slider that moved in the copy grid
                            westNeighbor.setGridCell(row, leftmostCol, sliderType);

                            // Set the slider's original position to a blank in the copy grid
                            westNeighbor.setGridCell(row, col, BLANK);
                        }
                    }
                }
            }
        }

        // Finally, return the successfully created west tilt configuration.
        return westNeighbor;
    }

    /**
     * Finds the valid neighboring configurations of this TiltConfig:
     * The board when it is tilted north, east, south, and west.
     *
     * @return a HashSet containing the valid neighboring configurations.
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        // Set up the empty HashSet of neighboring configurations
        LinkedHashSet<Configuration> neighbors = new LinkedHashSet<>();

        // Add all neighbors to the result that are not null
        TiltConfig n, s, e, w;
        if ((n = northTiltConfig()) != null && !n.equals(this)) {
            neighbors.add(n);
        }
        if ((s = southTiltConfig()) != null && !s.equals(this)) {
            neighbors.add(s);
        }
        if ((e = eastTiltConfig()) != null && !e.equals(this)) {
            neighbors.add(e);
        }
        if ((w = westTiltConfig()) != null && !w.equals(this)) {
            neighbors.add(w);
        }

        // Return the HashSet of neighbors that are not null
        return neighbors;
    }

    /**
     * Tests whether an Object is a TiltConfig that is equal to this one.
     *
     * @param other an Object to test to see if it is an equal TiltConfig to this one.
     * @return true if the TiltConfig is equal to this one, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        // Save the result of the comparison
        boolean result = false;

        // Compare this TiltConfig's grid with the other's grid
        if (other instanceof TiltConfig otherConfig) {
            result = Arrays.deepEquals(this.grid, otherConfig.grid);
        }

        // Return the result of the comparison
        return result;
    }

    /**
     * Returns a hashcode for this TiltConfig.
     * Hashes on the grid of this TiltConfig.
     *
     * @return the integer hashcode for this TiltConfig.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.grid);
    }

    /**
     * Returns a String representation of this TiltConfig.
     * The String representation consists of the current state of the grid.
     *
     * @return the String representation of this TiltConfig.
     */
    @Override
    public String toString() {
        // Accumulator for String representation
        String result = "";

        // For each row in the grid...
        for (int row = 0; row < N; row++) {
            // For each tile in the row...
            for (int col = 0; col < N; col++) {
                // Add the tile to the result
                result += grid[row][col];

                // If it is not the last tile in the row...
                if (col < N - 1) {
                    // Add a space
                    result += " ";
                }
            }
            // Move on to the next line
            result += "\n";
        }

        // Return accumulated result
        return result;
    }
}
