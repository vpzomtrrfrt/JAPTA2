package click.vpzom.mods.japta2.block.util

import click.vpzom.mods.japta2.block.util.TileEntityJPTBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object EnergyHelper {
	fun getStoredEnergy(world: World, pos: BlockPos, side: EnumFacing?): Pair<Long, Long>? {
		val te = world.getTileEntity(pos)
		if(te is TileEntityJPTBase) {
			return Pair(te.getStoredEnergy(side), te.getMaxStoredEnergy(side))
		}
		return null
	}
}
