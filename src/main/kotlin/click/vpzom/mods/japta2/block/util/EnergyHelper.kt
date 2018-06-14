package click.vpzom.mods.japta2.block.util

import click.vpzom.mods.japta2.block.util.TileEntityJPTBase
import click.vpzom.mods.japta2.item.util.ItemJPT
import net.darkhax.tesla.api.ITeslaConsumer
import net.darkhax.tesla.api.ITeslaHolder
import net.darkhax.tesla.api.ITeslaProducer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.IEnergyStorage

object EnergyHelper {
	@CapabilityInject(IEnergyStorage::class)
	lateinit var CAPABILITY_FORGE_ENERGY_STORAGE: Capability<IEnergyStorage>

	@CapabilityInject(ITeslaConsumer::class)
	var CAPABILITY_TESLA_CONSUMER: Capability<ITeslaConsumer>? = null
	@CapabilityInject(ITeslaHolder::class)
	var CAPABILITY_TESLA_HOLDER: Capability<ITeslaHolder>? = null
	@CapabilityInject(ITeslaProducer::class)
	var CAPABILITY_TESLA_PRODUCER: Capability<ITeslaProducer>? = null

	fun getStoredEnergy(world: World, pos: BlockPos, side: EnumFacing?): Pair<Long, Long>? {
		val te = world.getTileEntity(pos)
		if(te is TileEntityJPTBase) {
			return Pair(te.getStoredEnergy(side), te.getMaxStoredEnergy(side))
		}
		if(te != null) {
			val capTesla = getCapability(te, CAPABILITY_TESLA_HOLDER, side)
			if(capTesla != null) {
				return Pair(capTesla.getStoredPower(), capTesla.getCapacity())
			}

			val capFE = getCapability(te, CAPABILITY_FORGE_ENERGY_STORAGE, side)
			if(capFE != null) {
				return Pair(capFE.getEnergyStored().toLong(), capFE.getMaxEnergyStored().toLong())
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
			val capTesla = getCapability(te, CAPABILITY_TESLA_PRODUCER, side)
			if(capTesla != null) {
				return capTesla.takePower(maxExtract, simulate)
			}

			val capFE = getCapability(te, CAPABILITY_FORGE_ENERGY_STORAGE, side)
			if(capFE != null) {
				return capFE.extractEnergy(Math.min(maxExtract, Int.MAX_VALUE.toLong()).toInt(), simulate).toLong()
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
			val capTesla = getCapability(te, CAPABILITY_TESLA_CONSUMER, side)
			if(capTesla != null) {
				return capTesla.givePower(maxInsert, simulate)
			}

			val capFE = getCapability(te, CAPABILITY_FORGE_ENERGY_STORAGE, side)
			if(capFE != null) {
				return capFE.receiveEnergy(Math.min(maxInsert, Int.MAX_VALUE.toLong()).toInt(), simulate).toLong()
			}
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

		val capTC = getCapability(stack, CAPABILITY_TESLA_CONSUMER, null)
		if(capTC != null) {
			return capTC.givePower(max, false)
		}

		val capFE = getCapability(stack, CAPABILITY_FORGE_ENERGY_STORAGE, null)
		if(capFE != null) {
			return capFE.receiveEnergy(longToInt(max), false).toLong()
		}

		return 0
	}

	fun <T> getCapability(holder: ICapabilityProvider, cap: Capability<T>?, side: EnumFacing?): T? {
		if(cap == null) return null

		if(!holder.hasCapability(cap, side)) return null

		return holder.getCapability(cap, side)
	}
}
