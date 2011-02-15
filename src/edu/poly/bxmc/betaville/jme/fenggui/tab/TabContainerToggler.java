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
package edu.poly.bxmc.betaville.jme.fenggui.tab;

import org.fenggui.ToggableGroup;
import org.fenggui.composite.tab.TabButton;
import org.fenggui.composite.tab.TabContainer;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.util.Alignment;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

/**
 * TabContainerToggler animates TabContainer.
 * @author Peter Schulz
 */
public class TabContainerToggler {
	
	private TabContainer tabContainer;
	private ToggableGroup<?> tabGroup;
	
	private Toggler toggler;
	
	private int tabContentMargin = 0;
	private int tabContentSize   = 0;
	
	private int tabHeaderSize    = 0;
	
	private final Point hidePoint = new Point(0, 0);
	private final Point origin = new Point(0, 0);
	private final Point target = new Point(0, 0);
	
	private int delta;
	
	protected Animator animator;
	
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
	  /**
	   * Called <em>before</em> the actual change takes place,
	   * therefore allows to detect an transition from deselected to selected.
	   */
	  public void selectionChanged(Object sender, SelectionChangedEvent event) {
	    if (sender == event.getToggableWidget() && event.isSelected()) {
	      if (sender instanceof TabButton) {
           tabContentSize = ((TabButton) sender).getValue().getMinHeight();
           tabContentSize += tabContentMargin;
        }
	      if (event.getToggableWidget().isSelected() && !isHidden()) {
	        hide(true);
	      } else {
	        show(true);
	      }
	    }
	  }
		
	};
	
	/**
	 * Creates a TabContainerToggler.
	 * @param container the container to be toggled
	 * @param group the group all tab-buttons belong to
	 * @param treatBorderAsHeader true 
	 */
	public TabContainerToggler(TabContainer container, ToggableGroup<?> group, boolean treatBorderAsHeader) {
		this.tabContainer = container;
    this.tabGroup     = group;
    Alignment align   = container.getContainerAlignment();
    
    tabGroup.addSelectionChangedListener(selectionListener);
    
    if (treatBorderAsHeader) {
      Spacing border  = container.getTabContainer().getAppearance().getBorder();
      int borderWidth = 0;
      switch (align) {
      case TOP:
        borderWidth = border.getTop();
        break;
      case BOTTOM:
        borderWidth = border.getBottom();
        break;
      }
      
      tabHeaderSize     += borderWidth;
      tabContentMargin  -= borderWidth;
    }
    
    switch (align) {
    case TOP:
    case BOTTOM:
      tabHeaderSize     += container.getHeaderContainer().getHeight();
      tabContentMargin  += tabContainer.getTabContainer().getAppearance().getTopMargins();
      tabContentMargin  += tabContainer.getTabContainer().getAppearance().getBottomMargins();
      
      tabContentSize     = tabContainer.getHeight() - tabHeaderSize;
      
      toggler = new VerticalToggler();
      break;
    default:
      throw new IllegalArgumentException(align + " is not supported.");
    }
    
    animator = new Animator(500, toggler);
	}
	
	/**
	 * Updates the containers position 
	 * @param animate
	 */
	private void update(boolean animate) {
	  if (tabContainer.getPosition().equals(target)) return;
	  
    if (animate) { 
      animate();
    } else { 
      tabContainer.setPosition(target.clone());
    }
	}
	
	/**
	 * Checks if the container is currently hidden.
	 * @return true if the contents of the container are fully hidden, false else
	 */
  public boolean isHidden() {
    return toggler.isHidden();
  }
  
  /**
   * Sets the position so the tab container appears hidden.
   * @param animate
   *        true to animate between old and new position, 
   *        false for an immediate change
   */
  public void hide(boolean animate) {
    toggler.hide(animate);
  }
  
  /**
   * Sets the position so the current tab content is visible.
   * @param animate
   *        true to animate between old and new position, 
   *        false for an immediate change
   */
  public void show(boolean animate) {
    toggler.show(animate);
  }
	
	/**
	 * Triggers the toggle animation. 
	 * If another animation is still running it will 
	 * <strong>not</strong> be interrupted.
	 */
	public void animate() {
	  if (!animator.isRunning()) 
	    animator.start();
	}
	
	private interface Toggler extends TimingTarget {
	  /**
	   * Sets the position so the current tab content is visible.
	   * @param animate
     *        true to animate between old and new position, 
     *        false for an immediate change
	   */
	  void show(boolean animate);

    /**
     * Sets the position so the tab container appears hidden.
     * @param animate
     *        true to animate between old and new position, 
     *        false for an immediate change
     */
	  void hide(boolean animate);
	  
	  /**
	   * Checks if the container is currently hidden.
	   * @return true if the container is fully hidden, false else
	   */
	  boolean isHidden();
	}
	
	private class VerticalToggler implements Toggler {
	  
	  boolean isBottom = tabContainer.getContainerAlignment() == Alignment.BOTTOM;
	  
	  public VerticalToggler() {
	    hidePoint.setXY(
	        tabContainer.getPosition().getX(), 
	        isBottom ? tabContentSize : -tabContentSize);
	    
	    target.setX(hidePoint.getX());
    }
	  
	  public void begin() {
	    origin.setY(tabContainer.getPosition().getY());
	    delta = target.getY() - origin.getY();
	  }

	  public void end() {}

	  public void repeat() {}

	  public void timingEvent(float t) {
	    int d = (int)(delta * t);
	    tabContainer.setPosition(new Point(origin.getX(), origin.getY() + d));
	  }

	  public boolean isHidden() {
	    return target.getY() == hidePoint.getY();
	  }
	  
	  public void hide(boolean animate) {
	    target.setY(hidePoint.getY());
	    update(animate);
	  }
	  
	  public void show(boolean animate) {
      target.setY(hidePoint.getY() + (isBottom ? -tabContentSize : tabContentSize));
	    update(animate);
	  }
	}
	
}
