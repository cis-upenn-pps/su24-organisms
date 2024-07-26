package organisms.g3;

        import organisms.Move;
        import organisms.OrganismsPlayer;
        import organisms.ui.OrganismsGame;

        import java.awt.*;
        import java.util.ArrayList;
        import java.util.Random;
        import java.util.concurrent.ThreadLocalRandom;

public class g3Player2awoiv implements OrganismsPlayer {
    private OrganismsGame game;
    private int dna;
    private ThreadLocalRandom random;
    private Random rand;

    private int currentX = 0;
    private int currentY = 0;
    private boolean movingRight = true;
    private boolean movingDown = true;

    private ArrayList<Integer> organismHistory = new ArrayList<>();

    @Override
    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();
        this.rand = new Random();
    }

    @Override
    public String name() {
        return "g3Player2awoiv";
    }

    @Override
    public Color color() {
        return new Color(50, 83, 215, 255);
    }

    @Override
    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {

        // Consume food if present
        if (foodHere > 0) {
            return reproduceRandomly();
        }

        // Prioritize moving towards food
        if (foodW) return Move.movement(Action.WEST);
        if (foodN) return Move.movement(Action.NORTH);
        if (foodE) return Move.movement(Action.EAST);
        if (foodS) return Move.movement(Action.SOUTH);

        // Reproduction strategy
        if (energyLeft > 350) {
            return reproduceRandomly();
        }

        // Movement strategy when no food is visible
        if (organismHistory.size() < 5) {
            ArrayList<Integer> movelist = new ArrayList<>();
            movelist.add(0); // STAY
            movelist.add(2); // NORTH
            movelist.add(4); // SOUTH
            int actionVal = movelist.get(rand.nextInt(movelist.size()));
            organismHistory.add(actionVal);
            return Move.movement(Action.fromInt(actionVal));
        } else {
            return moveInSquarePattern();
        }
    }

    private Move reproduceRandomly() {
        int childPosIndex = this.random.nextInt(1, 5);
        Action childPosChoice = Action.fromInt(childPosIndex);
        int childKey = this.random.nextInt();
        return Move.reproduce(childPosChoice, childKey);
    }

    private Move moveInSquarePattern() {
        if (movingRight) {
            if (currentX < 4) {
                currentX++;
                return Move.movement(Action.EAST);
            } else {
                movingRight = false;
                if (movingDown && currentY < 4) {
                    currentY++;
                    return Move.movement(Action.SOUTH);
                } else {
                    movingDown = false;
                    currentY--;
                    return Move.movement(Action.NORTH);
                }
            }
        } else {
            if (currentX > 0) {
                currentX--;
                return Move.movement(Action.WEST);
            } else {
                movingRight = true;
                if (movingDown && currentY < 4) {
                    currentY++;
                    return Move.movement(Action.SOUTH);
                } else {
                    movingDown = false;
                    currentY--;
                    return Move.movement(Action.NORTH);
                }
            }
        }
    }

    @Override
    public int externalState() {
        return 0;
    }
}
