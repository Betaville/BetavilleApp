/**
 * 
 */
package edu.poly.bxmc.betaville.module;

/**
 * @author Skye Book
 *
 */
public abstract class AdminModule extends Module {
	protected String buttonName;

	/**
	 * @param name
	 */
	public AdminModule(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param description
	 */
	public AdminModule(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	public void deconstruct() {
		// TODO Auto-generated method stub

	}

}
