package mc.challenge.maze;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.renamebeforepr.ChallengeImpl;

import java.io.IOException;
import java.util.Map;

public class AllChallenges {

    public interface MazeBuilder {
        Maze build(int size, long seed);
    }

    private static Map<String, MazeBuilder> mazeFactories =
            Map.of("Dungeon", MazeFactory::getDungeon, "Empty", (size, seed) -> MazeFactory.getEmptyMap(size), "Scatter", MazeFactory::getScatterMap, "1Width", (size, seed) -> MazeFactory.get1WMap(size), "Flowing", MazeFactory::getFlowingCave);

    private static int[] mazeSizes = {
//            Configuration.SMALL,
            Configuration.MEDIUM, Configuration.LARGE, Configuration.HUGE};

    public static RunInfo run(Challenge challenge, Maze maze) {
        while (!maze.isEndReached()) {
            maze.doMove(challenge.getMove());
            challenge.handleLineOfSightUpdate(maze.getLineOfSight());

            if (maze.getStepsTaken() > 10000000) {
                throw new RuntimeException("Sorry, too many steps");
            }
        }
        return maze.getFinishedInfo();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        pause("Before starting");
        long seed = 102;
        System.out.printf("%20s%10s%10s%10s%10s%n", "name", "size", "time(ms)", "tiles", "moves");
        mazeFactories.forEach((name, builder) -> {
            for (int size : mazeSizes) {
                Maze maze = builder.build(size, seed);
                ChallengeImpl challenge = new ChallengeImpl();
                maze.setEntrant(challenge.getEntrant());
                challenge.handleLineOfSightUpdate(maze.getLineOfSight());
                var r = run(challenge, maze);
                System.out.printf("%20s%10d%10d%10d%10d%n", maze.getMazeType(), size, r.timeTakenMS(), r.tilesExplored(), r.moves());
            }
        });
        pause("Finished");
    }

    private static void pause(String msg) throws IOException, InterruptedException {
        System.out.println(msg);
        while (System.in.available() == 0) {
            Thread.sleep(100);
        }
        System.in.read(new byte[System.in.available()]);
    }

}
