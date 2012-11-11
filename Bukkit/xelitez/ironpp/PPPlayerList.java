package xelitez.ironpp;

public class PPPlayerList
{
    private String username;
    public boolean isEnabled = true;

    public PPPlayerList(String var1)
    {
        this.username = var1;
        this.isEnabled = true;
    }

    public PPPlayerList(String var1, boolean var2)
    {
        this.username = var1;
        this.isEnabled = var2;
    }

    public PPPlayerList() {}

    public boolean getEnabled()
    {
        return this.isEnabled;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setEnabled(Boolean var1)
    {
        this.isEnabled = var1.booleanValue();
    }

    public void setMobname(String var1)
    {
        this.username = var1;
    }

    public void enable()
    {
        this.isEnabled = true;
    }

    public void disable()
    {
        this.isEnabled = false;
    }
}
