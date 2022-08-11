package path;

import mc.challenge.maze.Maze.CellType;
import mc.renamebeforepr.AbsolutePosition;
import mc.renamebeforepr.ChallengeImpl;
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
        ChallengeImpl.findPath(from, to, maze, false);
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
        ChallengeImpl.findPath(from, to, maze, false);

    }
}
