package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.TileEntityJPT
import net.minecraft.block.BlockBasePressurePlate
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

val ACTIVE = PropertyBool.create("active")

enum class ChargingPlateType(
		val plateName: String,
		val material: Material,
		val target: Class<out Entity>,
		val clickOnSound: SoundEvent,
		val clickOffSound: SoundEvent
) {
	ITEM(
			"chargingplate_item",
			Material.WOOD,
			EntityItem::class.java,
			SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_ON,
			SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_OFF
	),
	PLAYER(
			"chargingplate_player",
			Material.ROCK,
			EntityPlayer::class.java,
			SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_ON,
			SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_OFF
	)
}

public class BlockChargingPlate private constructor(val type: ChargingPlateType): BlockBasePressurePlate(type.material), ITileEntityProvider {
	companion object {
		val wooden = BlockChargingPlate(ChargingPlateType.ITEM)
		val normal = BlockChargingPlate(ChargingPlateType.PLAYER)
	}
	init {
		setHardness(1f)
		setCreativeTab(JAPTA2.Tab)
		setUnlocalizedName(type.plateName)
		setRegistryName(type.plateName)
	}

	val item = JAPTA2.basicBlockItem(this)

	fun getBB(pos: BlockPos): AxisAlignedBB {
		return PRESSURE_AABB.offset(pos)
	}

	override fun createNewTileEntity(world: World, i: Int): TileEntity {
		return TileEntityChargingPlate()
	}

	override fun createBlockState(): BlockStateContainer {
		return BlockStateContainer(this, ACTIVE)
	}

	override fun getMetaFromState(state: IBlockState): Int {
		if(state.getValue(ACTIVE)) return 15
		return 0
	}

	override fun getStateFromMeta(meta: Int): IBlockState {
		return getDefaultState().withProperty(ACTIVE, meta > 0)
	}

	override fun computeRedstoneStrength(world: World, pos: BlockPos): Int {
		val nothing = world.getEntitiesWithinAABB(type.target, getBB(pos)).isEmpty()
		if(nothing) return 0
		return 15
	}

	override fun getRedstoneStrength(state: IBlockState): Int {
		return getMetaFromState(state)
	}

	override fun setRedstoneStrength(state: IBlockState, s: Int): IBlockState {
		return state.withProperty(ACTIVE, s > 0)
	}

	override fun playClickOnSound(world: World, pos: BlockPos) {
		world.playSound(null, pos, type.clickOnSound, SoundCategory.BLOCKS, .3f, .3f)
	}

	override fun playClickOffSound(world: World, pos: BlockPos) {
		world.playSound(null, pos, type.clickOffSound, SoundCategory.BLOCKS, .3f, .3f)
	}
}

public class TileEntityChargingPlate: TileEntityJPT(), ITickable {
	override fun getMaxStoredEnergy(): Long = 1000

	override fun update() {
		if(stored <= 0) return

		val state = world.getBlockState(pos)
		val block = state.block as? BlockChargingPlate
		if(block == null) return
		if(!state.getValue(ACTIVE)) return

		val aabb = block.getBB(pos)

		when(block.type) {
			ChargingPlateType.ITEM -> {
				val list = world.getEntitiesWithinAABB(EntityItem::class.java, aabb)
				for(item in list) {
					val stack = item.item
					chargeItem(stack)
					if(stored <= 0) return

					item::class.java.getField("age").set(item, 0)
				}
			}
			ChargingPlateType.PLAYER -> {
				val list = world.getEntitiesWithinAABB(EntityPlayer::class.java, aabb)
				for(player in list) {
					val inv = player.inventory
					for(i in 0 until inv.getSizeInventory()) {
						val stack = inv.getStackInSlot(i)
						chargeItem(stack)
					}
				}
			}
		}
	}
}
