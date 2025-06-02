package eval.newApp.modele.login;

public class LoginRequest
{
    private String usr;
    private String pwd;

    public LoginRequest() {}

    public LoginRequest(String usr, String pwd) {
        this.usr = usr;
        this.pwd = pwd;
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}