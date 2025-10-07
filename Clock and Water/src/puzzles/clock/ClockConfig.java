package puzzles.clock;

import puzzles.common.solver.Configuration;

import java.util.*;

/**
 * A clock interface that holds the current hour and can create and store neighbors of the hours next to it.
 * Contains static data for the goal to eventually reach, how many hours are on the clock, the total and unique number of configs.
 *
 * @author Aidan Sanderson
 */
public class ClockConfig implements Configuration {

    /** List of all configs created. */
    private final static List<ClockConfig> totalConfigs = new ArrayList<>();

    /** Set of all unique configs created. */
    private final static Set<ClockConfig> uniqueConfigs = new HashSet<>();

    /** Number of hours on the clock. */
    private static int totalHands;

    /** What hour the clock wants to reach. */
    private static int goal;

    /** The current hour the clocks hand is on. */
    private final int currentHand;

    /** The neighbor configs of the clock */
    private final Collection<Configuration> neighbors;

    /**
     * Creates the initial clock config with the total number of hours, the starting hour, and the goal.
     *
     * @param totalHours Total number of hours on the clock.
     * @param startingHour Hour to start on.
     * @param goalHour The hour to end on.
     */
    public ClockConfig(int totalHours, int startingHour, int goalHour) {
        totalHands = totalHours;
        this.currentHand = startingHour;
        goal = goalHour;
        this.neighbors = new ArrayList<>();
        totalConfigs.add(this);
        uniqueConfigs.add(this);
    }

    /**
     * Creates a new config of a clock for neighbors.
     *
     * @param currentHand The hour that the clock hand will be on.
     */
    public ClockConfig(int currentHand) {
        this.currentHand = currentHand;
        this.neighbors = new ArrayList<>();
        totalConfigs.add(this);
        uniqueConfigs.add(this);
    }

    /**
     * Gets the current hour that the hand is on.
     *
     * @return The current hour the hand is on.
     */
    public int getCurrentHand() {
        return this.currentHand;
    }


    /**
     * Gets the list of all configs.
     *
     * @return All configs created.
     */
    public List<ClockConfig> getTotalConfigs() {
        return totalConfigs;
    }

    /**
     * Gets the set of unique configs.
     *
     * @return  All unique configs created.
     */
    public Set<ClockConfig> getUniqueConfigs() {
        return uniqueConfigs;
    }


    /**
     * Is this ClockConfig equal to another object?
     *
     * @param o Other object.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ClockConfig) {
            ClockConfig otherConfig = (ClockConfig) o;
            if (this.currentHand == otherConfig.currentHand) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Gets the hashCode of the ClockConfig
     *
     * @return hashCode of the ClockConfig
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(this.currentHand);
    }



    /**
     * Is the current configuration a solution?
     *
     * @return true if the configuration is a puzzle's solution; false, otherwise
     */
    @Override
    public boolean isSolution() {
        if (this.currentHand == goal) {
            return true;
        }
        return false;
    }

    /**
     * Get the collection of neighbors from the current configuration.
     *
     * @return All the neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ClockConfig backConfig;
        ClockConfig frontConfig;
        if (this.currentHand == 1) {
            backConfig = new ClockConfig(totalHands);
            frontConfig = new ClockConfig(this.currentHand + 1);
        }
        else if (this.currentHand == totalHands) {
            backConfig = new ClockConfig(this.currentHand - 1);
            frontConfig = new ClockConfig(1);
        }
        else {
            backConfig = new ClockConfig(this.currentHand - 1);
            frontConfig = new ClockConfig(this.currentHand + 1);
        }
        this.neighbors.add(backConfig);
        this.neighbors.add(frontConfig);
        return this.neighbors;
    }

    /**
     * The string representation of the ClockConfig
     *
     * Format:
     * Clock: Current Hand: + (current hour that the hand is on)
     *
     * @return String representation of the ClockConfig.
     */
    @Override
    public String toString() {
        return "Clock: Current Hand: " + this.currentHand;
    }
}
