package xelitez.ironpp.netty;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import xelitez.ironpp.PacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class PacketPressurePlateData extends Packet
{
	private ByteBuf data;
	
	public PacketPressurePlateData()
	{
		
	}
	
	public PacketPressurePlateData(byte[] data)
	{
		this.data = Unpooled.wrappedBuffer(data);
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) 
	{
		if(data != null)
		{
			buffer.writeBytes(data);
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) 
	{
		data = buffer.copy();
	}

	@Override
	public void handleClientSide(EntityPlayer player) 
	{
        ByteArrayDataInput dat = ByteStreams.newDataInput(data.array());
        short ID = dat.readShort();
		PacketHandler.INSTANCE.handleClientPacket(this, player, dat, ID);
	}

	@Override
	public void handleServerSide(EntityPlayer player) 
	{
        ByteArrayDataInput dat = ByteStreams.newDataInput(data.array());
        short ID = dat.readShort();
		PacketHandler.INSTANCE.handleServerPacket(this, player, dat, ID);
	}
	
	public ByteBuf getData()
	{
		return data;
	}

}
