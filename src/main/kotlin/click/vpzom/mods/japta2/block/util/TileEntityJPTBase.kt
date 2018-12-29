package click.vpzom.mods.japta2.block.util

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.Direction

abstract class TileEntityJPTBase(type: BlockEntityType<out TileEntityJPTBase>): BlockEntity(type) {
	abstract fun getStoredEnergy(side: Direction?): Long
	abstract fun getMaxStoredEnergy(side: Direction?): Long
	abstract fun attemptInputEnergy(side: Direction?, maxInput: Long, simulate: Boolean): Long
	abstract fun attemptExtractEnergy(side: Direction?, maxExtract: Long, simulate: Boolean): Long
}
