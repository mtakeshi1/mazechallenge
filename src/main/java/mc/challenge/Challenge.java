package mc.challenge;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze;
import mc.challenge.maze.RunInfo;

/**
 * Basicly implementing this is the whole challenge
 */
public interface Challenge {

    /**
     * Is called after a move, supplies you with the current Line Of Sight of the player.
     *
     * @param los a [13,13] array with 'you' in the middle at [6,6]
     */
    void handleLineOfSightUpdate(Maze.CellType[][] los);

    /**
     * Called at the beginning of every iteration.
     * You must return a {@link Direction} here to tell the program where to go.
     *
     * @return Direction
     */
    Direction getMove();

    default void handleFinish(RunInfo ri) {
        System.out.println(ri);
    }
}
