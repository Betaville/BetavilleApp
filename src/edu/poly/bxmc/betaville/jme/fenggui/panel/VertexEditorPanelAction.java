/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.ModuleNameException;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.module.VertexEditModule;

/**
 * @author Skye Book
 *
 */
public class VertexEditorPanelAction extends PanelAction {
	
	private VertexEditModule module;
	
	private boolean isInVertMode = false;

	/**
	 * @param name
	 * @param description
	 * @param listener
	 */
	public VertexEditorPanelAction() {
		super("Enter Vert Mode", "Edits Vertices", null);
		
		button.addButtonPressedListener(new IButtonPressedListener() {
			
			@Override
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				isInVertMode = !isInVertMode;
				
				if(isInVertMode){
					// if there is no target spatial selected, turn off vert mode and return
					if(SceneScape.isTargetSpatialEmpty()){
						isInVertMode = false;
						return;
					}
					
					module = new VertexEditModule();
					try {
						SceneGameState.getInstance().addModuleToUpdateList(module);
						button.setText("Leave Vert Mode");
					} catch (ModuleNameException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else{
					SceneGameState.getInstance().removeModuleFromUpdateList(module);
					button.setText("Enter Vert Mode");
				}
			}
		});
		
	}

	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public VertexEditorPanelAction(String name, String description,
			String buttonTitle, AvailabilityRule ruleToSet,
			UserType minimumUserLevel, IButtonPressedListener listener) {
		super(name, description, buttonTitle, ruleToSet, minimumUserLevel,
				listener);
		// TODO Auto-generated constructor stub
	}

}
