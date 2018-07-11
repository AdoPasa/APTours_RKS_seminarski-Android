package helpers;

public class AppConfiguration {
    public static int  ResultsPerPage = 5;
    public static int  ResetPasswordTokenLength = 10;

    //Intent extras keys
    //TourDetails intent
    public static String TourIDKey = "TourID";
    public static String TourPriceKey = "TourPrice";
    public static String AuthTokenKey = "AuthToken";
    public static String TourDatesKey = "TourDates";
    public static String TourKey = "Tour";
    public static String FilterKey = "Filter";
    public static String TourFavoriteKey = "TourActive";

    //Tour filters
    public static String TourFlterActive = "active";
    public static String TourFlterUpcoming = "upcoming";
    public static String TourFlterSaved = "saved";
    public static String TourFlterFinished = "finished";

}
