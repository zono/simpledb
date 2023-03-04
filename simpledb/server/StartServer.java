package simpledb.server;

import java.rmi.registry.*;

import simpledb.jdbc.network.*;

public class StartServer {

  public static void main(String args[]) throws Exception {
    // configure and initalize the database
    String dirname = (args.length == 0) ? "studentdb" : args[0];
    SimpleDB db = new SimpleDB(dirname);

    // create a registry for the server on the default port
    Registry reg = LocateRegistry.createRegistry(1099);

    // and post the server entry in it
    RemoteDriver d = new RemoteDriverImple(db);
    reg.rebind("simpledb", d);

    System.out.println("database server ready");
  }
}