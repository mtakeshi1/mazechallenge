package mc.renamebeforepr;

import mc.challenge.maze.Direction;

public record AbsolutePosition(int row, int col) implements Position {

    public AbsolutePosition walk(Direction direction) {
        return this.walk(direction, AbsolutePosition::new);
    }
}
