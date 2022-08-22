package mc.challenge.maze;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.renamebeforepr.ChallengeImpl;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static mc.renamebeforepr.Position.square;

public class StandardBenchmark {

    public static final int RANDOM_SEED = 10;

    private final int count;

    private final AllChallenges.MazeBuilder mazeBuilder;

    private final Supplier<Challenge> challengeSupplier;


    public record RunResult(long elapsedTimeNanos, int tilesExplored, int moves) {
    }

    public record Statistics(int sampleCount, long max, long min, double avg, double stdDev) {
    }

    public record AllResults(Statistics time, Statistics tiles, Statistics moves) {
    }

    public static Statistics calculateStatistics(long[] info) {
        if (info.length == 0) {
            return new Statistics(0, 0, 0, 0, 0);
        }
        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < info.length; i++) {
            sum += info[i];
            min = Math.min(min, info[i]);
            max = Math.max(max, info[i]);
        }
        double avg = ((double) sum) / info.length;
        double variance = 0.0;
        for (int i = 0; i < info.length; i++) {
            variance += square(info[i] - avg);
        }
        variance = variance / info.length;
        return new Statistics(info.length, max, min, avg, Math.sqrt(variance));
    }

    public StandardBenchmark(int count, AllChallenges.MazeBuilder mazeBuilder, Supplier<Challenge> challengeSupplier) {
        this.count = count;
        this.mazeBuilder = mazeBuilder;
        this.challengeSupplier = challengeSupplier;
    }

    public AllResults run() {
        warmup();
        RunResult[] measures = new RunResult[count];
        for (int i = 0; i < measures.length; i++) {
            measures[i] = runOnce(RANDOM_SEED);
        }
        Statistics time = calculateStatistics(Arrays.stream(measures).mapToLong(RunResult::elapsedTimeNanos).map(TimeUnit.NANOSECONDS::toMillis).toArray());
        Statistics tiles = calculateStatistics(Arrays.stream(measures).mapToLong(RunResult::tilesExplored).toArray());
        Statistics moves = calculateStatistics(Arrays.stream(measures).mapToLong(RunResult::moves).toArray());
        return new AllResults(time, tiles, moves);
    }

    private void warmup() {
        for (int i = 0; i < 100; i++) {
            runOnce(RANDOM_SEED);
        }

    }

    private RunResult runOnce(int seed) {
        System.gc();
        Maze maze = mazeBuilder.build(Configuration.MEDIUM, seed);
        Challenge challenge = challengeSupplier.get();
        long t0 = System.nanoTime();
        challenge.handleLineOfSightUpdate(maze.getLineOfSight());
        maze.setEntrant(challenge.getEntrant());
        while (!maze.isEndReached()) {
            maze.doMove(challenge.getMove());
            challenge.handleLineOfSightUpdate(maze.getLineOfSight());

            if (maze.getStepsTaken() > 10000000) {
                throw new RuntimeException("Sorry, too many steps");
            }
        }
        RunInfo runInfo = maze.getFinishedInfo();
        long elapsed = System.nanoTime() - t0;
        return new RunResult(elapsed, runInfo.tilesExplored(), runInfo.moves());
    }


    public static void main(String[] args) {
        StandardBenchmark benchmark = new StandardBenchmark(100, MazeFactory::getDungeon, ChallengeImpl::new);
        AllResults run = benchmark.run();
        System.out.println("time (ms): " + run.time());
        System.out.println("tiles: " + run.tiles());
        System.out.println("moves: " + run.moves());

    }

}
