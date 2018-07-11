package models;

import java.io.Serializable;
import java.util.List;

public class TourDetails implements Serializable {
    public List<TourAgenda> TourAgenda;
    public List<ToursAdditionalInformations> ToursAdditionalInformations;
    public List<TourReviews> TourReviews;
}
