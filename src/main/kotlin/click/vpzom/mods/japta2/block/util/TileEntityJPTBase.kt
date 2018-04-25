package click.vpzom.mods.japta2.block.util

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

abstract class TileEntityJPTBase: TileEntity() {
	abstract fun getStoredEnergy(side: EnumFacing?): Long
	abstract fun getMaxStoredEnergy(side: EnumFacing?): Long
	abstract fun attemptInputEnergy(side: EnumFacing?, maxInput: Long, simulate: Boolean): Long
	abstract fun attemptExtractEnergy(side: EnumFacing?, maxExtract: Long, simulate: Boolean): Long

	fun getStoredEnergyInt(side: EnumFacing?): Int {
		val storedLong = getStoredEnergy(side)
		if(storedLong > Int.MAX_VALUE) return Int.MAX_VALUE
		return storedLong as Int
	}
}
