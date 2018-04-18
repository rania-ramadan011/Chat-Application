package server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.*;
import rmiinterfaces.*;

public class ServerImpl extends UnicastRemoteObject implements ServerInt, Service {
    
    private static ArrayList<ClientInt> clients;
    private DataBaseConnection dbConn;
    private static ServerImpl instance;
    private ClientInt clientInt;
    private ArrayList<Group> groups;
    
    public static ArrayList<ClientInt> getClients() {        
        return clients;
    }
    
    private ServerImpl() throws RemoteException {
        groups = new ArrayList<Group>();
        clients = new ArrayList<>();
    }
    
    public static ServerImpl getInstance() {
        if (instance == null) {
            try {
                instance = new ServerImpl();
            } catch (RemoteException ex) {
                Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }
    
    @Override
    public void tellOthers(Message msg) {//if the other user is offline dont send message to both
        try {
            for (ClientInt clientRef : clients) {
                if (msg.getReceiverName().getClient_user_name().equals(clientRef.getCurrentClient().getClient_user_name())) {
                    clientRef.receiveMsg(msg);
                }
                if (msg.getSenderName().getClient_user_name().equals(clientRef.getCurrentClient().getClient_user_name())) {
                    clientRef.receiveMsg(msg);
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void register(String username, String password, ClientInt clientRef) {
        try {
            if (dbConn.clientValidate(username, password)) {
                clientRef.setCurrentClient(dbConn.fillData(username));
                clientRef.setContactList(dbConn.getContactList(username));
                clientRef.setRequestsList(dbConn.getAllRequests(username));
//                clientRef.setGroupsList(dbConn.getGroups(username));
                clients.add(clientRef);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void unregister(ClientInt clientRef) {
        clients.remove(clientRef);
    }
    
    @Override
    public String getName() {
        return "serverImpl";
    }
    
    @Override
    public void excute() {
        dbConn = (DataBaseConnection) ServiceLocator.getService("dbConn");
    }
    
    @Override
    public ArrayList<Client> searchUserName(String client_user_name) throws RemoteException {
        dbConn = (DataBaseConnection) ServiceLocator.getService("dbConn");
        return dbConn.searchUserName(client_user_name);
    }
    
    @Override
    public void addUserName(Client ref, Client currRef) throws RemoteException {
        dbConn.acceptContact(ref, currRef);
        for (ClientInt client : clients) {
            if (client.getCurrentClient().getClient_user_name().equals(ref.getClient_user_name())) {
                client.addToContactList(currRef);
            }
            if (client.getCurrentClient().getClient_user_name().equals(currRef.getClient_user_name())) {
                client.addToContactList(ref);
            }
        }
    }
    
    @Override
    public void removeRequestFromDB(String reqSender, String reqReceiver) throws RemoteException {
        try {
            dbConn.removeRequest(reqSender, reqReceiver);
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void addToRequests(Client receiver, Client sender) throws RemoteException {
        dbConn.addToRequestsTable(receiver, sender);
        for (ClientInt client : clients) {
            if (client.getCurrentClient().getClient_user_name().equals(receiver.getClient_user_name())) {
                client.updateNotifList(sender);
                System.out.println(receiver.getClient_user_name());
                System.out.println(sender.getClient_user_name());
            }
        }
    }
    
    @Override
    public Boolean checkUserName(String username) throws RemoteException {
        try {
            if (dbConn.uniqueUserName(username)) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public void signUp(ClientRegData clientRegData) throws RemoteException {
        dbConn.insertClientInfo(clientRegData);
    }
    
    public void sendNotificationsToAll(String notification) {
        for (ClientInt client : clients) {
            try {
                client.receiveNotification(notification);
            } catch (RemoteException ex) {
                Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void addImage(byte[] imageInByte, Client client) throws RemoteException {
        dbConn.setImage(imageInByte, client);
        client.setClient_image(imageInByte);
        for (ClientInt clientuser : clients) {
            if (clientuser.getCurrentClient().getClient_user_name().equals(client.getClient_user_name())) {
                clientuser.updateImage();
            }            
        }
    }
    
    @Override
    public void forwardFile(RemoteInputStream data, Client client,String name) throws RemoteException {        
        for (ClientInt clientUser : clients) {
            if(clientUser.getCurrentClient().getClient_user_name().equals(client.getClient_user_name())){
                    try {
                        clientUser.receiveFile(data,name);
                    } catch (RemoteException ex) {
                        Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }             
            }
        }                            
        
    }

    @Override
    public void setStatus(String status,Client client) throws RemoteException {
        dbConn.setStatus(status,client); 
        for (ClientInt clientuser : clients) {
            if (clientuser.getCurrentClient().getClient_user_name().equals(client.getClient_user_name())) {
                clientuser.updateStatus(status);
            }            
        }
    }
    
    @Override
    public void addGroup(Group group) throws RemoteException {
       dbConn.addGroup(group);
        for (ClientInt client : clients) {
            for (Client groupMember : group.getGroup()) {
                if (client.getCurrentClient().getClient_user_name().equals(groupMember.getClient_user_name())) {
                    client.addToGroupList(group);
            }
            }            
        }
    }
    
    @Override
    public ArrayList<Group> getGroups(String username) {
        try {
            groups = dbConn.getGroups(username);           
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
         return groups;
    }
    
}