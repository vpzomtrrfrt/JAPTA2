package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.TileEntityJPTBase
import net.minecraft.block.material.Material
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World

const val MAX_BASE_ENERGY = 1999L
const val KEY_INTERNAL_ENERGY = "InternalEnergy"

object BlockPowerCabinetBase: BlockModelContainer(Material.IRON) {
	init {
		setRegistryName("powercabinetbase")
		setUnlocalizedName("powercabinetbase")
		setHardness(3f)
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityPowerCabinetBase()
	}
}

class TileEntityPowerCabinetBase: TileEntityJPTBase() {
	var internalStorage = 0L
	override fun getStoredEnergy(side: EnumFacing?): Long {
		val world = getWorld()
		var total = internalStorage
		var curPos = getPos()
		while(true) {
			curPos = curPos.up()
			val state = world.getBlockState(curPos)
			if(state.getBlock() == BlockPowerCabinet) {
				val value = state.getValue(BlockPowerCabinet.PROP_VALUE)
				total += value * BlockPowerCabinet.LINE_VALUE
			}
			else {
				break
			}
		}
		return total
	}
	override fun getMaxStoredEnergy(side: EnumFacing?): Long {
		var total = MAX_BASE_ENERGY
		val world = getWorld()
		var curPos = getPos()
		while(true) {
			curPos = curPos.up()
			val state = world.getBlockState(curPos)
			if(state.getBlock() == BlockPowerCabinet) {
				total += BlockPowerCabinet.LINE_VALUE * 15
			}
			else {
				break
			}
		}
		return total
	}
	override fun attemptInputEnergy(side: EnumFacing?, maxInput: Long, simulate: Boolean): Long {
		var accepted = 0L
		val remaining = MAX_BASE_ENERGY - internalStorage
		if(maxInput >= remaining) {
			accepted += remaining
			if(!simulate) internalStorage = MAX_BASE_ENERGY
		}
		else {
			accepted += maxInput
			if(!simulate) internalStorage += maxInput
		}
		return accepted
	}
	override fun attemptExtractEnergy(side: EnumFacing?, maxExtract: Long, simulate: Boolean): Long {
		var extracted = 0L
		if(maxExtract >= internalStorage) {
			extracted += internalStorage
			if(!simulate) internalStorage = 0
		}
		else {
			extracted += maxExtract
			if(!simulate) internalStorage -= maxExtract
		}
		return extracted
	}

	override fun writeToNBT(_tag: NBTTagCompound): NBTTagCompound {
		val tag = super.writeToNBT(_tag)
		tag.setLong(KEY_INTERNAL_ENERGY, internalStorage)
		return tag
	}

	override fun readFromNBT(tag: NBTTagCompound) {
		super.readFromNBT(tag)
		internalStorage = tag.getLong(KEY_INTERNAL_ENERGY)
	}
}
