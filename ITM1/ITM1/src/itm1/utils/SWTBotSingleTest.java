package itm1.utils;

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

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
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

	public static String HELLOWORLD_PROJECT_NAME = "org.eclipse.examples.helloworld";

	//test project selection
	public static String project = HELLOWORLD_PROJECT_NAME;

	@BeforeClass
	public static void beforeClass() throws Exception {
		workbenchBot = new SWTWorkbenchBot();
		workbenchBot.viewByTitle("Welcome").close();
	}
	
	@After //performed after each test
	@Before //performed before each test
	public void cleanWorkspace() {
		SWTBotSupport.saveAll();
		SWTBotSupport.closeAll();
		try {
			SWTBotShell shellViews = workbenchBot.shell("Save");
			shellViews.activate();
			workbenchBot.button("Yes").click();
			workbenchBot.waitUntil(shellCloses(shellViews));
		} catch (Exception e) {
			System.out.println("Nothing to Save");
		}
	}
	
	@Test
	public void canCreateANewJavaProject() throws Exception {
		
		try {
			SWTBotShell redGenShell = workbenchBot.shell("SICStus Prolog Path Not Configured");
			redGenShell.setFocus();
			bot.button("No").click();
			bot.waitUntil(shellCloses(redGenShell), 5000);
		} catch (Exception e) {
		}
		
		try {
			SWTBotShell redGenShell = bot.shell("What's New in Excalibur?");
			redGenShell.setFocus();
			bot.button("OK").click();
			bot.waitUntil(shellCloses(redGenShell), 5000);
		} catch (Exception e) {
		}
		
		workbenchBot.resetWorkbench();

	}
}
