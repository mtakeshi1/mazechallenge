package mc.renamebeforepr;

import mc.challenge.maze.Maze;

import java.util.Collection;
import java.util.Map;

public interface PathFindingCallback {
    void newState(AbsolutePosition nextCell, Collection<? extends FringeEntry> fringe, Collection<? extends AbsolutePosition> visited, Map<AbsolutePosition, Maze.CellType> maze, AbsolutePosition from, AbsolutePosition to);

    PathFindingCallback NO_OP = new PathFindingCallback() {
        @Override
        public void newState(AbsolutePosition nextCell, Collection<? extends FringeEntry> fringe, Collection<? extends AbsolutePosition> visited, Map<AbsolutePosition, Maze.CellType> maze, AbsolutePosition from, AbsolutePosition to) {

        }
    };

}


