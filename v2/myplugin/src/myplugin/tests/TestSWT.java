package myplugin.tests;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import myplugin.views.TestDashboard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.IViewPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SWTBotJunit4ClassRunner.class)

public class TestSWT extends SWTBotTestCase{
	private static SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		workbenchBot = new SWTWorkbenchBot();
		workbenchBot.viewByTitle("Welcome").close();
	}
	
	@Before
	public static void before() throws Exception {
		System.out.println("Before");
	}
	
	@Test
	public void testCanCreateANewJavaProject() throws Exception {
		System.out.println("IN CanCreateANewJavaProject");

		try {
			SWTBotShell redGenShell = workbenchBot.shell("SICStus Prolog Path Not Configured");
			redGenShell.setFocus();
			bot.button("No").click();
			bot.waitUntil(shellCloses(redGenShell), 5000);
		} catch (Exception e) {
		}
		
		try {
			SWTBotShell redGenShell = bot.shell("What's New in Excalibur");
			redGenShell.setFocus();
			bot.button("OK").click();
			bot.waitUntil(shellCloses(redGenShell), 5000);
		} catch (Exception e) {
		}

		//Open menu Window, Open View..., Other . then Test Dashboard
		SWTBotMenu openViewMenu = bot.menu("Window").menu("Show View").menu("Other...");
		openViewMenu.click();
		
		SWTBotShell shell = bot.shell("Show View");
		shell.activate();
		shell.bot().tree().setFocus();
		shell.bot().tree().expandNode("Sample Category", true).select("Test Dashboard");
		shell.bot().button("OK").click();
	
		for (SWTBotView view : workbenchBot.views()) {
			IViewPart viewPart = view.getReference().getView(false);
			if (viewPart != null) {
				System.out.println("View: " + viewPart.getTitle());
			}
			if (view.getViewReference() != null && viewPart instanceof TestDashboard) {
				SWTBotCombo firstCombobox = workbenchBot.comboBox(0);
				SWTBotCombo secondCombobox = workbenchBot.comboBox(1);
				System.out.println("First Combo box");
				firstCombobox.setText("PluginTestJunit");
				for (String s :firstCombobox.items()) {
					System.out.println(s);
				}
				System.out.println("Second Combo box");
				for (String s :secondCombobox.items()) {
					System.out.println(s);
				}
				workbenchBot.button(0).click();
			}
		}
		
		// FIXME: assert that the project is actually created, for later
		assertTrue(true);
	}
	
	@After
	public static void sleepAfter() {
		workbenchBot.sleep(5000);
	}
 
 
	@AfterClass
	public static void sleep() {
		workbenchBot.sleep(5000);
	}
}
//IViewPart testDashboardviewPart = viewPart;
