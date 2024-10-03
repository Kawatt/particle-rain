package pigcart.particlerain.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import pigcart.particlerain.ParticleRainClient;

public class PetalParticle extends WeatherParticle {

    //float amountToRotateBy;
    private float rollSpeed;
    private float yawSpeed;
    private final float particleRandom;
    private final float rotAcceleration;
    private final float yawAcceleration;

    protected float yaw;
    protected float oYaw;

    protected int groundLifetime;

    protected PetalParticle(ClientLevel level, double x, double y, double z, SpriteSet provider) {
        super(level, x, y, z, ParticleRainClient.config.petalGravity, provider);
        this.setSize(0.1F, 0.1F);

        this.xd = level.getRandom().nextFloat()/ParticleRainClient.config.petalWindDampening;
        this.zd = level.getRandom().nextFloat()/ParticleRainClient.config.petalWindDampening;

        this.groundLifetime = ParticleRainClient.config.petalGroundLifetime;

        this.particleRandom = this.random.nextFloat();
        this.rotAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -ParticleRainClient.config.petalRotationAmount : ParticleRainClient.config.petalRotationAmount);
        this.yawAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -ParticleRainClient.config.petalRotationAmount : ParticleRainClient.config.petalRotationAmount);
    }

    public void tick() {
        super.tick();

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        float f = (float)(300 - this.lifetime);
        float g = Math.min(f / 300.0F, 1.0F);
        double d = Math.cos(Math.toRadians((double)(this.particleRandom * 60.0F))) * 2.0 * Math.pow((double)g, 1.25);
        double e = Math.sin(Math.toRadians((double)(this.particleRandom * 60.0F))) * 2.0 * Math.pow((double)g, 1.25);
        this.xd += d * 0.0024999999441206455;
        this.zd += e * 0.0024999999441206455;
        this.move(this.xd, this.yd, this.zd);
        if (this.onGround || this.removeIfObstructed()) {
            this.groundLifetime--;

            this.oRoll = this.roll;
            this.roll = Mth.HALF_PI /*+ Mth.HALF_PI/8*/;
            this.oYaw = this.yaw;
            this.yaw = 0;
        }
        else {
            this.rollSpeed += this.rotAcceleration / 20.0F;
            this.yawSpeed += this.yawAcceleration / 20.0F;
            this.oRoll = this.roll;
            this.roll += this.rollSpeed;
            this.oYaw = this.yaw;
            this.yaw += this.yawSpeed;
        }
        if (groundLifetime <= 0) {
            this.remove();
        }

        if (!this.removed) {
            this.xd *= (double)this.friction;
            this.yd *= (double)this.friction;
            this.zd *= (double)this.friction;
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickPercentage) {

        Quaternionf[] faceArray = new Quaternionf[] {
                new Quaternionf(new AxisAngle4f(0, 0, 1, 0)),
                new Quaternionf(new AxisAngle4f(Mth.PI, 0, 1, 0))
        };
        for (Quaternionf quaternion : faceArray) {
            quaternion.rotateLocalX(Mth.lerp(tickPercentage, this.oRoll, this.roll));
            quaternion.rotateLocalY(Mth.lerp(tickPercentage, this.oYaw, this.yaw));

            Vec3 camPos = camera.getPosition();
            float x = (float)(Mth.lerp((double)tickPercentage, this.xo, this.x) - camPos.x());
            float y = (float)(Mth.lerp((double)tickPercentage, this.yo, this.y) - camPos.y()) + 0.01f;
            float z = (float)(Mth.lerp((double)tickPercentage, this.zo, this.z) - camPos.z());

            float quadSize = this.getQuadSize(tickPercentage);
            float u0 = this.getU0();
            float u1 = this.getU1();
            float v0 = this.getV0();
            float v1 = this.getV1();
            int lightColor = this.getLightColor(tickPercentage);

            Vector3f[] vector3fs = new Vector3f[]{
                    new Vector3f(-1.0F, -1.0F, 0.0F),
                    new Vector3f(-1.0F, 1.0F, 0.0F),
                    new Vector3f(1.0F, 1.0F, 0.0F),
                    new Vector3f(1.0F, -1.0F, 0.0F)};

            for(int k = 0; k < 4; ++k) {
                Vector3f vector3f = vector3fs[k];
                vector3f.rotate(quaternion);
                vector3f.mul(quadSize);
                vector3f.add(x, y, z);
            }

            vertexConsumer.vertex((double)vector3fs[0].x(), (double)vector3fs[0].y(), (double)vector3fs[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
            vertexConsumer.vertex((double)vector3fs[1].x(), (double)vector3fs[1].y(), (double)vector3fs[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
            vertexConsumer.vertex((double)vector3fs[2].x(), (double)vector3fs[2].y(), (double)vector3fs[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
            vertexConsumer.vertex((double)vector3fs[3].x(), (double)vector3fs[3].y(), (double)vector3fs[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet provider;

        public DefaultFactory(SpriteSet provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new PetalParticle(level, x, y, z, this.provider);
        }
    }
}
