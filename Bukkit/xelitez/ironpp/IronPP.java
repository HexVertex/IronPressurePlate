package xelitez.ironpp;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod$Init;
import cpw.mods.fml.common.Mod$Instance;
import cpw.mods.fml.common.Mod$PreInit;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import java.util.logging.Level;
import net.minecraft.server.Block;
import net.minecraft.server.EnumMobType;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.LocaleI18n;
import net.minecraft.server.Material;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

@Mod(
        modid = "IronPP",
        name = "Iron Pressure Plate mod",
        version = "3.2.2"
)
@NetworkMod(
        clientSideRequired = true,
        serverSideRequired = false,
        versionBounds = "[3.2.2]",
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
    private int PPT;
    @SidedProxy(
            clientSide = "xelitez.ironpp.client.ClientProxy",
            serverSide = "xelitez.ironpp.CommonProxy"
    )
    public static CommonProxy proxy = new CommonProxy();
    @Mod$Instance
    public static IronPP instance;

    @Mod$PreInit
    public void preload(FMLPreInitializationEvent var1)
    {
        var1.getModMetadata().version = Version.getVersion() + " for " + Version.MC;
        this.P = new Configuration(var1.getSuggestedConfigurationFile());

        try
        {
            this.P.load();
            this.BlockPPiD = this.P.getBlock("PressurePlateIronId", this.defaultPressurePlateIronId).getInt(150);
            this.BlockAPPiD = this.P.getBlock("AdvancedPressurePlateIronId", this.defaultAPressurePlateIronId).getInt(151);
            Configuration var10002 = this.P;
            Property var2 = this.P.get("PressurePlateIronCustomTexture", "general", false);
            var2.comment = "set to true to enable custom textures which must be located in \'.minecraft/bin/minecraft.jar\' or the mod zip file as \'IronPP.png\'";
            this.customTexture = var2.getBoolean(false);
        }
        catch (Exception var6)
        {
            FMLLog.log(Level.SEVERE, var6, "Failed to load Iron Pressure Plate configuration", new Object[0]);
        }
        finally
        {
            this.P.save();
        }
    }

    @Mod$Init
    public void load(FMLInitializationEvent var1)
    {
    	this.PPT = Block.IRON_BLOCK.textureId;

        PressurePlateIron = (new BlockPressurePlate(this.BlockPPiD, Block.IRON_BLOCK.textureId, EnumMobType.PLAYERS, Material.ORE)).c(0.5F).a(Block.i).b("pressurePlate");
        APressurePlateIron = (new BlockAPressurePlate(this.BlockAPPiD, this.PPT, Material.ORE)).c(0.5F).a(Block.i).b("ApressurePlate");
        GameRegistry.registerBlock(PressurePlateIron);
        GameRegistry.registerBlock(APressurePlateIron);
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
