package pigcart.particlerain;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "particlerain")
public class ModConfig implements ConfigData {

    public int particleDensity = 100;
    public int particleStormDensity = 200;
    public int particleRadius = 30;
    public float rainDropGravity = 1.0F;
    public float snowFlakeGravity = 0.1F;
    public float snowRotationAmount = 0.03F;
    public float snowWindDampening = 1.5F;
    public float petalGravity = 0.05F;
    public float petalRotationAmount = 1F;
    public float petalWindDampening = 1.5F;
    public int petalGroundLifetime = 400;
    public float desertDustGravity = 0.2F;
    public boolean doRainParticles = true;
    public boolean doSnowParticles = true;
    public boolean doSandParticles = true;
    public boolean doShrubParticles = true;
    public boolean doPetalParticles = true;
    public boolean renderVanillaWeather = false;
    public boolean doExperimentalFog = false;
    public boolean alwaysRaining = false;


    @ConfigEntry.Gui.CollapsibleObject
    public ParticleColors color = new ParticleColors();

    public static class ParticleColors {
        public float rainRed = 0.5F;
        public float rainGreen = 0.5F;
        public float rainBlue = 1.0F;
        public float snowRed = 1.0F;
        public float snowGreen = 1.0F;
        public float snowBlue = 1.0F;
        public float desertRed = 0.9F;
        public float desertGreen = 0.8F;
        public float desertBlue = 0.6F;
        public float mesaRed = 0.8F;
        public float mesaGreen = 0.4F;
        public float mesaBlue = 0.0F;
    }

    @ConfigEntry.Gui.CollapsibleObject
    public ParticleSizes size = new ParticleSizes();

    public static class ParticleSizes {
        public float rainDropSize = 0.5F;
        public float rainSheetSize = 2F;
        public float snowFlakeSize = 0.1F;
        public float snowSheetSize = 2F;
        public float dustMoteSize = 0.1F;
        public float dustSheetSize = 1.5F;
        public float fogSize = 8F;
        public float petalSize = 0.1F;
    }
}