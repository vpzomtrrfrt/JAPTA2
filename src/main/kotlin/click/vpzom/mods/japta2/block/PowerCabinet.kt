package click.vpzom.mods.japta2.block

import click.vpzom.mods.japta2.JAPTA2
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

val VALUE = PropertyInteger.create("value", 0, 15)

object BlockPowerCabinet: Block(Material.IRON) {
	val LINE_VALUE = 2000
	val PROP_VALUE = VALUE
	init {
		setRegistryName("powercabinet")
		setUnlocalizedName("powercabinet")
		setHardness(2f)
		setCreativeTab(JAPTA2.Tab)
	}

	object MetaGetter: IItemPropertyGetter {
		override fun apply(stack: ItemStack, world: World?, p2: EntityLivingBase?): Float {
			return stack.getMetadata().toFloat()
		}
	}

	object Item: ItemBlock(BlockPowerCabinet) {
		init {
			setRegistryName(BlockPowerCabinet.getRegistryName())
			setHasSubtypes(true)
			addPropertyOverride(ResourceLocation("meta"), MetaGetter)
		}

		override fun getMetadata(damage: Int): Int {
			return damage
		}

		override fun getUnlocalizedName(stack: ItemStack): String {
			return "tile.powercabinet_" + stack.getMetadata()
		}
	}
	
	override fun createBlockState(): BlockStateContainer {
		return BlockStateContainer(this, VALUE)
	}

	override fun getMetaFromState(state: IBlockState): Int {
		return state.getValue(VALUE)
	}

	override fun getStateFromMeta(meta: Int): IBlockState {
		return getDefaultState().withProperty(VALUE, meta)
	}

	override fun damageDropped(state: IBlockState): Int {
		return getMetaFromState(state)
	}

	override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
		list.add(ItemStack(this, 1, 0))
		list.add(ItemStack(this, 1, 15))
	}
}
