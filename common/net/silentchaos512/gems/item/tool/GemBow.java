package net.silentchaos512.gems.item.tool;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.client.renderers.tool.ToolRenderHelper;
import net.silentchaos512.gems.core.registry.IAddRecipe;
import net.silentchaos512.gems.core.util.LocalizationHelper;
import net.silentchaos512.gems.core.util.LogHelper;
import net.silentchaos512.gems.core.util.ToolHelper;
import net.silentchaos512.gems.item.CraftingMaterial;
import net.silentchaos512.gems.lib.Names;

public class GemBow extends ItemBow implements IAddRecipe {

  public static final float ENCHANTABILITY_MULTIPLIER = 0.45f;

  public final int gemId;
  public final boolean supercharged;
  public final double arrowDamage;
  public final ToolMaterial toolMaterial;

  public GemBow(ToolMaterial toolMaterial, int gemId, boolean supercharged) {

    this.gemId = gemId;
    this.supercharged = supercharged;
    this.arrowDamage = (double) (toolMaterial.getDamageVsEntity()) / 2.0 + 0.5;
    this.toolMaterial = toolMaterial;
    this.setMaxDamage(toolMaterial.getMaxUses());
    this.setCreativeTab(SilentGems.tabSilentGems);
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {

    ToolHelper.addInformation(stack, player, list, advanced);
  }

  @Override
  public void addRecipes() {

    ItemStack material = ToolHelper.getCraftingMaterial(gemId, supercharged);
    ItemStack result = new ItemStack(this);
    if (supercharged) {
      ItemStack rod = CraftingMaterial.getStack(Names.ORNATE_STICK);
      ItemStack string = CraftingMaterial.getStack(Names.GILDED_STRING);
      GameRegistry.addShapedRecipe(result, "rgs", "g s", "rgs", 'r', rod, 'g', material, 's',
          string);
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(result, "rgs", "g s", "rgs", 'r', "stickWood", 'g',
          material, 's', Items.string));
    }
  }

  @Override
  public void addOreDict() {

  }

  @Override
  public int getMaxDamage(ItemStack stack) {

    return super.getMaxDamage(stack) + ToolHelper.getDurabilityBoost(stack);
  }

  @Override
  public int getItemEnchantability() {

    return (int) (toolMaterial.getEnchantability() * ENCHANTABILITY_MULTIPLIER);
  }

  @Override
  public boolean isFull3D() {

    return true;
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
  public IIcon getIcon(ItemStack stack, int pass, EntityPlayer player, ItemStack usingItem,
      int useRemaining) {

    return ToolRenderHelper.instance.getIcon(stack, pass, gemId, supercharged, usingItem,
        useRemaining);
  }

  @Override
  public void registerIcons(IIconRegister reg) {

    if (gemId >= 0 && gemId < ToolRenderHelper.HEAD_TYPE_COUNT) {
      itemIcon = ToolRenderHelper.instance.bow0Icons.headM[gemId];
    }
  }

  public int getUsingIndex(ItemStack stack, int useRemaining) {

    int k = getMaxItemUseDuration(stack) - useRemaining;
    float drawSpeed = getDrawDelay(stack);
    if (useRemaining == 0) {
      return 0;
    } else if (k >= (int) (0.9f * drawSpeed)) { // was k >= 18
      return 3;
    } else if (k > (int) (0.65f * drawSpeed)) { // was k > 13
      return 2;
    } else if (k > 0) {
      return 1;
    } else {
      return 0;
    }
  }
  
  public float getDrawDelay(ItemStack stack) {
    
    float f = toolMaterial.getEfficiencyOnProperMaterial();
    return 38.4f - 1.4f * f;
  }

  @Override
  public int getRenderPasses(int meta) {

    return ToolRenderHelper.RENDER_PASS_COUNT;
  }

  @Override
  public boolean requiresMultipleRenderPasses() {

    return true;
  }

  @Override
  public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {

    return ToolHelper.getIsRepairable(stack1, stack2);
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {

    return LocalizationHelper.TOOL_PREFIX + "Bow" + gemId + (supercharged ? "Plus" : "");
  }

  @Override
  public boolean hasEffect(ItemStack stack, int pass) {

    return ToolRenderHelper.instance.hasEffect(stack, pass);
  }

  // Same as vanilla bow, except it can be fired without arrows with infinity.
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

    ArrowNockEvent event = new ArrowNockEvent(player, stack);
    MinecraftForge.EVENT_BUS.post(event);
    if (event.isCanceled()) {
      return event.result;
    }

    if (player.capabilities.isCreativeMode
        || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0
        || player.inventory.hasItem(Items.arrow)) {
      player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
    }

    return stack;
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn,
      int timeLeft) {

    int j = this.getMaxItemUseDuration(stack) - timeLeft;
    net.minecraftforge.event.entity.player.ArrowLooseEvent event = new net.minecraftforge.event.entity.player.ArrowLooseEvent(
        playerIn, stack, j);
    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
      return;
    j = event.charge;

    boolean flag = playerIn.capabilities.isCreativeMode
        || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

    if (flag || playerIn.inventory.hasItem(Items.arrow)) {
      float f = (float) j / getDrawDelay(stack); // was j / 20
      f = (f * f + f * 2.0F) / 3.0F;

      if ((double) f < 0.1D) {
        return;
      }

      if (f > 1.0F) {
        f = 1.0F;
      }

      EntityArrow entityarrow = new EntityArrow(worldIn, playerIn, f * 2.0F);
      entityarrow.setDamage(this.arrowDamage);

      if (f == 1.0F) {
        entityarrow.setIsCritical(true);
      }

      int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

      if (k > 0) {
        entityarrow.setDamage(entityarrow.getDamage() + (double) k * 0.5D + 0.5D);
      }

      int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

      if (l > 0) {
        entityarrow.setKnockbackStrength(l);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
        entityarrow.setFire(100);
      }

      stack.damageItem(1, playerIn);
      worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0F,
          1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

      if (flag) {
        entityarrow.canBePickedUp = 2;
      } else {
        playerIn.inventory.consumeInventoryItem(Items.arrow);
      }

      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      // Shots fired statistics
      ToolHelper.incrementStatShotsFired(stack, 1);

      if (!worldIn.isRemote) {
        worldIn.spawnEntityInWorld(entityarrow);
      }
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
}
