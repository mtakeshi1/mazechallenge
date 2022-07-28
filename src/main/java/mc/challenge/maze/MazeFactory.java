package mc.challenge.maze;

import mc.Configuration;
import squidpony.squidgrid.mapping.ConnectingMapGenerator;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidgrid.mapping.FlowingCaveGenerator;
import squidpony.squidgrid.mapping.IDungeonGenerator;
import squidpony.squidmath.RNG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static mc.Configuration.HUGE;
import static mc.Configuration.LARGE;
import static mc.challenge.maze.ArrayUtil.FOUR_DIRECTIONS;
import static mc.challenge.maze.ArrayUtil.invertrows;
import static mc.challenge.maze.ArrayUtil.rotate;
import static mc.challenge.maze.Maze.CellType.FLR;
import static mc.challenge.maze.Maze.CellType.FSH;
import static mc.challenge.maze.Maze.CellType.SRT;
import static mc.challenge.maze.Maze.CellType.WLL;

/**
 * From here the Maze creation is called.
 */
public class MazeFactory {

    private MazeFactory() {
        throw new RuntimeException("may not instantiate this class");
    }

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
                var sb = new StringBuilder();
                for (int c = 0; c < cols; c++) {
                    switch (map.getTile(r, c)) {
                        case WLL -> sb.append('#');
                        case FLR -> sb.append('.');
                        case SRT -> sb.append('<');
                        case FSH -> sb.append('>');
                        case UNK -> sb.append('?');
                    }
                }
                System.out.println(sb);
            }
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

                if (map.getTile(r, c) == SRT || map.getTile(r, c) == FSH) {
                    for (int[] dir : FOUR_DIRECTIONS) {
                        map.setTile(new Position(r + dir[0], c + dir[1]), FLR);
                        map.setTile(new Position(r + dir[0] * 2, c + dir[1] * 2), FLR);
                        map.setTile(new Position(r + dir[0] * 3, c + dir[1] * 3), FLR);
                        map.setTile(new Position(r + dir[0] * 4, c + dir[1] * 4), FLR);
                    }

                }

            }
        }

        return print(rows, cols, map);
    }

    public static Maze get1WMap(int rows, int cols) {
        return new Maze(get1WArray(rows, cols));
    }
    public static char[][] get1WArray(int rows, int cols) {
        IDungeonGenerator gen = new ConnectingMapGenerator(rows, cols, 1, 1, new RNG(), 1, 0.5);
        char[][] generated = gen.generate();


        DungeonGenerator dg = new DungeonGenerator(20, 20);
        dg.addStairs();

        generated = dg.generate(generated);
        if (Configuration.PRINT_MAZE_CLI) {
            DungeonUtility.debugPrint(generated);
        }
        return generated;
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
        if (Configuration.PRINT_MAZE_CLI) {
            DungeonUtility.debugPrint(generated);
        }
        return new Maze(generated);
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

    public static Maze getDungeon(int rows, int cols) {
        DungeonGenerator dg = new DungeonGenerator(rows, cols);
        dg.addStairs();

        char[][] generated = dg.generate();

        if (Configuration.PRINT_MAZE_CLI) {
            DungeonUtility.debugPrint(generated);
        }
        return new Maze(generated);
    }

    public static void main(String[] args) throws IOException {

        IntStream.range(1, 15).parallel().forEach(i -> {
            try {
//                Files.write(Path.of("./data/maps/dungeonhuge_" + i + ".map"), toList(hugemap()));
//                Files.write(Path.of("./data/maps/dungeonhuge_" + i + ".map"), toList(hugedungeon()));
                Files.write(Path.of("./data/maps/width1large_" + i + ".map"), toList(get1WArray(LARGE, LARGE)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    static char[][] hugemap() {
        IDungeonGenerator gen = new ConnectingMapGenerator(HUGE, HUGE, 1, 1, new RNG(), 1, 0.5);
        char[][] generated = gen.generate();


        DungeonGenerator dg = new DungeonGenerator(20, 20);
        dg.addStairs();
        generated = dg.generate(generated);
        return generated;
    }

    static char[][] hugedungeon() {
        DungeonGenerator dg = new DungeonGenerator(HUGE, HUGE);
        dg.addStairs();

        char[][] generated = dg.generate();
        return generated;
    }

    static char[][] flowingCave(int rc) {
        IDungeonGenerator gen = new FlowingCaveGenerator(rc, rc);
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


    static List<String> toList(char[][] arr) {
        List<String> lines = new ArrayList<>();

        for (var a : arr) {
            StringBuilder sb = new StringBuilder();
            for (var c : a) {
                sb.append(c);
            }
            lines.add(sb.toString());
        }
        return lines;
    }


    private static List<Maze> huge1WidthMazes = null;
    public static List<Maze> getHuge1wMazes() {
        if (huge1WidthMazes == null) {
            var list = new ArrayList<Maze>();
            for (int x = 1; x <= 14; x++) {
                list.add(MazeParser.loadMazeFromFile("width1huge_" + x + ".map"));
            }
            huge1WidthMazes = list;
        }
        return huge1WidthMazes;
    }
    private static List<Maze> large1WidthMazes = null;
    public static List<Maze> getLarge1WidthMazes() {
        if (large1WidthMazes == null) {
            var list = new ArrayList<Maze>();
            for (int x = 1; x <= 14; x++) {
                list.add(MazeParser.loadMazeFromFile("width1large_" + x + ".map"));
            }
            large1WidthMazes = list;
        }
        return large1WidthMazes;
    }

    private static List<Maze> hugeDungeons = null;
    public static List<Maze> getHugeDungeons() {
        if (hugeDungeons == null) {
            var list = new ArrayList<Maze>();
            for (int x = 1; x <= 14; x++) {
                list.add(MazeParser.loadMazeFromFile("dungeonhuge_" + x + ".map"));
            }
            hugeDungeons = list;
        }
        return hugeDungeons;
    }


    private static List<Maze> hugeFlowingCaves = null;
    public static List<Maze> getHugeFlowingCaves() {
        if (hugeFlowingCaves == null) {
            var list = new ArrayList<Maze>();
            for (int x = 1; x <= 14; x++) {
                list.add(MazeParser.loadMazeFromFile("flowingCavehuge_" + x + ".map"));
            }
            hugeFlowingCaves = list;
        }
        return hugeFlowingCaves;
    }


}
