package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.shape.Arrow;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Box;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

public class BArrow extends Arrow {
	 private static final long serialVersionUID = 1L;

	    private float length = 1;
	    private float width = .25f;

	    private transient Cylinder shaft;
	    private transient Box tip;

	    public BArrow() {
	        this(null, 1.0f, 0.25f);
	    }

	    public BArrow(String name) {
	        super(name, 1.0f, 0.25f);
	    }

	    public BArrow(String name, float length, float width) {
	        super(name);
	        updateGeometry(length, width);
	    }

	    public float getLength() {
	        return length;
	    }

	    public float getWidth() {
	        return width;
	    }

	    public void read(JMEImporter e) throws IOException {
	        super.read(e);
	        InputCapsule capsule = e.getCapsule(this);
	        length = capsule.readFloat("length", 1);
	        width = capsule.readFloat("width", .25f);
	        updateGeometry(length, width);
	    }

	    public void setDefaultColor(ColorRGBA color) {
	        for (int x = 0; x < getQuantity(); x++) {
	            if (getChild(x) instanceof Geometry) {
	                ((Geometry) getChild(x)).setDefaultColor(color);
	            }
	        }
	    }

	    /**
	     * @deprecated use {@link #updateGeometry(float, float)}.
	     */
	    public void setLength(float length) {
	        this.length = length;
	    }

	    public void setSolidColor(ColorRGBA color) {
	        if (shaft != null) {
	            shaft.setSolidColor(color);
	            tip.setSolidColor(color);
	        }
	    }

	    /**
	     * @deprecated use {@link #updateGeometry(float, float)}.
	     */
	    public void setWidth(float width) {
	        this.width = width;
	    }

	    /**
	     * Rebuild this arrow based on a new set of parameters.
	     * 
	     * @param length
	     * @param width
	     */
	    public void updateGeometry(float length, float width) {
	        this.length = length;
	        this.width = width;
	        if (shaft == null) {
	            shaft = new Cylinder("base", 4, 16, width * .75f, length);
	            Quaternion q = new Quaternion();
	            q.fromAngles(90 * FastMath.DEG_TO_RAD, 0, 0);
	            shaft.rotatePoints(q);
	            shaft.rotateNormals(q);
	            attachChild(shaft);
	            //tip = new Pyramid("tip", 2 * width, length / 2f);
	            tip = new Box("tip", new Vector3f(0, length/2f, 0), 2*width, 2*width, 2*width);
	            //tip.translatePoints(0, length, 0);
	            attachChild(tip);
	        } else {
	            shaft.updateGeometry(4, 16, width * .75f, width * .75f, length,
	                    false, false);
	            Quaternion q = new Quaternion();
	            q.fromAngles(90 * FastMath.DEG_TO_RAD, 0, 0);
	            shaft.rotatePoints(q);
	            shaft.rotateNormals(q);
	            tip.updateGeometry(new Vector3f(0, length/2f, 0), 2*width, 2*width, 2*width);
	            //tip.translatePoints(0, length * .75f, 0);
	        }
	    }

	    public void write(JMEExporter e) throws IOException {
	        super.write(e);
	        OutputCapsule capsule = e.getCapsule(this);
	        capsule.write(length, "length", 1);
	        capsule.write(width, "width", .25f);
	    }


}
