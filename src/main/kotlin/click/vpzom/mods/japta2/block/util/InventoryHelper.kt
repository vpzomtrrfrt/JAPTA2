package click.vpzom.mods.japta2.block.util

import net.minecraft.block.BlockState
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IWorld

object InventoryHelper {
	public fun insertItem(dest: Inventory?, stack: ItemStack): ItemStack {
		return stack // TODO implement this function
	}

	public fun dropInventoryItems(world: IWorld, pos: BlockPos, inv: Inventory) {
		// TODO implement this function
	}
}
