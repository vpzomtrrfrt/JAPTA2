package click.vpzom.mods.japta2

import click.vpzom.mods.japta2.block.BlockChargingPlate
import click.vpzom.mods.japta2.block.BlockEater
import click.vpzom.mods.japta2.block.BlockElevatorShaft
import click.vpzom.mods.japta2.block.BlockElevatorTop
import click.vpzom.mods.japta2.block.BlockFluxHopper
import click.vpzom.mods.japta2.block.BlockFurnaceHeater
import click.vpzom.mods.japta2.block.BlockFurnaceSiphon
import click.vpzom.mods.japta2.block.BlockMachineBase
import click.vpzom.mods.japta2.block.BlockMover
import click.vpzom.mods.japta2.block.BlockPowerCabinet
import click.vpzom.mods.japta2.block.BlockPowerCabinetBase
import click.vpzom.mods.japta2.block.TileEntityChargingPlate
import click.vpzom.mods.japta2.block.TileEntityEater
import click.vpzom.mods.japta2.block.TileEntityElevatorTop
import click.vpzom.mods.japta2.block.TileEntityFluxHopper
import click.vpzom.mods.japta2.block.TileEntityFurnaceHeater
import click.vpzom.mods.japta2.block.TileEntityFurnaceSiphon
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
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.block.BlockItem
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object JAPTA2 {
	const val ID = "japta2"

	/*
	fun clientInit(event: FMLInitializationEvent) {
		registerItemModel(BlockEater.item)
		registerItemModel(BlockElevatorShaft.item)
		registerItemModel(BlockElevatorTop.item)
		registerItemModel(BlockFluxBlaster.normal.item)
		registerItemModel(BlockFluxBlaster.inhaler.item)
		registerItemModel(BlockItemBlaster.normal.item)
		registerItemModel(BlockFluxHopper.item)
		registerItemModel(BlockFurnaceHeater.item)
		registerItemModel(BlockFurnaceSiphon.item)
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
	*/

	fun registerBlocks() {
		registerBlock("eater", BlockEater)
		registerBlock("elevatorshaft", BlockElevatorShaft)
		registerBlock("elevatortop", BlockElevatorTop)
		registerBlock("fluxblaster", BlockFluxBlaster.normal)
		registerBlock("fluxinhaler", BlockFluxBlaster.inhaler)
		registerBlock("itemblaster", BlockItemBlaster.normal)
		registerBlock("fluxhopper", BlockFluxHopper)
		registerBlock("furnaceheater", BlockFurnaceHeater)
		registerBlock("furnacesiphon", BlockFurnaceSiphon)
		registerBlock("mover", BlockMover)
		registerBlock("powercabinetbase", BlockPowerCabinetBase)
		registerBlock("powercabinet", BlockPowerCabinet)
		registerBlock("machinebase", BlockMachineBase)
		registerBlock("chargingplate_item", BlockChargingPlate.wooden)
		registerBlock("chargingplate_player", BlockChargingPlate.normal)
	}

	private fun registerBlock(name: String, block: Block) {
		Registry.register(Registry.BLOCK, Identifier(ID, name), block)
	}

	fun registerItems() {
		registerBlockItems(
				BlockEater.item,
				BlockElevatorShaft.item,
				BlockElevatorTop.item,
				BlockFluxBlaster.normal.item,
				BlockFluxBlaster.inhaler.item,
				BlockItemBlaster.normal.item,
				BlockFluxHopper.item,
				BlockFurnaceHeater.item,
				BlockFurnaceSiphon.item,
				BlockMover.item,
				BlockPowerCabinetBase.item,
				BlockPowerCabinet.ItemPowerCabinet,
				BlockMachineBase.item,
				BlockChargingPlate.wooden.item,
				BlockChargingPlate.normal.item)

		registerItem("rfmeter", ItemRFMeter)
		registerItem("coilreception", ItemCoil.reception)
		registerItem("coiltransmission", ItemCoil.transmission)
		registerItem("batterypotato", ItemBatteryPotato)
	}

	private fun registerItem(name: String, item: Item) {
		Registry.register(Registry.ITEM, Identifier(ID, name), item)
	}

	private fun registerBlockItems(vararg items: BlockItem) {
		for(item in items) {
			Registry.register(Registry.ITEM, Registry.BLOCK.getId(item.block), item)
		}
	}

	fun basicBlockItem(block: Block): BlockItem {
		val tr = BlockItem(block, Item.Settings())
		return tr
	}

	fun <T: BlockEntity>registerBlockEntity(name: String, builder: BlockEntityType.Builder<T>): BlockEntityType<T> {
		return Registry.register(Registry.BLOCK_ENTITY, ID + ":" + name, builder.method_11034(null))
	}

	/*
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
		MinecraftClient.instance.itemRenderer.getItemModelMesher().register(item, meta, location);
	}
	*/
}
