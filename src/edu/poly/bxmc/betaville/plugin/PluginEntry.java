/**
 * 
 */
package edu.poly.bxmc.betaville.plugin;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;

/**
 * @author Skye Book
 *
 */
public class PluginEntry extends Container{
	private String pluginClass = "Plugin Name";
	
	private TextEditor name;
	private Button button;
	
	private RemoveCallback callback;
	
	public PluginEntry(){
		setLayoutManager(new RowExLayout(true));
		
		name = FengGUI.createWidget(TextEditor.class);
		name.setReadonly(true);
		
		button = FengGUI.createWidget(Button.class);
		button.setText("Remove");
		button.addButtonPressedListener(new IButtonPressedListener() {
			
			@Override
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(PluginManager.unloadPlugin(pluginClass)){
					executeCallback();
				}
			}
		});
		
		addWidget(name, button);
	}
	
	private void executeCallback(){
		callback.onPluginRemoval(this);
	}
	
	public void setPlugin(String pluginClass){
		this.pluginClass=pluginClass;
		name.setText(pluginClass);
		layout();
	}
	
	public void setRemoveCallback(RemoveCallback callback){
		this.callback=callback;
	}
	
	public interface RemoveCallback{
		/**
		 * Called when a plugin is removed using a {@link PluginEntry}
		 * @param pluginEntry
		 */
		public void onPluginRemoval(PluginEntry pluginEntry);
	}
}