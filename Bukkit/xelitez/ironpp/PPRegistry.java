package xelitez.ironpp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.World;
import xelitez.ironpp.PPRegistry$RegistrySettings;

public class PPRegistry implements IConnectionHandler, ITickHandler
{
    private static List PressurePlates;
    public static boolean loggedIn = false;
    public static boolean send = false;
    public static boolean sendToServer = false;
    static World world = null;
    public static PPRegistry pp;

    public PPRegistry()
    {
        PressurePlates = new ArrayList();
        pp = this;
    }

    public static void addPressurePlate(int var0, int var1, int var2, int var3, boolean var4, ItemStack var5)
    {
        int[] var6 = new int[3];

        for (int var7 = 0; var7 < PressurePlates.size(); ++var7)
        {
            PPRegistry$RegistrySettings var8 = (PPRegistry$RegistrySettings)PressurePlates.get(var7);

            if (var8.xCoord == var0 && var8.yCoord == var1 && var8.zCoord == var2 && var8.dimension == var3)
            {
                return;
            }
        }

        PressurePlates.add(new PPRegistry$RegistrySettings(var0, var1, var2, var5, var3, var4));
    }

    public static void addPressurePlate(TileEntityPressurePlate var0, int var1)
    {
        boolean var2 = false;

        if (var0.settings != null)
        {
            var2 = var0.getIsEnabled(2);
        }

        addPressurePlate(var0.x, var0.y, var0.z, var1, var2, var0.getItem(0));
    }

    public static void removePressurePlate(TileEntityPressurePlate var0, int var1)
    {
        for (int var2 = 0; var2 < PressurePlates.size(); ++var2)
        {
            PPRegistry$RegistrySettings var3 = (PPRegistry$RegistrySettings)PressurePlates.get(var2);

            if (var3.xCoord == var0.x && var3.yCoord == var0.y && var3.zCoord == var0.z && var3.dimension == var1)
            {
                PressurePlates.remove(var2);
                return;
            }
        }
    }

    public static boolean getContainsPressurePlate(int var0, int var1, int var2, int var3)
    {
        for (int var4 = 0; var4 < PressurePlates.size(); ++var4)
        {
            PPRegistry$RegistrySettings var5 = (PPRegistry$RegistrySettings)PressurePlates.get(var4);

            if (var5.xCoord == var0 && var5.yCoord == var1 && var5.zCoord == var2 && var5.dimension == var3)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean getContainsPressurePlate(TileEntityPressurePlate var0, int var1)
    {
        return getContainsPressurePlate(var0.x, var0.y, var0.z, var1);
    }

    public static ItemStack getItem(int var0, int var1, int var2, int var3)
    {
        for (int var4 = 0; var4 < PressurePlates.size(); ++var4)
        {
            PPRegistry$RegistrySettings var5 = (PPRegistry$RegistrySettings)PressurePlates.get(var4);

            if (var5.xCoord == var0 && var5.yCoord == var1 && var5.zCoord == var2 && var3 == var5.dimension)
            {
                return var5.item;
            }
        }

        return null;
    }

    public static ItemStack getItem(TileEntityPressurePlate var0, int var1)
    {
        return getItem(var0.x, var0.y, var0.z, var1);
    }

    public static boolean getUsesPassword(int var0, int var1, int var2, int var3)
    {
        for (int var4 = 0; var4 < PressurePlates.size(); ++var4)
        {
            PPRegistry$RegistrySettings var5 = (PPRegistry$RegistrySettings)PressurePlates.get(var4);

            if (var5.xCoord == var0 && var5.yCoord == var1 && var5.zCoord == var2 && var3 == var5.dimension)
            {
                return var5.usesPassword;
            }
        }

        return true;
    }

    public static boolean getUsesPassword(TileEntityPressurePlate var0, int var1)
    {
        return getUsesPassword(var0.x, var0.y, var0.z, var1);
    }

    public static void setUsesPassword(int var0, int var1, int var2, int var3, boolean var4)
    {
        for (int var5 = 0; var5 < PressurePlates.size(); ++var5)
        {
            PPRegistry$RegistrySettings var6 = (PPRegistry$RegistrySettings)PressurePlates.get(var5);

            if (var6.xCoord == var0 && var6.yCoord == var1 && var6.zCoord == var2 && var3 == var6.dimension)
            {
                var6.usesPassword = var4;
                return;
            }
        }
    }

    public static void setUsesPassword(TileEntityPressurePlate var0, int var1, boolean var2)
    {
        setUsesPassword(var0.x, var0.y, var0.z, var1, var2);
    }

    public static void setItem(int var0, int var1, int var2, int var3, ItemStack var4)
    {
        for (int var5 = 0; var5 < PressurePlates.size(); ++var5)
        {
            PPRegistry$RegistrySettings var6 = (PPRegistry$RegistrySettings)PressurePlates.get(var5);

            if (var6.xCoord == var0 && var6.yCoord == var1 && var6.zCoord == var2 && var3 == var6.dimension)
            {
                var6.item = var4;

                if (FMLCommonHandler.instance().getEffectiveSide().isServer())
                {
                    if (var4 != null)
                    {
                        PacketSendManager.sendItemStackToClients(var0, var1, var2, var4.id, var4.getData(), var4.count, var3);
                    }
                    else
                    {
                        PacketSendManager.sendItemStackToClients(var0, var1, var2, 0, 0, 0, var3);
                    }
                }

                return;
            }
        }
    }

    public static void setItem(TileEntityPressurePlate var0, int var1, ItemStack var2)
    {
        setItem(var0.x, var0.y, var0.z, var1, var2);
    }

    public void playerLoggedIn(Player var1, NetHandler var2, INetworkManager var3)
    {
        send = true;
        PacketSendManager.SendIsReadyToClient(var1);
    }

    public String connectionReceived(NetLoginHandler var1, INetworkManager var2)
    {
        return null;
    }

    public void connectionOpened(NetHandler var1, String var2, int var3, INetworkManager var4) {}

    public void connectionOpened(NetHandler var1, MinecraftServer var2, INetworkManager var3) {}

    public void connectionClosed(INetworkManager var1) {}

    public void clientLoggedIn(NetHandler var1, INetworkManager var2, Packet1Login var3) {}

    public void tickStart(EnumSet var1, Object ... var2) {}

    public void tickEnd(EnumSet var1, Object ... var2)
    {
        if (loggedIn && send)
        {
            for (int var3 = 0; var3 < PressurePlates.size(); ++var3)
            {
                PPRegistry$RegistrySettings var4 = (PPRegistry$RegistrySettings)PressurePlates.get(var3);

                if (var4.item != null)
                {
                    PacketSendManager.sendItemStackToClients(var4.xCoord, var4.yCoord, var4.zCoord, var4.item.id, var4.item.getData(), var4.item.count, var4.dimension);
                }
                else
                {
                    PacketSendManager.sendItemStackToClients(var4.xCoord, var4.yCoord, var4.zCoord, 0, 0, 0, var4.dimension);
                }

                PacketSendManager.sendUsesPasswordToClient(var4.xCoord, var4.yCoord, var4.zCoord, var4.dimension, Boolean.valueOf(var4.usesPassword));
            }

            loggedIn = false;
            send = false;
        }
    }

    public EnumSet ticks()
    {
        return EnumSet.of(TickType.PLAYER);
    }

    public String getLabel()
    {
        return "IronPP";
    }
}
