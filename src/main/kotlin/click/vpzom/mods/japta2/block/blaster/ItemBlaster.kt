package click.vpzom.mods.japta2.block.blaster

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.BlockElevatorShaft
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.wrapper.InvWrapper

class BlockItemBlaster private constructor(name: String, inhale: Boolean, split: Boolean): BlockBlaster(name, inhale, split) {
	companion object {
		val normal = BlockItemBlaster("itemblaster", false, false)
	}

	init {
		setCreativeTab(JAPTA2.Tab)
	}

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityItemBlaster()
	}

	override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, f1: Float, f2: Float, f3: Float): Boolean {
		player.displayGUIChest(world.getTileEntity(pos) as IInventory)
		return true
	}

	override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
		InventoryHelper.dropInventoryItems(world, pos, world.getTileEntity(pos) as IInventory)
		super.breakBlock(world, pos, state)
	}
}

val SIZE = 9

class TileEntityItemBlaster: TileEntity(), ITickable, IInventory {
	companion object {
		@CapabilityInject(IItemHandler::class)
		lateinit var CAPABILITY_ITEM_HANDLER: Capability<IItemHandler>
	}

	private val inv = Array(SIZE, { ItemStack.EMPTY })
	private var index = 1

	override fun update() {
		val world = getWorld()
		val myPos = getPos()
		val myState = world.getBlockState(myPos)
		val myBlock = myState.block as? BlockItemBlaster
		if(myBlock == null) return

		val facing = myState.getValue(BlockBlaster.PROP_FACING)
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

			val te = world.getTileEntity(curPos)
			if(te != null) {
				if(te.hasCapability(CAPABILITY_ITEM_HANDLER, side)) {
					val cap = te.getCapability(CAPABILITY_ITEM_HANDLER, side)
					if(cap != null) {
						val returned = ItemHandlerHelper.insertItem(cap, firstStack.splitStack(1), false)
						if(returned.isEmpty()) {
							break
						}
						else {
							firstStack.grow(1)
						}
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
					return removeStackFromSlot(i)
				}
				return stack
			}
		}
		return null
	}

	override fun writeToNBT(_tag: NBTTagCompound): NBTTagCompound {
		val tag = super.writeToNBT(_tag)
		val list = NBTTagList()
		for(i in 0 until SIZE) {
			val stack = inv[i]
			if(!stack.isEmpty()) {
				val stackTag = NBTTagCompound()
				stack.writeToNBT(stackTag)
				stackTag.setByte("Slot", i.toByte())
				list.appendTag(stackTag)
			}
		}
		tag.setTag("Items", list)
		return tag
	}

	override fun readFromNBT(tag: NBTTagCompound) {
		super.readFromNBT(tag)

		val list = tag.getTag("Items") as? NBTTagList
		if(list != null) {
			for(i in 0 until list.tagCount()) {
				val stackTag = list.getCompoundTagAt(i)
				val slot = stackTag.getByte("Slot")
				inv[slot.toInt()] = ItemStack(stackTag)
			}
		}
	}

	override fun getField(i: Int): Int {
		return 0
	}

	override fun hasCustomName(): Boolean {
		return false
	}

	override fun getStackInSlot(slot: Int): ItemStack {
		return inv[slot]
	}

	override fun decrStackSize(slot: Int, count: Int): ItemStack {
		val stack = inv[slot]
		if(stack.count <= count) {
			inv[slot] = ItemStack.EMPTY
			return stack
		}
		else {
			return stack.splitStack(count)
		}
	}

	override fun clear() {
		for(i in 0 until SIZE) {
			inv[i] = ItemStack.EMPTY
		}
	}

	override fun getSizeInventory(): Int = SIZE

	override fun getName(): String {
		return getWorld().getBlockState(getPos()).getBlock().getUnlocalizedName() + ".name"
	}

	override fun isEmpty(): Boolean {
		for(stack in inv) {
			if(!stack.isEmpty()) return false
		}
		return true
	}

	override fun isItemValidForSlot(slot: Int, stack: ItemStack): Boolean {
		return true
	}

	override fun getInventoryStackLimit(): Int = 64

	override fun isUsableByPlayer(player: EntityPlayer): Boolean {
		return true
	}

	override fun openInventory(player: EntityPlayer) {}

	override fun closeInventory(player: EntityPlayer) {}

	override fun setField(i: Int, v: Int) {}

	override fun setInventorySlotContents(slot: Int, stack: ItemStack?) {
		inv[slot] = stack ?: ItemStack.EMPTY
	}

	override fun removeStackFromSlot(slot: Int): ItemStack {
		val tr = inv[slot]
		inv[slot] = ItemStack.EMPTY
		return tr
	}

	override fun getFieldCount(): Int = 0

	override fun getDisplayName(): ITextComponent {
		return TextComponentTranslation(getName())
	}

	private val itemHandler = InvWrapper(this)

	override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
		if(capability == CAPABILITY_ITEM_HANDLER) return itemHandler as T
		return super.getCapability(capability, facing)
	}

	override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
		if(capability == CAPABILITY_ITEM_HANDLER) return true
		return super.hasCapability(capability, facing)
	}
}
