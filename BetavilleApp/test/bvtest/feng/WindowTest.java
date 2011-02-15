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

import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FG;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.composite.tab.TabContainer;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.StaticLayout;
import org.fenggui.tooltip.LabelTooltipDecorator;
import org.fenggui.util.Alignment;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BetavilleTabContainerBuilder;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BetavilleTextEditor;
import edu.poly.bxmc.betaville.jme.fenggui.tab.TabContainerToggler;

/**
 * Displays a text area with a rather stupid text.
 * @author Johannes Schaback ($Author: marcmenghin $)
 */
public class WindowTest implements IExample
{
  private Display display;

  private void stuffHere() {
    display.setLayoutManager(new StaticLayout());
    
    Window w = FengGUI.createWindow(true, true);
    w.setSize(400, 400);
//    display.addWidget(w);

    BetavilleTabContainerBuilder builder = BetavilleTabContainerBuilder.create(Alignment.BOTTOM);

    FixedButton b1 = FengGUI.createWidget(FixedButton.class);
    b1.setText("City Panel City Panel");
    b1.setEnabled(false);
    b1.getAppearance().addDecorator("Tooltip", new LabelTooltipDecorator("Hover#Hovered", true, "This is a label"));

    final FixedButton b2 = FengGUI.createWidget(FixedButton.class);
    b2.setText("I dont work");
    b2.setEnabled(false);

    Label l1 = FG.createLabel("Freak'n awesome tab!");
    l1.getAppearance().add(new PlainBackground(Color.GREEN));
    Label l2 = FG.createLabel("Yet another label...");
    l2.getAppearance().add(new PlainBackground(Color.BLUE));

    BetavilleTextEditor speedMin = FengGUI.createWidget(BetavilleTextEditor.class);
    speedMin.setDefaultText("<you text here>");

    BetavilleTextEditor speedMax = FengGUI.createWidget(BetavilleTextEditor.class);
    speedMax.setDefaultText("<you text here>");

    builder.addTab("Tab I", true).addWidget(b1, b2);
    builder.addTab("Tab II").addWidget(l1);
    builder.addTab("Tab III").addWidget(speedMin, speedMax);
    
    TabContainer tabContainer = builder.buildTabContainer(true, true);
    tabContainer.setActiveTab(0);

    Container wrapper = FengGUI.createWidget(Container.class);
    wrapper.getAppearance().add(new PlainBackground(Color.BLUE));
    wrapper.setShrinkable(false);
    wrapper.setLayoutManager(new BorderLayout());
    tabContainer.setLayoutData(BorderLayoutData.NORTH);
    wrapper.addWidget(tabContainer);

    TabContainerToggler toggler = builder.getToggler(true);

    org.fenggui.Button btn = FengGUI.createWidget(org.fenggui.Button.class);
    btn.addButtonPressedListener(new IButtonPressedListener() {

      public void buttonPressed(Object source, ButtonPressedEvent e) {
        //tabContainer.setSize(150, 100);
        System.out.println("WTF?");
        b2.setVisible(!b2.isVisible());
      }

    });
    btn.setText("Test");
    btn.setPosition(new Point(0, 150));
    w.getContentContainer().addWidget(btn);
    
    tabContainer.setSizeToMinSize();
    
    display.addWidget(wrapper);
    
    toggler.hide(false); 
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
