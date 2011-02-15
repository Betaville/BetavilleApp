/* Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
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
 * 
 * $Id$
 */
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.RadioButton;
import org.fenggui.ToggableGroup;
import org.fenggui.composite.tab.TabContainer;
import org.fenggui.composite.tab.TabItem;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.ILayoutData;
import org.fenggui.util.Alignment;

import edu.poly.bxmc.betaville.jme.fenggui.tab.BottomTabContainer;
import edu.poly.bxmc.betaville.jme.fenggui.tab.BottomTabItem;
import edu.poly.bxmc.betaville.jme.fenggui.tab.TabContainerToggler;
import edu.poly.bxmc.betaville.jme.fenggui.tab.TabContent;

/**
 * BetavilleTabContainerBuilder simplifies creating {@link TabContainer TabContainers}.
 * 
 * @author Peter Schulz
 */
public class BetavilleTabContainerBuilder {

  final private ToggableGroup<RadioButton<TabItem>> tabGroup;
  final private TabContainer                        tabContainer;
  final private TabItem                             tabItemPrototype;
  final private Container                           tabContentPrototype;
  final private ILayoutData                         tabContentLayoutData;

  private TabContainerToggler                       tabContainerToggler;
  
  public static BetavilleTabContainerBuilder create(Alignment alignment) {
    if (alignment == Alignment.BOTTOM) {
      return new BetavilleTabContainerBuilder(
          BottomTabContainer.class,
          BottomTabItem.class, 
          TabContent.class, alignment);
    }
    return new BetavilleTabContainerBuilder(
        TabContainer.class,
        TabItem.class, 
        TabContent.class, alignment);
  }
  
  public BetavilleTabContainerBuilder(
      Class<? extends TabContainer> containerClass, 
      Class<? extends TabItem> itemClass,
      Class<? extends Container> contentClass,
      Alignment alignment) {
    
    tabGroup = new ToggableGroup<RadioButton<TabItem>>();
    tabContainer = FengGUI.createWidget(containerClass);
    tabItemPrototype = FengGUI.createWidget(itemClass);
    tabItemPrototype.setLayoutManager(new BorderLayout());
    tabContentPrototype = FengGUI.createWidget(contentClass);
    
    tabContainer.setHeaderAlignment(alignment, Alignment.LEFT);
    
    switch (alignment) {
    case TOP:
      tabContentLayoutData = BorderLayoutData.NORTH;
      break;
    case BOTTOM:
      tabContentLayoutData = BorderLayoutData.SOUTH;
      break;
    default:
      throw new IllegalArgumentException(alignment.toString() + " is not supported");
    }
  }
  
  public Container addTab(String title) {
    return addTab(title, false);
  }
  
  public Container addTab(String title, boolean active) {
    TabItem tabItem = tabItemPrototype.clone();
    tabItem.getHeadWidget().setRadioButtonGroup(tabGroup);
    tabItem.getHeadWidget().setText(title);

    Container tabContent = tabContentPrototype.clone();
    tabContent.setLayoutData(tabContentLayoutData);
    tabItem.addWidget(tabContent);
    
    tabContainer.addTab(tabItem);
    return tabContent;
  }
  
  public TabContainer buildTabContainer(boolean toggable, boolean hide) {
    return tabContainer;
  }
  
  public ToggableGroup<?> getTabGroup()  {
    return tabGroup;
  }
  
  public TabContainerToggler getToggler(boolean treatBorderAsHeader) {
    if (tabContainerToggler == null)
      tabContainerToggler = new TabContainerToggler(tabContainer, tabGroup, treatBorderAsHeader);
    return tabContainerToggler;
  }
  
}
