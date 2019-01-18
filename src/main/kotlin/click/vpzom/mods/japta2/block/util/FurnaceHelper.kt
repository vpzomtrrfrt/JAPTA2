package click.vpzom.mods.japta2.block.util

import click.vpzom.mods.japta2.mixin.MixinFurnace
import net.minecraft.item.ItemStack
import net.minecraft.block.entity.FurnaceBlockEntity
import net.minecraft.recipe.RecipeType

object FurnaceHelper {
	fun canSmelt(te: FurnaceBlockEntity): Boolean {
		val recipe = te.world?.recipeManager?.get(RecipeType.SMELTING, te, te.world)

		return (te as MixinFurnace).callCanAcceptRecipeOutput(recipe?.orElse(null))
	}
}
