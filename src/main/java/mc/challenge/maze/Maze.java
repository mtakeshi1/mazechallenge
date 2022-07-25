package mc.challenge.maze;

import java.util.Random;
import java.util.function.Consumer;

public class Maze {

    private static final Random rnd = new Random();

    public int getStepsTaken() {
        return stepsTaken;
    }

    public enum CellType {
        WALL, FLOOR, START, FINISH, UNKNOWN
    }

    private final Player player;
    private final CellType[][] matrix;
    private final boolean[][] explored;
    private final Position finish;
    private int stepsTaken = 0;
    private boolean endReached = false;


    private final ExploredBounds exploredBounds = new ExploredBounds();

    public Maze(int rows, int cols) {
        this(
                rows,
                cols,
                new Position(rnd.nextInt((rows / 2) - 2) + 1, rnd.nextInt((cols / 2) - 2) + 1),
                new Position(rnd.nextInt((rows / 2) - 2) + rows / 2, rnd.nextInt((cols / 2) - 2) + cols / 2)
        );
    }


    boolean doMove(MoveSupplier moveSupplier) {
        var direction = moveSupplier.supply();
        if (endReached) {
            return false;
        }
        Position newPosition = player.getPosition().plus(direction.getTP()[0], direction.getTP()[1]);
        var type = matrix[newPosition.row()][newPosition.col()];

        if (type == CellType.WALL) {
            return false;
        }

        if (newPosition.equals(finish)) {
            System.out.println("FINISH !");
            endReached = true;
        }

        stepsTaken++;
        player.setPosition(newPosition);
        for (int r = newPosition.row() - 1; r <= newPosition.row() + 1; r++) {
            for (int c = newPosition.col() - 1; c <= newPosition.col() + 1; c++) {
                explore(new Position(r, c));
            }
        }
        return true;
    }


    public Maze(int rows, int cols, Position start, Position finish) {
        if (rows < 5) throw new IllegalArgumentException("rows must be >= 5");
        if (cols < 5) throw new IllegalArgumentException("cols must be >= 5");
        if (start == null) throw new IllegalArgumentException("start may not be null");
        if (start.row() < 1) throw new IllegalArgumentException("start row must be > 0");
        if (start.row() >= rows) throw new IllegalArgumentException("start row must be < rows");
        if (start.col() < 1) throw new IllegalArgumentException("start col must be > 0");
        if (start.col() >= rows) throw new IllegalArgumentException("start col must be < rows");

        if (finish == null) throw new IllegalArgumentException("finish may not be null");
        if (finish.row() < 1) throw new IllegalArgumentException("finish row must be > 0");
        if (finish.row() >= rows) throw new IllegalArgumentException("finish row must be < rows");
        if (finish.col() < 1) throw new IllegalArgumentException("finish col must be > 0");
        if (finish.col() >= rows) throw new IllegalArgumentException("finish col must be < rows");
        if (start.equals(finish)) throw new IllegalArgumentException("start must not be finish");

        this.finish = finish;

        matrix = new CellType[rows][cols];
        explored = new boolean[rows][cols];

        fillMatrixWithFloor();
        wallEdges();
        matrix[start.row()][start.col()] = CellType.START;
        matrix[finish.row()][finish.col()] = CellType.FINISH;

        explore(start);
        player = new Player(start);
    }

//    public void explore() {
//        for (int r = 0; r < matrix.length; r++) {
//            for (int c = 0; c < matrix[0].length; c++) {
//                explore(new Position(r, c));
//            }
//        }
//    }


    private void fillMatrixWithFloor() {
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                matrix[r][c] = CellType.FLOOR;
            }
        }
    }

    private void wallEdges() {
        for (int r = 0; r < matrix.length; r++) {
            matrix[r][0] = CellType.WALL;
            matrix[r][matrix[0].length - 1] = CellType.WALL;
        }
        for (int c = 0; c < matrix[0].length; c++) {
            matrix[0][c] = CellType.WALL;
            matrix[matrix.length - 1][c] = CellType.WALL;
        }
    }

    static class ExploredBounds {
        private int north = -1;
        private int south = -1;
        private int east = -1;
        private int west = -1;


        void process(Position pos) {
            if (north == -1) {
                north = pos.row();
                south = pos.row();
                east = pos.col();
                west = pos.col();
            } else {
                north = Math.max(north, pos.row());
                south = Math.min(south, pos.row());
                east = Math.max(east, pos.col());
                west = Math.min(west, pos.col());
            }
        }
    }

    private void explore(Position pos) {
        exploredBounds.process(pos);
        explored[pos.row()][pos.col()] = true;
    }

    public CellType[][] visitedMatrix() {

        if (exploredBounds.north == -1) {
            return new CellType[0][0];
        }

        int rows = 1 + exploredBounds.north - exploredBounds.south;
        int cols = 1 + exploredBounds.east - exploredBounds.west;
        CellType[][] mx = new CellType[rows][cols];


        int mr = 0;
        int mc;
        for (int r = exploredBounds.south; r <= exploredBounds.north; r++) {
            mc = 0;
            for (int c = exploredBounds.west; c <= exploredBounds.east; c++) {
                if (explored[r][c]) {
                    mx[mr][mc] = matrix[r][c];
                } else {
                    mx[mr][mc] = CellType.UNKNOWN;
                }
                mc++;
            }
            mr++;
        }

        return mx;
    }


    /**
     * gets player position relative to the explored map.
     */
    public Position getPlayerPosition() {
        return new Position(
                player.getPosition().row() - exploredBounds.south,
                player.getPosition().col() - exploredBounds.west
        );
    }


    public boolean isEndReached() {
        return endReached;
    }


    public void drawMaze(Consumer<CellType[][]> drawMaze) {
        drawMaze.accept(visitedMatrix());
    }

    public void drawPlayer(Consumer<Position> drawPlayer) {
        drawPlayer.accept(getPlayerPosition());
    }

}
