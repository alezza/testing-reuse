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
package lu.uni.lassy.excalibur.tests.swtbot.excalibur;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withStyle;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.junit.Assert.assertEquals;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.hamcrest.Matcher;

import lu.uni.lassy.excalibur.common.ui.ExcaliburUiConstantsMessageDialogTitle;
import lu.uni.lassy.excalibur.common.utils.OSUtils;
import lu.uni.lassy.excalibur.common.utils.WorkspaceUtils;
import lu.uni.lassy.excalibur.tests.swtbot.utils.SWTBotSupport;
import lu.uni.lassy.excalibur.tests.swtbot.utils.UIStrings;
import lu.uni.lassy.excalibur.ui.outline.actions.ReportUpdateMakeGlossariesAction;

public class ExcaliburStartup implements UIStrings {
	
	public static void closeStartView(SWTWorkbenchBot bot) {
		bot.viewByTitle("Welcome").close();
	}
	
	public static void closeSICStusNotConfigured() {
		try {
			SWTBotSupport.waitForWindowDialogAndClose("SICStus Prolog Path Not Configured", "No", 10*1000);
		} catch (Exception e) {
			System.out.println("No SICStus not configure window dialog at startup.");
		}
	}

	public static void closeWhatsNewDialogView() {
		SWTBotSupport.waitForWindowDialogAndClose("What's New in Excalibur", SWTBotSupport.BUTTON_OK, 50*1000);
	}

	public static void newProject(SWTWorkbenchBot bot) {
	
		bot.menu("File").menu("New").menu("Project...").click();

		SWTBotShell shellProj = bot.shell("New Project");
		shellProj.activate();
		bot.tree().expandNode(excalibur)
				.select("Excalibur Specification Project");

		bot.button("Next >").click();

		bot.textWithLabel("Project name:").setText(newProject);

		bot.button("Finish").click();

		shellProj = bot.activeShell();

		SWTBotShell latexShell = bot.shell(ExcaliburUiConstantsMessageDialogTitle.LATEX_PREFERENCES_MUST_BE_SET);
		latexShell.setFocus();
		bot.button("OK").click();

		shellProj.setFocus();

		bot.waitUntil(shellCloses(shellProj),500000);

	}
	
	//can be useful if something goes wrong during startup
	public static void setProgressView(SWTWorkbenchBot bot) {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell shellView = bot.shell("Show View");
		shellView.activate();
		bot.tree().expandNode("General").select("Progress");
		bot.button("OK").click();
	}

	public static void newCaseStudyProject(SWTWorkbenchBot bot, String name) {
		String menuExampleProject = "";
		if (name.equals(ICRASHMINI_PROJECT_NAME)) {
			menuExampleProject = "iCrashMini Example Project";
		} else if (name.equals(HELLOWORLD_PROJECT_NAME)) {
			menuExampleProject = "Hello World Example Project";
		} else if (name.equals(ICRASH_PROJECT_NAME)) {
			if (!SWTBotSupport.isProjectCreated(UIStrings.STD_LIBS)) {
				createStandardLibraries(bot);
			}
			checkoutICrashFromSVN(bot);
			return;
		}
		//call related menu new example project
		bot.menu("Excalibur").menu("New").menu("Example Projects").menu(menuExampleProject).click();
		SWTBotSupport.waitForWindowDialogAndClose("New Example Project", SWTBotSupport.BUTTON_FINISH, 5000);
		
		//refresh Specification Outline
	//	SWTBotSupport.refreshSpecificationOutline();
		SWTBotSupport.waitForEndOfRunningJobs();
	}

	public static void checkoutICrashFromSVN(SWTWorkbenchBot bot) {
		SWTBotSupport.openSVNPerspective();
		
		//add new Excalibur Repository
		bot.menu("File").menu("New").menu("Repository Location").click();
		SWTBotShell shellNewRepositoryLocation= bot.shell("New Repository Location").activate();
		bot.waitUntil(shellIsActive("New Repository Location"));
		@SuppressWarnings("unchecked")
		Matcher<Widget> matcher = allOf(widgetOfType(Combo.class), withStyle(SWT.DROP_DOWN, "SWT.DROP_DOWN"));
		Widget widgetURL = shellNewRepositoryLocation.bot().widget(matcher,0);
		Widget widgetUser = shellNewRepositoryLocation.bot().widget(matcher,1);
		if (widgetURL instanceof Combo) {
			final Combo comboURL = (Combo) widgetURL;
			final Combo comboUser = (Combo) widgetUser;
			UIThreadRunnable.syncExec(new VoidResult() {
				@Override
				public void run() {
					comboURL.setText("https://hera.uni.lu/svn/excalibur/tags/casestudies/iCrash/latest");
					comboUser.setText("anonymous");
				}
			});
		}
		shellNewRepositoryLocation.bot().button(SWTBotSupport.BUTTON_FINISH).click();
		bot.waitUntil(shellCloses(shellNewRepositoryLocation));
		
		//check out iCrash project
		SWTBotView viewSVNRepositories = SWTBotSupport.getSVNRepositoriesView();
		SWTBotTreeItem item = viewSVNRepositories.bot().tree().expandNode("https://hera.uni.lu/svn/excalibur/tags/casestudies/iCrash/latest");
		viewSVNRepositories.bot().sleep(2000);
		
		//checkout specification project
		String icrashNodeName = SWTBotSupport.getChildNodeNameByNameSubstring(item, "lu.uni.lassy.excalibur.examples.icrash ");
		item.getNode(icrashNodeName)
			.contextMenu("Check Out")
			.click();
		SWTBotSupport.waitForEndOfRunningJobs();
		
		//checkout report project
		String reportNodeName = SWTBotSupport.getChildNodeNameByNameSubstring(item, "lu.uni.lassy.excalibur.examples.icrash.report");
		item.getNode(reportNodeName)
			.contextMenu("Check Out")
			.click();
		SWTBotSupport.waitForEndOfRunningJobs();

		//checkout simulation project
		String simulationNodeName = SWTBotSupport.getChildNodeNameByNameSubstring(item, "lu.uni.lassy.excalibur.examples.icrash.simulation");
		item.getNode(simulationNodeName)
			.contextMenu("Check Out")
			.click();
		bot.sleep(1000);
		SWTBotSupport.waitForEndOfRunningJobs();
		
		//open Excalibur Specification perspective
		ExcaliburStartup.setExcaliburSpecificationPerspective(bot);
		
		//disconnect specification project from SVN
		SWTBotTree modelExplorerTree = SWTBotSupport.getModelExplorerTree();
		String icrashNodeNameInME = SWTBotSupport.getChildNodeNameByNameSubstring(modelExplorerTree, "lu.uni.lassy.excalibur.examples.icrash ");
		modelExplorerTree.getTreeItem(icrashNodeNameInME)
			.contextMenu("Team")
			.menu("Disconnect")
			.click();
		
		clickYesOnConfirmDisconnectSVN(bot);
		
		//disconnect specification project from SVN
		modelExplorerTree = SWTBotSupport.getModelExplorerTree();
		String icrashReportNodeNameInME = SWTBotSupport.getChildNodeNameByNameSubstring(modelExplorerTree, "lu.uni.lassy.excalibur.examples.icrash.report");
		modelExplorerTree.getTreeItem(icrashReportNodeNameInME)
			.contextMenu("Team")
			.menu("Disconnect")
			.click();
		
		clickYesOnConfirmDisconnectSVN(bot);
		
		//disconnect specification project from SVN
		modelExplorerTree = SWTBotSupport.getModelExplorerTree();
		String icrashSimulationNodeNameInME = SWTBotSupport.getChildNodeNameByNameSubstring(modelExplorerTree, "lu.uni.lassy.excalibur.examples.icrash.simulation");
		modelExplorerTree.getTreeItem(icrashSimulationNodeNameInME)
			.contextMenu("Team")
			.menu("Disconnect")
			.click();
		
		clickYesOnConfirmDisconnectSVN(bot);
		
		SWTBotSupport.waitForEndOfRunningJobs();

	}
	
	public static void clickYesOnConfirmDisconnectSVN(SWTBot bot) {
		//additional dialog 
		SWTBotShell confirmDisconnectFromSVN = SWTBotSupport.waitForWindowDialog("Confirm Disconnect from SVN");
		Matcher<Widget> matcherRadioButtons = allOf(widgetOfType(Button.class), withStyle(SWT.RADIO, "SWT.RADIO"));
		Widget widgetAlsoDeleteSVNResources = confirmDisconnectFromSVN.bot().widget(matcherRadioButtons,0);
		Widget widgetDontDeleteSVNResources = confirmDisconnectFromSVN.bot().widget(matcherRadioButtons,1);
		if (widgetAlsoDeleteSVNResources instanceof Button
				&& widgetDontDeleteSVNResources instanceof Button) {
			final Button buttonAlso = (Button) widgetAlsoDeleteSVNResources;
			final Button buttonDont = (Button) widgetDontDeleteSVNResources;
			UIThreadRunnable.syncExec(new VoidResult() {
				@Override
				public void run() {
					buttonAlso.setSelection(true);
					buttonDont.setSelection(false);
				}
			});
		}
		confirmDisconnectFromSVN.bot().button("Yes").click();
		bot.waitUntil(shellCloses(confirmDisconnectFromSVN));
	}

	public static void createStandardLibraries(SWTWorkbenchBot bot) {
		SWTBotMenu menu = bot.menu("Excalibur").menu("New").menu("Excalibur Standard Libraries Project");
		if (menu.isEnabled()) {
			menu.click();
		}
		bot.captureScreenshot("screenshots/create_STD_LIBS.png");
		SWTBotSupport.waitForWindowDialogAndClose("New Standard Libraries Project", SWTBotSupport.BUTTON_FINISH);
		SWTBotSupport.waitForEndOfRunningJobs();
	}

	public static void createSimulationRef(SWTWorkbenchBot bot) {
//		bot.menu("File").menu("New").menu("Other...").click();
		KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.COMMAND, KeyStroke.getInstance('N'));

		SWTBotShell shellSimRef = bot.shell("New").activate();
		bot.tree().expandNode(excalibur).select("Excalibur Simulation Reference Project");
		bot.button("Next >").click();
		bot.activeShell().setFocus();
		bot.button("Finish").click();

		shellSimRef = bot.activeShell();
		bot.waitUntil(shellCloses(shellSimRef),500000);
	}

	public static void setExcaliburSpecificationPerspective(SWTWorkbenchBot bot) {
		bot.menu("Window").menu("Open Perspective").menu("Other...").click();
		SWTBotShell shellPersp = bot.shell("Open Perspective");
		//SWTBotTable table = new SWTBotTable(bot.widget(widgetOfType(Table.class), shellPersp.widget));
		bot.table().select("Specification");

		bot.button("OK").click();
		bot.waitUntil(shellCloses(shellPersp), 5000);
	}

	public static void setPreferenceWin(SWTWorkbenchBot bot) {
		bot.menu("Window").menu("Preferences").click();

		SWTBotShell shellProj = bot.shell("Preferences").activate();
		bot.tree().expandNode(excalibur).select("Generation");

		/*bot.button("Browse...", 0).click();
		bot.sleep(2000);
		Matcher<Widget> matcher = allOf(widgetOfType(Combo.class), withStyle(SWT.DROP_DOWN, "SWT.DROP_DOWN"));
		System.out.println("SIZE: " +bot.widgets(matcher).size());
		for (Widget widget: bot.widgets(matcher)) {
			System.out.println(widget.getData());
		}
		Widget widgetURL = bot.widget(matcher,1);
		if (widgetURL instanceof Combo) {
			final Combo comboURL = (Combo) widgetURL;
			UIThreadRunnable.syncExec(new VoidResult() {
				@Override
				public void run() {
					comboURL.setText(prefInkscapeBinWin);
				}
			});
		}
		bot.textWithLabel("File name:").setText(prefInkscapeBinWin);
		bot.button("Open").click();
		bot.sleep(2000);
		//bot.waitUntil(shellCloses(shellOpen));*/
		bot.textWithLabel("Inkscape bin path :").setText(prefInkscapeBinWin);
		assertEquals(bot.textWithLabel("Inkscape bin path :").getText(),prefInkscapeBinWin);

		//PDFTk
		/*bot.button("Browse...", 1).click();
		bot.sleep(2000);
		System.out.println("SIZE: " +bot.widgets(matcher));
		for (Widget widget: bot.widgets(matcher)) {
			System.out.println(widget.getData());
		}
		Widget widgetPDFTk = bot.widget(matcher,1);
		if (widgetURL instanceof Combo) {
			final Combo comboPDFTK = (Combo) widgetPDFTk;
			UIThreadRunnable.syncExec(new VoidResult() {
				@Override
				public void run() {
					comboPDFTK.setText(prefPdftkBinWin);
				}
			});
		}
		//bot.textWithLabel("File name:").setText(prefInkscapeBinWin);
		bot.button("Open").click();
		bot.sleep(2000);
		//bot.waitUntil(shellCloses(shellOpen));*/
		
		bot.textWithLabel("PDFtk bin path :").setText(prefPdftkBinWin);
		assertEquals(bot.textWithLabel("PDFtk bin path :").getText(),prefPdftkBinWin);
bot.sleep(10000);
		bot.button("OK").click();
		bot.waitUntil(shellCloses(shellProj));
	}
	
	public static void setPreferenceLinux(SWTWorkbenchBot bot) {
		bot.menu("Window").menu("Preferences").click();

		SWTBotShell shellProj = bot.shell("Preferences").activate();
		bot.tree().expandNode(excalibur).select("Generation");

		/*bot.button("Browse...", 0).click();
		bot.sleep(2000);
		Matcher<Widget> matcher = allOf(widgetOfType(Combo.class), withStyle(SWT.DROP_DOWN, "SWT.DROP_DOWN"));
		System.out.println("SIZE: " +bot.widgets(matcher).size());
		for (Widget widget: bot.widgets(matcher)) {
			System.out.println(widget.getData());
		}
		Widget widgetURL = bot.widget(matcher,1);
		if (widgetURL instanceof Combo) {
			final Combo comboURL = (Combo) widgetURL;
			UIThreadRunnable.syncExec(new VoidResult() {
				@Override
				public void run() {
					comboURL.setText(prefInkscapeBinWin);
				}
			});
		}
		bot.textWithLabel("File name:").setText(prefInkscapeBinWin);
		bot.button("Open").click();
		bot.sleep(2000);
		//bot.waitUntil(shellCloses(shellOpen));*/
		bot.textWithLabel("Inkscape bin path :").setText(prefInkscapeBinLinux);
		assertEquals(bot.textWithLabel("Inkscape bin path :").getText(),prefInkscapeBinLinux);

		//PDFTk
		/*bot.button("Browse...", 1).click();
		bot.sleep(2000);
		System.out.println("SIZE: " +bot.widgets(matcher));
		for (Widget widget: bot.widgets(matcher)) {
			System.out.println(widget.getData());
		}
		Widget widgetPDFTk = bot.widget(matcher,1);
		if (widgetURL instanceof Combo) {
			final Combo comboPDFTK = (Combo) widgetPDFTk;
			UIThreadRunnable.syncExec(new VoidResult() {
				@Override
				public void run() {
					comboPDFTK.setText(prefPdftkBinWin);
				}
			});
		}
		//bot.textWithLabel("File name:").setText(prefInkscapeBinWin);
		bot.button("Open").click();
		bot.sleep(2000);
		//bot.waitUntil(shellCloses(shellOpen));*/
		
		bot.textWithLabel("PDFtk bin path :").setText(prefPdftkBinLinux);
		assertEquals(bot.textWithLabel("PDFtk bin path :").getText(),prefPdftkBinLinux);
bot.sleep(10000);
		bot.button("OK").click();
		bot.waitUntil(shellCloses(shellProj));
	}
	
	public static void setPreferenceShowLineNumbersMac(SWTWorkbenchBot bot) {
		openEclipsePreferencesDialogWindowForMac();
		
		//doesn't always work 
		//KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.COMMAND, KeyStroke.getInstance(','));
		
		bot.waitUntil(shellIsActive("Preferences"));
		SWTBotShell shellProj = bot.shell("Preferences").activate();
		bot.tree()
			.expandNode("General")
			.expandNode("Editors")
			.expandNode("Text Editors")
			.click();
		bot.radioWithLabel("Show line numbers").click();
		bot.button("OK").click();
		bot.waitUntil(shellCloses(shellProj));
	}
	
	
	public static void setKeyboardPreferenceMac() {
		SWTBotPreferences.KEYBOARD_STRATEGY = "org.eclipse.swtbot.swt.finder.keyboard.SWTKeyboardStrategy";
		SWTBotPreferences.KEYBOARD_LAYOUT = "src.org.eclipse.swtbot.swt.finder.keyboard.FR_FR";
	}
	
	public static void setPreferenceMac(SWTWorkbenchBot bot) {
		//set keyboard preferences
		setKeyboardPreferenceMac();

		//SWTBotPreferences.KEYBOARD_STRATEGY = "org.ecplipse.swtbot.finder.keyboard.strategy";
		//SWTBotPreferences.KEYBOARD_LAYOUT = "src.org.eclipse.swtbot.swt.finder.keyboard.MAC_EN_US";
		//SWTBotPreferences.KEYBOARD_LAYOUT = "MAC_EN_US";
		//SWTBotPreferences.KEYBOARD_LAYOUT = "MAC_FR_FR";

		openEclipsePreferencesDialogWindowForMac();

		//set generation path
		bot.waitUntil(shellIsActive("Preferences"));
		SWTBotShell shellProj = bot.shell("Preferences");
		shellProj.activate();
		bot.tree().expandNode(excalibur).select("Generation");

		//Inkscape path
		bot.textWithLabel("Inkscape bin path :").setText(prefInkscapeBinMac);
		assertEquals(bot.textWithLabel("Inkscape bin path :").getText(), prefInkscapeBinMac);
		
		//PDFtk path
		bot.textWithLabel("PDFtk bin path :").setText(prefPdftkBinMac);
		assertEquals(bot.textWithLabel("PDFtk bin path :").getText(), prefPdftkBinMac);

		//close preferences
		bot.button(SWTBotSupport.BUTTON_OK).click();
		bot.waitUntil(shellCloses(shellProj));
	}

	private static void openEclipsePreferencesDialogWindowForMac() {
		//the usual SWTBot call doesn't work for the Mac System menu 
		//bot.menu("Eclipse").menu("Preferences...").click();
		//so use the following...
		final IWorkbench workbench = WorkspaceUtils.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow window = WorkspaceUtils.getActiveWindow();
				if (window != null) {
					Menu appMenu = workbench.getDisplay().getSystemMenu();
					for (MenuItem item : appMenu.getItems()) {
						if (item.getText().startsWith("Preferences")) {
							item.setSelection(true);
							item.notifyListeners(SWT.Selection, createEvent(item, workbench.getDisplay()));
							break;
						}
					}
				}
			}
		});
	}

	public static void setTexlipseMac(SWTWorkbenchBot bot) {
		//open preferences window 
		openEclipsePreferencesDialogWindowForMac();
		SWTBotShell shellProj = bot.shell("Preferences").activate();
		bot.tree().expandNode("Texlipse").select("Builder Settings");

		//texlipse bin directory preference
		bot.textWithLabel("Bin directory of TeX distribution:").setText(prefTexLiveMac);
		assertEquals(bot.textWithLabel("Bin directory of TeX distribution:").getText(), prefTexLiveMac);
		bot.button("Apply").click();

		//setup dvipdf 
		bot.list().select(SWTBotSupport.getIndexOfByStringPrefix(bot.list(), "Dvipdf program"));
		bot.button("Edit...").click();
		bot.textWithLabel("Program file:").setText(prefDviPdfMac);
		bot.button("OK").click();
		bot.sleep(1000);
		
		//setup ps2pdf 
		bot.list().select(SWTBotSupport.getIndexOfByStringPrefix(bot.list(), "Ps2pdf program"));
		bot.button("Edit...").click();
		bot.textWithLabel("Program file:").setText(prefPs2PdfMac);
		bot.button("OK").click();
		bot.sleep(1000);

		//close Preferences
		bot.button("OK").click();
		bot.waitUntil(shellCloses(shellProj));
	}
	
	public static void setTexlipseWin(SWTWorkbenchBot bot) {
		//open preferences window 
		bot.menu("Window").menu("Preferences").click();
		SWTBotShell shellPreferences = SWTBotSupport.waitForWindowDialog("Preferences");
		shellPreferences.bot().tree().expandNode("Texlipse").select("Builder Settings");

		//texlipse bin directory preference
		shellPreferences.bot().textWithLabel("Bin directory of TeX distribution:").setText(prefTexLiveMac);
		assertEquals(shellPreferences.bot().textWithLabel("Bin directory of TeX distribution:").getText(), prefTexLiveMac);
		shellPreferences.bot().button("Apply").click();

/*		//setup dvipdf 
		bot.list().select(SWTBotSupport.getIndexOfByStringPrefix(bot.list(), "Dvipdf program"));
		bot.button("Edit...").click();
		bot.textWithLabel("Program file:").setText(prefDviPdfMac);
		bot.button("OK").click();
		bot.sleep(1000);
		
		//setup ps2pdf 
		bot.list().select(SWTBotSupport.getIndexOfByStringPrefix(bot.list(), "Ps2pdf program"));
		bot.button("Edit...").click();
		bot.textWithLabel("Program file:").setText(prefPs2PdfMac);
		bot.button("OK").click();
		bot.sleep(1000);*/

		//close Preferences
		shellPreferences.bot().button("OK").click();
		bot.waitUntil(shellCloses(shellPreferences));
	}

	public static void setTexlipseLinux(SWTWorkbenchBot bot) {
		//open preferences window 
		bot.menu("Window").menu("Preferences").click();
		SWTBotShell shellPreferences = SWTBotSupport.waitForWindowDialog("Preferences");
		shellPreferences.bot().tree().expandNode("Texlipse").select("Builder Settings");

		//texlipse bin directory preference
		shellPreferences.bot().textWithLabel("Bin directory of TeX distribution:").setText(prefTexLiveLinux);
		assertEquals(shellPreferences.bot().textWithLabel("Bin directory of TeX distribution:").getText(), prefTexLiveLinux);
		shellPreferences.bot().button("Apply").click();

/*		//setup dvipdf 
		bot.list().select(SWTBotSupport.getIndexOfByStringPrefix(bot.list(), "Dvipdf program"));
		bot.button("Edit...").click();
		bot.textWithLabel("Program file:").setText(prefDviPdfMac);
		bot.button("OK").click();
		bot.sleep(1000);
		
		//setup ps2pdf 
		bot.list().select(SWTBotSupport.getIndexOfByStringPrefix(bot.list(), "Ps2pdf program"));
		bot.button("Edit...").click();
		bot.textWithLabel("Program file:").setText(prefPs2PdfMac);
		bot.button("OK").click();
		bot.sleep(1000);*/

		//close Preferences
		shellPreferences.bot().button("OK").click();
		bot.waitUntil(shellCloses(shellPreferences));
	}
	
	static Event createEvent(Widget widget, Display display) {
		Event event = new Event();
		event.time = (int) System.currentTimeMillis();
		event.widget = widget;
		event.display = display;
		return event;
	}

	public static void setMakeGlossariesBuilder(SWTWorkbenchBot bot, String projectName) {
		if (!OSUtils.isUnix()) {
			SWTBotTree reportOutlineTree = SWTBotSupport.getExcaliburOutlineTree(projectName);
			reportOutlineTree
				.getTreeItem(projectName)
				.contextMenu("Configure...")
				.menu("Update MakeGlossaries Builder")
				.click();
			
			SWTBotSupport.waitForWindowDialogAndClose(ReportUpdateMakeGlossariesAction.DIALOG_TITLE_MAKE_GLOSSARIES_UPDATED, SWTBotSupport.BUTTON_OK, 5000);
			SWTBotSupport.waitForEndOfRunningJobs();
			
			reportOutlineTree
			.getTreeItem(projectName)
			.contextMenu("Properties")
			.click();
	
			SWTBotShell shellProperties = bot.shell("Properties for " + projectName).activate();
			bot.waitUntil(shellIsActive("Properties for " + projectName));
			shellProperties.bot().tree().select("Builders");
			
			bot.table().getTableItem("makeGlossariesBuilderConfiguration").select();
			bot.captureScreenshot("tutorial_long/3.2.a_1.png");
	
			bot.button("Edit...").click();
			SWTBotSupport.waitForWindowDialogAndClose("Edit Configuration", SWTBotSupport.BUTTON_OK, 1000);
			bot.button(SWTBotSupport.BUTTON_OK).click();
			bot.waitUntil(shellCloses(shellProperties));
		}
	}

}
