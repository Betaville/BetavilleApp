/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * @author Skye Book
 *
 */
public class WhitewashAction extends PanelAction {

	/**
	 * @param name
	 * @param description
	 * @param listener
	 */
	public WhitewashAction(String name, String description,
			IButtonPressedListener listener) {
		super(name, description, listener);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public WhitewashAction() {
		super("Whitewash", "Whitewashes the selected building", "Whitewash", AvailabilityRule.OBJECT_SELECTED, UserType.MEMBER,
				null);
		
		
		final MaterialState white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		final ColorRGBA whiteColor = ColorRGBA.white;
		white.setAmbient(whiteColor);
		white.setDiffuse(whiteColor);
		white.setEmissive(whiteColor);
		white.setSpecular(whiteColor);
		white.setMaterialFace(MaterialFace.FrontAndBack);
		
		
		getButton().addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				if(GeometryUtilities.checkForRenderState(SceneScape.getTargetSpatial(), white)){
					GeometryUtilities.removeRenderState(SceneScape.getTargetSpatial(), white.getStateType());
				}else{
					GeometryUtilities.applyColor(SceneScape.getTargetSpatial(), whiteColor);
				}
				
			}
		});
		
	}

}
