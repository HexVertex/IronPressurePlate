package xelitez.ironpp;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import java.util.logging.Level;
import net.minecraft.server.v1_4_6.Block;
import net.minecraft.server.v1_4_6.EnumMobType;
import net.minecraft.server.v1_4_6.Item;
import net.minecraft.server.v1_4_6.ItemStack;
import net.minecraft.server.v1_4_6.LocaleI18n;
import net.minecraft.server.v1_4_6.Material;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

@Mod(
        modid = "IronPP",
        name = "Iron Pressure Plate mod",
        version = "3.3.1"
)
@NetworkMod(
        clientSideRequired = true,
        serverSideRequired = false,
        versionBounds = "[3.3,3.4)",
        channels = {"IPP"},
        packetHandler = PacketHandler.class,
        connectionHandler = PPRegistry.class
)
public class IronPP
{
    Configuration P;
    public int defaultPressurePlateIronId = 150;
    public int defaultAPressurePlateIronId = 151;
    private int BlockPPiD;
    private int BlockAPPiD;
    public static Block PressurePlateIron;
    public static Block APressurePlateIron;
    private boolean customTexture;
    private boolean checkForUpdates;
    private int PPT;
    @SidedProxy(
            clientSide = "xelitez.ironpp.client.ClientProxy",
            serverSide = "xelitez.ironpp.CommonProxy"
    )
    public static CommonProxy proxy = new CommonProxy();
    @Instance
    public static IronPP instance;

    @PreInit
    public void preload(FMLPreInitializationEvent var1)
    {
        var1.getModMetadata().version = Version.getVersion() + " for " + Version.MC;
        this.P = new Configuration(var1.getSuggestedConfigurationFile());

        try
        {
            this.P.load();
            this.BlockPPiD = this.P.getBlock("PressurePlateIronId", this.defaultPressurePlateIronId).getInt(150);
            this.BlockAPPiD = this.P.getBlock("AdvancedPressurePlateIronId", this.defaultAPressurePlateIronId).getInt(151);
            Configuration var10001 = this.P;
            Property var2 = this.P.get("general", "PressurePlateIronCustomTexture", false);
            var2.comment = "set to true to enable custom textures which must be located in \'.minecraft/bin/minecraft.jar\' or the mod zip file as \'IronPP.png\'";
            Property var3 = this.P.get("Updates", "Check for updates", true);
            Property var4 = this.P.get("Updates", "Ignore minor builds", true);
            Property var5 = this.P.get("Updates", "Ignore other MC versions", false);
            this.customTexture = var2.getBoolean(false);
            this.checkForUpdates = var3.getBoolean(true);
            Version.ignoremB = var4.getBoolean(true);
            Version.ignoreMC = var5.getBoolean(false);
        }
        catch (Exception var11)
        {
            FMLLog.log(Level.SEVERE, var11, "Failed to load Iron Pressure Plate configuration", new Object[0]);
        }
        finally
        {
            this.P.save();
        }

        try
        {
            if (this.checkForUpdates)
            {
                Version.checkForUpdates();
            }
        }
        catch (Exception var10)
        {
            FMLLog.log(Level.SEVERE, var10, "Failed to check for updates", new Object[0]);
        }
    }

    @Init
    public void load(FMLInitializationEvent var1)
    {
        this.PPT = Block.IRON_BLOCK.textureId;
        PressurePlateIron = (new BlockPressurePlate(this.BlockPPiD, Block.IRON_BLOCK.textureId, EnumMobType.PLAYERS, Material.ORE)).c(0.5F).a(Block.i).b("pressurePlate");
        APressurePlateIron = (new BlockAPressurePlate(this.BlockAPPiD, this.PPT, Material.ORE)).c(0.5F).a(Block.i).b("ApressurePlate");
        GameRegistry.registerBlock(PressurePlateIron, "Iron Pressure Plate");
        GameRegistry.registerBlock(APressurePlateIron, "Advanced Iron Pressure Plate");
        LanguageRegistry.addName(APressurePlateIron, "Advanced " + LocaleI18n.get("tile.pressurePlate.name"));
        GameRegistry.addRecipe(new ItemStack(PressurePlateIron, 1), new Object[] {"##", '#', Item.IRON_INGOT});
        GameRegistry.addRecipe(new ItemStack(APressurePlateIron, 2), new Object[] {"###", "@#@", '#', Item.IRON_INGOT, '@', Item.REDSTONE});
        GameRegistry.registerTileEntity(TileEntityPressurePlate.class, "Advanced Pressure Plate");
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);
        TickRegistry.registerTickHandler(new PPRegistry(), Side.CLIENT);
        TickRegistry.registerTickHandler(new PPRegistry(), Side.SERVER);
        proxy.RegisterKeyHandler();
    }
}
