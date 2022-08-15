package path;

import mc.challenge.maze.Maze.CellType;
import mc.renamebeforepr.AbsolutePosition;
import mc.renamebeforepr.ChallengeImpl;
import mc.renamebeforepr.PathFindingCallback;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class PathFindingTest {


    @Test
    public void testCase1() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        System.out.println(maze.size());
        ChallengeImpl.findPath(from, to, maze, false, PathFindingCallback.NO_OP);
    }

    @Test
    public void testCaseSmall() throws IOException, ClassNotFoundException, InterruptedException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test-small.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
//        maze.put(to, CellType.UNK);
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        System.out.println(maze.size());
//        SwingPathCallback instance = SwingPathCallback.getInstance();
        ChallengeImpl.findPath(from, to, maze, false, PathFindingCallback.NO_OP);
//        while (instance.isVisible()) {
//            Thread.sleep(1000);
//        }

    }

    @Test
    public void testCase2() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test2.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        System.out.println(maze.size());
        System.out.println(from + " -> " + to);
        System.out.println(from.stepDistance(to));
        ChallengeImpl.print(maze, Map.of(from, "FFF", to, "TTT"));
        ChallengeImpl.findPath(from, to, maze, false, PathFindingCallback.NO_OP);
    }

    @Test
    public void testCase3() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test3.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        System.out.println(maze.size());
        System.out.println(from + " -> " + to);
        System.out.println(from.stepDistance(to));
        ChallengeImpl.print(maze, Map.of(from, "FFF", to, "TTT"));
        AbsolutePosition absolutePosition = ChallengeImpl.closestUnknown(from, maze, SwingPathCallback.getInstance());
        ChallengeImpl.findPath(from, absolutePosition, maze, false, SwingPathCallback.getInstance());

    }

    @Test
    public void testCase4() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(getClass().getResourceAsStream("/test4.bin"));
        AbsolutePosition from = (AbsolutePosition) in.readObject();
        AbsolutePosition to = (AbsolutePosition) in.readObject();
        Map<AbsolutePosition, CellType> maze = (Map<AbsolutePosition, CellType>) in.readObject();
        Assert.assertNotEquals(from, to);
        Assert.assertTrue(maze.size() > 0);
        System.out.println(maze.size());
        System.out.println(from + " -> " + to);
        System.out.println(from.stepDistance(to));
        ChallengeImpl.print(maze, Map.of(from, "FFF", to, "TTT"));
        ChallengeImpl.closestUnknown(from, maze, SwingPathCallback.getInstance());
//        ChallengeImpl.findPath(from, to, maze, false, SwingPathCallback.getInstance());
    }
}
