/*******************************************************************************
 * Copyright (c) 2014-2015 University of Luxembourg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Philippe YOANN - initial API and implementation
 *     Benoît RIES - misc implementation
 ******************************************************************************/
package lu.uni.lassy.excalibur.tests.swtbot.utils;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withText;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredList;
import org.hamcrest.Matcher;

import lu.uni.lassy.excalibur.common.log.Log;
import lu.uni.lassy.excalibur.common.preferences.ExcaliburPreferences;
import lu.uni.lassy.excalibur.common.ui.ExcaliburUiConstantsMessageDialogTitle;
import lu.uni.lassy.excalibur.common.utils.OSUtils;
import lu.uni.lassy.excalibur.common.utils.WorkspaceUtils;
import lu.uni.lassy.excalibur.dsl.messir.messir.PackageDeclaration;
import lu.uni.lassy.excalibur.ui.outline.views.AbstractExcaliburOutlineView;
import lu.uni.lassy.excalibur.ui.outline.views.ExcaliburSingleOutlineView;
import lu.uni.lassy.excalibur.ui.outline.views.providers.label.ExcaliburOutlineLabelProvider;

public class SWTBotSupport implements UIStrings {

	private static SWTWorkbenchBot bot = new SWTWorkbenchBot();

	public static List<String> getAllSpecificationProjectNames() {
		List<String> allSpecificationProjectNames = new ArrayList<>();
		SWTBotView modelExplorerView = SWTBotSupport.getModelExplorerView();
		SWTBotTree modelExplorerTree = modelExplorerView.bot().tree();

		SWTBotTreeItem[] treeItems = modelExplorerTree.getAllItems();

		for (SWTBotTreeItem modelExplorerNode : treeItems) {
			//open Properties dialog for current model explorer node
			modelExplorerView.setFocus();
			modelExplorerTree.setFocus();
			if (!modelExplorerNode.getText().endsWith(".simulation")) {
				modelExplorerNode.contextMenu("Properties").click();
				SWTBotShell propertiesShell = waitForWindowDialog("Properties for " + modelExplorerNode.getText());
	
				//if the project selected in the model explorer is a specification project
				bot.tree().select(UIStrings.excalibur);
				if (SWTBotSupport.projectNature("specification")) {
					String nodeName = modelExplorerNode.getText();
					if (nodeName.startsWith("*")) {
						nodeName = nodeName.substring(1);
					}
					allSpecificationProjectNames.add(nodeName);
				}
				//close Properties dialog
				bot.captureScreenshot("screenshots/getAllSpecProjNames_" + modelExplorerNode.getText() + "1.png");
				propertiesShell.bot().button("OK").click();
				bot.captureScreenshot("screenshots/getAllSpecProjNames_" + modelExplorerNode.getText() + "2.png");
				bot.waitUntil(shellCloses(propertiesShell));
				waitForEndOfRunningJobs();
			}
		}
		return allSpecificationProjectNames;
	}

	public static void waitForEndOfRunningJobs() {
		boolean wasInterrupted = false;
		do {
			try {
//				System.out.println("XX JOBS =" + Job.getJobManager().find(null).length);
//				System.out.println("XX CHECKOUT JOBS =" + Job.getJobManager().find("Check Out").length);
				for (Job j: Job.getJobManager().find(null)) {
//					System.out.println("running job:"+j.getName());
					if (j.getName().equals("Check Out")
							|| j.getName().toLowerCase().contains("disconect project")
							|| j.getName().equals("Session saving")
							|| j.getName().toLowerCase().contains("disconecting project")
							|| j.getName().equals("Report Generation - Latex Files generation...")
							|| j.getName().equals("Running excalibur-docgen prolog...")
							|| j.getName().startsWith("Generating")) {
						j.join();
					}
				}
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_REFRESH, null);
				wasInterrupted = false;
			} catch (OperationCanceledException e) {
				System.out.println("OperationCanceledException");
			} catch (InterruptedException e) {
				wasInterrupted = true;
				System.out.println("InterruptedException");
			}
		} while (wasInterrupted);
	}

	// Asserts
	public static boolean isProjectCreated(String name) {
		try {
			SWTBotView modExp = getModelExplorerView();
			modExp.setFocus();
			SWTBotTree tree = modExp.bot().tree();
			tree.getTreeItem(name);
			return true;
		} catch (WidgetNotFoundException e) {
			return false;
		}
	}

	// works
	public static boolean projectNature(String type) {

	// try/catch block to avoid crash when trying to retrieve the string if the string is not present

		try {
			if (bot.widgets(
					withText("This is an Excalibur " + type + " project. \n"))
					.isEmpty()) {
				return false;
			}
			return true;
		} catch (Exception e) {

			return false;
		}

	}

	public static boolean isExcaliburProject() {

		if (bot.widgets(withText("This is not an Excalibur Project !"))
				.isEmpty()) {

			return false;
		}

		return true;

	}



	/**
	 * Refreshes the specification or the report outline
	 * 
	 * @param name
	 *            Specification -> refresh specification outline /Report ->
	 *            refresh report outline
	 */

	public static void collapseAllInOutlineView(String projectName) {
		SWTBotView outlineView = getExcaliburOutlineView(projectName);
		if (outlineView != null) {
			SWTBotToolbarButton toolbar = outlineView.toolbarButton(AbstractExcaliburOutlineView.TOOLBAR_BUTTON_COLLAPSE_ALL);
			toolbar.click();
		}
	}


	public static void refreshExcaliburOutlineView(SWTBotView view) {
		SWTBotToolbarButton toolbar = view.toolbarButton(ExcaliburSingleOutlineView.TOOLBAR_BUTTON_REFRESH_THIS_VIEW_TOOLTIP);
		toolbar.click();
		waitForEndOfRunningJobs();
	}

	public static void refreshExcaliburOutlineView(String projectName) {
		SWTBotView specOut = getExcaliburOutlineView(projectName);
		refreshExcaliburOutlineView(specOut);
	}

	public static void createNewExcaliburOutlineView(String projectName, String newProjectName) {
		SWTBotView specOut = getExcaliburOutlineView(projectName);
		SWTBotToolbarButton toolbar = specOut.toolbarButton(ExcaliburSingleOutlineView.TOOLBAR_BUTTON_CREATE_NEW_VIEW_TOOLTIP);
		toolbar.click();
		waitForEndOfRunningJobs();
		for (SWTBotView view : bot.views()) {
			IViewPart viewPart = view.getViewReference().getView(false);
//			System.out.println("Title:" + view.getTitle());
//			System.out.println("ViewPart:" + viewPart);
			if (view.getViewReference() != null && viewPart instanceof ExcaliburSingleOutlineView) {
				final ExcaliburSingleOutlineView excaliburSingleOutlineView = (ExcaliburSingleOutlineView) viewPart;
				if (excaliburSingleOutlineView.getExcaliburProject() == null) {
					view.setFocus();
					excaliburSingleOutlineView.setExcaliburProject(WorkspaceUtils.getProjectByName(newProjectName));
					if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
						Log.trace(AbstractExcaliburOutlineView.class, "NON-UI Thread");
						 PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
							 @Override
							public void run() {
								excaliburSingleOutlineView.updateViewerData(true);
							 }
						 });
					} else {
						Log.trace(AbstractExcaliburOutlineView.class, "UI Thread");
						excaliburSingleOutlineView.updateViewerData(true);
					}

				}
			}
		}
	}

	public static boolean isFileCreated(String projectName, String folderContainer, String fileName) {
		try {
			SWTBotView navigator = getNavigator();
			navigator.setFocus();
			SWTBotTree tree = navigator.bot().tree();
			tree.expandNode(projectName).expandNode(folderContainer).select(fileName);
			return true;
		} catch (WidgetNotFoundException e) {
			return false;
		}
	}

	public static void setLogical() {
		setExcaliburSpecificationOutlineViewsToLogicalMode();

	}

	public static void setStructural() {
		setExcaliburSpecificationOutlineViewsToStructuralMode();
	}

	public static void setExcaliburSpecificationOutlineViewsToLogicalMode() {
		setExcaliburSpecificationOutlineViews(true);
	}

	public static void setExcaliburSpecificationOutlineViewsToStructuralMode() {
		setExcaliburSpecificationOutlineViews(false);
	}

	private static void setExcaliburSpecificationOutlineViews(final boolean logical) {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
			Log.trace(AbstractExcaliburOutlineView.class, "NON-UI Thread");
			 PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				 @Override
				public void run() {
					 switchModeAllExcaliburSpecificationOutlineViewsInUIThread(logical);
				 }
			 });
		} else {
			Log.trace(AbstractExcaliburOutlineView.class, "UI Thread");
			switchModeAllExcaliburSpecificationOutlineViewsInUIThread(logical);
		}
	}
	
	protected static void switchModeAllExcaliburSpecificationOutlineViewsInUIThread(boolean logical) {
		for (IViewReference viewRef : WorkspaceUtils.getActivePage().getViewReferences()) {
			Log.trace(AbstractExcaliburOutlineView.class, "view:" + viewRef.getPartName());
			IViewPart viewPart = viewRef.getView(true);
			if (viewPart instanceof ExcaliburSingleOutlineView) {
				ExcaliburSingleOutlineView excaliburSingleOutlineView = (ExcaliburSingleOutlineView) viewPart;
				excaliburSingleOutlineView.getDisplayLogicalModeRadioButton().setSelection(logical);
				excaliburSingleOutlineView.getDisplayStructuralModeRadioButton().setSelection(!logical);
			}
		}
	}

	// Views
	public static SWTBotView getNavigator() {
		SWTBotView view = bot.viewByTitle("Navigator");
		view.setFocus();
		return view;
	}

	public static SWTBotView getModelExplorerView() {
		SWTBotView view = bot.viewByTitle("Model Explorer");
		view.setFocus();
		return view;
	}

	public static void showProgressView() {
		SWTBotView view = bot.viewByTitle("Progress");
		view.setFocus();
	}

	public static SWTBotTree getModelExplorerTree() {
		SWTBotView view = bot.viewByTitle("Model Explorer");
		view.setFocus();
		SWTBotTree tree = view.bot().tree();
		tree.setFocus();
		bot.sleep(1000);
		return tree;
	}

/*	public static SWTBotView getSpecificationOutlineView() {
		SWTBotView view = bot.viewById("lu.uni.lassy.excalibur.ui.views.outline.spec");
		view.setFocus();
		return view;
	}
*/
	public static SWTBotView getSVNRepositoriesView() {
		SWTBotView view = bot.viewById("org.eclipse.team.svn.ui.repository.RepositoriesView");
		view.setFocus();
		return view;
	}

	public static SWTBotView getExcaliburOutlineView(String projectName) {
		if (projectName.endsWith(ExcaliburPreferences.REPORT_PROJECT_SUFFIX)) {
			projectName = projectName.substring(0, projectName.lastIndexOf(ExcaliburPreferences.REPORT_PROJECT_SUFFIX));
		}
		if (projectName.endsWith(ExcaliburPreferences.SIMULATION_PROJECT_SUFFIX)) {
			projectName = projectName.substring(0, projectName.lastIndexOf(ExcaliburPreferences.SIMULATION_PROJECT_SUFFIX));
		}
		for (SWTBotView view : bot.views()) {
			IViewPart viewPart = view.getViewReference().getView(false);
//			System.out.println("Title:" + view.getTitle());
//			System.out.println("ViewPart:" + viewPart);
//			System.out.println("Looking for ProjectName: " + projectName);
			if (view.getViewReference() != null && viewPart instanceof ExcaliburSingleOutlineView) {
				ExcaliburSingleOutlineView excaliburSingleOutlineView = (ExcaliburSingleOutlineView) viewPart;
				if (projectName.equals(excaliburSingleOutlineView.getExcaliburProject().getName())) {
//					System.out.println("View found");
					view.setFocus();
					return view;
				}
			}
		}
		return null;
	}

	public static SWTBotTree getExcaliburOutlineTree(String projectName) {
		SWTBotView view = getExcaliburOutlineView(projectName);
		if (view != null) {
			SWTBotTree tree = view.bot().tree();
			tree.setFocus();
			bot.sleep(1000);
			return tree;
		}
		return null;
	}

/*	public static SWTBotTree getSpecificationOutlineTree() {
		SWTBotView view = bot.viewById("lu.uni.lassy.excalibur.ui.views.outline.spec");
		view.setFocus();
		SWTBotTree tree = view.bot().tree();
		tree.setFocus();
		bot.sleep(1000);
		return tree;
	}

	public static SWTBotView getReportOutlineView() {
		SWTBotView view = bot.viewById("lu.uni.lassy.excalibur.ui.views.outline.report");
		view.setFocus();
		return view;
	}

	public static SWTBotTree getReportOutlineTree() {
		SWTBotView view = bot.viewById("lu.uni.lassy.excalibur.ui.views.outline.report");
		view.setFocus();
		SWTBotTree tree = view.bot().tree();
		tree.setFocus();
		bot.sleep(500);
		return tree;
	}
*/
	public static void saveAll() {
		try {
			SWTBotMenu saveAllMenu = bot.menu("File").menu("Save All");
			if (saveAllMenu.isEnabled() && saveAllMenu.isVisible()) {
				System.out.println("File-Save All: click");
				saveAllMenu.click();
			}
		} catch (Exception e) {
			System.out.println("File-Save All: nothing todo");
		}
	}

	public static void closeAll() {
		try {
			SWTBotMenu closeAllMenu = bot.menu("File").menu("Close All");
			if (closeAllMenu.isEnabled() && closeAllMenu.isVisible()) {
				System.out.println("File-Close All: click");
				closeAllMenu.click();
			}
		} catch (Exception e) {
			System.out.println("File-Close All: nothing todo");
		}
	}

	/**
	 * Recursively traverses a given project tree folder and double-clicks on
	 * every elements
	 * 
	 * 
	 * @param nodeItem
	 *            name of the folder to be traversed
	 * @param not
	 *            element to be ignored (if needed)
	 */

	public static void recursTree(SWTBotTreeItem nodeItem) {
		for (SWTBotTreeItem treeItem: nodeItem.expand().getItems()) {
			assertTrue("Erroneous files", !treeItem.getText().equals("Erroneous Files"));
			assertTrue("LaTeX Erroneous files", !treeItem.getText().equals("All LaTeX Erroneous Files"));

			treeItem.doubleClick();
			recursTree(treeItem);
		}
	}
	
	/**
	 * Delete a selected folder (TreeItem)
	 * 
	 * @param nodeToDelete
	 * 			 folder to be deleted
	 */
	public static void deleteTreeItem(SWTBotTreeItem nodeToDelete){
		//select node to delete in tree
		nodeToDelete.select();

		//press delete key
		KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.DELETE);

		//handle delete resource dialog
		SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.DELETE_RESOURCES);

		//wait for background jobs
		SWTBotSupport.waitForEndOfRunningJobs();
	}

	// images counters
	static int epsCount = 0;
	static int svgCount = 0;
	static int pdfCount = 0;

	public static void setEpsCount(int epsCount) {
		SWTBotSupport.epsCount = epsCount;
	}

	public static void setSvgCount(int svgCount) {
		SWTBotSupport.svgCount = svgCount;
	}

	public static void setPdfCount(int pdfCount) {
		SWTBotSupport.pdfCount = pdfCount;
	}
	
	/**
	 * Recursively counts the three types of images used in excalibur images and report generation
	 * 
	 * @param nodeItem folder containing the images
	 * @return Number of each image types
	 */
	public static int[] elemCount(SWTBotTreeItem nodeItem) {

		SWTBotTreeItem exItem = nodeItem.expand();

		for (int i = 0; i < nodeItem.rowCount(); i++) {

			String node = nodeItem.getNode(i).select().getText();

			if (node.endsWith(".eps")) {

				epsCount = epsCount + 1;

			} else {

				if (node.endsWith(".svg")) {

					svgCount = svgCount + 1;

				} else {

					if (node.endsWith(".pdf")) {

						pdfCount = pdfCount + 1;

					}
				}

			}
			elemCount(exItem.getNode(i));

		}

		return new int[] { epsCount, svgCount, pdfCount };

	}

	public static SWTBotShell waitForWindowDialog(String windowTitle) {
		SWTBotShell shell = bot.shell(windowTitle).activate();
		bot.waitUntil(shellIsActive(windowTitle));
		return shell;
	}


	public static boolean waitForWindowDialogAndClose(String windowTitle, String buttonLabelToCloseDialog, int timeout) {
		bot.waitUntil(shellIsActive(windowTitle), timeout);
		return closeWindowDialog(windowTitle, buttonLabelToCloseDialog, timeout);
	}

	public static boolean waitForWindowDialogAndClose(String windowTitle, String buttonLabelToCloseDialog) {
		try {
			bot.waitUntil(shellIsActive(windowTitle));
		} catch (Exception e) {
			return false;
		}
		return closeWindowDialog(windowTitle, buttonLabelToCloseDialog);
	}

	public static boolean waitForWindowDialogAndClose(String windowTitle, int timeout) {
		bot.waitUntil(shellIsActive(windowTitle), timeout);
		return closeWindowDialog(windowTitle);
	}

	public static boolean waitForWindowDialogAndClose(String windowTitle) {
		bot.waitUntil(shellIsActive(windowTitle));
		return closeWindowDialog(windowTitle);
	}

	public static boolean closeWindowDialog(String windowTitle) {
		return closeWindowDialog(windowTitle, BUTTON_OK, 5000);
	}

	public static boolean closeWindowDialog(String windowTitle, int timeout) {
		return closeWindowDialog(windowTitle, BUTTON_OK, timeout);
	}
	
	public static boolean closeWindowDialog(String windowTitle, String buttonNameToClick) {
		return closeWindowDialog(windowTitle, buttonNameToClick, SWTBotPreferences.DEFAULT_POLL_DELAY);
	}
	
	public static boolean closeWindowDialog(String windowTitle, String buttonNameToClick, long timeout) {
		try {
			SWTBotShell redGenShell = bot.shell(windowTitle);
			redGenShell.setFocus();
			bot.button(buttonNameToClick).click();
			bot.waitUntil(shellCloses(redGenShell), timeout);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static void searchTextInSpecOutline(final SWTBotView view, final String searchText, final Keyboard keyboard) {
		Display.getDefault().syncExec(new Runnable() {
	        @Override
			public void run() {
				IWorkbenchPartSite site = view.getReference().getPart(true).getSite();
				if (site instanceof IViewSite) {
					IToolBarManager t = ((IViewSite) site).getActionBars().getToolBarManager();
					if (t instanceof ToolBarManager) {
						ToolBar toolbar = ((ToolBarManager)t).getControl();
						if (toolbar != null) {
							Control[] tablList = toolbar.getTabList();
							if (tablList != null && tablList.length > 0) {
								if (tablList[0] instanceof Text) {
									Text text = (Text) tablList[0];
									text.setFocus();
									text.setText(searchText);
								}
							}
						}
					}
				}
	        }
		});
		if (OSUtils.isMac())
			keyboard.pressShortcut(Keystrokes.LF);
		if (OSUtils.isUnix())
			keyboard.pressShortcut(Keystrokes.CR);
		if (OSUtils.isWindows())
			keyboard.pressShortcut(Keystrokes.CR);
		
	}
	
	public static void listAllWidgetsOfCurrentShell(final SWTBot botParam) {
		UIThreadRunnable.syncExec(new VoidResult() {
			@Override
			public void run() {
				Matcher<Widget> matcher = allOf(widgetOfType(Widget.class));
				System.out.println("XXXX " + botParam.widgets(matcher));
				System.out.println("XXXX " + botParam.widgets(matcher).size());
				for (Widget widget: botParam.widgets(matcher)) {
					System.out.println("XX "+ widget + ", " + widget.getData());
					if (widget instanceof FilteredList) {
						FilteredList filteredList = (FilteredList) widget;
						for (Control control : filteredList.getChildren()) {
							System.out.println("CONTROL-LIST "+ control);
						}
					}
					if (widget instanceof Table) {
						Table table = (Table) widget;
						for (Control control : table.getChildren()) {
							System.out.println("CONTROL-TABLE "+ control);
						}
						
					}
					if (widget instanceof Tree) {
						Tree tree = (Tree) widget;
						for (Control control : tree.getChildren()) {
							System.out.println("CONTROL-TREE"+ control);
						}
						
					}
				}
			}
			
		});
	}
	
/*	public static void selectCheckButton(final SWTBot botParam, final String radioLabel) {
		UIThreadRunnable.syncExec(new VoidResult() {
			@Override
			public void run() {
				Matcher<Widget> matcher = allOf(widgetOfType(Button.class), withStyle(SWT.CHECK, "SWT.CHECK"));
				Button b = (Button) botParam.widget(matcher);
				b.setSelection(true);
			}
			
		});
	}*/
	
	public static void setStructuralLogicalViewSpecOutline(final SWTBotView view, final String tooltip) {
		Display.getDefault().syncExec(new Runnable() {
	        @Override
			public void run() {
				IWorkbenchPartSite site = view.getReference().getPart(true).getSite();
				if (site instanceof IViewSite) {
					IToolBarManager t = ((IViewSite) site).getActionBars().getToolBarManager();
					if (t instanceof ToolBarManager) {
						ToolBar toolbar = ((ToolBarManager)t).getControl();
						if (toolbar != null) {
							Control[] tablList = toolbar.getChildren();
							if (tablList != null) {
								for (Control control: tablList) {
									if (control instanceof Button && tooltip.equals(control.getToolTipText())) {
										Button button = (Button) control;
										button.setFocus();
										button.setSelection(true);
									}
									if (control instanceof Button && !tooltip.equals(control.getToolTipText())) {
										Button button = (Button) control;
										button.setFocus();
										button.setSelection(false);
									}
								}
							}
						}
					}
				}
				if (view.getViewReference().getView(false) instanceof ExcaliburSingleOutlineView) {
					ExcaliburSingleOutlineView excaliburSingleOutlineView = (ExcaliburSingleOutlineView) view.getViewReference().getView(false);
					excaliburSingleOutlineView.updateViewerData(true);
				}
	        }
		});
		waitForEndOfRunningJobs();
	}
	
	public static void assertNodeDataInstanceOfPackageDeclaration(final SWTBotTreeItem item) {
		Display.getDefault().syncExec(new Runnable() {
	        @Override
			public void run() {
	     		System.out.println("DATA first node:" + item.widget.getData());
	    		
	    		assertTrue("In structual view children nodes of a project should be package declarations.",
	    			item.widget.getData() instanceof PackageDeclaration);
	        }
		});
	}

	//Eclipse Menus Label
	//file menu
	public static final String ECLIPSE_MENU_FILE = "File";
	public static final String ECLIPSE_MENU_FILE_NEW = "New";
	public static final String ECLIPSE_MENU_FILE_NEW_OTHER = "Other...";

	//Excalibur menu
	public static final String ECLIPSE_MENU_EXCALIBUR = "Excalibur";
	public static final String ECLIPSE_MENU_FILE_NEW_STANDARD_LIBRARIES_PROJECT = "Excalibur Standard Libraries Project";
	public static final String ECLIPSE_MENU_FILE_NEW_SPECIFICATION_PROJECT = "Specification Project";
	public static final String ECLIPSE_MENU_FILE_NEW_EXAMPLE_PROJECTS = "Example Projects";
	public static final String ECLIPSE_MENU_FILE_NEW_EXAMPLE_PROJECTS_HelloWorld = "Hello World Example Project";
	public static final String ECLIPSE_MENU_FILE_NEW_EXAMPLE_PROJECTS_ICrashMini = "iCrashMini Example Project";

	//Buttons Label
	public static final String BUTTON_NEXT = "Next >";
	public static final String BUTTON_OK = "OK";
	public static final String BUTTON_FINISH = "Finish";
	public static final String ECLIPSE_DIALOG_TITLE_NEW_FILE = "New";
	public static final String ECLIPSE_DIALOG_TITLE_FOLDER_SELECTION = "Folder Selection";
	public static final String ECLIPSE_NEW_FILE_DIALOG_TREE_MESSIR_DOC_FILE_MSRDMESSIR_FILE_MSR = "Messir File (.msr)";
	public static final String ECLIPSE_NEW_FILE_DIALOG_TREE_MESSIR_DOC_FILE_MSRD = "Messir Doc File (.msrd)";

/*	public static void typeLongText(SWTBotEclipseEditor editor, String longText) {
		System.out.println("LONG TEXT=" + longText);
		if (longText.split("(?<=.)(?=\\p{Lu})").length > 0) {
			System.out.println("contains an upperCase");
			for (String str : longText.split("(?<=.)(?=\\p{Lu})")) {
				System.out.println("SUBSTRING="+str);
				if (str.contains(".")) {
					System.out.println("substring contains a dot");
					for (String strsub: str.split("\\.", -2)) {
						System.out.println("SUBSUBSTRING="+strsub +".");
						editor.insertText(strsub);
						editor.insertText(".");
					}
					System.out.println("remove last dot");
					editor.pressShortcut(Keystrokes.LEFT);
					editor.pressShortcut(Keystrokes.DELETE);
				} else {
					System.out.println("NO DOT type this="+str);
					editor.insertText(str);
				}
			}
		} else if (longText.contains(".")) {
			System.out.println("DOT");
			for (String strsub: longText.split("\\.", -2)) {
				System.out.println("SUBSUBSTRING="+strsub +".");
				editor.insertText(strsub);
				editor.insertText(".");
			}
			System.out.println("remove last dot");
			editor.pressShortcut(Keystrokes.LEFT);
			editor.pressShortcut(Keystrokes.DELETE);
		} else {
			System.out.println("NO DOT NO UPPERCASE type this="+longText);
			editor.insertText(longText);
		}
	}*/

	public static String getChildNodeNameByNameSubstring(SWTBotTreeItem item, String nodeSubString) {
		for (String childrenNodeName: item.getNodes()) {
			if (childrenNodeName.contains(nodeSubString)) {
				return childrenNodeName;
			}
		}
		return null;
	}

	public static String getChildNodeNameByNameSubstring(SWTBotTree tree, String nodeSubString) {
		for (SWTBotTreeItem item: tree.getAllItems()) {
			String childrenNodeName = item.getText();
			if (childrenNodeName.contains(nodeSubString)) {
				return childrenNodeName;
			}
		}
		return null;
	}

	public static int getIndexOfByStringPrefix(SWTBotList list, String itemPrefix) {
		for (String childrenNodeName: list.getItems()) {
			if (childrenNodeName.startsWith(itemPrefix)) {
				return list.indexOf(childrenNodeName);
			}
		}
		return -1;
	}

	public static String from_MAC_FR_to_MAC_EN_US_Keyboard(String str) {
		String result = "";
		for (char ch: str.toCharArray()) {
			if (ch == 'a') {
				result += "q";
			} else if (ch == 'A') {
				result += "Q";
			} else if (ch == 'w') {
				result += "z";
			} else if (ch == 'W') {
				result += "Z";
			} else if (ch == 'm') {
				result += ",";
			} else if (ch == 'M') {
				result += "?";
			} else if (ch == '.') {
				result += ":";
			} else if (ch == ':') {
				result += ".";
			} else if (ch == ',') {
				result += ";";
			} else {
				result += ch;
			}
		}
		return result;
	}
	
/*	public static MenuItem contextMenu(
			final AbstractSWTBot<? extends Control> bot, final String... texts) {
		return UIThreadRunnable.syncExec(new WidgetResult<MenuItem>() {
			public MenuItem run() {
				MenuItem menuItem = null;
				Control control = bot.widget;

				// MenuDetectEvent added by Stefan Schaefer
				Event event = new Event();
				control.notifyListeners(SWT.MenuDetect, event);
				if (!event.doit) {
					return null;
				}

				Menu menu = control.getMenu();
				for (String text : texts) {
					@SuppressWarnings("unchecked")
					Matcher<?> matcher = allOf(instanceOf(MenuItem.class),
							withMnemonic(text));
					menuItem = show(menu, matcher);
					if (menuItem != null) {
						menu = menuItem.getMenu();
					} else {
						hide(menu);
						throw new WidgetNotFoundException(
								"Could not find menu: " + text); //$NON-NLS-1$
					}
				}

				return menuItem;
			}
		});
	}
	

	private static MenuItem show(final Menu menu, final Matcher<?> matcher) {
		if (menu != null) {
			menu.notifyListeners(SWT.Show, new Event());
			MenuItem[] items = menu.getItems();
			for (final MenuItem menuItem : items) {
				System.out.println("XYXYXY:="+menuItem.getText());
				if (matcher.matches(menuItem)) {
					return menuItem;
				}
			}
			menu.notifyListeners(SWT.Hide, new Event());
		}
		return null;
	}

	private static void hide(final Menu menu) {
		menu.notifyListeners(SWT.Hide, new Event());
		if (menu.getParentMenu() != null) {
			hide(menu.getParentMenu());
		}
	}*/
	
	public static void generateReport(String projectName, String copyFileName, boolean openReport) {
		if (!OSUtils.isUnix()) {
			//show Progress view
			SWTBotSupport.showProgressView();
	
			//call Full Report Generation in the Report Outline
			SWTBotTree outlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName);
			outlineTree
				.getTreeItem(projectName)
				.contextMenu("Generate...")
				.menu("Report")
				.click();
			
			SWTBotSupport.waitForEndOfRunningJobs();
			SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.REPORT_GENERATION_FINISHED, BUTTON_OK, 30000);
			SWTBotSupport.waitForEndOfRunningJobs();
			
			outlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName + ".report");
			outlineTree.expandNode(projectName + ".report");
	
			assertTrue("Report was not generated", outlineTree.expandNode(projectName + ".report").getNode("report.pdf").isVisible());
	
			//delete TWO TIMES the report.pdf file
			String reportFileToOpen = "report.pdf";
			//first time
			outlineTree.setFocus();
			outlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName + ".report");
			SWTBotSupport.deleteTreeItem(outlineTree.expandNode(projectName + ".report",reportFileToOpen));
			SWTBotSupport.refreshExcaliburOutlineView(projectName + ".report");
			//second time
			outlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName + ".report");
			SWTBotSupport.deleteTreeItem(outlineTree.expandNode(projectName + ".report",reportFileToOpen));
			SWTBotSupport.refreshExcaliburOutlineView(projectName + ".report");
	
			//copy the report.pdf file
			SWTBotTree modelExplorerTree = SWTBotSupport.getModelExplorerTree();
			modelExplorerTree
				.expandNode(projectName + ".report")
				.select("report.pdf");
	
			bot.menu("Edit").menu("Copy").click();
			bot.menu("Edit").menu("Paste").click();
	
			SWTBotShell shellNameConflict = SWTBotSupport.waitForWindowDialog("Name Conflict");
			shellNameConflict.bot().text().setText(copyFileName);
			shellNameConflict.bot().button("OK").click();
			bot.waitUntil(shellCloses(shellNameConflict));
			
			//double-click on report.pdf file
			if (openReport) {
				outlineTree.expandNode(projectName + ".report")
					.getNode(reportFileToOpen)
					.doubleClick();
			}
	
			//TODO How todo this assert ?
			//USE Acrobat PDF APIs todo a number of checks
		}
	}
	
	public static SWTBotEditor openMsrFileByPackageName(SWTWorkbenchBot workbenchBot, String projectName, String packageName, String expectedFileToBeOpened) {
		SWTBotView specificationOutlineViewStructural = SWTBotSupport.getExcaliburOutlineView(projectName);
		SWTBotSupport.setStructuralLogicalViewSpecOutline(specificationOutlineViewStructural, ExcaliburSingleOutlineView.RADIO_BUTTON_TOOLTIP_STRUCTURAL_VIEW);
		SWTBotTree tree = SWTBotSupport.getExcaliburOutlineTree(projectName);
		
		tree.expandNode(projectName)
			.expandNode(ExcaliburOutlineLabelProvider.LABEL_SPEC_NODE)
			.expandNode(packageName)
			.doubleClick();
		
		SWTBotEditor activeEditor = workbenchBot.activeEditor();
		activeEditor.setFocus();
		
		assertTrue("Messir specification file for element " +  packageName  + " was not opened.", activeEditor.getTitle().equals(expectedFileToBeOpened));
		
		return activeEditor;
	}
//
//Doesn't work color yellow is not backgroundColor and not foregroundColor either!
//
/*	public static int countYellowNodes(SWTBotTree tree) {
		return countYellowNodes(tree.getAllItems(), 0);
	}

	public static int countYellowNodes(SWTBotTreeItem[] items, int numberOfYellowNodes) {
		for (SWTBotTreeItem swtBotTreeItem: items) {
			//if color is yellow
				System.out.println("item=" + swtBotTreeItem.getText() + ", bkgColor: " + swtBotTreeItem.backgroundColor().getRGB().toString()+ ", foregroundColor: " + swtBotTreeItem.foregroundColor().getRGB().toString());
				if (swtBotTreeItem.backgroundColor().equals(Color.YELLOW)) {
					System.out.println("YELLOW");
					numberOfYellowNodes++;
				} else {
					System.out.println("NOT YELLOW");
				}
				if (swtBotTreeItem.getItems().length != 0) {
					countYellowNodes(swtBotTreeItem.getItems(), numberOfYellowNodes);
				}
		}
		return numberOfYellowNodes;
	}*/

	public static void saveAllEditors(SWTWorkbenchBot workbenchBot) {
		for (SWTBotEditor editor: workbenchBot.editors()) {
			editor.save();
		}
		//SWTBotSupport.refreshSpecificationOutline();
		SWTBotSupport.waitForEndOfRunningJobs();
	}

	public static void saveAllEditorsAndShowEditorByName(SWTWorkbenchBot workbenchBot, String editorName) {
		for (SWTBotEditor editor: workbenchBot.editors()) {
			if (editor.getTitle().equals(editorName)) {
				editor.show();
			}
			editor.save();
		}
		//SWTBotSupport.refreshSpecificationOutline();
		SWTBotSupport.waitForEndOfRunningJobs();
	}

	public static void saveActiveEditor(SWTBotEditor activeEditor) {
		activeEditor.save();
		SWTBotSupport.waitForEndOfRunningJobs();
	}

	public static SWTBotTreeItem getNodeItem(SWTBotView specificationOutlineView, String[] strs) {
		specificationOutlineView.setFocus();
		return specificationOutlineView.bot().tree().expandNode(strs);
	}

	public static void openSVNPerspective() {
		bot.menu("Window").menu("Open Perspective").menu("Other...").click();
		SWTBotShell openPerspectiveShell = bot.shell("Open Perspective").activate();
		bot.waitUntil(shellIsActive("Open Perspective"));
		bot.checkBox().select();
		bot.table().select("SVN Repository Exploring");
		bot.button(SWTBotSupport.BUTTON_OK).click();

		//additional dialog enable activities
		SWTBotShell confirmEnablementShell = bot.shell("Confirm Enablement").activate();
		bot.waitUntil(shellIsActive("Confirm Enablement"));
		confirmEnablementShell.bot().checkBox().select();
		confirmEnablementShell.bot().button("OK").click();
		bot.waitUntil(shellCloses(confirmEnablementShell));
		bot.waitUntil(shellCloses(openPerspectiveShell));	
	}

	public static SWTBotView getProblemsView() {
		SWTBotView view = bot.viewById("org.eclipse.ui.views.ProblemView");
		view.setFocus();
		return view;
	}

	public static void deleteResource(Keyboard keyboard, String projectName, String... nodePath) {
		//retrieve the resource
		getModelExplorerTree()
				.expandNode(projectName)
				.expandNode(nodePath)
				.select();
		
		//delete the resource
		keyboard.pressShortcut(Keystrokes.DELETE);
	
		waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.DELETE_RESOURCES, BUTTON_OK);
		WorkspaceUtils.refreshProject(WorkspaceUtils.getProjectByName(projectName));
		waitForEndOfRunningJobs();
		refreshExcaliburOutlineView(projectName);
		
		//CHECK that resource has been deleted
		try	{
			SWTBotSupport
				.getModelExplorerTree()
				.expandNode(projectName)
				.expandNode(nodePath);

			//should not reach this line
			assertTrue("Resource HAS BEEN found in Model Explorer, but should not have been, because it was deleted !", true);

		} catch (Exception e) {
			//good if it goes through here
		}
	}
	
	public static void deleteView(Keyboard keyboard, String projectName, String viewType, String viewName) {
		SWTBotTreeItem item = SWTBotSupport
				.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_ALL_VIEWS_NODE)
				.expandNode(viewType)
				.getNode(viewName);
		item.select();
		item.contextMenu("View...")
			.menu("Delete View")
			.click();
		
		SWTBotSupport.waitForWindowDialogAndClose(ExcaliburUiConstantsMessageDialogTitle.DELETE_RESOURCES, SWTBotSupport.BUTTON_OK);
		SWTBotSupport.waitForEndOfRunningJobs();
		SWTBotSupport.refreshExcaliburOutlineView(projectName);
		
		//CHECK that view has been deleted
		try	{
			SWTBotSupport
				.getExcaliburOutlineTree(projectName)
				.expandNode(projectName)
				.expandNode(ExcaliburOutlineLabelProvider.LABEL_ALL_VIEWS_NODE)
				.expandNode(viewType)
				.getNode(viewName);

			//should not reach this line
			assertTrue("View HAS BEEN found, but should not have been, because it was deleted !", true);

		} catch (Exception e) {
			//good if it goes through here
		}
	}
}
