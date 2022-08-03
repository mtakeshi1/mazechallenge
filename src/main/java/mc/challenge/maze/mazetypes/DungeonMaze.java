package mc.challenge.maze.mazetypes;

import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeParser;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.IRNG;

public class DungeonMaze extends Maze {

    public DungeonMaze(int size) {
        super(getDungeonArray(size, size));
    }

    public DungeonMaze(char[][] arr) {
        super(arr);
    }

    public DungeonMaze(String filename) {
        super(MazeParser.fileToMatrix(filename));
    }

    public static char[][] getDungeonArray(int rows, int cols) {
        return getDungeonArray(rows, cols, new GWTRNG());
    }

    public static char[][] getDungeonArray(int rows, int cols, IRNG rng) {
        DungeonGenerator dg = new DungeonGenerator(rows, cols, rng);
        dg.addStairs();
        char[][] generated = dg.generate();
        return generated;
    }

    @Override
    public String getMazeType() {
        return "DungeonMaze";
    }
}
