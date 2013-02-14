package icbm.sentry.render;

import icbm.sentry.turret.TileEntityBaseTurret;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import universalelectricity.core.vector.Vector3;

/**
 * 
 * @author Darkguardsman
 * 
 */
public class LookHelper
{
	TileEntityBaseTurret sentry;

	public LookHelper(TileEntityBaseTurret turret)
	{
		this.sentry = turret;
	}

	/**
	 * Adjusts the turret to look at a specific location.
	 */
	public void lookAt(Vector3 target)
	{
		sentry.rotationYaw = getYaw(sentry.getMuzzle(), target);
		sentry.rotationPitch = getPitch(sentry.getMuzzle(), target);
	}

	/**
	 * Tells the turret to look at a location using an entity
	 */
	public void lookAtEntity(Entity entity)
	{
		this.lookAt(Vector3.add(new Vector3(entity), new Vector3(0, entity.getEyeHeight(), 0)));
	}

	/**
	 * checks to see if the sentry is looking the target location
	 * 
	 * @param target - xyz target
	 * @param allowedError - amount these sentry can be off in degrees from target
	 * @return true if its with in error range
	 */
	public boolean isLookingAt(Vector3 target, float allowedError)
	{
		float pitch = getPitch(sentry.getMuzzle(), target);
		float yaw = getYaw(sentry.getMuzzle(), target);

		if (getAngleDif(sentry.rotationYaw, yaw) <= allowedError)
		{
			if (getAngleDif(sentry.rotationPitch, pitch) <= allowedError)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * checks to see if the sentry is looking the the entity
	 * 
	 * @param entity - entity be used for the location
	 * @param allowedError - amount these sentry can be off in degrees from target
	 * @return true if its with in error range
	 */
	public boolean isLookingAt(Entity entity, float allowedError)
	{
		return this.isLookingAt(new Vector3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), allowedError);
	}

	/**
	 * Gets the pitch angle between the two points
	 */
	public static float getPitch(Vector3 position, Vector3 target)
	{
		Vector3 difference = Vector3.subtract(target, position);
		double verticleDistance = MathHelper.sqrt_double(difference.x * difference.x + difference.z * difference.z);
		return MathHelper.wrapAngleTo180_float((float) Math.abs((Math.atan2(difference.y, verticleDistance) * 180.0D / Math.PI)));

	}

	/**
	 * Gets the rotation yaw between the two points in angles
	 */
	public static float getYaw(Vector3 position, Vector3 target)
	{
		Vector3 difference = Vector3.subtract(target, position);
		return MathHelper.wrapAngleTo180_float((float) (Math.atan2(difference.z, difference.x) * 180.0D / Math.PI) - 90.0F);
	}

	/**
	 * Gets the delta look position based on the rotation yaw and pitch.
	 * 
	 * @param rotationYaw
	 * @param rotationPitch
	 */
	public static Vector3 getDeltaPositionFromRotation(float rotationYaw, float rotationPitch)
	{
		float z = MathHelper.cos(-rotationYaw * 0.017453292F - (float) Math.PI);
		float x = MathHelper.sin(-rotationYaw * 0.017453292F - (float) Math.PI);
		float var4 = -MathHelper.cos(-rotationPitch * 0.017453292F);
		float y = MathHelper.sin(-rotationPitch * 0.017453292F);
		return new Vector3(x * var4, y, z * var4);
	}

	/**
	 * gets the difference in degrees between the two angles
	 */
	public static float getAngleDif(float angleOne, float angleTwo)
	{

		double dif = (Math.max(angleOne, angleTwo) - Math.min(angleOne, angleTwo));
		return MathHelper.wrapAngleTo180_float((float) dif);

	}

}
