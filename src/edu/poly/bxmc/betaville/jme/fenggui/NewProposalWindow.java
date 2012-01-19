/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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
 */
package edu.poly.bxmc.betaville.jme.fenggui;

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.RadioButton;
import org.fenggui.Slider;
import org.fenggui.TextEditor;
import org.fenggui.ToggableGroup;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ActivationEvent;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IActivationListener;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;

import com.jme.image.Image;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.gui.AcceptedModelFilter;
import edu.poly.bxmc.betaville.gui.ColladaFileFilter;
import edu.poly.bxmc.betaville.gui.WavefrontFileFilter;
import edu.poly.bxmc.betaville.jme.fenggui.MakeRoomWindow.IFinishedListener;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengTextContentException;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState.SOUNDS;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.map.DecimalDegreeConverter;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Translator;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.model.ProposalPermission;
import edu.poly.bxmc.betaville.model.StringVerifier;
import edu.poly.bxmc.betaville.model.Design.Classification;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.model.ProposalPermission.Type;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.net.PhysicalFileTransporter;
import edu.poly.bxmc.betaville.net.ProgressOutputStream.ProgressOutputListener;
import edu.poly.bxmc.betaville.net.ProtectedManager;
import edu.poly.bxmc.betaville.net.SecureClientManager;

/**
 * The window used to create new proposals, or versions of proposals.
 * @author Skye Book
 *
 */
public class NewProposalWindow extends Window implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(NewProposalWindow.class);

	private Window baseModelOption;
	private FixedButton baseDesignButton;
	private FixedButton proposalDesignButton;

	private Window errorWindow;
	private FixedButton errorDismiss;
	private FixedButton errorMoveOn;

	private Window simpleErrorWindow;
	private FixedButton simpleErrorOK;

	private boolean canCommitBase = false;

	private Pixmap green;
	private Pixmap yellow;
	private Pixmap red;

	private Label statusOne;
	private Label statusTwo;
	private Label statusThree;
	private Label statusFour;
	private Label statusFive;

	private FixedButton next;
	private FixedButton back;

	private Container selectionContainer;

	private Container stepOne;
	private Container stepTwo;
	private Container stepThree;
	private Container stepFour;
	private Container stepFive;

	private int currentStep=1;
	private int selectedStep=0;
	private boolean lockAtCurrentStep=false;

	// pre-access options
	private Design selectedDesignWhenOpened=null;

	private NewProposalWindow currentNewProposalWindow;

	// Step One
	private Classification stepOneSelection = Classification.PROPOSAL;
	private FixedButton newProposal;
	private FixedButton newBase;
	private TextEditor proposalTitle;
	private final String proposalTitleDefaultContent="PROPOSAL TITLE";
	private TextEditor proposalDescription;
	private final String proposalDescriptionDefaultContent="PROPOSAL DESCRIPTION";
	private TextEditor proposalAddress;
	private final String proposalAddressDefaultContent="ADDRESS (optional)";
	private TextEditor proposalURL;
	private final String proposalURLDefaultContent="WEBSITE URL (optional)";
	private FixedButton newVersion;
	private final String newVersionText = "new version of existing proposal";
	private Label versionAdvisor;
	private Label versionNoGo;
	private TextEditor versionDescription;
	private final String versionDescriptionDefault = "VERSION DESCRIPTION";

	private boolean titleChanged = false;
	private boolean descriptionChanged = false;
	private boolean addressChanged = false;
	private boolean urlChanged = false;
	private boolean versionDescriptionChanged=false;


	// Step Two
	private Label setItUp;
	private Label setItUpAdvisor;
	private UTMCoordinate coordinate;
	private TextEditor latDeg;
	private TextEditor latMin;
	private TextEditor latSec;
	private TextEditor lonDeg;
	private TextEditor lonMin;
	private TextEditor lonSec;
	private String latDegDefaultContent="Lat Deg.";
	private String latMinDefaultContent="Lat Min.";
	private String latSecDefaultContent="Lat Sec.";
	private String lonDegDefaultContent="Lon Deg.";
	private String lonMinDefaultContent="Lon Min.";
	private String lonSecDefaultContent="Lon Sec.";

	private FixedButton removeObstructionsButton;
	private MakeRoomWindow makeRoomWindow;
	private String removables="";

	private TextEditor mediaPath;
	private URL mediaURL = null;
	private FixedButton browseButton;
	private ComboBox textureSelector;
	private FixedButton importModel;
	private String modelIdentifier;

	private boolean locationIsSet=false;
	private boolean modelIsLoaded=false;

	private ModeledDesign designCreatedInThisWindow;
	private File packedFile = null;


	// Step Three
	private Label tweakIt;
	private FixedButton north;
	private FixedButton south;
	private FixedButton east;
	private FixedButton west;
	private FixedButton up;
	private FixedButton down;
	private Label moveSpeedLabel;
	private String moveSpeedPrefix="Move Speed: ";
	private Slider moveSpeedSlider;
	private int maxMoveSpeed=50;
	private int moveSpeed=1;

	private Label rotationLabel;
	private String rotationText = "Rotation";
	private Label xRotationLabel;
	private Label yRotationLabel;
	private Label zRotationLabel;
	private String xRotationPrefix="X: ";
	private String yRotationPrefix="Y: ";
	private String zRotationPrefix="Z: ";
	private Slider xRotationSlider;
	private Slider yRotationSlider;
	private Slider zRotationSlider;

	// Step Four
	private Label snapIt;
	private Label snapItAdvisor;
	private FixedButton shoot;
	private FixedButton save;
	private Label photoFrame;
	private boolean photoFrameLive=false;
	private File imageFile = null;

	private boolean atLeastOnePictureTaken=false;
	private boolean pictureTakenAndAccepted=false;


	// Step Five
	private Label shareIt;
	private ToggableGroup<RadioButton<String>> permissionsSelection;
	private  RadioButton<String> permissionsClosed;
	private  RadioButton<String> permissionsOpen;
	private  RadioButton<String> permissionsGroup;
	private TextEditor groupList;
	private String defaultGroupListText="username, username,...";
	private ComboBox permissionsCombo;
	private FixedButton upload;
	private boolean verifyingNames=false;

	private boolean groupSelected=false;
	private boolean groupEntered=false;


	private int targetWidth=300;
	private int targetHeight=425;
	private int stepFourWindowHeight=200;



	public NewProposalWindow() {
		super(true, true);
		currentNewProposalWindow = this;

		if(SettingsPreferences.getUser()!=null){
			UserType ut = NetPool.getPool().getConnection().getUserLevel(SettingsPreferences.getUser());

			// check if the user is a base committer or higher
			if(ut.compareTo(UserType.BASE_COMMITTER)>=0){
				canCommitBase=true;
			}
			else canCommitBase=false;
		}

		getContentContainer().setLayoutManager(new StaticLayout());
		createSwitcher();
		createStepOne();
		createStepTwo();
		createStepThree();
		createStepFour();
		createStepFive();

		createErrorWindow();
		createProposalOrBaseWindow();
		createSimpleErrorWindow();

		getContentContainer().addWidget(stepOne);
		layout();

		// This listener will keep the window within the available display area
		addEventListener(EVENT_POSITIONCHANGED, new IGenericEventListener(){
			public void processEvent(Object source, Event event) {
				SettingsPreferences.getGUIThreadPool().submit(new Runnable(){
					public void run() {
						// test x
						if(getX()<0){
							setX(0);
						}
						else if(getX()+getWidth()>DisplaySystem.getDisplaySystem().getWidth()){
							setX(DisplaySystem.getDisplaySystem().getWidth()-getWidth());
						}

						// Include the photoframe in the bounds checking only if
						// it is being displayed.
						if(!photoFrameLive){
							// test y
							if(getY()<0){
								setY(0);
							}
							else if(getY()+getHeight()>DisplaySystem.getDisplaySystem().getHeight()){
								setY(DisplaySystem.getDisplaySystem().getHeight()-getHeight());
							}
						}
						else{
							// If the photoframe is live, update its position.
							photoFrame.setXY(getX()-5, getY()-photoFrame.getHeight());

							// test y
							if(photoFrame.getY()<0){
								photoFrame.setY(0);
								setY(photoFrame.getHeight());
							}
							else if(getY()+getHeight()>DisplaySystem.getDisplaySystem().getHeight()){
								setY(DisplaySystem.getDisplaySystem().getHeight()-getHeight());
							}
						}

						// test the make room window
						if(makeRoomWindow.isInWidgetTree()){
							makeRoomWindow.setXY(removeObstructionsButton.getDisplayX()+removeObstructionsButton.getWidth(), removeObstructionsButton.getDisplayY()+removeObstructionsButton.getHeight()-makeRoomWindow.getHeight());
						}
					}
				});
			}
		});
	}

	private void createSwitcher(){
		selectionContainer = FengGUI.createWidget(Container.class);
		selectionContainer.setLayoutManager(new StaticLayout());

		try {
			green = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/proposalwindow/green.png"));
			yellow = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/proposalwindow/yellow.png"));
			red = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/proposalwindow/red.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		statusOne = FengGUI.createWidget(Label.class);
		statusTwo = FengGUI.createWidget(Label.class);
		statusThree = FengGUI.createWidget(Label.class);
		statusFour = FengGUI.createWidget(Label.class);
		statusFive = FengGUI.createWidget(Label.class);
		statusOne.setPixmap(yellow);
		statusTwo.setPixmap(red);
		statusThree.setPixmap(red);
		statusFour.setPixmap(red);
		statusFive.setPixmap(red);

		int half = targetWidth/2;
		int offset = 2;
		int sizeOfFive = 5*(10+offset);
		statusOne.setXY(half-(sizeOfFive/2)-(10+offset), 0);
		statusTwo.setXY(statusOne.getX()+statusOne.getWidth()+offset, 0);
		statusThree.setXY(statusTwo.getX()+statusTwo.getWidth()+offset, 0);
		statusFour.setXY(statusThree.getX()+statusThree.getWidth()+offset, 0);
		statusFive.setXY(statusFour.getX()+statusFour.getWidth()+offset, 0);

		statusOne.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseReleasedEvent){
					if(currentStep!=1){
						switchTo(1, false);
					}
				}
			}
		});

		statusTwo.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseReleasedEvent){
					if(currentStep!=2){
						switchTo(2, false);
					}
				}
			}
		});

		statusThree.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseReleasedEvent){
					if(currentStep!=3){
						switchTo(3, false);
					}
				}
			}
		});

		statusFour.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseReleasedEvent){
					if(currentStep!=4){
						switchTo(4, false);
					}
				}
			}
		});

		statusFive.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseReleasedEvent){
					if(currentStep!=5){
						switchTo(5, false);
					}
				}
			}
		});


		next = FengGUI.createWidget(FixedButton.class);
		next.setText("next");
		next.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				switchTo(currentStep+1, false);
			}
		});

		back = FengGUI.createWidget(FixedButton.class);
		back.setText("back");
		back.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				switchTo(currentStep-1, false);
			}
		});

		next.setWidth(next.getWidth()+10);
		back.setWidth(back.getWidth()+10);
		next.setXY(targetWidth-next.getWidth(), 0);
		back.setXY(0, 0);
		selectionContainer.addWidget(next);
		selectionContainer.addWidget(statusOne, statusTwo, statusThree, statusFour, statusFive);
		selectionContainer.layout();
		getContentContainer().addWidget(selectionContainer);
		selectionContainer.setXY(0, 0);
		selectionContainer.setSize(targetWidth, targetHeight-30);
	}

	private void createStepOne(){
		stepOne = FengGUI.createWidget(Container.class);
		stepOne.setLayoutManager(new StaticLayout());
		stepOne.setSize(targetWidth-10, targetHeight-50);
		stepOne.setXY(5, 20);

		int heightOffset=20;

		newProposal = FengGUI.createWidget(FixedButton.class);
		newProposal.setText("new proposal");
		newProposal.setWidth(newProposal.getWidth()+10);
		if(canCommitBase){
			newProposal.setX(10);
		}
		else{
			newProposal.setX(FengUtils.midWidth(stepOne, newProposal));
		}
		newProposal.setY(stepOne.getHeight()-newProposal.getHeight());
		newProposal.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				if(modelIsLoaded){
					showSimpleError("Cannot change type now");
					return;
				}
				logger.debug("User is creating proposal");
				stepOneSelection = Classification.PROPOSAL;
				setStepOneButtonsCorrectly();
				setStepOneTextFields();
			}
		});

		newBase = FengGUI.createWidget(FixedButton.class);
		newBase.setText("add to base");
		newBase.setWidth(newProposal.getWidth());
		newBase.setXY(stepOne.getWidth()-newBase.getWidth()-10, stepOne.getHeight()-newBase.getHeight());
		newBase.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(modelIsLoaded){
					showSimpleError("Cannot change type now");
					return;
				}
				logger.debug("User is creating base model");
				stepOneSelection = Classification.BASE;
				updateStepOneSelection();
				setStepOneButtonsCorrectly();
				setStepOneTextFields();
			}});

		proposalTitle = FengGUI.createWidget(TextEditor.class);
		proposalTitle.setText(proposalTitleDefaultContent);
		proposalTitle.setWidth(stepOne.getWidth()-10);
		proposalTitle.setXY(FengUtils.midWidth(stepOne, proposalTitle), newProposal.getY()-proposalTitle.getHeight()-heightOffset);
		proposalTitle.addTextChangedListener(new ITextChangedListener(){
			public void textChanged(TextChangedEvent textChangedEvent) {
				titleChanged=true;
			}
		});

		proposalDescription = FengGUI.createWidget(TextEditor.class);
		proposalDescription.setText(proposalDescriptionDefaultContent);
		proposalDescription.setWidth(stepOne.getWidth()-10);
		proposalDescription.setXY(FengUtils.midWidth(stepOne, proposalDescription), proposalTitle.getY()-proposalDescription.getHeight()-heightOffset);
		proposalDescription.addTextChangedListener(new ITextChangedListener(){
			public void textChanged(TextChangedEvent textChangedEvent) {
				descriptionChanged=true;
			}
		});

		proposalAddress = FengGUI.createWidget(TextEditor.class);
		proposalAddress.setText(proposalAddressDefaultContent);
		proposalAddress.setWidth(stepOne.getWidth()-10);
		proposalAddress.setXY(FengUtils.midWidth(stepOne, proposalAddress), proposalDescription.getY()-proposalAddress.getHeight()-heightOffset);

		proposalURL = FengGUI.createWidget(TextEditor.class);
		proposalURL.setText(proposalURLDefaultContent);
		proposalURL.setWidth(stepOne.getWidth()-10);
		proposalURL.setXY(FengUtils.midWidth(stepOne, proposalURL), proposalAddress.getY()-proposalURL.getHeight()-heightOffset);

		newVersion = FengGUI.createWidget(FixedButton.class);
		newVersion.setText(newVersionText);
		newVersion.setWidth(newVersion.getWidth()+10);
		newVersion.setXY(FengUtils.midWidth(stepOne, newVersion), proposalURL.getY()-newVersion.getHeight()-newVersion.getHeight());
		newVersion.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				if(modelIsLoaded){
					showSimpleError("Cannot change type now");
					setStepOneButtonsCorrectly();
					return;
				}
				if(selectedDesignWhenOpened==null){
					showSimpleError("Proposal Must Be Selected When Opened");
					logger.warn("Version can't be made without proposal selected");
					setStepOneButtonsCorrectly();
					return;
				}
				else if(selectedDesignWhenOpened.isClassification(Classification.BASE)){
					showSimpleError("Can Only Make Versions of Proposals!");
					logger.warn("Version can't be made for base designs");
					setStepOneButtonsCorrectly();
					return;
				}
				logger.debug("User is creating version");
				stepOneSelection = Classification.VERSION;
				updateStepOneSelection();
				setStepOneButtonsCorrectly();
				setStepOneTextFields();
			}
		});

		versionAdvisor = FengGUI.createWidget(Label.class);
		versionAdvisor.setText("[Click on the model you want to update]");
		versionAdvisor.setXY(FengUtils.midWidth(stepOne, versionAdvisor), newVersion.getY()-versionAdvisor.getHeight()-heightOffset);

		versionNoGo = FengGUI.createWidget(Label.class);
		versionNoGo.getAppearance().removeAll();
		versionNoGo.getAppearance().add(new PlainBackground(Color.RED));
		versionNoGo.setMultiline(true);
		versionNoGo.setText("At its creator\'s request, the currently\n" +
				"selected model is read-only.  You can\n" +
				"make suggestions through its forum,\n" +
		"or make a new proposal.");
		versionNoGo.setWidth(stepOne.getWidth()-10);
		versionNoGo.setXY(FengUtils.midWidth(stepOne, versionNoGo), newVersion.getY()-versionNoGo.getHeight()-heightOffset);

		versionDescription = FengGUI.createWidget(TextEditor.class);
		versionDescription.setText(versionDescriptionDefault);
		versionDescription.setWidth(stepOne.getWidth()-10);
		versionDescription.setXY(FengUtils.midWidth(stepOne, versionDescription), versionAdvisor.getY()-versionDescription.getHeight()-heightOffset);
		proposalTitle.addTextChangedListener(new ITextChangedListener(){
			public void textChanged(TextChangedEvent textChangedEvent) {
				versionDescriptionChanged=true;
			}
		});



		stepOne.addWidget(newProposal, proposalTitle, proposalDescription, proposalAddress, proposalURL,
				newVersion, versionAdvisor, versionDescription);

		newProposal.setEnabled(false);
		if(canCommitBase){
			stepOne.addWidget(newBase);
		}
	}

	private void setStepOneTextFields(){
		String titleTag=null;
		String descTag=null;
		String addrTag=null;
		String urlTag=null;
		switch (stepOneSelection) {
		case BASE:
			titleTag = "Base Model Name";
			descTag = "Base Model Description";
			addrTag = "Base Model Address (Optional)";
			urlTag = "Base Model URL (Optional)";
			break;
		case PROPOSAL:
			titleTag = proposalTitleDefaultContent;
			descTag = proposalDescriptionDefaultContent;
			addrTag = proposalAddressDefaultContent;
			urlTag = proposalURLDefaultContent;
			break;
		case VERSION:
			titleTag = "Version Model Name";
			descTag = "Version Model Description";
			addrTag = "Version Model Address (Optional)";
			urlTag = "Version Model URL (Optional)";
			break;
		}
		proposalTitle.setText(titleTag);
		proposalDescription.setText(descTag);
		proposalAddress.setText(addrTag);
		proposalURL.setText(urlTag);
	}

	private void createStepTwo(){
		stepTwo = FengGUI.createWidget(Container.class);
		stepTwo.setLayoutManager(new StaticLayout());
		stepTwo.setSize(targetWidth-10, targetHeight-50);
		stepTwo.setXY(5, 20);

		setItUp = FengGUI.createWidget(Label.class);
		setItUp.setText("SET IT UP!");
		setItUp.setXY((stepTwo.getWidth()/2)-(setItUp.getWidth()/2), stepTwo.getHeight()-20);

		int heightOffset=10;

		setItUpAdvisor = FengGUI.createWidget(Label.class);
		setItUpAdvisor.setMultiline(true);
		setItUpAdvisor.setText("[Click on a building or the ground\n" +
		"to automatically set coordinates]");
		applyAdvisorAppearance(setItUpAdvisor);
		setItUpAdvisor.setXY(stepTwo.getWidth()/2-setItUpAdvisor.getWidth()/2, setItUp.getY()-setItUpAdvisor.getHeight()-heightOffset);

		latDeg = FengGUI.createWidget(TextEditor.class);
		latMin = FengGUI.createWidget(TextEditor.class);
		latSec = FengGUI.createWidget(TextEditor.class);
		lonDeg = FengGUI.createWidget(TextEditor.class);
		lonMin = FengGUI.createWidget(TextEditor.class);
		lonSec = FengGUI.createWidget(TextEditor.class);
		//		this.latDegX = latDegX;
		//		this.latMinX = latMinX;
		//		this.latSecX = latSecX;
		//		this.lonDegX = lonDegX;
		//		this.lonMinX = lonMinX;
		//		this.lonSecX = lonSecX;


		latDeg.setText(latDegDefaultContent);
		latMin.setText(latMinDefaultContent);
		latSec.setText(latSecDefaultContent);
		lonDeg.setText(lonDegDefaultContent);
		lonMin.setText(lonMinDefaultContent);
		lonSec.setText(lonSecDefaultContent);
		latDeg.setWidth(75);
		latMin.setWidth(75);
		latSec.setWidth(75);
		lonDeg.setWidth(75);
		lonMin.setWidth(75);
		lonSec.setWidth(75);
		latDeg.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);
		latMin.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);
		latSec.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		lonDeg.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);
		lonMin.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);
		lonSec.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);

		Container latContainer = FengGUI.createWidget(Container.class);
		latContainer.setLayoutManager(new RowLayout(true));
		latContainer.addWidget(latDeg, latMin, latSec);
		latContainer.layout();
		latContainer.setXY((stepTwo.getWidth()/2)-(latContainer.getWidth()/2), setItUpAdvisor.getY()-latContainer.getHeight()-heightOffset);

		Container lonContainer = FengGUI.createWidget(Container.class);
		lonContainer.setLayoutManager(new RowLayout(true));
		lonContainer.addWidget(lonDeg, lonMin, lonSec);
		lonContainer.layout();
		lonContainer.setXY((stepTwo.getWidth()/2)-(lonContainer.getWidth()/2), latContainer.getY()-lonContainer.getHeight()-heightOffset);

		IFinishedListener finishedListener = new IFinishedListener() {
			public void finished(Collection<Integer> values) {
				removables="";
				if(values.isEmpty()){
					removables="NONE";
					logger.debug("No Removables Selected");
					return;
				}
				for(int value : values){
					removables+=value+";";
				}
				logger.debug("Remove String: " + removables);
			}
		};

		makeRoomWindow = FengGUI.createWidget(MakeRoomWindow.class);
		makeRoomWindow.finishSetup(new ICloseAction() {
			public void close() {
				GUIGameState.getInstance().getDisp().removeWidget(makeRoomWindow);
				lockAtCurrentStep=false;
				setSwitcherStatus(true);
			}
		}, finishedListener);


		removeObstructionsButton = FengGUI.createWidget(FixedButton.class);
		removeObstructionsButton.setText("remove obstructions");
		removeObstructionsButton.setWidth(removeObstructionsButton.getWidth()+10);
		removeObstructionsButton.setXY(FengUtils.midWidth(stepTwo, removeObstructionsButton), lonContainer.getY()-removeObstructionsButton.getHeight()-(heightOffset*2));
		removeObstructionsButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!makeRoomWindow.isInWidgetTree()){
					placeMakeRoomWindow();
					removeObstructionsButton.setText("done removing");
					browseButton.setEnabled(false);
					importModel.setEnabled(false);
				}
				else{
					makeRoomWindow.completeAndClose();
					removeObstructionsButton.setText("remove obstructions");
					browseButton.setEnabled(true);
					importModel.setEnabled(true);
				}
			}
		});

		if(stepOneSelection.equals(Classification.BASE)){
			removeObstructionsButton.setText("Nothing to Remove");
			removeObstructionsButton.setEnabled(false);
		}
		else{
			removeObstructionsButton.setEnabled(true);
			removeObstructionsButton.setText("remove obstructions");
		}

		mediaPath = FengGUI.createWidget(TextEditor.class);
		mediaPath.setText("Location of Media");
		mediaPath.setReadonly(true);
		mediaPath.setWidth(stepTwo.getWidth()-10);
		mediaPath.setXY((stepTwo.getWidth()/2)-(mediaPath.getWidth()/2), removeObstructionsButton.getY()-mediaPath.getHeight()-(heightOffset*2));

		browseButton = FengGUI.createWidget(FixedButton.class);
		browseButton.setText("Browse Media..");
		browseButton.setWidth(browseButton.getWidth()+5);
		int tripletY=mediaPath.getY()-browseButton.getHeight()-heightOffset;
		browseButton.setXY(5, tripletY);

		// SWING FILE BROWSER
		browseButton.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				SettingsPreferences.getThreadPool().submit(new Runnable() {

					public void run() {
						JDialog dialog = new JDialog();
						dialog.setModalityType(ModalityType.APPLICATION_MODAL);
						JFileChooser fileChooser = new JFileChooser(SettingsPreferences.BROWSER_LOCATION);
						AcceptedModelFilter modelFilter = new AcceptedModelFilter();
						fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
						fileChooser.addChoosableFileFilter(modelFilter);
						fileChooser.addChoosableFileFilter(new ColladaFileFilter());
						fileChooser.addChoosableFileFilter(new WavefrontFileFilter());
						fileChooser.setFileFilter(modelFilter);
						fileChooser.showOpenDialog(dialog);
						File file = fileChooser.getSelectedFile();
						
						// flash an error if the file is larger than 5mb
						if(file.length()>5000000){
							logger.warn(file.toString()+" is "+file.length()+"bytes.  This is rather large");
							
							GUIGameState.getInstance().getDisp().addWidget(
									FengUtils.createDismissableWindow("Betaville", "The selected file is rather large, at "+
											(file.length()/1000000f)+"MB, why don't you see if you can't get that down a bit?", "ok", false));
							
						}

						String pathToDisplay = file.toString();
						if(pathToDisplay.contains("/")){
							pathToDisplay = new String(pathToDisplay.substring(pathToDisplay.lastIndexOf("/")+1));
						}
						else if(pathToDisplay.contains("\\")){
							pathToDisplay = new String(pathToDisplay.substring(pathToDisplay.lastIndexOf("\\")+1));
						}
						SettingsPreferences.BROWSER_LOCATION = fileChooser.getCurrentDirectory();
						logger.info("path "+pathToDisplay);
						mediaPath.setText(pathToDisplay);
						try {
							mediaURL = file.toURI().toURL();
						} catch (MalformedURLException e) {
							logger.warn("Problem occured when selecting from file browser", e);
						}
					}
				});
			}
		});

		textureSelector = FengGUI.createWidget(ComboBox.class);
		textureSelector.addItem("Textured");
		textureSelector.addItem("Untextured");
		textureSelector.setSize(textureSelector.getWidth()+5, browseButton.getHeight());
		textureSelector.setXY(FengUtils.midWidth(stepTwo, textureSelector), tripletY);

		importModel = FengGUI.createWidget(FixedButton.class);
		importModel.setText("Import");
		importModel.setWidth(browseButton.getWidth());
		importModel.setXY(stepTwo.getWidth()-importModel.getWidth()-5, tripletY);
		importModel.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				logger.debug("Import Button Pressed");
				String title = null;
				String description = null;
				String address = null;
				String url=null;

				if(!titleChanged && !stepOneSelection.equals(Classification.VERSION)){
					showSimpleError("Please Name Design");
					logger.warn("Design Needs To Be Named");
					return;
				}
				else if(titleChanged){
					title = FengUtils.getText(proposalTitle);
				}
				else if(stepOneSelection.equals(Classification.VERSION)){
					title = "VersionOf";
				}

				if(!descriptionChanged || FengUtils.getText(proposalDescription).equals(proposalDescriptionDefaultContent)){
					description="None";
				}
				else description=FengUtils.getText(proposalDescription);

				if(!addressChanged){
					address="None";
				}
				else address=FengUtils.getText(proposalAddress);

				if(!urlChanged){
					url="None";
				}
				else url=FengUtils.getText(proposalURL);


				if(mediaURL==null){
					showSimpleError("No Media Selected!");
					mediaPath.setText("MODEL NOT SELECTED!");
					return;
				}


				try {
					coordinate = createCoordinate();
				} catch (NumberFormatException e1) {
					FengUtils.showNewDismissableWindow("Betaville", "Make sure you've set coordinates for your item!", "ok", true);
				}

				if(coordinate!=null){
					boolean textured = textureSelector.getSelectedValue().equals("Textured");

					// create a design from the supplied information, it will initialize with an ID and sourceID of zero
					designCreatedInThisWindow = new ModeledDesign(title, coordinate, address, SceneScape.getCity().getCityID(), SettingsPreferences.getUser(), description, mediaURL.toString(), url, true, 0, 0, 0, textured);
					designCreatedInThisWindow.setClassification(stepOneSelection);

					try {
						packedFile = SceneGameState.getInstance().addDesignToCity(designCreatedInThisWindow, mediaURL, mediaURL, designCreatedInThisWindow.getSourceID());
						modelIdentifier=designCreatedInThisWindow.getFullIdentifier();
						logger.info("modelIdentifier"+modelIdentifier);
						/*
						Vector3f distanceFromZero = GeometryUtilities.getDistanceFromZero(SceneGameState.getInstance().getDesignNode().getChild(modelIdentifier));
						if(distanceFromZero.getX()!=0 || distanceFromZero.getY()!=0 || distanceFromZero.getZ()!=0){
							showSimpleError("Not at Zero! " + distanceFromZero.getX() +","+distanceFromZero.getY()+distanceFromZero.getZ());
						}
						 */

						logger.info(designCreatedInThisWindow.toString() + " imported");
						modelIsLoaded=true;
					} catch (URISyntaxException uriException){
						logger.warn(uriException);
						// send error to add design window
					} catch (IOException ioException){
						logger.warn("File could not be found when trying to import!", ioException);
						showSimpleError("File could not be found\nwhen trying to import!");
					}
				}
			}
		});

		stepTwo.addWidget(setItUp, setItUpAdvisor, latContainer, lonContainer,
				removeObstructionsButton, mediaPath, browseButton, textureSelector, importModel);
	}

	private void createStepThree(){
		stepThree = FengGUI.createWidget(Container.class);
		stepThree.setLayoutManager(new StaticLayout());
		stepThree.setSize(targetWidth-10, targetHeight-50);
		stepThree.setXY(5, 20);

		int offset=15;

		tweakIt = FengGUI.createWidget(Label.class);
		tweakIt.setText("TWEAK IT!");
		tweakIt.setXY((stepThree.getWidth()/2)-(tweakIt.getWidth()/2), stepThree.getHeight()-offset);

		north = FengGUI.createWidget(FixedButton.class);
		north.setText("north");
		north.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!modelIsLoaded){
					showSimpleError("Load a model first!");
					return;
				}

				Translator.moveX(SceneGameState.getInstance().getDesignNodeChild(modelIdentifier), moveSpeed);
				SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier).getCoordinate().move(0, moveSpeed, 0);
			}
		});

		south = FengGUI.createWidget(FixedButton.class);
		south.setText("south");
		south.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!modelIsLoaded){
					showSimpleError("Load a model first!");
				}

				Translator.moveX(SceneGameState.getInstance().getDesignNodeChild(modelIdentifier), -moveSpeed);
				SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier).getCoordinate().move(0, -moveSpeed, 0);
			}
		});

		east = FengGUI.createWidget(FixedButton.class);
		east.setText("east");
		east.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!modelIsLoaded){
					showSimpleError("Load a model first!");
					return;
				}

				Translator.moveZ(SceneGameState.getInstance().getDesignNodeChild(modelIdentifier), moveSpeed);
				SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier).getCoordinate().move(moveSpeed, 0, 0);

				//final Node dNode = (Node)SceneGameState.getInstance().getDesignNode().getChild("$1357");
				//dNode.setLocalTranslation(MapManager.utmToBetaville(new UTMCoordinate(583558,4506150,18,'T',10)));
				//Translator.moveY(SceneGameState.getInstance().getDesignNode().getChild("$1357"), 1);
				//SceneScape.getCity().findDesignByFullIdentifier("$1357").getCoordinate().move(0, 0, 1);
				//sadf$local
				//1357
				//Node n= (Node)SceneGameState.getInstance().getDesignNode().getChild("$320");
				//Node n= (Node)SceneGameState.getInstance().getDesignNode();
				//logger.info(n.getQuantity());logger.info(n.getChild(5).getName());
				//for(int i=0; i<n.getQuantity(); i++){
				//	logger.info(n.getChild(i).getName());
				//	logger.info("+++");
				//}
			}
		});

		west = FengGUI.createWidget(FixedButton.class);
		west.setText("west");
		west.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!modelIsLoaded){
					showSimpleError("Load a model first!");
					return;
				}

				Translator.moveZ(SceneGameState.getInstance().getDesignNodeChild(modelIdentifier), -moveSpeed);
				SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier).getCoordinate().move(-moveSpeed, 0, 0);
			}
		});

		up = FengGUI.createWidget(FixedButton.class);
		up.setText("up");
		up.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!modelIsLoaded){
					showSimpleError("Load a model first!");
					return;
				}

				Translator.moveY(SceneGameState.getInstance().getDesignNodeChild(modelIdentifier), moveSpeed);
				SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier).getCoordinate().move(0, 0, moveSpeed);
			}
		});

		down = FengGUI.createWidget(FixedButton.class);
		down.setText("down");
		down.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!modelIsLoaded){
					showSimpleError("Load a model first!");
					return;
				}

				Translator.moveY(SceneGameState.getInstance().getDesignNodeChild(modelIdentifier), -moveSpeed);
				SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier).getCoordinate().move(0, 0, -moveSpeed);
			}
		});

		// Since south has the longest name, we normalize
		// all buttons to it's width.
		south.setWidth(south.getWidth()+10);
		north.setWidth(south.getWidth());
		east.setWidth(south.getWidth());
		west.setWidth(south.getWidth());
		up.setWidth(south.getWidth());
		down.setWidth(south.getWidth());

		north.setXY(stepThree.getWidth()/2-north.getWidth()/2, tweakIt.getY()-north.getHeight()-offset);
		up.setXY(north.getX(), north.getY()-up.getHeight()-(offset*2));
		down.setXY(north.getX(), up.getY()-down.getHeight()-(offset/4));
		south.setXY(north.getX(), down.getY()-south.getHeight()-(offset*2));
		west.setXY(5, down.getY()+((up.getHeight()+(offset/4)+down.getHeight())/2)-(west.getHeight()/2));
		east.setXY(stepThree.getWidth()-east.getWidth()-5, down.getY()+((up.getHeight()+(offset/4)+down.getHeight())/2)-(east.getHeight()/2));

		moveSpeedLabel = FengGUI.createWidget(Label.class);
		moveSpeedLabel.setText(moveSpeedPrefix+moveSpeed);
		moveSpeedLabel.setXY((stepThree.getWidth()/2)-(moveSpeedLabel.getWidth()/2), south.getY()-moveSpeedLabel.getHeight()-offset);

		moveSpeedSlider = FengGUI.createSlider(true);
		moveSpeedSlider.setWidth(stepThree.getWidth()-10);
		moveSpeedSlider.setXY((stepThree.getWidth()/2)-(moveSpeedSlider.getWidth()/2), moveSpeedLabel.getY()-moveSpeedSlider.getHeight()-(offset/4));
		moveSpeedSlider.addSliderMovedListener(new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(moveSpeedSlider.getValue()*(maxMoveSpeed-1))+1;
				moveSpeed=newValue;
				moveSpeedLabel.setText(moveSpeedPrefix+moveSpeed);
			}
		});




		rotationLabel = FengGUI.createWidget(Label.class);
		rotationLabel.setText(rotationText);
		rotationLabel.setXY((stepThree.getWidth()/2)-(rotationLabel.getWidth()/2), moveSpeedSlider.getY()-rotationLabel.getHeight()-(offset/2));

		xRotationLabel = FengGUI.createWidget(Label.class);
		xRotationLabel.setText(xRotationPrefix+"0");

		yRotationLabel = FengGUI.createWidget(Label.class);
		yRotationLabel.setText(yRotationPrefix+"0");

		zRotationLabel = FengGUI.createWidget(Label.class);
		zRotationLabel.setText(zRotationPrefix+"0");

		xRotationSlider = FengGUI.createSlider(true);
		xRotationSlider.setWidth(stepThree.getWidth()-20-xRotationLabel.getWidth());
		xRotationSlider.setXY(stepThree.getWidth()-xRotationSlider.getWidth()-5, rotationLabel.getY()-xRotationSlider.getHeight()-(offset/8));
		xRotationSlider.setValue(0);
		xRotationSlider.addSliderMovedListener(new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(xRotationSlider.getValue()*360);
				xRotationLabel.setText(xRotationPrefix + newValue);
				if(modelIsLoaded){
					SceneGameState.getInstance().getDesignNodeChild(modelIdentifier).setLocalRotation(Rotator.fromThreeAngles(newValue,
							((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).getRotationY(),
							((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).getRotationZ()));
					((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).setRotationX(newValue);
				}
			}
		});

		yRotationSlider = FengGUI.createSlider(true);
		yRotationSlider.setWidth(stepThree.getWidth()-20-xRotationLabel.getWidth());
		yRotationSlider.setXY(stepThree.getWidth()-yRotationSlider.getWidth()-5, xRotationSlider.getY()-yRotationSlider.getHeight()-(offset/4));
		yRotationSlider.setValue(0);
		yRotationSlider.addSliderMovedListener(new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(yRotationSlider.getValue()*360);
				yRotationLabel.setText(yRotationPrefix + newValue);
				if(modelIsLoaded){
					SceneGameState.getInstance().getDesignNodeChild(modelIdentifier).setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).getRotationX(),
							newValue, ((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).getRotationZ()));
					((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).setRotationY(newValue);
				}
			}
		});

		zRotationSlider = FengGUI.createSlider(true);
		zRotationSlider.setWidth(stepThree.getWidth()-20-xRotationLabel.getWidth());
		zRotationSlider.setXY(stepThree.getWidth()-zRotationSlider.getWidth()-5, yRotationSlider.getY()-zRotationSlider.getHeight()-(offset/4));
		zRotationSlider.setValue(0);
		zRotationSlider.addSliderMovedListener(new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(zRotationSlider.getValue()*360);
				zRotationLabel.setText(zRotationPrefix + newValue);
				if(modelIsLoaded){
					SceneGameState.getInstance().getDesignNodeChild(modelIdentifier).setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).getRotationX(),
							((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).getRotationY(), newValue));
					((ModeledDesign)SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier)).setRotationZ(newValue);
				}
			}
		});

		xRotationLabel.setXY(5, xRotationSlider.getY()-(xRotationLabel.getHeight()/2));
		yRotationLabel.setXY(5, yRotationSlider.getY()-(yRotationLabel.getHeight()/2));
		zRotationLabel.setXY(5, zRotationSlider.getY()-(zRotationLabel.getHeight()/2));

		stepThree.addWidget(tweakIt, north, south, east, west, up, down,
				moveSpeedLabel, moveSpeedSlider, rotationLabel,
				xRotationLabel, xRotationSlider, yRotationLabel, yRotationSlider, zRotationLabel, zRotationSlider);
	}

	private void createStepFour(){
		stepFour = FengGUI.createWidget(Container.class);
		stepFour.setLayoutManager(new StaticLayout());
		stepFour.setSize(targetWidth, stepFourWindowHeight-50);
		stepFour.setXY(0, 20);

		int offset=20;

		snapIt = FengGUI.createWidget(Label.class);
		snapIt.setText("SNAP IT!");
		snapIt.setXY(FengUtils.midWidth(stepFour, snapIt), stepFour.getHeight()-offset);

		snapItAdvisor = FengGUI.createWidget(Label.class);
		snapItAdvisor.setMultiline(true);
		snapItAdvisor.setText("[Move this panel to frame a view,\n" +
		"save it as the proposal menu icon]");
		applyAdvisorAppearance(snapItAdvisor);
		snapItAdvisor.setXY(FengUtils.midWidth(stepFour, snapItAdvisor), snapIt.getY()-snapItAdvisor.getHeight()-offset);

		shoot = FengGUI.createWidget(FixedButton.class);
		shoot.setText("shoot");
		shoot.setWidth(shoot.getWidth()+10);
		shoot.setXY(FengUtils.midWidth(stepFour, shoot), snapItAdvisor.getY()-shoot.getHeight()-offset);
		shoot.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				// Just return if there is no model loaded && registered in the window
				if(modelIdentifier==null) return;
				SoundGameState.getInstance().playSound(SOUNDS.CAMERA, SceneGameState.getInstance().getCamera().getLocation());
				try {
					imageFile = new File(new URL(SettingsPreferences.getDataFolder()+"local/"+new String(modelIdentifier.substring(0, modelIdentifier.indexOf("$"))).replaceAll(" ", "")+".png").toURI());
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				final int startingX = photoFrame.getX()+5;
				final int startingY = photoFrame.getY()+5;
				final int width =300;
				final int height = 200;
				final ByteBuffer buff = BufferUtils.createByteBuffer(width * height * 3);

				DisplaySystem.getDisplaySystem().getRenderer().grabScreenContents(buff, Image.Format.RGB8, startingX, startingY, width, height);


				SettingsPreferences.getGUIThreadPool().submit(new Runnable(){
					public void run() {
						BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

						// Grab each pixel information and set it to the BufferedImage info.
						for (int x = 0; x < width; x++) {
							for (int y = 0; y < height; y++) {

								int index = 3 * ((height- y - 1) * width + x);
								int argb = (((int) (buff.get(index+0)) & 0xFF) << 16) //r
								| (((int) (buff.get(index+1)) & 0xFF) << 8)  //g
								| (((int) (buff.get(index+2)) & 0xFF));      //b

								img.setRGB(x, y, argb);
							}
						}
						// write out the screenshot image to a file.
						try {
							logger.info("Writing screenshot to: " + imageFile.toString());
							ImageIO.write(img, "png", imageFile);
							atLeastOnePictureTaken=true;
						} catch (IOException e) {
							logger.warn("Screenshot " + imageFile.getName() + " could not be written");
						}
					}
				});
			}
		});

		save = FengGUI.createWidget(FixedButton.class);
		save.setText("save");
		save.setWidth(shoot.getWidth());
		save.setXY(FengUtils.midWidth(stepFour, save), shoot.getY()-save.getHeight()-(offset/4));
		save.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				pictureTakenAndAccepted=true;
			}
		});
		photoFrame = FengGUI.createWidget(Label.class);
		Pixmap bracketedFrame;
		try {
			bracketedFrame = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/screenshot/bracketed_frame.png"));
			photoFrame.setPixmap(bracketedFrame);
		} catch (IOException e) {
			logger.warn("Bracketed photo frame could not be found", e);
		}


		stepFour.addWidget(snapIt, snapItAdvisor, shoot, save);
	}

	private void createStepFive(){
		stepFive = FengGUI.createWidget(Container.class);
		stepFive.setLayoutManager(new StaticLayout());
		stepFive.setSize(targetWidth-10, targetHeight-50);
		stepFive.setXY(5, 20);

		int offset = 20;

		shareIt = FengGUI.createWidget(Label.class);
		shareIt.setText("SHARE IT!");
		shareIt.setXY((stepFive.getWidth()/2)-(shareIt.getWidth()/2), stepFive.getHeight()-offset);

		permissionsSelection = new ToggableGroup<RadioButton<String>>();

		permissionsClosed = FengGUI.<String>createRadioButton();
		permissionsClosed.setText("editable by creator only");
		permissionsClosed.setValue("CLOSED");
		permissionsClosed.setRadioButtonGroup(permissionsSelection);
		//permissionsClosed.setLayoutData(layoutData);
		permissionsClosed.setXY((stepFive.getWidth()/2)-25, shareIt.getY()-permissionsClosed.getHeight()-offset);

		permissionsOpen = FengGUI.<String>createRadioButton();
		permissionsOpen.setText("editable by anyone");
		permissionsOpen.setValue("OPEN");
		permissionsOpen.setRadioButtonGroup(permissionsSelection);
		//permissionsOpen.setLayoutData(layoutData);
		permissionsOpen.setXY((stepFive.getWidth()/2)-25, permissionsClosed.getY()-permissionsOpen.getHeight()-(offset/4));

		permissionsGroup = FengGUI.<String>createRadioButton();
		permissionsGroup.setText("editable by these users:");
		permissionsGroup.setValue("GROUP");
		permissionsGroup.setRadioButtonGroup(permissionsSelection);
		//permissionsGroup.setLayoutData(layoutData);
		permissionsGroup.setXY((stepFive.getWidth()/2)-25, permissionsOpen.getY()-permissionsGroup.getHeight()-(offset/4));

		permissionsClosed.addActivationListener(new IActivationListener(){
			public void activationChanged(Object source,
					ActivationEvent activationEvent) {
				logger.debug("closed selected");
				groupList.setEnabled(false);
			}});

		permissionsOpen.addActivationListener(new IActivationListener(){
			public void activationChanged(Object source,
					ActivationEvent activationEvent) {
				logger.debug("open selected");
				groupList.setEnabled(false);
			}});

		permissionsGroup.addActivationListener(new IActivationListener(){
			public void activationChanged(Object source,
					ActivationEvent activationEvent) {
				logger.debug("group selected");
				groupList.setEnabled(true);
			}});

		permissionsCombo = FengGUI.createWidget(ComboBox.class);
		permissionsCombo.addItem("editable by creator only");
		permissionsCombo.addItem("editable by anyone");
		permissionsCombo.addItem("editable by these users:");
		permissionsCombo.setWidth(stepFive.getWidth()-50);
		permissionsCombo.setXY(FengUtils.midWidth(stepFive, permissionsCombo), shareIt.getY()-permissionsCombo.getHeight()-offset);
		permissionsCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(Object sender,
					SelectionChangedEvent selectionChangedEvent) {
				if(permissionsCombo.getSelectedValue().equals("editable by these users:")){
					groupList.setEnabled(true);
				}
				else{
					groupList.setEnabled(false);
					groupList.setText(defaultGroupListText);
				}
			}
		});

		groupList = FengGUI.createWidget(TextEditor.class);
		groupList.getAppearance().add(TextEditor.STATE_DISABLED.toString(), new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
		groupList.setText(defaultGroupListText);
		groupList.setMultiline(true);
		groupList.setWordWarping(true);
		groupList.setEnabled(false);
		groupList.setWidth(stepFive.getWidth()-50);
		groupList.setXY(((stepFive.getWidth()/2)-groupList.getWidth()/2), permissionsGroup.getY()-groupList.getHeight()-(offset*4));

		upload = FengGUI.createWidget(FixedButton.class);
		upload.setText("Publish");
		upload.setWidth(upload.getWidth()+10);
		//upload.setXY((stepFive.getWidth()/2)-(upload.getWidth()/2), groupList.getY()-upload.getHeight()-offset);
		upload.setXY(FengUtils.midWidth(stepFive, upload), groupList.getY()-upload.getHeight()-offset);
		upload.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				SettingsPreferences.getThreadPool().submit(new Runnable(){
					public void run() {

						// handle permissions
						ProposalPermission permission=null;
						if(stepOneSelection.equals(Classification.PROPOSAL)){
							if(permissionsCombo.getSelectedValue().equals("editable by these users:")){
								if(!verifyGroupNames()){
									return;
								}
								else{
									ArrayList<String> names = getGroupNamesList(FengUtils.getText(groupList));
									if(names!=null){
										logger.info("Creating the Proposal Permission");
										permission = new ProposalPermission(Type.GROUP, names);
										logger.info("Group permissions created");
									}
								}
							}
							else if (permissionsCombo.getSelectedValue().equals("editable by anyone")){
								permission = new ProposalPermission(Type.ALL, null);
							}
							else{
								permission = new ProposalPermission(Type.CLOSED, null);
							}


							if(permission!=null) logger.info("Permissions created");
							else logger.error("Problem creating permissions!");
						}


						// Get the imported model's data
						Design design = SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier);
						try {

							// Check if the user has supplied their credentials yet
							if(!SettingsPreferences.isAuthenticated()){
								// TODO flash login window
							}

							// open connection and send design
							SecureClientManager manager = NetPool.getPool().getSecureConnection();
							final Window progressWindow = FengGUI.createWidget(Window.class);
							progressWindow.setTitle("Progress");
							final Label progressLabel = FengGUI.createWidget(Label.class);
							progressLabel.setText("Progress: N/A");
							progressWindow.getContentContainer().addWidget(progressLabel);
							manager.getProgressOutputStream().setListener(new ProgressOutputListener() {
								
								@Override
								public void writeProgressUpdate(int bytesWritten) {
									// remove the window if the counter has been reset to zero
									if(bytesWritten==0){
										if(progressWindow.getParent()!=null){
											((Container)progressWindow.getParent()).removeWidget(progressWindow);
										}
									}
									
									String updateString = "";
									if(bytesWritten<1000){
										updateString = bytesWritten+" bytes";
									}
									if(bytesWritten<1000000){
										updateString = (bytesWritten/1000)+"KB";
									}
									else{
										int numberMB = bytesWritten/1000000;
										int leftoverKB = bytesWritten%1000000;
										updateString = numberMB+"."+leftoverKB+"MB";
									}
									progressLabel.setText("Progress: "+updateString);
								}
							});
							StaticLayout.center(progressWindow, GUIGameState.getInstance().getDisp());
							GUIGameState.getInstance().getDisp().addWidget(progressWindow);
							
							int response=-4;
							if(stepOneSelection.equals(Classification.BASE)){
								
								// Attempt to get a thumbnail
								PhysicalFileTransporter thumbTransporter=packThumbnail();
								response = manager.addBase(design, SettingsPreferences.getUser(), SettingsPreferences.getPass(), GeometryUtilities.getPFT(design.getFullIdentifier()), thumbTransporter, PhysicalFileTransporter.readFromFileSystem(packedFile));
							}
							else if(stepOneSelection.equals(Classification.VERSION)){

								if(selectedDesignWhenOpened==null){
									logger.info("Versions require a proposal to be linked to");
									showSimpleError("Please re-open the window with a proposal selected.");
									return;
								}

								int rootProposalID=0;

								if(selectedDesignWhenOpened.isProposal()) rootProposalID = selectedDesignWhenOpened.getID();
								else if(selectedDesignWhenOpened.isVersion()) rootProposalID = selectedDesignWhenOpened.getSourceID();
								else{
									logger.info("Proposals can not be added to a base design");
									showSimpleError("Proposals cannot be added to a base design");
									return;
								}

								design.setSourceID(rootProposalID);
								design.setDescription(FengUtils.getText(versionDescription));

								PhysicalFileTransporter thumbTransporter=null;
								if(atLeastOnePictureTaken){
									if(imageFile.isFile()){
										FileInputStream fis = new FileInputStream(imageFile.getCanonicalFile());

										// Read the contents and pack it into a PFT
										byte[] b = new byte[fis.available()];
										fis.read(b);
										fis.close();
										thumbTransporter = new PhysicalFileTransporter(b);
									}
								}
								response = manager.addVersion(design, removables, SettingsPreferences.getUser(), SettingsPreferences.getPass(), GeometryUtilities.getPFT(design.getFullIdentifier()), thumbTransporter, PhysicalFileTransporter.readFromFileSystem(packedFile));

							}
							else if(stepOneSelection.equals(Classification.PROPOSAL)){
								PhysicalFileTransporter thumbTransporter=packThumbnail();
								response = manager.addProposal(design, removables, SettingsPreferences.getUser(), SettingsPreferences.getPass(), GeometryUtilities.getPFT(design.getFullIdentifier()), thumbTransporter, PhysicalFileTransporter.readFromFileSystem(packedFile), permission);
							}
							

							// interpret responses
							if(response>0){
								SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier).setID(response);
								//SceneScape.getTargetSpatial().setName("$"+response);
								SceneGameState.getInstance().getDesignNode().getChild(modelIdentifier).setName(SceneScape.getCity().findDesignByID(response).getFullIdentifier());

								// we need to reset or clearthe target spatial here in accordance with its new name
								SceneScape.clearTargetSpatial();

								showSimpleError("Success!");
								logger.info("Added design: " + response);
								currentNewProposalWindow.close();
							}
							else if(response == -3){
								showSimpleError("Authentication Failed!");
								logger.warn("Authentication failed when uploading model");
							}
							else if (response == -2){
								showSimpleError("Unsupported Type");
								logger.warn("A currently unsupported type of design was not able to be uploaded");
							}
							else if(response == -1){
								showSimpleError("Server Error :(");
								logger.warn("Database error on the server");
							}
						} catch (FileNotFoundException e1) {
							logger.error("File could not be found", e1);
							showSimpleError("File could not be found");
						} catch (URISyntaxException e1) {
							logger.error("URI exception", e1);
						} catch (IOException e1) {
							logger.error("Error uploading file", e1);
							showSimpleError("File could not be uploaded");
						}
					}});
			}
		});

		stepFive.addWidget(shareIt, groupList, permissionsCombo, upload);
		//stepFive.addWidget(permissionsClosed, permissionsOpen, permissionsGroup);
	}
	
	/**
	 * Packs the saved thumbnail, if it was taken, into a file transporter
	 * @return A {@link PhysicalFileTransporter} containing the image data
	 * or null if there was no thumbnail saved.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private PhysicalFileTransporter packThumbnail() throws FileNotFoundException, IOException{
		if(atLeastOnePictureTaken){
			if(imageFile.isFile()){
				FileInputStream fis = new FileInputStream(imageFile.getCanonicalFile());

				// Read the contents and pack it into a PFT
				byte[] b = new byte[fis.available()];
				fis.read(b);
				fis.close();
				return new PhysicalFileTransporter(b);
			}
		}
		
		return null;
	}

	private void applyAdvisorAppearance(Label label){
		//LabelAppearance app = label.getAppearance().clone(label);
		//app.getStyle("default").getTextStyleEntry("default").setColor(Color.DARK_RED);
		//label.setAppearance(app);
	}

	private void updateStepOneSelection(){
		switch (stepOneSelection) {
		case BASE:
			proposalTitle.setEnabled(true);
			proposalDescription.setEnabled(true);
			proposalAddress.setEnabled(true);
			proposalURL.setEnabled(true);
			versionDescription.setEnabled(false);
			removeObstructionsButton.setText("base model");
			removeObstructionsButton.setEnabled(false);
			break;
		case PROPOSAL:
			proposalTitle.setEnabled(true);
			proposalDescription.setEnabled(true);
			proposalAddress.setEnabled(true);
			proposalURL.setEnabled(true);
			versionDescription.setEnabled(false);
			removeObstructionsButton.setText("remove obstructions");
			removeObstructionsButton.setEnabled(true);
			break;
		case VERSION:
			proposalTitle.setEnabled(false);
			proposalDescription.setEnabled(false);
			proposalAddress.setEnabled(false);
			proposalURL.setEnabled(false);
			versionDescription.setEnabled(true);
			removeObstructionsButton.setText("remove obstructions");
			removeObstructionsButton.setEnabled(true);
			break;
		}
	}

	private void switchTo(int step, boolean forceMove){

		if(lockAtCurrentStep){
			showSimpleError("Close any dialogs first");
			return;
		}

		selectedStep=step;
		// if the current step is complete, change the icons and move to a new step
		if(isCurrentStepComplete(forceMove) || forceMove){

			logger.debug("Removing Step " + currentStep);
			removeStepFromDisplay(currentStep);
			if(currentStep==4){
				switchToNormalSize();
			}

			switch (step) {
			case 1:
				if(back.isInWidgetTree()){
					selectionContainer.removeWidget(back);
				}
				if(!next.isInWidgetTree()){
					selectionContainer.addWidget(next);
				}
				getContentContainer().addWidget(stepOne);
				statusOne.setPixmap(yellow);

				// select the correct button to disable
				switch (stepOneSelection) {
				case BASE:
					newBase.setEnabled(false);
					break;
				case PROPOSAL:
					newProposal.setEnabled(false);
					break;
				case VERSION:
					newVersion.setEnabled(false);
					break;
				}
				break;
			case 2:
				if(!back.isInWidgetTree()){
					selectionContainer.addWidget(back);
				}
				getContentContainer().addWidget(stepTwo);
				statusTwo.setPixmap(yellow);
				break;
			case 3:
				getContentContainer().addWidget(stepThree);

				// set up according to rotation (if available)
				if(designCreatedInThisWindow==null){
					logger.info("Using zeroed rotations");
					xRotationLabel.setText(xRotationPrefix+"0");
					yRotationLabel.setText(yRotationPrefix+"0");
					zRotationLabel.setText(zRotationPrefix+"0");

					xRotationSlider.setValue(0);
					yRotationSlider.setValue(0);
					zRotationSlider.setValue(0);
				}
				else{
					logger.info("Using supplied rotations: "+designCreatedInThisWindow.getRotationX()+","+designCreatedInThisWindow.getRotationY()+","+designCreatedInThisWindow.getRotationZ());
					xRotationLabel.setText(xRotationPrefix+designCreatedInThisWindow.getRotationX());
					yRotationLabel.setText(yRotationPrefix+designCreatedInThisWindow.getRotationY());
					zRotationLabel.setText(zRotationPrefix+designCreatedInThisWindow.getRotationZ());

					xRotationSlider.setValue((1f/360f)*designCreatedInThisWindow.getRotationX());
					yRotationSlider.setValue((1f/360f)*designCreatedInThisWindow.getRotationY());
					zRotationSlider.setValue((1f/360f)*designCreatedInThisWindow.getRotationZ());
				}
				
				statusThree.setPixmap(yellow);
				break;
			case 4:
				if(!next.isInWidgetTree()){
					selectionContainer.addWidget(next);
				}
				switchToStepFourSize();
				getContentContainer().addWidget(stepFour);
				photoFrame.setXY(getX()-5, getY()-photoFrame.getHeight());
				GUIGameState.getInstance().getDisp().addWidget(photoFrame);
				photoFrameLive=true;
				statusFour.setPixmap(yellow);
				break;
			case 5:
				if(next.isInWidgetTree()){
					selectionContainer.removeWidget(next);
				}
				getContentContainer().addWidget(stepFive);
				statusFive.setPixmap(yellow);
				break;
			}

			// change the current step
			currentStep=step;
		}
		else{
			// If the current step is not complete, flash a dialogbox before going forward
		}
	}

	private void removeStepFromDisplay(int step){
		switch (step) {
		case 1:
			getContentContainer().removeWidget(stepOne);
			break;
		case 2:
			getContentContainer().removeWidget(stepTwo);
			break;
		case 3:
			getContentContainer().removeWidget(stepThree);
			break;
		case 4:
			getContentContainer().removeWidget(stepFour);
			GUIGameState.getInstance().getDisp().removeWidget(photoFrame);
			photoFrameLive=false;
			break;
		case 5:
			getContentContainer().removeWidget(stepFive);
			break;
		}
	}

	/**
	 * Finishes a few odds and ends of setup that need to be completed
	 * after a window's instantiation.
	 */
	public void finishSetup(){
		//getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
		setTitle("Got a proposal, "+SettingsPreferences.getUser()+"?");
		setSize(targetWidth, targetHeight);
	}

	private void createErrorWindow(){
		errorWindow = FengGUI.createWindow(false, false);
		errorWindow.getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
		errorWindow.getContentContainer().setLayoutManager(new StaticLayout());
		errorWindow.setTitle("oops!");

		errorDismiss = FengGUI.createWidget(FixedButton.class);
		errorDismiss.setText("OK");
		errorDismiss.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				GUIGameState.getInstance().getDisp().removeWidget(errorWindow);
				setEnabled(true);
				setAllButtonsEnabled(true);
				setSwitcherStatus(true);
				applyColorToStep(currentStep, yellow);
			}
		});

		errorMoveOn = FengGUI.createWidget(FixedButton.class);
		errorMoveOn.setText("Move On");
		errorMoveOn.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				GUIGameState.getInstance().getDisp().removeWidget(errorWindow);
				setEnabled(true);
				setAllButtonsEnabled(true);
				setStepOneButtonsCorrectly();
				setSwitcherStatus(true);
				applyColorToStep(currentStep, red);
				switchTo(selectedStep, true);
			}
		});

		errorMoveOn.setWidth(errorMoveOn.getWidth());
		errorDismiss.setWidth(errorMoveOn.getWidth());

		errorWindow.setSize(errorMoveOn.getWidth()+errorDismiss.getWidth()+20, errorMoveOn.getHeight()+20);
		errorWindow.getContentContainer().addWidget(errorDismiss, errorMoveOn);
	}

	private void createSimpleErrorWindow(){
		simpleErrorWindow = FengGUI.createWindow(false, false);
		simpleErrorWindow.getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
		simpleErrorWindow.getContentContainer().setLayoutManager(new StaticLayout());
		simpleErrorWindow.setTitle("oops!");
		simpleErrorWindow.setSize(95, 50);

		simpleErrorOK = FengGUI.createWidget(FixedButton.class);
		simpleErrorOK.setText("OK!");
		simpleErrorOK.setWidth(simpleErrorOK.getWidth()+10);
		simpleErrorOK.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e){
				if(simpleErrorWindow.isInWidgetTree()){
					GUIGameState.getInstance().getDisp().removeWidget(simpleErrorWindow);
					setEnabled(true);
					setAllButtonsEnabled(true);
					setStepOneButtonsCorrectly();
					setSwitcherStatus(true);
				}
			}
		});

		simpleErrorWindow.addWidget(simpleErrorOK);
	}

	private void createProposalOrBaseWindow(){
		baseModelOption = FengGUI.createWindow(false, false);
		baseModelOption.getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
		baseModelOption.getContentContainer().setLayoutManager(new StaticLayout());
		baseModelOption.setTitle("Base Model or Proposal?");
		baseModelOption.setSize(95, 50);                                                


		proposalDesignButton = FengGUI.createWidget(FixedButton.class);
		proposalDesignButton.setText("Proposal");
		proposalDesignButton.setWidth(proposalDesignButton.getWidth()+10);
		proposalDesignButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				stepOneSelection = Classification.PROPOSAL;
				GUIGameState.getInstance().getDisp().removeWidget(baseModelOption);
			}
		});

		baseDesignButton = FengGUI.createWidget(FixedButton.class);
		baseDesignButton.setText("Base");
		baseDesignButton.setWidth(proposalDesignButton.getWidth());
		baseDesignButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				stepOneSelection = Classification.BASE;
				GUIGameState.getInstance().getDisp().removeWidget(baseModelOption);
			}
		});

	}

	private void showSimpleError(String error){
		// Turn off the back and next buttons
		setSwitcherStatus(false);

		// Turn off other window based functionalities
		setEnabled(false);
		setAllButtonsEnabled(false);

		// Update the error window and add it to the scene

		simpleErrorWindow.setTitle(error);
		simpleErrorWindow.layout();
		simpleErrorWindow.setXY(getX()+(getWidth()/2)-(simpleErrorWindow.getWidth()/2), getY()+(getHeight()/2)-(simpleErrorWindow.getHeight()/2));

		GUIGameState.getInstance().getDisp().addWidget(simpleErrorWindow);

		simpleErrorOK.setXY(FengUtils.midWidth(simpleErrorWindow, simpleErrorOK), 5);
	}

	private void showError(String error){
		// Turn off he back and next buttons
		setSwitcherStatus(false);

		// Turn off other window based functionalities
		setEnabled(false);
		setAllButtonsEnabled(false);

		// Update the error window and add it to the scene

		errorWindow.setTitle(error);
		errorWindow.layout();
		errorWindow.setXY(getX()+(getWidth()/2)-(errorWindow.getWidth()/2), getY()+(getHeight()/2)-(errorWindow.getHeight()/2));

		GUIGameState.getInstance().getDisp().addWidget(errorWindow);

		errorDismiss.setXY(4, 4);
		errorMoveOn.setXY(errorWindow.getWidth()-errorMoveOn.getWidth()-4, 4);
	}

	/**
	 * @experimental
	 * @param enabled Enable or disable these windows
	 */
	private void setAllButtonsEnabled(boolean enabled){
		switch (currentStep) {
		case 1:
			setAllButtonsEnabledImpl(stepOne, enabled);
			setStepOneButtonsCorrectly();
			break;
		case 2:
			setAllButtonsEnabledImpl(stepTwo, enabled);
			break;
		case 3:
			setAllButtonsEnabledImpl(stepThree, enabled);
			break;
		case 4:
			setAllButtonsEnabledImpl(stepFour, enabled);
			break;
		case 5:
			setAllButtonsEnabledImpl(stepFive, enabled);
			break;
		}
	}

	private void setAllButtonsEnabledImpl(IWidget w, boolean enabled){
		if(w instanceof Container){
			for(IWidget child : ((Container)w).getWidgets()){
				setAllButtonsEnabledImpl(child, enabled);
			}
		}
		else if(w instanceof Button){
			w.setEnabled(enabled);
		}
	}

	/**
	 * Sets the proposal/base/version buttons in step one
	 * to their correct states of enabled/disabled
	 */
	private void setStepOneButtonsCorrectly(){
		switch (stepOneSelection) {
		case VERSION:
			newVersion.setEnabled(false);
			newProposal.setEnabled(true);
			newBase.setEnabled(true);
			break;
		case PROPOSAL:
			newVersion.setEnabled(true);
			newProposal.setEnabled(false);
			newBase.setEnabled(true);
			break;
		case BASE:
			newVersion.setEnabled(true);
			newProposal.setEnabled(true);
			newBase.setEnabled(false);
			break;
		}
	}

	private void setSwitcherStatus(boolean status){
		back.setEnabled(status);
		next.setEnabled(status);
	}

	private void applyColorToStep(int step, Pixmap color){
		switch (step) {
		case 1:
			statusOne.setPixmap(color);
			break;
		case 2:
			statusTwo.setPixmap(color);
			break;
		case 3:
			statusThree.setPixmap(color);
			break;
		case 4:
			statusFour.setPixmap(color);
			break;
		case 5:
			statusFive.setPixmap(color);
			break;
		}
	}

	private void switchToStepFourSize(){
		setSize(targetWidth, stepFourWindowHeight);
		layout();
		setY(getY()+200);
	}

	private void switchToNormalSize(){
		setSize(targetWidth, targetHeight);
		setY(getY()-200);
		layout();
	}

	public int getCurrentStep(){
		return currentStep;
	}

	public void close(){
		if(photoFrameLive){
			GUIGameState.getInstance().getDisp().removeWidget(photoFrame);
			photoFrameLive=false;
		}
		super.close();
	}

	private UTMCoordinate createCoordinate() throws NumberFormatException{
		try{
			int latDegVal = FengUtils.getNumber(latDeg);
			int latMinVal = FengUtils.getNumber(latMin);
			float latSecVal = FengUtils.getFloat(latSec);
			int lonDegVal = FengUtils.getNumber(lonDeg);
			int lonMinVal = FengUtils.getNumber(lonMin);
			float lonSecVal = FengUtils.getFloat(lonSec);

			return new GPSCoordinate(0, latDegVal, latMinVal, latSecVal, lonDegVal, lonMinVal, lonSecVal).getUTM();
		} catch (FengTextContentException e) {
			logger.error("TextEditors not set correctly", e);
			return null;
		}
	}

	public void setProposalLocation(UTMCoordinate utm){
		GPSCoordinate gps = utm.getGPS();
		float[] lat = DecimalDegreeConverter.ddToDMS(gps.getLatitude());
		float[] lon = DecimalDegreeConverter.ddToDMS(gps.getLongitude());
		latDeg.setText(""+lat[0]);
		latMin.setText(""+lat[1]);
		latSec.setText(""+lat[2]);
		lonDeg.setText(""+lon[0]);
		lonMin.setText(""+lon[1]);
		lonSec.setText(""+lon[2]);
		locationIsSet=true;
	}

	/**
	 * Determines whether all five steps of the creation
	 * process are complete.
	 * @return true if the process is complete; false otherwise.
	 */
	public boolean allStepsComplete(){
		try {
			if(isStepComplete(1) &&
					isStepComplete(2) &&
					isStepComplete(3) &&
					isStepComplete(4) &&
					isStepComplete(5)){
				return true;
			}
			else return false;
		} catch (Exception e) {
			// This shouldn't happen here since we are only checking the
			// steps contained herein.
			logger.error("An invalid step has been checked for completion" + e);
			return false;
		}
	}

	private boolean isCurrentStepComplete(boolean moveForced){
		switch (currentStep) {
		case 1:
			if(stepOneSelection.equals(Classification.VERSION)){
				if(!titleChanged || !descriptionChanged){
					if(!moveForced)showError("Finish Step One First!");
					return false;
				}
				else{
					statusOne.setPixmap(green);
					logger.debug("Step One Complete");
					return true;
				}
			}
			else{
				if(!versionDescriptionChanged){
					if(!moveForced)showError("Finish Step One First!");
					return false;
				}
				else{
					statusOne.setPixmap(green);
					logger.debug("Step One Complete");
					return true;
				}
			}
		case 2:
			if(!modelIsLoaded || !locationIsSet){
				if(!moveForced)showError("Finish Step Two First!");
				return false;
			}
			statusTwo.setPixmap(green);
			logger.debug("Step Two Complete");
			return true;
		case 3:
			return true;
			/*
			if(!positionTweaked){
				if(!moveForced)showError("Finish Step Three First!");
				return false;
			}
			else{
				statusThree.setPixmap(green);
				logger.debug("Step Three Complete");
				return true;
			}
			 */
		case 4:
			if(!pictureTakenAndAccepted){
				if(!moveForced)showError("Finish Step Four First!");
				return false;
			}
			else{
				statusFour.setPixmap(green);
				logger.debug("Step Four Complete");
				return true;
			}
		case 5:
			if(groupSelected && !groupEntered){
				if(!moveForced)showError("Finish Step Five First!");
				return false;
			}
			else{
				statusFive.setPixmap(green);
				logger.debug("Step Five Complete");
				return true;
			}
		}
		return false;
	}

	private boolean isStepComplete(int step){
		switch (step) {
		case 1:
			if(statusOne.getPixmap()==green) return true;
			else return false;
		case 2:
			if(statusTwo.getPixmap()==green) return true;
			else return false;
		case 3:
			if(statusThree.getPixmap()==green) return true;
			else return false;
		case 4:
			if(statusFour.getPixmap()==green) return true;
			else return false;
		case 5:
			if(statusFive.getPixmap()==green) return true;
			else return false;
		}

		return false;
	}

	/**
	 * If a design is loaded, it is updated based on the current
	 * selection in step one.
	 */
	private void applyDesignType(){
		// a null identifier means that there is not
		// yet a design to modify
		if(modelIdentifier==null)
			return;

		Design design = SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier);
		design.setClassification(stepOneSelection);

		if(stepOneSelection.equals(Classification.BASE)){
			removeObstructionsButton.setText("Nothing to Remove");
			removeObstructionsButton.setEnabled(false);
		}
		else{
			removeObstructionsButton.setEnabled(true);
			removeObstructionsButton.setText("make room for " + stepOneSelection.toString().toLowerCase());
		}

		// Base and Proposal share the same fields, so the method of data application is the same.
		if(stepOneSelection.equals(Classification.BASE) || stepOneSelection.equals(Classification.PROPOSAL)){
			design.setName(FengUtils.getText(proposalTitle));
			if(descriptionChanged) design.setDescription(FengUtils.getText(proposalDescription));
			if(addressChanged) design.setAddress(FengUtils.getText(proposalAddress));
			if(urlChanged) design.setURL(FengUtils.getText(proposalURL));
		}
		else if(stepOneSelection.equals(Classification.VERSION)){
			if(versionDescriptionChanged) design.setDescription(FengUtils.getText(versionDescription));
		}
	}

	private void placeMakeRoomWindow(){
		makeRoomWindow.setXY(removeObstructionsButton.getDisplayX()+removeObstructionsButton.getWidth(), removeObstructionsButton.getDisplayY()+removeObstructionsButton.getHeight()-makeRoomWindow.getHeight());
		GUIGameState.getInstance().getDisp().addWidget(makeRoomWindow);
		lockAtCurrentStep=true;
		setSwitcherStatus(false);
	}

	public boolean isMakeRoomWindowActive(){
		return makeRoomWindow.isInWidgetTree();
	}

	/**
	 * Checks on whether or not a design has been loaded into the scene
	 * @return
	 */
	public boolean isModelLoaded(){
		return modelIsLoaded;
	}

	private boolean verifyGroupNames(){
		String groupListText = FengUtils.getText(groupList);

		if(!groupListText.equals(defaultGroupListText)){

			// show progress window
			final Window progress = FengGUI.createWindow(false, false);
			progress.setTitle("Betaville");

			final Label progressLabel = FengGUI.createWidget(Label.class);
			progressLabel.setText("Verifying Usernames");
			progress.addWidget(progressLabel);

			progress.setSize(progressLabel.getWidth()+30, progressLabel.getHeight()+30);
			progress.setXY(Binding.getInstance().getCanvasWidth()/2-progress.getWidth()/2,
					Binding.getInstance().getCanvasHeight()/2-progress.getHeight()/2);
			progressLabel.setXY(15, 15);

			GUIGameState.getInstance().getDisp().addWidget(progress);
			verifyingNames=true;

			SettingsPreferences.getThreadPool().submit(new Runnable() {

				@SuppressWarnings("static-access")
				public void run() {
					while(verifyingNames){
						try {
							// display three dots and then start the sequence over
							if(progressLabel.getText().endsWith("...")){
								progressLabel.setText(new String(progressLabel.getText().substring(0, progressLabel.getText().length()-3)));
							}
							else{
								progressLabel.setText(progressLabel.getText()+".");
							}
							Thread.currentThread().sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					GUIGameState.getInstance().getDisp().removeWidget(progress);
				}
			});

			// separate and validate names
			ArrayList<String> nameList = getGroupNamesList(groupListText);
			if(nameList==null) return false;



			// validate all of the names
			for(String s : nameList){
				if(!StringVerifier.isValidUsername(s)){
					showSimpleError(s + "is not a valid name!");
					verifyingNames=false;
					return false;
				}
			}

			// check on server
			for(String user : nameList){
				logger.info("Checking user: " + user);
				if(NetPool.getPool().getConnection().checkNameAvailability(user)){
					showSimpleError("User " + user + " does not exist!");
					verifyingNames=false;
					return false;
				}
			}

			logger.info("All users seem to be valid");

			verifyingNames=false;
			return true;
		}

		showSimpleError("Please Enter Names!");
		return false;
	}

	private ArrayList<String> getGroupNamesList(String groupListText){
		// The only valid characters are those in a username, commas, and spaces
		if(!groupListText.matches("[A-Za-z0-9 ,]+")){
			showSimpleError("Names Poorly Formatted");
			verifyingNames=false;
			return null;
		}

		ArrayList<String> nameList = new ArrayList<String>();

		if(groupListText.contains(",")){
			String[] splitNames = groupListText.split(" *, *");
			for(String s : splitNames){
				nameList.add(s);
			}
		}
		else{
			nameList.add(groupListText);
		}
		logger.info("name list contains " + nameList.size() + " entries");
		return nameList;
	}

	public void resetForNewProposal(){
		switchTo(1, true);

		// reset state variables
		currentStep=1;
		selectedStep=0;
		lockAtCurrentStep=false;

		selectedDesignWhenOpened=null;
		stepOneSelection = Classification.PROPOSAL;
		setStepOneButtonsCorrectly();	

		titleChanged = false;            
		descriptionChanged = false;      
		addressChanged = false;          
		urlChanged = false;              
		versionDescriptionChanged=false;

		mediaURL = null;

		locationIsSet=false;
		modelIsLoaded=false;

		maxMoveSpeed=50;
		moveSpeed=1;

		photoFrameLive=false;         

		atLeastOnePictureTaken=false; 
		pictureTakenAndAccepted=false;

		groupSelected=false;
		groupEntered=false;

		// reset switcher status statusOne.setPixmap(yellow);
		statusTwo.setPixmap(red);
		statusThree.setPixmap(red);
		statusFour.setPixmap(red);
		statusFive.setPixmap(red);

		// reset functionality items
		proposalTitle.setText(proposalTitleDefaultContent);
		proposalDescription.setText(proposalDescriptionDefaultContent);
		proposalAddress.setText(proposalAddressDefaultContent);
		proposalURL.setText(proposalURLDefaultContent);
		versionDescription.setText(versionDescriptionDefault);

		//		latDeg.setText(Double.toString(latDegX));
		//		latMin.setText(Double.toString(latMinX));
		//		latSec.setText(Double.toString(latSecX));
		//		lonDeg.setText(Double.toString(lonDegX));
		//		lonMin.setText(Double.toString(lonMinX));
		//		lonSec.setText(Double.toString(lonSecX));

		latDeg.setText(latDegDefaultContent);
		latMin.setText(latMinDefaultContent);
		latSec.setText(latSecDefaultContent);
		lonDeg.setText(lonDegDefaultContent);
		lonMin.setText(lonMinDefaultContent);
		lonSec.setText(lonSecDefaultContent);

		// set the coordinate to null so we don't use the old one.
		coordinate= null;

		mediaPath.setText("Location of Media");

		xRotationLabel.setText(xRotationPrefix+"0");
		xRotationSlider.setValue(0);
		yRotationLabel.setText(yRotationPrefix+"0");
		yRotationSlider.setValue(0);
		zRotationLabel.setText(zRotationPrefix+"0");
		zRotationSlider.setValue(0);

		groupList.setText(defaultGroupListText);
	}

	public void preload(Design selectedDesign){
		selectedDesignWhenOpened=selectedDesign;
		if(selectedDesignWhenOpened==null) newVersion.setEnabled(false);
	}
}
