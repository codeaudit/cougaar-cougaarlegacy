package sam;

import java.lang.String;
/**
 * Title:        Sam<p>
 * Description:  ALP Business Process User Interface<p>
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.<p>
 * @author Doug MacKenzie
 * @version 1.0
 */

public class prettyprint
{
   private static int tab = 2;

   private static class offset
   {
      private int pos;

      public offset(int value)
      {
         pos = value;
      }

      public int value()
      {
         return pos;
      }

      public void update(int newvalue)
      {
         pos = newvalue;
      }

   }

   private static class wrapString
   {
      private String val;

      public wrapString(String value)
      {
         val = value;
      }

      public String value()
      {
         return val;
      }

      public void update(String value)
      {
         val = value;
      }

   }

   public prettyprint()
   {
   }

   private static String startLine(int indent)
   {
      String output = "\n";
      for(int i=0; i<indent; i++)
         output = output + " ";
      return output;
   }

   public static String Lisp(String input, offset pointer, int indent)
   {
      String output = "";

      // Print the header
      int pos = pointer.value();
      while( input.charAt(pos) != '(' )
         output = output + input.charAt(pos++);

      // print the first '('
      output = output + "\n";
      for(int i=0; i<indent; i++)
         output = output + " ";

      output = output + input.charAt(pos++);

      // Now print until we see a close.
      while( input.charAt(pos) != ')' )
      {
         // Did we get a sub?
         if( input.charAt(pos) == '(' )
         {
            // Recurse to process it.
            offset ptr = new offset(pos);
            output = output + Lisp(input, ptr, indent + tab);
            pos = ptr.value();
         }
         else
         {
            output = output + input.charAt(pos++);
         }
      }

      // print the final ')'
      output = output + input.charAt(pos++);

      pointer.update(pos);

      return output;
   }

   public static String Lisp(String input)
   {
      return Lisp(input, new offset(0), 0);
   }



   // Cheat and use one of our int wrappers for the boolean flag "wasEnd".
   //    It is set to 1 (true) if the token was an end </xx> and 0 (false) if not <xx>
   //    and in either case, token is set to "xx".
   private static String processXMLtoken(String input, offset pointer, int indent, wrapString token, offset wasEnd)
   {
      int pos = pointer.value();
      String output = startLine(indent);

      int left = input.indexOf('<',pos);
      int right = input.indexOf('>',left);

      //return if no more tokens.
      if( left < 0 || right < 0 )
         return output;

      // get the token for our return parms
      if( input.charAt(left+1) == '/' )
      {
         token.update(input.substring(left+2,right));
         wasEnd.update(1);
      }
      else if( input.charAt(right-1) == '/' )
      {
         token.update(input.substring(left+1,right-1));
         wasEnd.update(1);
      }
      else
      {
         token.update(input.substring(left+1,right));
         wasEnd.update(0);
      }

      // Print the token
      output = output + input.substring(left,right+1);

      // Return our offset.
      pointer.update(right+1);

      return output;
   }


   public static String XML(String input, int length, offset pointer, int indent)
   {
      String output = "";

      // Start printing until we see the first token.
      int pos = pointer.value();

      while( input.charAt(pos) != '<' )
         output = output + input.charAt(pos++);

      // print the start token.
      offset ptr = new offset(pos);
      offset wasEnd = new offset(0);
      wrapString token = new wrapString("");
      output = output + processXMLtoken(input, ptr, indent, token, wasEnd);
      pos = ptr.value();

      boolean done = wasEnd.value() == 1;

      while( !done )
      {
         // Now print the intermediate stuff until we see the end token.
         while( pos < length && input.charAt(pos) != '<' )
            output = output + input.charAt(pos++);

         if( pos >= length )
         {
            // Off the end.
            done = true;
            break;
         }

         // Is it our end?
         int right = input.indexOf('>',pos);
         if( input.substring(pos+1,right).equals("/" + token.value()) )
         {
            // Yes
            output = output + startLine(indent) + input.substring(pos,right+1);
            pos = right + 1;
            done = true;
         }
         else
         {
            // No.
            offset ptr2 = new offset(pos);
            output = output + XML(input, length, ptr2, indent+tab);
            pos = ptr2.value();
         }
      }

      pointer.update(pos);

      return output;
   }


   public static String XML(String input)
   {
      return XML(input, input.length(), new offset(0), 0);
   }
}