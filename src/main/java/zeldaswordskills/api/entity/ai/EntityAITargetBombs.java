/**
    Copyright (C) <2016> <coolAlias>

    This file is part of coolAlias' Zelda Sword Skills Minecraft Mod; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package zeldaswordskills.api.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import zeldaswordskills.api.entity.IEntityBomb;
import zeldaswordskills.api.entity.IEntityCustomTarget;
import zeldaswordskills.api.entity.ai.EntityAIDynamicAction.EntityAIDynamicCustomTarget;
import zeldaswordskills.util.WorldUtils;

/**
 * 
 * AI that targets nearest bomb within the given range after a random delay
 *
 */
public class EntityAITargetBombs extends EntityAIDynamicCustomTarget
{
	/** Sorts potential targets by range */
	protected final Sorter sorter;
	protected final float range;
	protected Entity targetBomb;
	protected int delay;
	protected int timer;

	/**
	 * Passes true for require_sight; see {@link EntityAITargetBombs#EntityAITargetBombs(EntityCreature, EntityAction, float, boolean, boolean) EntityAITargetBombs}
	 */
	public <T extends EntityCreature & IEntityDynamic & IEntityCustomTarget> EntityAITargetBombs(T entity, EntityAction action, float range, boolean require_ground) {
		this(entity, action, range, require_ground, true);
	}

	/**
	 * Also see {@link EntityAIDynamicCustomTarget#EntityAIDynamicCustomTarget(EntityCreature, EntityAction, float, boolean, boolean) EntityAIDynamicCustomTarget}
	 * @param range Additionally used as the radius within which to search for bombs
	 */
	public <T extends EntityCreature & IEntityDynamic & IEntityCustomTarget> EntityAITargetBombs(T entity, EntityAction action, float range, boolean require_ground, boolean require_sight) {
		super(entity, action, range, require_ground, require_sight);
		this.sorter = new Sorter(entity);
		this.range = range;
	}

	@Override
	public boolean shouldExecute() {
		if (actor.getActionTime(action.id) > 0) {
			return true; // otherwise animation will cut short
		} else if (targetBomb != null && !targetBomb.isEntityAlive()) {
			this.softReset(); // target is no longer valid
		} else if (super.shouldExecute()) {
			if (delay == 0) {
				delay = 5 + entity.worldObj.rand.nextInt(10) + entity.worldObj.rand.nextInt(10);
				if (entity.getAttackTarget() != null) {
					double d = entity.getDistanceToEntity(entity.getAttackTarget());
					delay += MathHelper.ceiling_double_int(range - d);
				}
			}
			return timer++ > delay;
		}
		return false;
	}

	@Override
	public void startExecuting() {
		targeting.setCustomTarget(targetBomb);
		super.startExecuting();
	}

	@Override
	public void resetTask() {
		super.resetTask();
		this.softReset();
	}

	/**
	 * Soft reset of AI fields for when action not yet in progress (i.e. during delay period)
	 */
	protected void softReset() {
		targeting.setCustomTarget(null);
		targetBomb = null;
		delay = 0;
		timer = 0;
	}

	@Override
	protected Entity getTarget() {
		if (targetBomb == null && targeting.getCustomTarget() == null) {
			if (timer++ < entity.getRNG().nextInt(20)) {
				return null;
			}
			List<IEntityBomb> bombs = WorldUtils.getEntitiesWithinAABB(entity.worldObj, IEntityBomb.class, entity.getEntityBoundingBox().expand(range, range / 2.0F, range));
			Collections.sort(bombs, sorter); //fixed this compiler error by copying from the old Vanilla MC Sorter class
			if (!bombs.isEmpty()) {
				targetBomb = (Entity) bombs.get(0);
			}
			timer = 0;
		}
		return targetBomb;
	}
        
        public static class Sorter implements Comparator //copied from EntityAINearestAttackableTarget.Sorter class of MC 1.8.0
        {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn)
            {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_)
            {
                double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
                double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
                return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
            }
            
            public int compare(Object p_compare_1_, Object p_compare_2_)
            {
                return this.compare((Entity)p_compare_1_, (Entity)p_compare_2_);
            }
        }
}
