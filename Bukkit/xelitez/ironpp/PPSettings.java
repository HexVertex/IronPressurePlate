package xelitez.ironpp;

import java.util.ArrayList;
import java.util.List;
import xelitez.ironpp.PPSettings$SettingsButton;
import xelitez.ironpp.PPSettings$SettingsLine;

public class PPSettings
{
    public static List lines;
    public static List buttons;
    public static List settingsLines;
    private static TileEntityPressurePlate tpp;

    public PPSettings(TileEntityPressurePlate var1)
    {
        lines = new ArrayList();
        buttons = new ArrayList();
        settingsLines = new ArrayList();
        tpp = var1;
    }

    public static void addLine(String var0)
    {
        String var1 = "";
        byte var2 = 14;

        while (var0.length() > var2)
        {
            int var3 = 0;

            while (true)
            {
                if (var3 < var2)
                {
                    int var4 = var2 - var3;
                    String var5 = var0.substring(0, var2);

                    if (var5.contains(" "))
                    {
                        if (var0.charAt(var4) != 32)
                        {
                            ++var3;
                            continue;
                        }

                        var1 = var0.substring(0, var4);
                        var0 = var0.substring(var4 + 1);
                    }
                    else
                    {
                        var1 = var0.substring(0, var2);
                        var0 = var0.substring(var2 + 1);
                    }
                }

                if (!var1.matches(""))
                {
                    lines.add(var1);
                }

                break;
            }
        }

        lines.add(var0);
    }

    public static void addLineWithButton(String var0, String var1, String var2, boolean var3, int var4)
    {
        for (int var5 = 0; var5 < buttons.size(); ++var5)
        {
            PPSettings$SettingsButton var6 = (PPSettings$SettingsButton)buttons.get(var5);

            if (var6.ID == var4)
            {
                return;
            }
        }

        addLine(var0);
        addLine(" ");
        buttons.add(new PPSettings$SettingsButton(lines.size(), var1, var2, var3, var4));
    }

    public static void addClickableLine(String var0, int var1)
    {
        for (int var2 = 0; var2 < settingsLines.size(); ++var2)
        {
            PPSettings$SettingsLine var3 = (PPSettings$SettingsLine)settingsLines.get(var2);

            if (var3.ID == var1)
            {
                return;
            }
        }

        addLine(" ");
        settingsLines.add(new PPSettings$SettingsLine(lines.size(), var0, var1));
    }
}
