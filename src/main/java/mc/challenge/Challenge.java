package mc.challenge;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze;

public interface Challenge {


    void setMap(Maze maze);

    Direction getMove();


}
