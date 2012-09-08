package xelitez.ironpp;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.server.MinecraftServer;
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
	
	public PPRegistry()
	{
		this.PressurePlates = new ArrayList();
	}
	
	public static void addPressurePlate(TileEntityPressurePlate tpp)
	{
		int[] data = new int[6];
		if(tpp.getStackInSlot(0) != null)
		{
			data[0] = tpp.xCoord;
			data[1] = tpp.yCoord;
			data[2] = tpp.zCoord;
			data[3] = tpp.getStackInSlot(0).itemID;
			data[4] = tpp.getStackInSlot(0).getItemDamage();
			data[5] = tpp.getStackInSlot(0).stackSize;
		}
		else
		{
			data[0] = tpp.xCoord;
			data[1] = tpp.yCoord;
			data[2] = tpp.zCoord;
			data[3] = 0;
			data[4] = 0;
			data[5] = 0;
		}
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			int[] tempdata = (int[])PressurePlates.get(var1);
			if(tempdata[0] == data[0] && tempdata[1] == data[1] && tempdata[2] == data[2])
			{
				return;
			}
		}
		PressurePlates.add(data);
		return;
	}
	
	public static void removePressurePlate(TileEntityPressurePlate tpp)
	{
		int[] data = new int[6];
		if(tpp.getStackInSlot(0) != null)
		{
			data[0] = tpp.xCoord;
			data[1] = tpp.yCoord;
			data[2] = tpp.zCoord;
			data[3] = tpp.getStackInSlot(0).itemID;
			data[4] = tpp.getStackInSlot(0).getItemDamage();
			data[5] = tpp.getStackInSlot(0).stackSize;
		}
		else
		{
			data[0] = tpp.xCoord;
			data[1] = tpp.yCoord;
			data[2] = tpp.zCoord;
			data[3] = 0;
			data[4] = 0;
			data[5] = 0;
		}
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			int[] tempdata = (int[])PressurePlates.get(var1);
			if(tempdata[0] == data[0] && tempdata[1] == data[1] && tempdata[2] == data[2])
			{
				PressurePlates.remove(var1);
				return;
			}
		}
	}

	public static boolean getContainsPressurePlate(TileEntityPressurePlate tpp)
	{
		int[] data = new int[6];
		if(tpp.getStackInSlot(0) != null)
		{
			data[0] = tpp.xCoord;
			data[1] = tpp.yCoord;
			data[2] = tpp.zCoord;
			data[3] = tpp.getStackInSlot(0).itemID;
			data[4] = tpp.getStackInSlot(0).getItemDamage();
			data[5] = tpp.getStackInSlot(0).stackSize;
		}
		else
		{
			data[0] = tpp.xCoord;
			data[1] = tpp.yCoord;
			data[2] = tpp.zCoord;
			data[3] = 0;
			data[4] = 0;
			data[5] = 0;
		}
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			int[] tempdata = (int[])PressurePlates.get(var1);
			if(tempdata[0] == data[0] && tempdata[1] == data[1] && tempdata[2] == data[2])
			{
				return true;
			}
		}
		return false;
	}
	
	public static ItemStack getItem(int i, int j, int k)
	{
		int[] data = new int[6];
		data[0] = i;
		data[1] = j;
		data[2] = k;
		for(int var1 = 0;var1 < PressurePlates.size();var1++)
		{
			int[] tempdata = (int[])PressurePlates.get(var1);
			if(tempdata[0] == data[0] && tempdata[1] == data[1] && tempdata[2] == data[2])
			{
				if(tempdata[3] == 0 && tempdata[5] == 0 && tempdata[4] == 0)
				{
					return null;
				}
				else
				{
					return new ItemStack(tempdata[3], tempdata[5], tempdata[4]);
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
				int[] data = (int[])PressurePlates.get(var1);
				PacketSendManager.sendItemStackToClients(data[0], data[1], data[2], data[3], data[4], data[5]);
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

}
