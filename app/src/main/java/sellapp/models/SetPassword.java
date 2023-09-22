package sellapp.models;

public class SetPassword
    {

    private String token;
    private String oldPassword;
    private String newPassword;

    public SetPassword(String token, String oldPassword, String newPassword)
        {
        this.token = token;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        }

    public SetPassword()
        {

        }

    public String getToken()
        {
        return token;
        }

    public void setToken(String token)
        {
        this.token = token;
        }

    public String getOldPassword()
        {
        return oldPassword;
        }

    public void setOldPassword(String oldPassword)
        {
        this.oldPassword = oldPassword;
        }

    public String getNewPassword()
        {
        return newPassword;
        }

    public void setNewPassword(String newPassword)
        {
        this.newPassword = newPassword;
        }
    }
