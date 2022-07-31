package mc.challenge.maze.mazetypes;

import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeParser;
import squidpony.squidgrid.mapping.ConnectingMapGenerator;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.IDungeonGenerator;
import squidpony.squidmath.RNG;

public class Width1Maze extends Maze {


    public Width1Maze(int size) {
        super(get1WArray(size, size));
    }

    public Width1Maze(String filename) {
        super(MazeParser.fileToMatrix(filename));
    }


    private static char[][] get1WArray(int rows, int cols) {
        IDungeonGenerator gen = new ConnectingMapGenerator(rows, cols, 1, 1, new RNG(), 1, 0.5);
        char[][] generated = gen.generate();
        DungeonGenerator dg = new DungeonGenerator(20, 20);
        dg.addStairs();
        generated = dg.generate(generated);
        return generated;
    }

    @Override
    public String getMazeType() {
        return "Width1Maze";
    }
}
