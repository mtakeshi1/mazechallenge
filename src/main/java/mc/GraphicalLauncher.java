package mc;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import mc.drawing.GraphicalMain;

import static mc.Configuration.FRAME_RATE;
import static mc.Configuration.WINDOW_HEIGHT;
import static mc.Configuration.WINDOW_WIDTH;

public class GraphicalLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MazeRunner");
        config.setForegroundFPS(FRAME_RATE);
        config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        new Lwjgl3Application(new GraphicalMain(), config);
    }
}
