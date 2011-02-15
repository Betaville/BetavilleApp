/**
 * 
 */
package edu.poly.bxmc.betaville.gui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

/**
 * @author Skye Book
 *
 */
public class BetavilleClipboardOwner implements ClipboardOwner {
	private static BetavilleClipboardOwner owner = new BetavilleClipboardOwner();

	/**
	 * 
	 */
	private BetavilleClipboardOwner(){}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}
	
	public static BetavilleClipboardOwner getInstance(){
		return owner;
	}
}
