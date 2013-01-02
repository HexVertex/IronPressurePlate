package xelitez.ironpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Version
{
    public static int majorVersion = 3;
    public static int minorVersion = 3;
    public static int majorBuild = 3;
    public static int minorBuild = 7;
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

    private static String produceVersion(int var1, int var2, int var3, int var4)
    {
        boolean var5 = var2 != 0;
        boolean var6 = var3 != 0;
        boolean var7 = var4 != 0;
        StringBuilder Str1 = new StringBuilder();
        Str1.append(var1);

        if (var5)
        {
            Str1.append(".");
            Str1.append(var2);
        }

        if (var6)
        {
            Str1.append(".");
            Str1.append(var3);
        }

        if (var7)
        {
            Str1.append(".");
            Str1.append(var4);
        }

        return Str1.toString();
    }

    public static void checkForUpdates()
    {
        List strings = new ArrayList();
        int MV = 0;
        int mV = 0;
        int MB = 0;
        int mB = 0;
        String NMC = "";

        try
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().logger.info("Checking for updates of the Iron Pressure Plate mod...");
            URL url = new URL("https://raw.github.com/XEZKalvin/IronPressurePlate/master/common/xelitez/ironpp/Version.java");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;

            while ((str = in.readLine()) != null)
            {
                strings.add(str);
            }

            in.close();
        }
        catch (MalformedURLException e)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().logger.fine("Unable to check for updates");
        }
        catch (IOException e)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().logger.fine("Unable to check for updates");
        }

        for (int i = 0; i < strings.size(); i++)
        {
            String line = "";

            if (strings.get(i) != null)
            {
                line = (String)strings.get(i);
            }

            if (line != null && !line.matches(""))
            {
                if (line.contains("public static int majorVersion") && !line.contains("\"public static int majorVersion\""))
                {
                    line = line.substring(line.indexOf("= ") + 2, line.indexOf(';'));
                    MV = Integer.parseInt(line);
                }

                if (line.contains("public static int minorVersion") && !line.contains("\"public static int minorVersion\""))
                {
                    line = line.substring(line.indexOf("= ") + 2, line.indexOf(';'));
                    mV = Integer.parseInt(line);
                }

                if (line.contains("public static int majorBuild") && !line.contains("\"public static int majorBuild\""))
                {
                    line = line.substring(line.indexOf("= ") + 2, line.indexOf(';'));
                    MB = Integer.parseInt(line);
                }

                if (line.contains("public static int minorBuild") && !line.contains("\"public static int minorBuild\""))
                {
                    line = line.substring(line.indexOf("= ") + 2, line.indexOf(';'));
                    mB = Integer.parseInt(line);
                }

                if (line.contains("public static String MC") && !line.contains("\"public static String MC\"") && line.contains("MC:") && !line.contains("\"MC:\""))
                {
                    line = line.substring(line.indexOf("MC:") + 3, line.indexOf("\";"));
                    NMC = line;
                }
            }
        }

        if ((!getVersion().matches(produceVersion(MV, mV, MB, mB)) || !MC.matches("MC:" + NMC)) && !produceVersion(MV, mV, MB, mB).matches("0"))
        {
            if ((ignoreMC && MC.matches("MC:" + NMC) || (!ignoreMC && !MC.matches("MC:" + NMC))) || ((ignoremB && !produceVersion(MV, mV, MB, 0).matches(produceVersion(majorVersion, minorVersion, majorBuild, 0))) || (!ignoremB && !getVersion().matches(produceVersion(MV, mV, MB, mB)))))
            {
                available = true;
                newVersion = produceVersion(MV, mV, MB, mB);

                if (!NMC.matches(""))
                {
                    newVersion = newVersion + " for MC:" + NMC;
                }

                if (FMLCommonHandler.instance().getSide() == Side.SERVER)
                {
                    FMLCommonHandler.instance().getMinecraftServerInstance().logger.info("A new version of the Iron Pressure Plate mod is available(" + newVersion + ")");
                }

                if (mB != minorBuild && !ignoremB)
                {
                    color = "\u00a7b";
                }

                if (MB != majorBuild)
                {
                    color = "\u00a7a";
                }

                if (mV != minorVersion)
                {
                    color = "\u00a7e";
                }

                if (MV != majorVersion)
                {
                    color = "\u00a73";
                }
                
                if (!MC.matches("MC:" + NMC) && !ignoreMC)
                {
                    color = "\u00a75";
                }
            }
        }
    }
}
