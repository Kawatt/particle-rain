package pigcart.particlerain.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import pigcart.particlerain.ParticleRainClient;

public class RainSheetParticle extends RainDropParticle {

    protected RainSheetParticle(ClientLevel clientWorld, double x, double y, double z, SpriteSet provider) {
        super(clientWorld, x, y, z, provider);
        this.quadSize = ParticleRainClient.config.size.rainSheetSize;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet provider;

        public DefaultFactory(SpriteSet provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new RainSheetParticle(level, x, y, z, this.provider);
        }
    }
}