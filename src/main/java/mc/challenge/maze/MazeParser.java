package mc.challenge.maze;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.lines;

public class MazeParser {


    public static Maze loadMazeFromFile(String filename) {
        return new Maze(fileToMatrix(filename));
    }


    private static char[][] fileToMatrix(String filename) {
        try(var stream = lines(Path.of("./data/maps/" + filename))) {
            var lines = stream.toList();

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
