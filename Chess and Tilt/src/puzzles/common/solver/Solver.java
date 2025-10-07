package puzzles.common.solver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * BFS Solver that works with the Configuration interface.
 *
 * @author Ryan O'Malley
 * @github cro5058
 */
public class Solver {

    /** Fields */
    // Starting configuration
    private Configuration start;

    // Goal configuration
    private Configuration goal;

    // Queue for configurations to visit
    private LinkedList<Configuration> queue;

    // HashMap to store visited configurations
    // Key = Configuration, Value = How many times we've seen it
    private LinkedHashMap<Configuration, Integer> configurationMap;

    // Predecessor map
    // Key = Configuration, Value = Configuration that is its predecessor
    private LinkedHashMap<Configuration, Configuration> predecessorMap;

    // Number of configurations generated
    private int totalConfigs;

    // Path to the solution, if it exists
    private LinkedList<Configuration> path;


    /** Constructor */
    public Solver() {
        // Do not initialize the start or goal configurations yet

        // Initialize queue to empty queue
        this.queue = new LinkedList<>();

        // Initialize HashMaps as empty HashMaps
        this.configurationMap = new LinkedHashMap<>();
        this.predecessorMap = new LinkedHashMap<>();

        // No configurations seen yet
        this.totalConfigs = 0;

        // No path to a solution exists yet
        this.path = null;
    }

    /**
     * Performs BFS. Starts from the input start Configuration and searches for a solution Configuration.
     * Also calls buildPath to build the shortest path from start to finish if such a path is possible.
     *
     * @param start the starting Configuration to solve from.
     */
    public void solve(Configuration start) {
        // Set the start Configuration as the input Configuration
        this.start = start;

        // Enqueue the start configuration
        this.queue.add(this.start);

        // Add the start configuration to the configurations HashMap
        // and the predecessor map
        this.configurationMap.put(this.start, 1);
        this.predecessorMap.put(this.start, this.start);

        // Indicate that we have seen 1 configuration: the start configuration
        this.totalConfigs++;

        // While the queue is not empty...
        while (!this.queue.isEmpty()) {
            // Get the first element of the queue
            Configuration current = this.queue.poll();

            // See if it is the goal. If so...
            if (current.isSolution()) {
                // Save it as the goal configuration (for path building later)
                this.goal = current;

                // Stop doing BFS
                break;
            }

            // Otherwise, get its neighbors. For each neighbor...
            for (Configuration neighbor : current.getNeighbors()) {
                // If the neighbor is not already in the predecessor map...
                if (!this.predecessorMap.containsKey(neighbor)){
                    // Put it in the predecessor map with current as its predecessor
                    this.predecessorMap.put(neighbor, current);
                    // Add it to the queue of configurations to check
                    this.queue.add(neighbor);
                }

                // See if it is in the map of configurations. If so...
                if (this.configurationMap.containsKey(neighbor)) {
                    // Increment the number of times it was seen
                    this.configurationMap.put(neighbor,
                                              this.configurationMap.get(neighbor) + 1);
                }
                // Otherwise if it has not been seen yet...
                else {
                    // Put it in the configurations map. We've seen it 1 time so far.
                    this.configurationMap.put(neighbor, 1);
                }

                // Increment the number of configurations generated
                this.totalConfigs++;
            }
        }

        // Build the path from the start to the goal
        buildPath();
    }

    /**
     * Builds the shortest path between the start configuration and the solution configuration.
     * If a valid path exists, this method sets the path field equal to that path.
     * Otherwise, it sets the path field equal to null.
     */
    private void buildPath() {
        // Path is null until we know there was a solution
        LinkedList<Configuration> path = null;

        // If we were able to reach the goal configuration...
        if (this.predecessorMap.containsKey(this.goal)) {
            // There is actually a path, initialize a new LinkedList
            path = new LinkedList<>();

            // Set a Configuration as a cursor
            Configuration current = this.goal;

            // While we have not reached the start...
            while (!(current.equals(this.start))) {
                path.addFirst(current);
                current = predecessorMap.get(current);
            }

            // Add the start Configuration to the path
            path.addFirst(this.start);
        }

        // Set the path to the solution equal to the derived path
        this.path = path;
    }

    /**
     * Returns the total number of configurations seen
     * throughout the course of solving via BFS.
     *
     * @return the (integer) total number of configurations seen
     * in the course of generating the solution.
     */
    public int getTotalConfigs() {
        return this.totalConfigs;
    }

    /**
     * Returns the number of unique configurations seen
     * throughout the course of solving via BFS, which is equivalent to
     * the number of keys (size) in the configurations HashMap.
     *
     * @return the (integer) number of unique configurations seen
     * in the course of generating the solution.
     */
    public int getUniqueConfigs() {
        return this.configurationMap.size();
    }

    /**
     * Method that returns the path from start configuration to end configuration as a LinkedList.
     * Note that it is possible to get null as a result from this method if there is not a path
     * from the start configuration to the end configuration or if solve() has not been called yet.
     *
     * @return the path to the solution if it exists, else returns null.
     */
    public LinkedList<Configuration> getPath() {
        return this.path;
    }

    /**
     * Method that returns a String containing the steps from start to finish, if they exist.
     * If the path is null (meaning no solution exists), returns "No solution".
     *
     * @return the path to the solution if it exists, else returns "No solution".
     */
    public String getPathAsString() {
        // If there is no path from start to goal...
        if (path == null) {
            // Display the message
            return "No solution";
        }
        // Otherwise...
        else {
            // Accumulator variable for the result
            String result = "";

            // Get the size of the path from start to goal
            int n = path.size();

            // Accumulate the steps of the path
            for (int i = 0; i < n; i++) {
                result += "Step " + i + ": \n" + this.path.get(i) + "\n";
            }

            // Return the accumulated path
            return result;
        }
    }
}
