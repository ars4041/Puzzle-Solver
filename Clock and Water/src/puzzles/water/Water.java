
package puzzles.water;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.ArrayList;
import java.util.List;


/**
 * Main class for the water buckets puzzle.
 *
 * @author Aidan Sanderson
 */


public class Water {

    /**
     * Run an instance of the water buckets puzzle.
     *
     * @param args [0]: desired amount of water to be collected;
     *             [1..N]: the capacities of the N available buckets.
     */


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(
                    ("Usage: java Water amount bucket1 bucket2 ...")
            );
        } else {
            int goal = 0;
            List<Integer> bucketSizes = new ArrayList<>();
            boolean first = true;
            for (String arg : args) {
                if (first) {
                    goal = Integer.parseInt(arg);
                    first = false;
                }
                else {
                    bucketSizes.add(Integer.parseInt(arg));
                }
            }

            WaterConfig water = new WaterConfig(bucketSizes,goal);
            //List<Integer> testList = new ArrayList<>();
            //testList.add(0);
            //testList.add(17);
            //testList.add(28);
            //testList.add(0);
            //water.testSuccessors(testList);
            Solver solver = new Solver();


            solver.solve(water);

            System.out.println("Amount: " + WaterConfig.getGoal() + ", Buckets: " + WaterConfig.getSizes());
            System.out.println("Total configs: " + WaterConfig.getTotalConfigs().size());
            System.out.println("Unique configs: " + WaterConfig.getUniqueConfigs().size());

            System.out.println("Path: " + solver.getSolutionPath());

            //if (path != null) {
                int step = 0;
                //for (Configuration config : path) {
                    //System.out.println("Step " + step + ": " + config);
                    step++;
                }
            }
            //else {
                //System.out.println("No solution");
            }
        //}


    //}

//}
