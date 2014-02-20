package xelitez.ironpp;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;

public class PPRegistry
{
	public static final PPRegistry INSTANCE = new PPRegistry();
	
    private static List<RegistrySettings> PressurePlates;
    public static boolean loggedIn = false;
    public static boolean send = false;
    public static boolean sendToServer = false;
    static World world = null;
    public static PPRegistry pp;
    private boolean notify = false;

    public PPRegistry()
    {
        PPRegistry.PressurePlates = new ArrayList<RegistrySettings>();
        pp = this;
    }

    public static void addPressurePlate(int par1, int par2, int par3, int dimension, boolean b, ItemStack item)
    {
        for (int var1 = 0; var1 < PressurePlates.size(); var1++)
        {
            RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);

            if (tempdata.xCoord == par1 && tempdata.yCoord == par2 && tempdata.zCoord == par3 && tempdata.dimension == dimension)
            {
                return;
            }
        }

        PressurePlates.add(new RegistrySettings(par1, par2, par3, item, dimension, b));
        return;
    }

    public static void addPressurePlate(TileEntityPressurePlate tpp, int dimension)
    {
        boolean b = false;

        if (tpp.settings != null)
        {
            b = tpp.getIsEnabled(2);
        }

        addPressurePlate(tpp.xCoord, tpp.yCoord, tpp.zCoord, dimension, b, tpp.getStackInSlot(0));
    }

    public static void removePressurePlate(TileEntityPressurePlate tpp, int dimension)
    {
        for (int var1 = 0; var1 < PressurePlates.size(); var1++)
        {
            RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);

            if (tempdata.xCoord == tpp.xCoord && tempdata.yCoord == tpp.yCoord && tempdata.zCoord == tpp.zCoord && tempdata.dimension == dimension)
            {
                PressurePlates.remove(var1);
                return;
            }
        }
    }

    public static boolean getContainsPressurePlate(int i, int j, int k, int dimension)
    {
        for (int var1 = 0; var1 < PressurePlates.size(); var1++)
        {
            RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);

            if (tempdata.xCoord == i && tempdata.yCoord == j && tempdata.zCoord == k && tempdata.dimension == dimension)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean getContainsPressurePlate(TileEntityPressurePlate tpp, int dimension)
    {
        return getContainsPressurePlate(tpp.xCoord, tpp.yCoord, tpp.zCoord, dimension);
    }

    public static ItemStack getItem(int x, int y, int z, int dimension)
    {
        for (int var1 = 0; var1 < PressurePlates.size(); var1++)
        {
        	if(PressurePlates.size() <= var1)
        	{
        		return null;
        	}
            RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);

            if (tempdata.xCoord == x && tempdata.yCoord == y && tempdata.zCoord == z && dimension == tempdata.dimension)
            {
                return tempdata.item;
            }
        }

        return null;
    }

    public static ItemStack getItem(TileEntityPressurePlate tpp, int dimension)
    {
        return getItem(tpp.xCoord, tpp.yCoord, tpp.zCoord, dimension);
    }

    public static boolean getUsesPassword(int x, int y, int z, int dimension)
    {
        for (int var1 = 0; var1 < PressurePlates.size(); var1++)
        {
            RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);

            if (tempdata.xCoord == x && tempdata.yCoord == y && tempdata.zCoord == z && dimension == tempdata.dimension)
            {
                return tempdata.usesPassword;
            }
        }

        return true;
    }

    public static boolean getUsesPassword(TileEntityPressurePlate tpp, int dimension)
    {
        return getUsesPassword(tpp.xCoord, tpp.yCoord, tpp.zCoord, dimension);
    }

    public static void setUsesPassword(int x, int y, int z, int dimension, boolean b)
    {
        for (int var1 = 0; var1 < PressurePlates.size(); var1++)
        {
            RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);

            if (tempdata.xCoord == x && tempdata.yCoord == y && tempdata.zCoord == z && dimension == tempdata.dimension)
            {
                tempdata.usesPassword = b;
                return;
            }
        }
    }

    public static void setUsesPassword(TileEntityPressurePlate tpp, int dimension, boolean b)
    {
        setUsesPassword(tpp.xCoord, tpp.yCoord, tpp.zCoord, dimension, b);
    }

    public static void setItem(int x, int y, int z, int dimension, ItemStack item)
    {
        for (int var1 = 0; var1 < PressurePlates.size(); var1++)
        {
            RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);

            if (tempdata.xCoord == x && tempdata.yCoord == y && tempdata.zCoord == z && dimension == tempdata.dimension)
            {
                tempdata.item = item;

                if (FMLCommonHandler.instance().getEffectiveSide().isServer())
                {
                    if (item != null)
                    {
                        PacketSendManager.sendItemStackToClients(x, y, z, item.getItem(), item.getItemDamage(), item.stackSize, dimension);
                    }
                    else
                    {
                        PacketSendManager.sendItemStackToClients(x, y, z, Item.getItemById(0), 0, 0, dimension);
                    }
                }

                return;
            }
        }
    }

    public static void setItem(TileEntityPressurePlate tpp, int dimension, ItemStack item)
    {
        setItem(tpp.xCoord, tpp.yCoord, tpp.zCoord, dimension, item);
    }

	@SubscribeEvent
    public void clientLoggedIn(PlayerEvent.PlayerLoggedInEvent evt)
    {
		if(FMLCommonHandler.instance().getSide().isClient())
		{
	    	if (Version.notify && !this.notify && !Version.registered)
	    	{
	    		evt.player.addChatMessage(new ChatComponentText("\u00a7eIron Pressure Plate failed to register to the XEliteZ UpdateUtility. You should download it if you can. You can disable this message in the config file."));
	    		this.notify = true;
	    	}
	        if (Version.available && !Version.registered)
	        {
	        	evt.player.addChatMessage(new ChatComponentText("A new version of the Iron Pressure Plate mod is avalable(" + Version.color + Version.newVersion + "\u00a7f)."));
	        }
		}
		else
		{
	        send = true;
	        PacketSendManager.SendIsReadyToClient((EntityPlayerMP) evt.player);
		}
    }

	@SubscribeEvent
    public void onTickClient(TickEvent.ClientTickEvent evt)
    {
        if (sendToServer && evt.type == Type.SERVER)
        {
            if (FMLClientHandler.instance().getClient().theWorld != null)
            {
                PacketSendManager.sendIsReadyToServer();
                sendToServer = false;
            }
        }
    }
	
	@SubscribeEvent
    public void onTickServer(TickEvent.ServerTickEvent evt)
    {
        if (loggedIn && send && evt.type == Type.CLIENT)
        {
            for (int var1 = 0; var1 < PressurePlates.size(); var1++)
            {
                RegistrySettings data = (RegistrySettings)PressurePlates.get(var1);

                if (data.item != null)
                {
                    PacketSendManager.sendItemStackToClients(data.xCoord, data.yCoord, data.zCoord, data.item.getItem(), data.item.getItemDamage(), data.item.stackSize, data.dimension);
                }
                else
                {
                    PacketSendManager.sendItemStackToClients(data.xCoord, data.yCoord, data.zCoord, Item.getItemById(0), 0, 0, data.dimension);
                }

                PacketSendManager.sendUsesPasswordToClient(data.xCoord, data.yCoord, data.zCoord, data.dimension, data.usesPassword);
            }

            loggedIn = false;
            send = false;
        }
    }

    public static class RegistrySettings
    {
        public int xCoord;
        public int yCoord;
        public int zCoord;
        public ItemStack item;
        public int dimension;
        public boolean usesPassword;

        public RegistrySettings(int par1, int par2, int par3, ItemStack item, int par7, boolean usesPassword)
        {
            this.xCoord = par1;
            this.yCoord = par2;
            this.zCoord = par3;
            this.item = item;
            this.dimension = par7;
            this.usesPassword = usesPassword;
        }
    }
}
