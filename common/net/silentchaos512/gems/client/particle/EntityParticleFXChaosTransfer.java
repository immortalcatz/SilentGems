package net.silentchaos512.gems.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.core.proxy.ClientProxy;

public class EntityParticleFXChaosTransfer extends EntityFX {

  public static final int MAX_AGE = 20;
  public static final int MAX_SCALE = 6;

  public EntityParticleFXChaosTransfer(World world, double posX, double posY, double posZ) {

    this(world, posX, posY, posZ, 0.0001, 0.0001, 0.0001, 1.0f, 10, 0.0f, 0.2f, 0.8f);
  }

  public EntityParticleFXChaosTransfer(World world, double posX, double posY, double posZ,
      double motionX, double motionY, double motionZ) {

    this(world, posX, posY, posZ, motionX, motionY, motionZ, MAX_SCALE, MAX_AGE, 0.0f, 0.3f, 0.8f);
  }

  public EntityParticleFXChaosTransfer(World world, double posX, double posY, double posZ,
      double motionX, double motionY, double motionZ, float scale, int maxAge, float red,
      float green, float blue) {

    super(world, posX, posY, posZ, 0.0, 0.0, 0.0);
    this.motionX = motionX;
    this.motionY = motionY;
    this.motionZ = motionZ;
    this.particleTextureIndexX = 4;
    this.particleTextureIndexY = 2;
    this.particleRed = red;
    this.particleGreen = green;
    this.particleBlue = blue;
    this.particleScale = scale;
    this.particleMaxAge = maxAge;
    this.noClip = true;
    this.particleGravity = 0.0f;
    this.particleAlpha = 0.2f;
  }

  @Override
  public void onUpdate() {

    if (this.particleAge++ >= this.particleMaxAge - 1) {
      this.setDead();
    }

    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;

    this.moveEntity(this.motionX, this.motionY, this.motionZ);

    this.particleScale -= MAX_SCALE / (MAX_AGE * 1.5f);
    this.particleAlpha += 0.8f * 1f / MAX_AGE;

    // Spawn trail particles
    if (SilentGems.proxy.getParticleSettings() == 0) {
      double mx = worldObj.rand.nextGaussian() * 0.025;
      double my = worldObj.rand.nextGaussian() * 0.025;
      double mz = worldObj.rand.nextGaussian() * 0.025;
      SilentGems.proxy.spawnParticles(ClientProxy.FX_CHAOS_TRAIL, worldObj, posX, posY, posZ, mx,
          my, mz);
    }
  }

  @Override
  public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks,
      float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_,
      float p_180434_8_) {

    float uMin = (float) this.particleTextureIndexX / 16.0F;
    float uMax = uMin + .25f;
    float vMin = (float) this.particleTextureIndexY / 16.0F;
    float vMax = vMin + .25f;
    float f4 = 0.1F * this.particleScale;

    if (this.particleIcon != null) {
      uMin = this.particleIcon.getMinU();
      uMax = this.particleIcon.getMaxU();
      vMin = this.particleIcon.getMinV();
      vMax = this.particleIcon.getMaxV();
    }

    float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) posX - interpPosX);
    float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) posX - interpPosY);
    float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) posX - interpPosZ);

    int i = this.getBrightnessForRender(partialTicks);
    int j = i >> 16 & 65535;
    int k = i & 65535;

    worldRendererIn
        .pos((double) (f5 - p_180434_4_ * f4 - p_180434_7_ * f4), (double) (f6 - p_180434_5_ * f4),
            (double) (f7 - p_180434_6_ * f4 - p_180434_8_ * f4))
        .tex((double) uMax, (double) vMax)
        .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
        .lightmap(j, k).endVertex();
    worldRendererIn
        .pos((double) (f5 - p_180434_4_ * f4 + p_180434_7_ * f4), (double) (f6 + p_180434_5_ * f4),
            (double) (f7 - p_180434_6_ * f4 + p_180434_8_ * f4))
        .tex((double) uMax, (double) vMin)
        .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
        .lightmap(j, k).endVertex();
    worldRendererIn
        .pos((double) (f5 + p_180434_4_ * f4 + p_180434_7_ * f4), (double) (f6 + p_180434_5_ * f4),
            (double) (f7 + p_180434_6_ * f4 + p_180434_8_ * f4))
        .tex((double) uMin, (double) vMin)
        .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
        .lightmap(j, k).endVertex();
    worldRendererIn
        .pos((double) (f5 + p_180434_4_ * f4 - p_180434_7_ * f4), (double) (f6 - p_180434_5_ * f4),
            (double) (f7 + p_180434_6_ * f4 - p_180434_8_ * f4))
        .tex((double) uMin, (double) vMax)
        .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
        .lightmap(j, k).endVertex();
  }
}
