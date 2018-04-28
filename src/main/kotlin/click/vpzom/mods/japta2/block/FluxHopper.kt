package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

val FACING = PropertyEnum.create("facing", EnumFacing::class.java) {
	state -> state != EnumFacing.UP
}

object BlockFluxHopper: BlockModelContainer(Material.IRON) {
	init {
		setRegistryName("fluxhopper")
		setUnlocalizedName("fluxhopper")
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN))
		setHardness(3f)
		setCreativeTab(JAPTA2.Tab)
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityFluxHopper()
	}

	override fun createBlockState(): BlockStateContainer {
		return BlockStateContainer(this, FACING)
	}

	override fun getMetaFromState(state: IBlockState): Int {
		return state.getValue(FACING).getIndex()
	}

	override fun getStateFromMeta(meta: Int): IBlockState {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta))
	}

	override fun isOpaqueCube(state: IBlockState): Boolean {
		return false
	}

	override fun isFullCube(state: IBlockState): Boolean {
		return false
	}

	override fun getStateForPlacement(world: World, pos: BlockPos, side: EnumFacing, f1: Float, f2: Float, f3: Float, i1: Int, placer: EntityLivingBase): IBlockState {
		var facing = side.getOpposite()
		if(facing == EnumFacing.UP) {
			facing = EnumFacing.DOWN
		}

		return super.getStateForPlacement(world, pos, side, f1, f2, f3, i1, placer).withProperty(FACING, facing)
	}
}

class TileEntityFluxHopper: TileEntityJPT(), ITickable {
	override fun getMaxStoredEnergy(): Long {
		return 6000
	}

	override fun update() {
		val world = getWorld()
		if(world.isRemote) return

		val remaining = getMaxStoredEnergy() - stored

		if(remaining > 0) {
			val extracted = EnergyHelper.extractEnergy(world, getPos().up(), EnumFacing.DOWN, remaining)
			stored += extracted
		}

		if(stored > 0) {
			val state = world.getBlockState(getPos())
			if(state.getBlock() == BlockFluxHopper) {
				pushEnergy(state.getValue(FACING))
			}
		}
	}
}
