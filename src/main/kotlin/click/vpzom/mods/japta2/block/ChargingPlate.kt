package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.AbstractPressurePlateBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateFactory
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.Tickable
import net.minecraft.util.math.BoundingBox
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.IWorld
import net.minecraft.world.World

val ACTIVE = BooleanProperty.create("active")

enum class ChargingPlateType(
		val plateName: String,
		val material: Material,
		val target: Class<out Entity>,
		val clickOnSound: SoundEvent,
		val clickOffSound: SoundEvent
) {
	ITEM(
			"chargingplate_item",
			Material.WOOD,
			ItemEntity::class.java,
			SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF
	),
	PLAYER(
			"chargingplate_player",
			Material.STONE,
			PlayerEntity::class.java,
			SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF
	)
}

public class BlockChargingPlate private constructor(val type: ChargingPlateType): AbstractPressurePlateBlock(Block.Settings.of(type.material).strength(1f, 1f)), BlockEntityProvider {
	companion object {
		val wooden = BlockChargingPlate(ChargingPlateType.ITEM)
		val normal = BlockChargingPlate(ChargingPlateType.PLAYER)
	}

	val item = JAPTA2.basicBlockItem(this, ItemGroup.REDSTONE)

	fun getBB(pos: BlockPos): BoundingBox {
		return BOX.offset(pos)
	}

	override fun createBlockEntity(view: BlockView): BlockEntity = TileEntityChargingPlate()

	override fun appendProperties(builder: StateFactory.Builder<Block, BlockState>) {
		builder.with(ACTIVE)
	}

	override fun getRedstoneOutput(world: World, pos: BlockPos): Int {
		val nothing = world.getEntities(type.target, getBB(pos), EntityPredicates.VALID_ENTITY).isEmpty()
		if(nothing) return 0
		return 15
	}

	override fun getRedstoneOutput(state: BlockState): Int {
		if(state.get(ACTIVE)) {
			return 15
		}
		return 0
	}

	override fun setRedstoneOutput(state: BlockState, s: Int): BlockState {
		return state.with(ACTIVE, s > 0)
	}

	override fun playPressSound(world: IWorld, pos: BlockPos) {
		world.playSound(null, pos, type.clickOnSound, SoundCategory.BLOCK, .3f, .3f)
	}

	override fun playDepressSound(world: IWorld, pos: BlockPos) {
		world.playSound(null, pos, type.clickOffSound, SoundCategory.BLOCK, .3f, .3f)
	}
}

public class TileEntityChargingPlate: TileEntityJPT(type), Tickable {
	companion object {
		lateinit var type: BlockEntityType<TileEntityChargingPlate>
	}
	override fun getMaxStoredEnergy(): Long = 1000

	override fun tick() {
		if(stored <= 0) return

		val state = world.getBlockState(pos)
		val block = state.block as? BlockChargingPlate
		if(block == null) return
		if(!state.get(ACTIVE)) return

		val aabb = block.getBB(pos)

		when(block.type) {
			ChargingPlateType.ITEM -> {
				val list = world.getEntities(ItemEntity::class.java, aabb, EntityPredicates.VALID_ENTITY)
				for(item in list) {
					val stack = item.stack
					chargeItem(stack)
					if(stored <= 0) return

					item::class.java.getField("age").set(item, 0)
				}
			}
			ChargingPlateType.PLAYER -> {
				val list = world.getEntities(PlayerEntity::class.java, aabb, EntityPredicates.VALID_ENTITY)
				for(player in list) {
					val inv = player.inventory
					for(i in 0 until inv.getInvSize()) {
						val stack = inv.getInvStack(i)
						chargeItem(stack)
					}
				}
			}
		}
	}
}
