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
 * Created on Oct 14, 2007
 * $Id: ConsoleExample.java 362 2007-10-08 09:46:42Z marcmenghin $
 */
package bvtest.feng;

import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.ObservableLabelWidget;
import org.fenggui.appearance.LabelAppearance;
import org.fenggui.composite.menu.Menu;
import org.fenggui.composite.menu.MenuItem;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseButton;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.text.content.factory.simple.TextStyle;
import org.fenggui.text.content.factory.simple.TextStyleEntry;
import org.fenggui.util.Alignment;
import org.fenggui.util.Color;

/**
 * Example class showing how to build and use a popup menu. 
 * 
 * @author Johannes Schaback, last edited by $Author$, $Date$
 * @version $Revision$
 */
public class PopupMenuExample implements IExample
{
  private Menu popupMenu = null;

  public void buildGUI(final Display display)
  {
    /* we build a puristic widget showing a text.
       Note that you could use every other widget type as well. */
    ObservableLabelWidget l = new ObservableLabelWidget();
    l.getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
    l.getAppearance().getStyle(TextStyle.DEFAULTSTYLEKEY).getTextStyleEntry(TextStyleEntry.DEFAULTSTYLESTATEKEY)
        .setColor(Color.WHITE);
    l.getAppearance().setAlignment(Alignment.MIDDLE);
    l.setXY(150, 50);
    l.setText("Press Right Mouse Button Here!");
    l.setSize(l.getMinWidth() + 10, l.getMinHeight() + 20);
    l.setShrinkable(false);
    display.addWidget(l);

    Menu secondMenu = FengGUI.createWidget(Menu.class);
    secondMenu.addItem(buildMenuItem(display, "Tomatos", secondMenu.getAppearance()));
    secondMenu.addItem(buildMenuItem(display, "Printer", secondMenu.getAppearance()));
    secondMenu.addItem(buildMenuItem(display, "Monitor", secondMenu.getAppearance()));

    popupMenu = FengGUI.createWidget(Menu.class);
    popupMenu.addItem(buildMenuItem(display, "First Entry", secondMenu.getAppearance()));
    popupMenu.addItem(buildMenuItem(display, "Second Entry", secondMenu.getAppearance()));
    popupMenu.addItem(buildMenuItem(display, "Third Entry", secondMenu.getAppearance()));
    popupMenu.registerSubMenu(secondMenu, "A Submenu");
    popupMenu.addItem(buildMenuItem(display, "Fourth Entry", secondMenu.getAppearance()));
    popupMenu.setSizeToMinSize();
    popupMenu.layout();

    l.addMouseListener(new MouseAdapter()
    {

      @Override
      public void mousePressed(Object sender, MousePressedEvent mousePressedEvent)
      {
        int x = mousePressedEvent.getDisplayX();
        int y = mousePressedEvent.getDisplayY();
        popupMenu.setXY(x, y);

        if (mousePressedEvent.getButton() == MouseButton.RIGHT)
        {
          if (popupMenu.equals(display.getPopupWidget())) // popupmenu is already visible!
          {
            display.removePopup();
          }

          display.displayPopUp(popupMenu);
        }
      }
    });
  }

  private MenuItem buildMenuItem(final Display d, String s, LabelAppearance appearance)
  {
    MenuItem m = new MenuItem(s, appearance);

    // we have to close the popup manually if a button has been pressed
    m.addMenuItemPressedListener(new IMenuItemPressedListener()
    {
      public void menuItemPressed(MenuItemPressedEvent menuItemPressedEvent)
      {
        System.out.println("Selected Item: " + menuItemPressedEvent.getItem().getText());
        d.removePopup();
      }
    });
    return m;
  }

  public String getExampleDescription()
  {
    return "Popup Menu Example";
  }

  public String getExampleName()
  {
    return "Popup Menu Example";
  }

}
