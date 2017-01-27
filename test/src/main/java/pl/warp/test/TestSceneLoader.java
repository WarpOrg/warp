package pl.warp.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import pl.warp.engine.ai.behaviourTree.SequenceNode;
import pl.warp.engine.ai.loader.BehaviourTreeBuilder;
import pl.warp.engine.ai.loader.BehaviourTreeLoader;
import pl.warp.engine.ai.property.AIProperty;
import pl.warp.engine.audio.AudioManager;
import pl.warp.engine.core.EngineThread;
import pl.warp.engine.core.SimpleEngineTask;
import pl.warp.engine.core.scene.Component;
import pl.warp.engine.core.scene.NameProperty;
import pl.warp.engine.core.scene.Scene;
import pl.warp.engine.core.scene.SimpleComponent;
import pl.warp.engine.core.scene.properties.TransformProperty;
import pl.warp.engine.core.scene.script.ScriptManager;
import pl.warp.engine.graphics.GraphicsSceneLoader;
import pl.warp.engine.graphics.RenderingConfig;
import pl.warp.engine.graphics.camera.Camera;
import pl.warp.engine.graphics.camera.QuaternionCamera;
import pl.warp.engine.graphics.light.LightProperty;
import pl.warp.engine.graphics.light.SpotLight;
import pl.warp.engine.graphics.material.GraphicsMaterialProperty;
import pl.warp.engine.graphics.material.Material;
import pl.warp.engine.graphics.math.projection.PerspectiveMatrix;
import pl.warp.engine.graphics.mesh.GraphicsCustomRendererProgramProperty;
import pl.warp.engine.graphics.mesh.GraphicsMeshProperty;
import pl.warp.engine.graphics.mesh.Mesh;
import pl.warp.engine.graphics.mesh.shapes.Ring;
import pl.warp.engine.graphics.mesh.shapes.Sphere;
import pl.warp.engine.graphics.particles.GraphicsParticleEmitterProperty;
import pl.warp.engine.graphics.particles.ParticleAnimator;
import pl.warp.engine.graphics.particles.ParticleFactory;
import pl.warp.engine.graphics.particles.SimpleParticleAnimator;
import pl.warp.engine.graphics.particles.textured.RandomSpreadingTexturedParticleFactory;
import pl.warp.engine.graphics.particles.textured.TexturedParticle;
import pl.warp.engine.graphics.particles.textured.TexturedParticleSystem;
import pl.warp.engine.graphics.postprocessing.lens.GraphicsLensFlareProperty;
import pl.warp.engine.graphics.postprocessing.lens.LensFlare;
import pl.warp.engine.graphics.postprocessing.lens.SingleFlare;
import pl.warp.engine.graphics.resource.mesh.ObjLoader;
import pl.warp.engine.graphics.resource.texture.ImageData;
import pl.warp.engine.graphics.resource.texture.ImageDataArray;
import pl.warp.engine.graphics.resource.texture.ImageDecoder;
import pl.warp.engine.graphics.resource.texture.PNGDecoder;
import pl.warp.engine.graphics.skybox.GraphicsSkyboxProperty;
import pl.warp.engine.graphics.texture.Cubemap;
import pl.warp.engine.graphics.texture.Texture1D;
import pl.warp.engine.graphics.texture.Texture2D;
import pl.warp.engine.graphics.texture.Texture2DArray;
import pl.warp.engine.graphics.window.Display;
import pl.warp.engine.physics.property.PhysicalBodyProperty;
import pl.warp.game.GameContextBuilder;
import pl.warp.test.program.gas.GasPlanetProgram;
import pl.warp.test.program.ring.PlanetaryRingProgram;
import pl.warp.test.program.ring.PlanetaryRingProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * @author Jaca777
 *         Created 2017-01-22 at 11
 */
public class TestSceneLoader implements GraphicsSceneLoader {

    private static final float ROT_SPEED = 0.05f;
    private static final float MOV_SPEED = 0.2f * 10;
    private static final float BRAKING_FORCE = 0.2f * 10;
    private static final float ARROWS_ROTATION_SPEED = 2f;
    private static final int GUN_COOLDOWN = 5;

    private boolean loaded = false;

    private RenderingConfig config;
    private GameContextBuilder contextBuilder;
    private Scene scene;
    private Component controllableGoat;
    private Camera camera;
    private Mesh bulletMesh;
    private Texture2DArray boomSpritesheet;
    private Texture2D bulletTexture;
    private AudioManager audioManager;


    public TestSceneLoader(RenderingConfig config, GameContextBuilder contextBuilder) {
        this.config = config;
        this.contextBuilder = contextBuilder;
    }

    @Override
    public void loadScene() {

        try {
            unpackResources();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scene = new Scene(contextBuilder.getGameContext());
        scene.addProperty(new NameProperty("Test universe"));
        contextBuilder.setScene(scene);

        contextBuilder.setScriptManager(new ScriptManager());

        controllableGoat = new SimpleComponent(scene);
        controllableGoat.addProperty(new NameProperty("Player ship"));

        Display display = config.getDisplay();

        camera = new QuaternionCamera(controllableGoat, new PerspectiveMatrix(70, 0.01f, 20000f, display.getWidth(), display.getHeight()));
        camera.addProperty(new NameProperty("Camera"));
        camera.move(new Vector3f(0, 4f, 15f));

        loaded = true;
    }

    private static void unpackResources() throws IOException {
        String path = Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File jarFile = new File(decodedPath);
        String parent = jarFile.getParent();
        String dataPath = parent + File.separator + "data";
        File dataDir = new File(dataPath);
        if (dataDir.exists()) FileUtils.deleteDirectory(dataDir);
        copy(dataPath + "" + File.separator + "sound" + File.separator + "effects" + File.separator + "gun.wav", "sound/effects/gun.wav");
        copy(dataPath + "" + File.separator + "sound" + File.separator + "music" + File.separator + "Stellardrone-Light_Years-01_Red_Giant.wav", "sound/music/Stellardrone-Light_Years-01_Red_Giant.wav");
        copy(dataPath + "" + File.separator + "sound" + File.separator + "music" + File.separator + "Stellardrone-Light_Years-05_In_Time.wav", "sound/music/Stellardrone-Light_Years-05_In_Time.wav");
        copy(dataPath + "" + File.separator + "ai" + File.separator + "droneAI.xml", "ai/droneAI.xml");

    }

    private static void copy(String decodedPath, String name) throws IOException {
        FileOutputStream fileOutputStream4 = getFileOutputStream(decodedPath);
        InputStream ai = Test.class.getResourceAsStream(name);
        IOUtils.copy(ai, fileOutputStream4);
    }


    private static FileOutputStream getFileOutputStream(String decodedPath) throws IOException {
        File file = new File(decodedPath);
        new File(file.getParent()).mkdirs();
        file.createNewFile();
        return new FileOutputStream(file);
    }

    @Override
    public void loadGraphics(EngineThread graphicsThread) {
        graphicsThread.scheduleOnce(() -> {
            Mesh goatMesh = ObjLoader.read(Test.class.getResourceAsStream("fighter_1.obj"), false).toVAOMesh();
            ImageData decodedTexture = ImageDecoder.decodePNG(Test.class.getResourceAsStream("fighter_1.png"), PNGDecoder.Format.RGBA);
            Texture2D goatTexture = new Texture2D(decodedTexture.getWidth(), decodedTexture.getHeight(), GL11.GL_RGBA, GL11.GL_RGBA, true, decodedTexture.getData());


            ImageDataArray lensSpritesheet = ImageDecoder.decodeSpriteSheetReverse(Test.class.getResourceAsStream("lens_flares.png"), PNGDecoder.Format.RGBA, 2, 1);
            Texture2DArray lensTexture = new Texture2DArray(lensSpritesheet.getWidth(), lensSpritesheet.getHeight(), lensSpritesheet.getArraySize(), lensSpritesheet.getData());
            SingleFlare[] flares = new SingleFlare[]{
                    new SingleFlare(0.75f, 0, 0.08f, new Vector3f(1)),
                    new SingleFlare(0.1f, 1, 0.02f, new Vector3f(1)),
                    new SingleFlare(-0.2f, 0, 0.06f, new Vector3f(1)),
                    new SingleFlare(-0.4f, 1, 0.08f, new Vector3f(1)),
                    new SingleFlare(-0.5f, 1, 0.05f, new Vector3f(1)),
                    new SingleFlare(-0.6f, 1, 0.08f, new Vector3f(1)),
                    new SingleFlare(-0.4f, 1, 0.25f, new Vector3f(1)),
                    new SingleFlare(-0.1f, 1, 0.2f, new Vector3f(1)),
                    new SingleFlare(0.2f, 1, 0.25f, new Vector3f(1)),
                    new SingleFlare(0.6f, 1, 0.25f, new Vector3f(1f)),
            };

            ImageDataArray lightSpritesheet = ImageDecoder.decodeSpriteSheetReverse(Test.class.getResourceAsStream("boom_spritesheet.png"), PNGDecoder.Format.RGBA, 4, 4);
            Texture2DArray lightSpritesheetTexture = new Texture2DArray(lightSpritesheet.getWidth(), lightSpritesheet.getHeight(), lightSpritesheet.getArraySize(), lightSpritesheet.getData());
            Component light = new SimpleComponent(scene);
            LightProperty property = new LightProperty();
            light.addProperty(property);
            SpotLight spotLight = new SpotLight(light, new Vector3f(0f, 0f, 0f), new Vector3f(2f, 2f, 2f).mul(4), new Vector3f(0.6f, 0.6f, 0.6f), 0.1f, 0.1f);
            property.addSpotLight(spotLight);
            TransformProperty lightSourceTransform = new TransformProperty();
            light.addProperty(lightSourceTransform);
            lightSourceTransform.move(new Vector3f(1500f, 40000f, 0f));
            ParticleAnimator animator = new SimpleParticleAnimator(new Vector3f(0), 0, 0);
            ParticleFactory<TexturedParticle> factory = new RandomSpreadingTexturedParticleFactory(0.008f, 1000, true, true);
            light.addProperty(new GraphicsParticleEmitterProperty(new TexturedParticleSystem(animator, factory, 1000, lightSpritesheetTexture)));
            LensFlare flare = new LensFlare(lensTexture, flares);
            new GraphicsLensFlareProperty(flare);

            Component gasSphere = new SimpleComponent(scene);
            Sphere sphere = new Sphere(50, 50);
            gasSphere.addProperty(new GraphicsMeshProperty(sphere));
            ImageData decodedColorsTexture = ImageDecoder.decodePNG(Test.class.getResourceAsStream("gas.png"), PNGDecoder.Format.RGBA);
            Texture1D colorsTexture = new Texture1D(decodedColorsTexture.getWidth(), GL11.GL_RGBA, GL11.GL_RGBA, false, decodedColorsTexture.getData());
            GasPlanetProgram gasProgram = new GasPlanetProgram(colorsTexture);
            gasSphere.addProperty(new GraphicsCustomRendererProgramProperty(gasProgram));
            TransformProperty gasSphereTransform = new TransformProperty();
            gasSphereTransform.move(new Vector3f(-1200f, -200f, -500f));
            gasSphereTransform.scale(new Vector3f(1000.0f));
            gasSphere.addProperty(gasSphereTransform);
            graphicsThread.scheduleTask(new SimpleEngineTask() {
                @Override
                public void update(int delta) {
                    gasProgram.use();
                    gasProgram.update(delta);
                    gasSphereTransform.rotateLocalY(delta * 0.00001f);
                }
            });

            float startR = 1.5f;
            float endR = 2.5f;
            Component ring = new SimpleComponent(gasSphere);
            Ring ringMesh = new Ring(20, startR, endR);
            ring.addProperty(new GraphicsMeshProperty(ringMesh));
            ring.addProperty(new GraphicsCustomRendererProgramProperty(new PlanetaryRingProgram()));
            ImageData ringColorsData = ImageDecoder.decodePNG(Test.class.getResourceAsStream("ring_colors.png"), PNGDecoder.Format.RGBA);
            Texture1D ringColors = new Texture1D(ringColorsData.getWidth(), GL11.GL_RGBA, GL11.GL_RGBA, true, ringColorsData.getData());
            ringColors.enableAnisotropy(4);
            ring.addProperty(new PlanetaryRingProperty(startR, endR, ringColors));

            ImageData brightnessTextureData = ImageDecoder.decodePNG(Test.class.getResourceAsStream("fighter_1_brightness.png"), PNGDecoder.Format.RGBA);
            Texture2D brightnessTexture = new Texture2D(brightnessTextureData.getWidth(), brightnessTextureData.getHeight(), GL11.GL_RGBA, GL11.GL_RGBA, true, brightnessTextureData.getData());


            /*Mesh floorMesh = ObjLoader.read(Test.class.getResourceAsStream("floor.obj"), false).toVAOMesh();
            ImageData decodedFloorTexture = ImageDecoder.decodePNG(Test.class.getResourceAsStream("floor_1.png"), PNGDecoder.Format.RGBA);
            Texture2D floorTexture = new Texture2D(decodedFloorTexture.getWidth(), decodedFloorTexture.getHeight(), GL11.GL_RGBA, GL11.GL_RGBA, true, decodedFloorTexture.getData());
            Material floorMaterial = new Material(floorTexture);
            Component floor = new SimpleComponent(scene);
            new GraphicsMeshProperty(floor, floorMesh);
            new GraphicsMaterialProperty(floor,floorMaterial);
            new TransformProperty(floor);
            new PhysicalBodyProperty(floor, 100, 100, 100, 100);
*/

            ImageDataArray spritesheet = ImageDecoder.decodeSpriteSheetReverse(Test.class.getResourceAsStream("boom_spritesheet.png"), PNGDecoder.Format.RGBA, 4, 4);
            boomSpritesheet = new Texture2DArray(spritesheet.getWidth(), spritesheet.getHeight(), spritesheet.getArraySize(), spritesheet.getData());

            GraphicsMeshProperty graphicsMeshProperty = new GraphicsMeshProperty(goatMesh);
            controllableGoat.addProperty(graphicsMeshProperty);

            controllableGoat.addProperty(new PhysicalBodyProperty(10f, 10.772f / 2, 1.8f / 2, 13.443f / 2));
            Material material = new Material(goatTexture);
            material.setBrightnessTexture(brightnessTexture);
            controllableGoat.addProperty(new GraphicsMaterialProperty(material));
            controllableGoat.addProperty(new TransformProperty());

            bulletMesh = new Sphere(15, 15, 0.5f);
            ImageData bulletDecodedTexture = ImageDecoder.decodePNG(Test.class.getResourceAsStream("bullet.png"), PNGDecoder.Format.RGBA);
            bulletTexture = new Texture2D(bulletDecodedTexture.getWidth(), bulletDecodedTexture.getHeight(), GL11.GL_RGBA, GL11.GL_RGBA, true, bulletDecodedTexture.getData());

            audioManager = AudioManager.INSTANCE;
            controllableGoat.addProperty(new GunProperty(GUN_COOLDOWN, scene, bulletMesh, boomSpritesheet, bulletTexture, audioManager));
            new GunScript(controllableGoat);

            SpotLight goatLight = new SpotLight(
                    controllableGoat,
                    new Vector3f(0, 0, 1),
                    new Vector3f(0, 0, 1), 0.30f, 0.3f,
                    new Vector3f(0.5f),
                    new Vector3f(0f, 0f, 0f),
                    0.1f, 0.1f);
            LightProperty directionalLightProperty = new LightProperty();
            controllableGoat.addProperty(directionalLightProperty);
            directionalLightProperty.addSpotLight(goatLight);

            ImageDataArray decodedCubemap = ImageDecoder.decodeCubemap("pl/warp/test/stars3");
            Cubemap cubemap = new Cubemap(decodedCubemap.getWidth(), decodedCubemap.getHeight(), decodedCubemap.getData());
            scene.addProperty(new GraphicsSkyboxProperty(cubemap));


            Mesh friageMesh = ObjLoader.read(GunScript.class.getResourceAsStream("frigate_1_heavy.obj"), false).toVAOMesh();
            ImageData frigateDecodedTexture = ImageDecoder.decodePNG(Test.class.getResourceAsStream("frigate_1_heavy.png"), PNGDecoder.Format.RGBA);
            Texture2D frigateTexture = new Texture2D(frigateDecodedTexture.getWidth(), frigateDecodedTexture.getHeight(), GL11.GL_RGBA, GL11.GL_RGBA, true, frigateDecodedTexture.getData());
            Material frigateMaterial = new Material(frigateTexture);
            frigateMaterial.setShininess(20);
            ImageData frigateBrightnessDecodedTexture = ImageDecoder.decodePNG(Test.class.getResourceAsStream("frigate_1_heavy_brightness.png"), PNGDecoder.Format.RGBA);
            Texture2D frigateBrightnessTexture = new Texture2D(frigateBrightnessDecodedTexture.getWidth(), frigateBrightnessDecodedTexture.getHeight(), GL11.GL_RGBA, GL11.GL_RGBA, true, frigateBrightnessDecodedTexture.getData());
            frigateMaterial.setBrightnessTexture(frigateBrightnessTexture);
            Component frigate = new SimpleComponent(scene);
            frigate.addProperty(new NameProperty("Frigate"));
            frigate.addProperty(new GraphicsMeshProperty(friageMesh));
            frigate.addProperty(new GraphicsMaterialProperty(frigateMaterial));
            frigate.addProperty(new PhysicalBodyProperty(20.0f, 38.365f * 3, 15.1f * 3, 11.9f * 3));
            TransformProperty transformProperty = new TransformProperty();
            transformProperty.move(new Vector3f(100, 0, 0));
            transformProperty.rotateLocalY((float) -(Math.PI / 2));
            transformProperty.scale(new Vector3f(3));
            frigate.addProperty(transformProperty);
            generateGOATS(scene, goatMesh, goatTexture, brightnessTexture);

        });
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    private void generateGOATS(Component parent, Mesh goatMesh, Texture2D goatTexture, Texture2D brightnessTexture) {
        BehaviourTreeBuilder builder = BehaviourTreeLoader.loadXML("data/ai/droneAI.xml");
        ArrayList<Component> team1 = new ArrayList<>();
        ArrayList<Component> team2 = new ArrayList<>();
        int nOfGoats = 10;
        for (int i = 0; i < nOfGoats; i++) {
            Component goat = new SimpleComponent(parent);
            goat.addProperty(new NameProperty("Ship " + i));
            goat.addProperty(new GraphicsMeshProperty(goatMesh));
            Material material = new Material(goatTexture);
            material.setShininess(20f);
            material.setBrightnessTexture(brightnessTexture);
            goat.addProperty(new GraphicsMaterialProperty(material));
            goat.addProperty(new PhysicalBodyProperty(10f, 10.772f / 2, 1.8f / 2, 13.443f / 2));
            goat.addProperty(new GunProperty(GUN_COOLDOWN, scene, bulletMesh, boomSpritesheet, bulletTexture, audioManager));
            float x = 10 + random.nextFloat() * 200 - 100f;
            float y = random.nextFloat() * 200 - 100f;
            float z = random.nextFloat() * 200 - 100f;
            TransformProperty transformProperty = new TransformProperty();
            transformProperty.move(new Vector3f(x, y, z));
            goat.addProperty(transformProperty);
            SequenceNode basenode = new SequenceNode();
            //basenode.addChildren(new SpinLeaf());
            //BehaviourTree behaviourTree = builder.build(goat);
            if (i < nOfGoats / 2) {
                goat.addProperty(new DroneProperty(5, 1, team2));
                team1.add(goat);
            } else {
                goat.addProperty(new DroneProperty(5, 1, team1));
                team2.add(goat);
            }
            goat.addProperty(new AIProperty(builder.build(goat)));
        }
    }


    @Override
    public Scene getScene() {
        return scene;
    }
}

