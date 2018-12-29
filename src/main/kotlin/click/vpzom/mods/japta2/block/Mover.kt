package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.state.StateFactory
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.math.Direction
import net.minecraft.util.Tickable
import net.minecraft.util.math.BoundingBox
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

val MOVER_FACING = EnumProperty.create("facing", Direction::class.java)

object BlockMover: BlockModelContainer(Block.Settings.of(Material.STONE).strength(1f, 1f)) {
	init {
		setDefaultState(stateFactory.getDefaultState().with(MOVER_FACING, Direction.UP))
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createBlockEntity(view: BlockView): BlockEntity = TileEntityMover()

	override fun appendProperties(builder: StateFactory.Builder<Block, BlockState>) {
		builder.with(MOVER_FACING)
	}

	override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
		var facing = ctx.playerFacing
		if(ctx.player?.isSneaking() == true) {
			facing = facing.getOpposite()
		}

		return super.getDefaultState().with(MOVER_FACING, facing)
	}
}

val USE = 50

class TileEntityMover: TileEntityJPT(Type), Tickable {
	companion object {
		public val Type = JAPTA2.registerBlockEntity("mover", BlockEntityType.Builder.create(::TileEntityMover))
	}

	override fun tick() {
		val pos = pos
		val myState = world.getBlockState(pos)
		if(myState.block != BlockMover) return

		val facing = myState.get(MOVER_FACING)

		if(stored >= USE) {
			val change = facing.vector
			val list = world.getEntities(Entity::class.java, BoundingBox(pos.x.toDouble(), pos.y + 1.0, pos.z.toDouble(), pos.x + 1.0, pos.y + 1.5, pos.z + 1.0), EntityPredicates.VALID_ENTITY)

			entLoop@ for(ent in list) {
				when(ent) {
					is LivingEntity -> {
						if(ent.isSneaking()) continue@entLoop
					}
					else -> {
					}
				}
				ent.setPosition(ent.x + change.x * 0.5, ent.y + change.y * 0.5, ent.z + change.z * 0.5) // TODO see if this works?
				stored -= USE
				if(stored < USE) break@entLoop
			}
		}

		if(stored > 0) {
			pushEnergy(facing)
		}
	}

	override fun getMaxStoredEnergy(): Long = USE * 2L
}
