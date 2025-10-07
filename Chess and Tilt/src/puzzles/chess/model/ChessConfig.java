package puzzles.chess.model;

import puzzles.common.solver.Configuration;

import java.io.*;
import java.util.*;

/**
 * Configuration for a solitaire chess puzzle
 *
 * @author Aidan Sanderson
 */
public class ChessConfig implements Configuration {

    /** The grid representing the chess board and the pieces on it */
    private String[][] grid;

    /** Number of rows */
    private static  int ROWS;

    /** Number of columns */
    private static  int COLS;

    /** total number of configurations generated */
    private static List<Configuration> totalConfigs;

    /** unique number of configurations generated */
    private static Set<Configuration> uniqueConfigs;

    /** configs that can be created from the current config */
    private final Collection<Configuration> neighbors;

    /**
     * Initial constructor for the first config using a file to read in the chess board form.
     *
     * @param file The file to read the chess board from.
     * @throws IOException File given can not be read from.
     */
    public ChessConfig(File file) throws IOException {
        int lineNum = 0;
        int row = 0;
        this.grid = null;
        this.neighbors = new ArrayList<>();
        totalConfigs = new ArrayList<>();
        uniqueConfigs = new HashSet<>();
        ROWS = 0;
        COLS = 0;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (lineNum == 0) {
                        String[] dimesntions = line.split(" ");
                        ROWS = Integer.parseInt(dimesntions[0]);
                        COLS = Integer.parseInt(dimesntions[1]);
                        this.grid = new String[ROWS][COLS];
                        lineNum++;
                    } else {
                        String[] parts = line.split(" ");
                        for (int col = 0; col < COLS; col++) {
                            this.grid[row][col] = parts[col];
                        }
                        row++;
                    }
                }
            } catch (IOException e) {
                throw new IOException();
            }
            totalConfigs.add(this);
            uniqueConfigs.add(this);
    }

    /**
     * Constructor to create neighbor configs for an already created config.
     *
     * @param config The config to make neighbors for.
     * @param rowFrom row for piece to move from.
     * @param colFrom column for piece to move from.
     * @param rowTo row for piece to move to.
     * @param colTo column for piece to move to.
     */
    public ChessConfig(ChessConfig config, int rowFrom, int colFrom, int rowTo, int colTo) {
        this.grid = new String[ROWS][COLS];
        String[][] configGrid = config.getGrid();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                this.grid[row][col] = configGrid[row][col];
            }
        }
        String piece = this.grid[rowFrom][colFrom];
        this.grid[rowFrom][colFrom] = ".";
        this.grid[rowTo][colTo] = piece;
        this.neighbors = new ArrayList<>();
        totalConfigs.add(this);
        uniqueConfigs.add(this);
    }

    /**
     * Scans for all valid pawn moves at a certain location. If a move is valid, creates a new config and adds it to a list.
     *
     * @param row row to scan from.
     * @param col column to scan from.
     * @return The list of all new configurations from a valid move.
     */
    public List<Configuration> pawnScan(int row, int col) {
        List<Configuration> configs = new ArrayList<>();
        boolean checkLeftIndex = true;
        boolean checkRightIndex = true;
        boolean checkUpIndex = true;
        if (col <= 0) {
            checkLeftIndex = false;
        }
        if (col >= COLS - 1) {
            checkRightIndex = false;
        }
        if (row <= 0) {
            checkUpIndex = false;
        }
        String to;
        if (checkLeftIndex && checkUpIndex) {
            to = this.grid[row - 1][col - 1];
            if (!to.equals(".")) {
                configs.add(new ChessConfig(this, row, col, row - 1, col - 1));
            }
        }
        if (checkRightIndex && checkUpIndex) {
            to = this.grid[row - 1][col + 1];
            if (!to.equals(".")) {
                configs.add(new ChessConfig(this, row, col, row - 1, col + 1));
            }
        }
        return configs;
    }

    /**
     * Scans for all valid rook moves at a certain location. If a move is valid, creates a new config and adds it to a list.
     * Also used for queen moves.
     *
     * @param row row to scan from.
     * @param col column to scan from.
     * @return The list of all new configurations from a valid move.
     */
    public List<Configuration> rookScan(int row, int col) {
        List<Configuration> configs = new ArrayList<>();
        int curRow = row;
        int curCol = col;
        while (curRow > 0) {
            curRow--;
            if (!this.grid[curRow][curCol].equals(".")) {
                configs.add(new ChessConfig(this, row, col, curRow, curCol));
                break;
            }
        }
        curRow = row;
        while (curRow < ROWS - 1) {
            curRow++;
            if (!this.grid[curRow][curCol].equals(".")) {
                configs.add(new ChessConfig(this, row, col, curRow, curCol));
                break;
            }
        }
        curRow = row;
        while (curCol > 0) {
            curCol--;
            if (!this.grid[curRow][curCol].equals(".")) {
                configs.add(new ChessConfig(this, row, col, curRow, curCol));
                break;
            }
        }
        curCol = col;
        while (curCol < COLS - 1) {
            curCol++;
            if (!this.grid[curRow][curCol].equals(".")) {
                configs.add(new ChessConfig(this, row, col, curRow, curCol));
                break;
            }
        }
        return configs;
    }

    /**
     * Scans for all valid bishop moves at a certain location. If a move is valid, creates a new config and adds it to a list.
     * Also used for queen moves.
     *
     * @param row row to scan from.
     * @param col column to scan from.
     * @return The list of all new configurations from a valid move.
     */
    public List<Configuration> bishopScan(int row, int col) {
        List<Configuration> configs = new ArrayList<>();
        int curRow = row;
        int curCol = col;
        while (curRow > 0 && curCol > 0) {
            curRow--;
            curCol--;
            if (!this.grid[curRow][curCol].equals(".")) {
                configs.add(new ChessConfig(this, row, col, curRow, curCol));
                break;
            }
        }
        curRow = row;
        curCol = col;
        while (curRow > 0 && curCol < COLS - 1) {
            curRow--;
            curCol++;
            if (!this.grid[curRow][curCol].equals(".")) {
                configs.add(new ChessConfig(this, row, col, curRow, curCol));
                break;
            }
        }
        curRow = row;
        curCol = col;
        while (curRow < ROWS - 1 && curCol > 0) {
            curRow++;
            curCol--;
            if (!this.grid[curRow][curCol].equals(".")) {
                configs.add(new ChessConfig(this, row, col, curRow, curCol));
                break;
            }
        }
        curRow = row;
        curCol = col;
        while (curRow < ROWS - 1 && curCol < COLS - 1) {
            curRow++;
            curCol++;
            if (!this.grid[curRow][curCol].equals(".")) {
                configs.add(new ChessConfig(this, row, col, curRow, curCol));
                break;
            }
        }
        return configs;
    }

    /**
     * Scans for all valid knight moves at a certain location. If a move is valid, creates a new config and adds it to a list.
     *
     * @param row row to scan from.
     * @param col column to scan from.
     * @return The list of all new configurations from a valid move.
     */
    public List<Configuration> knightScan(int row, int col) {
        List<Configuration> configs = new ArrayList<>();
        boolean upCheck = true;
        boolean downCheck = true;
        boolean leftCheck = true;
        boolean rightCheck = true;
        boolean secondUpCheck = true;
        boolean secondDownCheck = true;
        boolean secondLeftCheck = true;
        boolean secondRightCheck = true;
        if (col < 2) {
            leftCheck = false;
        }
        if (col > COLS - 3) {
            rightCheck = false;
        }
        if (row < 2) {
            upCheck = false;
        }
        if (row > ROWS - 3) {
            downCheck = false;
        }
        if (row == 0) {
            secondUpCheck = false;
        }
        if (row == ROWS - 1) {
            secondDownCheck = false;
        }
        if (col == 0) {
            secondLeftCheck = false;
        }
        if (col == COLS - 1) {
            secondRightCheck = false;
        }
        if (upCheck) {
            if (secondLeftCheck) {
                if (!this.grid[row - 2][col - 1].equals(".")) {
                    configs.add(new ChessConfig(this, row, col, row - 2, col - 1));
                }
            }
            if (secondRightCheck) {
                if (!this.grid[row - 2][col + 1].equals(".")) {
                    configs.add(new ChessConfig(this, row, col, row - 2, col + 1));
                }
            }
        }
        if (downCheck) {
            if (secondLeftCheck) {
                if (!this.grid[row + 2][col - 1].equals(".")) {
                    configs.add(new ChessConfig(this, row, col, row + 2, col - 1));
                }
            }
            if (secondRightCheck) {
                if (!this.grid[row + 2][col + 1].equals(".")) {
                    configs.add(new ChessConfig(this, row, col, row + 2, col + 1));
                }
            }
        }
        if (leftCheck) {
            if (secondUpCheck) {
                if (!this.grid[row - 1][col - 2].equals(".")) {
                    configs.add(new ChessConfig(this, row, col, row - 1, col - 2));
                }
            }
            if (secondDownCheck) {
                if (!this.grid[row + 1][col - 2].equals(".")) {
                    configs.add(new ChessConfig(this, row, col, row + 1, col - 2));
                }
            }
        }
        if (rightCheck) {
            if (secondUpCheck) {
                if (!this.grid[row - 1][col + 2].equals(".")) {
                    configs.add(new ChessConfig(this, row, col, row - 1, col + 2));
                }
            }
            if (secondDownCheck) {
                if (!this.grid[row + 1][col + 2].equals(".")) {
                    configs.add(new ChessConfig(this, row, col, row + 1, col + 2));
                }
            }
        }
        return configs;
    }

    /**
     * Scans for all valid king moves at a certain location. If a move is valid, creates a new config and adds it to a list.
     *
     * @param row row to scan from.
     * @param col column to scan from.
     * @return The list of all new configurations from a valid move.
     */
    public List<Configuration> kingScan(int row, int col) {
        List<Configuration> configs = new ArrayList<>();
        boolean upCheck = true;
        boolean downCheck = true;
        boolean leftCheck = true;
        boolean rightCheck = true;
        if (row == 0) {
            upCheck = false;
        }
        if (row == ROWS - 1) {
            downCheck = false;
        }
        if (col == 0) {
            leftCheck = false;
        }
        if (col == COLS - 1) {
            rightCheck = false;
        }
        if (upCheck) {
            if (!this.grid[row - 1][col].equals(".")) {
                configs.add(new ChessConfig(this, row, col, row - 1, col));
            }
        }
        if (downCheck) {
            if (!this.grid[row + 1][col].equals(".")) {
                configs.add(new ChessConfig(this, row, col, row + 1, col));
            }
        }
        if (leftCheck) {
            if (!this.grid[row][col - 1].equals(".")) {
                configs.add(new ChessConfig(this, row, col, row, col - 1));
            }
        }
        if (rightCheck) {
            if (!this.grid[row][col + 1].equals(".")) {
                configs.add(new ChessConfig(this, row, col, row, col + 1));
            }
        }
        if (upCheck && leftCheck) {
            if (!this.grid[row - 1][col - 1].equals(".")) {
                configs.add(new ChessConfig(this, row, col, row - 1, col - 1));
            }
        }
        if (upCheck && rightCheck) {
            if (!this.grid[row - 1][col + 1].equals(".")) {
                configs.add(new ChessConfig(this, row, col, row - 1, col + 1));
            }
        }
        if (downCheck && leftCheck) {
            if (!this.grid[row + 1][col - 1].equals(".")) {
                configs.add(new ChessConfig(this, row, col, row + 1, col - 1));
            }
        }
        if (downCheck && rightCheck) {
            if (!this.grid[row + 1][col + 1].equals(".")) {
                configs.add(new ChessConfig(this, row, col, row + 1, col + 1));
            }
        }
        return configs;
    }

    /**
     * Scans for all valid moves at a certain location. First checks to see if a piece is at the location and if there is, which type.
     * Then uses the scan method for that piece and adds all the new valid configs to a list.
     *
     * @param row row to scan from.
     * @param col column to scan from.
     * @return The list of all new configurations from a valid move.
     */
    public List<Configuration> scan(int row, int col) {
        List<Configuration> neighbors = new ArrayList<>();
        List<Configuration> configs;
        String type = this.grid[row][col];
        switch (type) {
                case "P":
                    configs = pawnScan(row, col);
                    neighbors.addAll(configs);
                    break;
                case "B":
                    configs = bishopScan(row, col);
                    neighbors.addAll(configs);
                    break;
                case "R":
                    configs = rookScan(row, col);
                    neighbors.addAll(configs);
                    break;
                case "N":
                    configs = knightScan(row, col);
                    neighbors.addAll(configs);
                    break;
                case "K":
                    configs = kingScan(row, col);
                    neighbors.addAll(configs);
                    break;
                case "Q":
                    configs = bishopScan(row, col);
                    neighbors.addAll(configs);
                    configs = rookScan(row, col);
                    neighbors.addAll(configs);
                    break;
                default:
                    break;
            }
            return neighbors;
    }

    /**
     * Gets the grid representing the chess board.
     *
     * @return The grid representing the chess board.
     */
    public String[][] getGrid() {
        return this.grid;
    }

    /**
     * Gets the list of total configs created.
     *
     * @return The list of total configs created.
     */
    public static List<Configuration> getTotalConfigs() {
        return totalConfigs;
    }

    /**
     * Gets the set of unique configs created.
     *
     * @return The set of unique configs created.
     */
    public static Set<Configuration> getUniqueConfigs() {
        return uniqueConfigs;
    }

    /**
     * Is the current config a solution?
     *
     * The current config is a solution if there is only one piece on the board.
     *
     * @return true if the current config is a solution, false otherwise.
     */
    @Override
    public boolean isSolution() {
        int pieces = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (!this.grid[row][col].equals(".")) {
                    pieces++;
                }
            }
        }
        if (pieces > 1) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Generates and stores all neighbor configurations for the current config.
     *
     * @return The collection of neighbors for the current config.
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                List<Configuration> neighbors = scan(row, col);
                this.neighbors.addAll(neighbors);
            }
        }
        return this.neighbors;
    }

    /**
     * Checks to see if another object is the same as the current config.
     *
     * @param other The object to compare to.
     * @return true if the current config and other object are the equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof ChessConfig) {
            result = true;
            ChessConfig otherConfig = (ChessConfig) other;
            String[][] otherGrid = otherConfig.getGrid();
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (!this.grid[row][col].equals(otherGrid[row][col])) {
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks to see if another object is the same as the current config, but is much stricter and compares the strings (pieces) by
     * memory location and not equality. This is helpful for checking valid captures when a user is playing and making selections.
     *
     * @param other The object to compare to.
     * @return true if the current config and other object are the equal strictly by piece, false otherwise.
     */
    public boolean strictEquals(Object other) {
        boolean result = false;
        if (other instanceof ChessConfig) {
            result = true;
            ChessConfig otherConfig = (ChessConfig) other;
            String[][] otherGrid = otherConfig.getGrid();
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (this.grid[row][col] != (otherGrid[row][col])) {
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * The hash code of the current config (the hash code of the grid.)
     *
     * @return The hash code of the current config.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.grid);
    }

    /**
     * The string representation of the current config (specifically the grid.)
     *
     * Format:
     *
     *   c0 c1 cn
     * r0 .  .  .
     * r1 .  .  .
     * rn .  .  .
     *
     * @return The string representation of the current config.
     * */
    @Override
    public String toString() {
        String gridString = "";
        gridString += "\n";
        gridString += "  ";
        for (Integer colNum = 0; colNum < COLS; colNum++) {
            gridString += colNum + " ";
        }
        gridString += "\n";
        for (int row = 0; row < ROWS; row++) {
            gridString += row + " ";
            for (int col = 0; col < COLS; col++) {
                gridString += this.grid[row][col] + " ";
            }
            gridString += "\n";
        }
        return gridString;
    }

}
