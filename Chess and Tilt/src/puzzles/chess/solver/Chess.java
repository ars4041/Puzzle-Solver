package puzzles.chess.solver;

import puzzles.chess.model.ChessConfig;
import puzzles.common.solver.Solver;

import java.io.File;
import java.io.IOException;

/**
 * Runs a bfs solver for a solitaire chess game with a given file to read from for an initial ChessConfiguration.
 * Prints the solution path if it exists, otherwise indicates that no solution exists.
 *
 * @author Aidan Sanderson
 */
public class Chess {

    /**
     * Creates a file from a given file name.
     *
     * @param fileName The name of the file.
     * @return The new file.
     */
    public static File getFile(String fileName) {
            File file = new File(fileName);
            return file;
    }

    /**
     * Creates an initial solitaire chess configuration from a given file name in the command line, uses a bfs solver to find
     * the best success path of the config. If the config is solvable the path is printed,
     * otherwise it is indicated that there is no solution.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        }
        else {
            String fileName = args[0];
            File file = getFile(fileName);
            try {
                ChessConfig chess = new ChessConfig(file);
                Solver solver = new Solver();
                solver.solve(chess);
                String path = solver.getPathAsString();
                System.out.print("File: " + fileName);
                System.out.print(chess);
                System.out.println("Total configs: " + ChessConfig.getTotalConfigs().size());
                System.out.println("Unique configs: " + ChessConfig.getUniqueConfigs().size());
                System.out.println();
                System.out.println(path);
            }
            catch (IOException e) {
                System.out.println("Can't read file: " + fileName);
                System.exit(1);
            }
        }
    }
}
