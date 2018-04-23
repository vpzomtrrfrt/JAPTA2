package click.vpzom.mods.japta2

import click.vpzom.mods.japta2.block.BlockFluxHopper
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Mod(modid = JAPTA2.ID, name = "JAPTA2", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object JAPTA2 {
	const val ID = "japta2"

	@Mod.EventHandler
	@SideOnly(Side.CLIENT)
	fun clientInit(event: FMLInitializationEvent) {
		registerItemModel(BlockFluxHopper.item)
	}

	@Mod.EventBusSubscriber
	object EventHandler {
		@JvmStatic
		@SubscribeEvent
		fun registerBlocks(event: RegistryEvent.Register<Block>) {
			event.registry.register(BlockFluxHopper)
		}

		@JvmStatic
		@SubscribeEvent
		fun registerItems(event: RegistryEvent.Register<Item>) {
			event.registry.register(BlockFluxHopper.item)
		}
	}

	fun basicBlockItem(block: Block): ItemBlock {
		val tr = ItemBlock(block)
		tr.setRegistryName(block.getRegistryName())
		return tr
	}

	private fun registerItemModel(item: Item) {
		val name = item.getRegistryName().toString()
		println("Registering " + name)
		registerItemModel(item, 0, locationForName(name))
	}

	private fun locationForName(name: String): ModelResourceLocation {
		return ModelResourceLocation(name, "inventory")
	}

	private fun registerItemModel(item: Item, meta: Int, location: ModelResourceLocation) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, location);
	}
}
