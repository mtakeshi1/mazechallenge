package mc;

import mc.challenge.Challenge;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeFactory;

public class HeadlessLauncher {



    public static void main(String[] args) {
        new HeadlessMain(new ChallengeImpl(), MazeFactory.getEmptyMap(10, 10)).doAllMoves();
    }
}
