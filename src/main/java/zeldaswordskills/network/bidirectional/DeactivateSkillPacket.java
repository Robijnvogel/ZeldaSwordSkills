/**
    Copyright (C) <2014> <coolAlias>

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

package zeldaswordskills.network.bidirectional;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import zeldaswordskills.entity.ZSSPlayerSkills;
import zeldaswordskills.network.AbstractMessage;
import zeldaswordskills.skills.SkillActive;
import zeldaswordskills.skills.SkillBase;
import zeldaswordskills.util.LogHelper;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * Send to either side to {@link SkillActive#deactivate deactivate} a skill.
 *
 */
public class DeactivateSkillPacket extends AbstractMessage
{
	/** Skill to deactivate */
	private byte skillId;

	public DeactivateSkillPacket() {}

	public DeactivateSkillPacket(SkillActive skill) {
		this.skillId = skill.getId();
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		skillId = buffer.readByte();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeByte(skillId);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		SkillBase skill = ZSSPlayerSkills.get(player).getPlayerSkill(skillId);
		if (skill instanceof SkillActive) {
			((SkillActive) skill).deactivate(player);
		} else {
			LogHelper.warning("Error processing DeactivateSkillPacket for " + player + "; skill with ID " + skillId + " was not valid for this player.");
		}
	}
}
