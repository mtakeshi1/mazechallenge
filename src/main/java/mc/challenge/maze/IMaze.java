package mc.challenge.maze;

import mc.challenge.maze.Maze.CellType;

public interface IMaze {

    CellType[][] getLineOfSight();
}
