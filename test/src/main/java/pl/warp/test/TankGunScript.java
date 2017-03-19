package pl.warp.test;

import com.badlogic.gdx.math.Vector3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import pl.warp.engine.core.scene.Component;
import pl.warp.engine.core.scene.properties.TransformProperty;
import pl.warp.engine.core.scene.properties.Transforms;
import pl.warp.engine.graphics.material.GraphicsMaterialProperty;
import pl.warp.engine.graphics.material.Material;
import pl.warp.engine.graphics.mesh.Mesh;
import pl.warp.engine.graphics.mesh.RenderableMeshProperty;
import pl.warp.engine.graphics.particles.*;
import pl.warp.engine.graphics.particles.animator.DirectionalAccelerationAnimator;
import pl.warp.engine.graphics.particles.dot.DotParticle;
import pl.warp.engine.graphics.particles.dot.DotParticleAttribute;
import pl.warp.engine.graphics.particles.dot.ParticleStage;
import pl.warp.engine.physics.collider.PointCollider;
import pl.warp.engine.physics.property.ColliderProperty;
import pl.warp.engine.physics.property.PhysicalBodyProperty;
import pl.warp.game.scene.GameComponent;
import pl.warp.game.scene.GameSceneComponent;
import pl.warp.game.script.GameScript;
import pl.warp.game.script.OwnerProperty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Hubertus
 *         Created 03.03.17
 */
public class TankGunScript extends GameScript<GameComponent> {


    private static ScheduledExecutorService es = Executors.newScheduledThreadPool(5);
    private final int reloadTime;
    private final float outSpeed;
    private Component root;
    @OwnerProperty(name = GunProperty.GUN_PROPERTY_NAME)
    GunProperty gunProperty;
    private static final Vector3f FORWARD_VECTOR = new Vector3f(0, 0, 1);
    private static final Vector3f GUN_OFFSET = new Vector3f(0, 0, 0);
    private Mesh mesh;
    private Material material;

    private int reloadLeft = 0;
    private ParticleSystem smokeSystem;
    private ParticleSystem fireSystem;

    public TankGunScript(GameComponent owner, int reloadTime, float outSpeed, Component root) {
        super(owner);
        this.reloadTime = reloadTime;
        this.outSpeed = outSpeed;
        this.root = root;
    }

    @Override
    protected void init() {
        createGunParticles(getOwner());
        this.mesh = gunProperty.getBulletMesh();
        this.material = new Material(gunProperty.getBulletTexture());
    }

    @Override
    protected void update(int delta) {
        reload(delta);
        input();
    }

    private void reload(int delta) {
        if (reloadLeft > 0) reloadLeft -= delta;
    }

    private void input() {
        if (gunProperty.isTriggered()) shoot();
    }

    private Vector3f forwardVector = new Vector3f();
    private Vector3f translation = new Vector3f();
    private Vector3f gunOffset = new Vector3f();
    private Vector3 roundTranslation = new Vector3();

    private void shoot() {
        if (reloadLeft <= 0) {
            reloadLeft = reloadTime;
            Quaternionf rotation = Transforms.getAbsoluteRotation(getOwner());
            rotation.transform(FORWARD_VECTOR, forwardVector);
            rotation.transform(GUN_OFFSET, gunOffset);
            Transforms.getAbsolutePosition(getOwner(), translation);
            translation.add(gunOffset);
            GameComponent round = new GameSceneComponent(getContext());
            TransformProperty transformProperty = new TransformProperty();
            transformProperty.move(translation);
            round.addProperty(transformProperty);
            forwardVector.mul(outSpeed);
            PhysicalBodyProperty bodyProperty = new PhysicalBodyProperty(1, 1, 1, 1);
            bodyProperty.applyForce(forwardVector);
            round.addProperty(bodyProperty);
            round.addProperty(new ColliderProperty(new PointCollider(round, roundTranslation.set(translation.x, translation.y, translation.z))));
            //round.addProperty(new GravityProperty(new Vector3f(0, -1, 0)));
            round.addProperty(new RenderableMeshProperty(mesh));
            round.addProperty(new GraphicsMaterialProperty(material));
            root.addChild(round);
            new TankRoundScript(round);
            fireSystem.setEmit(true);
            smokeSystem.setEmit(true);
            es.schedule(() -> {
                fireSystem.setEmit(false);
                smokeSystem.setEmit(false);
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    private void createGunParticles(GameComponent mainGun) {
        GameComponent firedSmoke = new GameSceneComponent(mainGun);
        TransformProperty firedSmokeTransformProperty = new TransformProperty();
        firedSmokeTransformProperty.move(new Vector3f(0.01f, -0.04f, 2.7f));
        firedSmoke.addProperty(firedSmokeTransformProperty);
        ParticleAnimator firedSmokeAnimator = new DirectionalAccelerationAnimator(new Vector3f(0, 0, 0.00002f));
        ParticleStage[] firedSmokeStages = {
                new ParticleStage(0f, new Vector4f(0.5f, 0.5f, 0.5f, 0.3f)),
                new ParticleStage(2f, new Vector4f(0.5f, 0.5f, 0.5f, 0.0f)),
        };
        ParticleEmitter<DotParticle> emitter = new SpreadingParticleEmitter<>(300, new Vector3f(0), new Vector3f(0.005f), 300, 200, true);
        smokeSystem = new ParticleSystem(new DotParticleAttribute(firedSmokeStages), emitter, firedSmokeAnimator);
        firedSmoke.addProperty(new ParticleEmitterProperty(smokeSystem));

        GameComponent firedFlash = new GameSceneComponent(mainGun);
        TransformProperty firedFlashTransformProperty = new TransformProperty();
        firedFlashTransformProperty.move(new Vector3f(0.01f, -0.04f, 2.7f));
        firedFlash.addProperty(firedFlashTransformProperty);
        ParticleStage[] firedFlashStages = {
                new ParticleStage(1.0f, new Vector4f(1.0f, 0.6f, 0.5f, 1.0f)),
                new ParticleStage(1.0f, new Vector4f(1.0f, 0.6f, 0.3f, 0.0f))
        };
        ParticleEmitter firedFlashEmitter = new SpreadingParticleEmitter(200, new Vector3f(0), new Vector3f(0.006f), 200, 100, true);
        fireSystem = new ParticleSystem(new DotParticleAttribute(firedFlashStages), firedFlashEmitter);
        firedFlash.addProperty(new ParticleEmitterProperty(fireSystem));

        fireSystem.setEmit(false);
        smokeSystem.setEmit(false);
    }
}