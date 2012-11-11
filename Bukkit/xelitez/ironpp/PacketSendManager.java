package xelitez.ironpp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Packet250CustomPayload;

public class PacketSendManager
{

    private static void sendPacketToAllPlayers(Packet250CustomPayload var0)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().getServerConfigurationManager().sendAll(var0);
    }

    public static void sendCloseGuiPacketToAllPlayers(TileEntityPressurePlate var0)
    {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);

        try
        {
            var2.writeBoolean(false);
            var2.writeShort(1);
            int[] var3 = new int[] {var0.x, var0.y, var0.z};

            for (int var4 = 0; var4 < 3; ++var4)
            {
                var2.writeInt(var3[var4]);
            }
        }
        catch (IOException var5)
        {
            var5.printStackTrace();
        }

        Packet250CustomPayload var6 = new Packet250CustomPayload();
        var6.tag = "IPP";
        var6.data = var1.toByteArray();
        var6.length = var6.data.length;
        sendPacketToAllPlayers(var6);
    }

    public static void sendPressurePlateMobDataToClient(TileEntityPressurePlate var0)
    {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);

        try
        {
            var2.writeBoolean(false);
            var2.writeShort(2);
            int[] var3 = new int[] {var0.x, var0.y, var0.z};
            int var4;

            for (var4 = 0; var4 < 3; ++var4)
            {
                var2.writeInt(var3[var4]);
            }

            var2.writeInt(var0.allowedMobs.length);

            for (var4 = 0; var4 < var0.allowedMobs.length; ++var4)
            {
                var2.writeBoolean(var0.allowedMobs[var4].getEnabled());
            }
        }
        catch (IOException var5)
        {
            var5.printStackTrace();
        }

        Packet250CustomPayload var6 = new Packet250CustomPayload();
        var6.tag = "IPP";
        var6.data = var1.toByteArray();
        var6.length = var6.data.length;
        sendPacketToAllPlayers(var6);
    }

    public static void sendPressurePlatePlayerDataToClient(TileEntityPressurePlate var0)
    {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);

        try
        {
            var2.writeBoolean(false);
            var2.writeShort(3);
            int[] var3 = new int[] {var0.x, var0.y, var0.z};
            int var4;

            for (var4 = 0; var4 < 3; ++var4)
            {
                var2.writeInt(var3[var4]);
            }

            var2.writeInt(var0.allowedPlayers.size());

            for (var4 = 0; var4 < var0.allowedPlayers.size(); ++var4)
            {
                var2.writeShort(((PPPlayerList)var0.allowedPlayers.get(var4)).getUsername().length());

                for (int var5 = 0; var5 < ((PPPlayerList)var0.allowedPlayers.get(var4)).getUsername().length(); ++var5)
                {
                    var2.writeChar(((PPPlayerList)var0.allowedPlayers.get(var4)).getUsername().charAt(var5));
                }

                var2.writeBoolean(((PPPlayerList)var0.allowedPlayers.get(var4)).getEnabled());
            }
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }

        Packet250CustomPayload var7 = new Packet250CustomPayload();
        var7.tag = "IPP";
        var7.data = var1.toByteArray();
        var7.length = var7.data.length;
        sendPacketToAllPlayers(var7);
    }

    public static void sendSwitchMobButtonPacketToClient(TileEntityPressurePlate var0, int var1)
    {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        DataOutputStream var3 = new DataOutputStream(var2);

        try
        {
            var3.writeBoolean(false);
            var3.writeShort(4);
            int[] var4 = new int[] {var0.x, var0.y, var0.z};

            for (int var5 = 0; var5 < 3; ++var5)
            {
                var3.writeInt(var4[var5]);
            }

            if (var0.allowedMobs[var1] != null)
            {
                var3.writeInt(var1);
            }
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }

        Packet250CustomPayload var7 = new Packet250CustomPayload();
        var7.tag = "IPP";
        var7.data = var2.toByteArray();
        var7.length = var7.data.length;
        sendPacketToAllPlayers(var7);
    }

    public static void sendAddBooleanToClient(boolean var0, int var1, int var2, int var3, String var4)
    {
        ByteArrayOutputStream var5 = new ByteArrayOutputStream();
        DataOutputStream var6 = new DataOutputStream(var5);

        try
        {
            var6.writeBoolean(false);
            var6.writeShort(5);
            int[] var7 = new int[] {var1, var2, var3};
            int var8;

            for (var8 = 0; var8 < 3; ++var8)
            {
                var6.writeInt(var7[var8]);
            }

            var6.writeBoolean(var0);
            var6.writeInt(var4.length());

            for (var8 = 0; var8 < var4.length(); ++var8)
            {
                var6.writeChar(var4.charAt(var8));
            }
        }
        catch (IOException var9)
        {
            var9.printStackTrace();
        }

        Packet250CustomPayload var10 = new Packet250CustomPayload();
        var10.tag = "IPP";
        var10.data = var5.toByteArray();
        var10.length = var10.data.length;
        sendPacketToAllPlayers(var10);
    }

    public static void sendRemoveBooleanToClient(boolean var0, int var1, int var2, int var3, String var4)
    {
        ByteArrayOutputStream var5 = new ByteArrayOutputStream();
        DataOutputStream var6 = new DataOutputStream(var5);

        try
        {
            var6.writeBoolean(false);
            var6.writeShort(6);
            int[] var7 = new int[] {var1, var2, var3};
            int var8;

            for (var8 = 0; var8 < 3; ++var8)
            {
                var6.writeInt(var7[var8]);
            }

            var6.writeBoolean(var0);
            var6.writeInt(var4.length());

            for (var8 = 0; var8 < var4.length(); ++var8)
            {
                var6.writeChar(var4.charAt(var8));
            }
        }
        catch (IOException var9)
        {
            var9.printStackTrace();
        }

        Packet250CustomPayload var10 = new Packet250CustomPayload();
        var10.tag = "IPP";
        var10.data = var5.toByteArray();
        var10.length = var10.data.length;
        sendPacketToAllPlayers(var10);
    }

    public static void sendItemStackToClients(int var0, int var1, int var2, int var3, int var4, int var5, int var6)
    {
        ByteArrayOutputStream var7 = new ByteArrayOutputStream();
        DataOutputStream var8 = new DataOutputStream(var7);

        try
        {
            var8.writeBoolean(false);
            var8.writeShort(7);
            var8.writeInt(var0);
            var8.writeInt(var1);
            var8.writeInt(var2);
            var8.writeInt(var3);
            var8.writeInt(var5);
            var8.writeInt(var4);
            var8.writeInt(var6);
        }
        catch (IOException var10)
        {
            var10.printStackTrace();
        }

        Packet250CustomPayload var9 = new Packet250CustomPayload();
        var9.tag = "IPP";
        var9.data = var7.toByteArray();
        var9.length = var9.data.length;
        sendPacketToAllPlayers(var9);
    }

    public static void sendBlockBooleanToClient(TileEntityPressurePlate var0, boolean var1)
    {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        DataOutputStream var3 = new DataOutputStream(var2);

        try
        {
            var3.writeBoolean(false);
            var3.writeShort(8);
            int[] var4 = new int[] {var0.x, var0.y, var0.z};

            for (int var5 = 0; var5 < 3; ++var5)
            {
                var3.writeInt(var4[var5]);
            }

            var3.writeBoolean(var1);
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }

        Packet250CustomPayload var7 = new Packet250CustomPayload();
        var7.tag = "IPP";
        var7.data = var2.toByteArray();
        var7.length = var7.data.length;
        sendPacketToAllPlayers(var7);
    }

    public static void SendIsReadyToClient(Player var0)
    {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);

        try
        {
            var2.writeBoolean(false);
            var2.writeShort(9);
        }
        catch (IOException var4)
        {
            var4.printStackTrace();
        }

        Packet250CustomPayload var3 = new Packet250CustomPayload();
        var3.tag = "IPP";
        var3.data = var1.toByteArray();
        var3.length = var3.data.length;
        PacketDispatcher.sendPacketToPlayer(var3, var0);
    }

    public static void sendSwitchSettingButtonToClient(TileEntityPressurePlate var0, int var1)
    {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        DataOutputStream var3 = new DataOutputStream(var2);

        try
        {
            var3.writeBoolean(false);
            var3.writeShort(10);
            int[] var4 = new int[] {var0.x, var0.y, var0.z};

            for (int var5 = 0; var5 < 3; ++var5)
            {
                var3.writeInt(var4[var5]);
            }

            var3.writeInt(var1);
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }

        Packet250CustomPayload var7 = new Packet250CustomPayload();
        var7.tag = "IPP";
        var7.data = var2.toByteArray();
        var7.length = var7.data.length;
        sendPacketToAllPlayers(var7);
    }

    public static void sendSettingsDataToClient(TileEntityPressurePlate var0)
    {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(var1);

        try
        {
            var2.writeBoolean(false);
            var2.writeShort(11);
            int[] var3 = new int[] {var0.x, var0.y, var0.z};
            int var4;

            for (var4 = 0; var4 < 3; ++var4)
            {
                var2.writeInt(var3[var4]);
            }

            PPSettings var10001 = var0.pps;
            var2.writeInt(PPSettings.buttons.size());
            var4 = 0;

            while (true)
            {
                var10001 = var0.pps;

                if (var4 >= PPSettings.buttons.size())
                {
                    break;
                }

                var2.writeBoolean(var0.getIsEnabled(var4));
                ++var4;
            }
        }
        catch (IOException var5)
        {
            var5.printStackTrace();
        }

        Packet250CustomPayload var6 = new Packet250CustomPayload();
        var6.tag = "IPP";
        var6.data = var1.toByteArray();
        var6.length = var6.data.length;
        sendPacketToAllPlayers(var6);
    }

    public static void sendPasswordSetToClient(TileEntityPressurePlate var0, Player var1)
    {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        DataOutputStream var3 = new DataOutputStream(var2);

        try
        {
            var3.writeBoolean(false);
            var3.writeShort(12);
            int[] var4 = new int[] {var0.x, var0.y, var0.z};

            for (int var5 = 0; var5 < 3; ++var5)
            {
                var3.writeInt(var4[var5]);
            }

            String var8 = ((EntityHuman)var1).name;
            var3.writeInt(var8.length());
            var3.writeChars(var8);
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }

        Packet250CustomPayload var7 = new Packet250CustomPayload();
        var7.tag = "IPP";
        var7.data = var2.toByteArray();
        var7.length = var7.data.length;
        sendPacketToAllPlayers(var7);
    }

    public static void sendPasswordResponseToClient(TileEntityPressurePlate var0, Boolean var1, Player var2)
    {
        ByteArrayOutputStream var3 = new ByteArrayOutputStream();
        DataOutputStream var4 = new DataOutputStream(var3);

        try
        {
            var4.writeBoolean(false);
            var4.writeShort(13);
            int[] var5 = new int[] {var0.x, var0.y, var0.z};

            for (int var6 = 0; var6 < 3; ++var6)
            {
                var4.writeInt(var5[var6]);
            }

            var4.writeBoolean(var1.booleanValue());
        }
        catch (IOException var7)
        {
            var7.printStackTrace();
        }

        Packet250CustomPayload var8 = new Packet250CustomPayload();
        var8.tag = "IPP";
        var8.data = var3.toByteArray();
        var8.length = var8.data.length;
        PacketDispatcher.sendPacketToPlayer(var8, var2);
    }

    public static void sendUsesPasswordToClient(int var0, int var1, int var2, int var3, Boolean var4)
    {
        ByteArrayOutputStream var5 = new ByteArrayOutputStream();
        DataOutputStream var6 = new DataOutputStream(var5);

        try
        {
            var6.writeBoolean(false);
            var6.writeShort(14);
            int[] var7 = new int[] {var0, var1, var2};

            for (int var8 = 0; var8 < 3; ++var8)
            {
                var6.writeInt(var7[var8]);
            }

            var6.writeInt(var3);
            var6.writeBoolean(var4.booleanValue());
        }
        catch (IOException var9)
        {
            var9.printStackTrace();
        }

        Packet250CustomPayload var10 = new Packet250CustomPayload();
        var10.tag = "IPP";
        var10.data = var5.toByteArray();
        var10.length = var10.data.length;
        sendPacketToAllPlayers(var10);
    }

    public static void sendPasswordToClient(TileEntityPressurePlate var0, Player var1)
    {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        DataOutputStream var3 = new DataOutputStream(var2);

        try
        {
            var3.writeBoolean(false);
            var3.writeShort(15);
            int[] var4 = new int[] {var0.x, var0.y, var0.z};

            for (int var5 = 0; var5 < 3; ++var5)
            {
                var3.writeInt(var4[var5]);
            }

            var3.writeInt(var0.password.length());
            var3.writeChars(var0.password);
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }

        Packet250CustomPayload var7 = new Packet250CustomPayload();
        var7.tag = "IPP";
        var7.data = var2.toByteArray();
        var7.length = var7.data.length;
        PacketDispatcher.sendPacketToPlayer(var7, var1);
    }

    public static void sendRemovePressurePlateToClient(TileEntityPressurePlate var0, int var1)
    {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        DataOutputStream var3 = new DataOutputStream(var2);

        try
        {
            var3.writeBoolean(false);
            var3.writeShort(16);
            int[] var4 = new int[] {var0.x, var0.y, var0.z};

            for (int var5 = 0; var5 < 3; ++var5)
            {
                var3.writeInt(var4[var5]);
            }

            var3.writeInt(var1);
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }

        Packet250CustomPayload var7 = new Packet250CustomPayload();
        var7.tag = "IPP";
        var7.data = var2.toByteArray();
        var7.length = var7.data.length;
        sendPacketToAllPlayers(var7);
    }

    public static void sendAddPressurePlateToClient(TileEntityPressurePlate var0, int var1)
    {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        DataOutputStream var3 = new DataOutputStream(var2);

        try
        {
            var3.writeBoolean(false);
            var3.writeShort(17);
            int[] var4 = new int[] {var0.x, var0.y, var0.z};

            for (int var5 = 0; var5 < 3; ++var5)
            {
                var3.writeInt(var4[var5]);
            }

            var3.writeInt(var1);
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }

        Packet250CustomPayload var7 = new Packet250CustomPayload();
        var7.tag = "IPP";
        var7.data = var2.toByteArray();
        var7.length = var7.data.length;
        sendPacketToAllPlayers(var7);
    }

    public static void sendPPIntToClient(int var0, EntityHuman var1)
    {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        DataOutputStream var3 = new DataOutputStream(var2);

        try
        {
            var3.writeBoolean(false);
            var3.writeShort(18);
            var3.writeInt(var0);
        }
        catch (IOException var5)
        {
            var5.printStackTrace();
        }

        Packet250CustomPayload var4 = new Packet250CustomPayload();
        var4.tag = "IPP";
        var4.data = var2.toByteArray();
        var4.length = var4.data.length;
        PacketDispatcher.sendPacketToPlayer(var4, (Player)var1);
    }
}
