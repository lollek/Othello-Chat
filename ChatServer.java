import ChatApp.*; // The package containing our stubs.
import org.omg.CosNaming.*; // HelloServer will use the naming service.
import org.omg.CosNaming.NamingContextPackage.*; // ..for exceptions.
import org.omg.CORBA.*; // All CORBA applications need these classes.
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.*;

class ChatImpl extends ChatPOA {
  private ORB orb;
  private List<ChatCallback> callback_list = new ArrayList<ChatCallback>();
  private List<String> nick_list = new ArrayList<String>();
  private List<Character> team_list = new ArrayList<Character>();

  public void setORB(ORB orb_val) {
    orb = orb_val;
  }

  private void broadcast(String msg) {
    for (int i = 0; i < callback_list.size(); ++i) {
      callback_list.get(i).callback(msg);
    }
  }

  public boolean say(ChatCallback callobj, String msg) {
    int userindex = callback_list.indexOf(callobj);
    if (userindex != -1) {
      broadcast(nick_list.get(userindex) + ": " + msg);
      return true;
    }
    callobj.callback("Server: Please join before writing!");
    return false;
  }

  public boolean join(ChatCallback callobj, String nickname) {
    int userindex = callback_list.indexOf(callobj);
    if (userindex != -1) {
      callobj.callback("Server: You are already logged in as "
                      +nick_list.get(userindex));
      return false;
    } else if (nick_list.contains(nickname)) {
      callobj.callback("Server: Nickname is already in use!");
      return false;
    } else {
      callback_list.add(callobj);
      nick_list.add(nickname);
      team_list.add('0');
      broadcast(nickname + " has joined");
      return true;
    }
  }

  public boolean leave(ChatCallback callobj) {
    int userindex = callback_list.indexOf(callobj);
    if (userindex != -1) {
      broadcast(nick_list.get(userindex) + " has left");
      callback_list.remove(userindex);
      nick_list.remove(userindex);
      team_list.remove(userindex);
      callobj.callback("Server: Byebye!");
      return true;
    }
    callobj.callback("Server: Please join before leaving");
    return false;
  }

  public boolean list(ChatCallback callobj) {
    int userindex = callback_list.indexOf(callobj);
    if (userindex != -1) {
      callobj.callback("List of users");
      for (int i = 0; i < nick_list.size(); ++i) {
        callobj.callback(nick_list.get(i));
      }
      return true;
    }
    callobj.callback("Server: Please join before asking for a list");
    return false;
  }

  public boolean othello(ChatCallback callobj, String cmd) {
    int userindex = callback_list.indexOf(callobj);

    // Join the X team
    if (cmd.length() == 1 &&
        (cmd.charAt(0) == 'x' || cmd.charAt(0) == 'X')) {
      team_list.set(userindex, 'x');
      broadcast(nick_list.get(userindex) + " has joined team X");
      return true;

    // Join the O team
    } else if (cmd.length() == 1 &&
               (cmd.charAt(0) == 'o' || cmd.charAt(0) == 'O')) {
      team_list.set(userindex, 'o');
      broadcast(nick_list.get(userindex) + " has joined team O");
      return true;

    // Request the board
    } else if (cmd.equals("board")) {

    // Put chip
    } else if (cmd.length() == 2 &&
               'a' <= cmd.charAt(0) && cmd.charAt(0) <= 'h' && 
               '1' <= cmd.charAt(1) && cmd.charAt(1) <= '8') {

    // Bad command
    } else {
      callobj.callback("Othello commands:\n"
                      +"othello x/o - join the game as X or O\n"
                      +"othello board - draw board\n"
                      +"othello b5 - put a chip on b5\n");
      return false;
    }
    return false;
  }
}

public class ChatServer {
  public static void main(String args[]) {
    try {
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // create servant (impl) and register it with the ORB
      ChatImpl chatImpl = new ChatImpl();
      chatImpl.setORB(orb);

      // get reference to rootpoa & activate the POAManager
      POA rootpoa =
        POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      rootpoa.the_POAManager().activate();

      // get the root naming context
      org.omg.CORBA.Object objRef =
        orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      // obtain object reference from the servant (impl)
      org.omg.CORBA.Object ref =
        rootpoa.servant_to_reference(chatImpl);
      Chat cref = ChatHelper.narrow(ref);

      // bind the object reference in naming
      String name = "Chat";
      NameComponent path[] = ncRef.to_name(name);
      ncRef.rebind(path, cref);

      // Application code goes below
      System.out.println("ChatServer ready and waiting ...");

      // wait for invocations from clients
      orb.run();
    }

    catch(Exception e) {
      System.err.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }

    System.out.println("ChatServer Exiting ...");
  }

}
