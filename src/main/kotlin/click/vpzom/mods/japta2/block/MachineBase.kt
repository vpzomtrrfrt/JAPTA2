package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import net.minecraft.block.Block
import net.minecraft.block.material.Material

object BlockMachineBase: Block(Material.IRON) {
	init {
		setRegistryName("machinebase")
		setUnlocalizedName("machinebase")
		setCreativeTab(JAPTA2.Tab)
	}

	val item = JAPTA2.basicBlockItem(this)
}
