package mc.challenge.maze;

/**
 * Immutable class that holds the 'row' and 'column' value for a 2d position
 */
public record Position(int row, int col) {
    public Position plus(int row, int col) {
        return new Position(row() + row, col() + col);
    }
}
