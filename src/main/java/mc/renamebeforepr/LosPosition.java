package mc.renamebeforepr;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze;

public record LosPosition(int row, int col) implements Position {
    public LosPosition {
        if (row < 0 || col < 0 || row > 13 || col > 13) {
            throw new RuntimeException(String.format("relative position should be 0 <= (row,col) < 13", row, col));
        }
    }

    public LosPosition minus(LosPosition currentPosition) {
        return new LosPosition(currentPosition.row - this.row, currentPosition.col - this.col);
    }

    public LosPosition walk(Direction direction) {
        return this.walk(direction, LosPosition::new);
    }

}
