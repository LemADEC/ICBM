package icbm.content.rocketlauncher;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import icbm.Settings;
import icbm.ICBM;
import icbm.content.missile.EntityMissile;
import icbm.content.missile.ItemMissile;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import resonant.api.explosion.IExplosive;
import resonant.lib.prefab.item.ItemElectric;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.LanguageUtility;

/** Rocket Launcher
 * 
 * @author Calclavia */

public class ItemRocketLauncher extends ItemElectric
{
    private static final int ENERGY = 1000000;
    private static final int firingDelay = 1000;
    private HashMap<String, Long> clickTimePlayer = new HashMap<String, Long>();

    public ItemRocketLauncher()
    {
        super();
        this.setUnlocalizedName("rocketLauncher");
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            long clickMs = System.currentTimeMillis();
            if (clickTimePlayer.containsKey(player.getDisplayName()))
            {
                if (clickMs - clickTimePlayer.get(player.getDisplayName()) < firingDelay)
                {
                    //TODO play weapon empty click audio to note the gun is reloading
                    return itemStack;
                }
            }
            if (this.getEnergy(itemStack) >= ENERGY || player.capabilities.isCreativeMode)
            {
                // Check the player's inventory and look for missiles.
                for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
                {
                    ItemStack inventoryStack = player.inventory.getStackInSlot(slot);

                    if (inventoryStack != null)
                    {
                        if (inventoryStack.getItem() instanceof ItemMissile)
                        {
                            int meta = inventoryStack.getItemDamage();
                            IExplosive ex = ((ItemMissile)inventoryStack.getItem()).getExplosive(inventoryStack);

                            if (ex != null)
                            {
                                Vector3 launcher = new Vector3(player).add(new Vector3(0, 0.5, 0));
                                Vector3 playerAim = new Vector3(player.getLook(1));
                                Vector3 start = launcher.add(playerAim.multiply(1.1));
                                Vector3 target = launcher.add(playerAim.multiply(100));

                                //TOD: Fix this rotation when we use the proper model loader.
                                EntityMissile entityMissile = new EntityMissile(world, start, ex.getUnlocalizedName(), -player.rotationYaw, -player.rotationPitch);
                                world.spawnEntityInWorld(entityMissile);

                                if (player.isSneaking())
                                {
                                    player.mountEntity(entityMissile);
                                    player.setSneaking(false);
                                }

                                entityMissile.ignore(player);
                                entityMissile.launch(target);

                                if (!player.capabilities.isCreativeMode)
                                {
                                    player.inventory.setInventorySlotContents(slot, null);
                                    this.discharge(itemStack, ENERGY, true);
                                }

                                //Store last time player launched a rocket
                                clickTimePlayer.put(player.getDisplayName(), clickMs);

                                return itemStack;
                            }
                        }
                    }
                }
            }
        }

        return itemStack;
    }

    @Override
    public double getEnergyCapacity(ItemStack theItem)
    {
        return ENERGY * 16;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4)
    {
        String str = LanguageUtility.getLocal("info.rocketlauncher.tooltip").replaceAll("%s", String.valueOf(Settings.MAX_ROCKET_LAUCNHER_TIER));
        list.add(str);

        super.addInformation(itemStack, entityPlayer, list, par4);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        ItemStack currentItem = event.player.getCurrentEquippedItem();

        if (currentItem != null && (event.player != Minecraft.getMinecraft().renderViewEntity || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0))
        {
            if (currentItem.getItem() == ICBM.itemRocketLauncher)
            {
                if (event.player.getItemInUseCount() <= 0)
                {
                    event.player.setItemInUse(currentItem, Integer.MAX_VALUE);
                }
            }
        }
    }
}