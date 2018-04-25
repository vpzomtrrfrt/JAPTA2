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
	override fun attemptExtractEnergy(side: EnumFacing?, maxExtract: Long, simulate: Boolean): Long {
		if(maxExtract <= internalStorage) {
			if(!simulate) internalStorage -= maxExtract
			return maxExtract
		}
		var extracted = 0L

		if(!simulate) extracted += internalStorage
		internalStorage = 0

		val world = getWorld()
		val myPos = getPos()
		var curPos = myPos
		while(true) {
			curPos = curPos.up()
			val state = world.getBlockState(curPos)
			if(state.getBlock() != BlockPowerCabinet) {
				break
			}
		}
		while(true) {
			curPos = curPos.down()
			if(curPos.y <= myPos.y) break

			val state = world.getBlockState(curPos)
			var value = state.getValue(BlockPowerCabinet.PROP_VALUE)
			if(value <= 0) continue
			while(value > 0) {
				value--
				if(extracted + BlockPowerCabinet.LINE_VALUE >= maxExtract) {
					if(!simulate) {
						internalStorage = extracted + BlockPowerCabinet.LINE_VALUE - maxExtract
					}
					extracted = maxExtract
					break
				}
				else {
					extracted += BlockPowerCabinet.LINE_VALUE
				}
			}
			if(!simulate) {
				world.setBlockState(curPos, state.withProperty(BlockPowerCabinet.PROP_VALUE, value))
			}
			if(extracted >= maxExtract) break
		}

		return extracted
	}

	override fun attemptInputEnergy(side: EnumFacing?, maxInput: Long, simulate: Boolean): Long {
		var inserted = -internalStorage

		if(!simulate) internalStorage = 0

		val world = getWorld()
		val myPos = getPos()
		var curPos = myPos

		while(true) {
			curPos = curPos.up()
			val state = world.getBlockState(curPos)
			if(state.getBlock() != BlockPowerCabinet) break

			var value = state.getValue(BlockPowerCabinet.PROP_VALUE)
			if(value >= 15) continue
			while(value < 15) {
				if(inserted + BlockPowerCabinet.LINE_VALUE >= maxInput) {
					if(!simulate) {
						internalStorage = inserted + BlockPowerCabinet.LINE_VALUE - maxInput
					}
					inserted = maxInput
					break
				}
				else {
					value++
					inserted += BlockPowerCabinet.LINE_VALUE
				}
			}
			if(!simulate) {
				world.setBlockState(curPos, state.withProperty(BlockPowerCabinet.PROP_VALUE, value))
			}
			if(inserted >= maxInput) break
		}
		if(inserted < maxInput && maxInput - inserted <= MAX_BASE_ENERGY) {
			if(!simulate) internalStorage = maxInput - inserted
			inserted = maxInput
		}
		if(inserted < 0) {
			if(!simulate) internalStorage += -inserted
			inserted = 0
		}
		println("inserting " + inserted + " / " + maxInput)
		return inserted
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
