package edu.poly.bxmc.betaville.osm;

public class BaseOSMObject {

	protected long id;

	public BaseOSMObject() {
		super();
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

}