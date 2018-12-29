package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.FurnaceHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import click.vpzom.mods.japta2.mixin.MixinFurnace
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.FurnaceBlockEntity
import net.minecraft.item.ItemGroup
import net.minecraft.util.math.Direction
import net.minecraft.util.Tickable
import net.minecraft.world.BlockView
import net.minecraft.world.World

object BlockFurnaceSiphon: BlockModelContainer(Block.Settings.of(Material.METAL).strength(2f, 2f)) {
	val item = JAPTA2.basicBlockItem(this, ItemGroup.REDSTONE)

	override fun createBlockEntity(view: BlockView): BlockEntity = TileEntityFurnaceSiphon()
}

class TileEntityFurnaceSiphon: TileEntityJPT(type), Tickable {
	companion object {
		lateinit var type: BlockEntityType<TileEntityFurnaceSiphon>
		val TICK_VALUE = 2
	}

	override fun getMaxStoredEnergy(): Long {
		return (TICK_VALUE * 50).toLong()
	}

	override fun tick() {
		val world = getWorld()
		if(world == null || world.isClient) return

		for(direction in Direction.values()) {
			if(stored >= getMaxStoredEnergy()) break

			val targetPos = pos.offset(direction)
			val target = world.getBlockEntity(targetPos)

			if(target is FurnaceBlockEntity) {
				if((target as MixinFurnace).callIsBurning()) {
					stored += TICK_VALUE
				}
			}
		}

		if(stored > 0) {
			pushEnergy()
		}
	}
}
