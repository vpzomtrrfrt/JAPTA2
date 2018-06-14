package click.vpzom.mods.japta2.block.util

import net.darkhax.tesla.api.ITeslaConsumer
import net.darkhax.tesla.api.ITeslaHolder
import net.darkhax.tesla.api.ITeslaProducer
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

	inner class TeslaAdapter(val side: EnumFacing?): ITeslaConsumer, ITeslaHolder, ITeslaProducer {
		override fun givePower(max: Long, simulate: Boolean): Long {
			return attemptInputEnergy(side, max, simulate)
		}
		
		override fun getStoredPower(): Long {
			return getStoredEnergy(side)
		}
		override fun getCapacity(): Long {
			return getMaxStoredEnergy(side)
		}

		override fun takePower(max: Long, simulate: Boolean): Long {
			return attemptExtractEnergy(side, max, simulate)
		}
	}

	abstract fun getStoredEnergy(side: EnumFacing?): Long
	abstract fun getMaxStoredEnergy(side: EnumFacing?): Long
	abstract fun attemptInputEnergy(side: EnumFacing?, maxInput: Long, simulate: Boolean): Long
	abstract fun attemptExtractEnergy(side: EnumFacing?, maxExtract: Long, simulate: Boolean): Long

	override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
		if(capability == EnergyHelper.CAPABILITY_FORGE_ENERGY_STORAGE) return ForgeEnergyAdapter(side) as T
		if(capability == EnergyHelper.CAPABILITY_TESLA_HOLDER
		|| capability == EnergyHelper.CAPABILITY_TESLA_PRODUCER
		|| capability == EnergyHelper.CAPABILITY_TESLA_CONSUMER) {
			return TeslaAdapter(side) as T
		}
		return super.getCapability(capability, side)
	}

	override fun hasCapability(capability: Capability<*>, side: EnumFacing?): Boolean {
		if(capability == EnergyHelper.CAPABILITY_FORGE_ENERGY_STORAGE) return true
		if(capability == EnergyHelper.CAPABILITY_TESLA_HOLDER
		|| capability == EnergyHelper.CAPABILITY_TESLA_PRODUCER
		|| capability == EnergyHelper.CAPABILITY_TESLA_CONSUMER) {
			return true
		}
		return super.hasCapability(capability, side)
	}
}
