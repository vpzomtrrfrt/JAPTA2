package click.vpzom.mods.japta2.block.blaster

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.state.property.EnumProperty
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateFactory
import net.minecraft.util.math.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class BlockBlaster(settings: Block.Settings, val name: String, val inhale: Boolean, val split: Boolean): BlockModelContainer(settings) {
	companion object {
		val PROP_FACING = EnumProperty.create("facing", Direction::class.java)
		val RANGE = 8
		fun defaultSettings(): Block.Settings {
			return Block.Settings.of(Material.STONE).strength(3f, 3f)
		}
	}

	init {
		setDefaultState(stateFactory.getDefaultState().with(BlockBlaster.PROP_FACING, Direction.UP))
	}

	val item = JAPTA2.basicBlockItem(this, ItemGroup.REDSTONE)

	override fun appendProperties(builder: StateFactory.Builder<Block, BlockState>) {
		builder.with(PROP_FACING)
	}

	override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
		var facing = ctx.playerFacing
		if(ctx.player?.isSneaking() == true) {
			facing = facing.getOpposite()
		}

		return super.getDefaultState().with(BlockBlaster.PROP_FACING, facing)
	}
}
