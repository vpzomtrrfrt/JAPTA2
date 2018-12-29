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
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateFactory
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.math.Direction
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

val FACING = EnumProperty.create("facing", Direction::class.java) {
	state -> state != Direction.UP
}

object BlockFluxHopper: BlockModelContainer(Block.Settings.of(Material.METAL).strength(3f, 3f)) {
	init {
		setDefaultState(stateFactory.getDefaultState().with(FACING, Direction.DOWN))
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createBlockEntity(view: BlockView): BlockEntity = TileEntityFluxHopper()

	override fun appendProperties(builder: StateFactory.Builder<Block, BlockState>) {
		builder.with(FACING)
	}

	override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
		var facing = ctx.facing.getOpposite()
		if(facing == Direction.UP) {
			facing = Direction.DOWN
		}

		return super.getDefaultState().with(FACING, facing)
	}
}

class TileEntityFluxHopper: TileEntityJPT(Type), Tickable {
	companion object {
		val Type = JAPTA2.registerBlockEntity("fluxhopper", BlockEntityType.Builder.create(::TileEntityFluxHopper))
	}
	override fun getMaxStoredEnergy(): Long {
		return 6000
	}

	override fun tick() {
		val world = getWorld()
		if(world == null || world.isClient) return

		val remaining = getMaxStoredEnergy() - stored

		if(remaining > 0) {
			val extracted = EnergyHelper.extractEnergy(world, getPos().up(), Direction.DOWN, remaining)
			stored += extracted
		}

		if(stored > 0) {
			val state = world.getBlockState(getPos())
			if(state.getBlock() == BlockFluxHopper) {
				pushEnergy(state.get(FACING))
			}
		}
	}
}
