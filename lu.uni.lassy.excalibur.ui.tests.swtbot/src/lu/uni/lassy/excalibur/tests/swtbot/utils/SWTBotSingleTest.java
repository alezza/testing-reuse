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

import lu.uni.lassy.excalibur.tests.swtbot.excalibur.ExcaliburStartup;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * this class is useful when you wish 
 * to execute a single test case 
 * (just copy/paste from you main test class)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SWTBotJunit4ClassRunner.class)
public class SWTBotSingleTest extends SWTBotTestCase {

	private static SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
//	private static SWTGefBot gefBot = new SWTGefBot();

	//test project selection
	public static String project = UIStrings.ICRASHMINI_PROJECT_NAME;

	// Opens eclipse and creates a new excalibur project
	@BeforeClass
	public static void beforeClass() throws Exception {

		SWTBotPreferences.KEYBOARD_STRATEGY = "org.ecplipse.swtbot.finder.keyboard.strategy";
		SWTBotPreferences.KEYBOARD_LAYOUT = "src.org.eclipse.swtbot.swt.finder.keyboard.MAC_EN_US";

		ExcaliburStartup.closeStartView(workbenchBot);

		ExcaliburStartup.setExcaliburSpecificationPerspective(workbenchBot);

		if (project.equals(UIStrings.newProject)) {
			ExcaliburStartup.newProject(workbenchBot);

		} else {
			//Startup.createSimulationRef(workbenchBot);
			ExcaliburStartup.createStandardLibraries(workbenchBot);
			ExcaliburStartup.newCaseStudyProject(workbenchBot, project);
		}

		assertTrue(UIStrings.STD_LIBS + " doesn't exist", SWTBotSupport.isProjectCreated(UIStrings.STD_LIBS));
		assertTrue(UIStrings.SIM_REF + " doesn't exist", SWTBotSupport.isProjectCreated(UIStrings.SIM_REF));

		assertTrue(project + " doesn't exist", SWTBotSupport.isProjectCreated(project));
		assertTrue(project + ".report doesn't exist", SWTBotSupport.isProjectCreated(project + ".report"));
		assertTrue(project + ".Simulation doesn't exist", SWTBotSupport.isProjectCreated(project + ".simulation"));
		
		ExcaliburStartup.setPreferenceMac(workbenchBot);
		ExcaliburStartup.setTexlipseMac(workbenchBot);

		SWTBotSupport.waitForEndOfRunningJobs();

	}

	@Test
	// Works
	// Can be used to browse any Project 
	public void tc_ui_01a_BrowseExcaliburOutline(String projectName) throws Exception {

		SWTBotPreferences.PLAYBACK_DELAY = 0;

		SWTBotView specOut = SWTBotSupport.getExcaliburOutlineView(projectName);
		specOut.setFocus();
		SWTBotTree tree = specOut.bot().tree();

		SWTBotTreeItem treeRoot = tree.getTreeItem(project);

		SWTBotSupport.setLogical();

		SWTBotSupport.recursTree(treeRoot);

	}
	
	@Before
	public void cleanWorkspace() throws Exception {
		SWTBotSupport.saveAll();
		
//		SWTBotSupport.refreshSpecificationOutline();
//		SWTBotSupport.refreshReportOutline();

	}

	@AfterClass
	public static void afterClass() throws Exception {
		workbenchBot.closeAllShells();
		workbenchBot.sleep(5000);

	}

}
