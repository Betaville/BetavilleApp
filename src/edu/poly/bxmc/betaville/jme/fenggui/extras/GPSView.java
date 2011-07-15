/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.DecimalDegreeConverter;
import edu.poly.bxmc.betaville.jme.map.ILocation;

/**
 * @author Skye Book
 *
 */
public class GPSView extends Container {
	
	private Label title;
	
	private Label latLabel;
	private Label lonLabel;
	private Label latValue;
	private Label lonValue;
	
	private CheckBox<Boolean> ddDMSOption;
	
	public GPSView(){
		setLayoutManager(new RowExLayout(false));
		
		title = FengGUI.createWidget(Label.class);
		title.setText("GPS View");
		
		Container latCon = FengGUI.createWidget(Container.class);
		latCon.setLayoutManager(new RowExLayout(true));
		Container lonCon = FengGUI.createWidget(Container.class);
		lonCon.setLayoutManager(new RowExLayout(true));
		
		latLabel = FengGUI.createWidget(Label.class);
		latLabel.setText("Latitude");
		latLabel.setLayoutData(new RowExLayoutData(true, true));
		lonLabel = FengGUI.createWidget(Label.class);
		lonLabel.setText("Longitude");
		lonLabel.setLayoutData(new RowExLayoutData(true, true));
		
		latValue = FengGUI.createWidget(Label.class);
		latValue.setLayoutData(new RowExLayoutData(true, true));
		lonValue = FengGUI.createWidget(Label.class);
		lonValue.setLayoutData(new RowExLayoutData(true, true));
		
		latCon.addWidget(latLabel, latValue);
		lonCon.addWidget(lonLabel, lonValue);
		
		ddDMSOption = FengGUI.createCheckBox();
		ddDMSOption.setText("Display in Degrees/Minutes/Seconds");
		ddDMSOption.setSelected(false);
		
		addWidget(latCon, lonCon, ddDMSOption);
	}
	
	public void setTitle(String name){
		title.setText(name);
	}
	
	public void updateLocation(ILocation location){
		
		if(ddDMSOption.isSelected()){
			float[] latDMS = DecimalDegreeConverter.ddToDMS(location.getGPS().getLatitude());
			float[] lonDMS = DecimalDegreeConverter.ddToDMS(location.getGPS().getLongitude());
			latValue.setText(latDMS[0]+", "+latDMS[1]+", "+latDMS[2]);
			lonValue.setText(lonDMS[0]+", "+lonDMS[1]+", "+lonDMS[2]);
		}
		else{
			latValue.setText(""+location.getGPS().getLatitude());
			lonValue.setText(""+location.getGPS().getLongitude());
		}
	}
}
