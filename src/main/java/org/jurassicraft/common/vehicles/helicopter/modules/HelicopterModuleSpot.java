package org.jurassicraft.common.vehicles.helicopter.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.jurassicraft.common.vehicles.helicopter.EntityHelicopterBase;

import java.util.List;
import java.util.Map;

public class HelicopterModuleSpot
{

    private final List<HelicopterModule> modules;
    private final Map<HelicopterModule, NBTTagCompound> moduleData;
    private final float angleFromCenter;
    private final EnumModulePosition position;
    private final EntityHelicopterBase helicopter;

    public HelicopterModuleSpot(EnumModulePosition pos, EntityHelicopterBase helicopter, float angleFromCenter)
    {
        this.helicopter = helicopter;
        this.position = pos;
        this.angleFromCenter = angleFromCenter;
        modules = Lists.newArrayList();
        moduleData = Maps.newHashMap();
    }

    /**
     * List of modules present in this spot.
     */
    public List<HelicopterModule> getModules()
    {
        return modules;
    }

    public boolean addModule(HelicopterModule m)
    {
        return addModule(m, null, new Vec3(0,0,0));
    }

    public boolean addModule(HelicopterModule m, EntityPlayer player, Vec3 v)
    {
        if(!modules.contains(m))
        {
            modules.add(m);
            moduleData.put(m, new NBTTagCompound());
            m.onAdded(this, player, v);
            return true;
        }
        return false;
    }

    /**
     * Angle of the module spot compared to the right door (in radians)
     */
    public float getAngleFromCenter()
    {
        return angleFromCenter;
    }

    public void readFromNBT(NBTTagCompound tagList)
    {
        modules.clear();
        for (HelicopterModule m : HelicopterModule.registry.values())
        {
            if(tagList.hasKey(m.getModuleID()))
            {
                NBTTagCompound data = tagList.getCompoundTag(m.getModuleID());
                addModule(m);
                moduleData.put(m, data);
            }
        }
    }

    public void writeToNBT(NBTTagCompound tagList)
    {
        for (HelicopterModule m : modules)
        {
            tagList.setTag(m.getModuleID(), moduleData.get(m));
        }
    }

    public void readSpawnData(ByteBuf data)
    {
        modules.clear();
        int size = data.readByte();
        for (int i = 0; i < size; i++)
        {
            String id = ByteBufUtils.readUTF8String(data);
            HelicopterModule module = HelicopterModule.registry.get(id);
            if (module == null)
            {
                System.err.println("Null module for id " + id);
            }
            NBTTagCompound nbt = ByteBufUtils.readTag(data);
            addModule(module);
            moduleData.put(module, nbt);
        }
    }

    public void writeSpawnData(ByteBuf data)
    {
        data.writeByte(modules.size());
        for (HelicopterModule m : modules)
        {
            ByteBufUtils.writeUTF8String(data, m.getModuleID());
            ByteBufUtils.writeTag(data, moduleData.get(m));
            System.out.println("Wrote for "+m.getModuleID()+": "+moduleData.get(m));
        }
    }

    public EnumModulePosition getPosition()
    {
        return position;
    }

    public boolean isClicked(Vec3 v)
    {
        return position.isClicked(v);
    }

    public void onClicked(EntityPlayer player, Vec3 vec)
    {
        for(HelicopterModule m : modules)
        {
            if(m.onClicked(this, player, vec))
                return;
        }
    }

    public EntityHelicopterBase getHelicopter()
    {
        return helicopter;
    }

    NBTTagCompound getModuleData(HelicopterModule m)
    {
        return moduleData.get(m);
    }
}