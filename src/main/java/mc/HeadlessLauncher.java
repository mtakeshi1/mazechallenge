package mc;

import mc.challenge.maze.HeadlessMain;
import mc.everyos.EveryOSChallenge;
import mc.renamebeforepr.ChallengeImpl;

/**
 * Runs the mazes configured in {@link Configuration} headless
 *
 * Write the code in : {@link EveryOSChallenge}
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
