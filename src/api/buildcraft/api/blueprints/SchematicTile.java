/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.api.blueprints;

import buildcraft.api.core.JavaTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.LinkedList;

public class SchematicTile extends SchematicBlock {

	/**
	 * This tree contains additional data to be stored in the blueprint. By
	 * default, it will be initialized from Schematic.readFromWord with
	 * the standard readNBT function of the corresponding tile (if any) and will
	 * be loaded from BptBlock.buildBlock using the standard writeNBT function.
	 */
	public NBTTagCompound cpt = new NBTTagCompound();

	@Override
	public void idsToSchematic(MappingRegistry registry) {
		inventorySlotsToSchematic(registry, cpt, "Items");
	}

	@Override
	public void idsToWorld(MappingRegistry registry) {
		inventorySlotsToWorld(registry, cpt, "Items");
	}

	/**
	 * Places the block in the world, at the location specified in the slot.
	 */
	@Override
	public void writeToWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		super.writeToWorld(context, x, y, z, stacks);

		if (block.hasTileEntity(meta)) {
			TileEntity tile = context.world().getTileEntity(x, y, z);

			cpt.setInteger("x", x);
			cpt.setInteger("y", y);
			cpt.setInteger("z", z);

			if (tile != null) {
				tile.readFromNBT(cpt);
			}
		}
	}

	@Override
	public void writeToSchematic(IBuilderContext context, int x, int y, int z) {
		super.writeToSchematic(context, x, y, z);

		if (block.hasTileEntity(meta)) {
			TileEntity tile = context.world().getTileEntity(x, y, z);

			if (tile != null) {
				tile.writeToNBT(cpt);
			}
		}
	}

	@Override
	public void writeRequirementsToSchematic(IBuilderContext context, int x, int y, int z) {
		super.writeRequirementsToSchematic(context, x, y, z);

		if (block.hasTileEntity(meta)) {
			TileEntity tile = context.world().getTileEntity(x, y, z);

			if (tile instanceof IInventory) {
				IInventory inv = (IInventory) tile;

				ArrayList<ItemStack> rqs = new ArrayList<ItemStack>();

				for (int i = 0; i < inv.getSizeInventory(); ++i) {
					if (inv.getStackInSlot(i) != null) {
						rqs.add(inv.getStackInSlot(i));
					}
				}

				storedRequirements = JavaTools.concat(storedRequirements,
						rqs.toArray(new ItemStack[rqs.size()]));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, MappingRegistry registry) {
		super.writeToNBT(nbt, registry);

		nbt.setTag("blockCpt", cpt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt,	MappingRegistry registry) {
		super.readFromNBT(nbt, registry);

		cpt = nbt.getCompoundTag("blockCpt");
	}
}
