/*
 * FengGUI - Java GUIs in OpenGL (http://www.fenggui.org)
 * 
 * Copyright (c) 2005-2009 FengGUI Project
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details:
 * http://www.gnu.org/copyleft/lesser.html#TOC3
 * 
 * Created on Jul 15, 2005
 * $Id: TextAreaExample.java 657 2009-10-12 21:17:17Z marcmenghin $
 */
package bvtest.feng;

import org.fenggui.Button;
import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.experimental.PanelContainer;
import edu.poly.bxmc.betaville.jme.fenggui.experimental.TextScroller;

/**
 * Displays a text area with a rather stupid text.
 * @author Johannes Schaback ($Author: marcmenghin $)
 */
public class CemreExample implements IExample
{
	private Display display;


	private void stuffHere(){
		PanelContainer myContainer = FengGUI.createWidget(PanelContainer.class);
		myContainer.setLayoutManager(new RowExLayout(false));
		FixedButton b = FengGUI.createWidget(FixedButton.class);
		b.setText("hallo!");
		b.setWidth(b.getWidth()+10);
		myContainer.addWidget(b);
		display.addWidget(myContainer);
	}
	
	public void buildGUI(Display g)
	{
		display = g;
		stuffHere();
	}

	public String getExampleName()
	{
		return "Text Area Example";
	}

	public String getExampleDescription()
	{
		return "Text Area Example";
	}

}
