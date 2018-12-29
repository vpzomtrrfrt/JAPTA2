package click.vpzom.mods.japta2.item.util

import click.vpzom.mods.japta2.block.util.EnergyHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction

abstract class ItemJPT(settings: Item.Settings): Item(settings) {
	fun getMaxStoredEnergy(stack: ItemStack): Long = stack.durability.toLong()

	fun getStoredEnergy(stack: ItemStack): Long = (stack.durability - stack.damage).toLong()

	fun extractEnergy(stack: ItemStack, max: Long, simulate: Boolean): Long {
		val stored = getStoredEnergy(stack)
		val change = EnergyHelper.longToInt(if(max < stored) max else stored)

		if(!simulate) {
			stack.setDamage(stack.getDamage() + change)
		}
		return change.toLong()
	}

	fun receiveEnergy(stack: ItemStack, max: Long, simulate: Boolean): Long {
		val remaining = getMaxStoredEnergy(stack) - getStoredEnergy(stack)
		val change = EnergyHelper.longToInt(
				if(max < remaining) max else remaining
		)

		if(!simulate) {
			stack.setDamage(stack.getDamage() - change)
		}

		return change.toLong()
	}
}
