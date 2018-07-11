package models;

import java.io.Serializable;

public class TourDates implements Serializable {
    public int TourDateID;
    public String DateString;
    public int NumberOfFreePlaces;

    @Override
    public String toString() {
        return DateString;
    }
}
