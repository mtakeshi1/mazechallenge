package mc.renamebeforepr;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze;

import java.util.Objects;

public record Position(int row, int col) {

    public Position minus(Position currentPosition) {
        return new Position(currentPosition.row - this.row, currentPosition.col - this.col);
    }

    public Position move(Direction direction) {
        return switch (direction) {
            case NORTH -> new Position(this.row + 1, this.col);
            case EAST -> new Position(this.row, this.col + 1);
            case SOUTH -> new Position(this.row - 1, this.col);
            case WEST -> new Position(this.row, this.col - 1);
        };
    }

    public Maze.CellType cellAt(Maze.CellType[][] maze) {
        return maze[this.row][this.col];
    }

    public boolean isWithin(Maze.CellType[][] maze) {
        return this.row < maze.length && this.col < maze[0].length;
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
