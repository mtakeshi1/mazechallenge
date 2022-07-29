package mc.drawing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import mc.Configuration;
import mc.challenge.Challenge;
import mc.challenge.maze.Maze;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.Position;

import java.util.function.Consumer;

import static mc.Configuration.CELL_SIZE;
import static mc.Configuration.OFFSET_X;
import static mc.Configuration.OFFSET_Y;

/**
 * Helps connecting the Graphical runner with the challenge code.
 * - should be merged a bit with the headless one
 */
public class GraphicalHelper {

    private final Maze maze;
    private SpriteBatch batch;
    private Sprite wp;
    private Sprite playerSprite;
    private final Challenge challenge;

    private Thread thread;

    public GraphicalHelper(SpriteBatch batch, Maze maze) {
        this.batch = batch;
        this.challenge = Configuration.challenge.get();
        this.maze = maze;
        wp = new Sprite(new Texture("./data/wp.png"));
        playerSprite = new Sprite(new Texture("./data/player.png"));
        playerSprite.setSize(CELL_SIZE, CELL_SIZE);
        wp.setSize(CELL_SIZE, CELL_SIZE);
        challenge.handleLineOfSightUpdate(maze.getLineOfSight());


        thread = new Thread(() -> {
            long nextUpdate = System.currentTimeMillis() + Configuration.minimumDelayMS;
            while (!maze.isEndReached() && !Thread.interrupted()) {
                if (System.currentTimeMillis() > nextUpdate) {
                    nextUpdate = System.currentTimeMillis() + Configuration.minimumDelayMS;

                    maze.doMove(challenge.getMove());
                    challenge.handleLineOfSightUpdate(maze.getLineOfSight());
                }
            }
        });
        thread.start();

    }

    public void stop() {
        thread.interrupt();
    }

    /**
     * A consumer that can be passed to the maze.
     * A bit of an improvisation to keep the maze decoupled from graphical code.
     * Another solution would probably be better.
     */
    Consumer<CellType[][]> drawmaze = mx -> {


        wp.setColor(Configuration.BORDERCOLOR);
        for (int r = 0; r < mx.length + 2; r++) {
            wp.setPosition((float) OFFSET_X - CELL_SIZE, (float) OFFSET_Y - CELL_SIZE + r * CELL_SIZE);
            wp.draw(batch);
            wp.setPosition((float) OFFSET_X + CELL_SIZE * mx[0].length, (float) OFFSET_Y - CELL_SIZE + r * CELL_SIZE);
            wp.draw(batch);
        }
        for (int c = 0; c < mx[0].length + 2; c++) {
            wp.setPosition((float) OFFSET_Y - CELL_SIZE + c * CELL_SIZE, (float) OFFSET_X - CELL_SIZE);
            wp.draw(batch);
            wp.setPosition((float) OFFSET_Y - CELL_SIZE + c * CELL_SIZE, (float) OFFSET_X + CELL_SIZE * mx.length);
            wp.draw(batch);
        }

        for (int r = 0; r < mx.length; r++) {
            for (int c = 0; c < mx[0].length; c++) {
                wp.setPosition((float) OFFSET_X + c * CELL_SIZE, (float) OFFSET_Y + r * CELL_SIZE);
                switch (mx[r][c]) {
                    case WLL -> wp.setColor(Configuration.WALLCOLOR);
                    case FLR -> wp.setColor(Configuration.FLOORCOLOR);
                    case SRT -> wp.setColor(Configuration.STARTCOLOR);
                    case FSH -> wp.setColor(Configuration.FINISHCOLOR);
                    case UNK -> wp.setColor(Configuration.UNKNOWNCOLOR);
                }
                wp.draw(batch);
                wp.setColor(Color.BLACK);
                wp.setAlpha(.5f);
                wp.draw(batch);
                wp.setAlpha(1f);
            }
        }


    };

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            long nextUpdate = System.currentTimeMillis() + Configuration.minimumDelayMS;

            @Override
            public void run() {
                while (true) {
                    if (System.currentTimeMillis() > nextUpdate) {
                        nextUpdate = System.currentTimeMillis() + Configuration.minimumDelayMS;
                        System.out.println(System.currentTimeMillis());
//                        maze.doMove(challenge.getMove());
//                        challenge.handleLineOfSightUpdate(maze.getLineOfSight());
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);

        thread.start();
    }

    Consumer<Position> drawplayer = p -> {
        playerSprite.setPosition(
                (float) p.col() * CELL_SIZE + OFFSET_X,
                (float) p.row() * CELL_SIZE + OFFSET_Y
        );
        playerSprite.draw(batch);
    };

    public void draw() {
        maze.drawMaze(drawmaze);
        maze.drawPlayer(drawplayer);
    }

    public float getPlayerx() {
        return playerSprite.getX();
    }

    public float getPlayery() {
        return playerSprite.getY();
    }

    public void doMove() {
        maze.doMove(challenge.getMove());
    }

    public boolean finished() {
        return maze.isEndReached();
    }

    public void update() {
        challenge.handleLineOfSightUpdate(maze.getLineOfSight());
    }
}
