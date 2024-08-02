package organisms.g4;

import organisms.Move;
import organisms.OrganismsPlayer;
import organisms.ui.OrganismsGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Group4Player implements OrganismsPlayer {
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
        return "Group4Player";
    }

    @Override
    public Color color() {
        return new Color(255, 200, 124, 255);
    }

    @Override
    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {
        // Evaluate if staying put is beneficial (e.g., when there's food here)
        if (foodHere > 0 && energyLeft < game.M() - game.u()) {
            return Move.movement(Action.STAY_PUT);
        }

        // Consider reproduction if the energy is sufficient
        if (energyLeft > game.v() * 10) { // ensure enough energy to reproduce and survive
            if (foodN && neighborN == -1) { // check if north is free and has food
                int childKey = random.nextInt(256); // generate a random initial state for the child
                return Move.reproduce(Action.NORTH, childKey);
            }else if(foodE && neighborE == -1) { // check if east is free and has food
                int childKey = random.nextInt(256); // generate a random initial state for the child
                return Move.reproduce(Action.EAST, childKey);
            }else if(foodS && neighborS == -1) { // check if south is free and has food
                int childKey = random.nextInt(256); // generate a random initial state for the child
                return Move.reproduce(Action.SOUTH, childKey);
            }else if(foodW && neighborW == -1) { // check if west is free and has food
                int childKey = random.nextInt(256); // generate a random initial state for the child
                return Move.reproduce(Action.WEST, childKey);
            }
        }

        // Move towards food if adjacent and no neighbors are present
        if (foodN && neighborN == -1) return Move.movement(Action.NORTH);
        if (foodE && neighborE == -1) return Move.movement(Action.EAST);
        if (foodS && neighborS == -1) return Move.movement(Action.SOUTH);
        if (foodW && neighborW == -1) return Move.movement(Action.WEST);

        // Default to staying put if no better option is found
        return Move.movement(Action.STAY_PUT);
    }

    @Override
    public int externalState() {
        return 1;  // Provide some variability based on initial dna value
    }

    public static class Group4Player2 implements OrganismsPlayer {
        private OrganismsGame game;
        private int dna;
        private ThreadLocalRandom random;
        private int alreadyMoved = 0;

        @Override
        public void register(OrganismsGame game, int dna) throws Exception {
            this.game = game;
            this.dna = dna;
            this.random = ThreadLocalRandom.current();
        }

        @Override
        public String name() {
            return "Group4Player2";
        }

        @Override
        public Color color() {
            return new Color(22, 200, 200, 190);
        }

        @Override
        public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                         boolean foodS, boolean foodW, int neighborN, int neighborE,
                         int neighborS, int neighborW) {

            // if there is food around
            Action hasFoodDirection = getHasFoodDirection(foodN, foodE, foodS, foodW);
            if(Objects.nonNull(hasFoodDirection)){
                if(energyLeft > 300){
                    alreadyMoved = 0;
                    return Move.reproduce(hasFoodDirection, random.nextInt(256));
                }else{
                    alreadyMoved = 0;
                    return Move.movement(hasFoodDirection);
                }
            }else{
                // if no food stay
                if (foodHere > 0) {
                    return Move.movement(Action.STAY_PUT);
                }

                // if no food around but energy is high, reproduce
                Action noFoodDirection = getNoFoodDirection(foodN, foodE, foodS, foodW);
                if (energyLeft > 400) {
                    return Move.reproduce(noFoodDirection, random.nextInt(256));
                }

                if(alreadyMoved <= 1){
                    alreadyMoved++;
                    return Move.movement(noFoodDirection);
                }

                if(energyLeft < game.v() * 3){
                    return Move.movement(Action.STAY_PUT);
                }

                return Move.movement(Action.STAY_PUT);
            }
        }

        @Override
        public int externalState() {
            return 1;  // Provide some variability based on initial dna value
        }

        public Action getHasFoodDirection(boolean foodN, boolean foodE, boolean foodS, boolean foodW){
            java.util.List<Action> source = new ArrayList<Action>();
            if(foodN){
                source.add(Action.NORTH);
            }
            if(foodE){
                source.add(Action.EAST);
            }
            if(foodS){
                source.add(Action.SOUTH);
            }
            if(foodW){
                source.add(Action.WEST);
            }
            if(!source.isEmpty()){
                int index = random.nextInt(source.size());
                return source.get(index);
            }
            return null;
        }

        public Action getNoFoodDirection(boolean foodN, boolean foodE, boolean foodS, boolean foodW){
            List<Action> source = new ArrayList<Action>();
            if(!foodN){
                source.add(Action.NORTH);
            }
            if(!foodE){
                source.add(Action.EAST);
            }
            if(!foodS){
                source.add(Action.SOUTH);
            }
            if(!foodW){
                source.add(Action.WEST);
            }
            if(!source.isEmpty()){
                int index = random.nextInt(source.size());
                return source.get(index);
            }
            return null;
        }
    }
}

