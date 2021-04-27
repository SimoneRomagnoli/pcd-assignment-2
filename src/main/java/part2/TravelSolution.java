package part2;

import java.sql.Date;

public class TravelSolution implements Travel {

    private final String solutionId;
    private final Station origin;
    private final Station destination;
    //private final Date departureDate;
    private final Long departureTime;
    private final Long arrivalTime;

    public TravelSolution(String solutionId, Station origin, Station destination, Long departureTime, Long arrivalTime) {
        this.solutionId = solutionId;
        this.origin = origin;
        this.destination = destination;
        //NEED TO PARSE DATE FROM DEPARTURE TIME
        //this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String getSolutionId() {
        return this.solutionId;
    }

    @Override
    public Station getOrigin() {
        return this.origin;
    }

    @Override
    public Station getDestination() {
        return this.destination;
    }

    @Override
    public Date getDepartureDate() {
        //return this.departureDate;
        return null;
    }

    @Override
    public Long getDepartureTime() {
        return this.departureTime;
    }

    @Override
    public Long getArrivalTime() {
        return this.arrivalTime;
    }
}
