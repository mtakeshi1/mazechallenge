package mc.challenge.maze;

import mc.Configuration;
import mc.challenge.maze.mazetypes.DungeonMaze;
import mc.challenge.maze.mazetypes.EmptyMaze;
import mc.challenge.maze.mazetypes.FlowingCaveMaze;
import mc.challenge.maze.mazetypes.ScatterMaze;
import mc.challenge.maze.mazetypes.Width1Maze;

import java.util.ArrayList;
import java.util.List;

/**
 * From here the Maze creation is called.
 */
public class MazeFactory {

    private MazeFactory() {
        throw new RuntimeException("may not instantiate this class");
    }

    public static Maze getEmptyMap(int size) {
        return print(new EmptyMaze(size));
    }


    public static Maze getScatterMap(int size) {
        return print(new ScatterMaze(size));
    }

    public static Maze get1WMap(int size) {
        return print(new Width1Maze(size));
    }

    public static Maze getDungeon(int size) {
        return print(new DungeonMaze(size));
    }

    public static Maze getFlowingCave(int size) {
        return print(new FlowingCaveMaze(size));
    }

    public static Maze getHuge1wMazeFromFile(int index) {
        System.out.println("huge 1w");
        return print(new Width1Maze("width1huge_" + index + ".map"));
    }

    public static Maze getLarge1wMazeFromFile(int index) {
        System.out.println("large 1w");
        return print(new Width1Maze("width1large_" + index + ".map"));
    }

    public static Maze getHugeDungeonMazeFromFile(int index) {
        return print(new DungeonMaze("dungeonhuge_" + index + ".map"));
    }

    public static Maze getHugeFlowingCaveMazeFromFile(int index) {
        return print(new FlowingCaveMaze("flowingCavehuge_" + index + ".map"));
    }


    private static Maze print(Maze map) {
        if (Configuration.printMazeCli) {
            System.out.println();
            System.out.println("---------------------------------------");
            for (int r = map.totalRows - 1; r >= 0; r--) {
                var sb = new StringBuilder();
                for (int c = 0; c < map.totalCols; c++) {
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

    //    public static void main(String[] args) throws IOException {
//
//        IntStream.range(1, 15).parallel().forEach(i -> {
//            try {
////                Files.write(Path.of("./data/maps/dungeonhuge_" + i + ".map"), toList(hugemap()));
////                Files.write(Path.of("./data/maps/dungeonhuge_" + i + ".map"), toList(hugedungeon()));
////                Files.write(Path.of("./data/maps/width1large_" + i + ".map"), toList(get1WArray(LARGE, LARGE)));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }


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


    public static void main(String[] args) {
        getScatterMap(50);
    }
}
