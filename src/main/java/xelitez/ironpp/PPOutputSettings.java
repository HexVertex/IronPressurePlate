package xelitez.ironpp;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PPOutputSettings 
{
	public HashMap<Integer, Integer> output = new HashMap<Integer, Integer>();
	
	public PPOutputSettings(TileEntityPressurePlate tpp)
	{
		for(int i = 0; i < tpp.allowedMobs.length;i++)
		{
			output.put(i, 15);
		}
	}
	
	public void saveToNBT(NBTTagCompound nbt)
	{
        NBTTagList var1 = new NBTTagList();

        for (int var2 = 0; var2 < output.size(); var2++)
        {
            NBTTagCompound var3 = new NBTTagCompound();
            var3.setInteger("id", var2);
            var3.setInteger("output", output.get(var2));
            var1.appendTag(var3);
        }

        nbt.setTag("OutputS", var1);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
        NBTTagList var1 = nbt.getTagList("OutputS", 10);

        for (int var2 = 0; var2 < var1.tagCount(); var2++)
        {
            NBTTagCompound var4 = (NBTTagCompound)var1.getCompoundTagAt(var2);
            int var3 = var4.getInteger("id");
            int var5 = var4.getInteger("output");

            if (var3 >= 0)
            {
                this.setOutput(var3, var5);
            }
        }
	}
	
	public void setOutput(int id, int out)
	{
		output.put(id, out);
	}
}
