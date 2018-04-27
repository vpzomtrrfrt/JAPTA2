package click.vpzom.mods.japta2.block.blaster

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class BlockBlaster(name: String, val inhale: Boolean, val split: Boolean): BlockModelContainer(Material.ROCK) {
	companion object {
		val PROP_FACING = PropertyEnum.create("facing", EnumFacing::class.java)
		val RANGE = 8
	}

	init {
		setRegistryName(name)
		setUnlocalizedName(name)
		setHardness(3f)
		setDefaultState(blockState.getBaseState().withProperty(BlockBlaster.PROP_FACING, EnumFacing.UP))
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createBlockState(): BlockStateContainer {
		return BlockStateContainer(this, BlockBlaster.PROP_FACING)
	}

	override fun getMetaFromState(state: IBlockState): Int {
		return state.getValue(BlockBlaster.PROP_FACING).getIndex()
	}

	override fun getStateFromMeta(meta: Int): IBlockState {
		return getDefaultState().withProperty(BlockBlaster.PROP_FACING, EnumFacing.getFront(meta))
	}

	override fun getStateForPlacement(world: World, pos: BlockPos, side: EnumFacing, f1: Float, f2: Float, f3: Float, i1: Int, placer: EntityLivingBase): IBlockState {
		var facing = EnumFacing.getDirectionFromEntityLiving(pos, placer)
		if(!placer.isSneaking()) {
			facing = facing.getOpposite()
		}

		return super.getStateForPlacement(world, pos, side, f1, f2, f3, i1, placer).withProperty(BlockBlaster.PROP_FACING, facing)
	}
}
