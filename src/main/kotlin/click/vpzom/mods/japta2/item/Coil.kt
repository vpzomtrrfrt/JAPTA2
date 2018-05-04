package click.vpzom.mods.japta2.item

import click.vpzom.mods.japta2.JAPTA2
import net.minecraft.item.Item

class ItemCoil(name: String): Item() {
	companion object {
		val reception = ItemCoil("coilreception")
		val transmission = ItemCoil("coiltransmission")
	}
	init {
		setUnlocalizedName(name)
		setRegistryName(name)
		setCreativeTab(JAPTA2.Tab)
	}
}
