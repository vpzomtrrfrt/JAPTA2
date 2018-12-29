package click.vpzom.mods.japta2.block.util

import net.minecraft.block.Block
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState

abstract class BlockModelContainer(settings: Block.Settings): BlockWithEntity(settings) {
	override fun getRenderType(state: BlockState): BlockRenderType {
		return BlockRenderType.MODEL;
	}
}
