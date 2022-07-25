package mc.challenge;

import mc.challenge.maze.Direction;

import java.util.ArrayList;
import java.util.List;

public class Helper {

    public static List<Direction> updownlst = new ArrayList<>();


    static {
        for (int x = 0; x < 100; x++) {
            updownlst.add(Direction.SOUTH);
            updownlst.add(Direction.WEST);
        }

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                updownlst.add(Direction.NORTH);
            }
            updownlst.add(Direction.EAST);
            updownlst.add(Direction.EAST);
            updownlst.add(Direction.EAST);
            for (int x = 0; x < 100; x++) {
                updownlst.add(Direction.SOUTH);
            }
            updownlst.add(Direction.EAST);
            updownlst.add(Direction.EAST);
            updownlst.add(Direction.EAST);
        }
    }
}
