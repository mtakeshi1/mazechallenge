package mc.renamebeforepr;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze;

public record LosPosition(int row, int col) {
    public LosPosition {
        if (row < 0 || col < 0 || row > 13 || col > 13) {
            throw new RuntimeException(String.format("relative position should be 0 <= (row,col) < 13", row, col));
        }
    }

    public LosPosition minus(LosPosition currentPosition) {
        return new LosPosition(currentPosition.row - this.row, currentPosition.col - this.col);
    }

    public LosPosition move(Direction direction) {
        return switch (direction) {
            case NORTH -> new LosPosition(this.row + 1, this.col);
            case EAST -> new LosPosition(this.row, this.col + 1);
            case SOUTH -> new LosPosition(this.row - 1, this.col);
            case WEST -> new LosPosition(this.row, this.col - 1);
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

    public LosPosition add(LosPosition adjustement) {
        return new LosPosition(this.row + adjustement.row, this.col + adjustement.col);
    }
}
