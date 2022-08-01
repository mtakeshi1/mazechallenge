package mc.participants.everyos;

import mc.challenge.maze.Maze.CellType;

public record MazeCell(CellType cellType, boolean[] moves) {

}
