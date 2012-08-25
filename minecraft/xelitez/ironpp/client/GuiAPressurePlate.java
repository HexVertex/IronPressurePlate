/**
 * main gui class for the advanced iron pressure plate
 * 
 * @author Kalvin
 */
package xelitez.ironpp.client;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import xelitez.ironpp.ContainerPressurePlate;
import xelitez.ironpp.PPPlayerList;
import xelitez.ironpp.PacketSendManager;
import xelitez.ironpp.TileEntityPressurePlate;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StatCollector;

public class GuiAPressurePlate extends GuiContainer
{
	/**
	 * two things I need to use in this mod.
	 */
    private ContainerPressurePlate cpp;
    public static TileEntityPressurePlate tpp;
    
    /**
     * a separate list of the enabled players and mobs
     */
    public static boolean[] enabled;
    public static boolean[] enabledPlayers;
    
    /**
     * stuff for the scrolling.
     * most credit for teaching me how this
     * works goes to Risugami.
     */
    private int listHeight;
    private int scrollY;
    private int scrollHeight;
    private boolean isScrolling;
    private static int playerListHeight;
    private int playerScrollY;
    private static int playerScrollHeight;
    private boolean playerIsScrolling;
    
    /**
     * sets the minecraft instance ready for usage.
     */
    private static Minecraft game = FMLClientHandler.instance().getClient();
    
    /**
     * checks the scaling
     */
    private static ScaledResolution scaling = null;
    
    /**
     * the stuff for the tabs.
     * (tabOpen is for if I want to add future tabs
     * and give them a ID to check)
     */
    private int tabOpen = 0;
    private boolean playerTabIsOpen = false;
    
    /**
     * the size of the Gui
     */
    private int xSize = 176; 
    private int ySize = 204;
    
    /**
     * this method gets called every game update.
     * I use it to close the tab if Player is not enabled
     * and to change the scrolling if it is in an invalid state.
     */
    public void updateScreen()
    {
    	if(game.theWorld.isRemote)
    	{
    		if(!this.enabled[0] && this.playerTabIsOpen)
    		{
    			this.playerTabIsOpen = false;
    		}
    	}
    	else
    	{
    		if(!this.tpp.findMobName("humanoid") && this.playerTabIsOpen)
    		{
    			this.playerTabIsOpen = false;
    		}
    	}
    	if(playerScrollY > playerListHeight)
    	{
    		playerScrollY = playerListHeight;
    	}
    	
		if (playerScrollY < 0)
		{
			playerScrollY = 0;
		}
    }
    
    /**
     * main method that gets called on the creation of the gui.
     * it does all the registering.
     * @param tpp
     */
    public GuiAPressurePlate(TileEntityPressurePlate tpp)
    {
    	super(new ContainerPressurePlate(tpp));
        listHeight = 0;
        scrollY = 0;
        scrollHeight = 0;
        isScrolling = false;
		this.playerTabIsOpen = false;
        playerListHeight = 0;
        playerScrollY = 0;
        playerScrollHeight = 0;
        playerIsScrolling = false;
        this.cpp = (ContainerPressurePlate)this.inventorySlots;
        this.tpp = tpp;
        enabled = new boolean[this.tpp.allowedMobs.length];
        for(int i = 0;i < this.tpp.allowedMobs.length;i++)
        {
        	if(this.tpp.allowedMobs[i] != null)
        	{
        		this.enabled[i] = this.tpp.allowedMobs[i].getEnabled();
        	}
        }
        if (tpp.allowedMobs.length > 0)
        {
            listHeight = 14 * ((tpp.allowedMobs.length + 1) / 2) - 139;
            scrollHeight = (int)((139D / (double)(listHeight + 139)) * 139D);

            if (scrollHeight <= 0 || scrollHeight >= 139)
            {
                scrollHeight = 139;
            }

        }
        lineUp();
    	if(game.theWorld.isRemote)
    	{
    		PacketSendManager.requestPPDataFromServer(this.tpp);
    	}
    }
    
    /**
     * sets the new scrolling for if a player is added and when the block is created.
     */
    public static void lineUp()
    {
        enabledPlayers = new boolean[tpp.allowedPlayers.size()];
        for(int i = 0;i < tpp.allowedPlayers.size();i++)
        {
        	if(tpp.allowedPlayers.get(i) != null)
        	{
        		enabledPlayers[i] = ((PPPlayerList)tpp.allowedPlayers.get(i)).getEnabled();
        	}
        }
        
        if (tpp.allowedPlayers.size() > 0)
        {
            playerListHeight = 14 * ((tpp.allowedPlayers.size()) + 1) - 118;
            playerScrollHeight = (int)((118D / (double)(playerListHeight + 118)) * 118D);
            if (playerScrollHeight <= 0 || playerScrollHeight >= 118)
            {
                playerScrollHeight = 118;
            }
        }
    }
    
    /**
     * called when the mouse is clicked.
     * handles everything that should happen when you click
     * with a mouse in a button or a tab.
     */
    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        i -= l;
        j -= i1;       
        if(k == 0 && i >= 257 && i <= 262 && j >= 125 &&  j <= 130)
        {
            if(this.playerTabIsOpen)
        	{
                this.mc.displayGuiScreen(new GuiModifyPressurePlate(this, game));
        	}
        }
        if(this.playerTabIsOpen)
        {
        	if(k == 0 && i >=176)
        	{
                for (int j1 = 0; j1 < tpp.allowedPlayers.size(); j1++)
                {
                    if (!mouseInPlayerRadioButton(i, j, j1))
                    {
                        continue;
                    }
                	if(!game.theWorld.isRemote)
                	{
                		cpp.switchPlayer(((PPPlayerList)tpp.allowedPlayers.get(j1)).getUsername());
                	}
                	else
                    {
                		PacketSendManager.sendSwitchPlayerPacketToServer(tpp, j1);
                    }
                }
        	}
        }

        if (k == 0 && i >= 10 && i < 165 && j >= 20 && j < 159)
        {
            for (int j1 = 0; j1 < tpp.allowedMobs.length; j1++)
            {
                if (!mouseInRadioButton(i, j, j1))
                {
                    continue;
                }
                if(!game.theWorld.isRemote)
                {
                	cpp.switchMob(tpp.allowedMobs[j1].getMobname());
                }
                else
                {
                    PacketSendManager.sendSwitchMobPacketToServer(tpp, j1);
                }
                break;
            }
        }
        if(this.enabled[0])
        {
	        if(k == 0 && i >= 176 && i <= 185 && j >= 5 &&  j <= 63)
	        {
	            if(!this.playerTabIsOpen)
	        	{
	        		this.playerTabIsOpen = true;
	                playerScrollY = 0;
	                playerIsScrolling = false;
	        	}
	        }
        }
        if(k == 0 && i >= 256 && i <= 265 && j >= 5 &&  j <= 63)
        {
            if(this.playerTabIsOpen)
        	{
        		this.playerTabIsOpen = false;
        	}
        }
    }
    
    /**
     * a method to switch the gui button.
     * @param i		index of the mob button to be switched.
     */
    public static void switchbutton(int i)
    {
    	if(enabled[i])
    	{
    		enabled[i] = false;
    	}
    	else
    	{
    		enabled[i] = true;
    	}
    }
    
    /**
     * draws most of the gui.
     */
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
    	int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        int i1 = mc.renderEngine.getTexture("/gui/APP.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i1);
        int j1 = k;
        int l1 = l;
        drawTexturedModalRect(j1, l1, 0, 0, this.xSize, this.ySize);
        if(!this.playerTabIsOpen)
        {
        	drawTexturedModalRect(j1 + 176, l1 + 5, 177, 12, 10, 44);
        	drawTexturedModalRect(j1 + 176, l1 + 20, 177, 16, 10, 47);
        	drawTexturedModalRect(j1 + 177, l1 + 55, 187, 12, 5, 5);
        }
        else
        {
        	drawTexturedModalRect(j1 + 256, l1 + 5, 177, 12, 10, 44);
        	drawTexturedModalRect(j1 + 256, l1 + 20, 177, 16, 10, 47);
        	drawTexturedModalRect(j1 + 257, l1 + 55, 187, 17, 5, 5);
        	drawTexturedModalRect(j1 + 176, l1 + 5, 5, 0, 80, 4);
        	drawTexturedModalRect(j1 + 176, l1 + 130, 96, 162, 80, 4);
        	drawTexturedModalRect(j1 + 176, l1 + 9, 5, 17, 67, 120);
        	drawTexturedModalRect(j1 + 236, l1 + 63, 156, 30, 20, 66);
        	drawTexturedModalRect(j1 + 179, l1 + 129, 99, 158, 76, 1);
        	drawTexturedModalRect(j1 + 176, l1 + 129, 5, 158, 10, 1);
        	drawTexturedModalRect(j1 + 243, l1 + 9, 163, 17, 10, 65);
        	drawTexturedModalRect(j1 + 253, l1 + 9, 169, 30, 3, 52);
        	drawTexturedModalRect(j1 + 253, l1 + 61, 172, 161, 3, 2);
        	drawTexturedModalRect(j1 + 255, l1 + 129, 0, 161, 1, 1);
        }
        if (scrollHeight != 139)
        {
            drawScrollBar();
        }
        
        if(this.playerTabIsOpen && playerScrollHeight != 118)
        {
        	drawPlayerScrollBar();
        }
    	scaling = new ScaledResolution(game.gameSettings, game.displayWidth, game.displayHeight);
        clip(k, l);
        if(!game.theWorld.isRemote)
        {
        	for (int j2 = 0; j2 < tpp.allowedMobs.length; j2++)
        	{
        		int k1 = k + 10 + ((j2 & 1) != 0 ? 80 : 0);
        		int i2 = (l + 14 * (j2 >> 1) + 20) - scrollY;
        		if(cpp.tpp.allowedMobs[j2].getEnabled())
        		{
        			drawTexturedModalRect(k1, i2, 176 + 8, 0, 8, 9);
        		}
        		else
        		{
        			drawTexturedModalRect(k1, i2, 176, 0, 8, 9);
        		}
        	}
        }
        else
        {
        	for (int j2 = 0; j2 < enabled.length; j2++)
        	{
        		int k1 = k + 10 + ((j2 & 1) != 0 ? 80 : 0);
        		int i2 = (l + 14 * (j2 >> 1) + 20) - scrollY;
        		if(this.enabled[j2])
        		{
        			drawTexturedModalRect(k1, i2, 176 + 8, 0, 8, 9);
        		}
        		else
        		{
        			drawTexturedModalRect(k1, i2, 176, 0, 8, 9);
        		}
        	}
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if(this.playerTabIsOpen)
        {
        	this.lineUp();
            playerClip(k, l);
	        if(!game.theWorld.isRemote)
	        {
	        	for (int j2 = 0; j2 < tpp.allowedPlayers.size(); j2++)
	        	{
	        		int k1 = k + 180;
	        		int i2 = (l - 8 + 14 * (j2) + 20) - playerScrollY;
	        		if(((PPPlayerList)cpp.tpp.allowedPlayers.get(j2)).getEnabled())
	        		{
	        			drawTexturedModalRect(k1, i2, 176 + 8, 0, 8, 9);
	        		}
	        		else
	        		{
	        			drawTexturedModalRect(k1, i2, 176, 0, 8, 9);
	        		}
	        	}
	        }
	        else
	        {
	        	for (int j2 = 0; j2 < tpp.allowedPlayers.size(); j2++)
	        	{
	        		int k1 = k + 180;
	        		int i2 = (l - 8 + 14 * (j2) + 20) - playerScrollY;
	        		if(this.enabledPlayers[j2])
	        		{
	        			drawTexturedModalRect(k1, i2, 176 + 8, 0, 8, 9);
	        		}
	        		else
	        		{
	        			drawTexturedModalRect(k1, i2, 176, 0, 8, 9);
	        		}
	        	}
	        }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPushMatrix();
        GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(k, l, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
        i -= k;
        j -= l;
        int k2 = Mouse.getDWheel();
        int i2 = Mouse.getX();
        int j2 = Mouse.getY();
        if (scrollHeight != 139)
        {
            if (Mouse.isButtonDown(0))
            {
                if (i >= 163 && i < 167 && j >= 19 && j < 158)
                {
                    isScrolling = true;
                }
            }
            else
            {
                isScrolling = false;
            }

            if (isScrolling)
            {
                scrollY = ((j - 20) * listHeight) / (139 - (scrollHeight >> 1));

                if (scrollY < 0)
                {
                    scrollY = 0;
                }

                if (scrollY > listHeight)
                {
                    scrollY = listHeight;
                }
            }



            if(!this.playerTabIsOpen)
            {
	            if (k2 < 0)
	            {
	                scrollY += 14;
	
	                if (scrollY > listHeight)
	                {
	                    scrollY = listHeight;
	                }
	            }
	            else if (k2 > 0)
	            {
	                scrollY -= 14;
	
	                if (scrollY < 0)
	                {
	                    scrollY = 0;
	                }
	            }
            }
            else
            {
            	if(i >= 257 && i <= 262 && j >= 125 && j <= 130)
            	{
            		this.func_74190_a("Add/Remove Players", k + 167, l + 149);
            	}
            	if(i < 176 && i >= 0)
            	{
    	            if (k2 < 0)
    	            {
    	                scrollY += 14;
    	
    	                if (scrollY > listHeight)
    	                {
    	                    scrollY = listHeight;
    	                }
    	            }
    	            else if (k2 > 0)
    	            {
    	                scrollY -= 14;
    	
    	                if (scrollY < 0)
    	                {
    	                	scrollY = 0;
    	                }
    	            }
            	}
            }
        }
        if(this.playerTabIsOpen)
        {
        	if (playerScrollHeight != 118)
            {

                if (Mouse.isButtonDown(0))
                {
                    if (i >= 243 && i < 247 && j >= 11 && j < 128)
                    {
                        playerIsScrolling = true;
                    }
                }
                else
                {
                    playerIsScrolling = false;
                }

                if (playerIsScrolling)
                {
                	playerScrollY = ((j - 20) * playerListHeight) / (118 - (playerScrollHeight >> 1));

                    if (playerScrollY < 0)
                    {
                    	playerScrollY = 0;
                    }

                    if (playerScrollY > playerListHeight)
                    {
                    	playerScrollY = playerListHeight;
                    }
                }
	            if(i >= 176)
	            {
	            	if (k2 < 0)
	            	{
	            		playerScrollY += 14;
	            		
	            		if (playerScrollY > playerListHeight)
	            		{
	            			playerScrollY = playerListHeight;
	            		}
	            	}
	            	else if (k2 > 0)
	            	{
	            		playerScrollY -= 14;
	    	
	            		if (playerScrollY < 0)
	            		{
	            			playerScrollY = 0;
	            		}
	            	}
	            }  
            }
        }
    }
    
    /**
     * this is for cutting out the area of the mobs.
     * most of the credit for this goes to Risugami.
     * @param i
     * @param j
     */
    private void clip(int i, int j)
    {
        int k = (i + 10) * scaling.getScaleFactor();
        int l = (((j + 20) + 24) + 2) * scaling.getScaleFactor();
        int i1 = 153 * scaling.getScaleFactor();
        int j1 = 139 * scaling.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(k, l, i1, j1);
    }
    
    /**
     * this is for cutting out the player area.
     * I kind of learned myself how this works by examining 
     * Risugamis code.
     * @param i
     * @param j
     */
    private void playerClip(int i, int j)
    {
        int k = (i + 179) * scaling.getScaleFactor();
        int l = (j + 75) * scaling.getScaleFactor();
        int i1 = 64 * scaling.getScaleFactor();
        int j1 = 118 * scaling.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(k, l, i1, j1);
    }
    
    /**
     * checks if the mouse is in one of the mob buttons.
     * @param i
     * @param j
     * @param k
     * @return
     */
    public boolean mouseInRadioButton(int i, int j, int k)
    {
        int l = 10 + ((k & 1) != 0 ? 80 : 0);
        int i1 = (14 * (k >> 1) + 20) - scrollY;
        return i >= l - 1 && i < l + 9 && j >= i1 - 1 && j < i1 + 10;
    }
    
    /**
     * checks if the mouse is in one of the player buttons.
     * @param i
     * @param j
     * @param k
     * @return
     */
    public boolean mouseInPlayerRadioButton(int i, int j, int k)
    {
        int l = 180;
        int i1 = (-8 + 14 * (k) + 20) - playerScrollY;
        return i >= l - 1 && i < l + 9 && j >= i1 - 1 && j < i1 + 10;
    }
    
    /**
     * draws the texts of the gui.
     */
    protected void drawGuiContainerForegroundLayer()
    {
    	RenderHelper.disableStandardItemLighting();
    	FontRenderer var1 = game.fontRenderer;
		var1.drawString("Advanced " + StatCollector.translateToLocal("tile.pressurePlate.name"), 8, -13, 0x404040);
    	    if(!this.playerTabIsOpen)
    	    {
	    		var1.drawString("P", 177, -11, 0x404040);
	    		var1.drawString("l", 178, -3, 0x404040);
	    		var1.drawString("a", 177, 3, 0x404040);
	    		var1.drawString("y", 177, 9, 0x404040);
	    		var1.drawString("e", 177, 16, 0x404040);
	    		var1.drawString("r", 177, 22, 0x404040);
	    		var1.drawString("s", 177, 28, 0x404040);
    	    }
    	    else
    	    {
	    		var1.drawString("P", 257, -11, 0x404040);
	    		var1.drawString("l", 258, -3, 0x404040);
	    		var1.drawString("a", 257, 3, 0x404040);
	    		var1.drawString("y", 257, 9, 0x404040);
	    		var1.drawString("e", 257, 16, 0x404040);
	    		var1.drawString("r", 257, 22, 0x404040);
	    		var1.drawString("s", 257, 28, 0x404040);
	    		var1.drawString("+", 257, 105, 0xffffff);
    	    }
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        clip(i, j);

        for (int k = 0; k < tpp.allowedMobs.length; k++)
        {
            int l = ((k & 1) != 0 ? 80 : 0) + 19;
            int i1 = (14 * (k >> 1) + 20) - scrollY;
            String s = (new StringBuilder("entity.")).append(tpp.allowedMobs[k].getMobname()).append(".name").toString();
            String s1 = StatCollector.translateToLocal(s);

            if (s1 == s)
            {
                s1 = tpp.allowedMobs[k].getMobname();
            }
            
            if(s1 == "humanoid")
            {
            	s1 = "Players";
            }
            if(s1 == "Item")
            {
            	s1 = "Items";
            }

            var1.drawString(s1, l, i1 - 18, 0xffffff);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        
        if(this.playerTabIsOpen)
        {
        	this.lineUp();
        	playerClip(i, j);
            for (int k = 0; k < tpp.allowedPlayers.size(); k++)
            {
                int l = 170 + 19;
                int i1 = (14 * (k) + 12) - playerScrollY;
                String s1 = ((PPPlayerList)tpp.allowedPlayers.get(k)).getUsername();
                var1.drawString(s1, l, i1 - 18, 0xffffff);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }
    
    /**
     * draws the mob scrollbar if it's needed.
     */
    private void drawScrollBar()
    {
        int i = ((width - xSize) / 2) + 163;
        int j = ((height - ySize) / 2) + 19 + (scrollY * (139 - scrollHeight)) / listHeight;
        int k = j;
        drawTexturedModalRect(i, k, 176, 9, 5, 1);

        for (k++; k < (j + scrollHeight) - 1; k++)
        {
            drawTexturedModalRect(i, k, 176, 10, 5, 1);
        }

        drawTexturedModalRect(i, k, 176, 11, 5, 1);
    }
    
    /**
     * draws the players scrollbar if it's needed.
     */
    private void drawPlayerScrollBar()
    {
    	int j = 0;
        int i = ((width - xSize) / 2) + 243;
        if(playerListHeight != 0)
        {
        	j = ((height - ySize) / 2) + 11+ (playerScrollY * (118 - playerScrollHeight)) / playerListHeight;
        }
        int k = j;
        drawTexturedModalRect(i, k, 176, 9, 5, 1);
        if(enabledPlayers.length != 0)
        {
	        for (k++; k < (j + playerScrollHeight) - 1; k++)
	        {
	            drawTexturedModalRect(i, k, 176, 10, 5, 1);
	        }
	        
	        drawTexturedModalRect(i, k, 176, 11, 5, 1);
        }
    }
    
    /**
     * checks if the gui does pause the game.
     * this gui does not because it couldn't receive any packets while paused,
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    /**
     * check if there is a key typed that can close the screen and closes it if needed.
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.keyCode)
        {
            this.mc.thePlayer.closeScreen();
        }
    }
}
