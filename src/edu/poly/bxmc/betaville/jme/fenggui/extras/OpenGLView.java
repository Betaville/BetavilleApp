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
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.math.Vector3f;

/**
 * Container to view an OpenGL unit coordinate
 * @author Skye Book
 *
 */
public class OpenGLView extends Container implements LocationView {
	
	private Label title;
	
	private Container xc;
	private Container yc;
	private Container zc;
	
	private Label xl;
	private Label yl;
	private Label zl;
	private Label xv;
	private Label yv;
	private Label zv;
	
	private boolean xEnabled=true;
	private boolean yEnabled=true;
	private boolean zEnabled=true;
	
	public OpenGLView(){
		setLayoutManager(new RowExLayout(false));
		
		title = FengGUI.createWidget(Label.class);
		title.setText("OpenGL View");
		
		setupX();
		setupY();
		setupZ();
		
		// add the components to the view (they should all be defaulted to enabled)
		reshuffle();
	}
	
	private void setupX(){
		xc = FengGUI.createWidget(Container.class);
		xc.setLayoutManager(new RowExLayout(true));
		
		xl = FengGUI.createWidget(Label.class);
		xl.setText("X");
		xl.setLayoutData(new RowExLayoutData(true, true));
		
		xv = FengGUI.createWidget(Label.class);
		xv.setLayoutData(new RowExLayoutData(true, true));
		
		xc.addWidget(xl, xv);
	}
	
	private void setupY(){
		yc = FengGUI.createWidget(Container.class);
		yc.setLayoutManager(new RowExLayout(true));
		
		yl = FengGUI.createWidget(Label.class);
		yl.setText("Y");
		yl.setLayoutData(new RowExLayoutData(true, true));
		
		yv = FengGUI.createWidget(Label.class);
		yv.setLayoutData(new RowExLayoutData(true, true));
		
		yc.addWidget(yl, yv);
	}
	
	private void setupZ(){
		zc = FengGUI.createWidget(Container.class);
		zc.setLayoutManager(new RowExLayout(true));
		
		zl = FengGUI.createWidget(Label.class);
		zl.setText("Z");
		zl.setLayoutData(new RowExLayoutData(true, true));
		
		zv = FengGUI.createWidget(Label.class);
		zv.setLayoutData(new RowExLayoutData(true, true));
		
		zc.addWidget(zl, zv);
	}
	
	public void setXEnabled(boolean enabled){
		xEnabled=enabled;
		reshuffle();
	}
	
	public void setYEnabled(boolean enabled){
		yEnabled=enabled;
		reshuffle();
	}
	
	public void setZEnabled(boolean enabled){
		zEnabled=enabled;
		reshuffle();
	}
	
	private void reshuffle(){
		removeAllWidgets();
		if(xEnabled) addWidget(xc);
		if(yEnabled) addWidget(yc);
		if(zEnabled) addWidget(zc);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.LocationView#setTitle(java.lang.String)
	 */
	public void setTitle(String name){
		title.setText(name);
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.LocationView#updateLocation(com.jme.math.Vector3f)
	 */
	public void updateLocation(Vector3f location){
		xv.setText(""+location.x);
		yv.setText(""+location.y);
		zv.setText(""+location.z);
	}

}
