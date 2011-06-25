package edu.poly.bxmc.betaville.osm;

import java.util.ArrayList;
import java.util.List;

import edu.poly.bxmc.betaville.osm.tag.AbstractTag;

public class BaseOSMObject {

	protected long id;
	protected List<AbstractTag> tags = new ArrayList<AbstractTag>();

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

	/**
	 * @return the tags
	 */
	public List<AbstractTag> getTags() {
		return tags;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addTag(AbstractTag tag) {
		tags.add(tag);
	}

	public String findTag(Class<? extends AbstractTag> keyClass) {
		for(AbstractTag tag : tags){
			if(tag.getClass().equals(keyClass)){
				return tag.getValue();
			}
		}
		return null;
	}

}