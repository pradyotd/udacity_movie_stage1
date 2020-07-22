package android.example.com.popularmovies;

public class Response {
    private int page;
    private int total_results;
    private int total_pages;
    private MovieData[] results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public MovieData[] getResults() {
        return results;
    }

    public void setResults(MovieData[] results) {
        this.results = results;
    }
}
