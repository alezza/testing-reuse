package itm1.utils;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.dialogs.FilteredList;
import org.hamcrest.Matcher;


public class SWTBotSupport {

	private static SWTWorkbenchBot bot = new SWTWorkbenchBot();

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
	

	//Eclipse Menus Label
	//file menu
	public static final String ECLIPSE_MENU_FILE = "File";
	public static final String ECLIPSE_MENU_FILE_NEW = "New";
	public static final String ECLIPSE_MENU_FILE_NEW_OTHER = "Other...";

	//Buttons Label
	public static final String BUTTON_NEXT = "Next >";
	public static final String BUTTON_OK = "OK";
	public static final String BUTTON_FINISH = "Finish";
	public static final String ECLIPSE_DIALOG_TITLE_NEW_FILE = "New";
	public static final String ECLIPSE_DIALOG_TITLE_FOLDER_SELECTION = "Folder Selection";
	public static final String ECLIPSE_NEW_FILE_DIALOG_TREE_MESSIR_DOC_FILE_MSRDMESSIR_FILE_MSR = "Messir File (.msr)";
	public static final String ECLIPSE_NEW_FILE_DIALOG_TREE_MESSIR_DOC_FILE_MSRD = "Messir Doc File (.msrd)";

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


}
