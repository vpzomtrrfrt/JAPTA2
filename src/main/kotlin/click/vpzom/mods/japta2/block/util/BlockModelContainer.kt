package click.vpzom.mods.japta2.block.util

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumBlockRenderType

abstract class BlockModelContainer(material: Material): BlockContainer(material) {
	override fun getRenderType(state: IBlockState): EnumBlockRenderType {
		return EnumBlockRenderType.MODEL;
	}
}
