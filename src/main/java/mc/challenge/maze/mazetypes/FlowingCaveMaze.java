package mc.challenge.maze.mazetypes;

import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeParser;
import squidpony.squidgrid.mapping.FlowingCaveGenerator;
import squidpony.squidgrid.mapping.IDungeonGenerator;

import java.util.Random;

import static mc.challenge.maze.ArrayUtil.FOUR_DIRECTIONS;
import static mc.challenge.maze.ArrayUtil.invertrows;
import static mc.challenge.maze.ArrayUtil.rotate;

public class FlowingCaveMaze extends Maze {

    private static final Random rnd = new Random();

    public FlowingCaveMaze(int size) {
        super(getArray(size, size));
    }

    public FlowingCaveMaze(String filename) {
        super(MazeParser.fileToMatrix(filename));
    }


    private static char[][] getArray(int rows, int cols) {
        IDungeonGenerator gen = new FlowingCaveGenerator(rows, cols);
        char[][] generated = gen.generate();


        setStart(generated);
        setEnd(generated);

        if (rnd.nextBoolean()) {
            invertrows(generated);
        }
        if (rnd.nextBoolean()) {
            rotate(generated);
        }
        if (rnd.nextBoolean()) {
            rotate(generated);
        }
        if (rnd.nextBoolean()) {
            invertrows(generated);
        }
        if (rnd.nextBoolean()) {
            invertrows(generated);
        }
        if (rnd.nextBoolean()) {
            rotate(generated);
        }
        if (rnd.nextBoolean()) {
            rotate(generated);
        }
        if (rnd.nextBoolean()) {
            invertrows(generated);
        }
        return generated;
    }

    /**
     * Crude hack, should replace with dijkstra solution at some point.
     */
    public static void setStart(char[][] mx) {
        for (int r = 1; r < 2000; r++) {
            if (mx[r][r] == '.') {
                int count = 0;

                for (var direction : FOUR_DIRECTIONS) {
                    if (mx[r + direction[0]][r + direction[1]] == '.') {
                        count++;
                    }
                }

                if (count >= 3) {
                    mx[r][r] = '<';
                    return;
                }
            }
        }
    }

    /**
     * Crude hack, should replace with dijkstra solution at some point.
     */
    public static void setEnd(char[][] mx) {
        for (int r = 2; r < 2000; r++) {
            if (mx[mx.length - r][mx[0].length - r] == '.') {
                int count = 0;

                for (var direction : FOUR_DIRECTIONS) {
                    if (mx[r + direction[0]][r + direction[1]] == '.') {
                        count++;
                    }
                }

                if (count >= 3) {
                    mx[mx.length - r][mx[0].length - r] = '>';
                    return;
                }
            }
        }
    }
}
