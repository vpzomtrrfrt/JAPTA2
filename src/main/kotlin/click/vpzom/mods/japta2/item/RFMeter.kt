package click.vpzom.mods.japta2.item

import click.vpzom.mods.japta2.block.util.EnergyHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

object ItemRFMeter: Item() {
	init {
		setMaxStackSize(1)
		setUnlocalizedName("rfMeter")
		setRegistryName("rfMeter")
	}

	override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, f1: Float, f2: Float, f3: Float): EnumActionResult {
		if(!world.isRemote) {
			val result = EnergyHelper.getStoredEnergy(world, pos, side)
			if(result == null) {
				player.sendMessage(TextComponentTranslation("text.japta2.rfmeter.no"))
			}
			else {
				val (current, max) = result
				player.sendMessage(TextComponentTranslation("text.japta2.rfmeter.result", current, max))
			}
		}
		return EnumActionResult.SUCCESS
	}
}
