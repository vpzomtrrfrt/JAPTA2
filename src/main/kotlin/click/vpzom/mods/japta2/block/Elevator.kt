package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.SoundEvents
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockElevatorShaft: Block(Material.GLASS) {
	init {
		setRegistryName("elevatorshaft")
		setUnlocalizedName("elevatorshaft")
		setHardness(1f)
		setCreativeTab(JAPTA2.Tab)
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun isOpaqueCube(state: IBlockState): Boolean {
		return false
	}
}

object BlockElevatorTop: BlockModelContainer(Material.IRON) {
	init {
		setRegistryName("elevatortop")
		setUnlocalizedName("elevatortop")
		setHardness(3f)
		setCreativeTab(JAPTA2.Tab)
	}
	
	val item = JAPTA2.basicBlockItem(this)
	val USE_BASE = 1000
	val USE_EXTRA = 100

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityElevatorTop()
	}
}

class TileEntityElevatorTop: TileEntityJPT(), ITickable {
	override fun getMaxStoredEnergy(): Long {
		return 26000
	}

	fun getEnergyCost(d: Int): Long {
		return (BlockElevatorTop.USE_BASE + (BlockElevatorTop.USE_EXTRA - 1) * d).toLong()
	}

	override fun update() {
		if(stored < BlockElevatorTop.USE_BASE) return // no possibility of having enough power, abort

		val myPos = getPos()
		val world = getWorld()

		var d = 1
		while(true) {
			val curPos = myPos.down(d)
			if(world.isAirBlock(curPos)) break

			val state = world.getBlockState(curPos)
			val block = state?.getBlock()
			if(block != BlockElevatorShaft && block != BlockElevatorTop) {
				return // invalid structure, abort
			}

			d++
		}

		val cost = getEnergyCost(d)
		if(stored < cost) return // not enough energy, abort

		val topList = world.getEntitiesWithinAABB(
				EntityLivingBase::class.java,
				AxisAlignedBB(
						myPos.getX().toDouble(),
						myPos.getY().toDouble() + 1.0,
						myPos.getZ().toDouble(),
						myPos.getX().toDouble() + 1.0,
						myPos.getY().toDouble() + 1.5,
						myPos.getZ().toDouble() + 1.0
				)
		)
		for(entity in topList) {
			if(entity.isSneaking()) {
				entity.setPositionAndUpdate(
						myPos.getX().toDouble() + 0.5,
						myPos.getY().toDouble() - d - 1,
						myPos.getZ().toDouble() + 0.5
				)
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.PLAYERS, 1f, 1f)
				stored -= cost

				if(stored < cost) return // out of energy, abort
			}
		}

		val bottomList = world.getEntitiesWithinAABB(
				EntityLivingBase::class.java,
				AxisAlignedBB(
						myPos.getX().toDouble(),
						myPos.getY().toDouble() - d + 0.75,
						myPos.getZ().toDouble(),
						myPos.getX().toDouble() + 1,
						myPos.getY().toDouble() - d + 1,
						myPos.getZ().toDouble() + 1
				)
		)
		for(entity in bottomList) {
			if(entity.motionY > 0) {
				entity.setPositionAndUpdate(
						myPos.getX().toDouble() + 0.5,
						myPos.getY().toDouble() + 1,
						myPos.getZ().toDouble() + 0.5
				)
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.PLAYERS, 1f, 1f)
				stored -= cost

				if(stored < cost) return // out of energy, abort
			}
		}
	}
}
