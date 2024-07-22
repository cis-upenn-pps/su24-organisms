
package organisms.g5;
import org.junit.Before;
import org.junit.Test;
import organisms.ui.OrganismsGame;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;

    public class costMoveTest {

        OrganismsGame game;
        Group5Player player;
        int exist;
        int exert;
        int eat;

        @Before
        public void setUp() throws Exception {

            eat = 10;
            exert = 2;
            exist = 1;

            //MADE ORG GAME NOT FINAL - NEED TO CHANGE BACK
            game = new OrganismsGame() {
                @Override
                public int s() {
                    return 1;
                }

                @Override
                public int u() {
                    return 10;
                }

                @Override
                public int v() {
                    return 2;
                }
            };
            player = new Group5Player();
            player.register(game, 0);
        }
        @Test
        public void reproduceGoodOccupant() {
            assertEquals(-exert, player.costMove(false, Group5Player.OCCUPANT.empty, true));

        }
        @Test
        public void reproduceIrrelevantOccupant() {
            assertEquals(-exert, player.costMove(false, Group5Player.OCCUPANT.food, true));

        }
        @Test
        public void reproduceBadOccupant() {
            assertThrows(IllegalArgumentException.class, () -> player.costMove(false, Group5Player.OCCUPANT.other_organism, true));

        }
        @Test
        public void reproduceIllegalAttemptToMove() {

            assertThrows(IllegalArgumentException.class, () -> player.costMove(true, Group5Player.OCCUPANT.empty, true));
            assertThrows(IllegalArgumentException.class, () -> player.costMove(true, Group5Player.OCCUPANT.other_organism, true));
            assertThrows(IllegalArgumentException.class, () -> player.costMove(true, Group5Player.OCCUPANT.food, true));

        }
        @Test
        public void moveToEmptySquare() {
            assertEquals(-exert, player.costMove(true, Group5Player.OCCUPANT.empty, false));


        }
        @Test
        public void moveOntoOtherOrganism() {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(stream));
            assertEquals(-exert, player.costMove(true, Group5Player.OCCUPANT.other_organism, false));
            assertEquals("This move has poor efficiency! Please consider remain.", stream.toString().trim());

        }
        @Test
        public void costIntoFoodSquare() {
            assertEquals((eat - exert), player.costMove(true, Group5Player.OCCUPANT.food, false));


        }
        @Test
        public void stayNoFood() {

            assertEquals(-exist, player.costMove(false, Group5Player.OCCUPANT.empty, false));

        }
        @Test
        public void stayWithFood() {

            assertEquals(eat - exist, player.costMove(false, Group5Player.OCCUPANT.food, false));

        }
        @Test
        public void stayErrorCurrentSquare() {
            assertThrows(IllegalArgumentException.class, () -> player.costMove(false, Group5Player.OCCUPANT.other_organism, false));
        }
    }

