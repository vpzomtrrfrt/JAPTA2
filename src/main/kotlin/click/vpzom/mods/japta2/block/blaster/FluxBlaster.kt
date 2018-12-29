package click.vpzom.mods.japta2.block.blaster

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.BlockElevatorShaft
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.Direction
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class BlockFluxBlaster private constructor(name: String, inhale: Boolean): BlockBlaster(BlockBlaster.defaultSettings(), name, inhale, false) {
	companion object {
		val normal = BlockFluxBlaster("fluxblaster", false)
		val inhaler = BlockFluxBlaster("fluxinhaler", true)
	}

	override fun createBlockEntity(view: BlockView): BlockEntity {
		return TileEntityFluxBlaster()
	}
}

class TileEntityFluxBlaster: TileEntityJPT(Type), Tickable {
	companion object {
		public val Type = JAPTA2.registerBlockEntity("fluxblaster", BlockEntityType.Builder.create(::TileEntityFluxBlaster))
	}
	override fun getMaxStoredEnergy(): Long {
		return 10000
	}

	override fun tick() {
		val world = getWorld()
		if(world == null) return

		val myPos = getPos()
		val myState = world.getBlockState(myPos)

		val myBlock = myState.getBlock() as? BlockFluxBlaster
		if(myBlock == null) return

		val facing = myState.get(BlockBlaster.PROP_FACING)
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
