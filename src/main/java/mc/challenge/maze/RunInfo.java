package mc.challenge.maze;

import java.time.LocalDateTime;

/**
 * Store data on runs for feedback.
 * @param mazename the type of the maze
 * @param rows number of rows the maze has
 * @param cols number of rows the maze has
 * @param moves amount of moves spend to reach finish
 * @param tilesExplored amount of tiles that have been explored in this run
 * @param totalFloorTiles total ammount of walkable tiles in this map
 * @param startPosition row/column of the start position
 * @param finishPosition row/column of the finish position
 * @param timeTakenMS the time it took to complete the maze in milliseconds
 */
public record RunInfo(
        LocalDateTime currentDateTime,
        String mazename,
        int rows,
        int cols,
        int moves,
        int tilesExplored,
        int totalFloorTiles,
        Position startPosition,
        Position finishPosition,
        long timeTakenMS
) {
}
