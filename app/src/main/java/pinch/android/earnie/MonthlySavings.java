package pinch.android.earnie;

public class MonthlySavings {
    private String saved;
    private String month;

    public String getSaved() {
        return saved;
    }

    public String getMonth() {
        return month;
    }

    public MonthlySavings(String saved, String month) {
        this.saved = saved;
        this.month = month;
    }
}
