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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import org.apache.log4j.Logger;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.layout.RowExLayout;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Design;

/**
 * Utility window for viewing a bit more information about objects
 * @author Skye Book
 *
 */
public class DetailedInfoAction extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(DetailedInfoAction.class);

	
	private Label id;
	private Label file;
	private Label uploadedBy;
	
	private String idPfx = "ID: ";
	private String filePfx = "File: ";
	private String uploadedByPfx = "Uploaded By: ";
	
	/**
	 * 
	 */
	public DetailedInfoAction() {
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		id = FengGUI.createWidget(Label.class);
		file = FengGUI.createWidget(Label.class);
		uploadedBy = FengGUI.createWidget(Label.class);
		
		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			
			public void selectionCleared(Design previousDesign) {
				getContentContainer().removeAllWidgets();
			}
			
			public void designSelected(Spatial spatial, Design design) {
				id.setText(idPfx+design.getID());
				file.setText(filePfx+design.getFilepath());
				uploadedBy.setText(uploadedByPfx+design.getUser());
				if(!id.isInWidgetTree())getContentContainer().addWidget(id, file, uploadedBy);
				layout();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("More Info");
		setHeight(getHeight()+10);
	}
}
