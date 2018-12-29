package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.FurnaceHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import click.vpzom.mods.japta2.mixin.MixinFurnace
import net.minecraft.block.AbstractFurnaceBlock
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

object BlockFurnaceHeater: BlockModelContainer(Block.Settings.of(Material.METAL).strength(3f, 3f)) {
	val item = JAPTA2.basicBlockItem(this, ItemGroup.REDSTONE)

	override fun createBlockEntity(view: BlockView): BlockEntity = TileEntityFurnaceHeater()
}

class TileEntityFurnaceHeater: TileEntityJPT(type), Tickable {
	companion object {
		lateinit var type: BlockEntityType<TileEntityFurnaceHeater>
		val TICK_COST = 30
	}

	override fun getMaxStoredEnergy(): Long {
		return (TICK_COST * 200).toLong()
	}

	override fun tick() {
		val world = getWorld()
		if(world == null || world.isClient) return

		for(direction in Direction.values()) {
			if(stored < TICK_COST) return

			val targetPos = pos.offset(direction)
			val target = world.getBlockEntity(targetPos)

			if(target is FurnaceBlockEntity) {
				if((target as MixinFurnace).callIsBurning()) {
					if(FurnaceHelper.canSmelt(target) && target.getInvProperty(0) < 2) {
						target.setInvProperty(0, target.getInvProperty(0) + 1)
						stored -= TICK_COST
					}
				}
				else {
					if(FurnaceHelper.canSmelt(target)) {
						if(stored >= TICK_COST * (target as MixinFurnace).callGetCookTime()) {
							target.setInvProperty(0, 2)
							stored -= TICK_COST
							world.setBlockState(targetPos, world.getBlockState(targetPos).with(AbstractFurnaceBlock.field_11105, true), 3)
						}
					}
				}
			}
		}
	}
}
