package android.example.com.popularmovies;

public enum SortByCriteria {

    POPULARITY("Popularity.dsc"),
    TOP_RATED("Toprated.dsc"),
    FAVORITES("favorites");


    private final String criteria;

    SortByCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String criteria() {
        return criteria;
    }


}
