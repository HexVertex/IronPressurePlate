package xelitez.ironpp;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkCache;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

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
		this.PressurePlates = new ArrayList();
		pp = this;
	}
	
	public static void addPressurePlate(int par1, int par2, int par3, int dimension, boolean b, ItemStack item)
	{
		int[] data = new int[3];
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == par1 && tempdata.yCoord == par2 && tempdata.zCoord == par3 && tempdata.dimension == dimension)
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
		if(tpp.settings != null)
		{
			b = tpp.getIsEnabled(2);
		}
		addPressurePlate(tpp.xCoord, tpp.yCoord, tpp.zCoord, dimension, b, tpp.getStackInSlot(0));
	}
	
	public static void removePressurePlate(TileEntityPressurePlate tpp, int dimension)
	{
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == tpp.xCoord && tempdata.yCoord == tpp.yCoord && tempdata.zCoord == tpp.zCoord && tempdata.dimension == dimension)
			{
				PressurePlates.remove(var1);
				return;
			}
		}
	}

	public static boolean getContainsPressurePlate(int i, int j, int k, int dimension)
	{
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == i && tempdata.yCoord == j && tempdata.zCoord == k && tempdata.dimension == dimension)
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
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == x && tempdata.yCoord == y && tempdata.zCoord == z && dimension == tempdata.dimension)
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
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == x && tempdata.yCoord == y && tempdata.zCoord == z && dimension == tempdata.dimension)
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
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == x && tempdata.yCoord == y && tempdata.zCoord == z && dimension == tempdata.dimension)
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
	
	public static void setItem(int x, int y, int z, int dimension,ItemStack item)
	{
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == x && tempdata.yCoord == y && tempdata.zCoord == z && dimension == tempdata.dimension)
			{
				tempdata.item = item;
				if(FMLCommonHandler.instance().getEffectiveSide().isServer())
				{
					if(item != null)
					{
						PacketSendManager.sendItemStackToClients(x, y, z, item.itemID, item.getItemDamage(), item.stackSize, dimension);
					}
					else
					{
						PacketSendManager.sendItemStackToClients(x, y, z, 0, 0, 0, dimension);
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
	
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			INetworkManager manager) 
	{
		send = true;
		PacketSendManager.SendIsReadyToClient(player);
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			INetworkManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, INetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, INetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			INetworkManager manager, Packet1Login login) 
	{	
		if(Version.available)
		{
			clientHandler.getPlayer().addChatMessage("A new version of the \u00a7eIron Pressure Plate mod\u00a7f is available(" + Version.color + Version.newVersion + "\u00a7f)");
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		if(loggedIn && send)
		{
			for(int var1 = 0;var1 < PressurePlates.size();var1++)
			{
				RegistrySettings data = (RegistrySettings)PressurePlates.get(var1);
				if(data.item != null)
				{
					PacketSendManager.sendItemStackToClients(data.xCoord, data.yCoord, data.zCoord, data.item.itemID, data.item.getItemDamage(), data.item.stackSize, data.dimension);
				}
				else
				{
					PacketSendManager.sendItemStackToClients(data.xCoord, data.yCoord, data.zCoord, 0, 0, 0, data.dimension);
				}
				PacketSendManager.sendUsesPasswordToClient(data.xCoord, data.yCoord, data.zCoord, data.dimension, data.usesPassword);
			}
			loggedIn = false;
			send = false;
		}	
		if(sendToServer)
		{
			if(FMLClientHandler.instance().getClient().theWorld != null)
			{
				PacketSendManager.sendIsReadyToServer();
				sendToServer = false;
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "IronPP";
	}
	
	private static class RegistrySettings
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
