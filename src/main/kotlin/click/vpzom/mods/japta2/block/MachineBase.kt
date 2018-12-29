package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import net.minecraft.block.Block
import net.minecraft.block.Material

object BlockMachineBase: Block(Block.Settings.of(Material.METAL)) {
	val item = JAPTA2.basicBlockItem(this)
}
