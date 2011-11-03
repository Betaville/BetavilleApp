/** Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Brooklyn eXperimental Media Center nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Brooklyn eXperimental Media Center BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.poly.bxmc.betaville.jme.gamestates;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.Camera.FrustumIntersect;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Skybox.Face;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.Text3D;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.LoadingGameState;
import com.jmex.model.collada.ExtraPluginManager;
import com.jmex.model.collada.GoogleEarthPlugin;
import com.jmex.terrain.TerrainBlock;

import edu.poly.bxmc.betaville.CacheManager;
import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.aesthetics.ColorValues;
import edu.poly.bxmc.betaville.jme.controllers.SceneController;
import edu.poly.bxmc.betaville.jme.foliage.Foliage;
import edu.poly.bxmc.betaville.jme.loaders.ModelLoader;
import edu.poly.bxmc.betaville.jme.map.CardinalDirections;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.model.AudibleDesign;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.model.SketchedDesign;
import edu.poly.bxmc.betaville.model.VideoDesign;
import edu.poly.bxmc.betaville.model.Design.Classification;
import edu.poly.bxmc.betaville.module.FrameSyncModule;
import edu.poly.bxmc.betaville.module.GlobalSceneModule;
import edu.poly.bxmc.betaville.module.LocalSceneModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;
import edu.poly.bxmc.betaville.module.SceneModule;
import edu.poly.bxmc.betaville.proposals.JMEDesktopProposalController;
import edu.poly.bxmc.betaville.proposals.LiveProposalManager;

/**
 * Creates the Betaville application's 3D content layer.
 * @author Skye Book
 * @author Caroline Bouchat
 */
public class SceneGameState extends BasicGameState {
	private static Logger logger = Logger.getLogger(SceneGameState.class);

	public final static int MOVE_MODE_ALTITUDE_WALK_MAX = 4;
	public final static int MOVE_MODE_ALTITUDE_BIRD_MAX = 200;
	public final static int MOVE_MODE_ALTITUDE_CHOPPER_MAX = 400;
	public final static int MOVE_MODE_ALTITUDE_PLANE_MAX = 1000;
	public final static int MOVE_MODE_ALTITUDE_JET_MAX = 100000;

	private final static int MOVE_MODE_SPEED_WALK_MIN = 15;
	private final static int MOVE_MODE_SPEED_WALK_MAX = 70;
	private final static int MOVE_MODE_SPEED_BIRD_MIN = 50;
	private final static int MOVE_MODE_SPEED_BIRD_MAX = 150;
	private final static int MOVE_MODE_SPEED_CHOPPER_MIN = 150;
	private final static int MOVE_MODE_SPEED_CHOPPER_MAX = 300;
	private final static int MOVE_MODE_SPEED_PLANE_MIN = 350;
	private final static int MOVE_MODE_SPEED_PLANE_MAX = 700;
	private final static int MOVE_MODE_SPEED_JET_MIN = 1000;
	private final static int MOVE_MODE_SPEED_JET_MAX = 2000;

	private static double MOVE_ACCELERATION_TIME = 5000.0;
	
	public static final float NEAR_FRUSTUM = Scale.fromMeter(1f);
	public static final float FAR_FRUSTUM = (int) Scale.fromMeter(35000);


	//stick to ground when below Drive altitude
	private GroundMagnet groundMagnet;
	//private AltitudeUpdater altitudeUpdater;


	private ColorRGBA ambientLightColor = new ColorRGBA(1f, 1f, 1f, 1f);
	private ColorRGBA diffuseLightColor = new ColorRGBA(1f, 1f, 1f, 1f);

	private ColorRGBA ambientLightColor2 = new ColorRGBA(.3f,.4f,.45f,.1f);
	//private ColorRGBA diffuseLightColor = new ColorRGBA(0.85f, 0.85f, 0.85f, .85f);
	private ColorRGBA diffuseLightColor2 = new ColorRGBA(.3f,.4f,.45f,.3f);

	/** The camera that creates the viewport for the user.*/
	private Camera camera = DisplaySystem.getDisplaySystem().getRenderer().getCamera();

	/** The renderer for the scene.*/
	private Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();

	private LightState lightState;
	private LensFlare lensFlare;
	private Node designNode;
	private Node terrainNode;
	private Node groundBoxNode;
	private Node flagNode;
	private Node searchResultsNode;
	private Node gisNode;
	private Node editorWidgetNode;
	private Quad singleQuad;
	/** Sky box object: simulates the sky.*/
	private Skybox skybox;
	private boolean skyboxOn=true;
	private LoadingGameState transitionGameState;
	/** Controls movement of the camera within the scene*/
	private SceneController sceneController;
	
	private ILocation startingLocation;

	private Foliage foliage;

	private Font3D verdanaFont;

	private Pyramid flagPyramid;
	private Pyramid searchPyramid;
	private MaterialState defaultSearchColor;
	private MaterialState singledSearchColor;

	private MaterialState pyramidTextMaterial;

	private List<Module> modules;
	private int lastSpeed = 0;	

	private boolean inGroundMode = false;
	//private boolean movingForward = false;
	//private double movingForwardSince = 0;
	private boolean speeding = false;
	private double speedingSince = 0;
	private int speedingMilliSecs = 0;
	private float lastAltitude = 100;
	private int lastAltitudeSpeedLevel = MOVE_MODE_ALTITUDE_BIRD_MAX;
	private boolean constantSpeed = false;
	
	private boolean framerateOptimizationEnabled = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jmex.game.state.BasicGameState#update(float)
	 */
	public SceneGameState(String name, ILocation startingLocation) {
		super(name);

		if(startingLocation==null){
			this.startingLocation = new GPSCoordinate(0.0, 40.69660664827642, -74.01919373745085);
		}
		else{
			this.startingLocation=startingLocation;
		}
		
		JME2MapManager.instance.adjustOffsets(this.startingLocation);
		
		modules = new ArrayList<Module>();

		ExtraPluginManager.registerExtraPlugin("GOOGLEEARTH", new GoogleEarthPlugin());
		LiveProposalManager.getInstance().registerProposalController(new JMEDesktopProposalController());

		designNode = new Node("designNode");
		terrainNode = new Node("terrainNode");
		groundBoxNode = new Node("groundBoxNode");
		searchResultsNode = new Node("searchResultsNode");
		gisNode = new Node("gisNode");
		editorWidgetNode = new Node("editorWidgetNode");
		flagNode = new Node("flagNode");
		rootNode.attachChild(designNode);
		rootNode.attachChild(terrainNode);
		rootNode.attachChild(groundBoxNode);
		rootNode.attachChild(gisNode);
		rootNode.attachChild(editorWidgetNode);
		rootNode.attachChild(flagNode);

		defaultSearchColor = renderer.createMaterialState();
		defaultSearchColor.setAmbient(ColorRGBA.green);
		defaultSearchColor.setDiffuse(ColorRGBA.green);
		defaultSearchColor.setEmissive(ColorRGBA.green);
		defaultSearchColor.setSpecular(ColorRGBA.green);
		singledSearchColor = renderer.createMaterialState();
		singledSearchColor.setAmbient(ColorRGBA.orange);
		singledSearchColor.setDiffuse(ColorRGBA.orange);
		singledSearchColor.setEmissive(ColorRGBA.orange);
		singledSearchColor.setSpecular(ColorRGBA.orange);
		searchPyramid = new Pyramid("flagPyramid", Scale.fromMeter(5), Scale.fromMeter(5));
		searchPyramid.setRenderState(defaultSearchColor);
		searchPyramid.updateRenderState();

		setupFlagNode();

		// Creation of a plane to test for clipping of the nodes
		//		ClipState clipState = DisplaySystem.getDisplaySystem().getRenderer().createClipState();
		//		clipState.setEnableClipPlane(ClipState.CLIP_PLANE1, true);
		//		clipState.setEnabled(true);
		//		designNode.setRenderState(clipState);

		// Creation of a ZBufferState, used to evaluate what incoming fragment will be used
		ZBufferState zBuffer = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zBuffer.setEnabled(true);
		zBuffer.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		//zBuffer.setFunction(ZBufferState.TestFunction.LessThan);

		//rootNode.setRenderState(zBuffer);

		// Cull State determines which side of a model will be visible when it is rendered
		CullState cullState = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
		cullState.setCullFace(CullState.Face.Back);
		cullState.setPolygonWind(CullState.PolygonWind.CounterClockWise);
		//designNode.setRenderState(cullState);
		//designNode.updateRenderState();

		// Initialize the Camera
		cameraInitialization();

		transitionGameState = (LoadingGameState)GameStateManager.getInstance().getChild("transitionGameState");
		transitionGameState.setProgress(0.06f, "Creating Sky");

		setupGroundBox();

		// build skybox
		if(skyboxOn)
			buildSkybox();

		transitionGameState.setProgress(0.07f, "Starting Weather");

		// build fog
		if(SettingsPreferences.isFogEnabled()) setupFog();

		transitionGameState.setProgress(0.09f, "Setting Up Lights");

		// build lighting
		buildLights();
		//setupLensFlare();

		transitionGameState.setProgress(0.10f, "Loading Terrain");

		//loadFoliage();

		//designNode.lockBounds();
		//designNode.lockTransforms();

		rootNode.updateRenderState();

		sceneController = new SceneController(this);
		rootNode.addController(sceneController);


		BlendState blendState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		blendState.setBlendEnabled(true);
		blendState.setSourceFunction(BlendState.SourceFunction.DestinationColor);
		blendState.setDestinationFunction(BlendState.DestinationFunction.One);
		blendState.setTestEnabled(true);
		blendState.setTestFunction(BlendState.TestFunction.GreaterThan);
		//designNode.setRenderState(blendState);
		//designNode.updateRenderState();

		BlendState tBlendState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		tBlendState.setBlendEnabled(true);
		tBlendState.setSourceFunction(BlendState.SourceFunction.DestinationColor);
		tBlendState.setDestinationFunction(BlendState.DestinationFunction.Zero);
		tBlendState.setTestEnabled(true);
		tBlendState.setTestFunction(BlendState.TestFunction.GreaterThan);
		//terrainNode.setRenderState(tBlendState);
		//terrainNode.updateRenderState();

		try {
			GameTaskQueueManager.getManager().render(new Callable<Object>() {
				public Object call() throws Exception {
					//designNode.unlock();
					verdanaFont = new Font3D(new Font("verdana", Font.PLAIN, 2),0.1f,true,true,true);
					return null;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		groundMagnet = new GroundMagnet("Ground Magnet", "Forces the camera to be glued to the ground");
		//altitudeUpdater = new AltitudeUpdater("Altitude Updater", "Retrieves the altitude on every udpate");
	}


	private void setupFlagNode(){
		MaterialState pMat = renderer.createMaterialState();
		pMat.setAmbient(new ColorRGBA(
				ColorValues.getPyramidAmbientColorAsUnit()[0],
				ColorValues.getPyramidAmbientColorAsUnit()[1],
				ColorValues.getPyramidAmbientColorAsUnit()[2],
				ColorValues.getPyramidAmbientColorAsUnit()[3]));

		pMat.setDiffuse(new ColorRGBA(
				ColorValues.getPyramidDiffuseColorAsUnit()[0],
				ColorValues.getPyramidDiffuseColorAsUnit()[1],
				ColorValues.getPyramidDiffuseColorAsUnit()[2],
				ColorValues.getPyramidDiffuseColorAsUnit()[3]));

		flagPyramid = new Pyramid("flagPyramid", Scale.fromMeter(5), Scale.fromMeter(5));
		flagPyramid.setRenderState(pMat);

		pyramidTextMaterial = renderer.createMaterialState();
		pyramidTextMaterial.setAmbient(ColorRGBA.black);
		pyramidTextMaterial.setDiffuse(ColorRGBA.black);
	}

	private void setupGroundBox(){
		singleQuad = new Quad("singleQuad", Scale.fromMeter(5), Scale.fromMeter(5));
		singleQuad.setLocalRotation(Rotator.PITCH270);
		groundBoxNode.attachChild(singleQuad);
		MaterialState ms = renderer.createMaterialState();
		ms.setAmbient(ColorRGBA.red);
		ms.setDiffuse(ColorRGBA.red);
		ms.setShininess(50);
		singleQuad.setRenderState(ms);
		singleQuad.updateRenderState();
	}

	private void loadFoliage(){
		foliage = new Foliage();
		foliage.addObjectToCache(new File("data/foliage/ONE_TREE_Deciduous.dae"), "tree");
		foliage.addObjectToCache(new File("data/foliage/bush.dae"), "bush");
	}

	private void buildLights() {
		// Set up a directional light
		DirectionalLight directionalLight = new DirectionalLight();
		directionalLight.setDirection(new Vector3f(.25f, -.85f, .75f)); //previously 1, -.85f, 0
		directionalLight.setDiffuse(diffuseLightColor);
		directionalLight.setAmbient(ambientLightColor);
		directionalLight.setShadowCaster(false);
		directionalLight.setEnabled(true);

		DirectionalLight fillLight = new DirectionalLight();
		fillLight.setDirection(new Vector3f(-.25f,.85f,-.75f));//previously  -1f,.85f,0)
		fillLight.setDiffuse(diffuseLightColor2);
		fillLight.setAmbient(ambientLightColor2);
		fillLight.setShadowCaster(false);
		fillLight.setEnabled(true);

		/*
		SpotLight sl = new SpotLight();
		sl.setLocation(MapManager.utmToBetaville(new UTMCoordinate(2500, DecimalDegreeConverter.dmsToDD(40, 42, 4.07),
				DecimalDegreeConverter.dmsToDD(-74, 0, 31.28))));
		Vector3f direction = CardinalDirections.NW;
		direction.setY(-.75f);
		sl.setDirection(direction);
		sl.setEnabled(true);
		sl.setShadowCaster(true);
		sl.setDiffuse(diffuseLightColor);
		sl.setAmbient(ambientLightColor);
		sl.setAngle(90);

		PointLight pl = new PointLight();
		pl.setEnabled(true);
		pl.setDiffuse(new ColorRGBA(.7f, .7f, .7f, 1.0f));
		pl.setAmbient(new ColorRGBA(.25f, .25f, .25f, .25f));
		pl.setLocation(MapManager.utmToBetaville(new UTMCoordinate(2500, DecimalDegreeConverter.dmsToDD(40, 42, 4.07),
				DecimalDegreeConverter.dmsToDD(-74, 0, 31.28))));
		pl.setShadowCaster(true);
		 */

		// Attach the light to a lightState and the lightState to rootNode.
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
		lightState.detachAll();
		lightState.setEnabled(true);
		lightState.attach(directionalLight);
		lightState.attach(fillLight);
		//lightState.attach(sl);

		/*
		designNode.setRenderState(lightState);
		designNode.updateRenderState();

		groundBoxNode.setRenderState(lightState);
		groundBoxNode.updateRenderState();

		terrainNode.setRenderState(lightState);
		terrainNode.updateRenderState();
		 */


		rootNode.setRenderState(lightState);
		rootNode.updateRenderState();

	}

	private void setupLensFlare(){
		TextureState[] tex = new TextureState[4];
		tex[0] = renderer.createTextureState();
		tex[0].setTexture(TextureManager.loadTexture(ResourceLoader.loadResource("/data/sky/flare1.png"),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, Image.Format.RGBA8,
				0.0f, true));
		tex[0].setEnabled(true);

		tex[1] = renderer.createTextureState();
		tex[1].setTexture(TextureManager.loadTexture(ResourceLoader.loadResource("/data/sky/flare2.png"),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
		tex[1].setEnabled(true);

		tex[2] = renderer.createTextureState();
		tex[2].setTexture(TextureManager.loadTexture(ResourceLoader.loadResource("/data/sky/flare3.png"),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
		tex[2].setEnabled(true);

		tex[3] = renderer.createTextureState();
		tex[3].setTexture(TextureManager.loadTexture(ResourceLoader.loadResource("/data/sky/flare4.png"),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
		tex[3].setEnabled(true);

		lensFlare = LensFlareFactory.createBasicLensFlare("flare", tex);
		lensFlare.setRootNode(designNode);
		lensFlare.setIntensity(1);
	}

	/**
	 * Method <buildSkybox> Builds the skybox for Betaville
	 */
	private void buildSkybox() {
		skybox = new Skybox("skybox", FAR_FRUSTUM/2,
				Scale.fromMeter(3000),FAR_FRUSTUM/2);
		// Create the texture
		Texture texture = TextureManager.loadTexture(ResourceLoader.loadResource("/data/sky/sky.png"),
				MinificationFilter.BilinearNearestMipMap,MagnificationFilter.Bilinear);

		texture.setWrap(WrapMode.Repeat);
		skybox.setTexture(Face.North, texture);
		skybox.setTexture(Face.East, texture);
		skybox.setTexture(Face.South, texture);
		skybox.setTexture(Face.West, texture);
		skybox.setTexture(Face.Up, texture);
		skybox.setTexture(Face.Down, texture);
		// Translate the skybox at the camera's location
		skybox.setLocalTranslation(new Vector3f(camera.getLocation().getX(),
				camera.getLocation().getY(), camera.getLocation().getZ()));

		CullState cullState = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
		cullState.setCullFace(CullState.Face.Back);
		cullState.setEnabled(true);
		skybox.setRenderState(cullState);

		// The ZBufferState RenderState handles two things: setting the depth
		// testing function and allowing the mask
		// to be written to or not.
		//ZBufferState zState = DisplaySystem.getDisplaySystem().getRenderer()
		//.createZBufferState();
		//zState.setEnabled(true);
		//skybox.setRenderState(zState);

		skybox.setLightCombineMode(Spatial.LightCombineMode.Off);
		//skybox.setCullHint(Spatial.CullHint.Dynamic);
		skybox.setTextureCombineMode(TextureCombineMode.Replace);
		skybox.updateRenderState();
		
		//skybox.lockBounds();
		//skybox.lockMeshes();

		rootNode.attachChild(skybox);
	}

	/**
	 * Method <cameraInit> - Change the projection of the camera for a
	 * perspective projection.
	 */
	private void cameraInitialization() {
		// Setup the camera with a perspective projection
		cameraPerspectiveProjection();

		// Setup the camera with a parallel projection
		//cameraParallelProjection();

		// Initialize the camera
		//Vector3f left = new Vector3f( -1.0f, 0.0f, 0.0f );
		//Vector3f up = new Vector3f( 0.0f, 1.0f, 0.0f );
		camera.update();
	}

	/**
	 * Method <cameraPerspectiveProjection> - Setup the camera for a perspective projection
	 */
	public void cameraPerspectiveProjection() {
		camera.setParallelProjection(false);
		camera.setLocation(JME2MapManager.instance.locationToBetaville(startingLocation));
		camera.getLocation().setY(Scale.fromMeter(200));
		float aspect = (float) DisplaySystem.getDisplaySystem().getWidth() / DisplaySystem.getDisplaySystem().getHeight();
		// Parameters of the frustum for perspective : field of view (angle of view in the Y direction), aspect, near, far
		camera.setFrustumPerspective( 45.0f, aspect, NEAR_FRUSTUM, FAR_FRUSTUM);
		Vector3f lookAt = camera.getLocation().clone().add(CardinalDirections.NE.clone().mult(Scale.fromMeter(100)));
		camera.lookAt(lookAt, Vector3f.UNIT_Y);
		//camera.setFrustumPerspective( 45.0f, aspect, Scale.fromMeter(SceneScape.getMinimumHeight()), viewDistance);
		//camera.setFrustumPerspective( 45.0f, aspect, Scale.fromMeter(6), viewDistance);

		/*
		VideoTexture vid = new VideoTexture("myvideo");
		try {
			addModuleToUpdateList(vid);
			Spatial q = vid.getVideoQuad();
			q.setLocalTranslation(camera.getLocation().clone());
			designNode.attachChild(q);
		} catch (ModuleNameException e) {
			e.printStackTrace();
		}
		 */

	}



	/**
	 * Method <cameraParallelProjection> - Setup the camera for a parallel projection
	 */
	public void cameraParallelProjection() {
		camera.setParallelProjection(true);
		float aspect = (float) DisplaySystem.getDisplaySystem().getWidth() / DisplaySystem.getDisplaySystem().getHeight();
		// Parameters of the frustum : near, far, 44, right, top, bottom
		camera.setFrustum( -100, 1000, -50 * aspect, 50 * aspect, -50, 50 );
	}

	private void setupFog() {
		FogState fogState = DisplaySystem.getDisplaySystem().getRenderer().createFogState();
		fogState.setDensity(.012f);
		fogState.setEnabled(true);
		fogState.setColor(new ColorRGBA(.6f,.75f,1,1f));
		//fogState.setStart(Scale.fromMeter(30000/4));
		//fogState.setEnd(Scale.fromMeter(30000/2));
		fogState.setStart(Scale.fromMeter(30000/4));
		fogState.setEnd(Scale.fromMeter(30000/2));
		fogState.setDensityFunction(FogState.DensityFunction.Exponential);
		fogState.setQuality(FogState.Quality.PerVertex);
		rootNode.setRenderState(fogState);
	}

	public void applyColor(Spatial spatial, ColorRGBA color){
		colorStripper(spatial);
		MaterialState targetMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		targetMaterial.setDiffuse(color);
		spatial.setRenderState(targetMaterial);
		spatial.updateRenderState();
	}

	private void colorStripper(Spatial spatial){
		spatial.clearRenderState(StateType.Material);
		spatial.clearRenderState(StateType.Texture);
		spatial.clearRenderState(StateType.Shade);
		spatial.clearRenderState(StateType.Light);
		spatial.updateRenderState();
		if(spatial instanceof Node && ((Node)spatial).getChildren()!= null){
			for(int i=0; i<((Node)spatial).getChildren().size(); i++){
				colorStripper(((Node)spatial).getChildren().get(i));
			}
		}
	}

	/**
	 * Removes a design from the display while leaving it in the City object.
	 * @param designID The ID of the design to remove.
	 * @see City
	 */
	public boolean removeTerrainFromDisplay(int designID){
		logger.info("removing " + designID);
		Iterator<Spatial> elements = terrainNode.getChildren().iterator();
		while(elements.hasNext()){
			Spatial s = elements.next();
			if(s.getName().endsWith("$"+designID)){
				SceneScape.clearTerrainSelection();
				s.removeFromParent();
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a design from the display while leaving it in the City object.
	 * @param designID The ID of the design to remove.
	 * @see City
	 */
	public boolean removeDesignFromDisplay(int designID){
		logger.info("removing " + designID);
		Iterator<Spatial> elements = designNode.getChildren().iterator();
		while(elements.hasNext()){
			Spatial s = elements.next();
			if(s.getName().endsWith("$"+designID)){
				if(SceneScape.getTargetSpatial().getName().equals(s.getName())){
					SceneScape.clearTargetSpatial();
				}
				s.removeFromParent();
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param designID
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public void addDesignToDisplay(int designID) throws IOException, URISyntaxException{
		Design d = SceneScape.getCity().findDesignByID(designID);
		if(d!=null){
			// Remove required designs first
			for(int r : d.getDesignsToRemove()){
				designNode.getChild("$"+r).removeFromParent();
			}

			if(d instanceof ModeledDesign){
				boolean fileResponse = CacheManager.getCacheManager().requestFile(d.getID(), d.getFilepath());

				if(fileResponse){
					ModelLoader loader = null;
					try {
						loader = new ModelLoader((ModeledDesign)d, true, null);
						final Node dNode = loader.getModel();
						dNode.setName(d.getFullIdentifier());
						dNode.setLocalTranslation(JME2MapManager.instance.locationToBetaville(d.getCoordinate()));
						dNode.setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)d).getRotationX(),
								((ModeledDesign)d).getRotationY(), ((ModeledDesign)d).getRotationZ()));

						designNode.attachChild(dNode);
						designNode.updateRenderState();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public void replaceModelFile(int designID, URL newModel, boolean textureOnOff) throws IOException, URISyntaxException{
		// Only do this if we're working with a network design
		if(designID>0){

			Design design = SceneScape.getCity().findDesignByID(designID);
			((ModeledDesign)design).setTextured(textureOnOff);

			// hold in to the location in cache
			String currentFile = design.getFilepath();

			// set the location off cache of the new model
			design.setFilepath(newModel.toString());

			//load the new model
			ModelLoader ml = new ModelLoader((ModeledDesign)design, false, currentFile);
			designNode.getChild(design.getFullIdentifier()).removeFromParent();
			designNode.attachChild(ml.getModel());
			designNode.getChild(design.getFullIdentifier()).setLocalTranslation(JME2MapManager.instance.locationToBetaville(design.getCoordinate()));
			designNode.getChild(design.getFullIdentifier()).updateRenderState();

			// create new filename and 
			String newFilename;
			if(currentFile.contains("_")){
				int currentIteration = Integer.parseInt(currentFile.substring(currentFile.lastIndexOf("_")+1, currentFile.lastIndexOf(".")));
				newFilename = design.getID()+"_"+(currentIteration+1)+".jme";
			}
			else{
				newFilename=design.getID()+"_"+1+".jme";
			}
			SceneScape.getCity().findDesignByID(designID).setFilepath(newFilename);
			((ModeledDesign)SceneScape.getCity().findDesignByID(designID)).setTextured(textureOnOff);
		}
	}

	public void addDesignToCity(Design design, URL modelURL, URL textureURL, int sourceID) throws IOException, URISyntaxException{
		if(design instanceof SketchedDesign){}
		else if(design instanceof ModeledDesign){
			loadModeledDesign((ModeledDesign) design);
		}
		else if(design instanceof AudibleDesign){}
		else if(design instanceof VideoDesign){}
	}

	private void loadModeledDesign(ModeledDesign design) throws IOException, URISyntaxException{
		ModelLoader ml = new ModelLoader(design, false, null);
		if(design.getName().equals("TERRAIN")){
			terrainNode.attachChild(ml.getModel());
			terrainNode.getChild(design.getFullIdentifier()).setLocalTranslation(JME2MapManager.instance.locationToBetaville(design.getCoordinate()));
			terrainNode.getChild(design.getFullIdentifier()).updateRenderState();
		}
		else{
			designNode.attachChild(ml.getModel());
			designNode.getChild(design.getFullIdentifier()).setLocalTranslation(JME2MapManager.instance.locationToBetaville(design.getCoordinate()));
			designNode.getChild(design.getFullIdentifier()).updateRenderState();
			SceneScape.getCity().addDesign(design);
			logger.debug("Model Added");
		}
	}

	public Spatial getSpecificDesign(int designID){
		Iterator<Spatial> it = designNode.getChildren().iterator();
		while(it.hasNext()){
			Spatial s = it.next();
			if(s.getName().endsWith("$"+designID)){
				return s;
			}
		}
		return null;
	}

	public Camera getCamera() {
		return camera;
	}

	/**
	 * @return the designNode
	 */
	public Node getDesignNode() {
		return designNode;
	}
	
	public Node getBlockNodeFor(ILocation location){
		
		return designNode;
	}
	
	/**
	 * @legacy
	 * @return
	 */
	public Spatial getDesignNodeChild(String identifier){
		return designNode.getChild(identifier);
	}

	/**
	 * @return the terrainNode
	 */
	public Node getTerrainNode() {
		return terrainNode;
	}

	public Node getGroundBoxNode(){
		return groundBoxNode;
	}

	public Node getFlagNode() {
		return flagNode;
	}

	public Node getSearchNode(){
		return searchResultsNode;
	}

	public Skybox getSkybox() {
		return skybox;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public ColorRGBA getDefaultDiffuseLight() {
		return diffuseLightColor;
	}

	public LightState getLightState() {
		return lightState;
	}

	public SceneController getSceneController(){
		return sceneController;
	}

	public void detatchSceneController(){
		rootNode.removeController(sceneController);
	}

	public void reattachSceneController(){
		for(int i=0; i<rootNode.getControllers().size(); i++){
			if(rootNode.getControllers().get(i).equals(sceneController)) return;
		}
		rootNode.addController(sceneController);
	}

	public void render(float tpf){
		DisplaySystem.getDisplaySystem().getRenderer().draw(skybox);
		DisplaySystem.getDisplaySystem().getRenderer().renderQueue();
		DisplaySystem.getDisplaySystem().getRenderer().clearZBuffer();
	}

	public void addToFlagNode(Vector3f location, List<Integer> baseIDs){
		//Pyramid p = new Pyramid("flagPyramid", Scale.fromMeter(5), Scale.fromMeter(5));
		
		Node thisFlagNode = new Node(""+location.hashCode());
		thisFlagNode.setLocalTranslation(location);
		
		SharedMesh p = new SharedMesh(flagPyramid);
		p.updateRenderState();
		p.setLocalRotation(Rotator.ROLL180);
		
		// clear the spatial's name first
		p.setName("");
		for(Integer baseID : baseIDs){
			p.setName(p.getName()+baseID+";");
		}
		
		p.setModelBound(new BoundingBox());
		p.updateModelBound();
		thisFlagNode.attachChild(p);
		Text3D proposalText = verdanaFont.createText(""+baseIDs.size(), Scale.fromMeter(2), 0);
		proposalText.setLocalScale(new Vector3f(Scale.fromMeter(3),Scale.fromMeter(3),Scale.fromMeter(.1f)));
		proposalText.alignCenter();
		//		proposalText.setFontColor(ColorRGBA.black);
		proposalText.setRenderState(pyramidTextMaterial);
		Vector3f textLocation = new Vector3f();
		textLocation.setX(textLocation.x-(-1*(Scale.fromMeter(2)/2)));
		textLocation.setY(textLocation.y+(Scale.fromMeter(5)/2));
		textLocation.setZ(textLocation.z-(-1*(Scale.fromMeter(2)/2)));
		proposalText.setLocalTranslation(textLocation);
		//proposalText.setLocalRotation(Rotator.fromThreeAngles(-90, -90, 0));
		proposalText.setLocalRotation(Rotator.fromThreeAngles(-90, 90, 0));
		proposalText.setName("$text"+baseIDs.get(0));
		thisFlagNode.attachChild(proposalText);
		proposalText.updateRenderState();
		
		thisFlagNode.setLocalRotation(Rotator.angleY(180));
		flagNode.attachChild(thisFlagNode);
		flagNode.updateRenderState();
	}

	public void setMoveSpeed(float speed){
		sceneController.setMoveSpeed(speed);
	}

	public float getMoveSpeed(){
		return sceneController.getMoveSpeed();
	}

	/**
	 * Checks if a position on the ground is selected
	 * @return true if a position is selected; false if there is no position selected
	 */
	public boolean isGroundSelectorAttached(){
		return groundBoxNode.hasChild(singleQuad);
	}

	/**
	 * Places the ground selection marker on the scene
	 * @param location The locationat which to place the marker
	 */
	public void placeGroundBoxSelector(Vector3f location){
		if(!isGroundSelectorAttached()){
			groundBoxNode.attachChild(singleQuad);
		}
		singleQuad.setLocalTranslation(location);
	}

	/**
	 * Removes the ground selection marker from the scene
	 */
	public void removeGroundBox(){
		if(isGroundSelectorAttached()){
			groundBoxNode.detachChild(singleQuad);
		}
	}
	
	public Node getGISNode() {
		return gisNode;
	}
	
	public Node getEditorWidgetNode() {
		return editorWidgetNode;
	}

	public Vector3f getGroundSelectorLocation(){
		return singleQuad.getLocalTranslation();
	}

	public void addModuleToUpdateList(Module module) throws ModuleNameException{
		for(int i=0; i<modules.size(); i++){
			Module m = modules.get(i);
			if(module.getName().toLowerCase().equals(m.getName().toLowerCase())){
				throw new ModuleNameException("Module '" + module.getName() + "' requires a unique name");
			}
		}
		
		if(module instanceof LocalSceneModule) {
			((LocalSceneModule) module).initialize(designNode);
		}

		else if(module instanceof GlobalSceneModule) {
			((GlobalSceneModule) module).initialize(rootNode);
		}
		
		modules.add(module);
	}

	public void removeModuleFromUpdateList(Module module){
		modules.remove(module);
	}

	public boolean setConstantSpeed(boolean constantSpeed){
		return this.constantSpeed = constantSpeed;
	}

	public boolean getConstantSpeed(){
		return constantSpeed;
	}
	public void update(float tpf) {
		super.update(tpf);
		
		if(framerateOptimizationEnabled) optimizeFramerate(tpf);
		
		//logger.info(SceneGameState.getInstance().getDesignNode().getChild("$1357"));

		//dNode.setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)d).getRotationX(), ((ModeledDesign)d).getRotationY(), ((ModeledDesign)d).getRotationZ()));

		// legacy floor checking
		//if(camera.getLocation().getY()<=Scale.fromMeter(SceneScape.getMinimumHeight())){
		//	camera.setLocation(new Vector3f(camera.getLocation().getX(), Scale.fromMeter(SceneScape.getMinimumHeight()), camera.getLocation().getZ()));
		//}

		// maximum height
		if (camera.getLocation().getY() >= Scale.fromMeter(30000)) {
			camera.setLocation(new Vector3f(camera.getLocation().getX(), Scale.fromMeter(30000), camera.getLocation().getZ()));
		}


		if(SettingsPreferences.useGeneratedTerrainEnabled()){
			//logger.info("testing");
			if(terrainNode.getQuantity()>0){
				//logger.info("in terrain node");
				for(Spatial child : terrainNode.getChildren()){
					//logger.info("terrain child");
					if(child instanceof TerrainBlock){
						//logger.info("terrain block found! Checking at "+camera.getLocation().subtract(child.getLocalTranslation()));
						float height = ((TerrainBlock)child).getHeight(camera.getLocation().subtract(child.getLocalTranslation()));
						if(!Float.isNaN(height)){
							//if(height==0) logger.info("height is 0!");
							//camera.setLocation(new Vector3f(camera.getLocation().x, height, camera.getLocation().z));
							//logger.info("height found");
							break;
						}
					}
				}
			}
		}
		else{
			// do jonas and radu movement
			automaticMovementCalculation();
		}


		// sceneController.setMoveSpeed(Scale.toMeter(camera.getLocation().getY())/100);
		// System.out.println(SceneGameState.getInstance().getMoveSpeed());

		for(int i=0; i<modules.size(); i++){
			Module module = modules.get(i);
			if (module instanceof LocalSceneModule) {
				((SceneModule) module).onUpdate(designNode, camera
						.getLocation(), camera.getDirection());
			}
			if (module instanceof GlobalSceneModule) {
				((SceneModule) module).onUpdate(rootNode, camera.getLocation(),
						camera.getDirection());
			}
			if (module instanceof FrameSyncModule) {
				((FrameSyncModule) module).frameUpdate(tpf);
			}
		}

		// update water position
		// waterQuad.setLocalTranslation(camera.getLocation().x,
		// waterQuad.getLocalTranslation().y, camera.getLocation().z);

		// update skybox position
		if (skyboxOn)
			skybox.setLocalTranslation(new Vector3f(
					camera.getLocation().getX(), camera.getLocation().getY(),
					camera.getLocation().getZ()));
	}

	private void automaticMovementCalculation(){
		float currentAltitude = Scale.toMeter(camera.getLocation().getY());
		int newSpeed = MOVE_MODE_SPEED_WALK_MIN;
		if (currentAltitude <= MOVE_MODE_ALTITUDE_WALK_MAX) {
			if (!inGroundMode) {
				//SceneGameState.getInstance().addModuleToUpdateList(groundMagnet);
				camera.setLocation(new Vector3f(camera.getLocation().getX(), Scale.fromMeter(SceneScape.getMinimumHeight()), camera.getLocation().getZ()));
				System.out.println("add groundMagnet");
				inGroundMode = true;
			}
		}
		if (sceneController.getElevate() && inGroundMode) {
			//SceneGameState.getInstance().removeModuleFromUpdateList(groundMagnet);
			camera.setLocation(new Vector3f(camera.getLocation().getX(), Scale.fromMeter(MOVE_MODE_ALTITUDE_WALK_MAX), camera.getLocation().getZ()));
			inGroundMode = false;
			// newSpeed = MOVE_MODE_SPEED_CHOPPER;
			// System.out.println("currentAltitude: " + currentAltitude);
			System.out.println("remove groundMagnet");
		}
		if (lastAltitudeSpeedLevel != getAltitudeLevel(currentAltitude)){
			speedingMilliSecs = 0;
			speeding = false;
			lastAltitudeSpeedLevel = getAltitudeLevel(currentAltitude);
		}
		// count time you are speeding
		if (sceneController.getMoveKey()) {
			if (speeding) {
				speedingMilliSecs = (int) (System.currentTimeMillis() - speedingSince);
			} else {
				speeding = true;
				speedingSince = System.currentTimeMillis();
			}
		} else {
			speeding = false;
			speedingMilliSecs = 0;
		}

		double altitudeDelta = (lastAltitude - currentAltitude); 

		if (lastAltitude > currentAltitude && altitudeDelta > 0.5){
			// different speeds / height
			newSpeed = getSpeedForAltitude(currentAltitude,true);
		}
		else{
			newSpeed = getSpeedForAltitude(currentAltitude,false);
		}
		lastAltitude = currentAltitude;

		if(getConstantSpeed()){
			sceneController.setMoveSpeed(sceneController.getMoveSpeed());
		}else if (newSpeed != lastSpeed) {
			sceneController.setMoveSpeed(Scale.fromMeter(newSpeed));
			lastSpeed = newSpeed;
			//System.out.println("New Speed " + lastSpeed );
		}
	}


	private int getSpeedForAltitude(float altitude, boolean goDown) {
		int newSpeed = 0;

		//if speeding
		if (sceneController.getMoveKey()) {
			if(goDown && lastSpeed >= getMinSpeedForAltitude(altitude)){
				newSpeed = getAcceleratedSpeedForAltitude(altitude, goDown);

			}else if (speedingMilliSecs >= MOVE_ACCELERATION_TIME || lastSpeed >= getMaxSpeedForAltitude(altitude)) {
				newSpeed = getMaxSpeedForAltitude(altitude);
			}else {
				newSpeed = getAcceleratedSpeedForAltitude(altitude, goDown);
			}
			//no speeding - just usual moving
		} else {
			newSpeed = getMinSpeedForAltitude(altitude);
		}

		return newSpeed;
	}

	private int getAcceleratedSpeedForAltitude(float altitude, boolean goDown) {

		int newSpeed;
		if (goDown && altitude<MOVE_MODE_ALTITUDE_WALK_MAX*10) {
			int minSpeed = getMinSpeedForAltitude(altitude);
			double subSpeed = ((minSpeed / MOVE_ACCELERATION_TIME) * speedingMilliSecs);
			newSpeed = (int)((getMinSpeedForAltitude(altitude) - subSpeed));
			//newSpeed = (int)(getMaxSpeedForAltitude(altitude) - subSpeed);

		} else {
			int maxSpeed = getMaxSpeedForAltitude(altitude);
			double addSpeed = ((maxSpeed / MOVE_ACCELERATION_TIME) * speedingMilliSecs);
			newSpeed = (int)(getMinSpeedForAltitude(altitude) + addSpeed);
		}

		//System.out.println("maxSpeed: " + maxSpeed + ", ms/mat: " + (maxSpeed / MOVE_ACCELERATION_TIME) + ", speedingMS: " + speedingMilliSecs + ", add speed: " + addSpeed + ", newSpeed: " + newSpeed);
		return newSpeed;
	}

	private int getMaxSpeedForAltitude(float altitude) {
		int altitudeLevel = getAltitudeLevel(altitude);

		switch (altitudeLevel) {
		case MOVE_MODE_ALTITUDE_WALK_MAX:
			return MOVE_MODE_SPEED_WALK_MAX;
		case MOVE_MODE_ALTITUDE_BIRD_MAX:
			return MOVE_MODE_SPEED_BIRD_MAX;
		case MOVE_MODE_ALTITUDE_CHOPPER_MAX:
			return MOVE_MODE_SPEED_CHOPPER_MAX;
		case MOVE_MODE_ALTITUDE_PLANE_MAX:
			return MOVE_MODE_SPEED_PLANE_MAX;
		case MOVE_MODE_ALTITUDE_JET_MAX:
			return MOVE_MODE_SPEED_JET_MAX;
		}
		return 0;
	}

	private int getMinSpeedForAltitude(float altitude) {
		int altitudeLevel = getAltitudeLevel(altitude);

		switch (altitudeLevel) {
		case MOVE_MODE_ALTITUDE_WALK_MAX:
			return MOVE_MODE_SPEED_WALK_MIN;
		case MOVE_MODE_ALTITUDE_BIRD_MAX:
			return MOVE_MODE_SPEED_BIRD_MIN;
		case MOVE_MODE_ALTITUDE_CHOPPER_MAX:
			return MOVE_MODE_SPEED_CHOPPER_MIN;
		case MOVE_MODE_ALTITUDE_PLANE_MAX:
			return MOVE_MODE_SPEED_PLANE_MIN;
		case MOVE_MODE_ALTITUDE_JET_MAX:
			return MOVE_MODE_SPEED_JET_MIN;
		}
		return 0;
	}


	private int getAltitudeLevel(float altitude) {
		if (altitude > MOVE_MODE_ALTITUDE_PLANE_MAX) {
			return MOVE_MODE_ALTITUDE_JET_MAX;
		} else if (altitude > MOVE_MODE_ALTITUDE_CHOPPER_MAX) {
			return MOVE_MODE_ALTITUDE_PLANE_MAX;
		} else if (altitude > MOVE_MODE_ALTITUDE_BIRD_MAX) {
			return MOVE_MODE_ALTITUDE_CHOPPER_MAX;
		} else if (altitude > MOVE_MODE_ALTITUDE_WALK_MAX) {
			return MOVE_MODE_ALTITUDE_BIRD_MAX;
		}
		return MOVE_MODE_ALTITUDE_WALK_MAX;
	}

	/**
	 * Adds a search result flag to the scene at the location with the specified identifier, which
	 * can be used to find the result later
	 * @param location
	 * @param identifier
	 */
	public void addSearchResult(ILocation location, String identifier){
		SharedMesh p = new SharedMesh(searchPyramid);
		p.setLocalTranslation(JME2MapManager.instance.locationToBetaville(location));
		p.setLocalTranslation(p.getLocalTranslation().x, Scale.fromMeter(100), p.getLocalTranslation().z);
		p.setLocalRotation(Rotator.ROLL180);
		p.updateRenderState();
		p.setName(identifier);
		p.setModelBound(new BoundingBox());
		p.updateModelBound();
		// if this is not currently being displayed, set it up
		if(searchResultsNode.getParent()==null){
			logger.info("Attaching search node to scene");
			rootNode.attachChild(searchResultsNode);
		}
		searchResultsNode.attachChild(p);
		searchResultsNode.updateRenderState();
	}

	public void setSingledSearchResult(String identifier){
		if(searchResultsNode.getQuantity()>0){
			for(Spatial s : searchResultsNode.getChildren()){
				if(s instanceof SharedMesh){
					s.setRenderState(defaultSearchColor);
				}
			}
			searchResultsNode.updateRenderState();
		}
		Spatial s = searchResultsNode.getChild(identifier);
		if(s!=null){
			s.setRenderState(singledSearchColor);
			s.updateRenderState();
		}
	}
	
	private void optimizeFramerate(float tpf){
		float target = 20f; // fps
		
		if(1f/tpf > target){
			// ADD
			for(Design d : SceneScape.getCity().getDesigns()){
				if(!d.equals(Classification.BASE)) continue;
				
				boolean isInScene=false;
				
				if(designNode.getQuantity()==0) continue;
				for(Spatial design : designNode.getChildren()){
					if(design.getName().equals(d.getFullIdentifier())){
						isInScene=true;
						break;
					}
				}
				
				/*
				// add the object here
				try {
					if(!isInScene) addDesignToDisplay(d.getID());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
		}
		else{
			// REMOVE
			if(designNode.getQuantity()==0) return;
			
			SettingsPreferences.getThreadPool().submit(new Runnable() {
				
				public void run() {
					Vector3f cameraLoc = camera.getLocation();
					Spatial farthestObject=null;
					float distance=0;
					float tempDistance;
					
					logger.info("designNode has " + designNode.getQuantity() + " children");
					// remove buildings
					for(Spatial design : designNode.getChildren()){
						tempDistance = cameraLoc.distance(design.getLocalTranslation());
						if(tempDistance>distance){
							farthestObject = design;
							distance=tempDistance;
						}
					}
					
					Vector3f locationOfObject = farthestObject.getLocalTranslation();
					if(camera.contains(farthestObject.getWorldBound()).equals(FrustumIntersect.Inside)){
						logger.info("is inside");
						designNode.detachChild(farthestObject);
					}
					
					//logger.info("Angle Between camera and object: " + (180f-Math.toDegrees(cameraLoc.normalize().angleBetween(locationOfObject.normalize()))));
				}
			});
		}
	}
	
	public void setFramerateOptimizationEnabled(boolean enabled){
		framerateOptimizationEnabled=enabled;
	}

	/**
	 * Removes all result pyramids from the search display, also removes the search result
	 * node from the scene's root node.
	 */
	public void clearSearchDisplay(){
		searchResultsNode.detachAllChildren();
		logger.info("Detatching search node from scene");
		rootNode.detachChild(searchResultsNode);
	}

	public static SceneGameState getInstance(){
		return (SceneGameState)GameStateManager.getInstance().getChild("sceneGameState");
	}

	private class GroundMagnet extends Module implements GlobalSceneModule{

		public GroundMagnet(String name, String description) {
			super(name, description);
		}

		public void initialize(Node scene) {}

		public void onUpdate(Node scene, Vector3f cameraLocation, Vector3f cameraDirection) {
			Vector3f newLocation = cameraLocation.clone();
			newLocation.setY(Scale.fromMeter(SceneScape.getMinimumHeight()));
			DisplaySystem.getDisplaySystem().getRenderer().getCamera().setLocation(newLocation);
		}

		public void deconstruct() {}
	}
	
	private class FramerateOptimizer implements Runnable{

		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
}