package xelitez.ironpp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import net.minecraft.server.v1_4_6.MinecraftServer;

public class Version
{
    public static int majorVersion = 3;
    public static int minorVersion = 3;
    public static int majorBuild = 2;
    public static int minorBuild = 4;
    public static String MC = "MC:1.4.6";
    public static boolean ignoremB = true;
    public static boolean ignoreMC = false;
    public static String newVersion;
    public static boolean available = false;
    public static String color = "";

    public static String getVersion()
    {
        return produceVersion(majorVersion, minorVersion, majorBuild, minorBuild);
    }

    private static String produceVersion(int var0, int var1, int var2, int var3)
    {
        boolean var4 = var1 != 0;
        boolean var5 = var2 != 0;
        boolean var6 = var3 != 0;
        StringBuilder var7 = new StringBuilder();
        var7.append(var0);

        if (var4)
        {
            var7.append(".");
            var7.append(var1);
        }

        if (var5)
        {
            var7.append(".");
            var7.append(var2);
        }

        if (var6)
        {
            var7.append(".");
            var7.append(var3);
        }

        return var7.toString();
    }

    public static void checkForUpdates()
    {
        ArrayList var0 = new ArrayList();
        int var1 = 0;
        int var2 = 0;
        int var3 = 0;
        int var4 = 0;
        String var5 = "";

        try
        {
            FMLCommonHandler.instance().getMinecraftServerInstance();
            MinecraftServer.log.info("Checking for updates of the Iron Pressure Plate mod...");
            URL var6 = new URL("https://raw.github.com/XEZKalvin/IronPressurePlate/master/common/xelitez/ironpp/Version.java");
            BufferedReader var7 = new BufferedReader(new InputStreamReader(var6.openStream()));
            String var8;

            while ((var8 = var7.readLine()) != null)
            {
                var0.add(var8);
            }

            var7.close();
        }
        catch (MalformedURLException var9)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance();
            MinecraftServer.log.fine("Unable to check for updates");
        }
        catch (IOException var10)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance();
            MinecraftServer.log.fine("Unable to check for updates");
        }

        for (int var11 = 0; var11 < var0.size(); ++var11)
        {
            String var12 = "";

            if (var0.get(var11) != null)
            {
                var12 = (String)var0.get(var11);
            }

            if (var12 != null && !var12.matches(""))
            {
                if (var12.contains("public static int majorVersion") && !var12.contains("\"public static int majorVersion\""))
                {
                    var12 = var12.substring(var12.indexOf("= ") + 2, var12.indexOf(59));
                    var1 = Integer.parseInt(var12);
                }

                if (var12.contains("public static int minorVersion") && !var12.contains("\"public static int minorVersion\""))
                {
                    var12 = var12.substring(var12.indexOf("= ") + 2, var12.indexOf(59));
                    var2 = Integer.parseInt(var12);
                }

                if (var12.contains("public static int majorBuild") && !var12.contains("\"public static int majorBuild\""))
                {
                    var12 = var12.substring(var12.indexOf("= ") + 2, var12.indexOf(59));
                    var3 = Integer.parseInt(var12);
                }

                if (var12.contains("public static int minorBuild") && !var12.contains("\"public static int minorBuild\""))
                {
                    var12 = var12.substring(var12.indexOf("= ") + 2, var12.indexOf(59));
                    var4 = Integer.parseInt(var12);
                }

                if (var12.contains("public static String MC") && !var12.contains("\"public static String MC\"") && var12.contains("MC:") && !var12.contains("\"MC:\""))
                {
                    var12 = var12.substring(var12.indexOf("MC:") + 3, var12.indexOf("\";"));
                    var5 = var12;
                }
            }
        }

        if ((!getVersion().matches(produceVersion(var1, var2, var3, var4)) || !MC.matches("MC:" + var5)) && !produceVersion(var1, var2, var3, var4).matches("0") && (ignoreMC && MC.matches("MC:" + var5) || !ignoreMC && !MC.matches("MC:" + var5) || ignoremB && !produceVersion(var1, var2, var3, 0).matches(produceVersion(majorVersion, minorVersion, majorBuild, 0)) || !ignoremB && !getVersion().matches(produceVersion(var1, var2, var3, var4))))
        {
            available = true;
            newVersion = produceVersion(var1, var2, var3, var4);

            if (!var5.matches(""))
            {
                newVersion = newVersion + " for MC:" + var5;
            }

            if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            {
                FMLCommonHandler.instance().getMinecraftServerInstance();
                MinecraftServer.log.info("A new version of the Iron Pressure Plate mod is available(" + newVersion + ")");
            }

            if (var4 != minorBuild && !ignoremB)
            {
                color = "\u00a7b";
            }

            if (var3 != majorBuild)
            {
                color = "\u00a7a";
            }

            if (var2 != minorVersion)
            {
                color = "\u00a7e";
            }

            if (var1 != majorVersion)
            {
                color = "\u00a73";
            }

            if (!MC.matches("MC:" + var5) && !ignoreMC)
            {
                color = "\u00a75";
            }
        }
    }
}
