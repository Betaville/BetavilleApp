package edu.poly.bxmc.betaville.gui;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class JFileChooserDialog extends JFileChooser{
	public JDialog createDialog(Component parent) throws HeadlessException {
	    return super.createDialog(parent);
	  }
}