package mc;

import mc.challenge.maze.HeadlessMain;

/**
 * Runs the mazes configured in {@link Configuration} headless
 * <p>
 * Write the code in : {@link mc.renamebeforepr.ChallengeImpl}
 */
public class HeadlessLauncher {

    /**
     * Run all configured mazes.
     */
    public static void main(String[] args) {
        for (var maze : Configuration.MAZES) {
            new HeadlessMain(Configuration.challenge.get(), maze.get()).doAllMoves();
        }
    }
}
