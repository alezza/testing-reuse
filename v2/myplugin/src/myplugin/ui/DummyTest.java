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
package myplugin.ui;

import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SWTBotJunit4ClassRunner.class)
public class DummyTest extends SWTBotTestCase {
	
//	private static SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
//	private final static Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
//	private static SWTGefBot gefBot = new SWTGefBot();
	
/*
	@BeforeClass //Initialization before all tests
	public static void beforeAllTests() {
		
	}

	@Before //performed before each test
	public void beforeTest() {
	
	}
	
	@After //performed after each test
	public void afterTest() {
	
	}*/
	
	@Test
	public void firstTest() throws Exception {
		SWTBotMenu saveAllMenu = bot.menu("File").menu("Save All");

		assertTrue("OK", saveAllMenu.isVisible());
	}
/*
	@AfterClass //After all tests have been executed
	public static void afterAllTests() {
		
	}
*/
}
////SWTBotSupport getExcaliburOutlineView
//generateReport/
//
//for (SWTBotView view: BOT.views()){
//	
//	if ()
//}