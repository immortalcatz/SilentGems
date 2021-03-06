package net.silentchaos512.gems.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.api.lib.EnumMaterialTier;
import net.silentchaos512.gems.api.tool.part.ToolPart;
import net.silentchaos512.gems.api.tool.part.ToolPartRegistry;
import net.silentchaos512.gems.item.armor.ItemArmorFrame;
import net.silentchaos512.gems.util.ArmorHelper;
import net.silentchaos512.lib.recipe.RecipeBaseSL;
import net.silentchaos512.lib.util.StackHelper;

public class RecipeMultiGemArmor extends RecipeBaseSL {

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {

    // Get the middle stack, which determines the armor type.
    ItemStack centerStack = inv.getStackInRowAndColumn(1, 1);
    if (StackHelper.isEmpty(centerStack) || !(centerStack.getItem() instanceof ItemArmorFrame)) {
      return StackHelper.empty();
    }

    // Make sure nothing is in the corners.
    if (StackHelper.isValid(inv.getStackInRowAndColumn(0, 0)) || StackHelper.isValid(inv.getStackInRowAndColumn(2, 0))
        || StackHelper.isValid(inv.getStackInRowAndColumn(2, 2)) || StackHelper.isValid(inv.getStackInRowAndColumn(0, 2))) {
      return StackHelper.empty();
    }

    // Determine the target tier and output item.
    ItemArmorFrame item = (ItemArmorFrame) centerStack.getItem();
    Item outputItem = item.getOutputItem(centerStack);
    EnumMaterialTier targetTier = item.getTier(centerStack);

    // Get armor parts. Check tiers match (getGems checks all materials are same tier, or it returns null).
    ItemStack[] stacks = getGems(inv);

    if (stacks == null || EnumMaterialTier.fromStack(stacks[0]) != targetTier)
      return StackHelper.empty();

    return ArmorHelper.constructArmor(outputItem, stacks);
  }

  public ItemStack[] getGems(InventoryCrafting inv) {

    // Get materials from slots.
    ItemStack[] stacks = new ItemStack[4];
    stacks[0] = inv.getStackInRowAndColumn(0, 1); // West
    stacks[1] = inv.getStackInRowAndColumn(1, 0); // North
    stacks[2] = inv.getStackInRowAndColumn(2, 1); // East
    stacks[3] = inv.getStackInRowAndColumn(1, 2); // South

    // Make sure all are same tier and parts aren't blacklisted.
    ToolPart part;
    EnumMaterialTier tier = EnumMaterialTier.fromStack(stacks[0]);
    for (int i = 1; i < stacks.length; ++i) {
      part = ToolPartRegistry.fromStack(stacks[i]);
      if (tier == null || part == null || part.isBlacklisted(stacks[i]) || part.getTier() != tier) {
        return null;
      }
    }

    return stacks;
  }
}
