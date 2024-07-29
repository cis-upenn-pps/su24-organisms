package organisms.g3;

import organisms.Move;
import organisms.OrganismsPlayer;
import organisms.ui.OrganismsGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collections;
import java.util.stream.Stream;


public class g3JoelleDecisionMaker implements OrganismsPlayer {

    private ThreadLocalRandom random;

    private OrganismsGame game;
    private int dna;

    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();

    }

    public String name() {
        return "g3DecisionMaker";
    }

    public Color color() {
        return new Color(255, 255, 204, 255);
    }

    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {

        int actionIndex = 0;
        Action actionChoice = Action.fromInt(actionIndex);
        int childPosIndex = 1;
        Action childPosChoice = Action.fromInt(1);


        int decisionWeightStay = 0;

        boolean foodBool =true;

        int unitsPerFood = game.u();
        int maxEnergy = game.M();
        int moveEnergyLoss = game.v();
        int maxFoodUnits = game.K();
        int stayPutUnits = game.s();
        int repValue =100;

        ArrayList<Integer> moveDecisions = new ArrayList<>();
        ArrayList<Integer> repDecisions = new ArrayList<>();


        if (energyLeft < maxEnergy / 2) {
             repValue = -50;
        }

        for(int i = 1; i < 5; i++) {

            if (i == 1) {
                foodBool = foodW;
            } else if (i == 2) {
                foodBool = foodN;
            } else if (i == 3) {
                foodBool = foodE;
            } else if (i == 4) {
                foodBool = foodS;
            }


            if (foodBool) {
                        int moveAdd = (energyLeft - moveEnergyLoss + maxFoodUnits);
                        int repAdd = (energyLeft - moveEnergyLoss + maxFoodUnits + repValue);

                        moveDecisions.add(moveAdd);
                        repDecisions.add(repAdd);

            } else {
                        int moveAdd = (energyLeft - moveEnergyLoss);
                        int repAdd = (energyLeft - moveEnergyLoss + repValue);
                        moveDecisions.add(moveAdd);
                        repDecisions.add(repAdd);
                        decisionWeightStay = energyLeft + 20;
                    }
                }

        int repMax = Collections.max(repDecisions);
        int repInt = repDecisions.indexOf(repMax)+1;
        int movMax = Collections.max(moveDecisions);
        int movInt = moveDecisions.indexOf(movMax)+1;

        int firstElementREP = repDecisions.get(0);
        boolean repBOOL = repDecisions.stream().allMatch(element -> element.equals(firstElementREP));
        int firstElementMOV = moveDecisions.get(0);
        boolean movBOOL = moveDecisions.stream().allMatch(element -> element.equals(firstElementMOV));

        System.out.println("--------------------------------");
        System.out.println("decision for organism: " + this.dna);
        System.out.println("move weights:" + moveDecisions);
        System.out.println("max movement weight:" + movMax);
        System.out.println("rep weights:" +repDecisions);
        System.out.println("max reproduce weight:" + repMax);
        System.out.println("decision to stay weight: "+ decisionWeightStay);


        if (repMax >= movMax) {
            if(repMax>decisionWeightStay) {
                if(repBOOL){
                     childPosChoice = Action.fromInt(random.nextInt(1,5));
                }
                else {
                     childPosChoice = Action.fromInt(repInt);
                }
                int childKey = random.nextInt(1, 255);
                System.out.println("reproduced @: " + childPosChoice);
                return Move.reproduce(childPosChoice, childKey);
            }
            else{
                System.out.println(Action.fromInt(0));
                return Move.movement(Action.fromInt(movMax));

            }

        } else {
            if(movMax>decisionWeightStay){
                if(repBOOL){
                    actionChoice = Action.fromInt(random.nextInt(1,5));
                }
                else {
                    actionChoice = Action.fromInt(movInt);
                }
                System.out.println("moved to: " + actionChoice);
                return Move.movement(actionChoice);

            }
            else{
                System.out.println(Action.fromInt(0));
                return Move.movement(Action.fromInt(0));


            }


        }



    }


    public int externalState() {
        return this.dna;
    }


}
