package click.vpzom.mods.japta2.block.util

import click.vpzom.mods.japta2.block.util.TileEntityJPTBase
import click.vpzom.mods.japta2.item.util.ItemJPT
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
object EnergyHelper {
	fun getStoredEnergy(world: World, pos: BlockPos, side: Direction?): Pair<Long, Long>? {
		val te = world.getBlockEntity(pos)
		if(te is TileEntityJPTBase) {
			return Pair(te.getStoredEnergy(side), te.getMaxStoredEnergy(side))
		}
		return null
	}

	fun extractEnergy(world: World, pos: BlockPos, side: Direction?, maxExtract: Long, simulate: Boolean = false): Long {
		val te = world.getBlockEntity(pos)
		if(te is TileEntityJPTBase) {
			return te.attemptExtractEnergy(side, maxExtract, simulate)
		}
		return 0
	}

	fun insertEnergy(world: World, pos: BlockPos, side: Direction?, maxInsert: Long, simulate: Boolean = false): Long {
		val te = world.getBlockEntity(pos)
		if(te is TileEntityJPTBase) {
			return te.attemptInputEnergy(side, maxInsert, simulate)
		}
		return 0
	}

	fun longToInt(l: Long): Int {
		if(l > Int.MAX_VALUE) return Int.MAX_VALUE
		return l.toInt()
	}

	fun chargeItem(stack: ItemStack?, max: Long): Long {
		if(max <= 0) return 0
		if(stack == null) return 0

		val item = stack.item

		if(item is ItemJPT) {
			return item.receiveEnergy(stack, max, false)
		}

		return 0
	}
}
