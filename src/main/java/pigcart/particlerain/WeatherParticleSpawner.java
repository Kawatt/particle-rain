package pigcart.particlerain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

public final class WeatherParticleSpawner {

    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private WeatherParticleSpawner() {
    }
    private static String printBiome(Holder<Biome> holder) {
        return holder.unwrap().map((resourceKey) -> {
            return resourceKey.location().toString();
        }, (biome) -> {
            return "[unregistered " + biome + "]";
        });
    }

    private static void spawnParticle(ClientLevel level, Holder<Biome> biome, double x, double y, double z) {
        if (ParticleRainClient.config.doExperimentalFog) {
            if (level.random.nextFloat() < 0.3) {
                level.addParticle(ParticleRainClient.FOG, x, y, z, 0, 0, 0);
            }
        }
        Precipitation precipitation = biome.value().getPrecipitationAt(pos);
        if (biome.value().hasPrecipitation()) {
            if (precipitation == Precipitation.RAIN) {
                if (ParticleRainClient.config.doRainParticles) {
                    if (ParticleRainClient.config.doPetalParticles && printBiome(biome).contains("cherry")) {
                        level.addParticle(ParticleRainClient.PETAL, x, y, z, 0, 0, 0);
                    }
                    else {
                        if (y < Minecraft.getInstance().cameraEntity.yo + ( 4 * (ParticleRainClient.config.particleRadius / 5)) && level.random.nextBoolean()) {
                            level.addParticle(ParticleRainClient.RAIN_SHEET, x, y, z, 0, 0, 0);
                        } else {
                            level.addParticle(ParticleRainClient.RAIN_DROP, x, y, z, 0, 0, 0);
                        }
                    }
                }
            } else if (precipitation == Precipitation.SNOW) {
                if (ParticleRainClient.config.doSnowParticles) {
                    if (level.isThundering() && level.random.nextFloat() < 0.3) {
                        level.addParticle(ParticleRainClient.SNOW_SHEET, x, y, z, 0, 0, 0);
                    }
                    else if (level.random.nextFloat() < 0.8) {
                        level.addParticle(ParticleRainClient.SNOW_FLAKE, x, y, z, 0, 0, 0);
                    }
                }
            }
        } else if (ParticleRainClient.config.doSandParticles) {
            // printBiome offers some basic out of the box support for modded desert biomes (at least ones with descriptive names)
            if (printBiome(biome).contains("desert") && biome.value().getBaseTemperature() >= 1.0F || biome.is(BiomeTags.IS_BADLANDS)) {
                if (level.random.nextFloat() < 0.8F) {
                    if (level.random.nextBoolean()) {
                        level.addParticle(ParticleRainClient.DUST_SHEET, x, y, z, 0, 0, 0);
                    } else {
                        level.addParticle(ParticleRainClient.DUST_MOTE, x, y, z, 0, 0, 0);
                    }
                }
            }
        } if (ParticleRainClient.config.doShrubParticles) {
            if (printBiome(biome).contains("desert") && biome.value().getBaseTemperature() >= 1.0F || biome.is(BiomeTags.IS_BADLANDS)) {
                if (level.random.nextFloat() < 0.005F) {
                    level.addParticle(ParticleRainClient.DEAD_BUSH, x, y, z, 0, 0, 0);
                }
            }
        }
    }

    public static void update(ClientLevel level, Entity entity, float partialTicks) {
        if (level.isRaining() || ParticleRainClient.config.alwaysRaining) {
            int density;
            if (level.isThundering())
                if (ParticleRainClient.config.alwaysRaining) {
                    density = ParticleRainClient.config.particleStormDensity;
                } else {
                    density = (int) (ParticleRainClient.config.particleStormDensity * level.getRainLevel(partialTicks));
                }
            else if (ParticleRainClient.config.alwaysRaining) {
                density = ParticleRainClient.config.particleDensity;
            } else {
                density = (int) (ParticleRainClient.config.particleDensity * level.getRainLevel(partialTicks));
            }


            RandomSource rand = RandomSource.create();

            for (int pass = 0; pass < density; pass++) {

                float theta = (float) (2 * Math.PI * rand.nextFloat());
                float phi = (float) Math.acos(2 * rand.nextFloat() - 1);
                double x = ParticleRainClient.config.particleRadius * Mth.sin(phi) * Math.cos(theta);
                double y = ParticleRainClient.config.particleRadius * Mth.sin(phi) * Math.sin(theta);
                double z = ParticleRainClient.config.particleRadius * Mth.cos(phi);

                pos.set(x + entity.getX(), y + entity.getY(), z + entity.getZ());
                if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) > pos.getY())
                    continue;

                spawnParticle(level, level.getBiome(pos), pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat());
            }
        }
    }

    @Nullable
    public static SoundEvent getBiomeSound(BlockPos blockPos, boolean above) {
        Holder<Biome> biome = Minecraft.getInstance().level.getBiome(blockPos);
        if (biome.value().hasPrecipitation()) {
            if (biome.value().getPrecipitationAt(blockPos) == Precipitation.RAIN) {
                return above ? SoundEvents.WEATHER_RAIN_ABOVE : SoundEvents.WEATHER_RAIN;
            } else if (biome.value().getPrecipitationAt(blockPos) == Precipitation.SNOW) {
                return above ? ParticleRainClient.WEATHER_SNOW_ABOVE : ParticleRainClient.WEATHER_SNOW;
            }
        } else if (biome.is(Biomes.DESERT) || biome.is(BiomeTags.IS_BADLANDS)) {
            return above ? ParticleRainClient.WEATHER_SANDSTORM_ABOVE : ParticleRainClient.WEATHER_SANDSTORM;
        }
        return null;
    }
}