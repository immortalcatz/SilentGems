package net.silentchaos512.gems.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentFireAspect;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;

public class EnchantmentLightningAspect extends Enchantment {

  public static final String NAME = "LightningAspect";
  public static final String EFFECT_NBT = NAME + "_timer";
  public static final int EFFECT_DURATION = 120;
  public static final int EFFECT_AMPLIFIER = 1;
  public static final int CONTINUOUS_DAMAGE_DELAY = 15;
  public static final int CONTINUOUS_DAMAGE_AMOUNT = 1;
  public static final int CHAIN_DELAY = 5;
  public static final double CHAIN_RADIUS_SQUARED = 2.5 * 2.5;

  public EnchantmentLightningAspect() {

    super(Rarity.RARE, EnumEnchantmentType.WEAPON,
        new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
    setName(NAME);
  }

  @Override
  public boolean canApplyTogether(Enchantment ench) {

    return !(ench instanceof EnchantmentFireAspect) && !(ench instanceof EnchantmentIceAspect)
        && super.canApplyTogether(ench);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {

    Item item = stack.getItem();
    return item instanceof ItemSword;
  }

  @Override
  public int getMinEnchantability(int level) {

    return 10 + 20 * (level - 1);
  }

  @Override
  public int getMaxEnchantability(int level) {

    return getMinEnchantability(level) + 50;
  }

  @Override
  public int getMaxLevel() {

    return 2;
  }

  @Override
  public String getName() {

    return "enchantment.silentgems:" + NAME;
  }

  /**
   * Apply effect to mob. Called in GemsCommonEvents#onLivingAttack. Also see GemsCommonEvents#onLivingUpdate.
   * 
   * @param entityLiving
   * @param enchLevel
   */
  public void applyTo(EntityLivingBase entityLiving, int enchLevel, int duration) {

    int amplifier = getEffectAmplifier(entityLiving, enchLevel);
    entityLiving.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, duration, amplifier, true, false));
    entityLiving.getEntityData().setInteger(EFFECT_NBT, duration);
  }

  public void applyTo(EntityLivingBase entityLiving, int enchLevel) {

    applyTo(entityLiving, enchLevel, getEffectDuration(entityLiving, enchLevel));
  }

  public int getEffectDuration(EntityLivingBase entityLiving, int enchLevel) {

    int ret = EFFECT_DURATION + (enchLevel - 1) * EFFECT_DURATION / 2;
    if (entityLiving instanceof EntityPlayer)
      ret /= 2;
    return ret;
  }

  public int getEffectAmplifier(EntityLivingBase entityLiving, int enchLevel) {

    int ret = EFFECT_AMPLIFIER * enchLevel;
    if (entityLiving instanceof EntityPlayer)
      ret /= 2;
    return ret;
  }
}
