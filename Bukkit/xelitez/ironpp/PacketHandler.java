package xelitez.ironpp;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.server.v1_4_6.EntityHuman;
import net.minecraft.server.v1_4_6.INetworkManager;
import net.minecraft.server.v1_4_6.ItemStack;
import net.minecraft.server.v1_4_6.Packet250CustomPayload;
import net.minecraft.server.v1_4_6.TileEntity;
import net.minecraft.server.v1_4_6.World;

public class PacketHandler implements IPacketHandler
{
    public void onPacketData(INetworkManager var1, Packet250CustomPayload var2, Player var3)
    {
        ByteArrayDataInput var4 = ByteStreams.newDataInput(var2.data);
        boolean var5 = var4.readBoolean();
        boolean var6 = false;
        short var7 = var4.readShort();

        if (var7 == 0)
        {
            System.out.println("Invalid IronPP Packet recieved");
        }
        else
        {
            if (var5)
            {
                this.handleServerPacket(var1, var2, var3, var4, var7);
            }
            else
            {
                System.out.println("Invalid IronPP Packet recieved");
            }
        }
    }

    public void handleServerPacket(INetworkManager var1, Packet250CustomPayload var2, Player var3, ByteArrayDataInput var4, short var5)
    {
        EntityHuman var6 = (EntityHuman)var3;
        World var7 = var6.world;
        int[] var8;
        int var9;
        StringBuilder var11;
        int var12;
        TileEntityPressurePlate var17;
        String var19;
        short var18;

        if (var5 == 1)
        {
            var8 = new int[3];

            for (var9 = 0; var9 < 3; ++var9)
            {
                var8[var9] = var4.readInt();
            }

            var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
            var18 = var4.readShort();
            var11 = new StringBuilder();

            for (var12 = 0; var12 < var18; ++var12)
            {
                var11.append(var4.readChar());
            }

            var19 = var11.toString();
            var17.switchMob(var19, var7);
        }
        else if (var5 == 2)
        {
            var8 = new int[3];

            for (var9 = 0; var9 < 3; ++var9)
            {
                var8[var9] = var4.readInt();
            }

            var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
            var18 = var4.readShort();
            var11 = new StringBuilder();

            for (var12 = 0; var12 < var18; ++var12)
            {
                var11.append(var4.readChar());
            }

            var19 = var11.toString();
            var17.switchPlayer(var19, var7);
        }
        else
        {
            int var14;
            char var15;
            int var16;
            String var20;
            char var22;
            boolean var24;

            if (var5 == 3)
            {
                var8 = new int[3];

                for (var9 = 0; var9 < 3; ++var9)
                {
                    var8[var9] = var4.readInt();
                }

                if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                {
                    var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                    var16 = var4.readInt();
                    var20 = "";

                    for (var12 = 0; var12 < var16; ++var12)
                    {
                        var22 = var4.readChar();
                        var20 = var20 + var22;
                    }

                    var12 = var4.readInt();
                    String var23 = "";

                    for (var14 = 0; var14 < var12; ++var14)
                    {
                        var15 = var4.readChar();
                        var23 = var23 + var15;
                    }

                    var24 = var17.addPlayer(var20);
                    PacketSendManager.sendAddBooleanToClient(var24, var8[0], var8[1], var8[2], var23);
                    PacketSendManager.sendPressurePlatePlayerDataToClient(var17);
                }
            }
            else if (var5 == 4)
            {
                var8 = new int[3];

                for (var9 = 0; var9 < 3; ++var9)
                {
                    var8[var9] = var4.readInt();
                }

                if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                {
                    var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                    var16 = var4.readInt();
                    var20 = "";

                    for (var12 = 0; var12 < var16; ++var12)
                    {
                        var22 = var4.readChar();
                        var20 = var20 + var22;
                    }

                    var19 = "";
                    int var21 = var4.readInt();

                    for (var14 = 0; var14 < var21; ++var14)
                    {
                        var15 = var4.readChar();
                        var19 = var19 + var15;
                    }

                    var24 = var17.removePlayer(var20);
                    PacketSendManager.sendRemoveBooleanToClient(var24, var8[0], var8[1], var8[2], var19);
                    PacketSendManager.sendPressurePlatePlayerDataToClient(var17);
                }
            }
            else if (var5 == 5)
            {
                var8 = new int[3];

                for (var9 = 0; var9 < 3; ++var9)
                {
                    var8[var9] = var4.readInt();
                }

                if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                {
                    var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                    PacketSendManager.sendPressurePlatePlayerDataToClient(var17);
                }
            }
            else if (var5 == 6)
            {
                var8 = new int[3];

                for (var9 = 0; var9 < 3; ++var9)
                {
                    var8[var9] = var4.readInt();
                }

                var17 = null;

                if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                {
                    var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                }

                if (var17 != null)
                {
                    PacketSendManager.sendPressurePlateMobDataToClient(var17);
                    PacketSendManager.sendPressurePlatePlayerDataToClient(var17);
                    PacketSendManager.sendSettingsDataToClient(var17);
                }
            }
            else
            {
                if (var5 == 7)
                {
                    PPRegistry.loggedIn = true;
                }

                if (var5 == 8)
                {
                    var8 = new int[3];

                    for (var9 = 0; var9 < 3; ++var9)
                    {
                        var8[var9] = var4.readInt();
                    }

                    var17 = null;

                    if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                    {
                        var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                    }

                    if (var17 != null)
                    {
                        var16 = var4.readInt();
                        var17.switchSetting(var16);
                    }
                }
                else if (var5 == 9)
                {
                    var8 = new int[3];

                    for (var9 = 0; var9 < 3; ++var9)
                    {
                        var8[var9] = var4.readInt();
                    }

                    var17 = null;

                    if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                    {
                        var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                    }

                    if (var17 != null)
                    {
                        var16 = var4.readInt();
                        var11 = new StringBuilder();

                        for (var12 = 0; var12 < var16; ++var12)
                        {
                            var11.append(var4.readChar());
                        }

                        var19 = var11.toString();
                        var17.password = var19;
                        PacketSendManager.sendPasswordSetToClient(var17, var3);
                    }
                }
                else if (var5 == 10)
                {
                    var8 = new int[3];

                    for (var9 = 0; var9 < 3; ++var9)
                    {
                        var8[var9] = var4.readInt();
                    }

                    var17 = null;

                    if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                    {
                        var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                    }

                    if (var17 != null)
                    {
                        var16 = var4.readInt();
                        var11 = new StringBuilder();

                        for (var12 = 0; var12 < var16; ++var12)
                        {
                            var11.append(var4.readChar());
                        }

                        var19 = var11.toString();
                        boolean var13 = var17.password.matches(var19);
                        PacketSendManager.sendPasswordResponseToClient(var17, Boolean.valueOf(var13), var3);
                    }
                }
                else if (var5 == 11)
                {
                    var8 = new int[3];

                    for (var9 = 0; var9 < 3; ++var9)
                    {
                        var8[var9] = var4.readInt();
                    }

                    var17 = null;

                    if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                    {
                        var17 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                    }

                    if (var17 != null)
                    {
                        PacketSendManager.sendPasswordToClient(var17, var3);
                    }
                }
                else if (var5 == 12)
                {
                    var8 = new int[3];

                    for (var9 = 0; var9 < 3; ++var9)
                    {
                        var8[var9] = var4.readInt();
                    }

                    var9 = var4.readInt();
                    TileEntityPressurePlate var10 = null;

                    if (var7.getTileEntity(var8[0], var8[1], var8[2]) instanceof TileEntityPressurePlate)
                    {
                        var10 = (TileEntityPressurePlate)var7.getTileEntity(var8[0], var8[1], var8[2]);
                    }

                    if (var10 != null)
                    {
                        var6.openGui(IronPP.instance, var9, var7, var10.x, var10.y, var10.z);
                    }
                }
                else
                {
                    if (var5 == 13)
                    {
                        var8 = new int[3];

                        for (var9 = 0; var9 < 3; ++var9)
                        {
                            var8[var9] = var4.readInt();
                        }

                        var7.setTypeId(var8[0], var8[1], var8[2], 0);
                    }
                }
            }
        }
    }
}
