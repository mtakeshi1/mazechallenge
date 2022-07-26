package mc.myname_renamethis;

import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.IMaze;

import java.util.Arrays;
import java.util.Random;

public class ChallengeImpl implements Challenge {

    private static final Random rnd = new Random(); // not important
    private IMaze maze; // must be set by method beneath

    @Override
    public void setMap(IMaze maze) {
        this.maze = maze;
    } // will be called right after map creation to give you access.


    int counter = 1; // not important
    int counter2 = 1; // not important
    int index = 0; // not important

    Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}; // not important

    /**
     * This method will be called each iteration by the program.
     * - GraphicalLauncher for visual
     * - HeadlessLauncher for non-visual
     */
    public Direction getMove() {

        // this should contain all actual information you may use
        var los = maze.getLineOfSight();

        // Line of sight printed with 'you' in the middle at: [6,6]
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

        // not important, circles the player ( wont solve many )
        if (counter2 == 0) {
            index++;
            index %= 4;
            counter++;
            counter2 = counter;
        }
        counter2--;
        return dirs[index];
    }

}
