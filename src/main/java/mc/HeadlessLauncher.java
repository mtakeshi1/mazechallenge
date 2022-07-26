package mc;

import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.MazeFactory;

public class HeadlessLauncher {
    public static void main(String[] args) {
        System.out.println("Start 10x10 empty");
        new HeadlessMain(new ChallengeImpl(), MazeFactory.getEmptyMap(10, 10)).doAllMoves();

        System.out.println("Start 100x100 empty");
        new HeadlessMain(new ChallengeImpl(), MazeFactory.getEmptyMap(100, 100)).doAllMoves();

        System.out.println("Start 10x10 scattered");
        new HeadlessMain(new ChallengeImpl(), MazeFactory.getScatterMap(10, 10)).doAllMoves();

        System.out.println("Start 100x100 scattered");
        new HeadlessMain(new ChallengeImpl(), MazeFactory.getScatterMap(100, 100)).doAllMoves();

    }
}
