package itm1.ui.handlers;

import java.util.Enumeration;

import junit.framework.TestFailure;
import junit.framework.TestResult;
import itm1.utils.SWTBotSingleTest;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class SWTBotTestHandler extends AbstractHandler implements IHandler {

	/**
	 * The constructor.
	 */
	public SWTBotTestHandler() {}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			SWTBotSingleTest swtBot = new SWTBotSingleTest();
			TestResult testResult = swtBot.run();
			Enumeration<TestFailure> en = testResult.failures();
			while (en.hasMoreElements()) {
				TestFailure failure = testResult.failures().nextElement();
				System.out.println(failure.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    /*Result result = JUnitCore.runClasses(MyClassTest.class);
	    for (Failure failure : result.getFailures()) {
	      System.out.println(failure.toString());
	    }*/
		System.out.println("HELLO");
		return null;
	}

}
