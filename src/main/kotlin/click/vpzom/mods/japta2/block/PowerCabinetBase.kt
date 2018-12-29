package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.TileEntityJPTBase
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemGroup
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

const val MAX_BASE_ENERGY = 1999L
const val KEY_INTERNAL_ENERGY = "InternalEnergy"

object BlockPowerCabinetBase: BlockModelContainer(Block.Settings.of(Material.METAL).strength(3f, 3f)) {
	val item = JAPTA2.basicBlockItem(this, ItemGroup.REDSTONE)

	override fun createBlockEntity(view: BlockView): BlockEntity {
		return TileEntityPowerCabinetBase()
	}
}

class TileEntityPowerCabinetBase: TileEntityJPTBase(type) {
	companion object {
		lateinit var type: BlockEntityType<TileEntityPowerCabinetBase>
	}

	var internalStorage = 0L
	override fun getStoredEnergy(side: Direction?): Long {
		val world = getWorld()
		var total = internalStorage

		if(world == null) return total

		var curPos = getPos()
		while(true) {
			curPos = curPos.up()
			val state = world.getBlockState(curPos)
			if(state.getBlock() == BlockPowerCabinet) {
				val value = state.get(BlockPowerCabinet.PROP_VALUE)
				total += value * BlockPowerCabinet.LINE_VALUE
			}
			else {
				break
			}
		}
		return total
	}
	override fun getMaxStoredEnergy(side: Direction?): Long {
		val world = getWorld()
		var total = MAX_BASE_ENERGY

		if(world == null) return total

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
	override fun attemptExtractEnergy(side: Direction?, maxExtract: Long, simulate: Boolean): Long {
		val world = getWorld()
		if(world == null) return 0

		if(maxExtract <= internalStorage) {
			if(!simulate) internalStorage -= maxExtract
			return maxExtract
		}
		var extracted = 0L

		if(!simulate) extracted += internalStorage
		internalStorage = 0

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
			var value = state.get(BlockPowerCabinet.PROP_VALUE)
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
				world.setBlockState(curPos, state.with(BlockPowerCabinet.PROP_VALUE, value))
			}
			if(extracted >= maxExtract) break
		}

		return extracted
	}

	override fun attemptInputEnergy(side: Direction?, maxInput: Long, simulate: Boolean): Long {
		val world = getWorld()
		if(world == null) return 0

		var inserted = -internalStorage

		if(!simulate) internalStorage = 0

		val myPos = getPos()
		var curPos = myPos

		while(true) {
			curPos = curPos.up()
			val state = world.getBlockState(curPos)
			if(state.getBlock() != BlockPowerCabinet) break

			var value = state.get(BlockPowerCabinet.PROP_VALUE)
			if(value >= 15) continue
			while(value < 15) {
				if(inserted + BlockPowerCabinet.LINE_VALUE > maxInput) {
					if(!simulate) {
						val newInternal = maxInput - inserted
						internalStorage = newInternal
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
				world.setBlockState(curPos, state.with(BlockPowerCabinet.PROP_VALUE, value))
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
		return inserted
	}

	override fun toTag(_tag: CompoundTag): CompoundTag {
		val tag = super.toTag(_tag)
		tag.putLong(KEY_INTERNAL_ENERGY, internalStorage)
		return tag
	}

	override fun fromTag(tag: CompoundTag) {
		super.fromTag(tag)
		internalStorage = tag.getLong(KEY_INTERNAL_ENERGY)
	}
}
