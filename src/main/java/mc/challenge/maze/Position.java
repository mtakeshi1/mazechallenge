package mc.challenge.maze;

/**
 * Immutable class that holds the 'row' and 'column' value for a 2d position
 */
public record Position(int row, int col) {
    public Position plus(int row, int col) {
        return new Position(row() + row, col() + col);
    }

    public Position plus(int[] arr) {
        if (arr == null) throw new IllegalArgumentException("arr may not be null");
        if (arr.length != 2) throw new IllegalArgumentException("arr must be of length 2");
        return new Position(row() + arr[0], col() + arr[1]);
    }

    public Position plus(Position pos) {
        if (pos == null) throw new IllegalArgumentException("pos may not be null");

        return new Position(row() + pos.row(), col() + pos.col());
    }
}
