package mc.challenge.maze.mazetypes;

import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeParser;
import squidpony.squidgrid.mapping.DungeonGenerator;

public class DungeonMaze extends Maze {

    public DungeonMaze(int size) {
        super(getDungeonArray(size, size));
    }

    public DungeonMaze(String filename) {
        super(MazeParser.fileToMatrix(filename));
    }

    private static char[][] getDungeonArray(int rows, int cols) {
        DungeonGenerator dg = new DungeonGenerator(rows, cols);
        dg.addStairs();
        char[][] generated = dg.generate();
        return generated;
    }

    @Override
    public String getMazeType() {
        return "DungeonMaze";
    }
}
