package click.vpzom.mods.japta2.block.blaster

import click.vpzom.mods.japta2.block.BlockElevatorShaft
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BlockFluxBlaster private constructor(name: String, inhale: Boolean): BlockBlaster(name, inhale, false) {
	companion object {
		val normal = BlockFluxBlaster("fluxblaster", false)
		val inhaler = BlockFluxBlaster("fluxinhaler", true)
	}

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityFluxBlaster()
	}
}

class TileEntityFluxBlaster: TileEntityJPT(), ITickable {
	override fun getMaxStoredEnergy(): Long {
		return 10000
	}

	override fun update() {
		val world = getWorld()
		val myPos = getPos()
		val myState = world.getBlockState(myPos)

		val myBlock = myState.getBlock() as? BlockFluxBlaster
		if(myBlock == null) return

		val facing = myState.getValue(BlockBlaster.PROP_FACING)
		val range = BlockBlaster.RANGE
		val inhale = myBlock.inhale

		for(i in 1..range) {
			if(!inhale && stored <= 0) return
			val remaining = getMaxStoredEnergy() - stored
			if(inhale && remaining <= 0) return

			var curPos = myPos.offset(facing, i)
			while(world.getBlockState(curPos).getBlock() == BlockElevatorShaft) {
				curPos = curPos.up()
			}

			if(inhale) {
				stored += EnergyHelper.extractEnergy(world, curPos, facing.getOpposite(), remaining)
			}
			else {
				stored -= EnergyHelper.insertEnergy(world, curPos, facing.getOpposite(), stored)
			}
		}
	}
}
