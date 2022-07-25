package mc.challenge.maze;

import java.util.Random;

import static mc.challenge.maze.Maze.CellType.WALL;

public class MazeFactory {

    private static final Random rnd = new Random();


    public static Maze getEmptyMap(int rows, int cols) {
        var map = new Maze(rows, cols);

        return map;
    }


    public static Maze getScatterMap(int rows, int cols) {
        var map = new Maze(rows, cols);

        int total = (int) (0.10f * (rows * cols));

        while (total-- > 0) {
            Position p = new Position(rnd.nextInt(rows - 2) + 1, rnd.nextInt(cols - 2) + 1);
            map.setTile(p, WALL);
        }

        for (int r = 2; r < rows - 2; r++) {
            for (int c = 1; c < cols - 1; c++) {
                if (map.getTile(r, c) == WALL) {
                    if (map.getTile(r + 1, c + 1) == WALL) {
                        map.setTile(r, c + 1, WALL);
                    }

                    if (map.getTile(r - 1, c + 1) == WALL) {
                        map.setTile(r, c + 1, WALL);
                    }

                }
            }
        }

        return map;
    }
}
