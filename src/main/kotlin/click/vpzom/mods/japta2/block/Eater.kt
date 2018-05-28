package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.material.Material
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EntitySelectors
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper

object BlockEater: BlockModelContainer(Material.ROCK) {
	init {
		setRegistryName("eater")
		setUnlocalizedName("eater")
		setHardness(1f)
		setCreativeTab(JAPTA2.Tab)
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityEater()
	}
}

val TAG_TOTAL_TIME = "TotalTime"
val TAG_ELAPSED_TIME = "ElapsedTime"
val TAG_MULTIPLIER = "Multiplier"

val TAG_STACK = "Stack"
val TAG_STATE = "State"

data class EaterState(val totalTime: Int, val multiplier: Float, var elapsedTime: Int) {
	constructor(tag: NBTTagCompound): this(tag.getInteger(TAG_TOTAL_TIME), tag.getFloat(TAG_MULTIPLIER), tag.getInteger(TAG_ELAPSED_TIME))

	fun writeToNBT(tag: NBTTagCompound) {
		tag.setInteger(TAG_TOTAL_TIME, totalTime)
		tag.setFloat(TAG_MULTIPLIER, multiplier)
		tag.setInteger(TAG_ELAPSED_TIME, elapsedTime)
	}
}

val TIME_MULT = 32
val POWER_MULT = 10

class TileEntityEater: TileEntityJPT(), IInventory, ITickable {
	companion object {
		@CapabilityInject(IItemHandler::class)
		lateinit var CAPABILITY_ITEM_HANDLER: Capability<IItemHandler>
	}

	private var stack = ItemStack.EMPTY
	private var state: EaterState? = null

	private fun getStateForItem(itemStack: ItemStack): EaterState? {
		val item = itemStack.item
		val food = item as? ItemFood
		if(food == null) return null
		val healAmount = food.getHealAmount(itemStack)
		val saturation = food.getSaturationModifier(itemStack)
		return EaterState((saturation * TIME_MULT).toInt(), healAmount + saturation, 0)
	}

	private fun suckItem() {
		val targetPos = pos.up()
		val te = world.getTileEntity(targetPos)
		if(te != null) {
			val side = EnumFacing.DOWN
			if(te.hasCapability(CAPABILITY_ITEM_HANDLER, side)) {
				val cap = te.getCapability(CAPABILITY_ITEM_HANDLER, side)
				if(cap != null) {
					for(i in 0 until cap.getSlots()) {
						val result = cap.extractItem(i, 1, false)
						if(result != null && !result.isEmpty()) {
							stack = result
							return
						}
					}
				}
			}
		}

		val ents = world.getEntitiesWithinAABB(EntityItem::class.java, AxisAlignedBB(pos.x.toDouble(), pos.y + 0.5, pos.z.toDouble(), pos.x + 1.0, pos.y + 2.0, pos.z + 1.0), EntitySelectors.IS_ALIVE)
		for(ent in ents) {
			val itemStack = ent.item
			if(itemStack != null && !itemStack.isEmpty()) {
				val split = itemStack.splitStack(1)
				if(itemStack.isEmpty()) ent.setDead()
				stack = split
				return
			}
		}
	}

	override fun update() {
		if(world.isRemote) return

		val state = state

		if(state != null) {
			val toAdd = (POWER_MULT * state.multiplier).toInt()
			if(toAdd + stored <= getMaxStoredEnergy()) {
				stored += toAdd
				println("Adding " + toAdd)
				state.elapsedTime++
				if(state.elapsedTime % 2 == 0) {
					world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 0.5f + Math.random().toFloat(), ((Math.random() - Math.random()).toFloat() * 0.2f + 1))
				}
			}

			if(state.elapsedTime >= state.totalTime) this.state = null
		}

		if(this.state == null && !stack.isEmpty()) {
			val subStack = stack.splitStack(1) // stack should only contain 1, but just in case
			this.state = getStateForItem(subStack)
		}

		if(stored > 0) {
			pushEnergy()
		}

		if(stack.isEmpty()) {
			suckItem()
		}
	}

	override fun getMaxStoredEnergy(): Long = 2000

	override fun getField(i: Int): Int = 0
	override fun hasCustomName(): Boolean = false
	override fun getStackInSlot(slot: Int): ItemStack {
		if(slot == 0) return stack
		return ItemStack.EMPTY
	}
	override fun decrStackSize(slot: Int, count: Int): ItemStack {
		if(slot != 0) {
			return ItemStack.EMPTY
		}
		if(stack.count <= count) {
			val tmp = stack
			stack = ItemStack.EMPTY
			return tmp
		}
		else {
			return stack.splitStack(count)
		}
	}
	override fun clear() {
		stack = ItemStack.EMPTY
	}
	override fun getSizeInventory(): Int = 1
	override fun getName(): String = "tile.eater.name"
	override fun isEmpty(): Boolean = stack.isEmpty()
	override fun isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = slot == 0
	override fun getInventoryStackLimit(): Int = 1
	override fun isUsableByPlayer(player: EntityPlayer): Boolean = true
	override fun openInventory(player: EntityPlayer) {}
	override fun closeInventory(player: EntityPlayer) {}
	override fun setField(i: Int, v: Int) {}
	override fun setInventorySlotContents(slot: Int, newStack: ItemStack?) {
		if(slot == 0) {
			stack = newStack ?: ItemStack.EMPTY
		}
	}
	override fun removeStackFromSlot(slot: Int): ItemStack {
		if(slot != 0) return ItemStack.EMPTY
		val tmp = stack
		stack = ItemStack.EMPTY
		return tmp
	}
	override fun getFieldCount(): Int = 0
	override fun getDisplayName(): ITextComponent = TextComponentTranslation(getName())

	private val itemHandler = InvWrapper(this)

	override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
		if(capability == CAPABILITY_ITEM_HANDLER) return itemHandler as T
		return super.getCapability(capability, side)
	}

	override fun hasCapability(capability: Capability<*>, side: EnumFacing?): Boolean {
		if(capability == CAPABILITY_ITEM_HANDLER) return true
		return super.hasCapability(capability, side)
	}

	override fun writeToNBT(_tag: NBTTagCompound): NBTTagCompound {
		val tag = super.writeToNBT(_tag)
		val stack = stack
		val state = state

		if(!stack.isEmpty()) {
			val stackTag = NBTTagCompound()
			stack.writeToNBT(stackTag)
			tag.setTag(TAG_STACK, stackTag)
		}
		else {
			tag.removeTag(TAG_STACK)
		}

		if(state != null) {
			val stateTag = NBTTagCompound()
			state.writeToNBT(stateTag)
			tag.setTag(TAG_STATE, stateTag)
		}
		else {
			tag.removeTag(TAG_STATE)
		}

		return tag
	}
	
	override fun readFromNBT(tag: NBTTagCompound) {
		super.readFromNBT(tag)

		val stateTag = tag.getTag(TAG_STATE) as? NBTTagCompound
		val stackTag = tag.getTag(TAG_STACK) as? NBTTagCompound

		state = stateTag?.let { EaterState(it) }
		stack = stackTag?.let { ItemStack(it) } ?: ItemStack.EMPTY
	}
}
