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

package zeldaswordskills.network.server;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import zeldaswordskills.network.AbstractMessage.AbstractServerMessage;
import zeldaswordskills.ref.ZeldaSong;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * Sent to the server after a song has successfully completed playing to
 * perform the actual effects of the song.
 *
 */
public class ZeldaSongPacket extends AbstractServerMessage
{
	private ZeldaSong song;

	public ZeldaSongPacket() {}

	public ZeldaSongPacket(ZeldaSong song) {
		this.song = song;
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		this.song = ZeldaSong.values()[buffer.readInt() % ZeldaSong.values().length];
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeInt(song.ordinal());
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		song.performSongEffects(player);
	}
}
