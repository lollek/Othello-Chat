import java.io.*;

import ChatApp.*; // The package containing our stubs
import org.omg.CosNaming.*; // HelloClient will use the naming service.
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*; // All CORBA applications need these classes.
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

class ChatCallbackImpl extends ChatCallbackPOA {
  private ORB orb;
  public void setORB(ORB orb_val) {
    orb = orb_val;
  }
  public void callback(String notification) {
    System.out.println(notification);
  }
}

public class ChatClient {
  static Chat chatImpl;

  public static void main(String args[]) {
    try {
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // create servant (impl) and register it with the ORB
      ChatCallbackImpl chatCallbackImpl = new ChatCallbackImpl();
      chatCallbackImpl.setORB(orb);

      // get reference to RootPOA and activate the POAManager
      POA rootpoa =
        POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      rootpoa.the_POAManager().activate();

      // get the root naming context
      org.omg.CORBA.Object objRef =
        orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      // resolve the object reference in naming
      String name = "Chat";
      chatImpl = ChatHelper.narrow(ncRef.resolve_str(name));

      // obtain callback reference for registration w/ server
      org.omg.CORBA.Object ref =
        rootpoa.servant_to_reference(chatCallbackImpl);
      ChatCallback cref = ChatCallbackHelper.narrow(ref);

      // Application code goes below
      BufferedReader stdin =
        new BufferedReader(new InputStreamReader(System.in));
      String[] line;

      // Print Hello
      System.out.println("Hello!\n"
                        +"Available commands:\n"
                        +"join username - join as username\n"
                        +"post text to post - write text to the chat\n"
                        +"list - list logged on users\n"
                        +"leave - leave the chat");
      boolean has_joined = false;
      for (;;) {
        try {
          line = stdin.readLine().split(" ", 2);
          switch(line[0]) {
            case "join": has_joined = chatImpl.join(cref, line[1]); break;
            case "leave": chatImpl.leave(cref); break;
            case "list": chatImpl.list(cref); break;
            case "post": chatImpl.say(cref, line[1]); break;
            default: System.out.println("Bad command"); break;
          }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
          System.out.println("Bad command");
        } catch (java.lang.NullPointerException e) {
          if (has_joined) {
            chatImpl.leave(cref);
          } else {
            System.out.println("Byebye!");
          }
          System.exit(0);
        }
      }
    } catch(Exception e) {
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }
  }
}
