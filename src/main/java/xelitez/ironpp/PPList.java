/**
 * instance class for mobs in the list.
 *
 * @author Kalvin
 */
package xelitez.ironpp;

import net.minecraft.nbt.NBTTagCompound;

public class PPList
{
    private String mobname;
    private boolean isEnabled;

    public PPList(String var1)
    {
        mobname = var1;
    }

    public PPList(String var1, boolean var2)
    {
        mobname = var1;
        isEnabled = var2;
    }

    public PPList()
    {
    }

    public boolean getEnabled()
    {
        return isEnabled;
    }

    public String getMobname()
    {
        return mobname;
    }

    public void setEnabled(Boolean var1)
    {
        isEnabled = var1;
    }

    public void setMobname(String var1)
    {
        mobname = var1;
    }

    public void enable()
    {
        this.isEnabled = true;
    }

    public void disable()
    {
        this.isEnabled = false;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setBoolean("isEnabled", isEnabled);
        return par1NBTTagCompound;
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        isEnabled = par1NBTTagCompound.getBoolean("isEnabled");
    }

    public static PPList loadSettingsFromNBT(NBTTagCompound par0NBTTagCompound, String var2)
    {
        PPList var1 = new PPList(var2);
        var1.readFromNBT(par0NBTTagCompound);
        return var1;
    }
}
