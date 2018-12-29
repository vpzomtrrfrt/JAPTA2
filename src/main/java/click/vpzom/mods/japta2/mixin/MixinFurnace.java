package click.vpzom.mods.japta2.mixin;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface MixinFurnace {
	@Invoker
	boolean callCanAcceptRecipeOutput(Recipe recipe_1);

	@Invoker
	boolean callIsBurning();

	@Invoker
	int callGetCookTime();
}
