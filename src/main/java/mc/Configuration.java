package mc;

import com.badlogic.gdx.graphics.Color;
import mc.challenge.Challenge;
import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeFactory;
import mc.redempt.ChallengeImpl;

import java.util.List;
import java.util.function.Supplier;

/**
 * Here you can set some settings to your liking.
 * Especially:
 * FRAME_RATE -> if you want it to go slower/faster
 * CELL_SIZE -> lower if you want more tiles on your screen
 * mazes -> change this if you like an other routine of mazes
 */
public class Configuration {

    private Configuration() {
        throw new RuntimeException("may not instantiate this class");
    }

    /**
     * Dimentions for generated mazes
     */
    public static final int SMALL = 15;
    public static final int MEDIUM = 100;
    public static final int LARGE = 500;
    public static final int HUGE = 1000;


    ///////// Graphical
    public static final int FRAME_RATE = 60; // (max) FPS -> ALSO UPDATES PLAYER FASTER/SLOWER
    public static final int WINDOW_HEIGHT = 1200;
    public static final int WINDOW_WIDTH = 1200;
    public static final int CELL_SIZE = 8; // size of a drawn square/cell
    public static final int OFFSET_X = 100; // draw screen from left offset
    public static final int OFFSET_Y = 100; // draw screen from bottom offset

    public static final Color WALLCOLOR = Color.BROWN;
    public static final Color STARTCOLOR = Color.RED;
    public static final Color FINISHCOLOR = Color.GREEN;
    public static final Color UNKNOWNCOLOR = Color.BLACK;
    public static final Color FLOORCOLOR = Color.SALMON;
    public static final Color BORDERCOLOR = Color.WHITE;


    ///////// CLI
    public static final boolean PRINT_MAZE_CLI = true; // when a maze starts, print it ( just to help debugging )


    ///////// GAME

    /**
     * Most important part, deliver your own implementation of the Challenge interface
     */
    public static final Supplier<? extends Challenge> challenge = ChallengeImpl::new;

    /**
     * These are the mazes that are ran ( chance for testing if you feel the need )
     */
    public static final List<Supplier<Maze>> MAZES = List.of(
//            () -> MazeFactory.getEmptyMap(SMALL, SMALL),
//            () -> MazeFactory.getEmptyMap(MEDIUM, MEDIUM),
            () -> MazeFactory.get1WMap(SMALL, SMALL),
            () -> MazeFactory.getDungeon(SMALL, SMALL),

            () -> MazeFactory.getScatterMap(MEDIUM, MEDIUM),
            () -> MazeFactory.get1WMap(MEDIUM, MEDIUM),
            () -> MazeFactory.getFlowingCave(MEDIUM, MEDIUM),
            () -> MazeFactory.getDungeon(MEDIUM, MEDIUM),
            
            () -> MazeFactory.getScatterMap(LARGE, LARGE),
            () -> MazeFactory.getLarge1WidthMazes().get(0),
            () -> MazeFactory.getFlowingCave(LARGE, LARGE),
            () -> MazeFactory.getDungeon(LARGE, LARGE),
            
            
            () -> MazeFactory.getScatterMap(HUGE, HUGE),
            () -> MazeFactory.getHuge1wMazes().get(0),
            () -> MazeFactory.getHugeFlowingCaves().get(0),
            () -> MazeFactory.getHugeDungeons().get(0)

    );


}
