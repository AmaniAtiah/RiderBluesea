package com.barmej.riderbluesea;

import java.io.Serializable;

public class FullStatus implements Serializable {
    private Rider rider;
    private Trip trip;

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
