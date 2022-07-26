package mc;

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

    /**
     * This method will be called each iteration by the program.
     * - GraphicalLauncher for visual
     * - HeadlessLauncher for non-visual
     */
    public Direction getMove() {


        var vis = maze.getLos();
        maze.getLos();


        for (var m : vis) {
            System.out.println(Arrays.toString(m));
        }
        System.out.println();


//        for (int x = 1; x < 200; x++) {
//            for (int y = 1; y < x; y++) {
//                maze.doMove(Direction.EAST);
//            }
//
//            for (int y = 1; y < x; y++) {
//                maze.doMove(Direction.SOUTH);
//            }
//            for (int y = 1; y < x; y++) {
//                maze.doMove(Direction.WEST);
//            }
//
//            for (int y = 1; y < x; y++) {
//                maze.doMove(Direction.NORTH);
//            }
//        }

        return switch (rnd.nextInt(4)) {

            case 0 -> Direction.EAST;

            case 1 -> Direction.WEST;

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
