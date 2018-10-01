package click.vpzom.mods.japta2

import click.vpzom.mods.japta2.block.BlockChargingPlate
import click.vpzom.mods.japta2.block.BlockEater
import click.vpzom.mods.japta2.block.BlockElevatorShaft
import click.vpzom.mods.japta2.block.BlockElevatorTop
import click.vpzom.mods.japta2.block.BlockFluxHopper
import click.vpzom.mods.japta2.block.BlockFurnaceHeater
import click.vpzom.mods.japta2.block.BlockMachineBase
import click.vpzom.mods.japta2.block.BlockMover
import click.vpzom.mods.japta2.block.BlockPowerCabinet
import click.vpzom.mods.japta2.block.BlockPowerCabinetBase
import click.vpzom.mods.japta2.block.TileEntityChargingPlate
import click.vpzom.mods.japta2.block.TileEntityEater
import click.vpzom.mods.japta2.block.TileEntityElevatorTop
import click.vpzom.mods.japta2.block.TileEntityFluxHopper
import click.vpzom.mods.japta2.block.TileEntityFurnaceHeater
import click.vpzom.mods.japta2.block.TileEntityMover
import click.vpzom.mods.japta2.block.TileEntityPowerCabinetBase
import click.vpzom.mods.japta2.block.blaster.BlockFluxBlaster
import click.vpzom.mods.japta2.block.blaster.BlockItemBlaster
import click.vpzom.mods.japta2.block.blaster.TileEntityFluxBlaster
import click.vpzom.mods.japta2.block.blaster.TileEntityItemBlaster
import click.vpzom.mods.japta2.item.ItemBatteryPotato
import click.vpzom.mods.japta2.item.ItemCoil
import click.vpzom.mods.japta2.item.ItemRFMeter
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Mod(modid = JAPTA2.ID, name = "JAPTA2", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object JAPTA2 {
	const val ID = "japta2"

	object Tab: CreativeTabs("japta2") {
		override fun getTabIconItem(): ItemStack {
			return ItemStack(ItemRFMeter)
		}
	}

	@Mod.EventHandler
	fun preInit(event: FMLPreInitializationEvent) {
		GameRegistry.registerTileEntity(TileEntityChargingPlate::class.java, ID + ":ChargingPlate")
		GameRegistry.registerTileEntity(TileEntityEater::class.java, ID + ":Eater")
		GameRegistry.registerTileEntity(TileEntityElevatorTop::class.java, ID + ":ElevatorTop")
		GameRegistry.registerTileEntity(TileEntityFluxBlaster::class.java, ID + ":FluxBlaster")
		GameRegistry.registerTileEntity(TileEntityItemBlaster::class.java, ID + ":ItemBlaster")
		GameRegistry.registerTileEntity(TileEntityFluxHopper::class.java, ID + ":FluxHopper")
		GameRegistry.registerTileEntity(TileEntityFurnaceHeater::class.java, ID + ":FurnaceHeater")
		GameRegistry.registerTileEntity(TileEntityMover::class.java, ID + ":Mover")
		GameRegistry.registerTileEntity(TileEntityPowerCabinetBase::class.java, ID + ":PowerCabinetBase")
	}

	@Mod.EventHandler
	@SideOnly(Side.CLIENT)
	fun clientInit(event: FMLInitializationEvent) {
		registerItemModel(BlockEater.item)
		registerItemModel(BlockElevatorShaft.item)
		registerItemModel(BlockElevatorTop.item)
		registerItemModel(BlockFluxBlaster.normal.item)
		registerItemModel(BlockFluxBlaster.inhaler.item)
		registerItemModel(BlockItemBlaster.normal.item)
		registerItemModel(BlockFluxHopper.item)
		registerItemModel(BlockFurnaceHeater.item)
		registerItemModel(BlockMachineBase.item)
		registerItemModel(BlockMover.item)
		registerItemModel(BlockPowerCabinetBase.item)
		registerItemModel(BlockChargingPlate.wooden.item)
		registerItemModel(BlockChargingPlate.normal.item)
		for(i in 0..15) {
			registerItemModel(BlockPowerCabinet.Item, i, ModelResourceLocation(ID + ":powercabinet", "value=" + i))
		}

		registerItemModel(ItemRFMeter)
		registerItemModel(ItemCoil.reception)
		registerItemModel(ItemCoil.transmission)
		registerItemModel(ItemBatteryPotato)
	}

	@Mod.EventBusSubscriber
	object EventHandler {
		@JvmStatic
		@SubscribeEvent
		fun registerBlocks(event: RegistryEvent.Register<Block>) {
			event.registry.registerAll(
					BlockEater,
					BlockElevatorShaft,
					BlockElevatorTop,
					BlockFluxBlaster.normal,
					BlockFluxBlaster.inhaler,
					BlockItemBlaster.normal,
					BlockFluxHopper,
					BlockFurnaceHeater,
					BlockMover,
					BlockPowerCabinetBase,
					BlockPowerCabinet,
					BlockMachineBase,
					BlockChargingPlate.wooden,
					BlockChargingPlate.normal
			)
		}

		@JvmStatic
		@SubscribeEvent
		fun registerItems(event: RegistryEvent.Register<Item>) {
			event.registry.registerAll(
					BlockEater.item,
					BlockElevatorShaft.item,
					BlockElevatorTop.item,
					BlockFluxBlaster.normal.item,
					BlockFluxBlaster.inhaler.item,
					BlockItemBlaster.normal.item,
					BlockFluxHopper.item,
					BlockFurnaceHeater.item,
					BlockMover.item,
					BlockPowerCabinetBase.item,
					BlockPowerCabinet.Item,
					BlockMachineBase.item,
					BlockChargingPlate.wooden.item,
					BlockChargingPlate.normal.item,

					ItemRFMeter,
					ItemCoil.reception,
					ItemCoil.transmission,
					ItemBatteryPotato
			)
		}
	}

	fun basicBlockItem(block: Block): ItemBlock {
		val tr = ItemBlock(block)
		tr.setRegistryName(block.getRegistryName())
		return tr
	}

	private fun registerItemModel(item: Item) {
		registerItemModel(item, 0)
	}

	private fun registerItemModel(item: Item, meta: Int) {
		val name = item.getRegistryName().toString()
		registerItemModel(item, meta, locationForName(name))
	}

	private fun locationForName(name: String): ModelResourceLocation {
		return ModelResourceLocation(name, "inventory")
	}

	private fun registerItemModel(item: Item, meta: Int, location: ModelResourceLocation) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, location);
	}
}
