package click.vpzom.mods.japta2.block.util

import click.vpzom.mods.japta2.mixin.MixinFurnace
import net.minecraft.item.ItemStack
import net.minecraft.block.entity.FurnaceBlockEntity

object FurnaceHelper {
	fun canSmelt(te: FurnaceBlockEntity): Boolean {
		val recipe = te.world?.recipeManager?.get(te, te.world)

		return (te as MixinFurnace).callCanAcceptRecipeOutput(recipe)
	}
}
