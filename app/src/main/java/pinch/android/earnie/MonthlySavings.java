package pinch.android.earnie;

public class MonthlySavings {
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

    public MonthlySavings(String saved, String month, String year, boolean isSalarySet) {
        this.saved = saved;
        this.month = month;
        this.year = year;
        this.isSalarySet = isSalarySet;
    }

    public String getSaved() {
        return saved;
    }

    public String getMonth() {
        return month;
    }

}
