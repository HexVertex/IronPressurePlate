package xelitez.ironpp;

import net.minecraft.server.v1_4_6.NBTTagCompound;

public class PPList
{
    private String mobname;
    private boolean isEnabled;

    public PPList(String var1)
    {
        this.mobname = var1;
    }

    public PPList(String var1, boolean var2)
    {
        this.mobname = var1;
        this.isEnabled = var2;
    }

    public PPList() {}

    public boolean getEnabled()
    {
        return this.isEnabled;
    }

    public String getMobname()
    {
        return this.mobname;
    }

    public void setEnabled(Boolean var1)
    {
        this.isEnabled = var1.booleanValue();
    }

    public void setMobname(String var1)
    {
        this.mobname = var1;
    }

    public void enable()
    {
        this.isEnabled = true;
    }

    public void disable()
    {
        this.isEnabled = false;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound var1)
    {
        var1.setBoolean("isEnabled", this.isEnabled);
        return var1;
    }

    public void readFromNBT(NBTTagCompound var1)
    {
        this.isEnabled = var1.getBoolean("isEnabled");
    }

    public static PPList loadSettingsFromNBT(NBTTagCompound var0, String var1)
    {
        PPList var2 = new PPList(var1);
        var2.readFromNBT(var0);
        return var2;
    }
}
