package path;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze.CellType;
import mc.renamebeforepr.AbsolutePosition;
import mc.renamebeforepr.ChallengeImpl;
import mc.renamebeforepr.PathFindingCallback;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathFindingTest {


    @Test
    public void testCase1() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        @SuppressWarnings("unchecked")
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        System.out.println(maze.size());
        List<AbsolutePosition> path = new ArrayList<>(ChallengeImpl.findPath(from, to, maze, false, PathFindingCallback.NO_OP));
        Assert.assertTrue(path.size() > 0);
        Assert.assertEquals(to, path.get(path.size() - 1));
        Direction direction = from.directionTo(path.get(0));
        Assert.assertEquals(Direction.NORTH, direction);
    }

    @Test
    public void testTargetCellIsNotPresent() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test-small.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        @SuppressWarnings("unchecked")
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        System.out.println(maze.size());
        List<AbsolutePosition> path = new ArrayList<>(ChallengeImpl.findPath(from, to, maze, false, PathFindingCallback.NO_OP));
        Assert.assertTrue(path.size() > 0);
        Assert.assertEquals(to, path.get(path.size() - 1));
        Direction direction = from.directionTo(path.get(0));
        Assert.assertEquals(Direction.SOUTH, direction);
    }

    @Test
    public void testTargetIsUnknown() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test2.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        @SuppressWarnings("unchecked")
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        System.out.println(maze.size());
        System.out.println(from + " -> " + to);
        System.out.println(from.stepDistance(to));
        ChallengeImpl.print(maze, Map.of(from, "FFF", to, "TTT"));
        ChallengeImpl.findPath(from, to, maze, false, PathFindingCallback.NO_OP);
        List<AbsolutePosition> path = new ArrayList<>(ChallengeImpl.findPath(from, to, maze, false, PathFindingCallback.NO_OP));
        Assert.assertTrue(path.size() > 0);
        Assert.assertEquals(to, path.get(path.size() - 1));
        Direction direction = from.directionTo(path.get(0));
        Assert.assertEquals(Direction.WEST, direction);
    }

    @Test
    public void testLargeDistance() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test3.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        @SuppressWarnings("unchecked")
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        ChallengeImpl.print(maze, Map.of(from, "FFF", to, "TTT"));
        List<AbsolutePosition> path = new ArrayList<>(ChallengeImpl.findPath(from, to, maze, false, PathFindingCallback.NO_OP));
        Assert.assertTrue(path.size() > 0);
        Assert.assertEquals(to, path.get(path.size() - 1));
        Direction direction = from.directionTo(path.get(0));
        Assert.assertTrue(direction == Direction.NORTH || direction == Direction.WEST);
    }

}
