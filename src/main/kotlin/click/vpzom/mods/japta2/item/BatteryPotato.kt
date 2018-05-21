package click.vpzom.mods.japta2.item

import click.vpzom.mods.japta2.JAPTA2
import click.vpzom.mods.japta2.item.util.ItemJPT
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

object ItemBatteryPotato: ItemJPT() {
	val USE = 500

	init {
		setRegistryName("batterypotato")
		setUnlocalizedName("batterypotato")
		setCreativeTab(JAPTA2.Tab)
		setMaxDamage(16000)
	}

	override fun getItemUseAction(stack: ItemStack): EnumAction = EnumAction.EAT
	override fun getMaxItemUseDuration(stack: ItemStack): Int = 32

	override fun onItemUseFinish(stack: ItemStack, world: World, ent: EntityLivingBase): ItemStack {
		stack.damageItem(USE, ent)

		val player = ent as? EntityPlayer
		player?.getFoodStats()?.addStats(3, 1f)

		return stack
	}

	override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
		val stack = player.getHeldItem(hand)
		if(stack.item == this && player.canEat(false) && stack.getItemDamage() + USE <= stack.getMaxDamage()) {
			player.setActiveHand(hand)
			return ActionResult(EnumActionResult.SUCCESS, stack)
		}
		else {
			return ActionResult(EnumActionResult.FAIL, stack)
		}
	}
}
