package xelitez.ironpp;

public class Version
{
    public static int majorVersion = 3;
    public static int minorVersion = 2;
    public static int majorBuild = 2;
    public static int minorBuild = 0;
    public static String MC = "MC:1.4.2";

    public static String getVersion()
    {
        boolean var0 = minorVersion != 0;
        boolean var1 = majorBuild != 0;
        boolean var2 = minorBuild != 0;
        StringBuilder var3 = new StringBuilder();
        var3.append(majorVersion);

        if (var0)
        {
            var3.append(".");
            var3.append(minorVersion);
        }

        if (var1)
        {
            var3.append(".");
            var3.append(majorBuild);
        }

        if (var2)
        {
            var3.append(".");
            var3.append(minorBuild);
        }

        return var3.toString();
    }
}
