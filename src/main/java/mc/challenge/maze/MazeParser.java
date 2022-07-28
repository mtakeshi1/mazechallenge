package mc.challenge.maze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class MazeParser {


    public static Maze loadMazeFromFile(String filename) {
        return new Maze(fileToMatrix(filename));
    }


    static char[][] fileToMatrix(String filename) {
        try {
            var lines = Files.lines(Path.of("./data/map.map")).collect(Collectors.toList());

            char[][] grid = new char[lines.size()][lines.get(0).length()];

            for (int r = 0; r < lines.size(); r++) {
                var line = lines.get(r);
                for (int c = 0; c < lines.get(0).length(); c++) {
                    grid[r][c] = line.charAt(c);
                }
            }
            return grid;

        } catch (IOException e) {
            throw new RuntimeException("Could not load map");
        }


    }

}
