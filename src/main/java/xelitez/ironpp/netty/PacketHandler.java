package xelitez.ironpp.netty;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketHandler implements IMessageHandler<PacketPressurePlateData, IMessage>
{

	@Override
	public IMessage onMessage(PacketPressurePlateData message, MessageContext ctx) 
	{   
		EntityPlayer player;
		switch (FMLCommonHandler.instance().getEffectiveSide()) {
		case CLIENT:
			
			player = Minecraft.getMinecraft().thePlayer;
			message.handleClientSide(player);
			break;

		case SERVER:
			player = ctx.getServerHandler().playerEntity;
			message.handleServerSide(player);
			break;

		default:
		}
		return null;
	}


}