package click.vpzom.mods.japta2.block.util

import click.vpzom.mods.japta2.block.util.TileEntityJPTBase
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.Direction

const val KEY_INTERNAL_ENERGY = "Energy"

abstract class TileEntityJPT(type: BlockEntityType<out TileEntityJPT>): TileEntityJPTBase(type) {
	abstract fun getMaxStoredEnergy(): Long

	protected var stored = 0L

	final override fun getStoredEnergy(side: Direction?): Long = stored

	final override fun getMaxStoredEnergy(side: Direction?): Long {
		return getMaxStoredEnergy()
	}

	override fun attemptInputEnergy(side: Direction?, maxInput: Long, simulate: Boolean): Long {
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

	override fun attemptExtractEnergy(side: Direction?, maxExtract: Long, simulate: Boolean): Long {
		if(stored <= 0) return 0
		if(maxExtract < stored) {
			if(!simulate) stored -= maxExtract
			return maxExtract
		}
		val change = stored
		if(!simulate) stored -= change
		return change
	}

	override fun toTag(_tag: CompoundTag): CompoundTag {
		val tag = super.toTag(_tag)
		tag.putLong(KEY_INTERNAL_ENERGY, stored)
		return tag
	}

	override fun fromTag(tag: CompoundTag) {
		super.fromTag(tag)
		stored = tag.getLong(KEY_INTERNAL_ENERGY)
	}

	protected fun pushEnergy(direction: Direction) {
		val inserted = EnergyHelper.insertEnergy(world, getPos().offset(direction), direction.opposite, stored)
		stored -= inserted
	}

	protected fun pushEnergy() {
		for(direction in Direction.values()) pushEnergy(direction)
	}

	protected fun chargeItem(stack: ItemStack): Long {
		val change = EnergyHelper.chargeItem(stack, stored)
		stored -= change
		return change
	}
}
