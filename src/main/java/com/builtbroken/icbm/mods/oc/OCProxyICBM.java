package com.builtbroken.icbm.mods.oc;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.lib.mod.loadable.AbstractLoadable;

/** Proxy used to load content for Open computers
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class OCProxyICBM extends AbstractLoadable
{
    @Override
    public void preInit()
    {
        ICBM.blockDirectSiloController = ICBM.INSTANCE.getManager().newBlock("icbmDirectSiloConnector", TileSiloControllerOC.class);
    }
}
