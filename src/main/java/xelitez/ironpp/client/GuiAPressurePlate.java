/**
 * main gui class for the advanced iron pressure plate
 *
 * @author Kalvin
 */
package xelitez.ironpp.client;

import java.util.Arrays;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import xelitez.ironpp.ContainerPressurePlate;
import xelitez.ironpp.IronPP;
import xelitez.ironpp.PPPlayerList;
import xelitez.ironpp.PPSettings;
import xelitez.ironpp.PacketSendManager;
import xelitez.ironpp.TileEntityPressurePlate;
import xelitez.ironpp.PPSettings.SettingsLine;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiAPressurePlate extends GuiContainer
{
	
    private static final ResourceLocation texture = new ResourceLocation("ironpp:gui/APP.png");
    /**
     * two things I need to use in this mod.
     */
    public static ContainerPressurePlate cpp;
    public static TileEntityPressurePlate tpp;

    /**
     * a separate list of the enabled players and mobs
     */
    public static boolean[] enabled;
    public static boolean[] enabledPlayers;
    public static boolean[] enabledSettings;

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
    private static int settingsListHeight;
    private int settingsScrollY;
    private static int settingsScrollHeight;
    private boolean settingsIsScrolling;
    public static boolean hasData;
    
    private int mouseDownTime = 0;

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

    public int tab2Open = 0;
    
    private boolean justOpened = false;

    /**
     * this method gets called every game update.
     * I use it to close the tab if Player is not enabled
     * and to change the scrolling if it is in an invalid state.
     */
    public void updateScreen()
    {
        if (game.theWorld.isRemote)
        {
            if (!GuiAPressurePlate.enabled[0] && this.tabOpen == 1)
            {
                this.tabOpen = 0;
            }
        }
        else
        {
            if (!GuiAPressurePlate.tpp.findMobName("humanoid") && this.tabOpen == 1)
            {
                this.tabOpen = 0;
            }
        }

        if (playerScrollY > playerListHeight)
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
        super(new ContainerPressurePlate(tpp, game.thePlayer.inventory));
        xSize = 385;
        ySize = 194;
        listHeight = 0;
        scrollY = 0;
        scrollHeight = 0;
        isScrolling = false;
        this.tabOpen = 0 ;
        playerListHeight = 0;
        playerScrollY = 0;
        playerScrollHeight = 0;
        playerIsScrolling = false;
        GuiAPressurePlate.cpp = (ContainerPressurePlate)this.inventorySlots;
        GuiAPressurePlate.tpp = tpp;
        enabled = new boolean[GuiAPressurePlate.tpp.allowedMobs.length];

        for (int i = 0; i < GuiAPressurePlate.tpp.allowedMobs.length; i++)
        {
            if (GuiAPressurePlate.tpp.allowedMobs[i] != null)
            {
                GuiAPressurePlate.enabled[i] = GuiAPressurePlate.tpp.allowedMobs[i].getEnabled();
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

        if (game.theWorld.isRemote)
        {
        	hasData = false;
            PacketSendManager.requestPPDataFromServer(GuiAPressurePlate.tpp);
        }
    }
    
    public void initGui()
    {
    	super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;	
    }

    /**
     * sets the new scrolling for if a player is added and when the block is created.
     */
    public static void lineUp()
    {
        enabledPlayers = new boolean[tpp.allowedPlayers.size()];

        for (int i = 0; i < tpp.allowedPlayers.size(); i++)
        {
            if (tpp.allowedPlayers.get(i) != null)
            {
                enabledPlayers[i] = ((PPPlayerList)tpp.allowedPlayers.get(i)).getEnabled();
            }
        }

        if (tpp.allowedPlayers.size() > 0)
        {
            playerListHeight = 14 * ((tpp.allowedPlayers.size())) - 118;
            playerScrollHeight = (int)((118D / (double)(playerListHeight + 118)) * 118D);

            if (playerScrollHeight <= 0 || playerScrollHeight >= 118)
            {
                playerScrollHeight = 118;
            }
        }
    }

    public static void LineUpSettings()
    {
        enabledSettings = new boolean[PPSettings.buttons.size()];

        for (int i = 0; i < PPSettings.buttons.size(); i++)
        {
            if (PPSettings.buttons.get(i) != null)
            {
                enabledSettings[i] = tpp.getIsEnabled(i);
            }
        }

        if (PPSettings.lines.size() > 0)
        {
            settingsListHeight = 14 * ((PPSettings.lines.size())) - 137;
            settingsScrollHeight = (int)((137D / (double)(settingsListHeight + 137)) * 137D);

            if (settingsScrollHeight <= 0 || settingsScrollHeight >= 137)
            {
                settingsScrollHeight = 137;
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

        if (k == 0 && i >= 376 && i <= 381 && j >= 125 &&  j <= 130)
        {
            if (this.tabOpen == 1)
            {
                this.mc.displayGuiScreen(new GuiModifyPressurePlate(this, game));
            }
        }

        if (this.tabOpen == 1)
        {
            if (k == 0 && i >= 295)
            {
                for (int j1 = 0; j1 < tpp.allowedPlayers.size(); j1++)
                {
                    if (!mouseInPlayerRadioButton(i, j, j1))
                    {
                        continue;
                    }

                    if (!game.theWorld.isRemote)
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

        if (k == 0 && i >= 129 && i < 284 && j >= 20 && j < 159)
        {
            for (int j1 = 0; j1 < tpp.allowedMobs.length; j1++)
            {
                if (!mouseInRadioButton(i, j, j1))
                {
                    continue;
                }

                if (!game.theWorld.isRemote)
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

        if (this.tab2Open == 2)
        {
            if (k == 0 && i >= 24 && i < 110 && j >= 16 && j < 154)
            {
                for (int j1 = 0; j1 < PPSettings.buttons.size(); j1++)
                {
                    if (mouseInSettingsRadioButton(i, j, j1))
                    {
                        if (!game.theWorld.isRemote)
                        {
                            cpp.tpp.switchSetting(j1);
                        }
                        else
                        {
                            PacketSendManager.sendSwitchSettingToServer(tpp, j1);
                        }
                    }
                }

                for (int j1 = 0; j1 < PPSettings.settingsLines.size(); j1++)
                {
                    if (mouseInSettingsLine(i, j, j1))
                    {
                        switch (j1)
                        {
                            case 0:
                                mc.displayGuiScreen(new GuiPassword(GuiAPressurePlate.tpp, true, tpp.xCoord, tpp.yCoord, tpp.zCoord));
                        }
                    }
                }
            }
        }

        if (GuiAPressurePlate.enabled[0])
        {
            if (k == 0 && i >= 295 && i <= 304 && j >= 5 &&  j <= 63)
            {
                if (this.tabOpen == 0)
                {
                    this.tabOpen = 1;
                    playerScrollY = 0;
                    playerIsScrolling = false;
                }
            }
        }

        if (k == 0 && i >= 375 && i <= 384 && j >= 5 &&  j <= 63)
        {
            if (this.tabOpen == 1)
            {
                this.tabOpen = 0;
            }
        }

        if (k == 0 && i >= 109 && i <= 118 && j >= 5 && j <= 64)
        {
            if (this.tab2Open == 0)
            {
                tab2Open = 1;
                cpp.drawSlots(game.thePlayer.inventory, tpp);
            }
        }

        if (k == 0 && i >= 24 && i <= 32 && j >= 5 && j <= 64)
        {
            if (this.tab2Open == 1)
            {
                tab2Open = 0;
                cpp.removeAllSlots();
            }
        }

        if (k == 0 && i >= 100 && i <= 118 && j >= 133 && j <= 154)
        {
            if (this.tab2Open == 0)
            {
                tab2Open = 2;
                settingsScrollY = 0;
                settingsIsScrolling = false;
            }
        }

        if (k == 0 && i >= 0 && i <= 18 && j >= 133 && j <= 154)
        {
            if (this.tab2Open == 2)
            {
                tab2Open = 0;
            }
        }
        if (k == 0 && i >= 109 && i <= 118 && j >= 65 && j <= 100)
        {
            if (this.tab2Open == 0)
            {
                tab2Open = 3;
                this.justOpened = true;
            }
        }

        if (k == 0 && i >= 26 && i <= 35 && j >= 65 && j <= 100)
        {
            if (this.tab2Open == 3)
            {
                tab2Open = 0;
            }
        }
    }

    /**
     * a method to switch the gui button.
     * @param i		index of the mob button to be switched.
     */
    public static void switchbutton(int i)
    {
        if (enabled[i])
        {
            enabled[i] = false;
        }
        else
        {
            enabled[i] = true;
        }
    }

    public static void switchSettingsButton(int i)
    {
        if (enabledSettings[i])
        {
            enabledSettings[i] = false;
        }
        else
        {
            enabledSettings[i] = true;
        }
    }

    /**
     * draws most of the gui.
     */
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        if (this.tab2Open != 1 && cpp.inventorySlots.size() > 0)
        {
            cpp.removeAllSlots();
        }

        if ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && this.tab2Open == 0)
        {
            this.tab2Open = 1;
            cpp.drawSlots(game.thePlayer.inventory, tpp);
        }

        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int j1 = k;
        int l1 = l;

        if (tab2Open == 0 || tab2Open == 1)
        {
            drawTextureTab(j1, l1);
        }

        if (tab2Open == 0 || tab2Open == 2)
        {
            drawSettingsTab(i, j);
        }
        if (tab2Open == 0 || tab2Open == 3)
        {
            drawOutputTab(i - k, j - l);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(j1 + 119, l1, 0, 0, 176, 166);

        if (this.scrollHeight == 139)
        {
            drawTexturedModalRect(j1 + 282, l1 + 19, 20, 19, 5, 139);
        }

        if (this.tabOpen == 0)
        {
            drawTexturedModalRect(j1 + 295, l1 + 5, 177, 12, 10, 44);
            drawTexturedModalRect(j1 + 295, l1 + 20, 177, 16, 10, 44);
            drawTexturedModalRect(j1 + 296, l1 + 55, 187, 12, 5, 5);
        }
        else if (this.tabOpen == 1)
        {
            drawTexturedModalRect(j1 + 375, l1 + 5, 177, 12, 10, 44);
            drawTexturedModalRect(j1 + 375, l1 + 20, 177, 16, 10, 44);
            drawTexturedModalRect(j1 + 376, l1 + 55, 187, 17, 5, 5);
            drawTexturedModalRect(j1 + 295, l1 + 5, 5, 0, 80, 4);
            drawTexturedModalRect(j1 + 295, l1 + 130, 96, 162, 80, 4);
            drawTexturedModalRect(j1 + 295, l1 + 9, 5, 17, 67, 120);
            drawTexturedModalRect(j1 + 355, l1 + 63, 156, 30, 20, 66);
            drawTexturedModalRect(j1 + 298, l1 + 129, 99, 158, 76, 1);
            drawTexturedModalRect(j1 + 295, l1 + 129, 5, 158, 10, 1);
            drawTexturedModalRect(j1 + 362, l1 + 9, 163, 17, 10, 65);
            drawTexturedModalRect(j1 + 372, l1 + 9, 169, 30, 3, 52);
            drawTexturedModalRect(j1 + 372, l1 + 61, 172, 161, 3, 2);
            drawTexturedModalRect(j1 + 374, l1 + 129, 0, 161, 1, 1);

            if (GuiAPressurePlate.playerScrollHeight == 118)
            {
                drawTexturedModalRect(j1 + 362, l1 + 9, 120, 17, 5, 120);
            }
        }

        if (scrollHeight != 139)
        {
            drawScrollBar();
        }

        if (this.tabOpen == 1 && playerScrollHeight != 118)
        {
            drawPlayerScrollBar();
        }

        if (this.tab2Open == 2 && settingsScrollHeight != 137)
        {
            drawSettingsScrollBar();
        }

        scaling = new ScaledResolution(game.gameSettings, game.displayWidth, game.displayHeight);
        clip(k, l);

        if (!game.theWorld.isRemote)
        {
            for (int j2 = 0; j2 < tpp.allowedMobs.length; j2++)
            {
                int k1 = k + 129 + ((j2 & 1) != 0 ? 80 : 0);
                int i2 = (l + 14 * (j2 >> 1) + 20) - scrollY;

                if (cpp.tpp.allowedMobs[j2].getEnabled())
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
                int k1 = k + 129 + ((j2 & 1) != 0 ? 80 : 0);
                int i2 = (l + 14 * (j2 >> 1) + 20) - scrollY;

                if (GuiAPressurePlate.enabled[j2])
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

        if (this.tabOpen == 1)
        {
            GuiAPressurePlate.lineUp();
            playerClip(k, l);

            if (!game.theWorld.isRemote)
            {
                for (int j2 = 0; j2 < tpp.allowedPlayers.size(); j2++)
                {
                    int k1 = k + 299;
                    int i2 = (l - 8 + 14 * (j2) + 20) - playerScrollY;

                    if (((PPPlayerList)cpp.tpp.allowedPlayers.get(j2)).getEnabled())
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
                    int k1 = k + 299;
                    int i2 = (l - 8 + 14 * (j2) + 20) - playerScrollY;

                    if (GuiAPressurePlate.enabledPlayers[j2])
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

        if (scrollHeight != 139)
        {
            if (Mouse.isButtonDown(0))
            {
                if (i >= 282 && i < 287 && j >= 19 && j < 158)
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

            if (this.tabOpen == 0 && tab2Open == 0)
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
                if (i >= 376 && i <= 381 && j >= 125 && j <= 130)
                {
                    this.func_146283_a( Arrays.asList("Add/Remove Players"), k + 287, l + 150);
                }

                if (i < 295 && i >= 119)
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

        if (this.tabOpen == 1 && (tab2Open == 0 || tab2Open == 1))
        {
            if (playerScrollHeight != 118)
            {
                if (Mouse.isButtonDown(0))
                {
                    if (i >= 362 && i < 367 && j >= 11 && j < 128)
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

                if (i >= 295)
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

        if (tab2Open == 2 && tabOpen == 0)
        {
            if (settingsScrollHeight != 137)
            {
                if (Mouse.isButtonDown(0))
                {
                    if (i >= 362 && i < 366 && j >= 11 && j < 128)
                    {
                        settingsIsScrolling = true;
                    }
                }
                else
                {
                    settingsIsScrolling = false;
                }

                if (settingsIsScrolling)
                {
                    settingsScrollY = ((j - 20) * settingsListHeight) / (137 - (settingsScrollHeight >> 1));

                    if (settingsScrollY < 0)
                    {
                        settingsScrollY = 0;
                    }

                    if (settingsScrollY > settingsListHeight)
                    {
                        settingsScrollY = settingsListHeight;
                    }
                }

                if (i < 119)
                {
                    if (k2 < 0)
                    {
                        settingsScrollY += 14;

                        if (settingsScrollY > settingsListHeight)
                        {
                            settingsScrollY = settingsListHeight;
                        }
                    }
                    else if (k2 > 0)
                    {
                        settingsScrollY -= 14;

                        if (settingsScrollY < 0)
                        {
                            settingsScrollY = 0;
                        }
                    }
                }
            }
        }

        if (tab2Open == 2)
        {
            if (settingsScrollHeight != 137)
            {
                if (Mouse.isButtonDown(0))
                {
                    if (i >= 112 && i < 117 && j >= 16 && j < 153)
                    {
                        settingsIsScrolling = true;
                    }
                }
                else
                {
                    settingsIsScrolling = false;
                }

                if (settingsIsScrolling)
                {
                    settingsScrollY = ((j - 20) * settingsListHeight) / (137 - (settingsScrollHeight >> 1));

                    if (settingsScrollY < 0)
                    {
                        settingsScrollY = 0;
                    }

                    if (settingsScrollY > settingsListHeight)
                    {
                        settingsScrollY = settingsListHeight;
                    }
                }
            }
        }

        if (tab2Open == 2 && tabOpen == 1)
        {
            if (i < 119)
            {
                if (k2 < 0)
                {
                    settingsScrollY += 14;

                    if (settingsScrollY > settingsListHeight)
                    {
                        settingsScrollY = settingsListHeight;
                    }
                }
                else if (k2 > 0)
                {
                    settingsScrollY -= 14;

                    if (settingsScrollY < 0)
                    {
                        settingsScrollY = 0;
                    }
                }
            }
        }

        if (playerScrollHeight != 118)
        {
            if (Mouse.isButtonDown(0))
            {
                if (i >= 362 && i < 366 && j >= 11 && j < 128)
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

            if (i >= 295)
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

    private void drawTextureTab(int i, int j)
    {
        if (this.tab2Open == 0)
        {
            drawTexturedModalRect(i + 113, j + 5, 177, 12, 6, 44);
            drawTexturedModalRect(i + 109, j + 5, 0, 0, 4, 44);
            drawTexturedModalRect(i + 109, j + 49, 0, 150, 4, 16);
            drawTexturedModalRect(i + 113, j + 49, 177, 44, 6, 16);
            drawTexturedModalRect(i + 113, j + 56, 187, 17, 5, 5);
        }
        else
        {
            for (int var1 = 0; var1 < 9; var1++)
            {
                for (int var2 = 0; var2 < 3; var2++)
                {
                    drawTexturedModalRect(i + 37 + var2 * 18, j + 6 + var1 * 18, 199, 0 + ((var1 * var2) % 5) * 18, 18, 18);
                }

                drawTexturedModalRect(i + 96, j + 6 + var1 * 18, 199, 0 + ((var1) % 5) * 18, 18, 18);
            }

            drawTexturedModalRect(i + 91, j + 6, 194, 0, 5, 162);
            drawTexturedModalRect(i + 114, j + 6, 194, 0, 4, 162);
            drawTexturedModalRect(i + 118, j + 6, 194, 0, 1, 162);
            drawTexturedModalRect(i + 33, j + 2, 0, 0, 86, 4);
            drawTexturedModalRect(i + 33, j + 6, 0, 4, 4, 150);
            drawTexturedModalRect(i + 33, j + 156, 0, 149, 4, 17);
            drawTexturedModalRect(i + 37, j + 168, 10, 161, 23, 5);
            drawTexturedModalRect(i + 64, j + 172, 199, 0, 18, 18);
            drawTexturedModalRect(i + 59, j + 172, 0, 144, 5, 22);
            drawTexturedModalRect(i + 64, j + 190, 5, 162, 18, 4);
            drawTexturedModalRect(i + 82, j + 172, 171, 144, 5, 22);
            drawTexturedModalRect(i + 60, j + 168, 20, 5, 26, 4);
            drawTexturedModalRect(i + 60, j + 171, 1, 60, 1, 1);
            drawTexturedModalRect(i + 86, j + 168, 140, 161, 36, 5);
            drawTexturedModalRect(i + 85, j + 171, 174, 60, 1, 1);
            drawTexturedModalRect(i + 119, j + 163, 173, 140, 3, 5);
            drawTexturedModalRect(i + 34, j + 8, 177, 15, 3, 40);
            drawTexturedModalRect(i + 34, j + 48, 177, 15, 3, 16);
            drawTexturedModalRect(i + 35, j + 7, 20, 15, 1, 1);
            drawTexturedModalRect(i + 34, j + 63, 1, 15, 1, 1);
            drawTexturedModalRect(i + 28, j + 5, 177, 12, 6, 44);
            drawTexturedModalRect(i + 24, j + 5, 0, 0, 4, 44);
            drawTexturedModalRect(i + 24, j + 49, 0, 150, 4, 16);
            drawTexturedModalRect(i + 28, j + 49, 176, 44, 6, 16);
            drawTexturedModalRect(i + 28, j + 56, 187, 12, 5, 5);

            if (tpp.getStackInSlot(0) == null)
            {
                drawTexturedModalRect(i + 66, j + 173, 177, 78, 16, 16);
            }
        }
    }

    private void drawTextureText(FontRenderer var1)
    {
        if (this.tab2Open == 0)
        {
            var1.drawString("T", 113, 9, 0x404040);
            var1.drawString("e", 113, 15, 0x404040);
            var1.drawString("x", 113, 21, 0x404040);
            var1.drawString("t", 114, 28, 0x404040);
            var1.drawString("u", 113, 34, 0x404040);
            var1.drawString("r", 113, 41, 0x404040);
            var1.drawString("e", 113, 47, 0x404040);
        }
        else
        {
            var1.drawString("T", 28, 9, 0x404040);
            var1.drawString("e", 28, 15, 0x404040);
            var1.drawString("x", 28, 21, 0x404040);
            var1.drawString("t", 29, 28, 0x404040);
            var1.drawString("u", 28, 34, 0x404040);
            var1.drawString("r", 28, 41, 0x404040);
            var1.drawString("e", 28, 47, 0x404040);
        }
    }

    private void drawSettingsTab(int i, int j)
    {
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;

        if (this.tab2Open == 0)
        {
            drawTexturedModalRect(k + 100, l + 133, 0, 0, 19, 17);
            drawTexturedModalRect(k + 100, l + 150, 0, 161, 19, 7);
            drawTexturedModalRect(k + 104, l + 137, 177, 95, 14, 14);
        }
        else
        {
            drawTexturedModalRect(k, l + 133, 0, 0, 20, 17);
            drawTexturedModalRect(k, l + 150, 0, 161, 20, 7);
            drawTexturedModalRect(k + 4, l + 137, 177, 95, 14, 14);
            drawTexturedModalRect(k + 18, l + 2, 0, 0, 101, 4);
            drawTexturedModalRect(k + 18, l + 6, 0, 4, 4, 128);
            drawTexturedModalRect(k + 18, l + 154, 0, 161, 4, 6);
            drawTexturedModalRect(k + 22, l + 155, 4, 162, 97, 4);
            drawTexturedModalRect(k + 22, l + 6, 6, 9, 97, 13);
            drawTexturedModalRect(k + 22, l + 19, 6, 19, 97, 130);
            drawTexturedModalRect(k + 22, l + 149, 6, 154, 97, 6);
            drawTexturedModalRect(k + 19, l + 135, 6, 19, 1, 18);
            drawTexturedModalRect(k + 20, l + 134, 6, 6, 3, 12);
            drawTexturedModalRect(k + 20, l + 146, 6, 6, 3, 8);
            drawTexturedModalRect(k + 20, l + 134, 4, 2, 1, 1);
            drawTexturedModalRect(k + 19, l + 135, 4, 2, 1, 1);
            drawTexturedModalRect(k + 19, l + 153, 4, 2, 1, 1);
            drawTexturedModalRect(k + 109, l + 15, 160, 18, 10, 130);
            drawTexturedModalRect(k + 109, l + 145, 160, 150, 10, 9);
            GuiAPressurePlate.lineUp();
            this.settingsClip(k, l);

            if (!game.theWorld.isRemote)
            {
                for (int j2 = 0; j2 < PPSettings.buttons.size(); j2++)
                {
                    int var1 = PPSettings.buttons.get(j2).line;
                    int k1 = k + 99;
                    int i2 = (l - 8 + 14 * (var1) + 20) - settingsScrollY;

                    if (cpp.tpp.getIsEnabled(j2))
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
                for (int j2 = 0; j2 < PPSettings.buttons.size(); j2++)
                {
                    PPSettings.SettingsButton var1 = (PPSettings.SettingsButton)cpp.tpp.settings.get(j2);
                    int var2 = var1.line;
                    int k1 = k + 26;
                    int i2 = (l - 8 + 14 * (var2) + 10) - settingsScrollY;

                    if (GuiAPressurePlate.tpp.getIsEnabled(j2))
                    {
                        drawTexturedModalRect(k1, i2, 176 + 8, 0, 8, 9);
                    }
                    else
                    {
                        drawTexturedModalRect(k1, i2, 176, 0, 8, 9);
                    }
                }
            }

            i -= k;
            j -= l;
            FontRenderer var3 = game.fontRenderer;
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            var3.drawString("Settings", k + 24, l + 6, 0x404040);
            this.settingsClip(k, l);

            for (int j2 = 0; j2 < PPSettings.settingsLines.size(); j2++)
            {
                SettingsLine var1 = (SettingsLine)PPSettings.settingsLines.get(j2);
                int var2 = var1.line;
                int l1 = k + 25;
                int i1 = (l + 14 * (var2) + 15) - settingsScrollY;
                int var4 = 14737632;

                if (this.mouseInSettingsLine(i, j, j2))
                {
                    var4 = 16777120;
                }

                var3.drawString(var1.Text, l1, i1 - 12, var4);
            }

            int var2 = 0;

            for (var2 = 0; var2 < PPSettings.lines.size(); var2++)
            {
                int l1 = k + 25;
                int i1 = (l + 14 * (var2) + 17) - settingsScrollY;
                var3.drawString((String)PPSettings.lines.get(var2), l1, i1, 0xffffff);
            }

            for (var2 = 0; var2 < PPSettings.buttons.size(); var2++)
            {
                PPSettings.SettingsButton var4 = (PPSettings.SettingsButton)cpp.tpp.settings.get(var2);
                int var5 = var4.line;
                int l1 = k + 36;
                int i1 = (l + 14 * (var5) + 3) - settingsScrollY;

                if (tpp.getIsEnabled(var2))
                {
                    var3.drawString(var4.TextEnabled, l1, i1, 0x404040);
                }
                else
                {
                    var3.drawString(var4.TextDisabled, l1, i1, 0x404040);
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            this.mc.getTextureManager().bindTexture(texture);
        }
    }
    
    private void drawOutputTab(int i, int j)
    {
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        
        if(this.tab2Open == 0)
        {
            drawTexturedModalRect(k + 113, l + 65, 177, 12, 6, 20);
            drawTexturedModalRect(k + 109, l + 65, 0, 0, 4, 20);
            drawTexturedModalRect(k + 109, l + 85, 0, 150, 4, 16);
            drawTexturedModalRect(k + 113, l + 85, 177, 44, 6, 16);
            drawTexturedModalRect(k + 113, l + 92, 187, 17, 5, 5);
        }
        else
        {
        	drawTexturedModalRect(k + 30, l + 65, 177, 12, 6, 20);
            drawTexturedModalRect(k + 26, l + 65, 0, 0, 4, 20);
            drawTexturedModalRect(k + 26, l + 85, 0, 150, 4, 16);
            drawTexturedModalRect(k + 30, l + 85, 177, 44, 6, 16);
            drawTexturedModalRect(k + 30, l + 92, 187, 12, 5, 5);
            
            drawTexturedModalRect(k + 35, l + 61, 0, 0, 84, 5);
            drawTexturedModalRect(k + 36, l + 66, 2, 3, 84, 2);
            drawTexturedModalRect(k + 36, l + 68, 4, 3, 84, 15);
            drawTexturedModalRect(k + 36, l + 83, 4, 3, 84, 15);
            drawTexturedModalRect(k + 35, l + 101, 0, 162, 85, 15);
            drawTexturedModalRect(k + 36, l + 98, 1, 160, 84, 3);
            drawTexturedModalRect(k + 37, l + 98, 4, 160, 1, 1);
            
            drawRect(k + 84, l + 65, k + 117, l + 81, -6250336);
            drawRect(k + 85, l + 66, k + 116, l + 80, -16777216);
            drawRect(k + 84, l + 85, k + 117, l + 101, -6250336);
            drawRect(k + 85, l + 86, k + 116, l + 100, -16777216);
            
            GL11.glColor4f(255.0f, 255.0f, 255.0f, 255.0f);
            
            drawTexturedModalRect(k + 110, l + 65, 176, 60, 7, 8);
            drawTexturedModalRect(k + 110, l + 73, 183, 60, 7, 8);
            drawTexturedModalRect(k + 110, l + 85, 176, 60, 7, 8);
            drawTexturedModalRect(k + 110, l + 93, 183, 60, 7, 8);
            
        	if(Mouse.isButtonDown(0) && !this.justOpened)
        	{
	            if(i >= 110 && i <= 116 && j >= 65 && j <= 72)
	            {
	            	if(this.mouseDownTime == 0 || (this.mouseDownTime >= 60 && this.mouseDownTime % IronPP.changeSpeedSpeed == 0))
	            	{
	            		if(tpp.maxOutput < 15)
	            		{
	            			tpp.maxOutput++;
	            		}
	            	}
	            	this.mouseDownTime++;
	            }
	            else if(i >= 110 && i <= 116 && j >= 73 && j <= 80)
	            {            	
	            	if(this.mouseDownTime == 0 || (this.mouseDownTime >= 60 && this.mouseDownTime % IronPP.changeSpeedSpeed == 0))
	            	{
	            		if(tpp.maxOutput > 0)
	            		{
	            			tpp.maxOutput--;
	            		}
	            	}
	            	this.mouseDownTime++;
	            }
	            else if(i >= 110 && i <= 116 && j >= 85 && j <= 92)
	            {           
	            	if(this.mouseDownTime == 0 || (this.mouseDownTime >= 60 && this.mouseDownTime % IronPP.changeSpeedSpeed == 0))
	            	{
	            		if(tpp.itemsForMax < 9999)
	            		{
	            			tpp.itemsForMax++;
	            		}
	            	}
	            	this.mouseDownTime++;
	            }
	            else if(i >= 110 && i <= 116 && j >= 93 && j <= 100)
	            {            	
	            	if(this.mouseDownTime == 0 || (this.mouseDownTime >= 60 && this.mouseDownTime % IronPP.changeSpeedSpeed == 0))
	            	{
	            		if(tpp.itemsForMax > 0)
	            		{
	            			tpp.itemsForMax--;
	            		}
	            	}
	            	this.mouseDownTime++;
	            }
        	}
            else
            {
            	if(!Mouse.isButtonDown(0) && this.justOpened)
            	{
            		this.justOpened = false;
            	}
            	if(this.mouseDownTime != 0)
            	{
            		this.mouseDownTime = 0;
            		PacketSendManager.sendChangedDataPacketToServer(tpp);
            	}
            }
            
        }
    }
    
    private void drawOutputText(FontRenderer var1)
    {
        if (this.tab2Open == 0)
        {
            var1.drawString("O", 113, 69, 0x404040);
            var1.drawString("u", 113, 75, 0x404040);
            var1.drawString("t", 114, 83, 0x404040);
        }
        else
        {
            var1.drawString("O", 30, 69, 0x404040);
            var1.drawString("u", 30, 75, 0x404040);
            var1.drawString("t", 31, 83, 0x404040);
            
            var1.drawString("Max out:", 40, 69, 0x404040);
            
            var1.drawString("Items:", 40, 89, 0x404040);
            
            var1.drawString(new Integer(tpp.maxOutput).toString(), 86, 69, 14737632);
            var1.drawString(new Integer(tpp.itemsForMax).toString(), 86, 89, 14737632);

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
        int k = (i + 129) * scaling.getScaleFactor();
        int l = (((j + 20) + 24) - 8) * scaling.getScaleFactor();
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
        int k = (i + 298) * scaling.getScaleFactor();
        int l = (j + 65) * scaling.getScaleFactor();
        int i1 = 64 * scaling.getScaleFactor();
        int j1 = 118 * scaling.getScaleFactor();

        if (GuiAPressurePlate.playerScrollHeight == 118)
        {
            i1 = 69 * scaling.getScaleFactor();
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(k, l, i1, j1);
    }

    private void settingsClip(int i, int j)
    {
        int k = (i + 24) * scaling.getScaleFactor();
        int l = (j + 41) * scaling.getScaleFactor();
        int i1 = 87 * scaling.getScaleFactor();
        int j1 = 137 * scaling.getScaleFactor();
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
        int l = 129 + ((k & 1) != 0 ? 80 : 0);
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
        int l = 299;
        int i1 = (-8 + 14 * (k) + 20) - playerScrollY;
        return i >= l - 1 && i < l + 9 && j >= i1 - 1 && j < i1 + 10;
    }

    public boolean mouseInSettingsRadioButton(int i, int j, int k)
    {
        PPSettings.SettingsButton var1 = (PPSettings.SettingsButton)tpp.settings.get(k);
        int l = 26;
        int i1 = (-17 + 14 * (var1.line) + 20) - settingsScrollY;
        return i >= l && i < l + 8 && j >= i1 - 1 && j < i1 + 9;
    }

    public boolean mouseInSettingsLine(int i, int j, int k)
    {
        SettingsLine var1 = (SettingsLine)PPSettings.settingsLines.get(k);
        int l = 26;
        int i1 = (-17 + 14 * (var1.line) + 20) - settingsScrollY;
        return i >= l - 2 && i < l + 86 && j >= i1 - 2 && j < i1 + 10;
    }

    private void Textclip1(int i, int j)
    {
        int k = (i + 218) * scaling.getScaleFactor();
        int l = (((j + 20) + 24) - 8) * scaling.getScaleFactor();
        int i1 = 64 * scaling.getScaleFactor();
        int j1 = 139 * scaling.getScaleFactor();

        if (this.scrollHeight == 139)
        {
            i1 = 69 * scaling.getScaleFactor();
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(k, l, i1, j1);
    }

    private void Textclip2(int i, int j)
    {
        int k = (i + 129) * scaling.getScaleFactor();
        int l = (((j + 20) + 24) - 8) * scaling.getScaleFactor();
        int i1 = 79 * scaling.getScaleFactor();
        int j1 = 139 * scaling.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(k, l, i1, j1);
    }

    /**
     * draws the texts of the gui.
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        RenderHelper.disableStandardItemLighting();
        FontRenderer var1 = game.fontRenderer;
        var1.drawString("Advanced " + StatCollector.translateToLocal("tile.pressurePlate.name"), 127, 6, 0x404040);

        if (this.tabOpen == 0)
        {
            var1.drawString("P", 296, 8, 0x404040);
            var1.drawString("l", 297, 16, 0x404040);
            var1.drawString("a", 296, 22, 0x404040);
            var1.drawString("y", 296, 28, 0x404040);
            var1.drawString("e", 296, 35, 0x404040);
            var1.drawString("r", 296, 41, 0x404040);
            var1.drawString("s", 296, 47, 0x404040);
        }
        else if (this.tabOpen == 1)
        {
            var1.drawString("P", 376, 8, 0x404040);
            var1.drawString("l", 377, 16, 0x404040);
            var1.drawString("a", 376, 22, 0x404040);
            var1.drawString("y", 376, 28, 0x404040);
            var1.drawString("e", 376, 35, 0x404040);
            var1.drawString("r", 376, 41, 0x404040);
            var1.drawString("s", 376, 47, 0x404040);
            var1.drawString("+", 376, 124, 0xffffff);
        }

        if (this.tab2Open == 0 || this.tab2Open == 1)
        {
            drawTextureText(var1);
        }
        if (this.tab2Open == 0 || this.tab2Open == 3)
        {
            drawOutputText(var1);
        }

        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        for (int k = 0; k < tpp.allowedMobs.length; k++)
        {
            int l = ((k & 1) != 0 ? 80 : 0) + 138;
            int i1 = (14 * (k >> 1) + 39) - scrollY;
            String s = (new StringBuilder("entity.")).append(tpp.allowedMobs[k].getMobname()).append(".name").toString();
            String s1 = StatCollector.translateToLocal(s);

            if (s1 == s)
            {
                s1 = tpp.allowedMobs[k].getMobname();
            }

            if (s1 == "humanoid")
            {
                s1 = "Players";
            }

            if (s1 == "Item")
            {
                s1 = "Items";
            }

            if ((k & 1) != 0)
            {
                Textclip1(i, j);
            }
            else
            {
                Textclip2(i, j);
            }

            var1.drawString(s1, l, i1 - 18, 0xffffff);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        if (this.tabOpen == 1)
        {
            GuiAPressurePlate.lineUp();
            playerClip(i, j);

            for (int k = 0; k < tpp.allowedPlayers.size(); k++)
            {
                int l = 170 + 138;
                int i1 = (14 * (k) + 31) - playerScrollY;
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
        int i = ((width - xSize) / 2) + 282;
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
        if (GuiAPressurePlate.enabledPlayers.length != 0)
        {
            int j = 0;
            int i = ((width - xSize) / 2) + 362;

            if (playerListHeight != 0)
            {
                j = ((height - ySize) / 2) + 11 + (playerScrollY * (118 - playerScrollHeight)) / playerListHeight;
            }

            int k = j;
            drawTexturedModalRect(i, k, 176, 9, 5, 1);

            if (enabledPlayers.length != 0)
            {
                for (k++; k < (j + playerScrollHeight) - 1; k++)
                {
                    drawTexturedModalRect(i, k, 176, 10, 5, 1);
                }

                drawTexturedModalRect(i, k, 176, 11, 5, 1);
            }
        }
    }

    private void drawSettingsScrollBar()
    {
        if (PPSettings.lines.size() != 0)
        {
            int j = 0;
            int i = ((width - xSize) / 2) + 112;

            if (settingsListHeight != 0)
            {
                j = ((height - ySize) / 2) + 16 + (settingsScrollY * (137 - settingsScrollHeight)) / settingsListHeight;
            }

            int k = j;
            drawTexturedModalRect(i, k, 176, 9, 5, 1);

            if (PPSettings.lines.size() != 0)
            {
                for (k++; k < (j + settingsScrollHeight) - 1; k++)
                {
                    drawTexturedModalRect(i, k, 176, 10, 5, 1);
                }

                drawTexturedModalRect(i, k, 176, 11, 5, 1);
            }
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
        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            this.mc.thePlayer.closeScreen();
        }
    }
}
