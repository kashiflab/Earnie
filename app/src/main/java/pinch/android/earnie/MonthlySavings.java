package pinch.android.earnie;

public class MonthlySavings {
    private String id;

    public MonthlySavings(String id, String saved, String month, String year, boolean isSalarySet) {
        this.id = id;
        this.saved = saved;
        this.month = month;
        this.year = year;
        this.isSalarySet = isSalarySet;
    }

    private String saved;
    private String month;
    private String year;
    private boolean isSalarySet;

    public String getYear() {
        return year;
    }

    public boolean isSalarySet() {
        return isSalarySet;
    }

    public String getId() {
        return id;
    }

    public String getSaved() {
        return saved;
    }

    public String getMonth() {
        return month;
    }

}
