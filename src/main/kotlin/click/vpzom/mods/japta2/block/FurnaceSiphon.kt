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

object BlockFurnaceSiphon: BlockModelContainer(Material.IRON) {
	init {
		setRegistryName("furnacesiphon")
		setUnlocalizedName("furnacesiphon")
		setHardness(2f)
		setCreativeTab(JAPTA2.Tab)
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityFurnaceSiphon()
	}
}

class TileEntityFurnaceSiphon: TileEntityJPT(), ITickable {
	val TICK_VALUE = 2

	override fun getMaxStoredEnergy(): Long {
		return (TICK_VALUE * 50).toLong()
	}

	override fun update() {
		val world = getWorld()
		if(world.isRemote) return

		for(direction in EnumFacing.VALUES) {
			if(stored >= getMaxStoredEnergy()) break

			val targetPos = pos.offset(direction)
			val target = world.getTileEntity(targetPos)

			if(target is TileEntityFurnace) {
				if(target.isBurning()) {
					stored += TICK_VALUE
				}
			}
		}

		if(stored > 0) {
			pushEnergy()
		}
	}
}
