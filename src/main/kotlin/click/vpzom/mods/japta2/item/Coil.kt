package click.vpzom.mods.japta2.item

import click.vpzom.mods.japta2.JAPTA2
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

class ItemCoil(val name: String): Item(Item.Settings().itemGroup(ItemGroup.MISC)) {
	companion object {
		val reception = ItemCoil("coilreception")
		val transmission = ItemCoil("coiltransmission")
	}
}
