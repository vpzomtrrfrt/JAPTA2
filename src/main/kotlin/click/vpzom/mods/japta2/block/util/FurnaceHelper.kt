package click.vpzom.mods.japta2.block.util

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.tileentity.TileEntityFurnace

object FurnaceHelper {
	fun canSmelt(te: TileEntityFurnace): Boolean {
		// took this from decompiled forge
		if (te.getStackInSlot(0) == null) {
			return false;
		} else {
			val itemstack = FurnaceRecipes.instance().getSmeltingResult(te.getStackInSlot(0));
			if (itemstack == null || itemstack.isEmpty()) return false;
			val resultSlot = te.getStackInSlot(2);
			if (resultSlot == null || resultSlot.isEmpty()) return true;
			if (!resultSlot.isItemEqual(itemstack)) return false;
			val result = resultSlot.count + itemstack.count;
			return result <= te.getInventoryStackLimit() && result <= resultSlot.getMaxStackSize();
		}
	}
}
