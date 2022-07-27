package mc.renamebeforepr;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.MazeFactory;

import java.util.Arrays;

/**
 * The whole challenge can be completed by just adding to this file.
 * You can create other java files in the same package though.
 * <p>
 * Run this:
 * Graphically -> {@link mc.GraphicalLauncher} ( for debug purposes )
 * Non graphically ->  {@link mc.HeadlessLauncher}
 * <p>
 * Configure and choose what type of mazes are ran : {@link mc.Configuration}
 */
public class ChallengeImpl implements Challenge {

    private int counter = 1;
    private int counter2 = 1;
    private int index = 0;
    private final Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    // feel free to delete every field above and implement something usefull


    /**
     * This method will be called on init and after each move.
     *
     * @param los [13,13] array where 'you' always are at [6,6] ( further explained in printLOSUpdate
     */
    @Override
    public void handleLineOfSightUpdate(CellType[][] los) {
//        printLOSUpdate(los); // Uncomment this for debug printing the array
    }

    /**
     * This method will be called before each move.
     * Here you must supply the program with a 'Direction', an enum that can be: NORTH, SOUTH, EAST or WEST
     * <p>
     * 'You' will walk in that direction unless there is a wall there.
     */
    @Override
    public Direction getMove() {

        // this is just here so that the player will move a bit
        if (counter2 == 0) {
            index++;
            index %= 4;
            counter++;
            counter2 = counter;
        }
        counter2--;
        return dirs[index];
    }


    //I put a convenience launcher here in case you want to run a single maze headless.
    public static void main(String[] args) {
        new HeadlessMain(new ChallengeImpl(),
//                MazeFactory.getFlowingCave(
//                MazeFactory.get1WMap(
                MazeFactory.getDungeon(
                        Configuration.SMALL, Configuration.SMALL
//                        Configuration.MEDIUM, Configuration.MEDIUM
//                        Configuration.LARGE, Configuration.LARGE
//                        Configuration.HUGE, Configuration.HUGE
                )
        ).doAllMoves();
    }


    //Just here to show explain how the Line Of Sight array is build.
    private static void printLOSUpdate(CellType[][] los) {
        // [13,13] Line of sight array printed with 'you' in the middle at: [6,6]
        // UNK = unknown
        // WLL = wall
        // FLR = floor
        // SRT = start
        // FSH = finish
        System.out.println();
        for (var v : los) {
            System.out.println(Arrays.toString(v));
        }
        System.out.println();
    }
}
