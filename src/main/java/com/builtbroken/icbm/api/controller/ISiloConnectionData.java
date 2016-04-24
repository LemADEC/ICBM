package com.builtbroken.icbm.api.controller;

import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.map.radio.wireless.ConnectionStatus;

/**
 * Data pointer for information about a silo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/21/2016.
 */
public interface ISiloConnectionData extends IWorldPosition, ISave
{
    /**
     * User defined display name for the silo.
     *
     * @return name of the silo
     */
    String getSiloName();

    /**
     * Silo connected at that location provided.
     * <p>
     * DO NOT LOAD CHUNKS
     *
     * @return launcher, null if has no connection
     */
    ILauncher getSilo();

    /**
     * Gets the missile in the launcher
     * @return missile
     */
    IMissile getMissile();

    /**
     * First thing checked.
     * <p>
     * Gets the connection status for the silo. If
     * tile is missing return OFFLINE and attempt
     * to see if the tile still exists.
     *
     * @return status
     */
    ConnectionStatus getSiloStatus();
}