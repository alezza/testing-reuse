package myplugin.tests;

/*
* This class prints the given message on console.
*/
public class MessageUtil {

   private String message;

   //Constructor
   //@param message to be printed
   public MessageUtil(String message){
      this.message = message;
   }
      
   // prints the message
   public String printMessage(String message2){
      System.out.println(message);
      return message;
   }   
} 