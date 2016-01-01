package net.silentchaos512.gems.item.tool;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.client.renderers.tool.ToolRenderHelper;
import net.silentchaos512.gems.core.registry.SRegistry;
import net.silentchaos512.gems.core.util.LocalizationHelper;
import net.silentchaos512.gems.core.util.ToolHelper;
import net.silentchaos512.gems.item.CraftingMaterial;
import net.silentchaos512.gems.item.ModItems;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.material.ModMaterials;

public class GemHoe extends ItemHoe {

  public final int gemId;
  public final boolean supercharged;

  public GemHoe(ToolMaterial toolMaterial, int gemId, boolean supercharged) {

    super(toolMaterial);

    this.gemId = gemId;
    this.supercharged = supercharged;
    this.setMaxDamage(toolMaterial.getMaxUses());
    addRecipe(new ItemStack(this), gemId, supercharged);
    this.setCreativeTab(SilentGems.tabSilentGems);
  }
  
  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {

    ToolHelper.addInformation(stack, player, list, advanced);
  }

  public static void addRecipe(ItemStack tool, int gemId, boolean supercharged) {

    ItemStack material = ToolHelper.getCraftingMaterial(gemId, supercharged);

    if (supercharged) {
      GameRegistry.addRecipe(new ShapedOreRecipe(tool, true, "gg", " s", " s", 'g', material, 's',
          CraftingMaterial.getStack(Names.ORNATE_STICK)));
    } else {
      GameRegistry.addRecipe(
          new ShapedOreRecipe(tool, true, "gg", " s", " s", 'g', material, 's', "stickWood"));
    }
  }

  public int getGemId() {

    return gemId;
  }

  @Override
  public int getColorFromItemStack(ItemStack stack, int pass) {

    return ToolRenderHelper.instance.getColorFromItemStack(stack, pass);
  }

  @Override
  public IIcon getIcon(ItemStack stack, int pass) {

    return ToolRenderHelper.instance.getIcon(stack, pass, gemId, supercharged);
  }
  
  @Override
  public int getMaxDamage(ItemStack stack) {

    return super.getMaxDamage(stack) + ToolHelper.getDurabilityBoost(stack);
  }

  @Override
  public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {

    return ToolHelper.getIsRepairable(stack1, stack2);
  }

  @Override
  public int getRenderPasses(int meta) {

    return ToolRenderHelper.RENDER_PASS_COUNT;
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {

    return LocalizationHelper.TOOL_PREFIX + "Hoe" + gemId + (supercharged ? "Plus" : "");
  }

  @Override
  public boolean hasEffect(ItemStack stack, int pass) {

    return ToolRenderHelper.instance.hasEffect(stack, pass);
  }

  @Override
  public boolean requiresMultipleRenderPasses() {

    return true;
  }
  
  @Override
  public void registerIcons(IIconRegister reg) {

    if (gemId >= 0 && gemId < ToolRenderHelper.HEAD_TYPE_COUNT) {
      itemIcon = ToolRenderHelper.instance.hoeIcons.headM[gemId];
    }
  }
  
  @Override
  public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {

    boolean canceled = super.onBlockStartBreak(stack, x, y, z, player);
    if (!canceled) {
      ToolHelper.onBlockStartBreak(stack, x, y, z, player);
    }
    return canceled;
  }
  
  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase entity1, EntityLivingBase entity2) {

    ToolHelper.hitEntity(stack);
    return super.hitEntity(stack, entity1, entity2);
  }
  
  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float hitX, float hitY, float hitZ) {

    boolean success = super.onItemUse(stack, player, world, x, y, z, par7, hitX, hitY, hitZ);

    if (success) {
      ToolHelper.incrementStatBlocksTilled(stack, 1);
    }

    return success;
  }
}
