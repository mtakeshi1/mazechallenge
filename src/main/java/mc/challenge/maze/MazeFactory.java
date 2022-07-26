package mc.challenge.maze;

import mc.Configuration;
import squidpony.squidgrid.mapping.ConnectingMapGenerator;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidgrid.mapping.FlowingCaveGenerator;
import squidpony.squidgrid.mapping.IDungeonGenerator;
import squidpony.squidmath.RNG;

import java.util.Random;

import static mc.challenge.maze.Maze.CellType.WLL;

public class MazeFactory {

    private static final Random rnd = new Random();


    public static Maze getEmptyMap(int rows, int cols) {
        var map = new Maze(rows, cols);
        return print(rows, cols, map);
    }

    private static Maze print(int rows, int cols, Maze map) {
        if (Configuration.PRINT_MAZE_CLI) {
            System.out.println();
            System.out.println("---------------------------------------");
            for (int r = rows - 1; r >= 0; r--) {
                System.out.println();
                for (int c = 0; c < cols; c++) {
                    switch (map.getTile(r, c)) {
                        case WLL -> System.out.print('#');
                        case FLR -> System.out.print('.');
                        case SRT -> System.out.print('<');
                        case FSH -> System.out.print('>');
                        case UNK -> System.out.print('?');
                    }
                }
            }
            System.out.println();

            System.out.println();
        }
        return map;
    }


    public static Maze getScatterMap(int rows, int cols) {
        var map = new Maze(rows, cols);

        int total = (int) (0.10f * (rows * cols));

        while (total-- > 0) {
            Position p = new Position(rnd.nextInt(rows - 2) + 1, rnd.nextInt(cols - 2) + 1);
            map.setTile(p, WLL);
        }

        for (int r = 2; r < rows - 2; r++) {
            for (int c = 1; c < cols - 1; c++) {
                if (map.getTile(r, c) == WLL) {
                    if (map.getTile(r + 1, c + 1) == WLL) {
                        map.setTile(r, c + 1, WLL);
                    }

                    if (map.getTile(r - 1, c + 1) == WLL) {
                        map.setTile(r, c + 1, WLL);
                    }

                }
            }
        }

        return print(rows, cols, map);
    }

    public static Maze get1WMap(int rows, int cols) {
        IDungeonGenerator gen = new ConnectingMapGenerator(rows, cols, 1, 1, new RNG(), 1, 0.5);
        char[][] generated = gen.generate();

        DungeonGenerator dg = new DungeonGenerator(20, 20);
        dg.addStairs();

        generated = dg.generate(generated);
        if (Configuration.PRINT_MAZE_CLI) {
            DungeonUtility.debugPrint(generated);
        }
        return new Maze(generated);
    }


    public static Maze getEmpty(int rows, int cols) {
        IDungeonGenerator gen = new ConnectingMapGenerator(rows - 2, cols - 2, rows, rows, new RNG(), 1, 0.5);
        char[][] generated = gen.generate();

        DungeonGenerator dg = new DungeonGenerator(20, 20);
        dg.addStairs();

        generated = dg.generate(generated);
        if (Configuration.PRINT_MAZE_CLI) {
            DungeonUtility.debugPrint(generated);
        }
        return new Maze(generated);
    }

    public static Maze getFlowingCave(int rows, int cols) {
        IDungeonGenerator gen = new FlowingCaveGenerator(rows, cols);
        char[][] generated = gen.generate();


        setStart(generated);
        setEnd(generated);

        if (rnd.nextBoolean()) {
            Maze.invertrows(generated);
        }
        if (rnd.nextBoolean()) {
            Maze.rotate(generated);
        }
        if (rnd.nextBoolean()) {
            Maze.rotate(generated);
        }
        if (rnd.nextBoolean()) {
            Maze.invertrows(generated);
        }
        if (rnd.nextBoolean()) {
            Maze.invertrows(generated);
        }
        if (rnd.nextBoolean()) {
            Maze.rotate(generated);
        }
        if (rnd.nextBoolean()) {
            Maze.rotate(generated);
        }
        if (rnd.nextBoolean()) {
            Maze.invertrows(generated);
        }
//        gen.addStairs();

//        generated = gen.generate(generated);
        if (Configuration.PRINT_MAZE_CLI) {
            DungeonUtility.debugPrint(generated);
        }
        return new Maze(generated);
    }

    public static void setStart(char[][] mx) {
        for (int r = 1; r < 2000; r++) {
            if (mx[r][r] == '.') {
                int count = 0;
                if (mx[r + 1][r] == '.') {
                    count++;
                }

                if (mx[r - 1][r] == '.') {
                    count++;
                }

                if (mx[r][r - 1] == '.') {
                    count++;
                }

                if (mx[r][r + 1] == '.') {
                    count++;
                }
                if (count >= 3) {
                    mx[r][r] = '<';
                    return;
                }
            }
        }
    }

    public static void setEnd(char[][] mx) {
        for (int r = 2; r < 2000; r++) {
            if (mx[mx.length - r][mx[0].length - r] == '.') {
                int count = 0;
                if (mx[mx.length - r + 1][mx[0].length - r] == '.') {
                    count++;
                }

                if (mx[mx.length - r - 1][mx[0].length - r] == '.') {
                    count++;
                }

                if (mx[mx.length - r][mx[0].length - 1 - r] == '.') {
                    count++;
                }

                if (mx[mx.length - r][mx[0].length + 1 - r] == '.') {
                    count++;
                }
                if (count >= 3) {
                    mx[mx.length - r][mx[0].length - r] = '>';
                    return;
                }
            }
        }
    }

    public static Maze getDungeon(int rows, int cols) {
        DungeonGenerator dg = new DungeonGenerator(rows, cols);
        dg.addStairs();

        char[][] generated = dg.generate();

        if (Configuration.PRINT_MAZE_CLI) {
            DungeonUtility.debugPrint(generated);
        }
        return new Maze(generated);
    }
}
