package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.FoodItem
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.TextComponent
import net.minecraft.text.TranslatableTextComponent
import net.minecraft.util.math.Direction
import net.minecraft.util.Tickable
import net.minecraft.util.math.BoundingBox
import net.minecraft.world.BlockView
import net.minecraft.world.World

object BlockEater: BlockModelContainer(Block.Settings.of(Material.STONE).strength(1f, 1f)) {
	val item = JAPTA2.basicBlockItem(this, ItemGroup.REDSTONE)

	override fun createBlockEntity(view: BlockView): BlockEntity = TileEntityEater()
}

val TAG_TOTAL_TIME = "TotalTime"
val TAG_ELAPSED_TIME = "ElapsedTime"
val TAG_MULTIPLIER = "Multiplier"

val TAG_STACK = "Stack"
val TAG_STATE = "State"

data class EaterState(val totalTime: Int, val multiplier: Float, var elapsedTime: Int) {
	constructor(tag: CompoundTag): this(tag.getInt(TAG_TOTAL_TIME), tag.getFloat(TAG_MULTIPLIER), tag.getInt(TAG_ELAPSED_TIME))

	fun toTag(tag: CompoundTag) {
		tag.putInt(TAG_TOTAL_TIME, totalTime)
		tag.putFloat(TAG_MULTIPLIER, multiplier)
		tag.putInt(TAG_ELAPSED_TIME, elapsedTime)
	}
}

val TIME_MULT = 32
val POWER_MULT = 10

class TileEntityEater: TileEntityJPT(type), Inventory, Tickable {
	companion object {
		lateinit var type: BlockEntityType<TileEntityEater>
	}
	private var stack = ItemStack.EMPTY
	private var state: EaterState? = null

	private fun getStateForItem(itemStack: ItemStack): EaterState? {
		val item = itemStack.item
		val food = item as? FoodItem
		if(food == null) return null
		val healAmount = food.getHungerRestored(itemStack)
		val saturation = food.getSaturationModifier(itemStack)
		return EaterState((saturation * TIME_MULT).toInt(), healAmount + saturation, 0)
	}

	private fun suckItem() {
		val targetPos = pos.up()
		val te = world.getBlockEntity(targetPos)
		if(te != null) {
			val side = Direction.DOWN
			val inv = te as? Inventory
			if(inv != null) {
				for(i in 0 until inv.getInvSize()) {
					val result = inv.takeInvStack(i, 1)
					if(result != null && !result.isEmpty()) {
						stack = result
						return
					}
				}
			}
		}

		val ents = world.getEntities(ItemEntity::class.java, BoundingBox(pos.x.toDouble(), pos.y + 0.5, pos.z.toDouble(), pos.x + 1.0, pos.y + 2.0, pos.z + 1.0), EntityPredicates.VALID_ENTITY)
		for(ent in ents) {
			val itemStack = ent.stack
			if(itemStack != null && !itemStack.isEmpty()) {
				val split = itemStack.split(1)
				if(itemStack.isEmpty()) ent.invalidate()
				stack = split
				return
			}
		}
	}

	override fun tick() {
		if(world == null || world.isClient) return

		val state = state

		if(state != null) {
			val toAdd = (POWER_MULT * state.multiplier).toInt()
			if(toAdd + stored <= getMaxStoredEnergy()) {
				stored += toAdd
				state.elapsedTime++
				if(state.elapsedTime % 2 == 0) {
					world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCK, 0.5f + Math.random().toFloat(), ((Math.random() - Math.random()).toFloat() * 0.2f + 1))
				}
			}

			if(state.elapsedTime >= state.totalTime) this.state = null
		}

		if(this.state == null && !stack.isEmpty()) {
			val subStack = stack.split(1) // stack should only contain 1, but just in case
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

	override fun getInvStack(slot: Int): ItemStack {
		if(slot == 0) return stack
		return ItemStack.EMPTY
	}
	override fun takeInvStack(slot: Int, count: Int): ItemStack {
		if(slot != 0) {
			return ItemStack.EMPTY
		}
		if(stack.amount <= count) {
			val tmp = stack
			stack = ItemStack.EMPTY
			return tmp
		}
		else {
			return stack.split(count)
		}
	}
	override fun clearInv() {
		stack = ItemStack.EMPTY
	}
	override fun getInvSize(): Int = 1
	override fun getName(): TextComponent = TranslatableTextComponent("tile.eater.name")
	override fun isInvEmpty(): Boolean = stack.isEmpty()
	override fun isValidInvStack(slot: Int, stack: ItemStack): Boolean = slot == 0
	override fun getInvMaxStackAmount(): Int = 1
	override fun canPlayerUseInv(player: PlayerEntity): Boolean = false
	override fun setInvStack(slot: Int, newStack: ItemStack?) {
		if(slot == 0) {
			stack = newStack ?: ItemStack.EMPTY
		}
	}
	override fun removeInvStack(slot: Int): ItemStack {
		if(slot != 0) return ItemStack.EMPTY
		val tmp = stack
		stack = ItemStack.EMPTY
		return tmp
	}

	override fun toTag(_tag: CompoundTag): CompoundTag {
		val tag = super.toTag(_tag)
		val stack = stack
		val state = state

		if(!stack.isEmpty()) {
			val stackTag = CompoundTag()
			stack.toTag(stackTag)
			tag.put(TAG_STACK, stackTag)
		}
		else {
			tag.remove(TAG_STACK)
		}

		if(state != null) {
			val stateTag = CompoundTag()
			state.toTag(stateTag)
			tag.put(TAG_STATE, stateTag)
		}
		else {
			tag.remove(TAG_STATE)
		}

		return tag
	}
	
	override fun fromTag(tag: CompoundTag) {
		super.fromTag(tag)

		val stateTag = tag.getTag(TAG_STATE) as? CompoundTag
		val stackTag = tag.getTag(TAG_STACK) as? CompoundTag

		state = stateTag?.let { EaterState(it) }
		stack = stackTag?.let { ItemStack.fromTag(it) } ?: ItemStack.EMPTY
	}
}
