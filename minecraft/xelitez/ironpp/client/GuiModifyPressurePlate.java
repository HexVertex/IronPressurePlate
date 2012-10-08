/**
 * a simple gui to remove or add players to the pressure plate
 * 
 * @author Kalvin
 */
package xelitez.ironpp.client;

import org.lwjgl.input.Keyboard;

import xelitez.ironpp.PacketSendManager;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;

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
        this.parentGuiScreen = par1GuiScreen;
        this.game = mc;
    }
    
    /**
     * counts down the duration counter and disables the 
     * buttons if the text field is empty.
     */
    public void updateScreen()
    {
        this.theGuiTextField.updateCursorCounter();
        if(duration > 0)
        {
        	duration--;
        }
        if(theGuiTextField.getText().trim().matches(""))
        {
        	if(((GuiButton)this.controlList.get(0)).enabled || ((GuiButton)this.controlList.get(1)).enabled)
        	((GuiButton)this.controlList.get(0)).enabled = false;
        	((GuiButton)this.controlList.get(1)).enabled = false;
        }
        else
        {
        	if(!((GuiButton)this.controlList.get(0)).enabled || !((GuiButton)this.controlList.get(1)).enabled)
        	((GuiButton)this.controlList.get(0)).enabled = true;
        	((GuiButton)this.controlList.get(1)).enabled = true;
        }
    }
    
    /**
     * registration for buttons and the text field.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.controlList.clear();
        this.controlList.add(new GuiButton(0, this.width / 2 - 180, this.height / 4 + 96 + 12, 90, 20, "Add"));
        this.controlList.add(new GuiButton(1, this.width / 2 - 85, this.height / 4 + 96 + 12, 90, 20, "Remove"));
        this.controlList.add(new GuiButton(2, this.width / 2 + 10, this.height / 4 + 96 + 12, 180, 20, "Exit"));
        this.theGuiTextField = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        this.theGuiTextField.setFocused(true);
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
    	if(!game.theWorld.isRemote)
    	{
	        if (par1GuiButton.enabled)
	        {
	            if (par1GuiButton.id == 2)
	            {
	            	((GuiAPressurePlate)this.parentGuiScreen).lineUp();
	            	this.duration = 0;
	                this.mc.displayGuiScreen(this.parentGuiScreen);
	            }
	            else if (par1GuiButton.id == 0)
	            {
	            	if(((GuiAPressurePlate)this.parentGuiScreen).tpp.addPlayer(this.theGuiTextField.getText().trim()))
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
	            	if(((GuiAPressurePlate)this.parentGuiScreen).tpp.removePlayer(this.theGuiTextField.getText().trim()))
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
	            	PacketSendManager.sendGuiReloaderToServer(((GuiAPressurePlate)this.parentGuiScreen).tpp);
	            	((GuiAPressurePlate)this.parentGuiScreen).lineUp();
	            	PacketSendManager.requestPPDataFromServer(((GuiAPressurePlate)this.parentGuiScreen).tpp);
	            	this.duration = 0;
	                this.mc.displayGuiScreen(this.parentGuiScreen);
	            }
	            else if (par1GuiButton.id == 0)
	            {
	            	PacketSendManager.sendAddPlayerPacketToServer(((GuiAPressurePlate)this.parentGuiScreen).tpp, this.theGuiTextField.getText().trim(), this, game);
	            }
	            else if (par1GuiButton.id == 1)
	            {
	            	PacketSendManager.sendRemovePlayerPacketToServer(((GuiAPressurePlate)this.parentGuiScreen).tpp, this.theGuiTextField.getText().trim(), this, game);
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
        this.theGuiTextField.textboxKeyTyped(par1, par2);

        if (par1 == 13)
        {
            this.actionPerformed((GuiButton)this.controlList.get(0));
        }
    }
    
    /**
     * says what happens if the mouse clicks.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.theGuiTextField.mouseClicked(par1, par2, par3);
        this.theGuiTextField.setFocused(true);
    }
    
    /**
     * draws everything on the screen.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "add/remove players", this.width / 2, 20, 16777215);
        this.drawString(this.fontRenderer, "Enter Player Name", this.width / 2 - 100, 47, 10526880);
        if(duration > 0)
        {
        	this.drawString(this.fontRenderer, text, this.width / 2 - 100, 85, 10526880);
        }
        this.drawString(this.fontRenderer, new StringBuilder().append("Your username is: ").append(game.thePlayer.username).toString(), this.width / 2 - 100, 95, 10526880);
        this.theGuiTextField.drawTextBox();
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
