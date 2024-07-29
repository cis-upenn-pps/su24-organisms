package organisms.g3;

import organisms.Move;
import organisms.OrganismsPlayer;
import organisms.ui.OrganismsGame;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Group3PlayerSkyler implements OrganismsPlayer {
    private OrganismsGame game;
    private int dna;
    private ThreadLocalRandom random;

    @Override
    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public String name() {
        return "Group3Player";
    }

    @Override
    public Color color() {
        return new Color(166, 124, 255, 255);
    }

    @Override
    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {

        if (energyLeft >= 350) {
            int childPosIndex = this.random.nextInt(1, 5);
            Action childPosChoice = Action.fromInt(childPosIndex);
            int childKey = this.random.nextInt();
            return Move.reproduce(childPosChoice, childKey);
        } else {
            int actionIndex = this.random.nextInt(Action.getNumActions() - 1);
            Action actionChoice = Action.fromInt(actionIndex);
            return Move.movement(actionChoice);
        }
    }

    @Override
    public int externalState() {
        return 0;
    }
}
