package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.BlockModelContainer
import click.vpzom.mods.japta2.block.util.EnergyHelper
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EntitySelectors
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

val MOVER_FACING = PropertyEnum.create("facing", EnumFacing::class.java)

object BlockMover: BlockModelContainer(Material.ROCK) {
	init {
		setRegistryName("mover")
		setUnlocalizedName("mover")
		setHardness(1f)
		setCreativeTab(JAPTA2.Tab)
		setDefaultState(blockState.getBaseState().withProperty(MOVER_FACING, EnumFacing.UP))
	}

	val item = JAPTA2.basicBlockItem(this)

	override fun createNewTileEntity(world: World, i: Int): TileEntity = TileEntityMover()

	override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, MOVER_FACING)
	override fun getMetaFromState(state: IBlockState): Int = state.getValue(MOVER_FACING).getIndex()
	override fun getStateFromMeta(meta: Int): IBlockState {
		return defaultState.withProperty(MOVER_FACING, EnumFacing.getFront(meta))
	}

	override fun getStateForPlacement(world: World, pos: BlockPos, side: EnumFacing, f1: Float, f2: Float, f3: Float, i1: Int, placer: EntityLivingBase): IBlockState {
		var facing = EnumFacing.getDirectionFromEntityLiving(pos, placer)
		if(!placer.isSneaking()) {
			facing = facing.getOpposite()
		}

		return super.getStateForPlacement(world, pos, side, f1, f2, f3, i1, placer).withProperty(MOVER_FACING, facing)
	}
}

val USE = 50

class TileEntityMover: TileEntityJPT(), ITickable {
	override fun update() {
		val pos = pos
		val myState = world.getBlockState(pos)
		if(myState.block != BlockMover) return

		val facing = myState.getValue(MOVER_FACING)

		if(stored >= USE) {
			val change = facing.directionVec
			val list = world.getEntitiesWithinAABB(Entity::class.java, AxisAlignedBB(pos.x.toDouble(), pos.y + 1.0, pos.z.toDouble(), pos.x + 1.0, pos.y + 1.5, pos.z + 1.0), EntitySelectors.IS_ALIVE)

			entLoop@ for(ent in list) {
				when(ent) {
					is EntityLivingBase -> {
						if(ent.isSneaking()) continue@entLoop
						ent.setPositionAndUpdate(ent.posX + change.x * 0.5, ent.posY + change.y * 0.5, ent.posZ + change.z * 0.5)
					}
					else -> {
						ent.setPosition(ent.posX + change.x * 0.5, ent.posY + change.y * 0.5, ent.posZ + change.z * 0.5)
					}
				}
				stored -= USE
				if(stored < USE) break@entLoop
			}
		}

		if(stored > 0) {
			pushEnergy(facing)
		}
	}

	override fun getMaxStoredEnergy(): Long = USE * 2L
}
