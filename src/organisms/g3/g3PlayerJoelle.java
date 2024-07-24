package organisms.g3;

import organisms.OrganismsPlayer;
import organisms.Move;
import organisms.ui.OrganismsGame;
import java.util.ArrayList;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class g3PlayerJoelle implements OrganismsPlayer {
    private OrganismsGame game;
    private int dna;
    private ThreadLocalRandom random;

    private ArrayList<Integer> organismHistory = new ArrayList<>();

    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();
    }


    public String name() {
        return "g3PlayerJoelle";
    }


    public Color color() {
        return new Color(255, 124, 166, 255);
    }


    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {

        Random rand = new Random();


        int actionIndex = 0;
        Action actionChoice = Action.fromInt(actionIndex);

        if (foodHere == 1) {
            actionChoice = Action.fromInt(0);
            this.organismHistory.add(0);

        }

        if(foodW) {
            actionChoice = Action.fromInt(1);
            this.organismHistory.add(1);

        }

        if(foodN) {
            actionChoice = Action.fromInt(2);
            this.organismHistory.add(2);

        }

        if(foodE) {
            actionChoice = Action.fromInt(3);
            this.organismHistory.add(3);

        }

        if(foodS) {
            actionChoice = Action.fromInt(4);
            this.organismHistory.add(4);

        }


        if(!foodW & !foodE & !foodS & !foodW){
            ArrayList<Integer> movelist = new ArrayList<>();

            int actionVal =0;
            //only staying or moving N/South for the first few
            if(this.organismHistory.size()<5){
                movelist.add(0);
                movelist.add(4);
                movelist.add(2);
                actionVal = movelist.get(rand.nextInt(movelist.size()));

            }
            else{
                //after the first 5 moves, only go N/S and if energy is high enough reproduce becomes and option
                movelist.add(4);
                movelist.add(2);
                if(energyLeft>200) {
                    movelist.add(5);
                }
                actionVal = movelist.get(rand.nextInt(movelist.size()));
            }

            //if energy is very high, always reproduce
            if(energyLeft>400){
                actionVal =5;
            }


            actionChoice = Action.fromInt(actionVal);
            this.organismHistory.add(actionVal);


        }


        if (actionChoice == Action.REPRODUCE) {
            ArrayList<Integer> childlist = new ArrayList<>();

            childlist.add(2);
            childlist.add(4);

            //early on, an organism can reproduce in any direction.
            if(this.organismHistory.size()<5) {
                childlist.add(1);
                childlist.add(3);
            }
            // randomly pick a direction and key for the child
            int childPosIndex = childlist.get(rand.nextInt(childlist.size()));
            Action childPosChoice = Action.fromInt(childPosIndex);
            int childKey = rand.nextInt(Action.getNumActions());
            return Move.reproduce(childPosChoice, childKey);
        } else {
            System.out.println(this.dna + ": " + this.organismHistory);
            // staying put or moving in a direction
            return Move.movement(actionChoice);

        }


    }

    public int externalState() {
        return 0;
    }
}
