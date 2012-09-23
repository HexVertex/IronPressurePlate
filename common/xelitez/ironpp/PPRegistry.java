package xelitez.ironpp;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ChunkCache;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetworkManager;
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
	
	public static void addPressurePlate(TileEntityPressurePlate tpp, int dimension)
	{
		int[] data = new int[3];
		if(tpp.getStackInSlot(0) != null)
		{
			data[0] = tpp.getStackInSlot(0).itemID;
			data[1] = tpp.getStackInSlot(0).getItemDamage();
			data[2] = tpp.getStackInSlot(0).stackSize;
		}
		else
		{
			data[0] = 0;
			data[1] = 0;
			data[2] = 0;
		}
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == tpp.xCoord && tempdata.yCoord == tpp.yCoord && tempdata.zCoord == tpp.xCoord && tempdata.dimension == dimension)
			{
				return;
			}
		}
		PressurePlates.add(new RegistrySettings(tpp.xCoord, tpp.yCoord, tpp.zCoord, data[0], data[1], data[2], tpp.worldObj.provider.worldType));
		return;
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

	public static boolean getContainsPressurePlate(TileEntityPressurePlate tpp, int dimension)
	{
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == tpp.xCoord && tempdata.yCoord == tpp.yCoord && tempdata.zCoord == tpp.xCoord && tempdata.dimension == dimension)
			{
				return true;
			}
		}
		return false;
	}
	
	public static ItemStack getItem(TileEntityPressurePlate tpp, int dimension)
	{
		World world;
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			RegistrySettings tempdata = (RegistrySettings)PressurePlates.get(var1);
			if(tempdata.xCoord == tpp.xCoord && tempdata.yCoord == tpp.yCoord && tempdata.zCoord == tpp.zCoord && dimension == tempdata.dimension)
			{
				if(tempdata.itemId == 0 && tempdata.stackSize == 0 && tempdata.itemDamage == 0)
				{
					return null;
				}
				else
				{
					return new ItemStack(tempdata.itemId, tempdata.stackSize, tempdata.itemDamage);
				}
			}
		}
		return null;
	}
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			NetworkManager manager) 
	{
		send = true;
		PacketSendManager.SendIsReadyToClient(player);
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			NetworkManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, NetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, NetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed(NetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			NetworkManager manager, Packet1Login login) 
	{	
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		if(loggedIn && send && ((this.world != null && !this.world.isRemote) || FMLCommonHandler.instance().getSide().isServer()))
		{
			for(int var1 = 0;var1 < PressurePlates.size();var1++)
			{
				RegistrySettings data = (RegistrySettings)PressurePlates.get(var1);
				PacketSendManager.sendItemStackToClients(data.xCoord, data.yCoord, data.zCoord, data.itemId, data.itemDamage, data.stackSize, data.dimension);
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
		public int itemId;
		public int itemDamage;
		public int stackSize;
		public int dimension;
		
		public RegistrySettings(int par1, int par2, int par3, int par4, int par5, int par6, int par7)
		{
			this.xCoord = par1;
			this.yCoord = par2;
			this.zCoord = par3;
			this.itemId = par4;
			this.itemDamage = par5;
			this.stackSize = par6;
			this.dimension = par7;
		}
	}

}
