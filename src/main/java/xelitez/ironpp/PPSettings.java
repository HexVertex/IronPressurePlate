package xelitez.ironpp;

import java.util.ArrayList;
import java.util.List;

public class PPSettings
{
    public List<String> lines = new ArrayList<String>();
    public List<SettingsButton> buttons = new ArrayList<SettingsButton>();
    public List<SettingsLine> settingsLines = new ArrayList<SettingsLine>();

    public PPSettings()
    {
        this.addLineWithButton("Unlisted players are by default:", "Enabled", "Disabled", false, 0);
        this.addLineWithButton("Sound is:", "On", "Off", true, 1);
        this.addLineWithButton("Password", "Enabled", "Disabled", false, 2);
        this.addClickableLine("Set Password", 0);
        this.addLineWithButton("Ask password on break", "Yes", "No", false, 3);
        this.addLine("Note: if you have set no password but enabled password then just press enter if you get stuck on the gui screen");
    }
    
    public void addLine(String par1Str)
    {
        String var1 = "";
        int i = 14;

        while (par1Str.length() > i)
        {
            for (int var3 = 0; var3 < i; var3++)
            {
                int var4 = i - var3;
                String var5 = par1Str.substring(0, i);

                if (var5.contains(" "))
                {
                    if (par1Str.charAt(var4) == ' ')
                    {
                        var1 = par1Str.substring(0, var4);
                        par1Str = par1Str.substring(var4 + 1);
                        break;
                    }
                }
                else
                {
                    var1 = par1Str.substring(0, i);
                    par1Str = par1Str.substring(i + 1);
                    break;
                }
            }

            if (!var1.matches(""))
            {
                lines.add(var1);
            }
        }

        lines.add(par1Str);
    }

    public void addLineWithButton(String par1Str, String par2Str, String par3Str, boolean enabled, int ID)
    {
        for (int var1 = 0; var1 < buttons.size(); var1++)
        {
            SettingsButton button = (SettingsButton)buttons.get(var1);

            if (button.ID == ID)
            {
                return;
            }
        }

        addLine(par1Str);
        addLine(" ");
        buttons.add(new SettingsButton(lines.size(), par2Str, par3Str, enabled, ID));
    }

    public void addClickableLine(String s, int ID)
    {
        for (int var1 = 0; var1 < settingsLines.size(); var1++)
        {
            SettingsLine line = (SettingsLine)settingsLines.get(var1);

            if (line.ID == ID)
            {
                return;
            }
        }

        addLine(" ");
        settingsLines.add(new SettingsLine(lines.size(), s, ID));
    }

    public static class SettingsButton
    {
        public String TextEnabled;
        public String TextDisabled;
        public int line;
        public boolean enabled;
        public int ID;

        public SettingsButton(int line, String textEnabled, String TextDisabled, boolean enabled, int ID)
        {
            this.TextEnabled = textEnabled;
            this.TextDisabled = TextDisabled;
            this.line = line;
            this.enabled = enabled;
            this.ID = ID;
        }
    }

    public static class SettingsLine
    {
        public String Text;
        public int line;
        public int ID;

        public SettingsLine(int line, String text, int ID)
        {
            this.Text = text;
            this.line = line;
            this.ID = ID;
        }
    }
}
