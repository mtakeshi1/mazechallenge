package mc.challenge.maze;

public record Position(int row, int col) {


    public Position plus(int row, int col) {
        return new Position(row() + row, col() + col);
    }
}
