/**
 * Base class for the Iron pressure plate mod.
 * Registers all the block stuff and packet channels.
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import java.io.File;
import java.util.logging.Level;

import org.lwjgl.input.Keyboard;

import xelitez.ironpp.client.KeyHandler;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.NULL;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumMobType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.StatCollector;
import net.minecraft.src.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

/**
 * Mod registration stuff.
 */
@Mod(modid = "IronPP", name = "Iron Pressure Plate mod", version = "3.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, 
	versionBounds = "[3.1]",
	channels = {"IPP"},
	packetHandler = xelitez.ironpp.PacketHandler.class,
	connectionHandler = xelitez.ironpp.PPRegistry.class)
public class IronPP 
{
	Configuration P;
	
	/**
	 * Registers the default BlockIDs and the blocks.
	 */
	public String version = "3.1";
	public int defaultPressurePlateIronId = 150;
	public int defaultAPressurePlateIronId = 151;
	private int BlockPPiD;
	private int BlockAPPiD;
    public static Block PressurePlateIron;
    public static Block APressurePlateIron;
    private boolean customTexture;
    
    /**
     * Temporary storage for the iron pressure plate block texture index.
     */
    private int PPT;
    
    /**
     * Instances of the proxy and this class to be used by other classes.
     */
    @SidedProxy(clientSide = "xelitez.ironpp.client.ClientProxy", serverSide = "xelitez.ironpp.CommonProxy")
    public static CommonProxy proxy = new CommonProxy();
    @Instance
    public static IronPP instance;
    
    /**
     * Since the INSTANCE is private I use this method to gain access to the class.
     */
    @PreInit
    public void preload(FMLPreInitializationEvent evt)
    {
    	evt.getModMetadata().version = "v3.1 MC:1.3.2";
    	P = new Configuration(evt.getSuggestedConfigurationFile()); //sets the file to create or load for the configuration file.
    	try
    	{
    		P.load(); //loads the configuration file.
    		BlockPPiD = P.getOrCreateBlockIdProperty("PressurePlateIronId", defaultPressurePlateIronId).getInt(150); //gets the ID that's currently set in the configuration file or sets it with the default.
    		BlockAPPiD = P.getOrCreateBlockIdProperty("AdvancedPressurePlateIronId", defaultAPressurePlateIronId).getInt(151);	
    		Property PressurePlateIronTexture = P.getOrCreateBooleanProperty("PressurePlateIronCustomTexture", P.CATEGORY_GENERAL, false); //gets the boolean if the user wants to use a custom texture.
    		PressurePlateIronTexture.comment = "set to true to enable custom textures which must be located in '.minecraft/bin/minecraft.jar' or the mod zip file as 'IronPP.png'"; //adds a comment to the boolean section in the configuration.
    		customTexture = PressurePlateIronTexture.getBoolean(false);
    	}
    	catch(Exception E)
    	{
    		FMLLog.log(Level.SEVERE, E, "Failed to load Iron Pressure Plate configuration");
    	}
    	finally
    	{
    		P.save(); //saves the configuration file.
    	}
    }
    /**
     * forge load method to load and register everything needed.
     * @param evt
     */
	@Init
	public void load(FMLInitializationEvent evt) 
	{
		if(customTexture)
		{
			PPT = RenderingRegistry.addTextureOverride("/terrain.png", "/IronPP.png"); //replacement for ModLoaders addOverride method.
		}
		else
		{
			PPT = Block.blockSteel.blockIndexInTexture; //sets the block texture to the same as the one of the Iron Block
		}
		//the next two methods creates the instances of the blocks used in this mod.
		PressurePlateIron = new BlockPressurePlate(BlockPPiD, Block.blockSteel.blockIndexInTexture, EnumMobType.players, Material.iron).setHardness(0.5F).setStepSound(Block.soundMetalFootstep).setBlockName("pressurePlate");
		APressurePlateIron = new BlockAPressurePlate(BlockAPPiD, PPT, Material.iron).setHardness(0.5F).setStepSound(Block.soundMetalFootstep).setBlockName("ApressurePlate");
		GameRegistry.registerBlock(PressurePlateIron); //Registers the block in the game.(replaces ModLoaders registerBlock method)
		GameRegistry.registerBlock(APressurePlateIron);
		LanguageRegistry.addName(APressurePlateIron, new StringBuilder().append("Advanced ").append(StatCollector.translateToLocal("tile.pressurePlate.name")).toString()); //adds a display name for Items or Blocks.(replaces ModLoaders addName method) 
		GameRegistry.addRecipe(new ItemStack(PressurePlateIron, 1), new Object[] {"##", '#', Item.ingotIron}); //Registers a new recipe used in the crafting bench.(replaces ModLoaders addRecipe method)
		GameRegistry.addRecipe(new ItemStack(APressurePlateIron, 2), new Object[] {"###","@#@", '#', Item.ingotIron, '@', Item.redstone});
		GameRegistry.registerTileEntity(TileEntityPressurePlate.class, "Advanced Pressure Plate"); //registers the TileEntity of the advanced iron pressure plate.(replaces ModLoaders registerTileEntity method)
		NetworkRegistry.instance().registerGuiHandler(instance, proxy); //Registers a GuiHandler assigned to this mod.(mostly for Network SMP Gui's)
		TickRegistry.registerTickHandler(new PPRegistry(), Side.CLIENT);
		TickRegistry.registerTickHandler(new PPRegistry(), Side.SERVER);
		proxy.RegisterKeyHandler();
	}
}
