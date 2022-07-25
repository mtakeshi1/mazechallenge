package mc;

import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze;

import java.util.Random;

import static mc.challenge.Helper.updownlst;

public class ChallengeImpl implements Challenge {

    private static final Random rnd = new Random();
    private Maze maze;

    @Override
    public void setMap(Maze maze) {
        this.maze = maze;
    }


    int counter = 0;

    /**
     * This method will be called each iteration by the program.
     * - GraphicalLauncher for visual
     * - HeadlessLauncher for non-visual
     */
    public Direction getMove() {
        return switch (rnd.nextInt(4)) {

            case 0 -> Direction.EAST;

            case 1 ->  Direction.WEST;

            case 2 -> Direction.NORTH;

            case 3 -> Direction.SOUTH;

            default -> throw new IllegalStateException("Unexpected value: " + rnd.nextInt(4));
        };
//        Maze.CellType[][] cellTypes = maze.visitedMatrix();
//        Position playerPosition = maze.getPlayerPosition();
//        int stepsTaken = maze.getStepsTaken();

//        return updownlst.get(counter++ % updownlst.size());
    }

}
