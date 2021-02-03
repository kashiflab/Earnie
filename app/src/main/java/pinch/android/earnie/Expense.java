package pinch.android.earnie;

public class Expense {
    private String id;
    private String amount;

    public Expense(String id, String amount, String purpose, String startDate, String endDate, String date,
                   String month, String year, boolean isOneTimeExp, boolean deducted) {
        this.id = id;
        this.amount = amount;
        this.purpose = purpose;
        this.startDate = startDate;
        this.endDate = endDate;
        this.date = date;
        this.month = month;
        this.year = year;
        this.isOneTimeExp = isOneTimeExp;
        this.deducted = deducted;
    }

    private String purpose;
    private String startDate;
    private String endDate;
    private String date;
    private boolean deducted;

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", amount='" + amount + '\'' +
                ", purpose='" + purpose + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", date='" + date + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", isOneTimeExp=" + isOneTimeExp +
                ", deducted=" + deducted +
                '}';
    }

    private String month;
    private String year;
    private boolean isOneTimeExp;

    public boolean isDeducted() {
        return deducted;
    }

    public String getAmount() {
        return amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isOneTimeExp() {
        return isOneTimeExp;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }
}
