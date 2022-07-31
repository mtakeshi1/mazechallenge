package mc;

import com.badlogic.gdx.graphics.Color;
import mc.challenge.Challenge;
import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeFactory;
import mc.renamebeforepr.ChallengeImpl;

import java.util.List;
import java.util.function.Supplier;

/**
 * Here you can set some settings to your liking.
 * Especially:
 * minimumDelayMS -> if you want it to go slower/faster
 * CELL_SIZE -> lower if you want more tiles on your screen
 * mazes -> change this if you like an other routine of mazes
 */
public class Configuration {

    private Configuration() {
        throw new RuntimeException("may not instantiate this class");
    }

    /**
     * The minimum wait time before a the next move is made.
     * - 1000 = 1 second
     * - 0 = no wait
     */
    public static long minimumDelayMS = 200;

    /**
     * Dimentions for generated mazes
     */
    public static final int SMALL = 15;
    public static final int MEDIUM = 100;
    public static final int LARGE = 500;
    public static final int HUGE = 1000;


    ///////// Graphical
    public static final int WINDOW_HEIGHT = 1400;
    public static final int WINDOW_WIDTH = 1400;
    public static final int CELL_SIZE = 16; // size of a drawn square/cell
    public static final int OFFSET_X = 200; // draw screen from left offset
    public static final int OFFSET_Y = 200; // draw screen from bottom offset

    public static final Color WALLCOLOR = Color.BROWN;
    public static final Color STARTCOLOR = Color.RED;
    public static final Color FINISHCOLOR = Color.GREEN;
    public static final Color UNKNOWNCOLOR = Color.BLACK;
    public static final Color FLOORCOLOR = Color.SALMON;
    public static final Color BORDERCOLOR = Color.WHITE;
    public static final Color VISITEDCOLOR = Color.YELLOW;

    public static boolean screenshotOnClose = false;

    ///////// CLI
    public static boolean printMazeCli = false; // when a maze starts, print it ( just to help debugging )


    ///////// GAME

    /**
     * Most important part, deliver your own implementation of the Challenge interface
     */
    public static final Supplier<? extends Challenge> challenge = ChallengeImpl::new;

    /**
     * These are the mazes that are ran ( chance for testing if you feel the need )
     */
    public static final List<Supplier<Maze>> MAZES = List.of(
            () -> MazeFactory.getEmptyMap(SMALL),
            () -> MazeFactory.getEmptyMap(MEDIUM),
            () -> MazeFactory.get1WMap(SMALL),
            () -> MazeFactory.getDungeon(SMALL),

            () -> MazeFactory.getScatterMap(MEDIUM),
            () -> MazeFactory.get1WMap(MEDIUM),
            () -> MazeFactory.getFlowingCave(MEDIUM),
            () -> MazeFactory.getDungeon(MEDIUM),

            () -> MazeFactory.getScatterMap(LARGE),
            () -> MazeFactory.getLarge1wMazeFromFile(1),
            () -> MazeFactory.getFlowingCave(LARGE),
            () -> MazeFactory.getDungeon(LARGE),


            () -> MazeFactory.getScatterMap(HUGE),
            () -> MazeFactory.getHuge1wMazeFromFile(1),
            () -> MazeFactory.getHugeFlowingCaveMazeFromFile(1),
            () -> MazeFactory.getHugeDungeonMazeFromFile(1)

    );


}
