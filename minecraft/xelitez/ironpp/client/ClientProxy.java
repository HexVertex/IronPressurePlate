package xelitez.ironpp.client;

import xelitez.ironpp.CommonProxy;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import net.minecraft.src.World;

public class ClientProxy extends CommonProxy
{
	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}
	
	@Override
	public void RegisterKeyHandler()
	{
		KeyBindingRegistry.registerKeyBinding(new KeyHandler());
	}
	
	@Override
	public String getKey(int i)
	{
		return KeyHandler.instance().getKey(i);
	}
}
