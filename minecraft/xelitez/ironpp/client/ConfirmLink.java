package xelitez.ironpp.client;

import java.net.URI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StringTranslate;

public class ConfirmLink extends GuiScreen
{
    /** The text shown for the first button in GuiYesNo */
    protected String buttonText1;

    /** The text shown for the second button in GuiYesNo */
    protected String buttonText2;
    /** Initialises Copy Button. */
    private String copyLinkButton;
    private String field_73946_b;
    private String Link;
    private int worldNumber;
    private String message1;
    private GuiScreen parentScreen;
    private String URL;

    public ConfirmLink(GuiScreen par1GuiScreen, String par2Str, int par3)
    {
        this.parentScreen = par1GuiScreen;
        this.URL = par2Str;
        this.message1 = StringTranslate.getInstance().translateKey("chat.link.confirm");
        this.worldNumber = par3;
        StringTranslate var4 = StringTranslate.getInstance();
        this.buttonText1 = var4.translateKey("gui.yes");
        this.buttonText2 = var4.translateKey("gui.no");
        this.field_73946_b = var4.translateKey("chat.copy");
        this.copyLinkButton = var4.translateKey("chat.link.warning");
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.controlList.add(new GuiButton(0, this.width / 3 - 83 + 0, this.height / 6 + 96, 100, 20, this.buttonText1));
        this.controlList.add(new GuiButton(2, this.width / 3 - 83 + 105, this.height / 6 + 96, 100, 20, this.field_73946_b));
        this.controlList.add(new GuiButton(1, this.width / 3 - 83 + 210, this.height / 6 + 96, 100, 20, this.buttonText2));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            try
            {
                URI par1URI = new URI(URL);
                Class var2 = Class.forName("java.awt.Desktop");
                Object var3 = var2.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                var2.getMethod("browse", new Class[] {URI.class}).invoke(var3, new Object[] {par1URI});
            }
            catch (Throwable var4)
            {
                var4.printStackTrace();
            }

            mc.displayGuiScreen(parentScreen);
        }

        if (par1GuiButton.id == 2)
        {
            setClipboardString(this.URL);
            mc.displayGuiScreen(parentScreen);
        }
        else
        {
            mc.displayGuiScreen(parentScreen);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.copyLinkButton, this.width / 2, 110, 16764108);
        this.drawCenteredString(this.fontRenderer, "Iron Pressure Plate download page", this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRenderer, this.message1, this.width / 2, 70, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
