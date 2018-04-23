package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World

val FACING = PropertyEnum.create("facing", EnumFacing::class.java)

object BlockFluxHopper: BlockContainer(Material.IRON) {
	init {
		setRegistryName("fluxhopper")
		setUnlocalizedName("fluxhopper")
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN))
		setHardness(3f)
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
}

class TileEntityFluxHopper: TileEntity() {

}
