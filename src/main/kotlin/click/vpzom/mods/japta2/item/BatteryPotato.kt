package click.vpzom.mods.japta2.item

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.item.util.ItemJPT
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.TypedActionResult
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.world.World

object ItemBatteryPotato: ItemJPT(Item.Settings().itemGroup(ItemGroup.FOOD).durability(16000)) {
	val USE = 500

	override fun getUseAction(stack: ItemStack): UseAction = UseAction.EAT
	override fun getMaxUseTime(stack: ItemStack): Int = 32

	override fun onItemFinishedUsing(stack: ItemStack, world: World, ent: LivingEntity): ItemStack {
		stack.applyDamage(USE, ent)

		val player = ent as? PlayerEntity
		player?.hungerManager?.add(3, 1f)

		return stack
	}

	override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
		val stack = player.getStackInHand(hand)
		if(stack.item == this && player.canConsume(false) && stack.damage + USE <= stack.durability) {
			player.setCurrentHand(hand)
			return TypedActionResult(ActionResult.SUCCESS, stack)
		}
		else {
			return TypedActionResult(ActionResult.FAILURE, stack)
		}
	}
}
