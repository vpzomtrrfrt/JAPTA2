package click.vpzom.mods.japta2.block.blaster

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.BlockElevatorShaft
import click.vpzom.mods.japta2.block.util.InventoryHelper
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.BlockState
import net.minecraft.container.Container
import net.minecraft.container.Generic3x3Container
import net.minecraft.container.NameableContainerProvider
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.text.TextComponent
import net.minecraft.text.TranslatableTextComponent
import net.minecraft.util.BlockHitResult
import net.minecraft.util.Hand
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.IWorld
import net.minecraft.world.World

class BlockItemBlaster private constructor(name: String, inhale: Boolean, split: Boolean): BlockBlaster(BlockBlaster.defaultSettings(), name, inhale, split) {
	companion object {
		val normal = BlockItemBlaster("itemblaster", false, false)
	}

	init {
	}

	override fun createBlockEntity(view: BlockView): BlockEntity {
		return TileEntityItemBlaster()
	}

	override fun activate(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): Boolean {
		player.openContainer(world.getBlockEntity(pos) as NameableContainerProvider)
		return true
	}

	override fun onBroken(world: IWorld, pos: BlockPos, state: BlockState) {
		InventoryHelper.dropInventoryItems(world, pos, world.getBlockEntity(pos) as Inventory)
		super.onBroken(world, pos, state)
	}
}

val SIZE = 9

class TileEntityItemBlaster: BlockEntity(type), Tickable, Inventory, NameableContainerProvider {
	companion object {
		lateinit var type: BlockEntityType<TileEntityItemBlaster>
	}

	private val inv = Array(SIZE, { ItemStack.EMPTY })
	private var index = 1

	override fun tick() {
		val world = getWorld()
		if(world == null) return

		val myPos = getPos()
		val myState = world.getBlockState(myPos)
		val myBlock = myState.block as? BlockItemBlaster
		if(myBlock == null) return

		val facing = myState.get(BlockBlaster.PROP_FACING)
		val side = facing.opposite
		val range = BlockBlaster.RANGE

		if(myBlock.split) {
			index++
			if(index > range) index = 1
		}
		else {
			index = 1
		}

		val initialPos = index
		val firstStack = getFirstStack(false)
		if(firstStack == null) return
		do {
			var curPos = myPos.offset(facing, index)
			while(world.getBlockState(curPos).block == BlockElevatorShaft) {
				curPos = curPos.up()
			}

			val te = world.getBlockEntity(curPos)
			if(te != null) {
				val inv = te as? Inventory
				if(inv != null) {
					val returned = InventoryHelper.insertItem(inv, firstStack.split(1))
					if(returned.isEmpty()) {
						break
					}
					else {
						firstStack.addAmount(1)
					}
				}
			}

			index++
			if(index > range) index = 1
		} while(index != initialPos)
	}

	private fun getFirstStack(remove: Boolean): ItemStack? {
		for(i in 0 until SIZE) {
			val stack = inv[i]
			if(stack?.isEmpty() == false) {
				if(remove) {
					return removeInvStack(i)
				}
				return stack
			}
		}
		return null
	}

	override fun toTag(_tag: CompoundTag): CompoundTag {
		val tag = super.toTag(_tag)
		val list = ListTag()
		for(i in 0 until SIZE) {
			val stack = inv[i]
			if(!stack.isEmpty()) {
				val stackTag = CompoundTag()
				stack.toTag(stackTag)
				stackTag.putByte("Slot", i.toByte())
				list.add(stackTag)
			}
		}
		tag.put("Items", list)
		return tag
	}

	override fun fromTag(tag: CompoundTag) {
		super.fromTag(tag)

		val list = tag.getTag("Items") as? ListTag
		if(list != null) {
			for(i in 0 until list.size) {
				val stackTag = list.getCompoundTag(i)
				val slot = stackTag.getByte("Slot")
				inv[slot.toInt()] = ItemStack.fromTag(stackTag)
			}
		}
	}

	override fun getInvStack(slot: Int): ItemStack {
		return inv[slot]
	}

	override fun takeInvStack(slot: Int, count: Int): ItemStack {
		val stack = inv[slot]
		if(stack.amount <= count) {
			inv[slot] = ItemStack.EMPTY
			return stack
		}
		else {
			return stack.split(count)
		}
	}

	override fun clearInv() {
		for(i in 0 until SIZE) {
			inv[i] = ItemStack.EMPTY
		}
	}


	override fun getInvSize(): Int = SIZE

	override fun getDisplayName(): TextComponent {
		return TranslatableTextComponent(world?.getBlockState(pos)?.block?.translationKey + ".name")
	}

	override fun isInvEmpty(): Boolean {
		for(stack in inv) {
			if(!stack.isEmpty()) return false
		}
		return true
	}

	override fun setInvStack(slot: Int, stack: ItemStack?) {
		inv[slot] = stack ?: ItemStack.EMPTY
	}

	override fun removeInvStack(slot: Int): ItemStack {
		val tr = inv[slot]
		inv[slot] = ItemStack.EMPTY
		return tr
	}

	override fun canPlayerUseInv(player: PlayerEntity): Boolean {
		return player.squaredDistanceTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) < 64
	}

	override fun createMenu(syncId: Int, playerInv: PlayerInventory, player: PlayerEntity): Container? {
		return Generic3x3Container(syncId, playerInv, this)
	}
}
