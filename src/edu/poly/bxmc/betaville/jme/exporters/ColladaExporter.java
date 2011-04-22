/** Copyright (c) 2008-2010, Brooklyn eXperimental Media Center
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
package edu.poly.bxmc.betaville.jme.exporters;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.QuadMesh;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.TriMesh.Mode;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.scene.state.RenderState.StateType;

import edu.poly.bxmc.betaville.jme.exporters.ColladaEnums.NodeType;
import edu.poly.bxmc.betaville.xml.XMLWriter;

/**
 * Exporter for COLLADA files
 * 
 * <COLLADA>
 * 	<asset/>
 * 	<library_cameras/> // not important
 * 	<library_lights/> // not important
 * 	<library_images/>
 * 	<library_effects/>
 * 	<library_materials/>
 * 	<library_geometries/>
 * 	<library_animations/>
 * 	<library_controllers/>
 * 	<library_visual_scenes/>
 * 	<scene/>
 * 
 * @author Skye Book
 * @experimental - incomplete
 *
 */
public class ColladaExporter extends XMLWriter implements MeshExporter {
	private static Logger logger = Logger.getLogger(ColladaExporter.class);

	private Spatial exportTarget;
	private boolean exportLocal;

	private ArrayList<Element> effects = new ArrayList<Element>();
	private ArrayList<Element> materials = new ArrayList<Element>();

	// These are the basic elements of a COLLADA document
	private Element asset = new Element("asset");
	private Element library_cameras = new Element("library_cameras");
	private Element library_lights = new Element("library_lights");
	private Element library_images = new Element("library_images");
	private Element library_effects = new Element("library_effects");
	private Element library_materials = new Element("library_materials");
	private Element library_geometries = new Element("library_geometries");
	private Element library_animations = new Element("library_animations");
	private Element library_controllers = new Element("library_controllers");
	private Element library_visual_scenes = new Element("library_visual_scenes");
	private Element scene = new Element("scene");
	
	private Namespace namespace;

	/**
	 * @param rootElementName
	 * @param file
	 * @throws IOException
	 */
	public ColladaExporter(File file, Spatial toExport, boolean exportLocal)
	throws IOException {
		super("COLLADA", file);
		logger.info("COLLADA export started for " + toExport.getName());
		namespace = Namespace.getNamespace("http://www.collada.org/2005/11/COLLADASchema");
		rootElement.setAttribute(new Attribute("version", "1.4.1"));
		exportTarget=toExport;
		this.exportLocal=exportLocal;

		createAsset();
		logger.info("asset element exported");
		createLibCameras();
		logger.info("camera element exported");
		createLibLights();
		logger.info("lighting element exported");
		createLibImages();
		logger.info("images element exported");

		// need materials first - generates the material entries in the same function
		createLibEffects();
		logger.info("effects and materials elements exported");
		createLibGeometries();
		logger.info("geometry elements exported");

		createLibAnimations();
		logger.info("anomations element exported");
		createLibControllers();
		logger.info("controllers element exported");
		createLibVisualScenes();
		logger.info("visual scene elements exported");
		createScene();
		logger.info("scene element exported");
	}
	
	public void writeData() throws IOException{
		assignNamespaces(rootElement);
		super.writeData();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.exporters.MeshExporter#exportTriMesh(com.jme.scene.TriMesh)
	 */
	public void exportTriMesh(TriMesh mesh) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.exporters.MeshExporter#exportQuadMesh(com.jme.scene.QuadMesh)
	 */
	public void exportQuadMesh(QuadMesh mesh) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.exporters.MeshExporter#exportSharedMesh(com.jme.scene.SharedMesh)
	 */
	public void exportSharedMesh(SharedMesh mesh) {
		// TODO Auto-generated method stub

	}
	
	private void assignNamespaces(Element element){
		element.setNamespace(namespace);
		for(Object child : element.getChildren()){
			if(child instanceof Element){
				assignNamespaces((Element)child);
			}
		}
	}

	private void createAsset(){

		Element created = new Element("created");
		String date = formatISO8601(Calendar.getInstance());
		created.addContent(date);
		Element modified = new Element("modified");
		modified.addContent(date);
		Element unit = new Element("unit");
		unit.setAttribute(new Attribute("name", "meter"));
		unit.setAttribute(new Attribute("meter", "1.0"));
		Element up = new Element("up_axis");
		up.addContent("Y_UP");

		asset.addContent(created);
		asset.addContent(modified);
		asset.addContent(unit);
		asset.addContent(up);
		rootElement.addContent(asset);
	}

	private void createLibCameras(){
		rootElement.addContent(library_cameras);
	}

	private void createLibLights(){
		rootElement.addContent(library_lights);
	}

	private void createLibImages(){
		rootElement.addContent(library_images);
	}

	private void createLibEffects(){
		createLibEffectsImpl(exportTarget);
		rootElement.addContent(library_effects);
		rootElement.addContent(library_materials);
	}

	private void createLibEffectsImpl(Spatial s){
		if(!(s instanceof Node)){
			if(s.getRenderState(StateType.Material)!=null){
				Element matEffect = createMatEffect((MaterialState)s.getRenderState(StateType.Material), s.getName());
				library_effects.addContent(matEffect);
				library_materials.addContent(createMaterialEntry(matEffect.getAttributeValue("id"), s.getName()));
			}
		}
		else{
			if(((Node)s).getChildren()!=null){
				for(Spatial child : ((Node)s).getChildren()){
					createLibEffectsImpl(child);
				}
			}
		}
	}

	private Element createMatEffect(MaterialState ms, String meshName){
		Element effect = new Element("effect");
		effect.setAttribute(new Attribute("id", meshName+"-material-effect"));

		Element profile = new Element("profile_COMMON");
		Element technique = new Element("technique");
		technique.setAttribute(new Attribute("sid", "common"));

		Element lambert = new Element("lambert");
		Element emission = new Element("emission");
		emission.addContent(createColorElement(ms.getEmissive()));
		Element ambient = new Element("ambient");
		ambient.addContent(createColorElement(ms.getAmbient()));
		Element diffuse = new Element("diffuse");
		diffuse.addContent(createColorElement(ms.getDiffuse()));
		Element reflective = new Element("reflective");
		reflective.addContent(createColorElement(ms.getSpecular()));
		Element reflectivity = new Element("reflectivity");
		reflectivity.addContent(createFloatElement(ms.getShininess()));

		//TODO: How do these features work?
		Element transparent = new Element("transparent");
		transparent.addContent(createColorElement(0, 0, 0, 1));
		Element transparency = new Element("transparency");
		transparency.addContent(createFloatElement(1));
		Element indexOfRefraction = new Element("index_of_refraction");
		indexOfRefraction.addContent(createFloatElement(1));

		lambert.addContent(emission);
		lambert.addContent(ambient);
		lambert.addContent(diffuse);
		lambert.addContent(reflective);
		lambert.addContent(reflectivity);
		//lambert.addContent(transparent);
		//lambert.addContent(transparency);
		//lambert.addContent(indexOfRefraction);

		technique.addContent(lambert);
		profile.addContent(technique);


		Element extra = new Element("extra");
		// process front/back faces for this material
		Element eTechnique = new Element("technique");
		eTechnique.setAttribute(new Attribute("profile", "MAX3D"));
		Element dblSided = new Element("double_sided");
		dblSided.addContent("" + (ms.getMaterialFace().equals(MaterialFace.FrontAndBack) ? 1:0));
		eTechnique.addContent(dblSided);
		extra.addContent(eTechnique);

		effect.addContent(profile);
		effect.addContent(extra);
		return effect;
	}
	
	private Element createMaterialEntry(String materialEffectName, String meshName){
		Element material = new Element("material");
		material.setAttribute(new Attribute("id", getMaterialName(meshName)));
		material.setAttribute(new Attribute("name", getMaterialName(meshName)));
		material.addContent(createInstanceEffect(materialEffectName));
		return material;
	}

	private String getMaterialName(String meshName){
		return meshName+"-material";
	}

	private Element createInstanceEffect(String effectID){
		Element instance = new Element("instance_effect");
		instance.setAttribute(new Attribute("url", "#"+effectID));
		return instance;
	}

	private void createLibGeometries(){
		createLibGeometriesImpl(exportTarget);
		rootElement.addContent(library_geometries);
	}

	private void createLibGeometriesImpl(Spatial s){
		if(!(s instanceof Node)){
			if(s instanceof SharedMesh){
				logger.warn(SharedMesh.class.getName()+" is not yet supported in this exporter, exporting the target");
				logger.info("Creating COLLADA element from TriMesh " + s.getName());
				Element trimeshElement = createGeometryElement(((SharedMesh)s).getDeepTarget());
				library_geometries.addContent(trimeshElement);
			}
			else if(s instanceof TriMesh){
				logger.info("Creating COLLADA element from TriMesh " + s.getName());
				Element trimeshElement = createGeometryElement((TriMesh)s);
				library_geometries.addContent(trimeshElement);
			}
			// TODO: quadmesh
		}
		else{
			if(((Node)s).getChildren()!=null){
				for(Spatial child : ((Node)s).getChildren()){
					createLibGeometriesImpl(child);
				}
			}
		}
	}

	/**
	 * Created an element representing the supplied TriMesh.
	 * @param trimesh The TriMesh to create the COLLADA geometry element from.
	 * @return
	 */
	private Element createGeometryElement(TriMesh trimesh){
		Element geometry = new Element("geometry");
		geometry.setAttribute(new Attribute("id", trimesh.getName()));
		Element mesh = new Element("mesh");

		// Positions

		Element positionsSource = createSourceElement();
		positionsSource.setAttribute(new Attribute("id", trimesh.getName()+"-positions"));
		Element positionsArray = createFloatArray(positionsSource.getAttributeValue("id")+"-array", trimesh.getVertexCount()*3);
		positionsArray.addContent(arrayFromFloats(trimesh.getVertexBuffer()));
		Element positionsArrayTechnique = createCommonTechnique();
		Element pATAccessor = createAccessor(positionsArray.getAttributeValue("id"), trimesh.getVertexCount(), 3);
		pATAccessor.addContent(createParam("X", "float"));
		pATAccessor.addContent(createParam("Y", "float"));
		pATAccessor.addContent(createParam("Z", "float"));
		positionsArrayTechnique.addContent(pATAccessor);

		positionsSource.addContent(positionsArray);
		positionsSource.addContent(positionsArrayTechnique);
		mesh.addContent(positionsSource);

		// Normals

		Element normalsSource = createSourceElement();
		normalsSource.setAttribute(new Attribute("id", trimesh.getName()+"-normals"));
		Element normalsArray = createFloatArray(normalsSource.getAttributeValue("id")+"-array", trimesh.getNormalBuffer().capacity());
		normalsArray.addContent(arrayFromFloats(trimesh.getNormalBuffer()));
		Element normalsArrayTechnique = createCommonTechnique();
		Element nATAccessor = createAccessor(normalsArray.getAttributeValue("id"), trimesh.getVertexCount(), 3);
		nATAccessor.addContent(createParam("X", "float"));
		nATAccessor.addContent(createParam("Y", "float"));
		nATAccessor.addContent(createParam("Z", "float"));
		normalsArrayTechnique.addContent(nATAccessor);

		normalsSource.addContent(normalsArray);
		normalsSource.addContent(normalsArrayTechnique);
		mesh.addContent(normalsSource);

		// Texture Maps
		ArrayList<Element> mapSources = new ArrayList<Element>();
		ArrayList<TexCoords> texCoords = trimesh.getTextureCoords();
		for(int i=0; i<texCoords.size(); i++){
			TexCoords coord = texCoords.get(i);
			Element map = createSourceElement();
			map.setAttribute(new Attribute("id", trimesh.getName()+"-map-"+i));
			Element mapArray = createFloatArray(map.getAttributeValue("id")+"-array", coord.coords.capacity());
			mapArray.addContent(arrayFromFloats(coord.coords));
			Element mapArrayTechnique = createCommonTechnique();
			Element mATAccessor = createAccessor(mapArray.getAttributeValue("id"), (coord.coords.capacity()/coord.perVert), coord.perVert);
			mATAccessor.addContent(createParam("S","float"));
			mATAccessor.addContent(createParam("T","float"));
			if(coord.perVert==3) mATAccessor.addContent(createParam("W", "float"));
			mapArrayTechnique.addContent(mATAccessor);
			map.addContent(mapArray);
			map.addContent(mapArrayTechnique);
			mapSources.add(map);
		}

		// push any created maps to the source element
		for(Element map : mapSources){
			mesh.addContent(map);
		}

		// Colors
		Element colorsSource = createSourceElement();
		if(trimesh.getColorBuffer()!=null){
			if(trimesh.getColorBuffer().capacity()>0){
				colorsSource = createSourceElement();
				colorsSource.setAttribute(new Attribute("id", trimesh.getName()+"-colors"));
				Element colorArray = new Element(colorsSource.getAttributeValue("id")+"-array");
				colorArray.setAttribute(new Attribute("count", ""+trimesh.getColorBuffer().capacity()));
				colorArray.addContent(arrayFromFloats(trimesh.getColorBuffer()));
				Element colorArrayTechnique = createCommonTechnique();
				Element cATAccessor = createAccessor(colorArray.getAttributeValue("id"), trimesh.getColorBuffer().capacity(), 4);
				cATAccessor.addContent(createParam("R", "float"));
				cATAccessor.addContent(createParam("G", "float"));
				cATAccessor.addContent(createParam("B", "float"));
				cATAccessor.addContent(createParam("A", "float"));
				colorArrayTechnique.addContent(cATAccessor);
				colorsSource.addContent(colorArray);
				colorsSource.addContent(colorArrayTechnique);
				mesh.addContent(colorsSource);
			}
		}

		// Vertices
		Element vertices = new Element("vertices");
		vertices.setAttribute(new Attribute("id", trimesh.getName()+"-vertices"));
		vertices.addContent(createInput("POSITION", positionsSource.getAttributeValue("id")));
		mesh.addContent(vertices);

		
		Element triangles = new Element("triangles");
		triangles.setAttribute(new Attribute("material", getMaterialName(trimesh.getName())));
		triangles.setAttribute(new Attribute("count", ""+trimesh.getTriangleCount()));
		int offset=0;
		triangles.addContent(createInput("VERTEX", vertices.getAttributeValue("id"), offset));
		offset++;
		//triangles.addContent(createInput("NORMAL", normalsSource.getAttributeValue("id"), offset));
		//offset++;
		if(mesh.getParent()!=null){
			for(int i=0; i<mapSources.size(); i++){
				triangles.addContent(createInput("TEXCOORD", mapSources.get(i).getAttributeValue("id"), offset, i));
			}
			offset++;
		}
		if(colorsSource.getParent()!=null){
			triangles.addContent(createInput("COLOR", colorsSource.getAttributeValue("id"), offset));
			offset++;
		}
		
		Element primArray = new Element("p");
		
		primArray.addContent(arrayFromInts(trimesh.getIndexBuffer()));
		triangles.addContent(primArray);
		mesh.addContent(triangles);
		// end polylist
		geometry.addContent(mesh);
		return geometry;
	}
	
	private String createPolyList(TriMesh t){
		if (t.getMode().equals(Mode.Strip)) return createStripPolyList(t);
		if (t.getMode().equals(Mode.Triangles)) return createTrianglesPolyList(t);
		else return createFanPolyList(t);
	}
	
	// creates as polylist based on GL_TRIANGLE_STRIP
	private String createStripPolyList(TriMesh t){
		return"";
	}
	
	// creates as polylist based on GL_TRIANGLES
	private String createTrianglesPolyList(TriMesh t){
		return"";
	}
	
	// creates as polylist based on GL_TRIANGLE_FAN
	private String createFanPolyList(TriMesh t){
		return"";
	}

	private Element createInput(String semantic, String sourceName, int offset, int set){
		Element input = createInput(semantic, sourceName, offset);
		input.setAttribute(new Attribute("set", ""+set));
		return input;
	}

	private Element createInput(String semantic, String sourceName, int offest){
		Element input = createInput(semantic, sourceName);
		input.setAttribute(new Attribute("offset", ""+offest));
		return input;
	}

	private Element createInput(String semantic, String sourceName){
		Element input = createInput(semantic);
		input.setAttribute(new Attribute("source", "#"+sourceName));
		return input;
	}

	private Element createInput(String semantic){
		Element input = new Element("input");
		input.setAttribute(new Attribute("semantic", semantic));
		return input;
	}

	private Element createParam(String name, String type){
		Element param = new Element("param");
		param.setAttribute(new Attribute("name", name));
		param.setAttribute(new Attribute("type", type));
		return param;
	}

	private Element createAccessor(String source, int count, int stride){
		Element accessor = new Element("accessor");
		accessor.setAttribute(new Attribute("source", "#"+source));
		accessor.setAttribute(new Attribute("count", ""+count));
		accessor.setAttribute(new Attribute("stride", ""+stride));
		return accessor;
	}

	private Element createCommonTechnique(){
		return new Element("technique_common");
	}

	private String arrayFromInts(IntBuffer buffer){
		StringBuilder array=new StringBuilder();
		buffer.rewind();

		while(buffer.hasRemaining()){
			array.append(buffer.get()+" ");
		}
		return array.toString();
	}

	private String arrayFromFloats(FloatBuffer buffer){
		StringBuilder array=new StringBuilder();
		buffer.rewind();
		while(buffer.hasRemaining()){
			array.append(buffer.get()+" ");
		}
		return array.toString();
	}

	private String arrayFromVector3f(Vector3f[] vectors){
		StringBuilder array = new StringBuilder();
		int counter = 0;
		for(Vector3f vector : vectors){
			array.append(vector.x+" ");
			array.append(vector.y+" ");
			array.append(vector.z+" ");
			counter+=3;
		}
		System.out.println(counter + " written from " + (vectors.length*3));
		return array.toString();
	}

	private Element createFloatArray(String name, int size){
		Element array = new Element("float_array");
		array.setAttribute(new Attribute("id", name));
		array.setAttribute(new Attribute("count", ""+size));
		return array;
	}

	private Element createSourceElement(){
		return new Element("source");
	}

	private void createLibAnimations(){
		rootElement.addContent(library_animations);
	}

	private void createLibControllers(){
		rootElement.addContent(library_controllers);
	}

	private void createLibVisualScenes(){
		Element visualScene = createVisualScene(exportTarget);
		createVisualLibSceneImpl(visualScene, exportTarget);
		library_visual_scenes.addContent(visualScene);
		rootElement.addContent(library_visual_scenes);
	}

	private void createVisualLibSceneImpl(Element visualScene, Spatial s){
		// handle nodes different as they have objects under them
		logger.info("Processing " + s.getName() + " for visual scene");
		if(s instanceof Node){
			if(((Node)s).getChildren()!=null){
				for(Spatial child : (((Node)s).getChildren())){
					createVisualLibSceneImpl(visualScene, child);
				}
			}
		}
		else if(s instanceof Geometry){
			// deal with single geometry
			logger.info(s.getClass().getName() + " found named: " + s.getName());
			if(s instanceof TriMesh){
				Element node = createNode(s.getName(), NodeType.NODE, createGeometryInstance(s.getName()));
				visualScene.addContent(node);
				node.addContent(createTranslateElement(s.getLocalTranslation()));
				for(Element angle : createRotationComponent(s.getLocalRotation())){
					node.addContent(angle);
				}
				node.addContent(createScaleElement(s.getLocalScale()));
			}
			else{
				// whine
			}
		}
	}
	
	private Element createTranslateElement(Vector3f translation){
		Element element = new Element("translate");
		//element.setAttribute(new Attribute("sid", "location"));
		element.addContent(translation.x + " " + translation.y + " " + translation.z);
		return element;
	}
	
	private Element createScaleElement(Vector3f scale){
		Element element = new Element("scale");
		//element.setAttribute(new Attribute("sid", "scale"));
		element.addContent(scale.x + " " + scale.y + " " + scale.z);
		return element;
	}

	private Element[] createRotationComponent(Quaternion q){
		float[] angles = q.toAngles(null);
		Element x = new Element("rotate");
		//x.setAttribute(new Attribute("sid", "rotateX"));
		x.addContent("1 0 0 "+angles[0]);
		Element y = new Element("rotate");
		//y.setAttribute(new Attribute("sid", "rotateY"));
		y.addContent("0 1 0 "+angles[1]);
		Element z = new Element("rotate");
		//z.setAttribute(new Attribute("sid", "rotateZ"));
		z.addContent("0 0 1 "+angles[2]);
		return new Element[]{x,y,z};
	}

	private Element createGeometryInstance(String meshName){
		Element instance = new Element("instance_geometry");
		instance.setAttribute(new Attribute("url","#"+findGeometry(meshName).getAttributeValue("id")));
		return instance;
	}

	private Element findGeometry(String meshName){
		for(Object geometry : library_geometries.getChildren()){
			//System.out.println("Geometry: " + ((Element)geometry).getAttributeValue("id") + " Looking for " + meshName);
			if(((Element)geometry).getAttributeValue("id").equals(meshName)){
				return (Element)geometry;
			}
		}
		return null;
	}

	private Element createVisualScene(Spatial s){
		Element visualScene = new Element("visual_scene");
		visualScene.setAttribute(new Attribute("id", s.getName()+"-scene"));
		visualScene.setAttribute(new Attribute("name", s.getName()+"-scene"));
		return visualScene;
	}

	private Element createNode(String id, NodeType nodeType, Element instance){
		Element node = new Element("node");
		node.setAttribute(new Attribute("id", id+"-node"));
		node.setAttribute(new Attribute("type", nodeType.name()));
		node.addContent(instance);
		return node;
	}

	private void createScene(){
		Element visualSceneInstance = new Element("instance_visual_scene");
		visualSceneInstance.setAttribute(new Attribute("url","#"+exportTarget.getName()+"-scene"));
		scene.addContent(visualSceneInstance);
		rootElement.addContent(scene);
	}

	// Utility element factories

	private Element createFloatElement(float f){
		Element fElement = new Element("float");
		fElement.addContent(""+f);
		return fElement;
	}

	private Element createColorElement(ColorRGBA color){
		return createColorElement(color.r, color.g, color.b, color.a);
	}

	private Element createColorElement(float r, float g, float b, float a){
		Element color = new Element("color");
		color.addContent(r+" "+g+" "+b+" "+a);
		return color;
	}

	/**
	 * From the Java forums: http://forums.sun.com/thread.jspa?messageID=768476
	 * @param cal
	 * @return
	 */
	private String formatISO8601(Calendar cal) {

		/*
		 * create ISO 8601 formatter for Calendar objects
		 */
		MessageFormat iso8601 = new MessageFormat("{0,time}{1,number,+00;-00}:{2,number,00}") ;

		// need to shove a date formatter that is cognizant of the
		// calendar's time zone into the message formatter
		//
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") ;
		df.setTimeZone(cal.getTimeZone()) ;
		iso8601.setFormat(0, df) ;

		/*
		 * calculate the time zone offset from UTC in hours and minutes at the current time
		 */
		long zoneOff = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET) ;
		zoneOff /= 60000L ;  // in minutes
		int zoneHrs = (int) (zoneOff / 60L) ;
		int zoneMins = (int) (zoneOff % 60L) ;
		if (zoneMins < 0) zoneMins = -zoneMins ;

		return (iso8601.format(new Object[] {
				cal.getTime(),
				new Integer(zoneHrs),
				new Integer(zoneMins)
		}
		)) ;
	}
}
