package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class PPManager
{
    public static String getEntityString(Entity var0)
    {
        return var0.Q();
    }

    public static String getEntityType(EntityLiving var0)
    {
        return var0.aI;
    }

    public static void closeGuiScreen(EntityHuman var0)
    {
        var0.closeInventory();
    }
}
