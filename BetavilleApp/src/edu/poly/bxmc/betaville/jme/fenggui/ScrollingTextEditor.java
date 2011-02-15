/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Slider;
import org.fenggui.TextEditor;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;
import org.fenggui.layout.StaticLayout;

import com.jme.input.KeyInput;

import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.listeners.ISlideScrollSpreadChangeListener;

/**
 * @author Skye Book
 *
 */
public class ScrollingTextEditor extends Container{
	private static Logger logger = Logger.getLogger(ScrollingTextEditor.class);
	private int targetWidth;
	private int targetHeight;

	private SlideScrollContainer editorMount;
	private Slider slider;
	private TextEditor editor;

	/**
	 * 
	 */
	public ScrollingTextEditor(int width, int height) {
		super(new StaticLayout());
		targetWidth = width;
		targetHeight = height;
		setSize(targetWidth, targetHeight);
		createEditorMount();
		createEditor();
		createSlider();
		assignActions();
	}

	private void createEditorMount(){
		editorMount = new SlideScrollContainer();
		editorMount.setHorizontal(false);
		editorMount.setStartSize(targetWidth-10, targetHeight);
		editorMount.setSize( editorMount.getStartWidth(), editorMount.getStartHeight());
		editorMount.setXY(0, 0);
		addWidget(editorMount);
	}

	private void createEditor(){
		editor = FengGUI.createWidget(TextEditor.class);
		editor.setMultiline(true);
		editor.setWordWarping(true);
		editor.setSize(editorMount.getStartWidth(), editorMount.getStartHeight());
		editorMount.addToSlideScroller(editor);
	}

	private void createSlider(){
		slider = FengGUI.createSlider(false);
		int sizeDifference=10;
		slider.setHeight(targetHeight-sizeDifference);
		slider.setXY(editorMount.getWidth(), editorMount.getY()+sizeDifference/2);
		addWidget(slider);
		slider.setVisible(false);
	}

	private void assignActions(){
		editor.addTextChangedListener(new ITextChangedListener() {
			public void textChanged(TextChangedEvent textChangedEvent){
				int newHeight = editor.getTextRendererData().getSize().getHeight();
				if(newHeight>targetHeight) editor.setHeight(newHeight);
			}
		});
		
		editor.addKeyListener(new IKeyListener(){

			public void keyTyped(Object sender, KeyTypedEvent keyTypedEvent) {
				KeyInput keyInput = KeyInput.get();
				boolean isMac = System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;

				if(isMac){
					if(keyInput.isKeyDown(KeyInput.KEY_LMETA) || keyInput.isKeyDown(KeyInput.KEY_RMETA)){
						if(keyTypedEvent.getKey()=='v'){
							FengUtils.pasteAction(editor);
						}
						else if(keyTypedEvent.getKey()=='c'){
							FengUtils.copyAction(editor);
						}
					}
				}
				else{
					if(keyInput.isKeyDown(KeyInput.KEY_LCONTROL) || keyInput.isKeyDown(KeyInput.KEY_RCONTROL)){
						if(keyTypedEvent.getKey()=='v'){
							FengUtils.pasteAction(editor);
						}
						else if(keyTypedEvent.getKey()=='c'){
							FengUtils.copyAction(editor);
						}
					}
				}
			}

			public void keyReleased(Object sender, KeyReleasedEvent keyReleasedEvent) {
				/*
				KeyInput keyInput = KeyInput.get();
				boolean isMac = System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;

				if(isMac){
					if(keyInput.isKeyDown(KeyInput.KEY_LMETA) || keyInput.isKeyDown(KeyInput.KEY_RMETA)){
						if(keyReleasedEvent.getKey()=='v'){
							FengUtils.pasteAction(editor);
						}
						else if(keyReleasedEvent.getKey()=='c'){
							FengUtils.copyAction(editor);
						}
					}
				}
				else{
					if(keyInput.isKeyDown(KeyInput.KEY_LCONTROL) || keyInput.isKeyDown(KeyInput.KEY_RCONTROL)){
						if(keyReleasedEvent.getKey()=='v'){
							FengUtils.pasteAction(editor);
						}
						else if(keyReleasedEvent.getKey()=='c'){
							FengUtils.copyAction(editor);
						}
					}
				}
				*/
			}

			public void keyPressed(Object sender, KeyPressedEvent keyPressedEvent) {
				/*
				KeyInput keyInput = KeyInput.get();
				boolean isMac = System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;

				if(isMac){
					if(keyInput.isKeyDown(KeyInput.KEY_LMETA) || keyInput.isKeyDown(KeyInput.KEY_RMETA)){
						if(keyPressedEvent.getKey()=='v'){
							FengUtils.pasteAction(editor);
						}
						else if(keyPressedEvent.getKey()=='c'){
							FengUtils.copyAction(editor);
						}
					}
				}
				else{
					if(keyInput.isKeyDown(KeyInput.KEY_LCONTROL) || keyInput.isKeyDown(KeyInput.KEY_RCONTROL)){
						if(keyPressedEvent.getKey()=='v'){
							FengUtils.pasteAction(editor);
						}
						else if(keyPressedEvent.getKey()=='c'){
							FengUtils.copyAction(editor);
						}
					}
				}
				*/
			}
		});

		editorMount.addSpreadChangedListener(new ISlideScrollSpreadChangeListener() {
			public void spreadChanged(int spread){
				if(spread>0){
					if(!slider.isVisible()) slider.setVisible(true);
					logger.info("slider set visible");
				}
				else if(spread<1){
					if(slider.isVisible()) slider.setVisible(false);
					logger.info("slider set invisible");
				}
			}
		});

		slider.addSliderMovedListener(new ISliderMovedListener() {
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				editorMount.moveTo(slider.getValue());
			}
		});
	}



	/**
	 * Prepare this object for a fresh use
	 * @param defaultContent The default content to use in the
	 * TextEditor
	 */
	public void clearEditorContent(String defaultContent){
		if(defaultContent==null) defaultContent="";
		editor.setText(defaultContent);
		editorMount.clearHolster();
		editor.setSize(editorMount.getStartWidth(), editorMount.getStartHeight());
		editor.setXY(0, 0);
		editorMount.addToSlideScroller(editor);

		// this may or may not be necessary since it *should* be covered in the spread change listener
		if(slider.isVisible()) slider.setVisible(false);
	}
}
