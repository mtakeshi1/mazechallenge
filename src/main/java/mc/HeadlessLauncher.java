package mc;

import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.MazeFactory;

public class HeadlessLauncher {
    public static void main(String[] args) {
        new HeadlessMain(new ChallengeImpl(), MazeFactory.getEmptyMap(10, 10)).doAllMoves();

        new HeadlessMain(new ChallengeImpl(), MazeFactory.getEmptyMap(100, 100)).doAllMoves();


        new HeadlessMain(new ChallengeImpl(), MazeFactory.getScatterMap(10, 10)).doAllMoves();

    }
}
