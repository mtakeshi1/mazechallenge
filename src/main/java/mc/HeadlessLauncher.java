package mc;

import mc.challenge.Challenge;
import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeFactory;

public class HeadlessLauncher {


    private final Challenge challenge;
    private final Maze maze;

    public HeadlessLauncher(Challenge challenge, Maze maze) {

        if (challenge == null) throw new IllegalArgumentException("challenge may not be null");
        if (maze == null) throw new IllegalArgumentException("maze may not be null");

        this.challenge = challenge;
        this.maze = maze;
        challenge.setMap(maze);
    }


    /**
     * return true is moved, false if it was a wall
     */
    public boolean doMove() {
        return maze.doMove(challenge.getMove());
    }

    void doAllMoves() {
        while (!maze.isEndReached()) {
            maze.doMove(challenge.getMove());

            if (maze.getStepsTaken() > 1000000) {
                throw new RuntimeException("Sorry, too many steps");
            }
        }

        System.out.println("solved in steps: " + maze.getStepsTaken());
    }

    public static void main(String[] args) {
        new HeadlessLauncher(new ChallengeImpl(), MazeFactory.getEmptyMap(10, 10)).doAllMoves();
    }
}
