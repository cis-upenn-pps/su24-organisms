package organisms.g3;

import organisms.OrganismsPlayer;

import organisms.Move;
import organisms.ui.OrganismsGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class combinedPlayerJoelle implements OrganismsPlayer {

    private ThreadLocalRandom random;

    private OrganismsGame game;
    private int dna;

    private ArrayList<Integer> organismHistory = new ArrayList<>();

    private int familyCount = 0;

    private boolean exploreMode = true;

    int roundCount = 0;


    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();

    }

    public String name() {
        return "combinedPlayerJoelle";
    }

    public Color color() {
        return new Color(255, 150, 225, 255);
    }

    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {

        int actionIndex = 0;
        Action actionChoice = Action.fromInt(actionIndex);
        int childPosIndex = random.nextInt(0,4);

        if(energyLeft>=game.M()/2){
            exploreMode = false;
        }

        if (exploreMode) {
            if (foodHere > 0) {
                actionChoice = Action.fromInt(0);
                this.organismHistory.add(0);
            }
            if (foodW) {
                actionChoice = Action.fromInt(1);
                this.organismHistory.add(1);

            }
            if (foodN) {
                actionChoice = Action.fromInt(2);
                this.organismHistory.add(2);
            }
            if (foodE) {
                actionChoice = Action.fromInt(3);
                this.organismHistory.add(3);
            }
            if (foodS) {
                actionChoice = Action.fromInt(4);
                this.organismHistory.add(4);

            }

            if (!foodS && !foodE && !foodW && !foodN ) {
                actionChoice = Action.fromInt(0);
                this.organismHistory.add(0);

            }


        }

        else{

            if (foodW && neighborW ==-1 ) {
                actionChoice = Action.fromInt(5);
                this.organismHistory.add(5);
                childPosIndex =1;

            }
            if (foodN && neighborN ==-1) {
                actionChoice = Action.fromInt(5);
                this.organismHistory.add(5);
                childPosIndex =2;
            }
            if (foodE && neighborE ==-1) {
                actionChoice = Action.fromInt(5);
                this.organismHistory.add(5);
                childPosIndex =3;

            }
            if (foodS && neighborS==-1) {
                actionChoice = Action.fromInt(5);
                this.organismHistory.add(5);
                childPosIndex =4;

            }




        }


        if (actionChoice == Action.REPRODUCE) {

            Action childPosChoice = Action.fromInt(childPosIndex);
            int childKey =random.nextInt(1,255);
            return Move.reproduce(childPosChoice, childKey);

        } else {
            System.out.println(this.dna + ": " + this.organismHistory);
            System.out.println("mode: " + exploreMode);

            return Move.movement(actionChoice);

        }

    }


    public int externalState() {
        return this.dna;
    }



}
