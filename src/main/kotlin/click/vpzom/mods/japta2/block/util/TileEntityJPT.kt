package click.vpzom.mods.japta2.block.util

import click.vpzom.mods.japta2.block.util.TileEntityJPTBase
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

const val KEY_INTERNAL_ENERGY = "Energy"

abstract class TileEntityJPT: TileEntityJPTBase() {
	abstract fun getMaxStoredEnergy(): Long

	protected var stored = 0L

	final override fun getStoredEnergy(side: EnumFacing?): Long {
		return stored
	}

	final override fun getMaxStoredEnergy(side: EnumFacing?): Long {
		return getMaxStoredEnergy()
	}

	override fun attemptInputEnergy(side: EnumFacing?, maxInput: Long, simulate: Boolean): Long {
		val max = getMaxStoredEnergy()
		if(stored >= max) return 0
		if(stored + maxInput >= max) {
			val change = max - stored
			if(!simulate) stored += change
			return change
		}
		if(!simulate) stored += maxInput
		return maxInput
	}

	override fun attemptExtractEnergy(side: EnumFacing?, maxExtract: Long, simulate: Boolean): Long {
		if(stored <= 0) return 0
		if(maxExtract < stored) {
			if(!simulate) stored -= maxExtract
			return maxExtract
		}
		val change = stored
		if(!simulate) stored -= change
		return change
	}

	override fun writeToNBT(_tag: NBTTagCompound): NBTTagCompound {
		val tag = super.writeToNBT(_tag)
		tag.setLong(KEY_INTERNAL_ENERGY, stored)
		return tag
	}

	override fun readFromNBT(tag: NBTTagCompound) {
		super.readFromNBT(tag)
		stored = tag.getLong(KEY_INTERNAL_ENERGY)
	}

	protected fun pushEnergy(direction: EnumFacing) {
		val inserted = EnergyHelper.insertEnergy(world, getPos().offset(direction), direction.opposite, stored)
		println("Pushed " + inserted + "/" + stored)
		stored -= inserted
	}

	protected fun pushEnergy() {
		for(direction in EnumFacing.VALUES) pushEnergy(direction)
	}

	protected fun chargeItem(stack: ItemStack): Long {
		val change = EnergyHelper.chargeItem(stack, stored)
		stored -= change
		return change
	}
}
