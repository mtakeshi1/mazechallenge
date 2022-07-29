package mc.challenge.maze.mazetypes;

import mc.challenge.maze.Maze;

import java.util.Random;

import static mc.challenge.maze.mazetypes.MazeUtil.fillMatrixWithFloor;
import static squidpony.squidgrid.mapping.DungeonUtility.wallWrap;

public class EmptyMaze extends Maze {

    private static Random rnd = new Random();

    public EmptyMaze(int size) {
        super(getEmptyMazeArray(size, size));

    }

    static char[][] getEmptyMazeArray(int rows, int cols) {
        if (rows < 5) throw new IllegalArgumentException("rows must be >= 5");
        if (cols < 5) throw new IllegalArgumentException("cols must be >= 5");

        var mx = new char[rows][cols];
        fillMatrixWithFloor(mx);
        wallWrap(mx);

        mx[rnd.nextInt((rows / 2) - 2) + 1][rnd.nextInt((cols / 2) - 2) + 1] = '<';
        mx[rnd.nextInt((rows / 2) - 2) + rows / 2][rnd.nextInt((cols / 2) - 2) + cols / 2] = '>';
        return mx;
    }


    public static void main(String[] args) {
        int x = 10;
        while (x-- > 0) {
            var m = new EmptyMaze(30);
        }
    }

    @Override
    public String getMazeType() {
     return "EmptyMaze";
    }

}
