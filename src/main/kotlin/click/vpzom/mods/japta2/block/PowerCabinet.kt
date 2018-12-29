package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderLayer
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.state.property.IntegerProperty
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.block.BlockItem
import net.minecraft.state.StateFactory
import net.minecraft.util.DefaultedList
import net.minecraft.world.World

val VALUE = IntegerProperty.create("value", 0, 15)

object BlockPowerCabinet: Block(Block.Settings.of(Material.METAL).strength(2f, 2f)) {
	val LINE_VALUE = 2000
	val PROP_VALUE = VALUE

	object ItemPowerCabinet: BlockItem(BlockPowerCabinet, Item.Settings()) {
	}

	override fun appendProperties(builder: StateFactory.Builder<Block, BlockState>) {
		builder.with(VALUE)
	}

	// TODO fix drops

	override fun addStacksForDisplay(tab: ItemGroup, list: DefaultedList<ItemStack>) {
		val stackEmpty = ItemStack(this, 1)
		val stackFull = ItemStack(this, 1)
		stackFull.setDamage(15)

		list.add(stackEmpty)
		list.add(stackFull)
	}

	override fun getRenderLayer(): BlockRenderLayer = BlockRenderLayer.CUTOUT
}
