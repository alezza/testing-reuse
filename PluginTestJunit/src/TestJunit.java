import org.eclipse.examples.helloworld.HelloWorldAction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJunit {
	
   String message = "Hello World";	
   HelloWorldAction messageUtil = new HelloWorldAction();

   @Test
   public void testPrintMessage() {	  
      assertEquals(message,messageUtil.printString(message));
   }
}