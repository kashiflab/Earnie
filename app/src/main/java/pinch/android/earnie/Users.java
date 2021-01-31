package pinch.android.earnie;

public class Users {
    private String id;
    private String fullname;
    private String number;
    private String income;
    private String email;
    private String saved;

    @Override
    public String toString() {
        return "Users{" +
                "id='" + id + '\'' +
                ", fullname='" + fullname + '\'' +
                ", number='" + number + '\'' +
                ", income='" + income + '\'' +
                ", email='" + email + '\'' +
                ", saved='" + saved + '\'' +
                '}';
    }

    public String getSaved() {
        return saved;
    }

    public String getNumber() {
        return number;
    }

    public String getIncome() {
        return income;
    }

    public String getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }
    public String getEmail() {
        return email;
    }


}
