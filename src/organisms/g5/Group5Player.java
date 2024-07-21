package organisms.g5;

import organisms.Move;
import organisms.ui.OrganismsGame;
import organisms.OrganismsPlayer;

import java.awt.Color;

public class Group5Player implements OrganismsPlayer {
    private OrganismsGame game;
    private int currentFoodHere = 0;

    @Override
    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
    }

    @Override
    public String name() {
        return "Group 5 Player";
    }

    @Override
    public Color color() {
        return new Color(200, 200, 200, 255);
    }

    /**
     * Calculates the total number of food blocks in a given direction up to the given number of steps.
     *
     * @param steps      The number of steps to look ahead.
     * @param foodDirs   The presence of food in each of the four cardinal directions.
     * @param neighborDirs The presence of neighbors in each of the four cardinal directions.
     * @param x The current x-coordinate of the organism.
     * @param y The current y-coordinate of the organism.
     * @return An array where the value at each index represents the total number of food blocks in that direction.
     */
    private int[] countFoodInDirections(int steps, boolean[] foodDirs, int[] neighborDirs, int x, int y) {
        // Count food in the direction of NORTH, EAST, SOUTH, WEST
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int[] foodCounts = {0, 0, 0, 0};

        for (int i = 0; i < 4; i++) {
            int currentX = x;
            int currentY = y;
            for (int step = 0; step < steps; step++) {
                currentX += directions[i][0];
                currentY += directions[i][1];
                if (neighborDirs[i] != -1) {  break; } // end count if neighbor exists
                if (foodDirs[i]) { foodCounts[i]++; }
            }
        }

        return foodCounts;
    }

    @Override
    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE, boolean foodS, boolean foodW,
                     int neighborN, int neighborE, int neighborS, int neighborW) throws Exception {

        // If there is food, stay put until all foods consumed
        if (foodHere > 0) {
            currentFoodHere = foodHere;
            return Move.movement(Action.STAY_PUT);
        } else if (currentFoodHere > 0) {
            currentFoodHere = foodHere; // update the remaining food
            return Move.movement(Action.STAY_PUT);
        }

        // set current position as 0, 0
        int x = 0, y = 0;

        // Calculate the number of food blocks in each direction for 10 steps
        boolean[] foodDirs = {foodN, foodE, foodS, foodW};
        int[] neighborDirs = {neighborN, neighborE, neighborS, neighborW};
        int[] foodCounts = countFoodInDirections(20, foodDirs, neighborDirs, x, y);

        // Find the direction with the most food blocks
        int maxFoodDirection = 0, maxFoodCount = 0;
        for (int i = 0; i < 4; i++) {
            if (foodCounts[i] > maxFoodCount) {
                maxFoodCount = foodCounts[i];
                maxFoodDirection = i;
            }
        }

        // Move towards the direction with the most food block
        Action moveAction = switch (maxFoodDirection) {
            case 0 -> Action.NORTH;
            case 1 -> Action.EAST;
            case 2 -> Action.SOUTH;
            case 3 -> Action.WEST;
            default -> Action.STAY_PUT;
        };

        return Move.movement(moveAction);
    }

    @Override
    public int externalState() throws Exception {  return 0; }
}
