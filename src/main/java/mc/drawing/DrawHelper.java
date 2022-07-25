package mc.drawing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import mc.challenge.maze.Maze;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.Position;

import java.util.function.Consumer;

public class DrawHelper {

    private Maze maze;

    public void draw() {
        maze.drawMaze(drawmaze);
        maze.drawPlayer(drawplayer);
    }

    public DrawHelper(
            SpriteBatch batch,
            int cell_size,
            int offsetx,
            int offsety,
            Maze maze
    ) {
        this.batch = batch;
        this.cell_size = cell_size;
        this.offsetx = offsetx;
        this.offsety = offsety;


        this.maze = maze;
        wp = new Sprite(new Texture("./data/wp.png"));
        player = new Sprite(new Texture("./data/player.png"));
        player.setSize(cell_size, cell_size);
        wp.setSize(cell_size, cell_size);

    }

    private SpriteBatch batch;
    private int cell_size;
    private int offsetx;
    private int offsety;
    private Sprite wp;
    private Sprite player;

    Consumer<CellType[][]> drawmaze = (matrix) -> {


        wp.setColor(Color.WHITE);
        for (int r = 0; r < matrix.length + 2; r++) {
            wp.setPosition(offsetx - cell_size, offsety - cell_size + r * cell_size);
            wp.draw(batch);
            wp.setPosition(offsetx + cell_size * matrix[0].length, offsety - cell_size + r * cell_size);
            wp.draw(batch);
        }
        for (int c = 0; c < matrix[0].length + 2; c++) {
            wp.setPosition(offsety - cell_size + c * cell_size, offsetx - cell_size);
            wp.draw(batch);
            wp.setPosition(offsety - cell_size + c * cell_size, offsetx + cell_size * matrix.length);
            wp.draw(batch);
        }

        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                wp.setPosition(offsetx + c * cell_size, offsety + r * cell_size);
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


    };

    Consumer<Position> drawplayer = (p) -> {
        player.setPosition(
                p.col() * cell_size + offsetx,
                p.row() * cell_size + offsety
        );
        player.draw(batch);
    };

    public float getPlayerx() {
        return player.getX();
    }

    public float getPlayery() {
        return player.getY();
    }
}
