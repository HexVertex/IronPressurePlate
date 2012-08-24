/**
 * Base class for the Iron pressure plate mod.
 * Registers all the block stuff and packet channels.
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import java.io.File;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.NULL;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumMobType;
import net.minecraft.src.ItemStack;
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
@Mod(modid = "IronPP", name = "Iron Pressure Plate mod", version = "v3.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, 
	versionBounds = "[4.0]",
	channels = {"IPP"},
	packetHandler = PacketHandler.class)
public class IronPP 
{
	Configuration P;
	
	/**
	 * Registers the default BlockIDs and the blocks.
	 */
	public int defaultPressurePlateIronId = 150;
	public int defaultAPressurePlateIronId = 151;
    public static Block PressurePlateIron;
    public static Block APressurePlateIron;
    
    /**
     * Temporary storage for the iron pressure plate block texture index.
     */
    private int PPT;
    
    /**
     * Instances of the proxy and this class to be used by other classes.
     */
    public static CommonProxy proxy = new CommonProxy();
    private static IronPP INSTANCE;
    
    /**
     * Since the INSTANCE is private I use this method to gain access to the class.
     */
    public static IronPP getInstance()
    {
    	return INSTANCE;
    }
    
    /**
     * forge load method to load and register everything needed.
     * @param evt
     */
	@Init
	public void load(FMLInitializationEvent evt) 
	{
		INSTANCE = this;  //Registers this class to instance.
		P = new Configuration(new File(Minecraft.getMinecraftDir(), "/XEliteZ/Iron Pressure Plate.cfg")); //sets the file to create or load for the configuration file.
		P.load(); //loads the configuration file.
		Property PressurePlateIronId = P.getOrCreateIntProperty("PressurePlateIronId", P.CATEGORY_BLOCK, defaultPressurePlateIronId); //gets the ID that's currently set in the configuration file or sets it with the default.
		Property APressurePlateIronId = P.getOrCreateIntProperty("AdvancedPressurePlateIronId", P.CATEGORY_BLOCK, defaultAPressurePlateIronId);	
		Property PressurePlateIronTexture = P.getOrCreateBooleanProperty("PressurePlateIronCustomTexture", P.CATEGORY_GENERAL, false); //gets the boolean if the user wants to use a custom texture.
		PressurePlateIronTexture.comment = "set to true to enable custom textures which must be located in '.minecraft/bin/minecraft.jar' or the mod zip file as 'IronPP.png'"; //adds a comment to the boolean section in the configuration.
		if(Boolean.parseBoolean(PressurePlateIronTexture.value))
		{
			PPT = RenderingRegistry.addTextureOverride("/terrain.png", "/IronPP.png"); //replacement for ModLoaders addOverride method.
		}
		else
		{
			PPT = Block.blockSteel.blockIndexInTexture; //sets the block texture to the same as the one of the Iron Block
		}
		//the next two methods creates the instances of the blocks used in this mod.
		PressurePlateIron = new BlockPressurePlate(Integer.parseInt(PressurePlateIronId.value), Block.blockSteel.blockIndexInTexture, EnumMobType.players, Material.iron).setHardness(0.5F).setStepSound(Block.soundMetalFootstep).setBlockName("pressurePlate");
		APressurePlateIron = new BlockAPressurePlate(Integer.parseInt(APressurePlateIronId.value), PPT, Material.iron).setHardness(0.5F).setStepSound(Block.soundMetalFootstep).setBlockName("ApressurePlate");
		GameRegistry.registerBlock(PressurePlateIron); //Registers the block in the game.(replaces ModLoaders registerBlock method)
		GameRegistry.registerBlock(APressurePlateIron);
		LanguageRegistry.addName(APressurePlateIron, new StringBuilder().append("Advanced ").append(StatCollector.translateToLocal("tile.pressurePlate.name")).toString()); //adds a display name for Items or Blocks.(replaces ModLoaders addName method) 
		GameRegistry.addRecipe(new ItemStack(PressurePlateIron, 1), new Object[] {"##", '#', Item.ingotIron}); //Registers a new recipe used in the crafting bench.(replaces ModLoaders addRecipe method)
		GameRegistry.addRecipe(new ItemStack(APressurePlateIron, 2), new Object[] {"###","@#@", '#', Item.ingotIron, '@', Item.redstone});
		GameRegistry.registerTileEntity(TileEntityPressurePlate.class, "Advanced Pressure Plate"); //registers the TileEntity of the advanced iron pressure plate.(replaces ModLoaders registerTileEntity method)
		NetworkRegistry.instance().registerGuiHandler(INSTANCE, proxy); //Registers a GuiHandler assigned to this mod.(mostly for Network SMP Gui's)
		P.save(); //saves the configuration file.
	}
}
