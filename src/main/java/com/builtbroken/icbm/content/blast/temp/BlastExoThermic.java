package com.builtbroken.icbm.content.blast.temp;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.lib.world.edit.PlacementData;
import com.builtbroken.mc.lib.world.heat.HeatedBlockRegistry;
import com.builtbroken.mc.prefab.entity.damage.DamageSources;
import com.builtbroken.mc.prefab.entity.selector.EntitySelectors;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Blast that releases a lot of energy into the environment making it very hot.
 * Created by robert on 2/24/2015.
 */
public class BlastExoThermic extends BlastSimplePath<BlastExoThermic>
{
    public BlastExoThermic(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlockEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        if (block != null)
        {
            //TODO change temp to be based on init energy and heating data of the block
            //TODO change to dump heat into the heat map
            PlacementData data = HeatedBlockRegistry.getResultWarmUp(block, getTempForDistance(location.distance(x, y, z)));
            if (data != null && data.block() != null)
            {
                BlockEdit edit = new BlockEdit(location);
                edit.set(data.block(), data.meta() == -1 ? 0 : data.meta(), false, true);
                return edit;
            }
            else if (location.isAirBlock())
            {
                Location loc = location.add(0, -1, 0);
                if (!loc.isAirBlock() && loc.isSideSolid(ForgeDirection.UP))
                {
                    BlockEdit edit = new BlockEdit(location);
                    edit.set(Blocks.fire, 0, false, true);
                    return edit;
                }
            }
        }
        return null;
    }

    @Override
    public boolean shouldPathTo(Location last, Location next)
    {
        if (super.shouldPathTo(last, next))
        {
            if (last.isAirBlock() && next.isAirBlock())
            {
                return last.sub(next).toForgeDirection() != ForgeDirection.UP;
            }
            //TODO check if the block has thermal properties
            //TODO if yes then check if it allows heat transfer
            //TODO if allows heat transfer we can path
            return true;
        }
        return false;
    }

    private int getTempForDistance(double distance)
    {
        return 20000 - (int) ((20000 / size) * distance);
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            DamageSource source = DamageSources.THERMAL_INCREASE.getSource(this);
            List<Entity> list = EntitySelectors.LIVING_SELECTOR.selector().getEntities(this, size * 2);
            for (Entity entity : list)
            {
                double distance = entity.getDistance(x, y, z);
                int temp = getTempForDistance(distance);
                float damage = temp / 1000.0f;
                if (entity.attackEntityFrom(source, damage))
                {
                    entity.setFire((int) damage);
                }
            }
        }
    }

    @Override
    public void doStartDisplay()
    {
        //Mainly just to disable default effects
    }

    @Override
    public void doEndDisplay()
    {
        //Mainly just to disable default effects
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {
        if (!world.isRemote)
        {
            Engine.proxy.spawnParticle("lava", world, blocks.x(), blocks.y(), blocks.z(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {
        if (!world.isRemote)
        {
            world.playSoundEffect(blocks.x(), blocks.y(), blocks.z(), "liquid.lavapop", 0.2F + world.rand.nextFloat() * 0.2F, 0.9F + world.rand.nextFloat() * 0.15F);
        }
    }
}
