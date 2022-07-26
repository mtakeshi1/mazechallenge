package mc.myname_renamethis;

import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.IMaze;

import java.util.Arrays;
import java.util.Random;

public class ChallengeImpl implements Challenge {

    private static final Random rnd = new Random();
    private IMaze maze;

    @Override
    public void setMap(IMaze maze) {
        this.maze = maze;
    }


    int counter = 1;
    int counter2 = 1;
    int index = 0;

    Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    /**
     * This method will be called each iteration by the program.
     * - GraphicalLauncher for visual
     * - HeadlessLauncher for non-visual
     */
    public Direction getMove() {


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

        maze.getLineOfSight();

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
