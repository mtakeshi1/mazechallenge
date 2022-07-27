package mc.challenge.maze;

/**
 * An enum for the 4 main directions
 *
 *
 * |   )          .`      N      `.          (    |
 * |  (         .`   A    |        `.         )   |
 * |   )      .`     <\> )|(         `.      (    |
 * |  (     .`         \  |  (         `.     )   |
 * |   )  .`         )  \ |    (         `.  (    |
 * |    .`         )     \|      (         `.     |
 * |  .`     W---)--------O--------(---E     `.   |
 * |   `.          )      |\     (          .`    |
 * |   ) `.          )    | \  (          .` (    |
 * |  (    `.          )  |  \          .`    )   |
 * |   )     `.          )|( <\>      .`     (    |
 * |  (        `.         |         .`        )   |
 * |   )         `.       S       .`         (    |
 */
public enum Direction {
    NORTH(new Position(1, 0)),
    EAST(new Position(0, 1)),
    SOUTH(new Position(-1, 0)),
    WEST(new Position(0, -1));

    private final Position transposition;

    Direction(Position transposition) {
        this.transposition = transposition;
    }

    public Position getTP() {
        return transposition;
    }
}
