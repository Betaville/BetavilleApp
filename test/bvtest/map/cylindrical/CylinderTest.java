/**
 * 
 */
package bvtest.map.cylindrical;

import java.io.File;
import java.net.MalformedURLException;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.image.Texture;
import com.jme.image.Texture.WrapMode;
import com.jme.input.FirstPersonHandler;
import com.jme.input.MouseInput;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Triangle;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import edu.poly.bxmc.betaville.jme.map.Circle;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;

/**
 * @author Skye Book
 *
 */
public class CylinderTest extends SimpleGame {
	private Cylinder cylinder;
	private BoundingVolume capsule;
	
	private Node boxes;
	
	private int glRadius = 500;
	private int glHeight = 1500;
	
	private Text location;
	private Text target;
	
	private Ray forwardFacing;
	private TrianglePickResults pickResults = new TrianglePickResults();
	private Sphere pickingSphere;
	private Box[] pickingBoxes = new Box[3];
	
	private Ray[] rays;
	
	private TextureState instate;
	private TextureState outstate;
	private Texture inside;
	private Texture outside;
	private boolean inCylinder=true;
	
	/**
	 * 
	 */
	public CylinderTest(){
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame(){
		cam.setFrustumFar(10000);
		//cam.setUp(Vector3f.UNIT_Z);
		//cam.setDirection(Vector3f.UNIT_X);
		rootNode.setLocalRotation(Rotator.angleX(90));
		boxes = new Node("boxes");
		rootNode.attachChild(boxes);
		((FirstPersonHandler)input).getKeyboardLookHandler().setMoveSpeed(150);
		cylinder = new Cylinder("cylinder", 12, 64, glRadius, glHeight, false, true);
		pickingSphere = new Sphere("pickingSphere", new Vector3f(), 10, 10, 5);
		rootNode.attachChild(pickingSphere);
		
		for(int i=0; i<pickingBoxes.length; i++){
			pickingBoxes[i] = new Box(""+i, new Vector3f(), 3, 3, 3);
			rootNode.attachChild(pickingBoxes[i]);
		}
		
		// rotate the cylinder so that the height is aligned along the Y axis
		rootNode.attachChild(cylinder);
		//cylinder.setRenderState(display.getRenderer().createWireframeState());
		cylinder.updateRenderState();
		capsule = new BoundingSphere();
		cylinder.setModelBound(capsule);
		cylinder.updateModelBound();
		instate = display.getRenderer().createTextureState();
		outstate = display.getRenderer().createTextureState();
		MaterialState transparency = display.getRenderer().createMaterialState();
		ColorRGBA color = new ColorRGBA(1,1,1,.75f);
		transparency.setAmbient(color);
		transparency.setEmissive(color);
		transparency.setSpecular(color);
		transparency.setDiffuse(color);
		transparency.setMaterialFace(MaterialFace.Front);
		//cylinder.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		//cylinder.setRenderState(transparency);
		try {
			inside = TextureManager.loadTexture(new File("test/utm.jpg").toURI().toURL(), true);
			instate.setTexture(inside);
			inside.setWrap(WrapMode.BorderClamp);
			inside.setRotation(Rotator.angleZ(180));
			inside.setTranslation(new Vector3f(1, 1, 0));
			
			outside = TextureManager.loadTexture(new File("test/utm.jpg").toURI().toURL(), false);
			outstate.setTexture(outside);
			outside.setWrap(WrapMode.BorderClamp);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		cylinder.setRenderState(instate);
		
		//cylinder.setRenderState(display.getRenderer().createWireframeState());
		cylinder.updateRenderState();
		
		location = Text.createDefaultTextLabel("locationtext", "Hello!");
		location.setLightCombineMode(LightCombineMode.Off);
		//rootNode.attachChild(location);
		
		target = Text.createDefaultTextLabel("targettext", "+");
		target.setLightCombineMode(LightCombineMode.Off);
		target.setLocalTranslation(new Vector3f((DisplaySystem.getDisplaySystem().getWidth() / 2), (DisplaySystem.getDisplaySystem().getHeight() / 2), 0.0f));
		target.setLocalTranslation(new Vector3f(50, 0, 50));
		rootNode.attachChild(target);
		
		createRays();
		putBoxesAtRayIntersections();
		rootNode.updateRenderState();
		DataNode dn = new DataNode("", new Vector3f(), getLocationInSpace(new GPSCoordinate(0, 42d, -73d)));
		rootNode.attachChild(dn);
	}
	
	private void createRays(){
		rays = new Ray[60];
		float interval = 1f/15f;
		float x = 1f;
		float y = 0f;
		
		Vector3f position;
		Vector3f origin = new Vector3f(0, 0, 0);
		
		for(int i=0; i<15; i++){
			x-=interval;
			y+=interval;
			position = new Vector3f(x, y, 0);
			rays[i]=new Ray(origin, position);
		}
		
		for(int i=0; i<15; i++){
			x-=interval;
			y-=interval;
			position = new Vector3f(x, y, 0);
			rays[15+i]=new Ray(origin, position);
		}
		
		for(int i=0; i<15; i++){
			x+=interval;
			y-=interval;
			position = new Vector3f(x, y, 0);
			rays[30+i]=new Ray(origin, position);
		}
		
		for(int i=0; i<15; i++){
			x+=interval;
			y+=interval;
			position = new Vector3f(x, y, 0);
			rays[45+i]=new Ray(origin, position);
		}
	}
	
	private void putBoxesAtRayIntersections(){
		TrianglePickResults tr = new TrianglePickResults();
		tr.setCheckDistance(true);
		Vector3f[] triangle = new Vector3f[3];
		Vector3f pointOfIntersection = new Vector3f();
		Vector3f multipliedLocation;
		for(int r=0; r<rays.length; r++){
			tr.clear();
			cylinder.findPick(rays[r], tr);
			//System.out.println("ray "+r+" has "+tr.getNumber()+" results");
			for(int i=0; i<tr.getNumber(); i++){
				System.out.println("_______");
				Box b = new Box("Box"+r+"("+i+")", new Vector3f(0,0,0), 5, 5, 5);
				//System.out.println("pick distance: " + tr.getPickData(i).getDistance());
				
				multipliedLocation = rays[r].getDirection().clone();
				multipliedLocation = multipliedLocation.multLocal(tr.getPickData(i).getDistance());
				System.out.println("multiplied: " + multipliedLocation.toString());
				
				
				cylinder.getTriangle(tr.getPickData(i).getTargetTris().get(0), triangle);
				Triangle t = new Triangle(triangle[0], triangle[1], triangle[2]);
				for(Vector3f vLoc : triangle){
					Box vecBox = new Box("vecBox", vLoc, 3, 3, 3);
					vecBox.setSolidColor(ColorRGBA.red);
					//rootNode.attachChild(vecBox);
				}
				rays[r].intersectWhere(t, pointOfIntersection);
				System.out.println("POI: " + pointOfIntersection.toString());
				
				b.setLocalTranslation(multipliedLocation);
				//b.setLocalTranslation(pointOfIntersection);
				
				
				System.out.println("actual: "+b.getLocalTranslation().toString());
				boxes.attachChild(b);
			}
		}
	}
	
	protected void simpleUpdate(){
		location.print(((capsule.contains(cam.getLocation()) ? "Inside Cylinder" : "Outside Cylinder"))+" " +(int)cam.getLocation().x+", "+(int)cam.getLocation().y+", "+(int)cam.getLocation().z+" DIRECTION" +cam.getDirection().x+", "+cam.getDirection().y+", "+cam.getDirection().z);
		if(capsule.contains(cam.getLocation())){
			//if(inCylinder) return;
			//cylinder.setRenderState(instate);
			//cylinder.updateRenderState();
			//cameraTrackedPickng();
			inCylinder=true;
		}
		else{
			//if(!inCylinder) return;
			//cylinder.setRenderState(outstate);
			//cylinder.updateRenderState();
			inCylinder=false;
		}
	}
	
	private void cameraTrackedPickng(){
		Vector2f screenPosition = new Vector2f(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		Ray rayToUse = new Ray(cam.getLocation(), worldCoords.subtractLocal(cam.getLocation()));
		
		rayToUse.getDirection().normalizeLocal();
		
		pickResults.clear();
		cylinder.findPick(rayToUse, pickResults);
		if(pickResults.getNumber()>0){
			System.out.println(pickResults.getPickData(0).getTargetMesh().getName());
			Vector3f pointOfCameraIntersection = new Vector3f();
			Vector3f[] triangle = new Vector3f[3];
			if(pickResults.getPickData(0).getTargetTris().size()==0) return;
			System.out.println("target triangle "+pickResults.getPickData(0).getTargetTris().get(0));
			cylinder.getTriangle(pickResults.getPickData(0).getTargetTris().get(0), triangle);
			
			for(int i=0; i<triangle.length; i++){
				pickingBoxes[i].setLocalTranslation(triangle[i]);
			}
			
			System.out.println(rayToUse.intersectWhere(triangle[0], triangle[1], triangle[2], pointOfCameraIntersection));
			pickingSphere.setLocalTranslation(pointOfCameraIntersection);
			System.out.println(pointOfCameraIntersection.toString());
		}
		else System.out.println("no!");
	}
	
	private Vector3f getLocationInSpace(ILocation location){
		boolean debug=false;
		
		UTMCoordinate place = location.getUTM();
		int firstRay = place.getLonZone()-1;
		int secondRay = firstRay+1;
		
		// if the first ray is at zone 59, the second ray
		// would be at 0 rather than 60
		if(firstRay==59){
			secondRay=0;
		}

		float arcLength = Circle.arcLength(glRadius, (float)Math.toRadians(6));
		System.out.println("Arc length: " + arcLength);

		Ray first = rays[firstRay];
		Ray second = rays[secondRay];

		if(debug){
			Sphere s1 = new Sphere("s1", new Vector3f(), 10, 10, 10);
			s1.setLocalTranslation(first.getDirection().mult(glRadius));
			rootNode.attachChild(s1);

			Sphere s2 = new Sphere("s2", new Vector3f(), 10, 10, 10);
			s2.setLocalTranslation(second.getDirection().mult(glRadius));
			rootNode.attachChild(s2);
		}

		Vector3f median = second.getDirection().clone();
		median.interpolate(first.getDirection(), .5f);
		System.out.println("First: " + first.getDirection().toString());
		System.out.println("Second: " + second.getDirection().toString());
		
		System.out.println("Median: " + median.toString());
		Vector3f locationInSpace = median.mult(glRadius+125);
		float sizeOfHemisphere = glHeight/2f;
		
		
		float percentageOfHemi = (float) (location.getGPS().getLatitude()/90f);
		
		if(place.getLatZone()>'M'){
			locationInSpace.setZ((percentageOfHemi*sizeOfHemisphere)*-1);
			System.out.println("percent " + percentageOfHemi);
			System.out.println(locationInSpace.getZ());
		}

		if(debug){
			Box box = new Box(location.getUTM().toString(), new Vector3f(), 5, 5, 5);
			rootNode.attachChild(box);
			box.setLocalTranslation(locationInSpace);
			System.out.println("box location " + box.getLocalTranslation().toString());
		}

		float angleBetween = (float)Math.toDegrees(first.getDirection().angleBetween(second.getDirection()));
		System.out.println("angle between: " + angleBetween);
		return locationInSpace;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CylinderTest ct = new CylinderTest();
		ct.start();
	}

}
