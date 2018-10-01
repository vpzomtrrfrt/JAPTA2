package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.FurnaceHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.BlockFurnace
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.world.World

object BlockFurnaceHeater: BlockModelContainer(Material.IRON) {
	init {
		setRegistryName("furnaceheater")
		setUnlocalizedName("furnaceheater")
		setHardness(3f)
		setCreativeTab(JAPTA2.Tab)
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityFurnaceHeater()
	}
}

class TileEntityFurnaceHeater: TileEntityJPT(), ITickable {
	val TICK_COST = 20

	override fun getMaxStoredEnergy(): Long {
		return (TICK_COST * 200).toLong()
	}

	override fun update() {
		val world = getWorld()
		if(world.isRemote) return

		for(direction in EnumFacing.VALUES) {
			if(stored < TICK_COST) return

			val targetPos = pos.offset(direction)
			val target = world.getTileEntity(targetPos)

			if(target is TileEntityFurnace) {
				if(target.isBurning()) {
					if(FurnaceHelper.canSmelt(target) && target.getField(0) < 2) {
						target.setField(0, target.getField(0) + 1)
						stored -= TICK_COST
					}
				}
				else {
					if(FurnaceHelper.canSmelt(target)) {
						if(stored >= TICK_COST * target.getCookTime(target.getStackInSlot(0))) {
							target.setField(0, 2)
							stored -= TICK_COST
							BlockFurnace.setState(true, getWorld(), targetPos)
						}
					}
				}
			}
		}
	}
}
