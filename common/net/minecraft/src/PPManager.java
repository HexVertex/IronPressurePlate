/**
 * this is a class used to get protected values
 * of other classes in the net.minecraft.src package.
 * 
 * @author Kalvin
 */

package net.minecraft.src;

public class PPManager 
{
	public static String getEntityString(Entity entity)
	{
		return entity.getEntityString();
	}
	
	public static String getEntityType(EntityLiving entityliving)
	{
		return entityliving.entityType;
	}
	
	public static void closeGuiScreen(EntityPlayer player)
	{
		player.closeScreen();
	}
}
