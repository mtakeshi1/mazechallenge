package mc.challenge;

import mc.challenge.maze.Direction;
import mc.challenge.maze.IMaze;

public interface Challenge {


    void setMap(IMaze maze);

    Direction getMove();


}
