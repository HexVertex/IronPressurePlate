/**
 * class to handle incomming packets
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.PPManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.server.FMLServerHandler;

public class PacketHandler implements IPacketHandler
{

	/**
	 * this method is called if it recieves a custom packet.
	 * this method reads if the packet must be recieved by the
	 * server or by the client and reads the packet ID to know
	 * how to handle the packets.
	 */
	@Override
	public void onPacketData(NetworkManager manager,
			Packet250CustomPayload packet, Player player) 
	{
		ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
		boolean toServer = dat.readBoolean();
		short ID = 0;
		ID = dat.readShort();
		if(ID == 0)
		{
			System.out.println("Invalid IronPP Packet recieved");
			return;
		}
		if(!toServer)
		{
			this.handleClientPacket(manager, packet, player, dat, ID);
		}
		else if(toServer)
		{
			this.handleServerPacket(manager, packet, player, dat, ID);
		}
		else
		{
			System.out.println("Invalid IronPP Packet recieved");
		}
	}
	
	/**
	 * this is a method for handling packets that were
	 * sent to the client.
	 * @param manager
	 * @param packet
	 * @param player
	 * @param dat		the data that it can read from
	 * @param ID		the packet ID
	 */
	public void handleClientPacket(NetworkManager manager, Packet250CustomPayload packet, Player player, ByteArrayDataInput dat, short ID)
	{
		EntityPlayer thePlayer = FMLClientHandler.instance().getClient().thePlayer;
		World world = FMLClientHandler.instance().getClient().theWorld;
		if(FMLClientHandler.instance().getClient().currentScreen instanceof GuiAPressurePlate)
		{
			if(ID == 1)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				if(GuiAPressurePlate.tpp.xCoord == coords[0] && GuiAPressurePlate.tpp.yCoord == coords[1] && GuiAPressurePlate.tpp.zCoord == coords[2])
				{
					PPManager.closeGuiScreen(thePlayer);
				}
				return;
			}
			if(ID == 2)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				int allowedmobs = dat.readInt();
				if(GuiAPressurePlate.tpp.xCoord == coords[0] && GuiAPressurePlate.tpp.yCoord == coords[1] && GuiAPressurePlate.tpp.zCoord == coords[2])
				{
					for(int var1 = 0;var1 < allowedmobs;var1++)
					{
						boolean bool = dat.readBoolean();
						GuiAPressurePlate.enabled[var1] = bool;
					}
				}
				return;
			}
			if(ID == 3)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				if(GuiAPressurePlate.tpp.xCoord == coords[0] && GuiAPressurePlate.tpp.yCoord == coords[1] && GuiAPressurePlate.tpp.zCoord == coords[2])
				{
					GuiAPressurePlate.tpp.allowedPlayers.clear();
					int allowedPlayers = dat.readInt();
					{
						for(int var1 = 0;var1 < allowedPlayers;var1++)
						{
							short nameLength = dat.readShort();
							String username = "";
							for(int var2 = 0;var2 < nameLength;var2++)
							{
								username = new StringBuilder().append(username).append(dat.readChar()).toString();
							}
							boolean bool = dat.readBoolean();
							GuiAPressurePlate.tpp.addPlayer(username);
							GuiAPressurePlate.tpp.setEnabledForPlayer(username, bool);
						}
						GuiAPressurePlate.lineUp();
					}
				}
				return;
			}
			if(ID == 4)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				int index = dat.readInt();
				if(GuiAPressurePlate.tpp.xCoord == coords[0] && GuiAPressurePlate.tpp.yCoord == coords[1] && GuiAPressurePlate.tpp.zCoord == coords[2])
				{
					GuiAPressurePlate.switchbutton(index);
				}
				return;
			}
		}
		if(FMLClientHandler.instance().getClient().currentScreen instanceof GuiModifyPressurePlate)
		{
			if(ID == 1)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				if(((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.xCoord == coords[0] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.yCoord == coords[1] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.zCoord == coords[2])
				{
					PPManager.closeGuiScreen(thePlayer);
				}
			}
			if(ID == 3)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				if(((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.xCoord == coords[0] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.yCoord == coords[1] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.zCoord == coords[2])
				{
					((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.allowedPlayers.clear();
					int allowedPlayers = dat.readInt();
					{
						for(int var1 = 0;var1 < allowedPlayers;var1++)
						{
							short nameLength = dat.readShort();
							String username = "";
							for(int var2 = 0;var2 < nameLength;var2++)
							{
								username = new StringBuilder().append(username).append(dat.readChar()).toString();
							}
							boolean bool = dat.readBoolean();
							((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.setEnabledForPlayer(username, bool);
						}
						((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).lineUp();
					}
				}
				return;
			}
			if(ID == 4)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				int index = dat.readInt();
				if(((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.xCoord == coords[0] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.yCoord == coords[1] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.zCoord == coords[2])
				{
					((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).switchbutton(index);
				}
				return;
			}
			if(ID == 5)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				if(((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.xCoord == coords[0] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.yCoord == coords[1] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.zCoord == coords[2])
				{
					boolean bool = dat.readBoolean();
					int usernamelength = dat.readInt();
					String username = "";
					for(int var2 = 0;var2 < usernamelength;var2++)
					{
						char c = dat.readChar();
						username = new StringBuilder().append(username).append(c).toString();
					}
					if(thePlayer.username.matches(username))
					{
						if(bool)
						{
							GuiModifyPressurePlate.showText("Player added", 20);
						}
						else
						{
							GuiModifyPressurePlate.showText("Player is already in list", 20);
						}
					}
				}
				return;
			}
			if(ID == 6)
			{
				int coords[]  = new int[3];
				for(int var1 = 0;var1 < 3;var1++)
				{
					coords[var1] = dat.readInt();
				}
				if(((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.xCoord == coords[0] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.yCoord == coords[1] && ((GuiAPressurePlate)GuiModifyPressurePlate.parentGuiScreen).tpp.zCoord == coords[2])
				{
					boolean bool = dat.readBoolean();
					int usernamelength = dat.readInt();
					String username = "";
					for(int var2 = 0;var2 < usernamelength;var2++)
					{
						char c = dat.readChar();
						username = new StringBuilder().append(username).append(c).toString();
					}
					if(thePlayer.username.matches(username))
					{
						if(bool)
						{
							GuiModifyPressurePlate.showText("Player removed", 20);
						}
						else
						{
							GuiModifyPressurePlate.showText("Player is not in list", 20);
						}
					}
				}
				return;
			}
		}
		System.out.println(new StringBuilder().append("Client recieved a packet with an invalid id: ").append(ID).toString());
	}
	
	/**
	 * method to handle packets that were sent to the server.
	 * mostly the same as above but with different handling.
	 * @param manager
	 * @param packet
	 * @param player
	 * @param dat
	 * @param ID
	 */
	public void handleServerPacket(NetworkManager manager, Packet250CustomPayload packet, Player player, ByteArrayDataInput dat, short ID)
	{
		EntityPlayer thePlayer = (EntityPlayer)player;
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().theWorldServer[thePlayer.dimension];
    	if(ID == 1)
        {
    		int coords[] = new int[3];
    		for(int var1 = 0;var1 < 3;var1++)
    		{
    			coords[var1] = dat.readInt();
    		}
    		TileEntityPressurePlate tpp = (TileEntityPressurePlate)world.getBlockTileEntity(coords[0], coords[1], coords[2]);
    		short var3 = dat.readShort();
    		StringBuilder var4 = new StringBuilder();
    		for(int var5 = 0;var5 < var3;var5++)
    		{
    			var4.append(dat.readChar());
    		}
    		String var6 = var4.toString();
    		tpp.switchMob(var6, world);
    		return;
        }
    	if (ID == 2)
    	{
    		int coords[] = new int[3];
    		for(int var1 = 0;var1 < 3;var1++)
    		{
    			coords[var1] = dat.readInt();
    		}
    		TileEntityPressurePlate tpp = (TileEntityPressurePlate)world.getBlockTileEntity(coords[0], coords[1], coords[2]);
    		short var3 = dat.readShort();
    		StringBuilder var4 = new StringBuilder();
    		for(int var5 = 0;var5 < var3;var5++)
    		{
    			var4.append(dat.readChar());
    		}
    		String var6 = var4.toString();
    		tpp.switchPlayer(var6, world);
    		return;
    	}
    	if (ID == 3)
    	{
        	int coords[] = new int[3];
        	for(int var1 = 0;var1 < 3;var1++)
        	{
        		coords[var1] = dat.readInt();
        	}
        	if(world.getBlockTileEntity(coords[0], coords[1], coords[2]) instanceof TileEntityPressurePlate)
        	{
        		TileEntityPressurePlate tpp = (TileEntityPressurePlate)world.getBlockTileEntity(coords[0], coords[1], coords[2]);
        		int usernameLength = dat.readInt();
        		String username = "";
        		for(int var2 = 0; var2 < usernameLength;var2++)
        		{
        			char c = dat.readChar();
        			username = new StringBuilder().append(username).append(c).toString();
        		}
        		
				int playerUsernameLength = dat.readInt();
				String playerUsername = "";
				for(int var2 = 0;var2 < playerUsernameLength;var2++)
				{
					char c = dat.readChar();
					playerUsername = new StringBuilder().append(playerUsername).append(c).toString();
				}
        		boolean bool = tpp.addPlayer(username);
        		PacketSendManager.sendAddBooleanToClient(bool, coords[0], coords[1], coords[2], playerUsername);
        		PacketSendManager.sendPressurePlatePlayerDataToClient(tpp);
        	}
        	return;
    	}
    	if (ID == 4)
    	{
    		int coords[] = new int[3];
    		for(int var1 = 0;var1 < 3;var1++)
    		{
    			coords[var1] = dat.readInt();
    		}
    		if(world.getBlockTileEntity(coords[0], coords[1], coords[2]) instanceof TileEntityPressurePlate)
    		{
    			TileEntityPressurePlate tpp = (TileEntityPressurePlate)world.getBlockTileEntity(coords[0], coords[1], coords[2]);
    			int usernameLength = dat.readInt();
    			String username = "";
    			for(int var2 = 0; var2 < usernameLength;var2++)
    			{
    				char c = dat.readChar();
    				username = new StringBuilder().append(username).append(c).toString();
    			}
    			String playerUsername = "";
    			int playerUsernameLength = dat.readInt();
    			for(int var3 = 0; var3 < playerUsernameLength;var3++)
    			{
    				char c = dat.readChar();
    				playerUsername = new StringBuilder().append(playerUsername).append(c).toString();
    			}
    			boolean bool = tpp.removePlayer(username);
    			PacketSendManager.sendRemoveBooleanToClient(bool, coords[0], coords[1], coords[2], playerUsername);
    	        PacketSendManager.sendPressurePlatePlayerDataToClient(tpp);
    		}	
    		return;
    	}	
    	if (ID == 5)
    	{
    		int coords[] = new int[3];
    		for(int var1 = 0;var1 < 3;var1++)
    		{
    			coords[var1] = dat.readInt();
    		}
    		if(world.getBlockTileEntity(coords[0], coords[1], coords[2]) instanceof TileEntityPressurePlate)
    		{
    			TileEntityPressurePlate tpp = (TileEntityPressurePlate)world.getBlockTileEntity(coords[0], coords[1], coords[2]);
    	        PacketSendManager.sendPressurePlatePlayerDataToClient(tpp);
    		}
    		return;
    	}
		if(ID == 6)
		{
   			int[] coords = new int[3];
   			for(int var2 = 0;var2 < 3;var2 ++)
   			{
   				coords[var2] = dat.readInt();
   			}
   			TileEntityPressurePlate tpp = null;
   			if(world.getBlockTileEntity(coords[0], coords[1], coords[2]) instanceof TileEntityPressurePlate)
   			{
   				tpp = (TileEntityPressurePlate)world.getBlockTileEntity(coords[0], coords[1], coords[2]);
   			}
   			if(tpp != null)
   			{
   		        PacketSendManager.sendPressurePlateMobDataToClient(tpp);
   		        PacketSendManager.sendPressurePlatePlayerDataToClient(tpp);
   			}
   			return;
		}
		
		System.out.println(new StringBuilder().append("Sever recieved a packet with an invalid id: ").append(ID).toString());
	}
	
	

}
