package puzzles.tilt.solver;

import puzzles.common.solver.Solver;
import puzzles.tilt.model.TiltConfig;


/**
 * Tilt
 * Allows the user to see the steps to solve a Tilt puzzle,
 * if a solution is possible.
 *
 * @author Ryan O'Malley
 * @github cro5058
 */
public class Tilt {

    /** Constant */
    // Error message
    public static final String INVALID_FILE_MESSAGE = "Invalid file.";

    /**
     * The main method.
     */
    public static void main(String[] args) {
        // If the user gave bad input...
        if (args.length != 1) {
            // Display usage message
            System.out.println("Usage: java Tilt filename");
        }

        // Grab the board's filename from the args
        String filename = args[0];

        // Show the filename in the console
        System.out.println("File: " + filename);

        // Create a starting Tilt configuration based on the board
        TiltConfig startConfig = TiltConfig.loadFile(filename, true);

        // Create a new Solver
        Solver solver = new Solver();

        // Solve the problem
        solver.solve(startConfig);

        // Display numbers of configurations generated: total and unique
        System.out.println("Total configs: " + solver.getTotalConfigs());
        System.out.println("Unique configs: " + solver.getUniqueConfigs());

        // Display the solution or a message saying there was no solution
        System.out.println(solver.getPathAsString());
    }
}
