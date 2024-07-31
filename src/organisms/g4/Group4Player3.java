package organisms.g4;

import organisms.Move;
import organisms.OrganismsPlayer;
import organisms.ui.OrganismsGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Group4Player3 implements OrganismsPlayer {
    private OrganismsGame game;
    private int dna;
    private ThreadLocalRandom random;
    private int alreadyMoved = 0;
    private Pair pos;
    private List<Pair> visitedPos = new ArrayList<Pair>();

    @Override
    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();
        this.pos = new Pair(0, 0);

    }

    @Override
    public String name() {
        return "Group4Player";
    }

    @Override
    public Color color() {
        return new Color(215, 90, 100, 250);
    }

    @Override
    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {
        if(visitedPos.size() > 15){
            // remove the earliest location
            visitedPos.remove(0);
        }
        visitedPos.add(pos);

        // if there is food around
        Action hasFoodDirection = getHasFoodDirection(foodN, foodE, foodS, foodW, neighborN, neighborE, neighborS, neighborW);
        if(Objects.nonNull(hasFoodDirection)){
            if(energyLeft > game.M() * 0.60){
                int childKey = random.nextInt(256);
                return Move.reproduce(hasFoodDirection, childKey);
            }else{
                alreadyMoved = 0;
                if(hasFoodDirection == Action.NORTH) {
                    this.pos = new Pair(pos.x, pos.y - 1);
                }else if(hasFoodDirection == Action.EAST){
                    this.pos = new Pair(pos.x + 1, pos.y);
                }else if(hasFoodDirection == Action.SOUTH){
                    this.pos = new Pair(pos.x, pos.y + 1);
                }else if(hasFoodDirection == Action.WEST){
                    this.pos = new Pair(pos.x - 1, pos.y);
                }
                return Move.movement(hasFoodDirection);
            }
        }else{
            // if current square has food, stay
            if (foodHere > 0 && energyLeft < game.M() - game.u() ) {
                return Move.movement(Action.STAY_PUT);
            }


            // if no food around but energy is high, reproduce
            Action noFoodDirection = getNoFoodDirection(foodN, foodE, foodS, foodW, neighborN, neighborE, neighborS, neighborW);

            if(Objects.isNull(noFoodDirection)){
                return Move.movement(Action.STAY_PUT);
            }

            if (energyLeft > game.M() * 0.80) {
                int childKey = random.nextInt(256);
                return Move.reproduce(noFoodDirection, childKey);
            }


            if(energyLeft < game.v() * 4){
                return Move.movement(Action.STAY_PUT);
            }

            if(alreadyMoved <= 1){
                alreadyMoved++;
                if(noFoodDirection == Action.NORTH) {
                    this.pos = new Pair(pos.x, pos.y - 1);
                }else if(noFoodDirection == Action.EAST){
                    this.pos = new Pair(pos.x + 1, pos.y);
                }else if(noFoodDirection == Action.SOUTH){
                    this.pos = new Pair(pos.x, pos.y + 1);
                }else if(noFoodDirection == Action.WEST){
                    this.pos = new Pair(pos.x - 1, pos.y);
                }

                return Move.movement(noFoodDirection);
            }


            return Move.movement(Action.STAY_PUT);
        }
    }
    @Override
    public int externalState() {
        return 88;
    }

    private String printVisitedPos(){
        StringBuffer sb = new StringBuffer();
        for(Pair p : visitedPos){
            sb.append("[").append(p.x).append(", ").append(p.y).append("]");
        }
        return sb.toString();
    }

    public Action getHasFoodDirection(boolean foodN, boolean foodE, boolean foodS, boolean foodW, int neighborN, int neighborE,
                                      int neighborS, int neighborW){
        List<Action> source = new ArrayList<Action>();
        if(foodN && neighborN == -1){
            source.add(Action.NORTH);
        }
        if(foodE && neighborE == -1){
            source.add(Action.EAST);
        }
        if(foodS && neighborS == -1){
            source.add(Action.SOUTH);
        }
        if(foodW && neighborW == -1){
            source.add(Action.WEST);
        }
        if(!source.isEmpty()){
            int index = random.nextInt(source.size());
            return source.get(index);
        }
        return null;
    }

    public Action getNoFoodDirection(boolean foodN, boolean foodE, boolean foodS, boolean foodW,int neighborN, int neighborE,
                                     int neighborS, int neighborW){
        List<Action> source = new ArrayList<Action>();
        if(!foodN && neighborN == -1){
            Pair north = new Pair(pos.x, pos.y - 1);
            if(!visitedPos.contains(north)){
                source.add(Action.NORTH);
            }
        }
        if(!foodE && neighborE == -1){
            Pair east = new Pair(pos.x + 1, pos.y);
            if(!visitedPos.contains(east)){
                source.add(Action.EAST);
            }
        }
        if(!foodS && neighborS == -1){
            Pair south = new Pair(pos.x, pos.y + 1);
            if(!visitedPos.contains(south)){
                source.add(Action.SOUTH);
            }
        }
        if(!foodW && neighborW == -1){
            Pair west = new Pair(pos.x - 1, pos.y);
            if(!visitedPos.contains(west)){
                source.add(Action.WEST);
            }
        }
        if(!source.isEmpty()){
            int index = random.nextInt(source.size());
            return source.get(index);
        }
        return null;
    }

    static private class Pair{
        public int x;
        public int y;
        public Pair(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Pair){
                return this.x == ((Pair)obj).x && this.y == ((Pair)obj).y;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.x * 31 + this.y) % 100000;
        }
    }

}
