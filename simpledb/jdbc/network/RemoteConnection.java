package simpledb.jdbc.network;

import java.rmi.*;

/**
 * The RMI remote interface corresponding to Connection.
 * The methods are identical to those of Connection,
 * exception that they throw RemoteExceptions insteads of SQLException.
 * 
 * @author Edward Sciore
 */
public interface RemoteConnection extends Remote {
  public RemoteStatement createStatement() throws RemoteException;

  public void close() throws RemoteException;
}
