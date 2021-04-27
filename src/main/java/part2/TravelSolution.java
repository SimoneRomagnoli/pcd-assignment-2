package part2;

import java.sql.Date;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TravelSolution implements Travel {

    private List<Station> stations;
    private Date startingDate;
    private List<Time> times;

    public TravelSolution(Date startingDate, List<Time> times, Station... stations) {
        this.startingDate = startingDate;
        this.times = times;
        this.stations = new ArrayList<>(Arrays.asList(stations));
    }

    @Override
    public Station from() {
        return this.stations.get(0);
    }

    @Override
    public Station to() {
        return this.stations.get(this.stations.size()-1);
    }

    @Override
    public List<Station> stations() {
        return this.stations;
    }

    @Override
    public Date startingDate() {
        return this.startingDate;
    }

    @Override
    public Time startingTime() {
        return this.times.get(0);
    }

    @Override
    public List<Time> times() {
        return this.times;
    }

    @Override
    public Map<Station, Time> stops() {
        return IntStream
                .range(0, this.stations.size())
                .boxed()
                .collect(Collectors.toMap(this.stations::get, this.times::get));
    }
}
