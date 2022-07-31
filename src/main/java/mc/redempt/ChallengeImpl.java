package mc.redempt;

import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.Position;

/**
 * The whole challenge can be completed by just adding to this file.
 * You can create other java files in the same package though.
 * <p>
 * Run this:
 * Graphically -> {@link mc.GraphicalLauncher} ( for debug purposes )
 * Non graphically ->  {@link mc.HeadlessLauncher}
 * <p>
 * Configure and choose what type of mazes are ran : {@link mc.Configuration}
 */
public class ChallengeImpl implements Challenge {

    private MazeMap map = new MazeMap();
    private TargetFinder targetFinder = new TargetFinder(map);
    private Pathfinder pathfinder = new Pathfinder(map);
    
    @Override
    public void handleLineOfSightUpdate(CellType[][] los) {
        for (int row = 0; row < los.length; row++) {
            for (int col = 0; col < los[row].length; col++) {
                CellType type = los[row][col];
                Position pos = map.add(row - 6, col - 6, type);
                targetFinder.add(pos);
            }
        }
    }
    
    private void recalcPath() {
        if (targetFinder.hasExit()) {
            pathfinder.setTarget(targetFinder.getNextTarget());
        }
        while (!pathfinder.hasPath()) {
            Position goal = targetFinder.getNextTarget();
            if (!pathfinder.setTarget(goal)) {
                targetFinder.remove(goal);
            }
        }
    }
    
    @Override
    public Direction getMove() {
        recalcPath();
        Direction move = pathfinder.getNextMove();
        while (!map.move(move)) {
            recalcPath();
            move = pathfinder.getNextMove();
        }
        return move;
    }
    
}
