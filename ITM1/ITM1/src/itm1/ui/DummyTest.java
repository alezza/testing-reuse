package itm1.ui;

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
	
	@Test
	public void firstTest() throws Exception {
		SWTBotMenu saveAllMenu = bot.menu("File").menu("Save All");

		assertTrue("OK", saveAllMenu.isVisible());
	}
}
