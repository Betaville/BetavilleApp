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

import org.fenggui.Display;
import org.fenggui.event.key.Key;
import org.fenggui.event.mouse.MouseButton;
import org.fenggui.event.mouse.MouseEvent;
import org.lwjgl.input.Keyboard;
 
import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.system.DisplaySystem;
 
/**
 * FengJMEInputHandler - Translates jME input into FengGUI input.
 * 
 * @author Joshua Keplinger
 * @author Skye Book - updated to support FengGUI 12a
 *
 */
public class FengJMEInputHandler extends InputHandler
{
 
	private Display disp;
	private KeyInputAction keyAction;
 
	private boolean applicationHadFocusOnLastUpdate=true;
	private boolean keyHandled;
	private boolean mouseHandled;
	
	private MouseEvent mouseEvent;
 
	public FengJMEInputHandler(Display disp)
	{
		this.disp = disp;
 
		keyAction = new KeyAction();
		addAction(keyAction, DEVICE_KEYBOARD, BUTTON_ALL, AXIS_NONE, false);
 
		MouseInput.get().addListener(new MouseListener());
	}
 
	public void update(float time)
	{
		//if(DisplaySystem.getDisplaySystem().isActive()!=applicationHadFocusOnLastUpdate) System.out.println("FOCUS CHANGED");
		keyHandled = false;
		mouseHandled = false;
		super.update(time);
		applicationHadFocusOnLastUpdate = DisplaySystem.getDisplaySystem().isActive();
	}
	
	public MouseEvent getMouseEvent(){
		return mouseEvent;
	}
 
	public boolean wasKeyHandled()
	{
		return keyHandled;
	}
 
	public boolean wasMouseHandled()
	{
		return mouseHandled;
	}
 
	private class KeyAction extends KeyInputAction
	{
 
		public void performAction(InputActionEvent evt)
		{
			char character = evt.getTriggerCharacter();
			Key key = mapKeyEvent();
			if(evt.getTriggerPressed()) {
				disp.fireKeyPressedEvent(character, key);
				// Bug workaround see note after code
				if (key == Key.LETTER || key == Key.DIGIT){
					// is this still necessary?
					//disp.fireKeyTypedEvent(character);
				}
			} else
				disp.fireKeyReleasedEvent(character, key);
		}
 
		/**
		 * Helper method that maps LWJGL key events to FengGUI.
		 * @return The Key enumeration of the last key pressed.
		 */
		private Key mapKeyEvent()
		{
			Key keyClass;
 
	        switch(Keyboard.getEventKey())
	        {
		        case Keyboard.KEY_BACK:
		        	keyClass = Key.BACKSPACE;
		            break;
		        case Keyboard.KEY_RETURN:
		        	keyClass = Key.ENTER;
		            break;
		        case Keyboard.KEY_DELETE:
		        	keyClass = Key.DELETE;
		            break;
		        case Keyboard.KEY_TAB:
		        	keyClass = Key.TAB;
		        	break;
		        case Keyboard.KEY_UP:
		        	keyClass = Key.UP;
		        	break;
		        case Keyboard.KEY_RIGHT:
		        	keyClass = Key.RIGHT;
		            break;
		        case Keyboard.KEY_LEFT:
		        	keyClass = Key.LEFT;
		            break;
		        case Keyboard.KEY_DOWN:
		        	keyClass = Key.DOWN;
		            break;
		        case Keyboard.KEY_SCROLL:
		        	keyClass = Key.SHIFT;
		            break;
		        case Keyboard.KEY_LMENU:
		        	keyClass = Key.ALT;
		            break;
		        case Keyboard.KEY_RMENU:
		        	keyClass = Key.ALT;
		            break;
		        case Keyboard.KEY_LCONTROL:
		        	keyClass = Key.CTRL;
		            break;
		        case Keyboard.KEY_RSHIFT:
		        	keyClass = Key.SHIFT;
		            break;
		        case Keyboard.KEY_LSHIFT:
		        	keyClass = Key.SHIFT;
		            break;
		        case Keyboard.KEY_RCONTROL:
		        	keyClass = Key.CTRL;
		            break;
		        case Keyboard.KEY_INSERT:
		        	keyClass = Key.INSERT;
		            break;
		        case Keyboard.KEY_F12:
		        	keyClass = Key.F12;
		            break;
		        case Keyboard.KEY_F11:
		        	keyClass = Key.F11;
		            break;
		        case Keyboard.KEY_F10:
		        	keyClass = Key.F10;
		            break;
		        case Keyboard.KEY_F9:
		        	keyClass = Key.F9;
		            break;
		        case Keyboard.KEY_F8:
		        	keyClass = Key.F8;
		            break;
		        case Keyboard.KEY_F7:
		        	keyClass = Key.F7;
		            break;
		        case Keyboard.KEY_F6:
		        	keyClass = Key.F6;
		            break;
		        case Keyboard.KEY_F5:
		        	keyClass = Key.F5;
		            break;
		        case Keyboard.KEY_F4:
		        	keyClass = Key.F4;
		            break;
		        case Keyboard.KEY_F3:
		        	keyClass = Key.F3;
		            break;
		        case Keyboard.KEY_F2:
		        	keyClass = Key.F2;
		            break;
		        case Keyboard.KEY_F1:
		        	keyClass = Key.F1;
		            break;
		        default:
		        	if("1234567890".indexOf(Keyboard.getEventCharacter()) != -1) {
		        		keyClass = Key.DIGIT;
		        	} else {
		        		// @todo must not necessarily be a letter!! #
		        		keyClass = Key.LETTER;
		        	}
		        	break;
	    	}
 
	        return keyClass;
		}
 
	}
 
	private class MouseListener implements MouseInputListener
	{
 
		private boolean down;
		private int lastButton;
 
		private int lastX = -100;
		private int lastY = -100;
 
		public void onButton(int button, boolean pressed, int x, int y)
		{
			if(!applicationHadFocusOnLastUpdate){
				//System.out.println("Mouse event received on focus change");
				//return;
			}
			
			down = pressed;
			lastButton = button;
			if(pressed)
			{
				lastX = x;
				lastY = y;
				mouseEvent = disp.fireMousePressedEvent(x, y, getMouseButton(button));
			}
			else
			{
				mouseEvent = disp.fireMouseReleasedEvent(x, y, getMouseButton(button));
 
				if (x == lastX && y == lastY && getMouseButton(button) == MouseButton.LEFT)
				{
					mouseEvent = disp.fireMouseDoubleClickEvent(x, y, getMouseButton(button), disp.getWidget(x, y));
 
					if (mouseHandled == false)
					{
						//mouseHandled = rtnVal;
					}
				}
 
				lastX = -100;
				lastY = -100;
			}
		}
 
		public void onMove(int xDelta, int yDelta, int newX, int newY)
		{
			// If the button is down, the mouse is being dragged
			if(down)
				mouseEvent = disp.fireMouseDraggedEvent(newX, newY, getMouseButton(lastButton));
			else
				mouseEvent = disp.fireMouseMovedEvent(newX, newY);
		}
 
		public void onWheel(int wheelDelta, int x, int y)
		{
			// wheelDelta is positive if the mouse wheel rolls up, negative otherwise
			// we need to flip the delta, because FengGUI expects a positive wheelDelta
			// certain widgets will only scroll in one direction otherwise.
			if(wheelDelta > 0)
				mouseEvent = disp.fireMouseWheel(x, y, true, wheelDelta, 1);
			else
				mouseEvent = disp.fireMouseWheel(x, y, false, -wheelDelta, 1);
		}
 
		/**
		 * Helper method that maps the mouse button to the equivalent
		 * FengGUI MouseButton enumeration.
		 * @param button The button pressed or released.
		 * @return The FengGUI MouseButton enumeration matching the
		 * button.
		 */
		private MouseButton getMouseButton(int button)
		{
			switch(button)
			{
				case 0:
					return MouseButton.LEFT;
				case 1:
					return MouseButton.RIGHT;
				case 2:
					return MouseButton.MIDDLE;
				default:
					return MouseButton.LEFT;
			}
		}
 
	}
 
}