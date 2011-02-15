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

import java.io.IOException;

import org.fenggui.TextEditor;
import org.fenggui.event.FocusEvent;
import org.fenggui.event.IStateChangedListener;
import org.fenggui.event.StateChangedEvent;
import org.fenggui.theme.xml.IXMLStreamableException;
import org.fenggui.theme.xml.InputOnlyStream;
import org.fenggui.util.StateIdentifier;

/**
 * BetavilleTextEditor is a {@link TextEditor} which allows a
 * a placeholder text to be set. The placeholder text disappears
 * as soon as there is user defined text or as soon the empty
 * TextEditor gains focus.</p>
 * 
 * BetavilleTextEditor adds the states {@code Text#Empty} and {@code Text#Default}.
 * 
 * @author Peter Schulz
 */
public class BetavilleTextEditor extends TextEditor {
  public static final StateIdentifier STATE_TEXT_EMPTY = 
    new StateIdentifier("Text", "Empty");
  public static final StateIdentifier STATE_TEXT_NOTEMPTY = 
    new StateIdentifier("Text", "Default");
  
  private String defaultText;
  
  /**
   * Creates a new BetavilleTextEditor.
   */
  public BetavilleTextEditor() {
    super();
    init();
  }
  
  /**
   * Creates a new BetavilleTextEditor.
   * @param stream the input stream to process
   * @throws IOException
   * @throws IXMLStreamableException
   */
  public BetavilleTextEditor(InputOnlyStream stream) throws IOException, IXMLStreamableException {
    init();
    process(stream);
  }
  
  @Override
  public BetavilleTextEditor clone()
  {
    BetavilleTextEditor res = (BetavilleTextEditor) super.clone();
    res.init();
    return res;
  }
  
  @Override
  public String getText() {
    String res = super.getText(); 
    return res.trim().equals(defaultText) ? "" : res;
  }

  @Override
  public void setText(String text)
  {
    super.setText(text);
    if (text == null || text.trim().isEmpty()) {
      getStateManager().activate(STATE_TEXT_EMPTY);
    }
    else {
      getStateManager().activate(STATE_TEXT_NOTEMPTY);
    }
  }

  /**
   * @return the default text
   */
  public String getDefaultText() {
    return defaultText;
  }

  /**
   * @param defaultText the default text to set
   */
  public void setDefaultText(String defaultText) {
    this.defaultText = defaultText;
    
    if (getText().trim().isEmpty()) {
      setText(null);
    }
  }

  @Override
  protected void onFocusChanged(FocusEvent event) {
    super.onFocusChanged(event);
    if (getText().trim().isEmpty()) {
      if (event.isFocusGained()) {
        getStateManager().activate(STATE_TEXT_NOTEMPTY);
      } else if (event.isFocusLost()) {
        getStateManager().activate(STATE_TEXT_EMPTY);
      }
    }
  }

  /**
   * Returns the {@link IStateChangedListener} 
   * responsible for showing/hiding the default text. 
   * @return a state change listener.
   */
  private IStateChangedListener getTextStateListener()
  {
    return new IStateChangedListener()
    {
      public void stateChanged(StateChangedEvent e)
      {
        if (e.getNewActiveState() != e.getOldActiveState()) {
          if (e.getNewActiveState() == STATE_TEXT_EMPTY)
            getTextRendererData().setContent(defaultText, getAppearance());
          else if (e.getNewActiveState() == STATE_TEXT_NOTEMPTY)
            getTextRendererData().setContent("", getAppearance());
        }
      }
    };
  }

  private void init() {
    getStateManager().addStates(STATE_TEXT_EMPTY, STATE_TEXT_NOTEMPTY);
    getStateManager().addStateChangedListener(getTextStateListener());
  }

}
