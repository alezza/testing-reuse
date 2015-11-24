/*******************************************************************************
 * Copyright (c) 2015 University of Luxembourg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Benoît Ries- initial implementation
 ******************************************************************************/
package lu.uni.lassy.excalibur.tests.swtbot.excalibur;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.sirius.diagram.DNode;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import junit.framework.AssertionFailedError;
import lu.uni.lassy.excalibur.common.utils.OSUtils;
import lu.uni.lassy.excalibur.dsl.messir.ui.utils.DialogUseCaseInstanceCreation;
import lu.uni.lassy.excalibur.tests.swtbot.utils.SWTBotSupport;
import lu.uni.lassy.excalibur.tests.swtbot.utils.UIStrings;
import lu.uni.lassy.excalibur.ui.outline.views.ExcaliburSpecificationOutlineView;
import lu.uni.lassy.excalibur.ui.outline.views.providers.label.ExcaliburOutlineLabelProvider;
import lu.uni.lassy.excalibur.ui.outline.wizards.newprojects.NewSpecProjectWizard;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExcaliburGettingStartedLong extends SWTBotTestCase {
	
	private final static Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
	private final static SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
	private final static SWTGefBot gefBot = new SWTGefBot();
	
	//test project selection
	public static String projectName = "lu.uni.lassy.excalibur.myhelloworld";

	@BeforeClass //Initialization
	public static void beforeClass() {
		//close SICStus Prolog Path Not Configured
		ExcaliburStartup.closeSICStusNotConfigured();

		//close What's New dialog
		ExcaliburStartup.closeWhatsNewDialogView();

		//close Welcome startup page
		ExcaliburStartup.closeStartView(workbenchBot);

		//set preferences
		if (OSUtils.isMac()) {
			ExcaliburStartup.setPreferenceMac(workbenchBot);
			ExcaliburStartup.setTexlipseMac(workbenchBot);
		} else if (OSUtils.isWindows()) {
			ExcaliburStartup.setPreferenceWin(workbenchBot);
			ExcaliburStartup.setTexlipseWin(workbenchBot);
		} else if (OSUtils.isUnix()) {
			ExcaliburStartup.setPreferenceLinux(workbenchBot);
			ExcaliburStartup.setTexlipseLinux(workbenchBot);
		}

		try {
			SWTBotSupport.waitForWindowDialogAndClose("Oomph Preference Recorder");
		} catch (Exception e) {
			System.out.println("Oomph preference recorder didn't appear"); 
		}

		//set preferences not needed for windows
		//because defaults are OK
		//		ExcaliburStartup.setPreferenceWin(workbenchBot);
		//ExcaliburStartup.setTexlipseWin(workbenchBot);
	}

	@Before //performed before each test
	public void cleanWorkspace() {
		System.out.println("*** Clean workspace before executing test step ***");
		SWTBotSupport.saveAll();
		SWTBotSupport.closeAll();
		SWTBotSupport.waitForWindowDialogAndClose("Save", "Yes");
		SWTBotSupport.waitForEndOfRunningJobs();
		System.out.println("*** End of cleaning workspace ***\n");
	}

	@After
	public void collapseAll() {
		SWTBotSupport.collapseAllInOutlineView(projectName);
	}

	@Test
	public void GettingStartedLong_0_project_creation() throws Exception {
		//call Eclipse menu File->New->Other... then select Hello World Project
		workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_FILE)
					.menu(SWTBotSupport.ECLIPSE_MENU_FILE_NEW)
					.menu(SWTBotSupport.ECLIPSE_MENU_FILE_NEW_OTHER)
					.click();

		SWTBotShell newWizardShell = SWTBotSupport.waitForWindowDialog("New");
		SWTBotSupport.listAllWidgetsOfCurrentShell(newWizardShell.bot());
		newWizardShell.setFocus();
		SWTBotTree tree = newWizardShell.bot().tree();
		tree.setFocus();
		bot.sleep(1000);
		tree.expandNode("Excalibur", true)
			.expandNode("Projects")
			.expandNode("Specification Project")
			.select();
		
		newWizardShell.bot().button("&Next >").click();
		
		SWTBotShell newWizard = workbenchBot.shell(NewSpecProjectWizard.WIZARD_TITLE_NEW_SPECIFICATION_PROJECT);

		//wait for shell
		workbenchBot.waitUntil(shellIsActive(NewSpecProjectWizard.WIZARD_TITLE_NEW_SPECIFICATION_PROJECT), 15000);

		SWTBotText text = workbenchBot.textWithLabel("Project name:");
		text.setFocus();
		text.setText(projectName);

		//click on Finish
		newWizard.bot().button("&Finish").click();

		workbenchBot.waitUntil(shellCloses(newWizard), 480000);

		SWTBotSupport.waitForEndOfRunningJobs();

		//errors
		try {
			assertTrue(UIStrings.STD_LIBS + " was not created.", SWTBotSupport.isProjectCreated(UIStrings.STD_LIBS));
			assertTrue(projectName + " was not created.", SWTBotSupport.isProjectCreated(projectName));
			assertTrue(projectName + ".report was not created.", SWTBotSupport.isProjectCreated(projectName + ".report"));
			//assertTrue(projectName + ".simulation was not created.", SWTBotSupport.isProjectCreated(projectName + ".simulation"));
		} catch (AssertionFailedError e) {
			fail(e.getMessage());
		}

		//expand in outline
		SWTBotTree specificationOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName);
		specificationOutlineTree.expandNode(projectName, ExcaliburOutlineLabelProvider.LABEL_ALL_VIEWS_NODE);
		specificationOutlineTree.expandNode(projectName, ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE, "Concept Model", "Primary Types", "ctState");
		specificationOutlineTree.expandNode(projectName, ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE);

		captureScreenshot("tutorial_long/0.png");
	}

	@Test
	public void GettingStartedLong_1_1_a_specify_an_actor() throws Exception {
		//step 1- open related specification file by double-click on package
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.environment";
		String specificationFileThatShouldBeOpened = "environment.msr";

		SWTBotEditor activeEditor = SWTBotSupport.openMsrFileByPackageName(workbenchBot, projectName, packageToOpen, specificationFileThatShouldBeOpened);

		captureScreenshot("tutorial_long/1.1.a_1.png");

		//step 2- in the editor file, goto Environment Model and call auto-completion
		keyboard.pressShortcut(Keystrokes.UP);
		keyboard.pressShortcut(Keystrokes.UP);
		activeEditor.toTextEditor().autoCompleteProposal("", "Actor - new actor");
		activeEditor.toTextEditor().insertText("You");
		keyboard.pressShortcut(Keystrokes.LF);

		SWTBotSupport.saveActiveEditor(activeEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);

		captureScreenshot("tutorial_long/1.1.a_2.png");

		//step 3- Refresh Outline and expand related Model
		String[] strs = {projectName, ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE, packageToOpen, "Environment Model", "actYou (rnactYou)[1..*]"};
		SWTBotTreeItem item = SWTBotSupport.getNodeItem(SWTBotSupport.getExcaliburOutlineView(projectName), strs);
		assertTrue("New actor should appear in the Excalibur Outline", item.isVisible());
		
		captureScreenshot("tutorial_long/1.1.a_3.png");
	}
	
	@Test
	public void GettingStartedLong_1_2_a_specify_a_usecase() throws Exception {
		//step 1- open use-case model specification by double-click on package
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.usecases";
		String specificationFileThatShouldBeOpened = "usecases.msr";

		SWTBotEditor activeEditor = SWTBotSupport.openMsrFileByPackageName(workbenchBot, projectName, packageToOpen, specificationFileThatShouldBeOpened);
		
		//step 2- in the editor file, goto UseCase Model and call auto-completion
		String actorName = "actYou";
		String useCaseName = "oeHelloWorld";
		String importEnvironmentPackage = "lu.uni.lassy.excalibur.myhelloworld.environment";

		//type import
		activeEditor.toTextEditor().navigateTo(12, 0);
		activeEditor.toTextEditor().insertText("import " + importEnvironmentPackage+ "\n");
		
		//new use-case template
		activeEditor.toTextEditor().navigateTo(17, 0);
		activeEditor.toTextEditor().autoCompleteProposal("", "use case subfunction - new subfunction use case");
		
		//type use case name, then actorName
		activeEditor.toTextEditor().insertText(useCaseName);
		keyboard.pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().insertText(actorName);
		keyboard.pressShortcut(Keystrokes.LF);

		SWTBotSupport.saveActiveEditor(activeEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		//step 3- Refresh Outline and expand related Model
		SWTBotTreeItem item = SWTBotSupport.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
				.expandNode(packageToOpen)
				.expandNode("Use Case Model")
				.expandNode("oeHelloWorld");
				
		assertTrue("New use-case should appear in the Excalibur Outline", item.isVisible());

		captureScreenshot("tutorial_long/1.2.a.png");

	}
	
	@Test
	public void GettingStartedLong_1_2_b_create_view_usecase() throws Exception {
		//step 1- open use-case model specification by double-click on package
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.usecases";
		String useCaseName = "oeHelloWorld";
		String viewName = "uc-" + useCaseName;
		
		String[] strs = {projectName, ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE, packageToOpen, "Use Case Model", useCaseName};
		SWTBotTreeItem item = SWTBotSupport.getNodeItem(SWTBotSupport.getExcaliburOutlineView(projectName), strs);
		item.contextMenu("New...")
			.menu("Use Case View")
			.click();

		SWTBotShell analysisSelectionShell = SWTBotSupport.waitForWindowDialog("Analysis selection");
		analysisSelectionShell.bot().table().getTableItem(1).click();
		
		captureScreenshot("tutorial_long/1.2.b_1.png");
		
		analysisSelectionShell.bot().button("OK").click();
		bot.waitUntil(shellCloses(analysisSelectionShell));
		
		SWTBotSupport.waitForEndOfRunningJobs();
		
		workbenchBot.sleep(3000);
		
		captureScreenshot("tutorial_long/1.2.b_2.png");

		for (SWTBotEditor editor: workbenchBot.editors()) {
			if (editor.getTitle().endsWith(".msrd")) {
				editor.saveAndClose();
			}
			if (editor.getTitle().equals(viewName)) {
				editor.show();
				editor.save();
			}
		}
		assertTrue("View was not created got this intead ->" + workbenchBot.activeEditor().getTitle(), workbenchBot.activeEditor().getTitle().contains(viewName));
		
	}
	
	@Test
	public void GettingStartedLong_1_2_c_specify_usecase_instance() throws Exception {
		//step 1- call contextual menu New Messir File - UCI on new useCase oeHelloWorld
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.usecases";
		String useCaseName = "oeHelloWorld";
		String useCaseInstanceName = "uci" + useCaseName;
		
		String[] strs = {projectName, ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE, packageToOpen, "Use Case Model", useCaseName};
		SWTBotTreeItem item = SWTBotSupport.getNodeItem(SWTBotSupport.getExcaliburOutlineView(projectName), strs);
		item.contextMenu("New...")
			.menu("Messir File - Use Case Instance")
			.click();

		SWTBotSupport.waitForEndOfRunningJobs();
		
		//step 2- open Dialog and fill in text fields, then OK

		SWTBotShell shellNewMessirFileUCIView = workbenchBot.shell("New Messir File for a Use Case Instance");
		shellNewMessirFileUCIView.activate();
		workbenchBot.waitUntil(shellIsActive("New Messir File for a Use Case Instance"));
		
		SWTBotText textUseCaseInstanceName = shellNewMessirFileUCIView.bot().textWithLabel(DialogUseCaseInstanceCreation.DIALOG_TEXT_USE_CASE_INSTANCE_NAME);
		textUseCaseInstanceName.setText(useCaseInstanceName);
		
		captureScreenshot("tutorial_long/1.2.c_1.png");

		shellNewMessirFileUCIView.bot().button(SWTBotSupport.BUTTON_OK).click();
		
		workbenchBot.waitUntil(shellCloses(shellNewMessirFileUCIView));

		captureScreenshot("tutorial_long/1.2.c_2.png");
		for (SWTBotEditor editor: workbenchBot.editors()) {
			if (editor.getTitle().endsWith(".msrd")) {
				editor.saveAndClose();
			}
			if (editor.getTitle().equals(useCaseName)) {
				editor.show();
				editor.save();
			}
		}
		assertTrue("File was not created got this intead ->" + workbenchBot.activeEditor().getTitle(), workbenchBot.activeEditor().getTitle().contains(useCaseInstanceName));
		

		//step 3- add actors instances
		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		String importEnvironmentPackage = "lu.uni.lassy.excalibur.myhelloworld.environment";
		String actorDeclaration = "\t\t\t\tbenoitRies : actYou";
		String returnedMessage = "ieHelloWorld(\"Hello You\") returned to benoitRies\n";

		//type import
		activeEditor.toTextEditor().navigateTo(2, 0);
		activeEditor.toTextEditor().insertText("import " + importEnvironmentPackage+ "\n");

		//type Actor instance declaration
		activeEditor.toTextEditor().navigateTo(8, 0);
		activeEditor.toTextEditor().pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().insertText(actorDeclaration);
		
		//type returned message
		activeEditor.toTextEditor().navigateTo(10, 0);
		activeEditor.toTextEditor().pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().insertText(returnedMessage);

		SWTBotSupport.saveActiveEditor(activeEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		//step- Refresh Outline and expand related Model
		String uciPackageName = "usecases.ucioeHelloWorld";
		SWTBotTreeItem itemUCI = SWTBotSupport
				.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
				.expandNode(uciPackageName)
				.expandNode("Use Case Model")
				.expandNode("ucioeHelloWorld: oeHelloWorld");

		assertTrue("New messir specification element should appear in the Excalibur Outline", itemUCI.isVisible());
		
		captureScreenshot("tutorial_long/1.2.c_3.png");
	}
	
	@Test
	public void GettingStartedLong_1_2_d_create_view_usecase_instance() throws Exception {
		String packageToOpen = "usecases.ucioeHelloWorld";
		String useCaseInstanceName = "ucioeHelloWorld";
		String viewName = "uci-" + useCaseInstanceName;
		String nodeName = useCaseInstanceName + ": oeHelloWorld";
		
		String[] strs = {projectName, ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE, packageToOpen, "Use Case Model", nodeName};
		SWTBotTreeItem item = SWTBotSupport.getNodeItem(SWTBotSupport.getExcaliburOutlineView(projectName), strs);
		item.contextMenu("New...")
			.menu("Use Case Instance View")
			.click();

		SWTBotSupport.waitForEndOfRunningJobs();

		try {
			SWTBotShell analysisSelectionShell = SWTBotSupport.waitForWindowDialog("Analysis selection");
			analysisSelectionShell.bot().table().getTableItem(2).click();
			captureScreenshot("tutorial_long/1.2.d_1.png");
			analysisSelectionShell.bot().button("OK").click();
			bot.waitUntil(shellCloses(analysisSelectionShell));
			SWTBotSupport.waitForEndOfRunningJobs();
		
			workbenchBot.sleep(3000);
		} catch (Exception e) {
			//Analysis selection dialog didn't appear
		}
		
		captureScreenshot("tutorial_long/1.2.d_2.png");
		for (SWTBotEditor editor: workbenchBot.editors()) {
			if (editor.getTitle().endsWith(".msrd")) {
				editor.saveAndClose();
			}
			if (editor.getTitle().equals(viewName)) {
				editor.show();
				editor.save();
			}
		}
		assertTrue("View was not created got this intead ->" + workbenchBot.activeEditor().getTitle(), workbenchBot.activeEditor().getTitle().contains(viewName));
		
	}
/*	
	@Test
	public void GettingStartedLong_1_3_a_new_outline_view_for_report() throws Exception {
		SWTBotSupport.createNewExcaliburOutlineView(projectName, projectName + ".report");

		SWTBotSupport.getExcaliburOutlineTree(projectName + ".report").expandNode(projectName + ".report");
		captureScreenshot("tutorial_long/1.3.a.png");
	}*/

	@Test
	public void GettingStartedLong_1_3_b_generate_first_report() throws Exception {
		ExcaliburStartup.setMakeGlossariesBuilder(workbenchBot, projectName + ".report");
		SWTBotSupport.generateReport(projectName, "report_1_3_b.pdf", false);

		SWTBotSupport.getExcaliburOutlineTree(projectName + ".report").expandNode(projectName + ".report");
		captureScreenshot("tutorial_long/1.3.b.png");
	}

	@Test
	public void GettingStartedLong_1_3_c_document_a_messir_specification_element() throws Exception {
		SWTBotTreeItem undocumentedItem = SWTBotSupport.getExcaliburOutlineTree(projectName)
						.expandNode(projectName)
						.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
						.expandNode(ExcaliburOutlineLabelProvider.LABEL_UNDOCUMENTED_REPORT_NODE)
						.expandNode("actYou (rnactYou)[1..*]")
						.select();
		captureScreenshot("tutorial_long/1.3.c_1.png");
		
		String elementToDocumentName = "actYou";
		String documentationFileThatShouldBeOpened = "Actor-actYou.msrd";
		String descriptionFieldContent = "@description \"Is representing any person that would like to receive an hello world message from the system.\"\n";
		int lineForDescriptionField = 7;
		
		//open documentation file
		undocumentedItem.doubleClick();
		workbenchBot.sleep(3000);
		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("Documentation file was not opened.", activeEditor.getTitle().equals(documentationFileThatShouldBeOpened));

		//type dummy description for element documentation file
		activeEditor.toTextEditor().selectLine(lineForDescriptionField);
		bot.captureScreenshot("screenshots/document_element_"+documentationFileThatShouldBeOpened+".png");
		activeEditor.toTextEditor().insertText(descriptionFieldContent);
		activeEditor.toTextEditor().pressShortcut(Keystrokes.DELETE);
		activeEditor.save();
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		//check that element now appears in its Documentation section
		//step 2- parse documented nodes for element
		SWTBotTree outlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName);
		SWTBotTreeItem documentedActYouItem = outlineTree
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SECTION_3_ENVIRONMENT_MODEL_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_ACTORS_AND_INTERFACES_DESCRIPTIONS_REPORT_NODE);

		String nodeName = SWTBotSupport.getChildNodeNameByNameSubstring(documentedActYouItem, elementToDocumentName);
		documentedActYouItem = documentedActYouItem.getNode(nodeName);

		System.out.println("1_3_C: " + documentedActYouItem);
		assertTrue(elementToDocumentName + " was not found in documented messir specification elements", (documentedActYouItem != null));
	
		captureScreenshot("tutorial_long/1.3.c_2.png");
	}
	
	@Test
	public void GettingStartedLong_1_3_d_document_view_usecase() throws Exception {
		SWTBotTreeItem undocumentedItem = SWTBotSupport.getExcaliburOutlineTree(projectName)
			.expandNode(projectName)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_UNDOCUMENTED_REPORT_NODE);
		String nodeName = SWTBotSupport.getChildNodeNameByNameSubstring(undocumentedItem, "uc-oeHelloWorld");
		undocumentedItem = undocumentedItem.getNode(nodeName).select();
		captureScreenshot("tutorial_long/1.3.d_1.png");
		
		String viewToDocumentName = "uc-oeHelloWorld";
		String documentationFileThatShouldBeOpened = "View-uc-oeHelloWorld.msrd";
		String descriptionFieldContent = "@description \"shows the oeHelloWorld subfunction use-case and its primary actor actYou.\"\n";
		int lineForDescriptionField = 8;
		
		//open documentation file
		undocumentedItem.doubleClick();
		SWTBotSupport.waitForEndOfRunningJobs();
		captureScreenshot("tutorial_long/1.3.d_2.png");

		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("Documentation file was not opened.", activeEditor.getTitle().equals(documentationFileThatShouldBeOpened));
		
		//type dummy description for actor documentation file
		activeEditor.toTextEditor().pressShortcut(Keystrokes.RIGHT);
		activeEditor.toTextEditor().selectLine(lineForDescriptionField);
		bot.captureScreenshot("screenshots/document_view_"+documentationFileThatShouldBeOpened+".png");
		activeEditor.toTextEditor().insertText(descriptionFieldContent);
		activeEditor.save();
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
	
		//check that view now appears in Documented view
		//step 2- parse documented nodes
		SWTBotTree outlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName);
		SWTBotTreeItem documentedItem = outlineTree
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SECTION_2_3_USE_CASE_MODEL_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_USE_CASES_REPORT_NODE);
		String nodeName2 = SWTBotSupport.getChildNodeNameByNameSubstring(documentedItem,viewToDocumentName);
		documentedItem = documentedItem.getNode(nodeName2).select();
		assertTrue("view was not found in documented views", (documentedItem != null));
		captureScreenshot("tutorial_long/1.3.d_3.png");
	}
	
	@Test
	public void GettingStartedLong_1_3_e_re_generate_report() throws Exception {
		SWTBotSupport.generateReport(projectName, "report_1_3_e.pdf", false);
	}

	@Test
	public void GettingStartedLong_2_1_a_specify_a_new_datatype() throws Exception {
		//step 1- open related specification file by double-click on package
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.concepts.primarytypes.datatypes";
		String specificationFileThatShouldBeOpened = "primarytypes-datatypes.msr";

		SWTBotEditor activeEditor = SWTBotSupport.openMsrFileByPackageName(workbenchBot, projectName, packageToOpen, specificationFileThatShouldBeOpened);
		
		captureScreenshot("tutorial_long/2.1.a_1.png");

		//step 2- in the editor file, goto UseCase Model and call auto-completion
		String datatypeName = "dtAMessage";
		String attributeName = "value";
		String attributeType = "ptString";

		//import primitives
		//activeEditor.toTextEditor().navigateTo(14, 0);
		//activeEditor.toTextEditor().insertText("import lu.uni.lassy.messir.libraries.primitives");

		//new datatype template
		activeEditor.toTextEditor().navigateTo(18, 0);
		activeEditor.toTextEditor().autoCompleteProposal("", "datatype - new datatype with an attribute");

		//type dataype name, then attribute name, then attribute type name
		activeEditor.toTextEditor().insertText(datatypeName);
		keyboard.pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().insertText(attributeName);
		keyboard.pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().insertText(attributeType);
		keyboard.pressShortcut(Keystrokes.LF);

		SWTBotSupport.saveActiveEditor(activeEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		//step 3- Refresh Outline, expand Environment Model, check that node corresponding to new element is visible
		SWTBotTreeItem item = SWTBotSupport
				.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
				.expandNode(packageToOpen)
				.expandNode("Concept Model")
				.expandNode("Primary Types")
				.expandNode(datatypeName);
		assertTrue(datatypeName + " should appear in the Excalibur Outline", item.isVisible());

		captureScreenshot("tutorial_long/2.1.a_2.png");
	}
	
	@Test
	public void GettingStartedLong_2_1_b_create_view_class_diagram() throws Exception {
		//step 1- open use-case model specification by double-click on package
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.concepts.primarytypes.datatypes";
		String specificationElementName = "dtAMessage";
		String expectedViewName = "cm-view1";

		String[] strs = {projectName, ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE, packageToOpen, "Concept Model", "Primary Types", specificationElementName};
		SWTBotTreeItem item = SWTBotSupport.getNodeItem(SWTBotSupport.getExcaliburOutlineView(projectName), strs);
		item.contextMenu("New...")
			.menu("View - From Selection")
			.click();

		try {
			SWTBotShell analysisSelectionShell = SWTBotSupport.waitForWindowDialog("Analysis selection");
			analysisSelectionShell.bot().table().getTableItem(3).click();
			captureScreenshot("tutorial_long/2.1.b_1.png");
			analysisSelectionShell.bot().button("OK").click();
			bot.waitUntil(shellCloses(analysisSelectionShell));
			SWTBotSupport.waitForEndOfRunningJobs();
		} catch (Exception e) {
			//Analysis selection didn't appear
		}
		workbenchBot.sleep(3000);
		
		captureScreenshot("tutorial_long/2.1.b_2.png");
		for (SWTBotEditor editor: workbenchBot.editors()) {
			if (editor.getTitle().endsWith(".msrd")) {
				editor.saveAndClose();
			}
			if (editor.getTitle().equals(expectedViewName)) {
				editor.show();
				editor.save();
			}
		}
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		assertTrue("View was not created got this intead ->" + workbenchBot.activeEditor().getTitle(), workbenchBot.activeEditor().getTitle().contains(expectedViewName));
		
		//check that the view appears in the Logical mode
		String expectedViewNameInLogicalMode = "(CM-LPD-001) cm-view1";

		SWTBotSupport.setStructuralLogicalViewSpecOutline(SWTBotSupport.getExcaliburOutlineView(projectName), ExcaliburSpecificationOutlineView.RADIO_BUTTON_TOOLTIP_LOGICAL_VIEW);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);

		SWTBotTreeItem itemLocalView = SWTBotSupport
				.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
				.expandNode("Concept Model")
				.expandNode("Primary Types")
				.expandNode("Local Views");
		String nodeName = SWTBotSupport.getChildNodeNameByNameSubstring(itemLocalView, expectedViewNameInLogicalMode);
		itemLocalView = itemLocalView.expandNode(nodeName).select();

		assertTrue("View is not visible in Excalibur Outline logical mode (this is likely due to an invalid documentation issue)", itemLocalView.isVisible());

		captureScreenshot("tutorial_long/2.1.b_3.png");

		//check that view has one node and that it is named dtAMessage
		SWTBotGefEditor testGef = gefBot.gefEditor(expectedViewName);

		SWTBotGefEditPart rootDiagram = testGef.getSWTBotGefViewer().rootEditPart();
		assertTrue("View should have excatly one element", rootDiagram.children().size() == 1);
		
		SWTBotGefEditPart childNode= rootDiagram.children().get(0);
		if (childNode instanceof DNode) {
			DNode dNode = (DNode) childNode;
			assertTrue("There should be one node named." + specificationElementName, specificationElementName.equals(dNode.getName()));
		} else {
			assertTrue("There should be one node in the created diagram.", true);
		}
		
		assertTrue("View is not visible in Excalibur Outline logical mode (this is likely due to an invalid documentation issue)", itemLocalView.isVisible());

		//save and close all editors
		for (SWTBotEditor editor: workbenchBot.editors()) {
			editor.saveAndClose();
		}

	}

	@Test
	public void GettingStartedLong_2_1_c_rename_view() throws Exception {
		String expectedViewNameInLogicalMode = "(CM-LPD-001) cm-view1";

		//select view created and call Rename View contextual menu 
		SWTBotTreeItem itemClassViews = SWTBotSupport
				.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode("All Views")
				.expandNode("Class Views");
		String nodeName = SWTBotSupport.getChildNodeNameByNameSubstring(itemClassViews, expectedViewNameInLogicalMode);
		SWTBotTreeItem itemView = itemClassViews.expandNode(nodeName);
		itemView.select();
		
		itemView.contextMenu("View...")
			.menu("Rename View")
			.click();

		//rename view dialog
		String newViewName = "cm-lv-01";
		SWTBotShell renameViewShell = SWTBotSupport.waitForWindowDialog("Rename View");
		SWTBotText text = renameViewShell.bot().textWithTooltip("New View Name");
		text.setFocus();
		text.setText(newViewName);
		renameViewShell.bot().button(SWTBotSupport.BUTTON_OK).click();
		bot.waitUntil(shellCloses(renameViewShell));
		
		workbenchBot.sleep(1000);
		SWTBotSupport.saveAllEditors(workbenchBot);
		
		//open the renamed view
		String newViewNodeName = "(CM-LPD-001) cm-lv-01";
		SWTBotTreeItem newViewItem = SWTBotSupport
				.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode("All Views")
				.expandNode("Class Views");
		newViewNodeName = SWTBotSupport.getChildNodeNameByNameSubstring(newViewItem, newViewNodeName);
		newViewItem = newViewItem.expandNode(newViewNodeName).doubleClick();

		captureScreenshot("tutorial_long/2.1.c.png");
		assertTrue("View was not created got this intead ->" + workbenchBot.activeEditor().getTitle(), workbenchBot.activeEditor().getTitle().contains(newViewName));		
	}

	@Test
	public void GettingStartedLong_2_1_d_document_datatype() throws Exception {
		SWTBotTreeItem undocumentedItem = SWTBotSupport.getExcaliburOutlineTree(projectName)
			.expandNode(projectName)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_UNDOCUMENTED_REPORT_NODE)
			.getNode("dtAMessage")
			.select();

		captureScreenshot("tutorial_long/2.1.d_1.png");
		
		String elementToDocumentName = "dtAMessage";
		String documentationFileThatShouldBeOpened = "PrimaryType-Datatype-dtAMessage.msrd";
		String descriptionFieldContent = "@description \"Is representing the hello world message type.\"\n";
		int lineForDescriptionField = 7;
		
		//open documentation file
		undocumentedItem.doubleClick();
		workbenchBot.sleep(3000);
		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("Documentation file was not opened.", activeEditor.getTitle().equals(documentationFileThatShouldBeOpened));

		//type dummy description for element documentation file
		activeEditor.toTextEditor().selectLine(lineForDescriptionField);
		bot.captureScreenshot("screenshots/document_element_"+documentationFileThatShouldBeOpened+".png");
		activeEditor.toTextEditor().insertText(descriptionFieldContent);
		activeEditor.toTextEditor().pressShortcut(Keystrokes.DELETE);
		activeEditor.save();
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		//check that element now appears in its Documentation section
		//step 2- parse documented nodes for element
		SWTBotTree outlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName);
		SWTBotTreeItem documentedActYouItem = outlineTree
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SECTION_4_CONCEPT_MODEL_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_CONCEPT_MODEL_TYPES_DESCRIPTIONS_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_PRIMARY_TYPES_DATATYPES_TYPES_DESCRIPTIONS_REPORT_NODE);
		captureScreenshot("tutorial_long/2.1.d_2.png");

		String nodeName = SWTBotSupport.getChildNodeNameByNameSubstring(documentedActYouItem, elementToDocumentName);
		documentedActYouItem = documentedActYouItem.getNode(nodeName);

		assertTrue(elementToDocumentName + " was not found in documented messir specification elements", (documentedActYouItem != null));
	
		captureScreenshot("tutorial_long/2.1.d_3.png");
	}
	
	@Test
	public void GettingStartedLong_2_1_e_document_view_class_diagram() throws Exception {
		String viewToDocument = "(CM-LPD-001) cm-lv-01";
		
		SWTBotTreeItem item = SWTBotSupport
			.getExcaliburOutlineTree(projectName)
			.expandNode(projectName)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_UNDOCUMENTED_REPORT_NODE);
		String nodeName = SWTBotSupport.getChildNodeNameByNameSubstring(item, viewToDocument);
		item = item.getNode(nodeName).select();

		captureScreenshot("tutorial_long/2.1.e_1.png");
		
		String viewToDocumentName = "cm-lv-01";
		String documentationFileThatShouldBeOpened = "View-cm-view1.msrd";
		String descriptionFieldContent = "@description \"shows the dtAMessage primary datatype.\"\n";
		int lineForDescriptionField = 11;
		
		//open documentation file
		item.doubleClick();
		workbenchBot.sleep(3000);
		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("Documentation file was not opened.", activeEditor.getTitle().equals(documentationFileThatShouldBeOpened));
		
		//type dummy description for actor documentation file
		activeEditor.toTextEditor().pressShortcut(Keystrokes.RIGHT);
		activeEditor.toTextEditor().selectLine(lineForDescriptionField);
		bot.captureScreenshot("screenshots/document_view_"+documentationFileThatShouldBeOpened+".png");
		activeEditor.toTextEditor().insertText(descriptionFieldContent);
		activeEditor.save();
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
	
		//check that view now appears in Documented view
		//step 2- parse documented nodes
		SWTBotTree outlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName);
		SWTBotTreeItem documentedItem = outlineTree
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SECTION_4_CONCEPT_MODEL_REPORT_NODE)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_PRIMARY_TYPES_DATATYPES_VIEWS_REPORT_NODE);
		String nodeName2 = SWTBotSupport.getChildNodeNameByNameSubstring(documentedItem, viewToDocumentName);
		documentedItem.getNode(nodeName2).select();
	
		System.out.println("2_1_E: " + documentedItem);

		assertTrue("view was not found in documented views", (documentedItem != null));
		
		captureScreenshot("tutorial_long/2.1.e_2.png");
	}
	
	@Test
	public void GettingStartedLong_2_2_a_specify_input_event() throws Exception {
		//step 1- open messir specification file by double-click on package name
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.environment";
		String specificationFileThatShouldBeOpened = "environment.msr";

		SWTBotEditor activeEditor = SWTBotSupport.openMsrFileByPackageName(workbenchBot, projectName, packageToOpen, specificationFileThatShouldBeOpened);
		
		//captureScreenshot("tutorial_long/2.2.a_1.png");

		//step 2- in the editor file, goto UseCase Model and call auto-completion
		String operationName = "ieHelloWorld";
		String returnType = "ptBoolean";

		//import primitives
		//activeEditor.toTextEditor().navigateTo(13, 0);
		//activeEditor.toTextEditor().insertText("import lu.uni.lassy.messir.libraries.primitives");

		//new datatype template
		activeEditor.toTextEditor().selectRange(20, 0, 0);
		captureScreenshot("tutorial_long/2.2.a_1.png");
		keyboard.pressShortcut(Keystrokes.TAB);
		keyboard.pressShortcut(Keystrokes.TAB);
		keyboard.pressShortcut(Keystrokes.TAB);
		captureScreenshot("tutorial_long/2.2.a_2.png");
		activeEditor.toTextEditor().autoCompleteProposal("", "operation signature - new operation signature");

		//type dataype name, then attribute name, then attribute type name
		activeEditor.toTextEditor().insertText(operationName);
		keyboard.pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().insertText(returnType);
		keyboard.pressShortcut(Keystrokes.LF);

		SWTBotSupport.saveActiveEditor(activeEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
	
		//step 3- Refresh Outline, expand Environment Model, check that node corresponding to new element is visible
		SWTBotTreeItem item = SWTBotSupport
				.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
				.expandNode(packageToOpen)
				.expandNode("Environment Model")
				.expandNode("actYou (rnactYou)[1..*]")
				.expandNode("inactYou", operationName + "() : " + returnType);
		assertTrue(operationName + " should appear in the Excalibur Outline", item.isVisible());
		captureScreenshot("tutorial_long/2.2.a_3.png");
	}
	
	@Test
	public void GettingStartedLong_2_2_b_specify_output_event() throws Exception {
		//step 1- open messir specification file by double-click on package name
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.environment";
		String specificationFileThatShouldBeOpened = "environment.msr";

		SWTBotEditor activeEditor = SWTBotSupport.openMsrFileByPackageName(workbenchBot, projectName, packageToOpen, specificationFileThatShouldBeOpened);
		
		//captureScreenshot("tutorial_long/2.2.b_1.png");
		//step 2- in the editor file, goto UseCase Model and call auto-completion
		String operationName = "oeHelloWorld";
		String returnType = "ptBoolean";

		//new datatype template
		activeEditor.toTextEditor().selectRange(23, 0, 0);
		captureScreenshot("tutorial_long/2.2.b_1.png");
		keyboard.pressShortcut(Keystrokes.TAB);
		keyboard.pressShortcut(Keystrokes.TAB);
		keyboard.pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().autoCompleteProposal("", "operation signature - new operation signature");

		//type dataype name, then attribute name, then attribute type name
		activeEditor.toTextEditor().insertText(operationName);
		keyboard.pressShortcut(Keystrokes.TAB);
		activeEditor.toTextEditor().insertText(returnType);
		keyboard.pressShortcut(Keystrokes.LF);

		SWTBotSupport.saveActiveEditor(activeEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);

		//step 3- Refresh Outline, expand Environment Model, check that node corresponding to new element is visible
		SWTBotTreeItem item = SWTBotSupport.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
				.expandNode(packageToOpen)
				.expandNode("Environment Model")
				.expandNode("actYou (rnactYou)[1..*]")
				.expandNode("outactYou");
		captureScreenshot("tutorial_long/2.2.b_2.png");
		item = item.expandNode(operationName + "() : " + returnType);
		
		assertTrue(operationName + " should appear in the Excalibur Outline", item.isVisible());
		captureScreenshot("tutorial_long/2.2.b_3.png");
	}
	
	@Test
	public void GettingStartedLong_2_3_a_specify_operation_using_mcl() throws Exception {
		//step 1- open messir specification file by double-click on package name
		String packageToOpen = "lu.uni.lassy.excalibur.myhelloworld.environment";
		String specificationFileThatShouldBeOpened = "environment.msr";

		SWTBotEditor activeEditor = SWTBotSupport.openMsrFileByPackageName(workbenchBot, projectName, packageToOpen, specificationFileThatShouldBeOpened);
		

		//step 2- in the editor file, goto UseCase Model and call auto-completion

		//call contextual menu in textual editor after having selected an operation declaration name
		activeEditor.toTextEditor().selectRange(23, 23, 1);
		captureScreenshot("tutorial_long/2.3.a_1.png");
		activeEditor.toTextEditor().pressShortcut(Keystrokes.RIGHT);
		activeEditor.toTextEditor().contextMenu("Specify this operation").click();

		SWTBotSupport.waitForEndOfRunningJobs();
		activeEditor = workbenchBot.activeEditor();
		captureScreenshot("tutorial_long/2.3.a_2.png");
		assertTrue("Active editor should be a messir documentation file", activeEditor.getTitle().equals("environment-actYou-oeHelloWorld.msr"));
		
		//in the operation.msr file
		String importEnvironmentPackage = "lu.uni.lassy.excalibur.myhelloworld.environment";

		//type import
		activeEditor.toTextEditor().navigateTo(6, 0);
		activeEditor.toTextEditor().insertText("import " + importEnvironmentPackage+ "\n");

		activeEditor.toTextEditor().navigateTo(12, 0);
		String text = "";
		text += "preP:\n";
		text += "\tlet AvpStarted: ptBoolean in\n";
		text += "\tself.rnActor.rnSystem.vpStarted = AvpStarted\n";
		text += "\tand AvpStarted = true\n";
		text += "\n";
		text += "preF: true\n";
		text += "\n";
		text += "postF:\n";
		text += "\tlet TheactYou:actYou in\n";
		text += "\tlet AptString:ptString in\n";
		text += "\t/* Post Functional:*/\n";
		text += "\t/* PostF01 */\n";
		text += "\tAptString = 'Hello World !'\n";
		text += "\tand TheactYou.InterfaceIN = self.rnActor.InterfaceIN\n";
		text += "and TheactYou.InterfaceIN^ieHelloWorld(AptString)\n";
		text += "\n";
		text += "postP: true\n";
		activeEditor.toTextEditor().insertText(text);
			
		SWTBotSupport.saveActiveEditor(activeEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		SWTBotSupport.setStructuralLogicalViewSpecOutline(SWTBotSupport.getExcaliburOutlineView(projectName), ExcaliburSpecificationOutlineView.RADIO_BUTTON_TOOLTIP_STRUCTURAL_VIEW);
		//SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		//step 3- Refresh Outline, expand related Model, check that node corresponding to new element is visible
		String newPackageForOperation = "lu.uni.lassy.excalibur.myhelloworld.environment.operations.actYou.outactYou.oeHelloWorld";
		String[] strs = {projectName, ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE, newPackageForOperation, "Operation Model", "outactYou.oeHelloWorld() : ptBoolean"};
		SWTBotTreeItem item = SWTBotSupport.getNodeItem(SWTBotSupport.getExcaliburOutlineView(projectName), strs);
		assertTrue("Operation should appear in the Excalibur Outline", item.isVisible());
		captureScreenshot("tutorial_long/2.3.a_3.png");
	}
	
	@Test
	public void GettingStartedLong_2_4_generate_report() throws Exception {
		SWTBotSupport.generateReport(projectName, "report_2_4.pdf", false);
	}
	
	@Test
	public void GettingStartedLong_3_1_create_additional_latex_content() throws Exception {
		String latexFileToOpen = "title-page.tex";
		//open title-page.tex in report project
		SWTBotSupport
				.getExcaliburOutlineTree(projectName + ".report")
				.expandNode(projectName + ".report")
				.expandNode("doc")
				.expandNode("title-page")
				.expandNode(latexFileToOpen)
				.doubleClick();

		bot.sleep(2000);
		captureScreenshot("tutorial_long/3.1_1.png");

		for (SWTBotEditor editor: workbenchBot.editors()) {
			if (editor.getTitle().equals(latexFileToOpen)) {
				editor.show();
				editor.save();
			}
		}
		SWTBotEclipseEditor activeEclipseEditor = workbenchBot.activeEditor().toTextEditor();

		assertTrue("File was not opened got this intead ->" + activeEclipseEditor.getTitle(), activeEclipseEditor.getTitle().contains(latexFileToOpen));

		//fillin title page with some latex code
		activeEclipseEditor.selectLine(15);
		activeEclipseEditor.insertText("SWTBot\\\\");
		activeEclipseEditor.selectLine(16);
		activeEclipseEditor.insertText("Automatically generated\\\\");
		activeEclipseEditor.selectLine(17);
		activeEclipseEditor.insertText("University of Luxembourg\\\\");
		SWTBotSupport.saveActiveEditor(activeEclipseEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);

		SWTBotSupport.waitForEndOfRunningJobs();
		
		captureScreenshot("tutorial_long/3.1_2.png");
	}
	
	@Test
	public void GettingStartedLong_3_2_a_setup_makeglossaries_builder() throws Exception {
		ExcaliburStartup.setMakeGlossariesBuilder(workbenchBot, projectName + ".report");
	}
	
	@Test
	public void GettingStartedLong_3_2_b_add_new_glossary_entry() throws Exception {
		String latexFileToOpen = "glossary.tex";

		//open tex file in report project
		SWTBotSupport
			.getExcaliburOutlineTree(projectName + ".report")
			.expandNode(projectName + ".report")
			.expandNode("doc")
			.expandNode("glossary")
			.expandNode(latexFileToOpen)
			.doubleClick();
		
		bot.sleep(2000);
		captureScreenshot("tutorial_long/3.2.b_1.png");

		for (SWTBotEditor editor: workbenchBot.editors()) {
			if (editor.getTitle().equals(latexFileToOpen)) {
				editor.show();
				editor.save();
			}
		}
		SWTBotEclipseEditor activeEclipseEditor = workbenchBot.activeEditor().toTextEditor();
		assertTrue("File was not opened got this instead ->" + activeEclipseEditor.getTitle(), activeEclipseEditor.getTitle().contains(latexFileToOpen));
		bot.captureScreenshot("tutorial_long/3.2.b_2.png");

		//fillin title page with some latex code
		activeEclipseEditor.navigateTo(1, 0);
		String newEntry = "\\newglossaryentry{myNewEntry} {\n";
		newEntry += "\tname={newEntry},\n";
		newEntry += "\tdescription={description of the new entry}\n";
		newEntry += "}\n\n";

		activeEclipseEditor.insertText(newEntry);

		SWTBotSupport.saveActiveEditor(activeEclipseEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);

		SWTBotSupport.waitForEndOfRunningJobs();
		
		bot.captureScreenshot("tutorial_long/3.2.b_3.png");
	}

	@Test
	public void GettingStartedLong_3_2_c_reference_glossary_entry_in_body() throws Exception {
		String latexFileToOpen = "introduction.tex";
		//open tex file in report project
		SWTBotSupport
			.getExcaliburOutlineTree(projectName + ".report") 		
			.expandNode(projectName + ".report") 		
			.expandNode("doc") 		
			.expandNode("introduction") 		
			.expandNode(latexFileToOpen)
			.doubleClick();

		bot.sleep(2000);
		bot.captureScreenshot("tutorial_long/3.2.c_1.png");

		for (SWTBotEditor editor: workbenchBot.editors()) {
			if (editor.getTitle().equals(latexFileToOpen)) {
				editor.show();
				editor.save();
			}
		}
		SWTBotEclipseEditor activeEclipseEditor = workbenchBot.activeEditor().toTextEditor();
		assertTrue("File was not opened got this instead ->" + activeEclipseEditor.getTitle(), activeEclipseEditor.getTitle().contains(latexFileToOpen));
		bot.captureScreenshot("tutorial_long/3.2.c_2.png");

		//fillin title page with some latex code
		activeEclipseEditor.selectLine(10);
		activeEclipseEditor.insertText("\\gls{myNewEntry}\n");
		SWTBotSupport.saveActiveEditor(activeEclipseEditor);
		SWTBotSupport.refreshExcaliburOutlineView(projectName);

		SWTBotSupport.waitForEndOfRunningJobs();
		
		bot.captureScreenshot("tutorial_long/3.2.c_3.png");
	}
	
	@Test
	public void GettingStartedLong_3_3_generate_report() throws Exception {
		SWTBotSupport.generateReport(projectName, "report_3_3.pdf", true);
	}

	@AfterClass //All tests
	public static void afterClass() {
		try {
			//do nothing
		} catch (Exception e) {
			//do nothing
		}
		System.out.println("Test Case Finished !");
	}

}