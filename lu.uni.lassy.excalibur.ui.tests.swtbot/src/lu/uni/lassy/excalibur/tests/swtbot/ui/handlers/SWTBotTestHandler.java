/*******************************************************************************
 * Copyright (c) 2014 University of Luxembourg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Philippe YOANN - initial API and implementation
 ******************************************************************************/
package lu.uni.lassy.excalibur.tests.swtbot.ui.handlers;

import java.util.Enumeration;

import junit.framework.TestFailure;
import junit.framework.TestResult;
import lu.uni.lassy.excalibur.tests.swtbot.utils.SWTBotSingleTest;

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
