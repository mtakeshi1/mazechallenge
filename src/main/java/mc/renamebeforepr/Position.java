package mc.renamebeforepr;

import mc.challenge.maze.Direction;

public interface Position {

    int row();

    int col();

    default Direction directionTo(Position remove) {
        if (this.col() == remove.col()) { // north or south
            if (this.row() == remove.row() + 1) {
                return Direction.NORTH;
            } else if (this.row() == remove.row() - 1) {
                return Direction.SOUTH;
            }
        } else if (this.row() == remove.row()) {
            if (this.col() == remove.col() + 1) {
                return Direction.EAST;
            } else if (this.col() == remove.col() - 1) {
                return Direction.WEST;
            }
        }
        throw new RuntimeException("cannot move from " + this + " to " + remove);
    }


}
