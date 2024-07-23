package organisms.g5;

import organisms.Move;
import organisms.ui.OrganismsGame;
import organisms.OrganismsPlayer;

import java.awt.Color;
import java.util.Optional;

public class Group5Player implements OrganismsPlayer {

    enum OCCUPANT {
        empty,              //i.e no foreign object/organism on this square
        other_organism,     //can be of the same or a different species
        food
    }
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


    /**
     * Calculates the immediate cost (energy gain minus energy expenditure) of a particular move.
     * @param move boolean representing whether the organism will move one space across the board (TRUE),
     *             or remain in place (FALSE)
     * @param occupant enum representing the state of the space which we propose the organism inhabit.
     *                 This could be the same square (if staying in place) or a new one (if moving)
     * @param reproduce boolean representing whether the organism will reproduce.
     *                  An error will be thrown if we try to both move and reproduce simultaneously.
     * @param override an optional array of 4 integers that are used to price V1, V2, S, U
     *                 If no value is specified, default game-set parameters will be used.
     * NOTE immediate means next move. If we move onto e.g. a square with 4 units of food,
     *                  only one unit will count against the immediate cost of that move
     * @return an int representing the net energy change associated with a single move.
     */
    protected double netBenefit(boolean move, OCCUPANT occupant, boolean reproduce, Optional<double[]> override) {

        //get game costings (default values)
        double v1 = game.v();              //reproduce
        double v2 = game.v();              //move
        double s = game.s();
        double u = game.u();

        //give player opportunity to override these
        if (override.isPresent() && override.get().length == 4) {
            v1 = override.get()[0];
            v2 = override.get()[1];
            s = override.get()[2];
            u = override.get()[3];
        }

        //calculate override of specific moves

        if (reproduce)  {
            if (move) throw new IllegalArgumentException("Bad argument! Conflicting values for reproduce and move.");
            if (occupant.equals(OCCUPANT.other_organism)) throw new IllegalArgumentException("Bad argument! Conflicting values for reproduce and occupant.");
            return -v2;
        }

        if (!move) {

            if (occupant.equals(OCCUPANT.other_organism)) throw new IllegalArgumentException("Bad argument! Conflicting values for move and occupant.");
            if (occupant.equals(OCCUPANT.food)) return u - s;             //gain from food (eat) - cost of staying (x)
            else return -s;

        }

        else if (move) {
            if (occupant.equals(OCCUPANT.food)) return u - v1;              //gain from food (eat) - cost of movement to get that food (exert)
            if (occupant.equals(OCCUPANT.empty)) return -v1;
            if (occupant.equals(OCCUPANT.other_organism)) {System.err.println("This move has poor efficiency! Please consider remain."); return -game.v();
            }
        }

        //we should never get here
        throw new IllegalArgumentException("No valid strategy for this combination of arguments. Please try again.");
    }


    @Override
    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE, boolean foodS, boolean foodW,
                     int neighborN, int neighborE, int neighborS, int neighborW) throws Exception {

        // If there is food, stay put until all foods consumed
        if (foodHere > 0) {
            currentFoodHere = foodHere;
            System.out.println("Action " + Action.STAY_PUT.toString() + ", " + foodHere + " of food remaining on this square.");
            return Move.movement(Action.STAY_PUT);
        } else if (currentFoodHere > 0) {
            currentFoodHere = foodHere; // update the remaining food
            System.out.println("Action " + Action.STAY_PUT.toString() + ", " + foodHere + " of food remaining on this square.");
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

        System.out.println("Action " + moveAction.toString() + " , " + foodCounts[maxFoodDirection] + " units of food in this direction.");
        return Move.movement(moveAction);
    }

    /**
     * Applies a bias to the values of v, s, u that determine the cost to the organism of moving across the board.
     * @param foodW a value (/10) that represents the priority that an organism should give to food
     * @param energyW a value (/10) that represents the priority that an organism should give to conserving energy
     * @param repoW a value (/10) that represents the priority that an organism should give to reproducing
     * @return an array of 4 ints that represent new values of v1, v2, s, u, in that order.
     * Note that value of v is split into 2 (v1 and v2) to allow separate costs to be applied to reproduction and movement.
     */
    protected double[] applyBias(double foodW, double energyW, double repoW) {

        //check inputs
        if (foodW < 0 || energyW < 0 || repoW < 0 || foodW > 10 || energyW > 10 || repoW > 10) throw new IllegalArgumentException("Bad input! Priority values must be between 0 and 10, inclusive.");

        //container for return value
        double[] costings = new double[4];            //order of variables is V1, V2, S, U

        //work out priorities - expressed as a proportion of the max (30)
        double fp = (foodW / 30);
        double ep = (energyW / 30);
        double rp = (repoW / 30);

        // work out deltas for each game-set variable
        double v = 20 - 2;
        double u = 500 - 10;

        //change costings to reflect new biases
        costings[0] = game.v();                     //leave cost of movement the same (never changed by weightings)
        costings[1] = (game.v() - (v * rp));        //reduce cost of reproduction by scaling factor rp
        costings[2] = (game.s() - (1 * ep));        //reduce cost of staying in place by scaling factor ep
        costings[3] = (game.u() + (u * fp));        //increase the benefit of food by scaling factor fp

        //return new set of costings with biases applied
        return costings;

    }

    @Override
    public int externalState() throws Exception {  return 0; }
}
