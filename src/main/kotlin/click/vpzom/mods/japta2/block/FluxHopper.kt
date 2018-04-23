package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World

object BlockFluxHopper: BlockContainer(Material.IRON) {
	final val FACING: PropertyEnum<EnumFacing> = PropertyEnum.create("facing", EnumFacing::class.java)

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
		println(FACING)
		println(PropertyEnum.create("facing2", EnumFacing::class.java))
		return BlockStateContainer(this, FACING)
	}
}

class TileEntityFluxHopper: TileEntity() {

}
