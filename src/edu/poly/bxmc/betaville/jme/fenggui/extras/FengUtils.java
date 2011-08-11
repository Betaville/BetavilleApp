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

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.Widget;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;

import com.jme.input.MouseInput;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.listeners.ITweenFinishedListener;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;

/**
 * @author Skye Book
 *
 */
public class FengUtils {
	private static Logger logger = Logger.getLogger(FengUtils.class);
	
	public static String getText(TextEditor te){
		return te.getText().replaceAll("[\\r\\n]", "");
	}
	
	public static String getSelectedText(TextEditor te){
		return te.getTextRendererData().getSelectedContent().replaceAll("[\\r\\n]", "");
	}
	
	public static int getNumber(TextEditor te) throws FengTextContentException{
		if(te.getRestrict()==TextEditor.RESTRICT_NUMBERSONLY){
			String toUse = getText(te);
			
			// remove the decimal point and any trailing numbers
			if(toUse.contains(".")){
				toUse = toUse.substring(0, toUse.indexOf("."));
			}
			return Integer.parseInt(toUse);
		}
		else{
			throw new FengTextContentException("TextEditor must be restricted to numbers only", te.getText());
		}
	}
	
	public static float getFloat(TextEditor te) throws FengTextContentException{
		if(te.getRestrict()==TextEditor.RESTRICT_NUMBERSONLYDECIMAL){
			return Float.parseFloat(getText(te));
		}
		else{
			throw new FengTextContentException("TextEditor must be restricted to numbers & decimals only", te.getText());
		}
	}
	
	/**
	 * Moves a widget to a location over a length of time
	 * @param widget The widget to move
	 * @param newX The final X location of the widget
	 * @param newY The final Y location of the widget
	 * @param time The length, in milliseconds, of the animation
	 * @param resolution The amount of times to refresh this animation
	 */
	public static void tweenWidget(final IWidget widget, final int newX, final int newY, final int time, final int resolution){
		tweenWidget(widget, newX, newY, time, resolution, new ITweenFinishedListener() {
			public void tweenComplete(){}
		});
	}
	
	/**
	 * Moves a widget to a location over a length of time
	 * @param widget The widget to move
	 * @param newX The final X location of the widget
	 * @param newY The final Y location of the widget
	 * @param time The length, in milliseconds, of the animation
	 * @param resolution The amount of times to refresh this animation
	 * @param finishedListener Defines an action to be called at the end of a tween.
	 */
	public static void tweenWidget(final IWidget widget, final int newX, final int newY, final int time, final int resolution, final ITweenFinishedListener finishedListener){
		SettingsPreferences.getGUIThreadPool().submit(new Runnable(){
			@SuppressWarnings("static-access")
			public void run() {
				long startTime = System.currentTimeMillis();
				int oldX = widget.getX();
				int oldY = widget.getY();
				int diffX = newX-oldX;
				int diffY = newY-oldY;
				int intervalX = diffX/resolution;
				int intervalY = diffY/resolution;
				int waitTime = time/resolution;
				for(int i=0; i<resolution; i++){
					long curr = System.currentTimeMillis();
					if(curr<startTime+(waitTime*resolution)){
						try {
							long currWaitTime = ((startTime)+waitTime*i)-curr;
							if(currWaitTime<0){
								logger.error("currWaitTime " + currWaitTime + " at step " + (i+1));
							}
							Thread thisThread = Thread.currentThread();
							thisThread.sleep(currWaitTime);
						} catch (InterruptedException e) {
							logger.error("Tweener Thread Error, Jumping to Final Position", e);
							widget.setXY(newX, newY);
							finishedListener.tweenComplete();
							return;
						}
					}
					else{
						// too late for this update, do the move immediately
						logger.warn("Tweener came too late on this pass");
					}
					
					// perform the actual move
					if(i<resolution-1){
						widget.setXY(widget.getX()+intervalX, widget.getY()+intervalY);
					}
					else{
						widget.setXY(newX, newY);
						finishedListener.tweenComplete();
					}
				}
			}});
		
	}
	
	public static void pasteAction(TextEditor te){
		try {
			// remove the inserted v and paste
			te.setText(te.getText().substring(0, te.getText().length()-1)+Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int midWidth(Widget place, Widget thing){
		return (place.getWidth()/2)-(thing.getWidth()/2);
	}
	
	public static int midHeight(Widget place, Widget thing){
		return (place.getHeight()/2)-(thing.getHeight()/2);
	}
	
	/**
	 * Sets a widget at the mouse's location while still being fully
	 * inside of the window, preventing spilling over.
	 * @param w The widget for which to set the location
	 */
	public static void setAtSafeMousePosition(IWidget w){
		w.setXY(MouseInput.get().getXAbsolute()-(w.getSize().getWidth()/2),
				MouseInput.get().getYAbsolute()-(w.getSize().getHeight()/2));
		
		int xDiff = w.getX()+w.getSize().getWidth()-Binding.getInstance().getCanvasWidth();
		if(xDiff>0){
			w.setX(w.getX()-xDiff);
		}
		else if(w.getX()<0){
			w.setX(0);
		}
		
		int yDiff = w.getY()+w.getSize().getHeight()-Binding.getInstance().getCanvasHeight();
		if(yDiff>0){
			w.setY(w.getY()-yDiff);
		}
		else if(w.getY()<0){
			w.setY(0);
		}
	}
	
	public static void showNewDismissableWindow(String name, String message, String buttonText, boolean forceFocus){
		Window w = createDismissableWindow(name, message, buttonText, forceFocus);
		GUIGameState.getInstance().getDisp().addWidget(w);
		GUIGameState.getInstance().forceFocus(w, true);
	}
	
	/**
	 * Closes on click
	 * @param name
	 * @param message
	 * @param buttonText
	 * @return
	 */
	public static Window createDismissableWindow(String name, String message, String buttonText, boolean forceFocus){
		final Window w = FengGUI.createWindow(true, true);
		w.getContentContainer().setLayoutManager(new RowExLayout(false)); 
		w.setTitle(name);

		if(forceFocus){
			w.addWindowClosedListener(new IWindowClosedListener() {
				public void windowClosed(WindowClosedEvent windowClosedEvent) {
					GUIGameState.getInstance().forceFocus(w, false);
				}
			});
		}

		int width=100;
		
		Label msg = FengGUI.createWidget(Label.class);
		msg.setMultiline(true);
		msg.setText(message);
		msg.setLayoutData(new RowExLayoutData(true, true));
		
		FixedButton b1 = FengGUI.createWidget(FixedButton.class);
		b1.setText(buttonText);
		b1.setWidth(b1.getWidth()+10);
		b1.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				w.close();
			}
		});
		/*
		if(msg.getWidth()>width){
			String text = msg.getText();
			String newText = "";
			
			// find the number of lines and split the label
			int numLines=(msg.getWidth()/width)+1;
			for(int i=numLines; i>0; i--){
				newText += text.substring(0, text.length()/i)+"\n";
				text = text.substring(text.length()/i, text.length());
			}
			
			int oneLineHeight = msg.getHeight();
			msg.setMultiline(true);
			msg.setWordWarping(true);
			msg.setText(newText);
			msg.setHeight(oneLineHeight*numLines);
			logger.debug("msg height set to " + msg.getHeight());
		}*/
		
		//w.getContentContainer().setSize((int)(width*.75f), b1.getHeight()+20+msg.getHeight());
		//w.setSize(width, b1.getHeight()+20+msg.getHeight()+w.getTitleBar().getHeight());
		
		//b1.setXY(5, 5);
		
		//msg.setXY(5, b1.getY()+b1.getHeight()+10);
		
		//w.getContentContainer().setLayoutManager(new StaticLayout());
		
		Container c = FengGUI.createWidget(Container.class);
		c.setLayoutManager(new StaticLayout());
		c.setLayoutData(new RowExLayoutData(true, true));
		c.addWidget(b1);
		StaticLayout.center(b1, c);
		
		w.getContentContainer().addWidget(msg, c);
		w.setXY((Binding.getInstance().getCanvasWidth()/2)-(w.getWidth()/2), (Binding.getInstance().getCanvasHeight()/2)-(w.getHeight()/2));
		return w;
	}
	
	public static boolean checkIfWithin(int x, int y, IWidget widget){
		if (x >= widget.getX() && x <= widget.getSize().getWidth() + widget.getX() && y >= widget.getY() && y <= widget.getSize().getHeight() + widget.getY()){
			return true;
		} else{
			return false;
		}
	}
	
	
	
	/**
	 * 
	 * @param name
	 * @param message
	 * @param b1Name
	 * @param b2Name
	 * @param b1Listener
	 * @param b2Listener
	 * @param closeOnB1Click
	 * @param closeOnB2Click
	 * @return
	 */
	public static Window createTwoOptionWindow(String name, String message, String b1Name, String b2Name,
			IButtonPressedListener b1Listener, IButtonPressedListener b2Listener, boolean closeOnB1Click, boolean closeOnB2Click){
		final Window w = FengGUI.createWindow(true, true);
		w.getContentContainer().setLayoutManager(new RowExLayout(false));
		w.setTitle(name);
		//w.getTitleBar().removeWidget(w.getCloseButton());
		w.getContentContainer().setLayoutManager(new StaticLayout());
		
		Label msg = FengGUI.createWidget(Label.class);
		msg.setText(message);
		msg.setLayoutData(new RowExLayoutData(true, true));
		
		IButtonPressedListener clickCloseListener = new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				w.close();
			}
		};
		
		FixedButton b1 = FengGUI.createWidget(FixedButton.class);
		b1.setText(b1Name);
		b1.setWidth(b1.getWidth()+10);
		if(b1Listener!=null) b1.addButtonPressedListener(b1Listener);
		if(closeOnB1Click) b1.addButtonPressedListener(clickCloseListener);
		
		FixedButton b2 = FengGUI.createWidget(FixedButton.class);
		b2.setText(b2Name);
		b2.setWidth(b2.getWidth()+10);
		if(b2Listener!=null) b2.addButtonPressedListener(b2Listener);
		if(closeOnB2Click) b2.addButtonPressedListener(clickCloseListener);
		
		// set the window sizes equal
		if(b1.getWidth()>b2.getWidth()){
			b2.setWidth(b1.getWidth());
		}
		else b1.setWidth(b2.getWidth());
		
		int buttonSetWidth = b1.getWidth()+20+b2.getWidth();
		/*
		if(msg.getWidth()>buttonSetWidth){
			String text = msg.getText();
			String newText = "";
			
			// find the number of lines and split the label
			int numLines=(msg.getWidth()/buttonSetWidth)+1;
			for(int i=numLines; i>0; i--){
				newText += text.substring(0, text.length()/i)+"\n";
				text = text.substring(text.length()/i, text.length());
			}
			
			int oneLineHeight = msg.getHeight();
			msg.setMultiline(true);
			msg.setWordWarping(true);
			msg.setText(newText);
			msg.setHeight(oneLineHeight*numLines);
			logger.info("msg height set to " + msg.getHeight());
		}*/
		
		w.getContentContainer().setSize(buttonSetWidth, b1.getHeight()+20+msg.getHeight());
		w.setSize(buttonSetWidth, b1.getHeight()+20+msg.getHeight()+w.getTitleBar().getHeight());
		
		b1.setXY(5, 5);
		b2.setXY(w.getContentContainer().getWidth()-b1.getWidth()-5, 5);
		
		msg.setXY(5, b1.getY()+b1.getHeight()+10);
		
		w.getContentContainer().addWidget(msg, b1, b2);
		w.setXY((Binding.getInstance().getCanvasWidth()/2)-(w.getWidth()/2), (Binding.getInstance().getCanvasHeight()/2)-(w.getHeight()/2));
		return w;
	}
	
	/**
	 * Set the appearance of text style in White color
	 * @param lbl Label on which apply the new text style
	 */
	public static void setAppearanceTextStyleWhiteColor(Label lbl) {
		lbl.getAppearance().getStyle("default").getTextStyleEntry("default").setColor(Color.WHITE);		
	}
	
	/**
	 * Set the appearance of of a label to a different color
	 * @param lbl Label on which apply the new text style
	 * @param color The color to apple
	 */
	public static void setAppearanceTextStyleColor(Label lbl, Color color) {
		lbl.getAppearance().getStyle("default").getTextStyleEntry("default").setColor(color);		
	}
}
