package com.builtbroken.icbm.content.crafting.missile.trigger.impact;

import com.builtbroken.icbm.content.crafting.missile.trigger.Triggers;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/27/2016.
 */
public class ImpactTriggerElectrical extends ImpactTrigger
{
    public ImpactTriggerElectrical(ItemStack item)
    {
        super(item, Triggers.ELECTRICAL_IMPACT);
    }
}
