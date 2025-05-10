package myGame;

import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;

import java.lang.Math;
import java.awt.*;

import java.awt.event.*;

import java.io.*;
import java.util.*;
import java.util.UUID;
import java.net.InetAddress;

import java.net.UnknownHostException;

import org.joml.*;

import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;
import tage.networking.IGameConnection.ProtocolType;
import tage.rml.Matrix4;
import tage.rml.Matrix4f;
import tage.rml.Vector3f;
import tage.rml.Vector4f;
import tage.audio.*;

import tage.physics.JBullet.*;
import tage.physics.PhysicsEngine;
import tage.physics.PhysicsObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.collision.dispatch.CollisionObject;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;
	private InputManager im;
	private GhostManager gm;

	private int counter=0;
	private Vector3f currentPosition;
	private Matrix4f initialTranslation, initialRotation, initialScale;
	private double startTime, prevTime, elapsedTime, amt;

	private GameObject tor, avatar, x, y, z, robot, terr, water, dol1, dol2;
	private ObjShape torS, ghostS, dolS, linxS, linyS, linzS, terrS, waterS, dols;
	private AnimatedShape robS;
	private TextureImage doltx, ghostT, torX, robottx, hills, grass;
	private Light light;
	private int lakeIslands;
	private PhysicsEngine physicsEngine;
	private PhysicsObject caps1p, caps2p,caps3p, planeP;

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;
	private float robotHeightAdjust = 0.5f;
	private IAudioManager audioMgr;
	private Sound hereSound, oceanSound;
	private boolean running = false;
	private float vals[] = new float[16];

	private ObjShape npcShape;
	private TextureImage npcTex;
	private UUID clientID;

	

	public MyGame (String serverAddress, int serverPort, String protocol)
	{	super();
		gm = new GhostManager(this);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		if (protocol.toUpperCase().compareTo("TCP") == 0)
			this.serverProtocol = ProtocolType.TCP;
		else
			this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args)
	{	MyGame game = new MyGame(args[0], Integer.parseInt(args[1]), args[2]);
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadSkyBoxes()
	{
		lakeIslands = (engine.getSceneGraph()).loadCubeMap("lakeIslands");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(lakeIslands);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	@Override
	public void loadSounds()
	{
		AudioResource resource1, resource2;
		audioMgr = engine.getAudioManager();
		resource1 = audioMgr.createAudioResource("soapDispenser.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioMgr.createAudioResource("crunchyWind.wav", AudioResourceType.AUDIO_SAMPLE);
		hereSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);
		oceanSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);
		hereSound.initialize(audioMgr);
		oceanSound.initialize(audioMgr);
		hereSound.setMaxDistance(10.0f);
		hereSound.setMinDistance(0.5f);
		hereSound.setRollOff(5.0f);
		oceanSound.setMaxDistance(10.0f);
		oceanSound.setMinDistance(0.5f);
		oceanSound.setRollOff(5.0f);
	}

	@Override
	public void loadShapes()
	{	
		//super.loadShapes();
		robS = new AnimatedShape("spike ball.rkm", "spike ball.rks");
		robS.loadAnimation("WAVE", "spike ball.rka");
		terrS = new TerrainPlane(1000);
		waterS = new Plane();
		torS = new ImportedModel("spikeTrap.obj");
		ghostS = new Sphere();
		dolS = new ImportedModel("dolphinHighPoly.obj");
		linxS = new Line(new Vector3f(0f,0f,0f), new Vector3f(3f,0f,0f));
		linyS = new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,3f,0f));
		linzS = new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,0f,-3f));

		npcShape = new ImportedModel("cone.obj");
	}

	@Override
	public void loadTextures()
	{	
		//super.loadTextures();
		robottx = new TextureImage("spikeBallUV.png");
		hills = new TextureImage("dirt.png");
		grass = new TextureImage("grass.jpg");
		doltx = new TextureImage("Dolphin_HighPolyUV.png");
		ghostT = new TextureImage("redDolphin.jpg");
		torX = new TextureImage("spikeTrapUV.png");
		npcTex = new TextureImage("redDolphin.jpg");
	}

	@Override
	public void buildObjects()
	{	
		//super.buildObjects();


		Matrix4f initialTranslation, initialRotation, initialScale;

		//build physics dolphins
		dol1 = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(1,1.5f,3);
		dol1.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(2f);
		dol1.setLocalScale(initialScale);
		dol1.getRenderStates().setModelOrientationCorrection((new Matrix4f()).rotationY((float)java.lang.Math.toRadians(270.0f)));

		dol2 = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation =(new Matrix4f()).translation(1,1,3);
		dol2.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(2f);
		dol1.setLocalScale(initialScale);
		dol2.getRenderStates().setModelOrientationCorrection((new Matrix4f()).rotationY((float)java.lang.Math.toRadians(90.0f)));



		// Build Robot
		robot = new GameObject(GameObject.root(), robS, robottx);
		initialTranslation = (new Matrix4f()).translation(0, robotHeightAdjust,0);
		robot.setLocalTranslation(initialTranslation);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(180.0f));
		robot.setLocalRotation(initialRotation);
		initialScale = (new Matrix4f()).scaling(0.2f, 0.2f, 0.2f);
		robot.setLocalScale(initialScale);
		robot.getRenderStates().setModelOrientationCorrection((new Matrix4f()).rotationY((float)java.lang.Math.toRadians(90.0f)));
		robot.getRenderStates().hasLighting(true);
		robot.getRenderStates().isEnvironmentMapped(true);

		//build terrain
		terr = new GameObject(GameObject.root(),terrS, grass);
		initialTranslation = (new Matrix4f()).translation(0f,0f,0f);
		terr.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(20.0f, 1f, 20.0f);
		terr.setLocalScale(initialScale);
		terr.setHeightMap(hills);
		terr.getRenderStates().setTiling(1);
		terr.getRenderStates().setTileFactor(10);

		// build dolphin avatar
		avatar = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(-1f,0f,1f);
		avatar.setLocalTranslation(initialTranslation);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(135.0f));
		avatar.setLocalRotation(initialRotation);

		// build torus along X axis
		tor = new GameObject(GameObject.root(), torS, torX);
		initialTranslation = (new Matrix4f()).translation(1,4,3);
		tor.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(.5f);
		tor.setLocalScale(initialScale);

		// add X,Y,-Z axes
		x = new GameObject(GameObject.root(), linxS);
		y = new GameObject(GameObject.root(), linyS);
		z = new GameObject(GameObject.root(), linzS);
		(x.getRenderStates()).setColor(new Vector3f(1f,0f,0f));
		(y.getRenderStates()).setColor(new Vector3f(0f,1f,0f));
		(z.getRenderStates()).setColor(new Vector3f(0f,0f,1f));

		//npc
		GameObject npcGO = new GameObject(GameObject.root(), npcShape, npcTex);
    	npcGO.setLocalTranslation(new Matrix4f().translation(0,0,-5));
    	npcGO.setLocalScale(new Matrix4f().scaling(0.5f));
	}

	@Override
	public void initializeLights()
	{	Light.setGlobalAmbient(.5f, .5f, .5f);

		light = new Light();
		light.setLocation(new Vector3f(0f, 5f, 0f));
		(engine.getSceneGraph()).addLight(light);
	}

	@Override
	public void initializeGame()
	{	
		//super.initializeGame();

		float[]gravity = {0f, .5f, 0f};
		physicsEngine = (engine.getSceneGraph()).getPhysicsEngine();
		physicsEngine.setGravity(gravity);

		float mass = 1.0f;
		float up[] = {0,1,0};
		float down[] = {0,-1,0};
		float radius = 0.75f;
		float height = 2.0f;
		double[] tempTransform;

		Matrix4f translation = new Matrix4f(dol1.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		caps1p = (engine.getSceneGraph()).addPhysicsCapsuleX(mass, tempTransform, radius, height);

		caps1p.setBounciness(0.1f);
		dol1.setPhysicsObject(caps1p);


		translation = new Matrix4f(dol2.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		caps2p = (engine.getSceneGraph()).addPhysicsCapsuleX(mass, tempTransform, radius, height);

		caps2p.setBounciness(0.1f);
		dol2.setPhysicsObject(caps2p);

		
		translation = new Matrix4f(tor.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		caps3p = (engine.getSceneGraph()).addPhysicsCapsuleX(0, tempTransform, radius, height);

		caps3p.setBounciness(0.5f);
		tor.setPhysicsObject(caps3p);

		translation = new Matrix4f(terr.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		planeP = (engine.getSceneGraph()).addPhysicsStaticPlane(tempTransform, up, 0.0f);
		planeP.setBounciness(0.5f);
		terr.setPhysicsObject(planeP);
		engine.enableGraphicsWorldRender();
		engine.enablePhysicsWorldRender();



		robS.playAnimation("WAVE", 0.5f, AnimatedShape.EndType.LOOP,0);
		hereSound.setLocation(robot.getWorldLocation());
		oceanSound.setLocation(terr.getWorldLocation());
		setEarParameters();
		hereSound.play();
		oceanSound.play();


		prevTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		// ----------------- initialize camera ----------------
		positionCameraBehindAvatar();

		// ----------------- INPUTS SECTION -----------------------------
		im = engine.getInputManager();

		// build some action objects for doing things in response to user input
		FwdAction fwdAction = new FwdAction(this, protClient);
		TurnAction turnAction = new TurnAction(this);

		// attach the action objects to keyboard and gamepad components
		im.associateActionWithAllGamepads(
			net.java.games.input.Component.Identifier.Button._1,
			fwdAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(
			net.java.games.input.Component.Identifier.Axis.X,
			turnAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		setupNetworking();

		//----------Npc stuff--------//
		clientID = getClientID(); // however you obtain it
	    protClient.sendPacket("needNPC," + clientID.toString());
		
	}

	public void setEarParameters()
	{
		Camera camera = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		audioMgr.getEar().setLocation(avatar.getWorldLocation());
		audioMgr.getEar().setOrientation(camera.getN(),new Vector3f(0.0f,1.0f,0.0f));
	}

	public GameObject getAvatar() { return avatar; }

	private float[] toFloatArray(double[] arr)
	{
		if (arr == null) return null;
		int n = arr.length;
		float[] ret = new float[n];
		for(int i = 0; i < n; i++)
		{
			ret[i] = (float)arr[i];
		}
		return ret;
	}

	private double[] toDoubleArray(float[] arr)
	{
		if (arr == null) return null;
		int n = arr.length;
		double[] ret = new double[n];
		for(int i = 0; i < n; i++)
		{
			ret[i] = (double)arr[i];
		}
		return ret;
	}

	private void checkForCollisions()
	{
		com.bulletphysics.dynamics.DynamicsWorld dynamicsWorld;
		com.bulletphysics.collision.broadphase.Dispatcher dispatcher;
		com.bulletphysics.collision.narrowphase.PersistentManifold manifold;
		com.bulletphysics.dynamics.RigidBody object1, object2;
		com.bulletphysics.collision.narrowphase.ManifoldPoint contactPoint;

		dynamicsWorld = ((JBulletPhysicsEngine)physicsEngine).getDynamicsWorld();
		dispatcher = dynamicsWorld.getDispatcher();
		int manifoldCount = dispatcher.getNumManifolds();

		for (int i = 0; i< manifoldCount; i++)
		{
			manifold = dispatcher.getManifoldByIndexInternal(i);
			object1 = (com.bulletphysics.dynamics.RigidBody)manifold.getBody0();
			object2 = (com.bulletphysics.dynamics.RigidBody)manifold.getBody1();
			JBulletPhysicsObject obj1 = JBulletPhysicsObject.getJBulletPhysicsObject(object1);
			JBulletPhysicsObject obj2 = JBulletPhysicsObject.getJBulletPhysicsObject(object2);
			for(int j = 0; j < manifold.getNumContacts(); j++)
			{
				contactPoint = manifold.getContactPoint(j);
				if(contactPoint.getDistance() < 0.0f)
				{
					System.out.println("---- hit between" + obj1 + " and " + obj2);
					break;
				}
			}
			
			
		}




	}

	@Override
	public void update()
	{	
		Matrix4f currentTranslation, currentRotation;
		double totalTime = System.currentTimeMillis() - startTime;
		double amtt = totalTime * 0.001;

		if (running)
		{
			AxisAngle4f aa = new AxisAngle4f();
			Matrix4f mat = new Matrix4f();
			Matrix4f mat2 = new Matrix4f().identity();
			Matrix4f mat3 = new Matrix4f().identity();
			checkForCollisions();
			physicsEngine.update((float)elapsedTime);
			for (GameObject go:engine.getSceneGraph().getGameObjects())
			{
				if(go.getPhysicsObject()!= null)
				{
					mat.set(toFloatArray(go.getPhysicsObject().getTransform()));
					mat2.set(3,0,mat.m30());
					mat2.set(3,1,mat.m31());
					mat2.set(3,2,mat.m32());
					go.setLocalTranslation(mat2);

					mat.getRotation(aa);
					mat3.rotation(aa);
					go.setLocalRotation(mat3);
					
				}
			}

		}


		robS.updateAnimation();
		hereSound.setLocation(robot.getWorldLocation());
		oceanSound.setLocation(terr.getWorldLocation());
		setEarParameters();

		Vector3f locA = avatar.getWorldLocation();
		float height = terr.getHeight(locA.x(), locA.z());
		avatar.setLocalLocation(new Vector3f(locA.x(), height + robotHeightAdjust, locA.z()));
		//Vector3f locR = robot.getWorldLocation();
		//robot.setLocalLocation(new Vector3f(loc.x(), height + robotHeightAdjust, loc.z()));

		
		elapsedTime = System.currentTimeMillis() - prevTime;
		prevTime = System.currentTimeMillis();
		amt = elapsedTime * 0.03;
		Camera c = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		
		// build and set HUD
		int elapsTimeSec = Math.round((float)(System.currentTimeMillis()-startTime)/1000.0f);
		String elapsTimeStr = Integer.toString(elapsTimeSec);
		String counterStr = Integer.toString(counter);
		String dispStr1 = "Time = " + elapsTimeStr;
		String dispStr2 = "camera position = "
			+ (c.getLocation()).x()
			+ ", " + (c.getLocation()).y()
			+ ", " + (c.getLocation()).z();
		Vector3f hud1Color = new Vector3f(1,0,0);
		Vector3f hud2Color = new Vector3f(1,1,1);
		(engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, 15, 15);
		(engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, 500, 15);

		// update inputs and camera
		im.update((float)elapsedTime);
		positionCameraBehindAvatar();
		processNetworking((float)elapsedTime);
	}

	private void positionCameraBehindAvatar()
	{	Vector4f u = new Vector4f(-1f,0f,0f,1f);
		Vector4f v = new Vector4f(0f,1f,0f,1f);
		Vector4f n = new Vector4f(0f,0f,1f,1f);
		u.mul(avatar.getWorldRotation());
		v.mul(avatar.getWorldRotation());
		n.mul(avatar.getWorldRotation());
		Matrix4f w = avatar.getWorldTranslation();
		Vector3f position = new Vector3f(w.m30(), w.m31(), w.m32());
		position.add(-n.x()*2f, -n.y()*2f, -n.z()*2f);
		position.add(v.x()*.75f, v.y()*.75f, v.z()*.75f);
		Camera c = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		c.setLocation(position);
		c.setU(new Vector3f(u.x(),u.y(),u.z()));
		c.setV(new Vector3f(v.x(),v.y(),v.z()));
		c.setN(new Vector3f(n.x(),n.y(),n.z()));
	}

	@Override
	public void keyPressed(KeyEvent e)
	{	switch (e.getKeyCode())
		{	case KeyEvent.VK_W:
			{	Vector3f oldPosition = avatar.getWorldLocation();
				Vector4f fwdDirection = new Vector4f(0f,0f,1f,1f);
				fwdDirection.mul(avatar.getWorldRotation());
				fwdDirection.mul(0.05f);
				Vector3f newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
				avatar.setLocalLocation(newPosition);
				protClient.sendMoveMessage(avatar.getWorldLocation());
				break;
			}
			case KeyEvent.VK_D:
			{	Matrix4f oldRotation = new Matrix4f(avatar.getWorldRotation());
				Vector4f oldUp = new Vector4f(0f,1f,0f,1f).mul(oldRotation);
				Matrix4f rotAroundAvatarUp = new Matrix4f().rotation(-.01f, new Vector3f(oldUp.x(), oldUp.y(), oldUp.z()));
				Matrix4f newRotation = oldRotation;
				newRotation.mul(rotAroundAvatarUp);
				avatar.setLocalRotation(newRotation);
				break;
			}
			case KeyEvent.VK_SPACE:
			{
				System.out.println("starting physics");
				running = true;
				break;
			}
		}
		super.keyPressed(e);
	}

	// ---------- NETWORKING SECTION ----------------

	public ObjShape getGhostShape() { return ghostS; }
	public TextureImage getGhostTexture() { return ghostT; }
	public GhostManager getGhostManager() { return gm; }
	public Engine getEngine() { return engine; }
	
	
	private void setupNetworking()
	{	isClientConnected = false;	
		try 
		{	protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} 	catch (UnknownHostException e) 
		{	e.printStackTrace();
		}	catch (IOException e) 
		{	e.printStackTrace();
		}
		if (protClient == null)
		{	System.out.println("missing protocol host");
		}
		else
		{	// Send the initial join message with a unique identifier for this client
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
		}
	}
	
	protected void processNetworking(float elapsTime)
	{	// Process packets received by the client from the server
		if (protClient != null)
			protClient.processPackets();
	}

	public Vector3f getPlayerPosition() { return avatar.getWorldLocation(); }

	public void setIsConnected(boolean value) { this.isClientConnected = value; }
	
	private class SendCloseConnectionPacketAction extends AbstractInputAction
	{	@Override
		public void performAction(float time, net.java.games.input.Event evt) 
		{	if(protClient != null && isClientConnected == true)
			{	protClient.sendByeMessage();
			}
		}
	}

	//-----------------Npc stuff--------------//
		public ObjShape getNPCshape() { return npcShape; }
	    public TextureImage getNPCtexture() { return npcTex; }
}