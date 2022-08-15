package mc;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import mc.drawing.GraphicalMain;
import mc.participants.everyos.EveryOSChallenge;

import static mc.Configuration.WINDOW_HEIGHT;
import static mc.Configuration.WINDOW_WIDTH;

/**
 * Just here to start a display for debugging purposes
 * <p>
 * Runs the mazes configured in {@link Configuration}
 * <p>
 * Write the code in : {@link EveryOSChallenge}
 */
public class GraphicalLauncher {

    /**
     * Start the graphical setup.
     * The graphical setup then starts the mazes
     */
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MazeRunner");
        config.setForegroundFPS(100);
        config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        new Lwjgl3Application(new GraphicalMain(), config);
    }
}
