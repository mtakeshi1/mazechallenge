package mc.challenge.maze.mazetypes;

import mc.challenge.maze.Maze.CellType;

import static mc.challenge.maze.Maze.CellType.FLR;
import static mc.challenge.maze.Maze.CellType.WLL;

public class MazeUtil {

    private MazeUtil() {
        throw new RuntimeException("may not instantiate this class");
    }

    public static void fillMatrixWithFloor(CellType[][] matrix) {
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                matrix[r][c] = FLR;
            }
        }
    }

    public static void fillMatrixWithFloor(char[][] matrix) {
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                matrix[r][c] = '.';
            }
        }
    }


    public static void wallEdges(CellType[][] matrix) {
        for (int r = 0; r < matrix.length; r++) {
            matrix[r][0] = WLL;
            matrix[r][matrix[0].length - 1] = WLL;
        }
        for (int c = 0; c < matrix[0].length; c++) {
            matrix[0][c] = WLL;
            matrix[matrix.length - 1][c] = WLL;
        }
    }


}
