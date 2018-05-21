package click.vpzom.mods.japta2.block.util

import click.vpzom.mods.japta2.block.util.TileEntityJPTBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.energy.IEnergyStorage

object EnergyHelper {
	@CapabilityInject(IEnergyStorage::class)
	lateinit var CAPABILITY_FORGE_ENERGY_STORAGE: Capability<IEnergyStorage>

	fun getStoredEnergy(world: World, pos: BlockPos, side: EnumFacing?): Pair<Long, Long>? {
		val te = world.getTileEntity(pos)
		if(te is TileEntityJPTBase) {
			return Pair(te.getStoredEnergy(side), te.getMaxStoredEnergy(side))
		}
		if(te != null) {
			if(te.hasCapability(CAPABILITY_FORGE_ENERGY_STORAGE, side)) {
				val cap = te.getCapability(CAPABILITY_FORGE_ENERGY_STORAGE, side)
				if(cap != null) {
					return Pair(cap.getEnergyStored().toLong(), cap.getMaxEnergyStored().toLong())
				}
			}
		}
		return null
	}

	fun extractEnergy(world: World, pos: BlockPos, side: EnumFacing?, maxExtract: Long, simulate: Boolean = false): Long {
		val te = world.getTileEntity(pos)
		if(te is TileEntityJPTBase) {
			return te.attemptExtractEnergy(side, maxExtract, simulate)
		}
		if(te != null) {
			if(te.hasCapability(CAPABILITY_FORGE_ENERGY_STORAGE, side)) {
				val cap = te.getCapability(CAPABILITY_FORGE_ENERGY_STORAGE, side)
				if(cap != null) {
					return cap.extractEnergy(Math.min(maxExtract, Int.MAX_VALUE.toLong()).toInt(), simulate).toLong()
				}
			}
		}
		return 0
	}

	fun insertEnergy(world: World, pos: BlockPos, side: EnumFacing?, maxInsert: Long, simulate: Boolean = false): Long {
		val te = world.getTileEntity(pos)
		if(te is TileEntityJPTBase) {
			return te.attemptInputEnergy(side, maxInsert, simulate)
		}
		if(te != null) {
			if(te.hasCapability(CAPABILITY_FORGE_ENERGY_STORAGE, side)) {
				val cap = te.getCapability(CAPABILITY_FORGE_ENERGY_STORAGE, side)
				if(cap != null) {
					return cap.receiveEnergy(Math.min(maxInsert, Int.MAX_VALUE.toLong()).toInt(), simulate).toLong()
				}
			}
		}
		return 0
	}

	fun longToInt(l: Long): Int {
		if(l > Int.MAX_VALUE) return Int.MAX_VALUE
		return l.toInt()
	}
}
