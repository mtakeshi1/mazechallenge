package mc.challenge.maze;

public class MazeFactory {

//    enum Size {
//        TINY, // 10x10
//        SMALL, // 50x50
//        MEDIUM, // 100x100
//        LARGE, // 500x500
//    }

    public static Maze getEmptyMap(int rows, int cols) {
        var map = new Maze(rows, cols);

        return map;
    }
}
