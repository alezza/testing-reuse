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

import java.util.Arrays;
import java.util.List;

import org.eclipse.gmf.runtime.notation.impl.NodeImpl;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.DNodeList;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import junit.framework.AssertionFailedError;
import lu.uni.lassy.excalibur.common.ui.ExcaliburUiConstantsMessageDialogTitle;
import lu.uni.lassy.excalibur.common.utils.OSUtils;
import lu.uni.lassy.excalibur.common.utils.WorkspaceUtils;
import lu.uni.lassy.excalibur.dsl.messirdoc.ui.viewer.handlers.views.ExcaliburViewNameDialog;
import lu.uni.lassy.excalibur.dsl.messirdoc.ui.viewer.handlers.views.ExcaliburViewSelectionDialog;
import lu.uni.lassy.excalibur.tests.swtbot.utils.SWTBotSupport;
import lu.uni.lassy.excalibur.tests.swtbot.utils.UIStrings;
import lu.uni.lassy.excalibur.ui.outline.views.providers.label.ExcaliburOutlineLabelProvider;
import lu.uni.lassy.excalibur.ui.outline.wizards.newprojects.NewHelloWorldExampleProjectWizard;
import lu.uni.lassy.excalibur.ui.outline.wizards.newprojects.NewStdLibsProjectWizard;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExcaliburAllOutlineActions extends SWTBotTestCase {
	
	private final static Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
	private final static SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
	private final static SWTGefBot gefBot = new SWTGefBot();
	
	//test project selection
	public static String projectHelloWorld = UIStrings.HELLOWORLD_PROJECT_NAME;
	public static String projectICrashMini = UIStrings.ICRASHMINI_PROJECT_NAME;
	public static String projectStandardLibraries = UIStrings.STD_LIBS;
	public static String projectNew = "lu.uni.lassy.excalibur.myhelloworld";

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
		//SWTBotSupport.saveAll();
		//SWTBotSupport.closeAll();
		//SWTBotSupport.waitForWindowDialogAndClose("Save", "Yes", 2000);
		SWTBotSupport.waitForEndOfRunningJobs();
		System.out.println("*** End of cleaning workspace ***\n");
	}

	//@After
	//public void collapseAll() {
		//SWTBotSupport.collapseAllInOutlineView(projectHelloWorld);
		//SWTBotSupport.collapseAllInOutlineView(projectICrashMini);
		//SWTBotSupport.collapseAllInOutlineView(projectNew);
	//}

	@Test
	public void _00_0_newStandardLibrariesProjectAction() throws Exception {
		//call Eclipse menu File->New->Other... then select Excalibur Standard Libraries Project
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
			.expandNode("Standard Libraries Projects")
			.select();
		
		newWizardShell.bot().button("&Next >").click();
		SWTBotSupport.closeWindowDialog("New", SWTBotSupport.BUTTON_FINISH);


		SWTBotSupport.waitForWindowDialogAndClose(NewStdLibsProjectWizard.WIZARD_TITLE_NEW_STANDARD_LIBRARIES_PROJECT, SWTBotSupport.BUTTON_FINISH, 480000);

		SWTBotSupport.waitForEndOfRunningJobs();

		SWTBotSupport.refreshExcaliburOutlineView(projectStandardLibraries);

		//errors
		try {
			assertTrue(projectStandardLibraries+ " was not created.", SWTBotSupport.isProjectCreated(projectStandardLibraries));
			assertTrue(projectStandardLibraries + ".report was not created.", SWTBotSupport.isProjectCreated(projectStandardLibraries + ".report"));
			//assertTrue(projectName + ".simulation was not created.", SWTBotSupport.isProjectCreated(projectName + ".simulation"));
		} catch (AssertionFailedError e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void _00_1_newHelloWorldExampleProjectsAction() throws Exception {
		//call Eclipse menu Excalibur->New->Examples->Hello World Project
		workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR).click();
/*					.menu(SWTBotSupport.ECLIPSE_MENU_NEW)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW_EXAMPLE_PROJECTS)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW_EXAMPLE_PROJECTS_HelloWorld)
					.click();*/
		
		SWTBotSupport.waitForWindowDialogAndClose(NewHelloWorldExampleProjectWizard.WIZARD_TITLE_NEW_EXAMPLE_PROJECT, SWTBotSupport.BUTTON_FINISH, 48000);

		SWTBotSupport.waitForEndOfRunningJobs();

		SWTBotSupport.refreshExcaliburOutlineView(projectHelloWorld);

		//errors
		try {
			assertTrue(projectHelloWorld+ " was not created.", SWTBotSupport.isProjectCreated(projectHelloWorld));
			assertTrue(projectHelloWorld + ".report was not created.", SWTBotSupport.isProjectCreated(projectHelloWorld + ".report"));
			assertTrue(projectHelloWorld + ".simulation was not created.", SWTBotSupport.isProjectCreated(projectHelloWorld + ".simulation"));
		} catch (AssertionFailedError e) {
			fail(e.getMessage());
		}
	}
	
/*	@Test
	public void _00_2_newICrashMiniExampleProjectsAction() throws Exception {
		//call Eclipse menu Excalibur->New->Examples->Hello World Project
		workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW_EXAMPLE_PROJECTS)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW_EXAMPLE_PROJECTS_ICrashMini)
					.click();
		
		SWTBotSupport.waitForWindowDialogAndClose(NewICrashMiniExampleProjectWizard.WIZARD_TITLE_NEW_EXAMPLE_PROJECT, SWTBotSupport.BUTTON_FINISH, 480000);

		SWTBotSupport.waitForEndOfRunningJobs();

		//errors
		try {
			assertTrue(projectICrashMini+ " was not created.", SWTBotSupport.isProjectCreated(projectICrashMini));
			assertTrue(projectICrashMini + ".report was not created.", SWTBotSupport.isProjectCreated(projectICrashMini + ".report"));
			assertTrue(projectICrashMini + ".simulation was not created.", SWTBotSupport.isProjectCreated(projectICrashMini + ".simulation"));
		} catch (AssertionFailedError e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void _00_3_newSpecificationProjectAction() throws Exception {
		//call Eclipse menu Excalibur->New->Excalibur Specification Project
		workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW_SPECIFICATION_PROJECT)
					.click();
		
		SWTBotShell newWizard = workbenchBot.shell(NewSpecProjectWizard.WIZARD_TITLE_NEW_SPECIFICATION_PROJECT);
		//wait for shell
		workbenchBot.waitUntil(shellIsActive(NewSpecProjectWizard.WIZARD_TITLE_NEW_SPECIFICATION_PROJECT), 15000);

		SWTBotText text = workbenchBot.textWithLabel("Project name:");
		text.setFocus();
		text.setText(projectNew);
		
		//click on Finish
		newWizard.bot().button(SWTBotSupport.BUTTON_FINISH).click();

		workbenchBot.waitUntil(shellCloses(newWizard), 480000);

		SWTBotSupport.waitForEndOfRunningJobs();
		//errors
		try {
			assertTrue(projectNew+ " was not created.", SWTBotSupport.isProjectCreated(projectNew));
			assertTrue(projectNew + ".report was not created.", SWTBotSupport.isProjectCreated(projectNew + ".report"));
//			assertTrue(projectNew + ".simulation was not created.", SWTBotSupport.isProjectCreated(projectNew + ".simulation"));
		} catch (AssertionFailedError e) {
			fail(e.getMessage());
		}
	}
*/


	@Test
	public void addEObjectsToViewAction() throws Exception {
		//View->Add Selection to Existing View
		
		//expand in outline
		SWTBotTree helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		SWTBotTreeItem item = helloworldOutlineTree
			.expandNode(projectHelloWorld)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_ENVIRONMENT_MODEL_SPEC_NODE)
			.getNode("actYou (rnactYou)[0..*]");

		assertTrue("actYou should be present in case study", item.isVisible());

		item.contextMenu("View...")
			.menu("Add Selection to Existing View")
			.click();
		
		SWTBotShell shell = SWTBotSupport.waitForWindowDialog(ExcaliburViewSelectionDialog.DIALOG_TITLE_ADD_ELEMENT_TO_VIEW);
		
		shell.bot().tree().getTreeItem("cm-lv-01").doubleClick();
		
		bot.waitUntil(shellCloses(shell), 2000);
		
		//check that view has four nodes and that it is named dtAMessage
		SWTBotGefEditor testGef = gefBot.gefEditor("cm-lv-01");
		testGef.save();
		SWTBotSupport.waitForEndOfRunningJobs();
		
		SWTBotGefEditPart rootDiagram = testGef.getSWTBotGefViewer().rootEditPart().children().get(0);
		assertTrue("View should have exactly four elements", rootDiagram.children().size() == 4);
		
		String[] names = {"ctState", "inactYou", "outactYou", "actYou"};
		List<String> namesList = Arrays.asList(names);
		
		for (SWTBotGefEditPart n: rootDiagram.children()) {
			if (n.part().getModel() instanceof NodeImpl) {
				NodeImpl nodeImpl = (NodeImpl) n.part().getModel();
				if (nodeImpl.getElement() instanceof DNode) {
					DNode dNode = (DNode) nodeImpl.getElement();
					assertTrue("node name is invalid", namesList.contains(dNode.getName()));
				} else if (nodeImpl.getElement() instanceof DNodeList) {
					DNodeList dNodeList = (DNodeList) nodeImpl.getElement();
					String nodelistName = dNodeList.getName().trim().replaceAll("\n", "");
					if (nodelistName.indexOf('(') > 0) {
						nodelistName = nodelistName.substring(0, nodelistName.indexOf('('));
					}
					assertTrue("nodeList name is invalid", namesList.contains(nodelistName));
				} else {
					assertTrue("child name is not in the list", false);
				}
			}
		}
	}
	
	@Test
	public void copyAllViewsToProjectAction() throws Exception {
		//TODO
	}
	
	@Test
	public void copyViewToProjectAction() throws Exception {
		//TODO
	}
	
	@Test
	public void deleteViewAction() throws Exception {
		String viewName = "(CM-GPC-001) cm-gv-01 - representations";
		SWTBotSupport.deleteView(keyboard, projectHelloWorld, ExcaliburOutlineLabelProvider.LABEL_CLASS_VIEWS_NODE, viewName);
	}
	
	@Test
	public void documentElementAction() throws Exception {
		String msrdFileName = "UseCase-oeCreateSystemAndEnvironment.msrd";

		//1- Open documentation for a documented element
		SWTBotTree helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		SWTBotTreeItem item = helloworldOutlineTree
			.expandNode(projectHelloWorld)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_USE_CASE_MODEL_SPEC_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_SUBFUNCTION_USE_CASES_SPEC_NODE)
			.getNode(0);

		assertTrue("There should be at least one node", item.isVisible());

		item.contextMenu("Document...")
			.menu("Open Documentation")
			.click();

		//CHECK that msrd file has been opened
		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("Documentation file was not opened.", activeEditor.getTitle().equals(msrdFileName));
		
		activeEditor.close();

		//2- delete the documentation file
		SWTBotSupport.deleteResource(keyboard, projectHelloWorld, "usecases", "documentation", msrdFileName);

		//3- Document an element with no msrd file at all
		//then call Open Documentation again
		helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		item = helloworldOutlineTree
			.expandNode(projectHelloWorld)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_REPORT_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_UNDOCUMENTED_REPORT_NODE)
			.getNode(0);

		assertTrue("There should be at least one node", item.isVisible());

		item.contextMenu("Document...")
			.menu("Open Documentation")
			.click();

		WorkspaceUtils.refreshProject(WorkspaceUtils.getProjectByName(projectHelloWorld));
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectHelloWorld);

		//CHECK that msrd file has been opened
		activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("Documentation file was not opened.", activeEditor.getTitle().equals(msrdFileName));
	}
	
	@Test
	public void documentUndocumentedSpecificationsAction() throws Exception {
		String msrdFileName = "UseCase-oeCreateSystemAndEnvironment.msrd";

		//1- Call Document Undocumented Specifications on HelloWorld should return that there are no undocumented elements because all elements are already documented
		SWTBotTree helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		SWTBotTreeItem item = helloworldOutlineTree
			.expandNode(projectHelloWorld);

		item.contextMenu("Document...")
			.menu("Undocumented Specifications")
			.click();

		//CHECK that Dialog opens
		SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.ALL_SPECIFICATION_ELEMENTS_ALREADY_DOCUMENTED, SWTBotSupport.BUTTON_OK);
		
		//2- remove one documentation
		//delete a documentation file in HelloWorld
		SWTBotSupport.deleteResource(keyboard, projectHelloWorld, "usecases", "documentation", msrdFileName);
		
		//3- Call Document Undocumented Specifications again
		helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		item = helloworldOutlineTree
			.expandNode(projectHelloWorld);

		item.contextMenu("Document...")
			.menu("Undocumented Specifications")
			.click();

		WorkspaceUtils.refreshProject(WorkspaceUtils.getProjectByName(projectHelloWorld));
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectHelloWorld);

		//CHECK that Dialog opens
		SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.NEW_DOCUMENTATION_FOR_SPECIFICATION_ELEMENTS, SWTBotSupport.BUTTON_OK);
	}
	
	@Test
	public void documentUndocumentedViewsAction() throws Exception {
		String msrdFileName = "Views-cm.msrd";

		//1- Call Document Undocumented SpeciViews on HelloWorld should return that there are no undocumented views because all views are already documented
		SWTBotTree helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		SWTBotTreeItem item = helloworldOutlineTree
			.expandNode(projectHelloWorld);

		item.contextMenu("Document...")
			.menu("Undocumented Views")
			.click();

		//CHECK that Dialog opens
		SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.ALL_VIEWS_ALREADY_DOCUMENTED, SWTBotSupport.BUTTON_OK);
		
		//2- remove one documentation file of a view
		SWTBotSupport.deleteResource(keyboard, projectHelloWorld, "views", msrdFileName);
		
		//3- Call Document Undocumented Specifications again
		helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		item = helloworldOutlineTree
			.expandNode(projectHelloWorld);

		item.contextMenu("Document...")
			.menu("Undocumented Views")
			.click();

		WorkspaceUtils.refreshProject(WorkspaceUtils.getProjectByName(projectHelloWorld));
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectHelloWorld);

		//CHECK that Dialog opens
		SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.NEW_DOCUMENTATION_FOR_VIEWS, SWTBotSupport.BUTTON_OK);
	}
	
	@Test
	public void documentViewAction() throws Exception {
		String msrdFileName = "View-cm-lv-01.msrd";
		String viewName = "(CM-GPC-003) cm-lv-01 - representations";
		String undocumentedViewName = "cm-lv-01 - representations";

		//1- Open documentation for a documented view
		SWTBotTree helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);

		SWTBotTreeItem item = helloworldOutlineTree
			.expandNode(projectHelloWorld)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_ALL_VIEWS_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_CLASS_VIEWS_NODE)
			.getNode(viewName);

		item.contextMenu("Document...")
			.menu("Open Documentation")
			.click();

		//CHECK that msrd file has been opened
		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("Documentation file was not opened.", activeEditor.getTitle().equals(msrdFileName));
		activeEditor.close();

		//2- delete the documentation file
		SWTBotSupport.deleteResource(keyboard, projectHelloWorld, "documentation", msrdFileName);
		
		//3- Document a view with no msrd file at all
		//then open documentation again
		helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		item = helloworldOutlineTree
			.expandNode(projectHelloWorld)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_ALL_VIEWS_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_CLASS_VIEWS_NODE)
			.getNode(undocumentedViewName);

		item.contextMenu("Document...")
			.menu("Open Documentation")
			.click();

		WorkspaceUtils.refreshProject(WorkspaceUtils.getProjectByName(projectHelloWorld));
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectHelloWorld);

		//CHECK that msrd file has been opened
		activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("Documentation file was not opened.", activeEditor.getTitle().equals(msrdFileName));
	}
	
	@Test
	public void duplicateViewAction() throws Exception {
		String viewName = "(CM-GPC-003) cm-lv-01 - representations";
		String newViewName = "cm-lv-01-duplicate";

		//1- Duplicate view cm-lv-01
		SWTBotTree helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);

		SWTBotTreeItem item = helloworldOutlineTree
			.expandNode(projectHelloWorld)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_ALL_VIEWS_NODE)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_CLASS_VIEWS_NODE)
			.getNode(viewName);

		item.contextMenu("View...")
			.menu("Duplicate View...")
			.click();

		//enter for the new duplicate view
		SWTBotShell duplicateViewDialog = SWTBotSupport.waitForWindowDialog(ExcaliburViewNameDialog.TITLE_DUPLICATE_VIEW);
		duplicateViewDialog.bot().textWithTooltip(ExcaliburViewNameDialog.TEXT_TOOLTIP_NEW_VIEW_NAME).setText(newViewName);
		SWTBotSupport.closeWindowDialog(ExcaliburViewNameDialog.TITLE_DUPLICATE_VIEW);

		//select the view container if necessary
		String analysisSelectionDialogTitle = "Analysis selection";
		SWTBotShell analysisSelectionShell = SWTBotSupport.waitForWindowDialog(analysisSelectionDialogTitle);
		analysisSelectionShell.bot().table().getTableItem(1).click();
		SWTBotSupport.closeWindowDialog(analysisSelectionDialogTitle);
		
		//wait for end of running jobs
		SWTBotSupport.waitForEndOfRunningJobs();

		//CHECK that msrd file has been opened
		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		assertTrue("View was not duplicated.", activeEditor.getTitle().equals(newViewName));
		activeEditor.close();
	}
	
	@Test
	public void exportAsToRepositoryAction() throws Exception {
		//TODO later
	}
	
	@Test
	public void exportToRepositoryAction() throws Exception {
		//TODO later
	}
	
	@Test
	public void generateAllImagesAction() throws Exception {
		//1- delete folder images-all-gen in report project
		SWTBotSupport.deleteResource(keyboard, projectHelloWorld, "images-all-gen");
		
		//2- Call Generate->Images (all)
		SWTBotTree helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		SWTBotTreeItem item = helloworldOutlineTree
			.expandNode(projectHelloWorld);

		item.contextMenu("Generate...")
			.menu("Images (all)")
			.click();

		SWTBotShell progressShell = SWTBotSupport.waitForWindowDialog("Progress Information");
		bot.waitUntil(shellCloses(progressShell), 25000);
		
		//CHECK that images have been generated
		helloworldOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectHelloWorld);
		item = helloworldOutlineTree
			.expandNode(projectHelloWorld)
			.expandNode("images-all-gen");

		SWTBotTreeItem itemAll = item.expandNode("all");
		assertTrue("There should be 20 images generated !", itemAll.getItems().length == 20);
		
		SWTBotTreeItem itemMergedPdfFile =item.expandNode("merged").expandNode("merged.pdf");
		assertTrue("There should be a generated pdf file named all.pdf !", itemMergedPdfFile.isVisible());
	}
	
	@Test
	public void generateImagesFromSelectionAction() throws Exception {
		
	}
	
	@Test
	public void generateReportAction() throws Exception {
		
	}
	
	@Test
	public void generateReportDefinitionAction() throws Exception {
		
	}
	
	@Test
	public void generateReportSimulationAction() throws Exception {
		
	}
	
	@Test
	public void generateReportSpecificationAction() throws Exception {
		
	}
	
	@Test
	public void generateReportWithoutImagesAction() throws Exception {
		
	}
	
	@Test
	public void generateSimulationAction() throws Exception {
		
	}
	
	@Test
	public void implementOperationInPrologAction() throws Exception {
		
	}
	
	@Test
	public void inAppendixDocumentationAction() throws Exception {
		
	}
	
	@Test
	public void inReportDocumentationAction() throws Exception {
		
	}
	
	@Test
	public void lifesaverAction() throws Exception {
		
	}
	
	@Test
	public void newDuplicateSpecificationProjectAction() throws Exception {
		
	}
	
	@Test
	public void newLatexFileAction() throws Exception {
		
	}
	
	@Test
	public void newMessimDocFileAction() throws Exception {
		
	}
	
	@Test
	public void newMessirFileAction() throws Exception {
		
	}
	
	@Test
	public void newMessirFileTestCaseInstanceAction() throws Exception {
		
	}
	
	@Test
	public void newMessirFileUseCaseInstanceAction() throws Exception {
		
	}
	
	@Test
	public void newPrologFileAction() throws Exception {
		
	}
	
	@Test
	public void newTestCaseInstanceViewAction() throws Exception {
		
	}
	
	@Test
	public void newUseCaseInstanceViewAction() throws Exception {
		
	}
	
	@Test
	public void newUseCaseViewAction() throws Exception {
		
	}
	
	@Test
	public void newViewAction() throws Exception {
		
	}
	
	@Test
	public void newViewOperationScopeAction() throws Exception {
		
	}
	
	@Test
	public void newViewTypeDependencyAction() throws Exception {
		
	}
	
	@Test
	public void newViewWithEObjectsAction() throws Exception {
		
	}
	
	@Test
	public void openRelatedViewsAction() throws Exception {
		
	}
	
	@Test
	public void openViewAction() throws Exception {
		
	}
	
	@Test
	public void refreshProjectAction() throws Exception {
		
	}
	
	@Test
	public void renameViewAction() throws Exception {
		
	}
	
	@Test
	public void reportUpdateMakeGlossairesAction() throws Exception {
		
	}
	
	@Test
	public void showDocumentationAction() throws Exception {
		
	}
	
	@Test
	public void simulateAction() throws Exception {
		
	}
	
	@Test
	public void specifyActorInterfacesOperationsAction() throws Exception {
		
	}
	
	@Test
	public void specifyOperationAction() throws Exception {
		
	}
	
	@Test
	public void updateDocumentationAction() throws Exception {
		
	}
	
	@Test
	public void updateViewDocumentationsOrderingAction() throws Exception {
		
	}
	
	/* TOOLBAR */	
	/*@Test
	public void GettingStartedLong_1_3_a_new_outline_view_for_report() throws Exception {
		//SWTBotSupport.createNewExcaliburOutlineView(projectName, projectName + ".report");
		//SWTBotSupport.getExcaliburOutlineTree(projectName + ".report").expandNode(projectName + ".report");
	}*/

	/*
	@Test
	public void GettingStartedLong_0_project_creation() throws Exception {
		//call Eclipse menu Excalibur->New->Example Projects->Hello World Example Project
		workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW)
					.menu(SWTBotSupport.ECLIPSE_MENU_EXCALIBUR_NEW_SPECIFICATION_PROJECT)
					.click();
		
		SWTBotShell newWizard = workbenchBot.shell(NewSpecProjectWizard.WIZARD_TITLE_NEW_SPECIFICATION_PROJECT);
		//wait for shell
		workbenchBot.waitUntil(shellIsActive(NewSpecProjectWizard.WIZARD_TITLE_NEW_SPECIFICATION_PROJECT), 15000);

		SWTBotText text = workbenchBot.textWithLabel("Project name:");
		text.setFocus();
		text.setText(projectName);
		
		//click on Finish
		newWizard.bot().button(SWTBotSupport.BUTTON_FINISH).click();

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
		activeEditor.toTextEditor().autoCompleteProposal("", "use case (system subfunction) - new use case (system subfunction)");
		
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
	*/

}