
import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GreetingServer extends Thread
{
   private static Socket server;
   static ServerSocket serverSocket;
   Connection con;
   Statement s;
   ResultSet rs;
   boolean login=false,valid=true;
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
   public GreetingServer(Socket x) throws IOException
   {    server=x;
     // serverSocket = new ServerSocket(6066);
     // serverSocket.setSoTimeout(100000);
   }

   public void run()
   {
       try
       {
           System.out.println("Connected to "+ server.getRemoteSocketAddress());
           DataInputStream in =new DataInputStream(server.getInputStream());
           DataOutputStream out = new DataOutputStream(server.getOutputStream());
           
           while(true)
           {
               try//1st
               {
                   try//2nd
                   {
                       login=false;
                       System.out.println("Waiting for clients on port " + serverSocket.getLocalPort() + "...");
                       String host="jdbc:mysql://localhost:3306/ebanking";
                       String username="root";
                       String password="";
                       try//3rd
                       {
                           try//4th
                           {
                               Class.forName("com.mysql.jdbc.Driver");
                           }
                           catch (ClassNotFoundException ex)/*4th Try*/
                           {
                               Logger.getLogger(GreetingServer.class.getName()).log(Level.SEVERE, null, ex);
                           }
                           con=DriverManager.getConnection(host, username, password);
                       }
                       catch (SQLException ex)/*3rd Try*/
                       {
                           Logger.getLogger(GreetingServer.class.getName()).log(Level.SEVERE, null, ex);
                       }
                   }
                   catch(Exception e)/*2nd Try*/
                   {
                       System.out.println("Not connected to database");
                   }
                   
                   try//5th
                   {
                        String info="1.Register\n2.Login\n3.Logout";
                        out.writeUTF(info);
                        int i=Integer.parseInt(in.readUTF());
                        
                        switch(i)
                        {
                            case 1:
                                
                            System.out.println("New Registration on "+serverSocket.getLocalPort());
                            String name=in.readUTF();
                            String email=in.readUTF();
                            int no=Integer.parseInt(in.readUTF());
                            valid=true;
                            String uname="";
                            
                            uname=in.readUTF();
                                                        
                            String pass=in.readUTF();
                           
                            String sql="INSERT INTO member VALUES ('" + name + "','" + email +"','" +no+ "','" +uname+ "','" + pass + "');";
                            
                            s = con.createStatement();
                            s.executeUpdate(sql);
                            System.out.println("Registration Successful on "+serverSocket.getLocalPort());
                            break;
                            case 2:
                                boolean validity=false;
                                sql = "SELECT username,member_password FROM member";
                                s = con.createStatement();
                                ResultSet rs = s.executeQuery(sql);
                                uname=in.readUTF();
                                pass=in.readUTF();
                                while(rs.next())
                                {
                                    if(rs.getString("username").equals(uname))
                                    {
                                        if(rs.getString("member_password").equals(pass))
                                        {
                                            validity=true;
                                        }
                                    }
                                }
                                if(validity)
                                {
                                    login=true;
                                    System.out.println("Granted Access to "+uname +" on "+server.getRemoteSocketAddress());
                                    out.writeUTF("Login in Successful.");
                                    while(true)
                                    {
                                        String select="1.Deposit\n2.Withdraw\n3.Check Balance";
                                        out.writeUTF(select);
                                        String type;
                                        int j=Integer.parseInt(in.readUTF());
                                        switch(j)
                                        {
                                            case 1:
                                                type="D";
                                                out.writeUTF("Enter the Deposit Amount (Rs) ");
                                                String depamt=in.readUTF();
                                                System.out.println("Rs. "+depamt+" added for "+uname);
                                                out.writeUTF("Rs "+depamt+" has been added Successfully");
                                                sql="INSERT INTO transaction VALUES ('" + uname + "','" + type + "','"+ depamt +"','0');";
                                                s = con.createStatement();
                                                s.executeUpdate(sql);
                                                break;
                                            case 2:
                                                type="W";
                                                out.writeUTF("Enter the Amount to be withdrawn (Rs) ");
                                                String withamt=in.readUTF();
                                                double dep1=0;
                                                sql = "SELECT deposit FROM transaction where username='"+uname+"';";
                                                s = con.createStatement();
                                                ResultSet r1 = s.executeQuery(sql);
                                                while(r1.next())
                                                {
                                                    dep1+=Double.parseDouble(r1.getString("deposit"));
                                                }
                                                double dep2=0;
                                                sql = "SELECT withdraw FROM transaction where username='"+uname+"';";
                                                s = con.createStatement();
                                                ResultSet r2 = s.executeQuery(sql);
                                                while(r2.next())
                                                {                                                    

                                                    dep2+=Double.parseDouble(r2.getString("withdraw"));
                                                }
                                                System.out.println(dep2);
                                                dep1=dep1-dep2;
                                                if(dep1>=Double.parseDouble(withamt))
                                                {
                                                    System.out.println("Rs. "+withamt+" withdrawn from "+uname);
                                                    out.writeUTF("Rs "+withamt+" has been Withdrawn Successfully");
                                                    sql="INSERT INTO transaction VALUES ('" + uname + "','" + type + "','0','"+ withamt +"');";
                                                    s = con.createStatement();
                                                    s.executeUpdate(sql);
                                                }
                                                else
                                                {
                                                    out.writeUTF("Insufficient Amount");
                                                }
                                                break;
                                            case 3:
                                                double dep=0,wit=0,bal=0;
                                                sql = "SELECT deposit FROM transaction where username='"+uname+"';";
                                                s = con.createStatement();
                                                ResultSet r = s.executeQuery(sql);
                                                while(r.next())
                                                {
                                                    dep+=Double.parseDouble(r.getString("deposit"));
                                                }
                                                //System.out.println(dep+"");
                                                String sql1 = "SELECT withdraw FROM transaction where username='"+uname+"';";
                                                s = con.createStatement();
                                                ResultSet rsw = s.executeQuery(sql1);
                                                while(rsw.next())
                                                {
                                                    wit+=Double.parseDouble(rsw.getString("withdraw"));
                                                }
                                                //System.out.println(wit+"");
                                                bal=dep-wit;
                                                //System.out.println(bal+"");
                                                out.writeUTF(bal+"");
                                                break;
                                        }
                                        out.writeUTF("Do you wish to perform another Transaction ? (Y/N)");
                                        if(in.readUTF().equals("Y"))
                                        {
                                            
                                            out.writeUTF("\n");
                                            continue;
                                        }
                                        else
                                        {
                                            out.writeUTF("quit");
                                            break;
                                        }
                                    }
                                }
                                else
                                {
                                    login=false;
                                    System.out.println("Access Denied on "+server.getRemoteSocketAddress()+"\nUsername or Password invalid");
                                    System.out.println("Failed to Login. \nUsername or Password invalid");
                                    try{
                                    serverSocket.close();
                                    System.exit(0);
                                    }
                                    catch(Exception e){}
                                }
                                break;
                            case 3:
                                if(login){
                                try{
                                serverSocket.close();
                                System.exit(0);
                                 login=false;
                                break;
                                }
                                    catch(Exception e){}
                                }else{
                                    login=false;
                                    break;
                                }
                        }
                   }
                   catch(Exception ex)/*5th Try*/
                   {
                   }
               }
               catch(Exception ex)/*1st Try*/
               {
               }
               try
               {
                   out.writeUTF("Do you wish to proceed ? (Y/N)");
                   if(in.readUTF().equals("Y"))
                   {
                       out.writeUTF("\n");
                       continue;
                   }
                   else
                   {
                       out.writeUTF("quit");
                       break;
                   }
               }
               catch(Exception e)
               {
               }
           }
       }
       catch(Exception e)
       {
       }
   }
   public static void main(String [] args)
   {
       int port = 6066;
       //  ServerSocket sc;
       try
       {
           serverSocket=new ServerSocket(port);
           while(true)
           {
               Socket s=serverSocket.accept();
               new GreetingServer(s).start();
           }
           //Thread t = new GreetingServer(port);
           // t.start();
       }
       catch(IOException e)
       {
           e.printStackTrace();
       }
   }
}