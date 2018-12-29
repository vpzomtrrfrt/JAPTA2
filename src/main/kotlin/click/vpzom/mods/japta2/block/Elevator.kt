package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemGroup
import net.minecraft.entity.LivingEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Tickable
import net.minecraft.util.math.BoundingBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

object BlockElevatorShaft: Block(Block.Settings.of(Material.GLASS).strength(1f, 1f)) {
	val item = JAPTA2.basicBlockItem(this, ItemGroup.REDSTONE)
}

object BlockElevatorTop: BlockModelContainer(Block.Settings.of(Material.METAL).strength(3f, 3f)) {
	val item = JAPTA2.basicBlockItem(this, ItemGroup.REDSTONE)
	val USE_BASE = 1000
	val USE_EXTRA = 100

	override fun createBlockEntity(view: BlockView): BlockEntity = TileEntityElevatorTop()
}

class TileEntityElevatorTop: TileEntityJPT(type), Tickable {
	companion object {
		lateinit var type: BlockEntityType<TileEntityElevatorTop>
	}
	override fun getMaxStoredEnergy(): Long {
		return 26000
	}

	fun getEnergyCost(d: Int): Long {
		return (BlockElevatorTop.USE_BASE + (BlockElevatorTop.USE_EXTRA - 1) * d).toLong()
	}

	override fun tick() {
		if(stored < BlockElevatorTop.USE_BASE) return // no possibility of having enough power, abort

		val world = getWorld()
		if(world == null || world.isClient) return

		val myPos = getPos()

		var d = 1
		while(true) {
			val curPos = myPos.down(d)
			if(world.isAir(curPos)) break

			val state = world.getBlockState(curPos)
			val block = state?.getBlock()
			if(block != BlockElevatorShaft && block != BlockElevatorTop) {
				return // invalid structure, abort
			}

			d++
		}

		val cost = getEnergyCost(d)
		if(stored < cost) return // not enough energy, abort

		val topList = world.getEntities(
				LivingEntity::class.java,
				BoundingBox(
						myPos.getX().toDouble(),
						myPos.getY().toDouble() + 1.0,
						myPos.getZ().toDouble(),
						myPos.getX().toDouble() + 1.0,
						myPos.getY().toDouble() + 1.5,
						myPos.getZ().toDouble() + 1.0
				),
				null
		)
		for(entity in topList) {
			if(entity.isSneaking()) {
				entity.setPosition(
						myPos.getX().toDouble() + 0.5,
						myPos.getY().toDouble() - d - 1,
						myPos.getZ().toDouble() + 0.5
				)
				world.playSound(null, entity.x, entity.y, entity.z, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.PLAYER, 1f, 1f)
				stored -= cost

				if(stored < cost) return // out of energy, abort
			}
		}

		val bottomList = world.getEntities(
				LivingEntity::class.java,
				BoundingBox(
						myPos.getX().toDouble(),
						myPos.getY().toDouble() - d + 0.75,
						myPos.getZ().toDouble(),
						myPos.getX().toDouble() + 1,
						myPos.getY().toDouble() - d + 1,
						myPos.getZ().toDouble() + 1
				),
				null
		)
		for(entity in bottomList) {
			if(entity.velocityY > 0) {
				entity.setPosition(
						myPos.getX().toDouble() + 0.5,
						myPos.getY().toDouble() + 1,
						myPos.getZ().toDouble() + 0.5
				)
				world.playSound(null, entity.x, entity.y, entity.z, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.PLAYER, 1f, 1f)
				stored -= cost

				if(stored < cost) return // out of energy, abort
			}
		}
	}
}
