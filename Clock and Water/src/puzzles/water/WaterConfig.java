package puzzles.water;

import puzzles.common.solver.Configuration;

import java.util.*;

/**
 * A WaterConfig that holds the information of buckets and how much water is in them and can create and store the neighbor configs of it.
 * Holds static data for how much each bucket can be filled to the max, the total and unique number of configs created, the goal of water in a bucket to reach.
 *
 * @author Aidan Sanderson
 */
public class WaterConfig implements Configuration {

    /** List of all configs created. */
    private final static List<WaterConfig> totalConfigs = new ArrayList<>();

    /** Set of all unique configs created. */
    private final static Set<WaterConfig> uniqueConfigs = new HashSet<>();

    /** Max fill capacity of every bucket. */
    private static List<Integer> bucketSizes = new ArrayList<>();

    /** How much water one bucket needs to hold for the goal to be satisfied. */
    private static int goal;

    /** List of how much water is currently in each bucket. */
    private List<Integer> buckets;

    /** The neighbor configs of the WaterConfig. */
    private final Collection<Configuration> neighbors;

    /**
     * Initial constructor for the WaterConfig. Sets the max fill capacity's of the buckets, sets the goal,
     * and initializes each bucket to start with 0 gallons of water.
     *
     * @param bucketSizesLst List of bucket fill capacity's
     * @param goalAmount How much water in one bucket to reach.
     */
    public WaterConfig(List<Integer> bucketSizesLst, int goalAmount) {
        bucketSizes = bucketSizesLst;
        goal = goalAmount;
        this.buckets = new ArrayList<>();
        for (Integer bucket : bucketSizes) {
            this.buckets.add(0);
        }
        this.neighbors = new ArrayList<>();
        totalConfigs.add(this);
        uniqueConfigs.add(this);
    }

    /**
     * Neighbor constructor for a pouring from bucket a to bucket b config.
     *
     * @param config The parent config to inherit data from.
     * @param bucketAind Pour from bucket index.
     * @param bucketBind Pour to bucket index.
     */
    public WaterConfig(WaterConfig config, int bucketAind, int bucketBind) {
        this.buckets = new ArrayList<>();
        this.buckets.addAll(config.getBuckets());
        this.neighbors = new ArrayList<>();
        int aFill;
        int bFill;
        int differenceB = bucketSizes.get(bucketBind) - this.buckets.get(bucketBind);
        int differenceA = buckets.get(bucketAind) - differenceB;
        if (differenceA >= 0) {
            bFill = bucketSizes.get(bucketBind);
            aFill = differenceA;
        }
        else {
            bFill = this.buckets.get(bucketBind) + this.buckets.get(bucketAind);
            aFill = 0;
        }
        this.buckets.remove(bucketAind);
        this.buckets.add(bucketAind,aFill);
        this.buckets.remove(bucketBind);
        this.buckets.add(bucketBind,bFill);
        totalConfigs.add(this);
        uniqueConfigs.add(this);

    }

    /**
     * Neighbor constructor for either filling or dumping a bucket.
     *
     * @param config The parent config to inherit data from.
     * @param bucketInd Index of bucket to modify.
     * @param isFill Is the bucket being filled?
     */
    public WaterConfig(WaterConfig config, int bucketInd, boolean isFill) {
        this.buckets = new ArrayList<>();
        this.buckets.addAll(config.getBuckets());
        this.neighbors = new ArrayList<>();
        this.buckets.remove(bucketInd);
        if (isFill) {
            int maxFill = bucketSizes.get(bucketInd);
            this.buckets.add(bucketInd, maxFill);
        }
        else {
            this.buckets.add(bucketInd, 0);
        }
        totalConfigs.add(this);
        uniqueConfigs.add(this);
    }

    /**
     * Constructor for a test config to ensure the neighbors are generated properly.
     *
     * @param testConfig List of amount in each bucket for the test config.
     */
    public WaterConfig(List<Integer> testConfig) {
        this.buckets = new ArrayList<>();
        this.buckets.addAll(testConfig);
        this.neighbors = new ArrayList<>();
    }

    /**
     * Gets the goal amount.
     *
     * @return The goal amount.
     */
    public static int getGoal() {
        return goal;
    }

    /**
     * Gets the max fill capacity's.
     *
     * @return The max fill capacity's
     */
    public static List<Integer> getSizes() {
        return bucketSizes;
    }

    /**
     * Gets the list of how much water is in each bucket.
     *
     * @return The list of how much water is in each bucket.
     */
    public List<Integer> getBuckets() {
        return this.buckets;
    }

    /**
     * Gets the list of all configs.
     *
     * @return All configs created.
     */
    public static List<WaterConfig> getTotalConfigs() {
        return totalConfigs;
    }

    /**
     * Gets the set of unique configs.
     *
     * @return All unique configs created.
     */
    public static Set<WaterConfig> getUniqueConfigs() {
        return uniqueConfigs;
    }


    /**
     * Is the current configuration a solution?
     *
     * @return true if the configuration is a puzzle's solution; false, otherwise
     */
    @Override
    public boolean isSolution() {
        for (Integer amount : this.buckets) {
            if (amount == goal) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test function to ensure neighbors are being generated properly.
     *
     * @param testConfig The List of current bucket amounts to test from.
     */
    public void testSuccessors(List<Integer> testConfig) {
        WaterConfig test = new WaterConfig(testConfig);
        Collection<Configuration> successors = test.getNeighbors();
        for (Configuration successor : successors) {
            System.out.println(successor);
        }
    }

    /**
     * Get the collection of neighbors from the current configuration.
     *
     * @return All the neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        for (WaterStep type : WaterStep.values()) {
            switch (type) {
                case FILL:
                    for (int ind = 0; ind < this.buckets.size(); ind++) {
                        if (this.buckets.get(ind) != bucketSizes.get(ind)) {
                            WaterConfig neighbor = new WaterConfig(this, ind, true);
                            this.neighbors.add(neighbor);
                        }
                    }
                    break;
                case DUMP:
                    for (int ind = 0; ind < this.buckets.size(); ind++) {
                        if (this.buckets.get(ind) != 0) {
                            WaterConfig neighbor = new WaterConfig(this, ind, false);
                            this.neighbors.add(neighbor);
                        }
                    }
                    break;
                case POUR:
                    for (int a = 0; a < this.buckets.size(); a++) {
                        for (int b = 0; b < this.buckets.size(); b++) {
                            if (a != b && this.buckets.get(a) != 0 && this.buckets.get(b) != bucketSizes.get(b)) {
                                WaterConfig neighbor = new WaterConfig(this, a, b);
                                this.neighbors.add(neighbor);
                            }
                        }
                    }
                    break;
            }
        }
        return this.neighbors;
    }

    /**
     * Is this WaterConfig equal to another object?
     *
     * @param other Other object.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof WaterConfig) {
            WaterConfig otherConfig = (WaterConfig) other;
            if (this.buckets.equals(otherConfig.getBuckets())) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Gets the hashCode of the WaterConfig
     *
     * @return hashCode of the WaterConfig
     */
    @Override
    public int hashCode() {
        return this.buckets.hashCode();
    }

    /**
     * The string representation of the WaterConfig
     *
     * Format:
     * [(current bucket amounts)]
     *
     * @return String representation of the WaterConfig.
     */
    @Override
    public String toString() {
        return this.buckets.toString();
    }
}
