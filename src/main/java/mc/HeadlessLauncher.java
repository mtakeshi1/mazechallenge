package mc;

import mc.challenge.maze.HeadlessMain;
import mc.renamebeforepr.ChallengeImpl;

/**
 * Runs the mazes configured in {@link Configuration} headless
 *
 * Write the code in : {@link mc.renamebeforepr.ChallengeImpl}
 */
public class HeadlessLauncher {

    /**
     * Run all configured mazes.
     */
    public static void main(String[] args) {

        for (var maze : Configuration.MAZES) {
            new HeadlessMain(new ChallengeImpl(), maze.get()).doAllMoves();
        }
    }
}
