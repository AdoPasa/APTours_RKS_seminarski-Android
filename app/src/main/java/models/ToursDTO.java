package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ToursDTO implements Serializable {
    public int TourID;
    public String Title;
    public String Image;
    public String Description;
    public float Price;
    public float Grade;
    public int NumberOfReviews;
    public boolean Favorite;
    public boolean CanAddReview;

    public int TourReservationID;
    public int NumberOfPassengers;
    public String ReservedAtString;
    public String UpcomingDateString;

    //Application flags
    public boolean ChangingFavoriteState = false;
    public boolean AddingReviewFlag = false;
    public int ReviewGrade = 0;
}
