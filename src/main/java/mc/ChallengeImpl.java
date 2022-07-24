package mc;

import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze;

import static mc.challenge.Helper.updownlst;

public class ChallengeImpl implements Challenge {

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
        return updownlst.get(counter++ % updownlst.size());
    }

}
