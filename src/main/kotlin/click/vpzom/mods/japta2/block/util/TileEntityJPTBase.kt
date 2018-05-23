package click.vpzom.mods.japta2.block.util

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.IEnergyStorage

abstract class TileEntityJPTBase: TileEntity() {
	inner class ForgeEnergyAdapter(val side: EnumFacing?): IEnergyStorage {
		override fun canExtract(): Boolean = true
		override fun canReceive(): Boolean = true
		override fun getMaxEnergyStored(): Int = EnergyHelper.longToInt(getMaxStoredEnergy(side))
		override fun getEnergyStored(): Int = EnergyHelper.longToInt(getStoredEnergy(side))
		override fun extractEnergy(max: Int, simulate: Boolean): Int {
			return attemptExtractEnergy(side, max.toLong(), simulate).toInt()
		}
		override fun receiveEnergy(max: Int, simulate: Boolean): Int {
			return attemptInputEnergy(side, max.toLong(), simulate).toInt()
		}
	}

	abstract fun getStoredEnergy(side: EnumFacing?): Long
	abstract fun getMaxStoredEnergy(side: EnumFacing?): Long
	abstract fun attemptInputEnergy(side: EnumFacing?, maxInput: Long, simulate: Boolean): Long
	abstract fun attemptExtractEnergy(side: EnumFacing?, maxExtract: Long, simulate: Boolean): Long

	override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
		if(capability == EnergyHelper.CAPABILITY_FORGE_ENERGY_STORAGE) return ForgeEnergyAdapter(side) as T
		return super.getCapability(capability, side)
	}

	override fun hasCapability(capability: Capability<*>, side: EnumFacing?): Boolean {
		if(capability == EnergyHelper.CAPABILITY_FORGE_ENERGY_STORAGE) return true
		return super.hasCapability(capability, side)
	}
}
