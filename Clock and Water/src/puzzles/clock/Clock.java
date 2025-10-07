package puzzles.clock;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.*;

/**
 * Main class for the clock puzzle.
 *
 * @author Aidan Sanderson
 */
public class Clock {


    /**
     * Run an instance of the clock puzzle.
     *
     * @param args [0]: the number of hours in the clock;
     *             [1]: the starting hour;
     *             [2]: the finish hour.
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println(("Usage: java Clock hours start finish"));
        } else {
            int totalHours = Integer.parseInt(args[0]);
            int startingHour = Integer.parseInt(args[1]);
            int endingHour = Integer.parseInt(args[2]);
            ClockConfig clock = new ClockConfig(totalHours, startingHour, endingHour);
            if (!clock.isSolution()) {
                clock.getNeighbors();
            }

            Solver solver = new Solver();
            solver.solve(clock);

            System.out.println("Hours: " + totalHours + ", Start: " + startingHour + ", End: " + endingHour);
            System.out.println("Total configs: " + clock.getTotalConfigs().size());
            System.out.println("Unique configs: " + clock.getUniqueConfigs().size());

            System.out.println("Path: " + solver.getSolutionPath());

            //if (path == null) {
                System.out.println("No solution");
            }
            //else {

                int iteration = 0;
                //for (Configuration config : path) {
                    //ClockConfig curClock = (ClockConfig) config;
                    //System.out.println("Step " + iteration + ": " + curClock.getCurrentHand());
                    iteration++;
                //}
            //}

        //}
    }
}
