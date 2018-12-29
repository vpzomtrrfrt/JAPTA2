package click.vpzom.mods.japta2.item

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.block.util.EnergyHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.text.TranslatableTextComponent
import net.minecraft.world.World

object ItemRFMeter: Item(Item.Settings().stackSize(1).itemGroup(ItemGroup.TOOLS)) {
	override fun useOnBlock(ctx: ItemUsageContext): ActionResult {
		val player = ctx.player
		if(player == null) return ActionResult.FAILURE

		if(!ctx.world.isClient) {
			val result = EnergyHelper.getStoredEnergy(ctx.world, ctx.pos, ctx.facing)
			if(result == null) {
				player.addChatMessage(TranslatableTextComponent("text.japta2.rfmeter.no"), true)
			}
			else {
				val (current, max) = result
				player.addChatMessage(TranslatableTextComponent("text.japta2.rfmeter.result", current, max), true)
			}
		}
		return ActionResult.SUCCESS
	}
}
