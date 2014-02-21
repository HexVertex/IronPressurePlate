/**
 * Base class for the Iron pressure plate mod.
 * Registers all the block stuff and packet channels.
 *
 * @author Kalvin
 */
package xelitez.ironpp;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Level;

import xelitez.ironpp.client.PPRenderer;
import xelitez.ironpp.netty.Pipeline;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Mod registration stuff.
 */
@Mod(	modid = "IronPP", 
		name = "Iron Pressure Plate mod",
		version = "3.4.2",
		acceptableRemoteVersions = "[3.4,3.5)")
public class IronPP
{
    Configuration P;
   
    private static HashMap<List<Object>, Boolean> nonSolidBlocks = new HashMap<List<Object>, Boolean>();

    /**
     * Registers the default BlockIDs and the blocks.
     */
    public static Block PressurePlateIron;
    public static Block APressurePlateIron;
    public static String customTexture;
    public boolean checkForUpdates;
    public static int changeSpeedSpeed;

    /**
     * Instances of the proxy and this class to be used by other classes.
     */
    @SidedProxy(clientSide = "xelitez.ironpp.client.ClientProxy", serverSide = "xelitez.ironpp.CommonProxy")
    public static CommonProxy proxy = new CommonProxy();
    @Instance(value = "IronPP")
    public static IronPP instance;
	public static ISimpleBlockRenderingHandler ppRenderer;
	
	public static final Pipeline pipeline = new Pipeline();

    /**
     * Since the INSTANCE is private I use this method to gain access to the class.
     */
	@EventHandler
    public void preload(FMLPreInitializationEvent evt)
    {
        evt.getModMetadata().version = Version.getVersion() + " for " + Version.MC;
        P = new Configuration(new File((File)FMLInjectionData.data()[6], "XEliteZ/IronPP.cfg")); //sets the file to create or load for the configuration file.
        try
        {
            P.load(); //loads the configuration file.
            Property PressurePlateIronTexture = P.get(Configuration.CATEGORY_GENERAL, "PressurePlateIronCustomTexture", "blockIron"); //gets the boolean if the user wants to use a custom texture.
            PressurePlateIronTexture.comment = "set to true to enable custom textures which must be located in '.minecraft/bin/minecraft.jar' or the mod zip file as 'IronPP.png'"; //adds a comment to the boolean section in the configuration.
            Property update = P.get("Updates", "Check for updates", true);
            Property ignoreMinorBuilds = P.get("Updates", "Ignore minor builds", true);
            Property ignoreOtherMCVersions = P.get("Updates", "Ignore other MC versions", false);
            Property notify = P.get("Updates", "Notify about XEZUpdateUtility", true);
            Version.notify = notify.getBoolean(true);
            customTexture = PressurePlateIronTexture.getString();
            checkForUpdates = update.getBoolean(true);
            Version.ignoremB = ignoreMinorBuilds.getBoolean(true);
            Version.ignoreMC = ignoreOtherMCVersions.getBoolean(false);
            changeSpeedSpeed = P.get(Configuration.CATEGORY_GENERAL, "ChangeSpeed", 5).getInt(5);
            }
        catch (Exception E)
        {
            FMLLog.log("IronPP", Level.ERROR, "Failed to load Iron Pressure Plate configuration", E);
        }
        finally
        {
            P.save(); //saves the configuration file.
        }
        //the next two methods creates the instances of the blocks used in this mod.
        PressurePlateIron = new BlockPressurePlate(customTexture, Sensitivity.players, Material.iron).setHardness(0.5F).setStepSound(Block.soundTypeMetal).setBlockName("pressurePlate").setBlockTextureName("iron_block");
        APressurePlateIron = new BlockAPressurePlate(customTexture, Material.iron).setHardness(0.5F).setStepSound(Block.soundTypeMetal).setBlockName("pressurePlateAdvanced").setBlockTextureName("iron_block");
        GameRegistry.registerBlock(PressurePlateIron, "MixedPressurePlate"); //Registers the block in the game.(replaces ModLoaders registerBlock method)
        GameRegistry.registerBlock(APressurePlateIron, "AdvancedPressurePlate");
    }
    /**
     * forge load method to load and register everything needed.
     * @param evt
     */
	@EventHandler
    public void load(FMLInitializationEvent evt)
    {
        GameRegistry.addRecipe(new ItemStack(PressurePlateIron, 1), new Object[] {"@#@", '#', Items.iron_ingot, '@', Items.gold_ingot}); //Registers a new recipe used in the crafting bench.(replaces ModLoaders addRecipe method)
        GameRegistry.addRecipe(new ItemStack(APressurePlateIron, 2), new Object[] {" D ", "A#A", "@#@", '#', Items.iron_ingot, '@', Items.redstone, 'A', Items.gold_ingot, 'D', Items.diamond});
        GameRegistry.registerTileEntity(TileEntityPressurePlate.class, "Advanced Pressure Plate"); //registers the TileEntity of the advanced iron pressure plate.(replaces ModLoaders registerTileEntity method)
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy); //Registers a GuiHandler assigned to this mod.(mostly for Network SMP Gui's)
		FMLCommonHandler.instance().bus().register(PPRegistry.INSTANCE);
        if(evt.getSide().isClient())
        {
        	ppRenderer = new PPRenderer(RenderingRegistry.getNextAvailableRenderId());
        	RenderingRegistry.registerBlockHandler(ppRenderer);
        }
        pipeline.initalise();
        proxy.RegisterKeyHandler();
        PPSettings.addLineWithButton("Unlisted players are by default:", "Enabled", "Disabled", false, 0);
        PPSettings.addLineWithButton("Sound is:", "On", "Off", true, 1);
        PPSettings.addLineWithButton("Password", "Enabled", "Disabled", false, 2);
        PPSettings.addClickableLine("Set Password", 0);
        PPSettings.addLineWithButton("Ask password on break", "Yes", "No", false, 3);
        PPSettings.addLine("Note: if you have set no password but enabled password then just press enter if you get stuck on the gui screen");
        try
        {
            if (checkForUpdates)
            {
				Class<? extends Object> clazz = Class.forName("xelitez.updateutility.UpdateRegistry");
                Method registermod = clazz.getDeclaredMethod("addMod", Object.class, Object.class);
                registermod.invoke(null, this, new Update());
                Version.registered = true;
            }
        }
        catch (Exception E)
        {
        	FMLLog.info("IronPP", "IronPressurePlate failed to register to the XEZUpdateUtility");
        	FMLLog.info("IronPP", "It isn't required but you should download it if possible");
            if (checkForUpdates)
            {
                Version.checkForUpdatesNoXEZ();
            }
        }
    	registerNonSolidBlocks();
    }
	
	@EventHandler
    public void postload(FMLPostInitializationEvent evt)
    {
		pipeline.postInitialise();
    }
    
    private void registerNonSolidBlocks()
    {
    	addNonSolidBlock(Blocks.glass, 0);
    	addNonSolidBlock(Blocks.leaves, 0);
    	addNonSolidBlock(Blocks.leaves, 1);
    	addNonSolidBlock(Blocks.leaves, 2);
    	addNonSolidBlock(Blocks.leaves, 3);
    	addNonSolidBlock(Blocks.snow_layer, 7);
    	addNonSolidBlock(Blocks.ice, 0);
    	addNonSolidBlock(Blocks.cactus, 0);
    	addNonSolidBlock(Blocks.tnt, 0);
    	addNonSolidBlock(Blocks.beacon, 0);
    	addNonSolidBlock(Blocks.leaves2, 0);
    	addNonSolidBlock(Blocks.leaves2, 1);
    	for(int i = 0;i < 16;i++)
    	{
        	addNonSolidBlock(Blocks.stained_glass, i);
    	}
    }
    
    public static void addNonSolidBlock(Block block, int metadata)
    {
    	nonSolidBlocks.put(Arrays.asList(block, metadata), true);
    }
    
    public static void disableNonSolidBlock(Block block, int metadata)
    {
    	nonSolidBlocks.put(Arrays.asList(block, metadata), false);
    }
    
    public static boolean getNonSolidBlockEnabled(Block block, int metadata)
    {
    	if(nonSolidBlocks.containsKey(Arrays.asList(block, metadata)))
    	{
    		return nonSolidBlocks.get(Arrays.asList(block, metadata));
    	}
    	else
    	{
    		return false;
    	}
    }
}
