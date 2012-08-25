package xelitez.ironpp.client;

import xelitez.ironpp.CommonProxy;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.src.World;

public class ClientProxy extends CommonProxy
{
	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}
}
