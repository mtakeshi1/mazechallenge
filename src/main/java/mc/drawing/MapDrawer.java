package mc.drawing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import mc.challenge.maze.Maze;

public class MapDrawer {

    private final Maze map;

    private static final int CELL_SIZE = 16;
    private static final int OFFSETX = 100;
    private static final int OFFSETY = 100;
    private final Sprite wp;
    private final Sprite player;

    public MapDrawer(Maze map) {
        this.map = map;
        wp = new Sprite(new Texture("./data/wp.png"));
        player = new Sprite(new Texture("./data/player.png"));
        player.setSize(CELL_SIZE, CELL_SIZE);
//        player.setPosition(map.);
        wp.setSize(CELL_SIZE, CELL_SIZE);

    }


    public void draw(SpriteBatch batch) {
        var matrix = map.visitedMatrix();


        wp.setColor(Color.WHITE);
        for (int r = 0; r < matrix.length + 2; r++) {
            wp.setPosition(OFFSETX - CELL_SIZE, OFFSETY - CELL_SIZE + r * CELL_SIZE);
            wp.draw(batch);
            wp.setPosition(OFFSETX + CELL_SIZE * matrix[0].length, OFFSETY - CELL_SIZE + r * CELL_SIZE);
            wp.draw(batch);
        }
        for (int c = 0; c < matrix[0].length + 2; c++) {
            wp.setPosition(OFFSETY - CELL_SIZE + c * CELL_SIZE, OFFSETX - CELL_SIZE);
            wp.draw(batch);
            wp.setPosition(OFFSETY - CELL_SIZE + c * CELL_SIZE, OFFSETX + CELL_SIZE * matrix.length);
            wp.draw(batch);
        }

        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                wp.setPosition(OFFSETX + c * CELL_SIZE, OFFSETY + r * CELL_SIZE);
                switch (matrix[r][c]) {
                    case WALL -> wp.setColor(Color.BROWN);
                    case FLOOR -> wp.setColor(Color.SALMON);
                    case START -> wp.setColor(Color.RED);
                    case FINISH -> wp.setColor(Color.GREEN);
                    case UNKNOWN -> wp.setColor(Color.BLACK);
                }
                wp.draw(batch);
            }
        }


        player.setPosition(
                map.getPlayerPosition().col() * CELL_SIZE + OFFSETX,
                map.getPlayerPosition().row() * CELL_SIZE + OFFSETY
        );
        player.draw(batch);

    }

    public Vector2 getPlayerDrawPosition() {
        return new Vector2(
                player.getX(),
                player.getY()
        );
    }

}
