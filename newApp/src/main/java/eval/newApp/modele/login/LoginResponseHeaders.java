package eval.newApp.modele.login;

public class LoginResponseHeaders {
    private String sid;
    private String systemUser;
    private String fullName;
    private String userId;

    public LoginResponseHeaders(String sid, String systemUser, String fullName, String userId) {
        this.sid = sid;
        this.systemUser = systemUser;
        this.fullName = fullName;
        this.userId = userId;
    }

    // Getters
    public String getSid() {
        return sid;
    }

    public String getSystemUser() {
        return systemUser;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserId() {
        return userId;
    }
}