package part2;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Map;

public interface Travel {

    Station from();

    Station to();

    List<Station> stations();

    Date startingDate();

    Time startingTime();

    List<Time> times();

    Map<Station, Time> stops();

    String toString();
}
