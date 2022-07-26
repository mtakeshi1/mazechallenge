package mc;

import mc.challenge.maze.HeadlessMain;

public class HeadlessLauncher {
    public static void main(String[] args) {
        for (var maze : Configuration.mazes) {
            new HeadlessMain(new ChallengeImpl(), maze.get()).doAllMoves();
        }
    }
}
