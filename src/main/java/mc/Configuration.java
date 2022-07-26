package mc;

import mc.challenge.Challenge;
import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeFactory;

import java.util.List;
import java.util.function.Supplier;

public class Configuration {

    private static final int SMALL = 15;
    private static final int MEDIUM = 100;


    ///////// Graphical
    public static int FRAME_RATE = 200; // (max) FPS -> ALSO UPDATES PLAYER FASTER/SLOWER
    public static int WINDOW_HEIGHT = 1200;
    public static int WINDOW_WIDTH = 1200;
    public static int CELL_SIZE = 16; // size of a drawn square/cell
    public static int OFFSET_X = 100; // draw screen from left offset
    public static int OFFSET_Y = 100; // draw screen from bottom offset


    ///////// CLI
    public static boolean PRINT_MAZE_CLI = true; // when a maze starts, print


    ///////// GAME

    /**
     * Most important part, deliver your own implementation of the Challenge interface
     */
    public static Supplier<? extends Challenge> challenge = ChallengeImpl::new;

    public static List<Supplier<Maze>> mazes = List.of(
            () -> MazeFactory.getEmptyMap(SMALL, SMALL),
            () -> MazeFactory.getEmptyMap(MEDIUM, MEDIUM),
            () -> MazeFactory.getFlowingCave(MEDIUM, MEDIUM),
            () -> MazeFactory.getScatterMap(SMALL, SMALL),
            () -> MazeFactory.get1WMap(SMALL, SMALL),
            () -> MazeFactory.getDungeon(SMALL, SMALL)
    );


}
