package part2;

public class StationImpl implements Station {

    private final String name;

    public StationImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
