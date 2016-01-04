package itm1.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.examples.helloworld.HelloWorldAction;
import org.junit.Test;

public class TestJunit {
	
   String message = "Hello World";	
   HelloWorldAction messageUtil = new HelloWorldAction();

   @Test
   public void testPrintMessage() {	  
      assertEquals(message,messageUtil.printString(message));
   }
}