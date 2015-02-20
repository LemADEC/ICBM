package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ILauncher;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileSmall;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/** Mainly a test launcher for devs this tile also can be used by players as a small portable launcher
 * Created by robert on 1/18/2015.
 */
public class TileSmallLauncher extends TileMissileContainer implements ILauncher, ISimpleItemRenderer, IGuiTile
{
    public Pos target = null;

    @SideOnly(Side.CLIENT)
    private static IModelCustom launcher_model;

    public TileSmallLauncher()
    {
        super("smallLauncher", Material.anvil);
        this.addInventoryModule(1);
        this.bounds = new Cube(0, 0, 0, 1, .5, 1);
        this.isOpaque = false;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.flint_and_steel)
        {
            fireMissile();
            return true;
        }
        else if(player.getHeldItem() == null)
        {
            openGui(player, ICBM.INSTANCE);
            return true;
        }
        return super.onPlayerRightClick(player, side, hit);
    }

    @Override
    public void update()
    {
        super.update();
        if (ticks % 20 == 0)
        {
            if (world().isBlockIndirectlyGettingPowered(xi(), yi(), zi()))
            {
                fireMissile();
            }
        }
    }

    public void fireMissile()
    {
        Missile missile = getMissile();
        if(missile != null)
        {
            if (isServer())
            {
                //Create and setup missile
                EntityMissile entity = new EntityMissile(world());
                entity.setMissile(missile);
                entity.setPositionAndRotation(x() + 0.5, y() + 3, z() + 0.5, 0, 0);
                entity.setVelocity(0, 2, 0);

                //Set target data
                entity.setTarget(new Pos((TileEntity) this).add(Pos.north.multiply(50)), true);
                entity.sourceOfProjectile = new Pos(this);

                //Spawn and start moving
                world().spawnEntityInWorld(entity);
                entity.setIntoMotion();

                //Empty inventory slot
                this.setInventorySlotContents(0, null);
                sendDescPacket();
            }
            else
            {
                //TODO add some effects
                for (int l = 0; l < 20; ++l)
                {
                    double f = x() + 0.5 + 0.3 * (world().rand.nextFloat() - world().rand.nextFloat());
                    double f1 = y() + 0.1 + 0.5 * (world().rand.nextFloat() - world().rand.nextFloat());
                    double f2 = z() + 0.5 + 0.3 * (world().rand.nextFloat() - world().rand.nextFloat());
                    world().spawnParticle("largesmoke", f, f1, f2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public boolean canAcceptMissile(Missile missile)
    {
        return missile != null && missile.casing == MissileCasings.SMALL;
    }

    @Override
    public Tile newTile()
    {
        return new TileSmallLauncher();
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.gravel.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }

    @Override
    public String getInventoryName()
    {
        return name + ".container";
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        //Import model if missing
        if(launcher_model == null)
        {
            launcher_model = AdvancedModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_PREFIX + "small_launcher.tcn"));
        }

        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.8f, .8f, .8f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(MissileSmall.TEXTURE);
        launcher_model.renderAllExcept("rail");
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 2, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Import model if missing
        if(launcher_model == null)
        {
            launcher_model = AdvancedModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_PREFIX + "small_launcher.tcn"));
        }

        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(MissileSmall.TEXTURE);
        launcher_model.renderAll();
        GL11.glPopMatrix();

        //Render missile
        if(getMissile() != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
            GL11.glScaled(.0015625f, .0015625f, .0015625f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(MissileSmall.TEXTURE);
            MissileSmall.MODEL.renderAll();
            GL11.glPopMatrix();
        }
    }

    @Override @SideOnly(Side.CLIENT)
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDummy(player, this);
    }

    @Override @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSmallLauncher(this, player);
    }
}
