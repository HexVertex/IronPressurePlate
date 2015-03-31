package xelitez.ironpp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import xelitez.ironpp.PacketHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class PacketPressurePlateData extends Packet
{
	private ByteBuf data;
	
	public PacketPressurePlateData() {}
	
	public PacketPressurePlateData(byte[] data)
	{
		this.data = Unpooled.wrappedBuffer(data);
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

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		data = buf.copy();	
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		if(data != null)
		{
			buf.writeBytes(data);
		}	
	}

}
