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
package edu.poly.bxmc.betaville.jme.map;

//~--- non-JDK imports --------------------------------------------------------

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

//~--- JDK imports ------------------------------------------------------------

import java.nio.FloatBuffer;

/**
 * The Navigator is a combination compass and waypoint display.
 * The North indicator 'card' rotates with changes in the camera's heading
 * (the direction that it's pointing) relative to North; the outer waypoint
 * ring rotates with changes in the camera's location relative to the waypoint
 * (the bearing from camera to waypoint). Both North and the waypoint are
 * user-settable. If no waypoint is set (null) then the outer waypoint
 * ring is not visible.
 * * Note: Don't forget to call simpleUpdate from the game simpleUpdate()
 * @author Kropotkin (with lots of code and comments from HUD tutorial)
 */
public class Navigator {
    protected static DisplaySystem __display = DisplaySystem.getDisplaySystem();

    // in the case of the compass, 'up' is along the Z axis.
    protected Vector3f _upVector         = new Vector3f(0, 0, 1);
    // for the waypoint, it's along the Y axis
    protected Vector3f _waypointUpVector = new Vector3f(0, 1, 0);

    // default values
    protected Vector3f   _northVec     = new Vector3f(0, 0, -10).normalizeLocal();
    protected Vector2f   _north2f      = new Vector2f(0, -10).normalizeLocal();
    protected Quaternion _compassQuat  = new Quaternion();
    protected Quaternion _waypointQuat = new Quaternion();
    protected Vector2f   _camDirection2f;
    protected Vector2f   _camLocation2f;
    protected Vector3f   _cameraDirectionVec;
    protected Vector3f   _cameraLocationVec;
    protected float      _compassLocalScale;
    protected float      _compassLocationX;
    protected float      _compassLocationY;

    // inner compass
    protected Node _compassNode;
    protected Quad _compassQuad;
    protected int  _compassTextureHeight;
    protected int  _compassTextureWidth;
    private Node   _parentNode;

    // outer waypoint
    protected Vector3f _waypoint;
    protected Vector2f _waypoint2f;
    protected Node     _waypointNode;
    protected Quad     _waypointQuad;
    protected int      _waypointTextureHeight;
    protected int      _waypointTextureWidth;

    public Navigator(float localScale, Node parentNode) {
        _compassLocalScale = localScale;
        _parentNode        = parentNode;
        _compassNode       = new Node("_compassNode");
        _compassQuad       = new Quad("_compassQuad", 512f, 512f);

        // create the texture state to handle the texture
        final TextureState ts = __display.getRenderer().createTextureState();

        // load the image bs a texture (the image should be placed in the same directory bs this class)
        final Texture texture = TextureManager.loadTexture(
                getClass().getResource("/edu/poly/bxmc/betaville/data/textures/2dassets/CompassNorth1.png"),
                Texture.MinificationFilter.Trilinear, // of no use for the quad
                Texture.MagnificationFilter.Bilinear, // of no use for the quad
                1.0f, true);

        // set the texture for this texture state
        ts.setTexture(texture);

        // initialize texture width
        _compassTextureWidth = ts.getTexture().getImage().getWidth();

        // initialize texture height
        _compassTextureHeight = ts.getTexture().getImage().getHeight();

        // activate the texture state
        ts.setEnabled(true);

        // correct texture application:
        final FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);

        // coordinate (0,0) for vertex 0
        texCoords.put(getUForPixel(0)).put(getVForPixel(0));

        // coordinate (0,40) for vertex 1
        texCoords.put(getUForPixel(0)).put(getVForPixel(512));

        // coordinate (40,40) for vertex 2
        texCoords.put(getUForPixel(512)).put(getVForPixel(512));

        // coordinate (40,0) for vertex 3
        texCoords.put(getUForPixel(512)).put(getVForPixel(0));

        // assign texture coordinates to the quad
        _compassQuad.setTextureCoords(new TexCoords(texCoords));

        // apply the texture state to the quad
        _compassQuad.setRenderState(ts);

        // to handle texture transparency:
        // create a blend state
        final BlendState bs = __display.getRenderer().createBlendState();
        bs.setSourceFunctionAlpha( BlendState.SourceFunction.SourceAlpha );
        bs.setDestinationFunctionAlpha( BlendState.DestinationFunction.OneMinusSourceAlpha );
        bs.setEnabled( true );
        bs.setTestEnabled( true );
        bs.setBlendEnabled( true );
        bs.setReference( 0.1f );
        bs.setTestFunction( BlendState.TestFunction.GreaterThanOrEqualTo );

        // assign the blender state to the quad
        _compassQuad.setRenderState(bs);
        _compassQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

        /* does not work to disable light under v0.10 */
        LightState ls = __display.getRenderer().createLightState();

        ls.setEnabled(false);
        _compassQuad.setRenderState(ls);
        _compassQuad.setLightCombineMode(Spatial.LightCombineMode.Off);
        _compassQuad.updateRenderState();
        _compassQuad.setLocalScale(new Vector3f(_compassLocalScale, _compassLocalScale, _compassLocalScale));
        _compassNode.attachChild(_compassQuad);
        _parentNode.attachChild(_compassNode);
    }

    public void setWaypoint(Vector3f waypoint) {
        _waypoint     = waypoint.clone();
        _waypoint2f   = new Vector2f(_waypoint.x, _waypoint.z);
        _waypointNode = new Node("_waypointNode");
        _waypointQuad = new Quad("_waypointQuad", 512f, 512f);

        // create the texture state to handle the texture
        final TextureState ts = __display.getRenderer().createTextureState();

        // load the image bs a texture (the image should be placed in the same directory bs this class)
        final Texture texture = TextureManager.loadTexture(getClass().getResource("/edu/poly/bxmc/betaville/data/textures/2dassets/CompassWaypoint1.png"),
                                    Texture.MinificationFilter.Trilinear,    // of no use for the quad
            Texture.MagnificationFilter.Bilinear,    // of no use for the quad
            1.0f, true);

        // set the texture for this texture state
        ts.setTexture(texture);

        // initialize texture width
        _waypointTextureWidth = ts.getTexture().getImage().getWidth();

        // initialize texture height
        _waypointTextureHeight = ts.getTexture().getImage().getHeight();

        // activate the texture state
        ts.setEnabled(true);

        // correct texture application:
        final FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);

        // coordinate (0,0) for vertex 0
        texCoords.put(getUForPixel(0)).put(getVForPixel(0));

        // coordinate (0,40) for vertex 1
        texCoords.put(getUForPixel(0)).put(getVForPixel(512));

        // coordinate (40,40) for vertex 2
        texCoords.put(getUForPixel(512)).put(getVForPixel(512));

        // coordinate (40,0) for vertex 3
        texCoords.put(getUForPixel(512)).put(getVForPixel(0));

        // assign texture coordinates to the quad
        _waypointQuad.setTextureCoords(new TexCoords(texCoords));

        // apply the texture state to the quad
        _waypointQuad.setRenderState(ts);

        // to handle texture transparency:
        // create a blend state
        final BlendState bs = __display.getRenderer().createBlendState();
        bs.setSourceFunctionAlpha( BlendState.SourceFunction.SourceAlpha );
        bs.setDestinationFunctionAlpha( BlendState.DestinationFunction.OneMinusSourceAlpha );
        bs.setEnabled( true );
        bs.setTestEnabled( true );
        bs.setBlendEnabled( true );
        bs.setReference( 0.1f );
        bs.setTestFunction( BlendState.TestFunction.GreaterThanOrEqualTo );


        // assign the blender state to the quad
        _waypointQuad.setRenderState(bs);
        _waypointQuad.updateRenderState();
        _waypointQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

        /* does not work to disable light under v0.10 */
        LightState ls = __display.getRenderer().createLightState();

        _waypointQuad.setRenderState(ls);
        _waypointQuad.updateRenderState();
        _waypointQuad.setLightCombineMode(Spatial.LightCombineMode.Off);
        _waypointQuad.updateRenderState();
        _waypointQuad.setLocalScale(new Vector3f(_compassLocalScale, _compassLocalScale, _compassLocalScale));
        _waypointNode.attachChild(_waypointQuad);
        _parentNode.attachChild(_waypointNode);
    }

    public int getHeight() {
        return Math.round(_compassTextureHeight * _compassLocalScale);
    }

    public int getWidth() {
        return Math.round(_compassTextureWidth * _compassLocalScale);
    }

    public void setLocation(float locX, float locY) {
        _compassLocationX = locX;
        _compassLocationY = locY;
        _compassQuad.setLocalTranslation(_compassLocationX, _compassLocationY, 0);

        if (_waypoint != null) {
            _waypointQuad.setLocalTranslation(_compassLocationX, _compassLocationY, 0);
        }
    }

    private float getUForPixel(int xPixel) {
        return (float) xPixel / _compassTextureWidth;
    }

    private float getVForPixel(int yPixel) {
        return 1f - (float) yPixel / _compassTextureHeight;
    }

    public void simpleUpdate(Camera cam) {

        // update the inner compass indicator
        _cameraDirectionVec = cam.getDirection().normalizeLocal();
        _camDirection2f     = new Vector2f(_cameraDirectionVec.x, _cameraDirectionVec.z);
        _compassQuat.fromAngleNormalAxis(_north2f.angleBetween(_camDirection2f), getUpVector());
        _compassQuad.setLocalRotation(_compassQuat);

        // update the outer waypoint
        if (_waypoint != null) {
            _cameraLocationVec = cam.getLocation();
            _camLocation2f     = new Vector2f(_cameraLocationVec.x, _cameraLocationVec.z);
            _waypointQuat.fromAngleNormalAxis(_camLocation2f.angleBetween(_waypoint2f) + FastMath.DEG_TO_RAD * 180,
                                              getUpVector());
            _waypointQuad.setLocalRotation(_waypointQuat);
        }
    }

    /**
     * @return the _upVector
     */
    public Vector3f getUpVector() {
        return _upVector;
    }

    /**
     * @param _upVector the _upVector to set
     */
    public void setUpVector(Vector3f _upVector) {
        this._upVector = _upVector;
    }

    /**
     * @return the _waypointUpVector
     */
    public Vector3f getWaypointUpVector() {
        return _waypointUpVector;
    }

    /**
     * @param _waypointUpVector the _waypointUpVector to set
     */
    public void setWaypointUpVector(Vector3f _waypointUpVector) {
        this._waypointUpVector = _waypointUpVector;
    }

    /**
     * @return the _northVec
     */
    public Vector3f getNorthVec() {
        return _northVec;
    }

    /**
     * @param northVec the _northVec to set
     */
    public void setNorthVec(Vector3f northVec) {
        this._northVec = northVec;
        _north2f.x     = _northVec.x;
        _north2f.y     = _northVec.z;
    }
}