/**
 * a simple gui to remove or add players to the pressure plate
 *
 * @author Kalvin
 */
package xelitez.ironpp.client;

import org.lwjgl.input.Keyboard;

import xelitez.ironpp.PacketSendManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiButton;

public class GuiModifyPressurePlate extends GuiScreen
{
    /**
     * sets the stuff needed in this gui.
     */
    public static GuiScreen parentGuiScreen;
    private static GuiTextField theGuiTextField;

    /**
     * temporary data storage for text that needs to be shown.
     */
    private static String text = "";
    private static int duration = 0;

    /**
     * simple instance of the game
     */
    private Minecraft game;

    /**
     * main method to register the parrentGuiScreen and the game.
     * @param par1GuiScreen
     * @param mc
     */
    public GuiModifyPressurePlate(GuiScreen par1GuiScreen, Minecraft mc)
    {
        GuiModifyPressurePlate.parentGuiScreen = par1GuiScreen;
        this.game = mc;
    }

    /**
     * counts down the duration counter and disables the
     * buttons if the text field is empty.
     */
    public void updateScreen()
    {
        GuiModifyPressurePlate.theGuiTextField.updateCursorCounter();

        if (duration > 0)
        {
            duration--;
        }

        if (theGuiTextField.getText().trim().matches(""))
        {
            if (((GuiButton)this.buttonList.get(0)).enabled || ((GuiButton)this.buttonList.get(1)).enabled)
            {
                ((GuiButton)this.buttonList.get(0)).enabled = false;
            }

            ((GuiButton)this.buttonList.get(1)).enabled = false;
        }
        else
        {
            if (!((GuiButton)this.buttonList.get(0)).enabled || !((GuiButton)this.buttonList.get(1)).enabled)
            {
                ((GuiButton)this.buttonList.get(0)).enabled = true;
            }

            ((GuiButton)this.buttonList.get(1)).enabled = true;
        }
    }

    /**
     * registration for buttons and the text field.
     */
    @SuppressWarnings("unchecked")
	public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 180, this.height / 4 + 96 + 12, 90, 20, "Add"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 85, this.height / 4 + 96 + 12, 90, 20, "Remove"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 10, this.height / 4 + 96 + 12, 180, 20, "Exit"));
        GuiModifyPressurePlate.theGuiTextField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        GuiModifyPressurePlate.theGuiTextField.setFocused(true);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * says what happens when a button is clicked.
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (!game.theWorld.isRemote)
        {
            if (par1GuiButton.enabled)
            {
                if (par1GuiButton.id == 2)
                {
                    GuiAPressurePlate.lineUp();
                    GuiModifyPressurePlate.duration = 0;
                    this.mc.displayGuiScreen(GuiModifyPressurePlate.parentGuiScreen);
                }
                else if (par1GuiButton.id == 0)
                {
                    if (GuiAPressurePlate.tpp.addPlayer(GuiModifyPressurePlate.theGuiTextField.getText().trim()))
                    {
                        showText("Player added", 20);
                    }
                    else
                    {
                        showText("Player is already in list", 20);
                    }
                }
                else if (par1GuiButton.id == 1)
                {
                    if (GuiAPressurePlate.tpp.removePlayer(GuiModifyPressurePlate.theGuiTextField.getText().trim()))
                    {
                        showText("Player removed", 20);
                    }
                    else
                    {
                        showText("Player is not in list", 20);
                    }
                }
            }
        }
        else
        {
            if (par1GuiButton.enabled)
            {
                if (par1GuiButton.id == 2)
                {
                    PacketSendManager.sendGuiReloaderToServer(GuiAPressurePlate.tpp);
                    GuiAPressurePlate.lineUp();
                    PacketSendManager.requestPPDataFromServer(GuiAPressurePlate.tpp);
                    GuiModifyPressurePlate.duration = 0;
                    this.mc.displayGuiScreen(GuiModifyPressurePlate.parentGuiScreen);
                }
                else if (par1GuiButton.id == 0)
                {
                    PacketSendManager.sendAddPlayerPacketToServer(GuiAPressurePlate.tpp, GuiModifyPressurePlate.theGuiTextField.getText().trim(), this, game);
                }
                else if (par1GuiButton.id == 1)
                {
                    PacketSendManager.sendRemovePlayerPacketToServer(GuiAPressurePlate.tpp, GuiModifyPressurePlate.theGuiTextField.getText().trim(), this, game);
                }
            }
        }
    }

    /**
     * says to show a certain text for a certain duraion.
     * @param text1		text that needs to be shown
     * @param duration1	duration for which the text needs to be shown(in ticks).
     */
    public static void showText(String text1, int duration1)
    {
        text = text1;
        duration = duration1;
        theGuiTextField.setText("");
    }

    /**
     * says what happens if a key is typed.
     */
    protected void keyTyped(char par1, int par2)
    {
        GuiModifyPressurePlate.theGuiTextField.textboxKeyTyped(par1, par2);

        if (par1 == 13)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    /**
     * says what happens if the mouse clicks.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        GuiModifyPressurePlate.theGuiTextField.mouseClicked(par1, par2, par3);
        GuiModifyPressurePlate.theGuiTextField.setFocused(true);
    }

    /**
     * draws everything on the screen.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "add/remove players", this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, "Enter Player Name", this.width / 2 - 100, 47, 10526880);

        if (duration > 0)
        {
            this.drawString(this.fontRendererObj, text, this.width / 2 - 100, 85, 10526880);
        }

        this.drawString(this.fontRendererObj, new StringBuilder().append("Your username is: ").append(game.thePlayer.getCommandSenderName()).toString(), this.width / 2 - 100, 95, 10526880);
        GuiModifyPressurePlate.theGuiTextField.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }

    /**
     * says if the gui pauses the game.
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
