package xelitez.ironpp.client;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.src.GuiConfirmOpenLink;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiControls;
import net.minecraft.src.KeyBinding;

public class KeyHandler extends cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler{

	private GuiScreen currentscreen;
	private static KeyHandler INSTANCE;
	
	public KeyHandler() 
	{
		super(new KeyBinding[]{new KeyBinding("Open update URL", Keyboard.KEY_F4)}, new boolean[]{false});		
		INSTANCE = this;
	}

	@Override
	public String getLabel() {
		return "IronPPKey";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) 
	{

	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) 
	{
		currentscreen = FMLClientHandler.instance().getClient().currentScreen;
		if(kb == this.keyBindings[0] && !(currentscreen instanceof GuiControls))
		{
			FMLClientHandler.instance().displayGuiScreen(FMLClientHandler.instance().getClient().thePlayer, new ConfirmLink((GuiScreen)null, "http://adf.ly/Cg2Yv", 0));
		}
	}
	
	public String getKey(int i)
	{
		return keyBindings[0].keyDescription;
	}
	
	public static KeyHandler instance()
	{
		return INSTANCE;
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT, TickType.PLAYER);
	}

}
