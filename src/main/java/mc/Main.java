package mc;


import squidpony.squidgrid.mapping.ConnectingMapGenerator;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidgrid.mapping.IDungeonGenerator;
import squidpony.squidmath.RNG;

public class Main {

//
//    public static void main(String[] args) {
//        DungeonGenerator dg = new DungeonGenerator(10, 10);
//
//        char[][] generated = dg.generate();
//        generated = dg.generate(generated);
//        generated[dg.stairsDown.x][dg.stairsDown.y] = '>';
//        generated[dg.stairsUp.x][dg.stairsUp.y] = '<';
//
//        DungeonUtility.debugPrint(generated);
//    }



    public static void main(String[] args) {
        IDungeonGenerator gen = new ConnectingMapGenerator(150, 150, 1, 1, new RNG(), 1, 0.5);

        char[][] generated = gen.generate();
        DungeonGenerator dg = new DungeonGenerator(20, 20);
        generated = dg.generate(generated);
        generated[dg.stairsDown.x][dg.stairsDown.y] = '>';
        generated[dg.stairsUp.x][dg.stairsUp.y] = '<';

        DungeonUtility.debugPrint(generated);
    }
}
