package mc.challenge.maze;

/**
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
    NORTH(new int[]{1, 0}),
    EAST(new int[]{0, 1}),
    SOUTH(new int[]{-1, 0}),
    WEST(new int[]{0, -1});

    private final int[] transposition;

    Direction(int[] transposition) {
        this.transposition = transposition;
    }

    int[] getTP() {
        return transposition;
    }
}
