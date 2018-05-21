package click.vpzom.mods.japta2.item.util

import click.vpzom.mods.japta2.block.util.EnergyHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.IEnergyStorage

abstract class ItemJPT: Item() {
	inner class CapabilityProvider(val stack: ItemStack): ICapabilityProvider {
		val ForgeEnergyAdapter = object: IEnergyStorage {
			override fun canExtract(): Boolean = true
			override fun canReceive(): Boolean = true
			override fun getMaxEnergyStored(): Int = EnergyHelper.longToInt(getMaxStoredEnergy(stack))
			override fun getEnergyStored(): Int = EnergyHelper.longToInt(getStoredEnergy(stack))
			override fun extractEnergy(max: Int, simulate: Boolean): Int = extractEnergy(stack, max.toLong(), simulate).toInt()
			override fun receiveEnergy(max: Int, simulate: Boolean): Int = receiveEnergy(stack, max.toLong(), simulate).toInt()
		}

		override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
			if(cap == EnergyHelper.CAPABILITY_FORGE_ENERGY_STORAGE) {
				return ForgeEnergyAdapter as T
			}
			return null
		}

		override fun hasCapability(cap: Capability<*>, facing: EnumFacing?): Boolean {
			return cap == EnergyHelper.CAPABILITY_FORGE_ENERGY_STORAGE
		}
	}

	fun getMaxStoredEnergy(stack: ItemStack): Long = stack.maxDamage.toLong()

	fun getStoredEnergy(stack: ItemStack): Long = (stack.maxDamage - stack.itemDamage).toLong()

	fun extractEnergy(stack: ItemStack, max: Long, simulate: Boolean): Long {
		val stored = getStoredEnergy(stack)
		val change = EnergyHelper.longToInt(if(max < stored) max else stored)

		if(!simulate) {
			stack.setItemDamage(stack.getItemDamage() + change)
		}
		return change.toLong()
	}

	fun receiveEnergy(stack: ItemStack, max: Long, simulate: Boolean): Long {
		val remaining = getMaxStoredEnergy(stack) - getStoredEnergy(stack)
		val change = EnergyHelper.longToInt(
				if(max < remaining) max else remaining
		)

		if(!simulate) {
			stack.setItemDamage(stack.getItemDamage() - change)
		}

		return change.toLong()
	}

	override fun initCapabilities(stack: ItemStack, tag: NBTTagCompound?): ICapabilityProvider {
		return CapabilityProvider(stack)
	}
}
