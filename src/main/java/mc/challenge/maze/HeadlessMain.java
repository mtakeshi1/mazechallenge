package mc.challenge.maze;

import mc.challenge.Challenge;

/**
 * Runner for the challenge ( won't be used for grading )
 */
public class HeadlessMain {

    private final Challenge challenge;
    private final Maze maze;

    public HeadlessMain(Challenge challenge, Maze maze) {

        if (challenge == null) throw new IllegalArgumentException("challenge may not be null");
        if (maze == null) throw new IllegalArgumentException("maze may not be null");

        this.challenge = challenge;
        this.maze = maze;
    }

    public void doAllMoves() {
        while (!maze.isEndReached()) {
            maze.doMove(challenge.getMove());
            challenge.handleLineOfSightUpdate(maze.getLineOfSight());

            if (maze.getStepsTaken() > 10000000) {
                throw new RuntimeException("Sorry, too many steps");
            }
        }

        System.out.println("solved in steps: " + maze.getStepsTaken());
    }
}
