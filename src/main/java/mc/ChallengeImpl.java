package mc;

import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.IMaze;

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


        var vis = maze.getLos();
        maze.getLos();

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
