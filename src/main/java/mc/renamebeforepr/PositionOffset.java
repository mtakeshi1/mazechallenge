package mc.renamebeforepr;

import mc.challenge.maze.Direction;

public record PositionOffset(int row, int col) implements Position {

    public AbsolutePosition plus(LosPosition los) {
        return new AbsolutePosition(row + los.row(), col + los.col());
    }

    public PositionOffset walk(Direction selectedDirection) {
        return walk(selectedDirection, PositionOffset::new);
    }
}
