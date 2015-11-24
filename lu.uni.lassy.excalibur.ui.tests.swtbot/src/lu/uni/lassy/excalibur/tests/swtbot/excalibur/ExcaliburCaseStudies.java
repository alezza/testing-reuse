/*******************************************************************************
 * Copyright (c) 2014-2015 University of Luxembourg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Philippe YOANN - initial API and implementation
 *     Benoît Ries- follow-up implementation
 ******************************************************************************/
package lu.uni.lassy.excalibur.tests.swtbot.excalibur;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import lu.uni.lassy.excalibur.common.utils.OSUtils;
import lu.uni.lassy.excalibur.tests.swtbot.utils.SWTBotSupport;
import lu.uni.lassy.excalibur.tests.swtbot.utils.UIStrings;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExcaliburCaseStudies extends SWTBotTestCase {

	private static SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
	private static SWTGefBot gefBot = new SWTGefBot();

	//test project selection (handles iCrashMini or HelloWorld or BOTH)
//	public static String[] projects = {ExcaliburUIStrings.HELLOWORLD_PROJECT_NAME};
	public static String[] projects = {UIStrings.ICRASHMINI_PROJECT_NAME};
//	public static String[] projects = {ExcaliburUIStrings.ICRASH_PROJECT_NAME};
//	public static String[] projects = {ExcaliburUIStrings.HELLOWORLD_PROJECT_NAME, ExcaliburUIStrings.ICRASHMINI_PROJECT_NAME};
//	public static String[] projects = {ExcaliburUIStrings.HELLOWORLD_PROJECT_NAME, ExcaliburUIStrings.ICRASHMINI_PROJECT_NAME, ExcaliburUIStrings.ICRASH_PROJECT_NAME};

	// Opens eclipse and creates a new Excalibur project (contains the project creation tests)
	@BeforeClass //All tests
	public static void beforeClass() throws Exception {
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
			
		for (String project: projects) {
			//create new project
			ExcaliburStartup.newCaseStudyProject(workbenchBot,project);

			//check that standard libraries project created
			assertTrue(UIStrings.STD_LIBS + " was not created.", SWTBotSupport.isProjectCreated(UIStrings.STD_LIBS));
			//		assertTrue(SIM_REF + " was not created.", Support.isProjectCreated(Support.SIM_REF));

			//check that all excalibur projects created (specification, report, simulation)
			assertTrue(project + " was not created.", SWTBotSupport.isProjectCreated(project));
			assertTrue(project + ".report was not created.", SWTBotSupport.isProjectCreated(project + ".report"));
			assertTrue(project + ".simulation was not created.", SWTBotSupport.isProjectCreated(project + ".simulation"));
		}

		//open Excalibur Specification perspective
		ExcaliburStartup.setExcaliburSpecificationPerspective(workbenchBot);
		
		SWTBotSupport.waitForEndOfRunningJobs();
	}
	
	@Test
	// Checks if Specification projects in the model explorer exits in the specification outline
	public void tc_fc_00_CheckThatNoProblems() throws Exception {
		SWTBotPreferences.TIMEOUT = 1500;
		List<String> allSpecificationProjectNames = SWTBotSupport.getAllSpecificationProjectNames();
		SWTBotPreferences.TIMEOUT = 5000;
		
		SWTBotView problemsView = SWTBotSupport.getProblemsView();
		problemsView.bot().tree().setFocus();
		String errorsNodeName = SWTBotSupport.getChildNodeNameByNameSubstring(problemsView.bot().tree(), "Errors");
		SWTBotTreeItem errorsNodeItem = problemsView.bot().tree().expandNode(errorsNodeName);
		bot.sleep(1000);
		for (int i = 0; i < errorsNodeItem.getItems().length; i++) {
			String projectName = errorsNodeItem.cell(i, 2);
			if (projectName.startsWith("/")) {
				projectName = projectName.substring(1);
			}
			if (projectName.contains("/")) {
				projectName = projectName.substring(0, projectName.indexOf("/"));
			}
			assertTrue(projectName + " project has errors !" + errorsNodeItem.cell(i,0) + ", "+ errorsNodeItem.cell(i,1), !allSpecificationProjectNames.contains(projectName));
		}		
	}

/*	@Test
	// Checks if Specification projects in the model explorer exits in the specification outline
	public void tc_fc_01_CheckForSpecificationProject() throws Exception {

		// timeout change, speeds up the assert
		//SWTBotPreferences.TIMEOUT = 10;

		SWTBotView modelExplorerView = SWTBotSupport.getModelExplorerView();
		SWTBotView specificationOutlineView = SWTBotSupport.getSpecificationOutlineView();
		SWTBotTree modelExplorerTree = modelExplorerView.bot().tree();
		//SWTBotTree specificationOutlineTree = specificationOutlineView.bot().tree();

		SWTBotTreeItem[] treeItems = modelExplorerTree.getAllItems();

		for (SWTBotTreeItem modelExplorerNode : treeItems) {
			
			//open Properties dialog for current model explorer node
			modelExplorerView.setFocus();
			modelExplorerTree.select(modelExplorerNode);
			workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_FILE).menu("Properties").click();
			workbenchBot.tree().select(UIStrings.excalibur);

			//if the project selected in the model explorer is not a specification project
			if (!SWTBotSupport.projectNature("specification")) {
				//close Properties dialog
				workbenchBot.closeAllShells();
				//go to the next model explorer node
				continue;
			}

			String modelExplorerProjectName = modelExplorerNode.getText();
			captureScreenshot("screenshots/tc_fc_01_in_ME_" +  modelExplorerProjectName + ".png");

			//close Properties dialog
			workbenchBot.closeAllShells();


			//set focus to the Specification Outline
			specificationOutlineView.setFocus();
			specificationOutlineView.bot().tree().setFocus();

			captureScreenshot("screenshots/tc_fc_01_in_SO_" +  modelExplorerProjectName + ".png");

			//check that there is a node in the Specification Outline for the specification project present in the model explorer
			SWTBotTreeItem item = specificationOutlineView.bot().tree().expandNode(modelExplorerProjectName);
			assertTrue("Specification project " + modelExplorerProjectName + " is not visible in the Specification Outline !", item.isVisible());
			System.out.println("Specification project "+ modelExplorerProjectName + " is visible in the Specification Outline YES.");
		}
	}
	
	
	@Test
	//Checks if a project in the model explorer exits in the report outline
	public void tc_fc_02_CheckForReportProject() throws Exception {
		for (String project: projects) {

			SWTBotView modExp = SWTBotSupport.getModelExplorerView();
			modExp.setFocus();
	
			SWTBotTree tree = modExp.bot().tree();
	
			String modExpProject = tree.getTreeItem(project).getText();
			String modExpRepProject = tree.getTreeItem(project + ".report")
					.getText();
	
			tree.select(project + ".report");
			// Sometimes the selection doesn't take so re-select
			tree.select(project + ".report");
	
			workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_FILE).menu("Properties").click();
	
			workbenchBot.tree().select(UIStrings.excalibur);
	
			assertTrue("Report nature was not set", SWTBotSupport.projectNature("report"));
	
			workbenchBot.closeAllShells();
	
			SWTBotView repOut = SWTBotSupport.getReportOutlineView();
			repOut.setFocus();
	
			SWTBotToolbarButton toolbar = repOut.toolbarButton(UIStrings.refreshOutline);
			toolbar.click();
	
			tree = repOut.bot().tree();
	
			String specOutProject = tree.getTreeItem(project).getText();
	
			String specOutRepProject = tree.getTreeItem(project + ".report")
					.getText();
	
			assertTrue("Project name does not match",
					specOutRepProject.equals(modExpRepProject));
			assertTrue("Project name does not match",
					specOutProject.equals(modExpProject));
		}
	}

	@Test
	// Works
	public void tc_fc_03_CanCreateMsrFileWContextMenu() throws Exception {
		for (String project: projects) {

			String folderContainer = "concepts";
			String newFileName = "msrFileCM.msr";
			String newPackageName = "pkg.name.swtbot";

			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			specOut.setFocus();

			SWTBotTree specTree = specOut.bot().tree();

			specTree.select(project);

			new SWTBotMenu(ContextMenuHelper.contextMenu(specTree, AbstractExcaliburOutlineView.CONTEXTUAL_MENU_NEW, NewMessirFileAction.ACTION_NAME_MESSIR_FILE_PACKAGE)).click();

			SWTBotShell newMessirFileShell = workbenchBot.shell(NewMessirFileWizard.WIZARD_TITLE_NEW_MESSIR_FILE).activate();
			workbenchBot.waitUntil(shellIsActive(NewMessirFileWizard.WIZARD_TITLE_NEW_MESSIR_FILE));
			captureScreenshot("screenshots/tc_fc_03_wizard_" + project + ".png");
			newMessirFileShell.bot().textWithLabel(NewFileWizardPage.TEXT_LABEL_FILE_NAME).setText(newFileName);
			newMessirFileShell.bot().textWithLabel(NewMessirFileWizardPage.TEXT_LABEL_PACKAGE_NAME).setText(newPackageName);
			newMessirFileShell.bot().checkBox(NewMessirFileWizardPage.CHECKBOX_LABEL_CONCEPT_MODEL).click();
			captureScreenshot("screenshots/tc_fc_03_wizard_" + project + "_2.png");
			newMessirFileShell.bot().button(SWTBotSupport.BUTTON_FINISH).click();
			workbenchBot.waitUntil(shellCloses(newMessirFileShell), 10000);
			
			SWTBotSupport.waitForEndOfRunningJobs();
			SWTBotSupport.refreshReportOutline();

			assertTrue("Project doesn't exist", SWTBotSupport.isFileCreated(project, folderContainer, newFileName));
		}
	}

	@Test
	// Works
	public void tc_fc_04_CreateFileWMenuBrowse() throws Exception {
		for (String project: projects) {

			String folderContainer = "concepts";
			String newFileName = "msrFile.msr";
			String newPackageName = "pkg.tc.fc04.createFileWMenuBrowse";
			
			//call Eclipse menu File->New->Other
			workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_FILE).menu(SWTBotSupport.ECLIPSE_MENU_FILE_NEW).menu(SWTBotSupport.ECLIPSE_MENU_FILE_NEW_OTHER).click();
	
			//wait for dialog to open
			SWTBotShell newFileDialog = workbenchBot.shell(SWTBotSupport.ECLIPSE_DIALOG_TITLE_NEW_FILE).activate();
			workbenchBot.waitUntil(shellIsActive(SWTBotSupport.ECLIPSE_DIALOG_TITLE_NEW_FILE));
	
			//select messir file
			workbenchBot.tree().expandNode(UIStrings.excalibur).select(SWTBotSupport.ECLIPSE_NEW_FILE_DIALOG_TREE_MESSIR_DOC_FILE_MSRDMESSIR_FILE_MSR);
			workbenchBot.button(SWTBotSupport.BUTTON_NEXT).click();
	
			//browse for file location
			workbenchBot.button("Browse...").click();
	
			//open browser dialog
			SWTBotShell browserFolderSelection = workbenchBot.shell(SWTBotSupport.ECLIPSE_DIALOG_TITLE_FOLDER_SELECTION).activate();
			workbenchBot.waitUntil(shellIsActive(SWTBotSupport.ECLIPSE_DIALOG_TITLE_FOLDER_SELECTION));
			workbenchBot.tree().expandNode(project).getNode(folderContainer).select();
	
			//close browser dialog
			workbenchBot.button(SWTBotSupport.BUTTON_OK).click();
			workbenchBot.waitUntil(shellCloses(browserFolderSelection));
	
			//fillin file name and check concept model
			workbenchBot.textWithLabel(NewFileWizardPage.TEXT_LABEL_FILE_NAME).setText(newFileName);
			workbenchBot.textWithLabel(NewMessirFileWizardPage.TEXT_LABEL_PACKAGE_NAME).setText(newPackageName);
			workbenchBot.checkBox(NewMessirFileWizardPage.CHECKBOX_LABEL_CONCEPT_MODEL).click();
	
			//finish New File Dialog
			workbenchBot.button(SWTBotSupport.BUTTON_FINISH).click();
			workbenchBot.waitUntil(shellCloses(newFileDialog));
			
			//check that file has been created
			assertTrue("Project doesn't exist", SWTBotSupport.isFileCreated(project, folderContainer, newFileName));
		}
	}

	@Test
	public void tc_fc_05_CanCreateConceptModelSpecificationDocumentation() throws Exception {
		for (String project: projects) {
			SWTBotView modelExplorerView = SWTBotSupport.getModelExplorerView();
			SWTBotTree modelExplorerTree = modelExplorerView.bot().tree();
			modelExplorerTree.setFocus();
			
			SWTBotTreeItem conceptsDocumentationFolder = modelExplorerTree.expandNode(project,"concepts","documentation").select();
			SWTBotSupport.deleteTreeItem(conceptsDocumentationFolder);
			
			workbenchBot.sleep(2000);
			//select project node in model explorer and save it
			try {
				modelExplorerTree.setFocus();
				modelExplorerTree.getTreeItem("*"+project).select();
				modelExplorerTree.contextMenu("Save").click();
				SWTBotSupport.waitForEndOfRunningJobs();
				SWTBotSupport.refreshSpecificationOutline();
				workbenchBot.sleep(2000);
			} catch (Exception e) {
				//no save needed
			}
			
			//select project node in Specification Outline and call action Document Undocumented Specifications
			SWTBotView specificationOutlineView = SWTBotSupport.getSpecificationOutlineView();
			specificationOutlineView.setFocus();
			SWTBotTree specificationOutlineTree = specificationOutlineView.bot().tree();
			specificationOutlineTree.setFocus();
			
			specificationOutlineTree.getTreeItem(project).contextMenu("Document...").menu("Undocumented Specifications").click();
	
			WorkspaceUtils.refreshWorkspace();
			SWTBotSupport.waitForEndOfRunningJobs();
			
			SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.NEW_DOCUMENTATION_FOR_SPECIFICATION_ELEMENTS,10000);
			
			modelExplorerView.setFocus();
			modelExplorerTree.setFocus();

			workbenchBot.sleep(2000);
			
			assertTrue("Concept documentation folder is not present", modelExplorerTree.expandNode(project, "concepts", "documentation").isVisible());
		}
	}

	@Test
	public void tc_fc_05_CanCreateEnvironmentModelSpecificationDocumentation() throws Exception {
		for (String project: projects) {
			SWTBotView modelExplorerView = SWTBotSupport.getModelExplorerView();
			SWTBotTree modelExplorerTree = modelExplorerView.bot().tree();
			modelExplorerTree.setFocus();

			SWTBotTreeItem environmentDocumentationFolderItem = modelExplorerTree.expandNode(project, "environment", "documentation").select();
			SWTBotSupport.deleteTreeItem(environmentDocumentationFolderItem);
	
			workbenchBot.sleep(2000);
			
			//select project node in model explorer and save it
			try {
				modelExplorerTree.setFocus();
				modelExplorerTree.getTreeItem("*"+project).select();
				modelExplorerTree.contextMenu("Save").click();
				SWTBotSupport.waitForEndOfRunningJobs();
				SWTBotSupport.refreshSpecificationOutline();
				workbenchBot.sleep(2000);
			} catch (Exception e) {
				//no save needed
			}

			//select project node in Specification Outline and call action Document Undocumented Specifications
			SWTBotView specificationOutlineView = SWTBotSupport.getSpecificationOutlineView();
			specificationOutlineView.setFocus();
			SWTBotTree specTree = specificationOutlineView.bot().tree();
			
			specTree.getTreeItem(project).contextMenu("Document...").menu("Undocumented Specifications").click();
	
			WorkspaceUtils.refreshWorkspace();
			SWTBotSupport.waitForEndOfRunningJobs();
	
			SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.NEW_DOCUMENTATION_FOR_SPECIFICATION_ELEMENTS,10000);
	
			modelExplorerView.setFocus();
			modelExplorerTree.setFocus();
		
			workbenchBot.sleep(2000);

			assertTrue("Environment documentation folder is not present",environmentDocumentationFolderItem.isVisible());
		}
	}

	@Test
	public void tc_fc_07_CanAddMrsdFiles() throws Exception {
		for (String project: projects) {
			String folderContainer = "concepts";
			String fileName = "msrdFile.msrd";
	
			workbenchBot.menu(SWTBotSupport.ECLIPSE_MENU_FILE).menu(SWTBotSupport.ECLIPSE_MENU_FILE_NEW).menu(SWTBotSupport.ECLIPSE_MENU_FILE_NEW_OTHER).click();
	
			//wait for New File Dialog to open
			SWTBotShell newFileDialog = workbenchBot.shell(SWTBotSupport.ECLIPSE_MENU_FILE_NEW).activate();
			workbenchBot.waitUntil(shellIsActive(SWTBotSupport.ECLIPSE_DIALOG_TITLE_NEW_FILE));
			
			//select new msrd file wizard
			workbenchBot.tree().expandNode(UIStrings.excalibur).select(SWTBotSupport.ECLIPSE_NEW_FILE_DIALOG_TREE_MESSIR_DOC_FILE_MSRD);
			workbenchBot.button(SWTBotSupport.BUTTON_NEXT).click();
	
			//fillin new msrd file location and file name
			workbenchBot.textWithLabel(NewFileWizardPage.TEXT_LABEL_LOCATION).setText("/" + project + "/" + folderContainer);
			workbenchBot.textWithLabel(NewFileWizardPage.TEXT_LABEL_FILE_NAME).setText(fileName);
			
			//close New File Dialog
			workbenchBot.button(SWTBotSupport.BUTTON_FINISH).click();
			workbenchBot.waitUntil(shellCloses(newFileDialog));
			
			//check that file has been created
			assertTrue("File wasn't created", SWTBotSupport.isFileCreated(project, folderContainer,fileName));
		}
	}

	@Test
	public void tc_mv_02_CanOpenRelatedViewWContextMenu() throws Exception {
		for (String project: projects) {
			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			specOut.setFocus();
			SWTBotTree specificationOutlineTree = specOut.bot().tree();
	
			specificationOutlineTree.expandNode(project)
				.expandNode(UIStrings.SPEC_ENVIRONMENT_MODEL)
				.getNode(2)
				.contextMenu("View...")
				.menu("Open Related Views")
				.click();
					
			
			try {
				SWTBotShell shell = workbenchBot.shell("Open Excalibur Views");
				shell.setFocus();
	
				workbenchBot.tree().select(0);
	
				workbenchBot.button(SWTBotSupport.BUTTON_OK).click();
				
				workbenchBot.waitUntil(shellCloses(shell));
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			assertTrue("View was not opened", workbenchBot.activeEditor().isActive());
	
			workbenchBot.activeEditor().saveAndClose();
		}
	}

	@Test
	public void tc_mv_03_canClickGefEditorElements() throws Exception {
		for (String project: projects) {
		
			SWTBotPreferences.PLAYBACK_DELAY = 0;
			SWTBotPreferences.TIMEOUT=50;
	
			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			specOut.setFocus();
			SWTBotTree tree = specOut.bot().tree();
	
			SWTBotTreeItem mDiag = tree.expandNode(project).expandNode(UIStrings.OUTLINE_ALL_VIEWS);
	
			//open first class diagram
			mDiag.getNode(0).expand().getNode(0).doubleClick();
	
			SWTBotSupport.waitForEndOfRunningJobs();
	
			SWTBotEditor test = workbenchBot.activeEditor();
			test.setFocus();
	
			SWTBotGefEditor testGef = gefBot.gefEditor(test.getTitle());
	
			for (SWTBotGefEditPart diagramEditPart : testGef.getSWTBotGefViewer()
					.rootEditPart().children()) {
				for (SWTBotGefEditPart editpart : diagramEditPart.children()) {
					SWTBotPreferences.PLAYBACK_DELAY = 100;
					editpart.doubleClick();
					editpart.resize(1, 10, 10);
					mDiag.getNode(0).doubleClick();
				}
			}
			
			SWTBotPreferences.TIMEOUT= 5000;
		}
	}

	@Test
	public void tc_mv_04_HiddenViewsTests() throws Exception {
		for (String project: projects) {

			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			specOut.setFocus();
			SWTBotTree specTree = specOut.bot().tree();
	
			//Hide 1 view using the Specification Outline
			SWTBotTreeItem viewNode = specTree.expandNode(project).expandNode(UIStrings.OUTLINE_ALL_VIEWS).getNode(0).getNode(1);
			String viewName = viewNode.getText();
			viewNode.select();
	
			new SWTBotMenu(ContextMenuHelper.contextMenu(specTree, "Document...", "Hide this Element's documentation")).click();
	
			
			//Focus on Report Outline, to check if the view was effectively hidden
			SWTBotView repOut = SWTBotSupport.getReportOutlineView();
			repOut.setFocus();
			SWTBotTree reportOutlineTree = repOut.bot().tree();
	
			SWTBotTreeItem viewsItem = reportOutlineTree.expandNode(project).expandNode("Views");
			SWTBotTreeItem documentedItem = null;
			SWTBotTreeItem hiddenDocumentedItem = null;
			
			boolean viewIsHidden = false;
			System.out.println("views children:" + viewsItem.getItems().length);
			for (SWTBotTreeItem item : viewsItem.getItems()) {
				if (item.getText().startsWith("Documented")) {
					System.out.println("documented found");
					documentedItem = item;
					documentedItem.expand();
					System.out.println("documented views children:" + documentedItem.getItems().length);
					for (SWTBotTreeItem item2 : documentedItem.getItems()) {
						if (item2.getText().startsWith("Hidden")) {
							System.out.println("hidden found");
							hiddenDocumentedItem = item2;
							hiddenDocumentedItem.expand();
							System.out.println("hidden documented views children:" + hiddenDocumentedItem.getItems().length);
							for (SWTBotTreeItem item3 : hiddenDocumentedItem.getItems()) {
								if (item3.getText().equals(viewName)) {
									viewIsHidden = true;
								}
							}						
						}
					}
				}
			}
			
			System.out.println("viewIsHidden? " + viewIsHidden);
			assertTrue("View was not hidden", viewIsHidden);
		}
	}

	@Test
	public void tc_mv_05_ViewsNameTest() throws Exception {
		for (String project: projects) {

			SWTBotView specificationOutlineView = SWTBotSupport.getSpecificationOutlineView();
			SWTBotView modelExplorerView =SWTBotSupport.getModelExplorerView();
			specificationOutlineView.setFocus();
	
			SWTBotTree specificationOutlineTree = specificationOutlineView.bot().tree();
	
			SWTBotTreeItem viewNode = specificationOutlineTree.expandNode(project).expandNode(UIStrings.OUTLINE_ALL_VIEWS);
	
			// check if not empty
			for (int i = 0; i < viewNode.getItems().length; i++) {
	
				assertTrue("View name is empty", !viewNode.getNode(i).getText().isEmpty());
				
			}
			
			modelExplorerView.setFocus();
			
			SWTBotTree modelExplorerTree = modelExplorerView.bot().tree();
			
			try {
				SWTBotTreeItem viewSpaceNode = modelExplorerTree.expandNode(project).expandNode("REPRESENTATION_AIRD_FILENAME").expandNode("msr-viewpoint");
				
				// check for spaces
				SWTBotTreeItem messirDiagramNode = viewSpaceNode.expandNode("Messir Diagram");
				for (SWTBotTreeItem childNode : messirDiagramNode.getItems()) {
					assertTrue("View name doesn't exist", childNode.getText() != null);
					assertTrue("View name contains spaces", !childNode.getText().contains(" "));
				}
	
				// check for spaces
				SWTBotTreeItem useCaseDiagramNode = viewSpaceNode.expandNode("Use_Case_Diagram");
				for (SWTBotTreeItem childNode : useCaseDiagramNode.getItems()) {
					assertTrue("View name doesn't exist", childNode.getText() != null);
					assertTrue("View name contains spaces", !childNode.getText().contains(" "));
				}
	
				// check for spaces
				SWTBotTreeItem testCaseInstanceDiagramNode = viewSpaceNode.expandNode("Test Case Instance Sequence Diagram");
				for (SWTBotTreeItem childNode : testCaseInstanceDiagramNode.getItems()) {
					assertTrue("View name doesn't exist", childNode.getText() != null);
					assertTrue("View name contains spaces", !childNode.getText().contains(" "));
				}
	
			} catch (Exception e) {
				System.out.println("No direct child folder \"documentation\" under project root.");
			}
		}
	}

	@Test
	public void tc_mv_06_CanCreateModelViewsWContextMenu() throws Exception {
		for (String project: projects) {
			SWTBotPreferences.PLAYBACK_DELAY = 100;
	
			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			specOut.setFocus();
			SWTBotTree specificationOutlineTree = specOut.bot().tree();
	
			specificationOutlineTree.expandNode(project)
				.expandNode(UIStrings.SPEC_ENVIRONMENT_MODEL)
				.getNode(2)
				.contextMenu("New...")
				.menu("View - From Selection")
				.click();
	
	//		new SWTBotMenu(ContextMenuHelper.contextMenu(specificationOutlineTree, "New...",
	//				"View - Type Dependency")).click();
	
		}
	}

	@Test
	public void tc_mv_07_CanDragAndDropViewElements() throws Exception {
		for (String project: projects) {
			String newViewName = "TestView";
	
			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			specOut.setFocus();
			SWTBotTree tree = specOut.bot().tree();
	
			tree.expandNode(project).getNode(UIStrings.OUTLINE_ALL_VIEWS).select();
	
			new SWTBotMenu(ContextMenuHelper.contextMenu(tree, "New...", "View")).click();
	
			SWTBotShell shell = workbenchBot.shell(DialogViewName.DIALOG_TITLE_NEW_VIEW);
			shell.setFocus();
			workbenchBot.waitUntil(shellIsActive(DialogViewName.DIALOG_TITLE_NEW_VIEW));
	
			workbenchBot.textWithLabel(DialogViewName.TEXT_LABEL_VIEW_NAME).setText(newViewName);
	
			workbenchBot.radioWithTooltip(DialogViewName.RADIO_BUTTON_VIEW_CATEGORY_ENVIRONMENT_MODEL_TEXT).click(); 
			
			workbenchBot.button(SWTBotSupport.BUTTON_OK).click();
			
			workbenchBot.waitUntil(shellCloses(shell));
	
			SWTBotSupport.waitForEndOfRunningJobs();
	
			if (workbenchBot.activeEditor().getTitle().contains(".msrd")) {
				workbenchBot.activeEditor().saveAndClose();
			} else {
				workbenchBot.activeEditor().save();
			}
	
			assertTrue("View was not created got this intead ->" + workbenchBot.activeEditor().getTitle(), workbenchBot.activeEditor().getTitle().contains("TestView"));
	
			SWTBotView modExp = SWTBotSupport.getModelExplorerView();
			modExp.setFocus();
			
			SWTBotTree modTree = modExp.bot().tree();
			
			
			SWTBotGefEditor viewEditor = gefBot.gefEditor("TestView");
			
			SWTBotTreeItem dragNode = modTree.expandNode(project)
											.expandNode("environment")
											.expandNode("environment.msr")
											.getNode(0).expand()
											.getNode("Environment Model").expand()
											.getNode(0).select();
			
			
			SWTBotGefViewer viewer = viewEditor.getSWTBotGefViewer();
			SWTBotGefFigureCanvas canvas = null;
			
			for (Field f : viewer.getClass().getDeclaredFields()){
				
				if("canvas".equals(f.getName())){
					f.setAccessible(true);
					try {
						canvas = (SWTBotGefFigureCanvas) f.get(viewer);
					}catch (IllegalAccessException e){
						e.printStackTrace();
					}catch (IllegalArgumentException e){
						e.printStackTrace();
					}
				}
				
			}
			
			Assert.assertNotNull(canvas);
			dragNode.dragAndDrop(canvas);
		}
	}

	@Test
	public void tc_rg_01_RepGenerationTest() throws Exception {
		for (String project: projects) {
			SWTBotView specificationOutline = SWTBotSupport.getSpecificationOutlineView();
			specificationOutline.setFocus();
	
			specificationOutline.bot().tree()
				.expandNode(project)
				.contextMenu(UIStrings.generate)
				.menu("Report")
				.click();
	
			SWTBotSupport.waitForEndOfRunningJobs();
	
			SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.REPORT_GENERATION_FINISHED,30000);
	
			SWTBotSupport.waitForEndOfRunningJobs();
			
			SWTBotTreeItem reportOutlineTreeItem = SWTBotSupport.getReportOutlineTree().expandNode(project + ".report");
	
			assertTrue("Report was not generated", reportOutlineTreeItem.getNode("report.pdf").isVisible());
		}
	}

	@Test
	public void tc_rg_002_AllImagesGenerationTest() {
		for (String project: projects) {
		
			SWTBotPreferences.PLAYBACK_DELAY=0;
		
			SWTBotView modExp = SWTBotSupport.getModelExplorerView();
			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			modExp.setFocus();
			SWTBotTree modTree = modExp.bot().tree();
			specOut.setFocus();
			SWTBotTree specTree = specOut.bot().tree();
			
			int viewsCount = specTree.expandNode(project, UIStrings.OUTLINE_ALL_VIEWS).getItems().length;
	
			specTree.select(project);
	
			new SWTBotMenu(ContextMenuHelper.contextMenu(specTree, UIStrings.generate, UIStrings.specOutAllImages)).click();
	
			SWTBotSupport.waitForEndOfRunningJobs();
			
			SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.IMAGES_GENERATION_FINISHED,30000);
	
			modExp.setFocus();
			SWTBotTreeItem nodeItem = modTree.expandNode(project + ".report", UIStrings.MODEXP_ALL_IMAGES, "all").select();

			int[] repImages = SWTBotSupport.elemCount(nodeItem);
	
			assertTrue("eps image generation failed", repImages[0] == viewsCount);
			assertTrue("svg image generation failed", repImages[1] == viewsCount);
			assertTrue("pdf image generation failed", repImages[2] == viewsCount);
			assertTrue("all-images.pdf was not created",modTree
					.expandNode(project + ".report", UIStrings.MODEXP_ALL_IMAGES, "merged")
					.getNode(0)
					.getText().equals("all-images.pdf"));

			SWTBotSupport.setEpsCount(0);
			SWTBotSupport.setPdfCount(0);
			SWTBotSupport.setSvgCount(0);
		}
	}

	@Test
	public void tc_rg_003_ReportImageGenerationTest() throws Exception {
		for (String project: projects) {
			
			SWTBotPreferences.PLAYBACK_DELAY = 0;
	
			SWTBotView modExp = SWTBotSupport.getModelExplorerView();
			modExp.setFocus();
	
			SWTBotTree modTree = modExp.bot().tree();
	
			SWTBotTreeItem nodeItem = modTree.expandNode(project + ".report", UIStrings.MODEXP_REPORT_IMAGES).select();
	
			int[] repImages = SWTBotSupport.elemCount(nodeItem);
	
			SWTBotView reportOutline = SWTBotSupport.getReportOutlineView();
			reportOutline.setFocus();
			
			SWTBotTree reportOutlineTree= reportOutline.bot().tree();
			
			int nbrOfDocumentedViews = 0;
	
			for (String viewsChildrenNodeLabel : reportOutlineTree.expandNode(project).expandNode("Views").getNodes()) {
				System.out.println(viewsChildrenNodeLabel);
				if (viewsChildrenNodeLabel.startsWith("Documented")) {
					String documented = viewsChildrenNodeLabel;
					System.out.println("split: " + viewsChildrenNodeLabel.split("\\("));
					String[] splits = documented.split("\\(");
					if (splits.length > 1) {
						String documentedValue = splits[1];
						documentedValue = documentedValue.substring(0, documentedValue.length() - 1);
						
						nbrOfDocumentedViews = Integer.valueOf(documentedValue).intValue();
					}
				}
			}
	
			//clunky
	//		int nbrOfDocumentedViews = Character.getNumericValue(documented.charAt(12));
	
			
			//asserts can fail if some views were hidden, 
			//TODO: check vs report outline views folder (extract number out of folder name)
			assertTrue("eps image generation failed", repImages[0] == nbrOfDocumentedViews);
			assertTrue("svg image generation failed", repImages[1] == nbrOfDocumentedViews);
			
			SWTBotSupport.setEpsCount(0);
			SWTBotSupport.setPdfCount(0);
			SWTBotSupport.setSvgCount(0);
		}
	}
	
	@Test
	public void tc_ui_01a_BrowseSpecificationOutline() throws Exception {
		for (String project: projects) {
	
			SWTBotPreferences.PLAYBACK_DELAY = 0;
	
			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			specOut.setFocus();
			SWTBotTree tree = specOut.bot().tree();
	
			SWTBotTreeItem treeRoot = tree.getTreeItem(project);
	
			SWTBotSupport.setLogical();
	
			SWTBotSupport.recursTree(treeRoot);
		}
	}
	
	@Test
	public void tc_ui_01c_BrowseReportOutline() throws Exception {
		for (String project: projects) {

			SWTBotPreferences.PLAYBACK_DELAY = 0;
	
			SWTBotView reportOutlineView = SWTBotSupport.getReportOutlineView();
			reportOutlineView.setFocus();
			SWTBotTree tree = reportOutlineView.bot().tree();
	
			SWTBotTreeItem treeRoot = tree.getTreeItem(project);
	
			SWTBotSupport.recursTree(treeRoot);
	
			while (SWTBotSupport.closeWindowDialog("Multiple Documententations for the selected element")) {
				System.out.println("window closed");
			}
		}
	}
	
	@Test
	public void tc_ui_01b_BrowseStructuralView() throws Exception {
		for (String project: projects) {
			SWTBotView specificationOutlineView = SWTBotSupport.getSpecificationOutlineView();
			SWTBotSupport.setStructuralLogicalViewSpecOutline(specificationOutlineView, ExcaliburSpecificationOutlineView.RADIO_BUTTON_TOOLTIP_STRUCTURAL_VIEW);
			
			// this test is dependent of tc_ui_01a
			
			SWTBotPreferences.PLAYBACK_DELAY = 0;
		
			SWTBotView specOutStructural = SWTBotSupport.getSpecificationOutlineView();
			specOutStructural.setFocus();
			SWTBotTree tree = specOutStructural.bot().tree();
			SWTBotTreeItem treeRoot = tree.getTreeItem(project);
			
			SWTBotSupport.recursTree(treeRoot);
		
			// set the view back to logical for possible following tests
			SWTBotSupport.setLogical();
		}
	}

	@Test
	public void tc_ui_02a_OpenAdditionalSpecViews() throws Exception {
//		for (String project: projects) {
			SWTBotView specOut = SWTBotSupport.getSpecificationOutlineView();
			specOut.setFocus();
		
			SWTBotView[] tempViews = new SWTBotView[3];
		
			for (int i = 0; i < tempViews.length; i++) {
		
				SWTBotToolbarButton toolbar = specOut.toolbarButton(UIStrings.newSpecOutline);
				toolbar.click();
		
				tempViews[i] = workbenchBot.activeView();
		
			}
		
			for (SWTBotView close : tempViews) {
				close.setFocus();
				close.close();
			}
//		}
	}

	@Test
	public void tc_ui_02b_OpenAdditionalRepViews() throws Exception {
//		for (String project: projects) {
			SWTBotView repOut = SWTBotSupport.getReportOutlineView();
			repOut.setFocus();
	
			SWTBotView[] tempViews = new SWTBotView[3];
	
			for (int i = 0; i < tempViews.length; i++) {
	
				SWTBotToolbarButton toolbar = repOut.toolbarButton(UIStrings.newReportOutline);
				toolbar.click();
	
				tempViews[i] = workbenchBot.activeView();
			}
	
			for (SWTBotView close : tempViews) {
				close.setFocus();
				close.close();
			}
//		}
	}

	@Before //Each test
	public void cleanWorkspace() throws Exception {
		SWTBotSupport.saveAll();
		SWTBotSupport.closeAll();
		
		try {
			SWTBotShell shellViews = workbenchBot.shell("Save");
			shellViews.activate();
			workbenchBot.button("Yes").click();
			workbenchBot.waitUntil(shellCloses(shellViews));

		} catch (WidgetNotFoundException e) {
			System.out.println("Nothing to Save");
		}

	}
	
	@After //Each tests
	public void resetWorkspace() throws Exception {
		workbenchBot.closeAllShells();
		workbenchBot.resetActivePerspective();
	
		SWTBotSupport.refreshSpecificationOutline();
		SWTBotSupport.refreshReportOutline();
		
		SWTBotSupport.waitForEndOfRunningJobs();
	}

	@AfterClass //All tests
	public static void afterClass() throws Exception {

	//	bot.sleep(2000);

	}

*/
}