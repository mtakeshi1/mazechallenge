package mc.renamebeforepr;

public record FringeEntry(FringeEntry previousPath, AbsolutePosition destination, int cost) implements Comparable<FringeEntry> {
//    private final int cost;
//    private final Position destination;
//    private final FringeEntry previousPath;

//    public FringeEntry(FringeEntry from, Position next, int newCost) {
//        this.cost = newCost;
//        this.destination = next;
//        this.previousPath = from;
//    }

    public AbsolutePosition getSource() {
        return previousPath == null ? null : previousPath.destination();
    }

    @Override
    public int compareTo(FringeEntry o) {
        return Integer.compare(this.cost, o.cost);
    }
}
