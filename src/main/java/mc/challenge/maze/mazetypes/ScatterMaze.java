package mc.challenge.maze.mazetypes;

import mc.challenge.maze.ArrayUtil;
import mc.challenge.maze.Maze;

import java.util.Random;

import static mc.challenge.maze.ArrayUtil.FOUR_DIRECTIONS;
import static mc.challenge.maze.mazetypes.EmptyMaze.getEmptyMazeArray;

public class ScatterMaze extends Maze {

//    private final Random rnd = new Random();

    public ScatterMaze(int size) {
        super(getArray(size, size, new Random()));
    }

    public ScatterMaze(int size, long seed) {
        super(getArray(size, size, new Random(seed)));
    }


    private static char[][] getArray(int rows, int cols, Random rnd) {
        var map = getEmptyMazeArray(rows, cols);

        int total = (int) (0.10f * (rows * cols));

        while (total-- > 0) {
            int r = rnd.nextInt(rows - 2) + 1;
            int c = rnd.nextInt(cols - 2) + 1;
            if (map[r][c] == '.') map[r][c] = '#';
        }

        for (int r = 2; r < rows - 2; r++) {
            for (int c = 1; c < cols - 1; c++) {
                if (map[r][c] == '#') {

                    if (map[r + 1][c + 1] == '#') {
                        setWallIfFloor(map, r, c + 1);
                    }

                    if (map[r - 1][c - 1] == '#') {
                        setWallIfFloor(map, r, c + 1);
                    }
                }

            }
        }

        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < cols - 1; c++) {
                clearStartAndFinishPaths(map, r, c);
            }
        }
        return map;
    }

    private static void clearStartAndFinishPaths(char[][] map, int r, int c) {
        if (map[r][c] == '<' || map[r][c] == '>') {
            for (int[] dir : FOUR_DIRECTIONS) {
                for (int i = 1; i <= map.length; i++) {
                    setTileToIfWall(map, r + dir[0] * i, c + dir[1] * i, '.');
                }
            }
        }
    }

    private static void setWallIfFloor(char[][] arr, int r, int c) {
        if (arr[r][c] == '.') arr[r][c] = '#';
    }

    private static boolean setTileToIfWall(char[][] arr, int r, int c, char target) {
        if (ArrayUtil.isInsideOuterWallsMatrix(r, c, arr) && arr[r][c] == '#') {
            arr[r][c] = target;
            return true;
        }
        return false;
    }

    @Override
    public String getMazeType() {
        return "ScatterMaze";
    }
}
