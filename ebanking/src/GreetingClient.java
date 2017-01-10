import java.net.*;
import java.io.*;
import java.util.Scanner;

public class GreetingClient
{
    public static void main(String [] args)
    {
        String serverIP = "192.168.1.107";
        int port = 6066;
        boolean login=false;
        try
        {
            
            System.out.println("Connecting to Server on port " + port);
            Socket client = new Socket(serverIP , port);
            System.out.println("Connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out =new DataOutputStream(outToServer);
            
            InputStream inFromServer = client.getInputStream();
            DataInputStream in=new DataInputStream(inFromServer);
            
            while(true)
            {
                Scanner sc=new Scanner(System.in);
                try
                {
                    String s = in.readUTF();
                    System.out.println(s);
                    System.out.println("Enter your choice:");
                    
                    switch(sc.nextInt())
                    {
                        case 1:
                            out.writeUTF("1");

                            System.out.print("Enter your Name : ");
                            DataOutputStream out_name =new DataOutputStream(outToServer);
                            out_name.writeUTF(sc.next());
                            System.out.println();
                            System.out.print("Enter your email Id : ");
                            DataOutputStream out_email_id =new DataOutputStream(outToServer);
                            out_email_id.writeUTF(sc.next());
                            System.out.println();
                            System.out.print("Enter your Mobile No : ");
                            DataOutputStream out_mobile_no =new DataOutputStream(outToServer);
                            out_mobile_no.writeUTF(sc.next());
                            System.out.println();
                           
                            System.out.print("Enter your Account number : ");
                            DataOutputStream out_account_number =new DataOutputStream(outToServer);
                            out_account_number.writeUTF(sc.next());
                            
                            System.out.println();
                            System.out.print("Set Password : ");
                            DataOutputStream out_password =new DataOutputStream(outToServer);
                            out_password.writeUTF(sc.next());
                            break;
                        case 2:
                            out.writeUTF("2");

                            System.out.println("Enter your Account number : ");
                            out.writeUTF(sc.next());

                            System.out.println("Enter your Password : ");
                            out.writeUTF(sc.next());

                            String permission=in.readUTF();
                            if(permission.equals("Login in Successful."))
                            {
                                login=true;
                                System.out.println("Login in Successful.");
                                while(true)
                                {
                                    System.out.println(in.readUTF());
                                    System.out.println("Enter your Selection : ");
                                    switch(sc.nextInt())
                                    {
                                        case 1:
                                            out.writeUTF("1");
                                            System.out.println(in.readUTF());
                                            out.writeUTF(sc.nextDouble()+"");
                                            System.out.println(in.readUTF());
                                            break;
                                        case 2:
                                            out.writeUTF("2");
                                            System.out.println(in.readUTF());
                                            out.writeUTF(sc.nextDouble()+"");
                                            System.out.println(in.readUTF());
                                            break;
                                        case 3:
                                            out.writeUTF("3");
                                            System.out.println("Balance is Rs ");
                                            System.out.println(in.readUTF());
                                    }
                                    System.out.println(in.readUTF());
                                    out.writeUTF(sc.next());
                                    if(in.readUTF().equals("quit"))
                                    {
                                        break;
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                            }
                            else{
                                login=false;
                            }
                            break;
                        case 3:
                            out.writeUTF("3");
                            if(login){
                                System.out.println("Logout Succcessful.");
                                 login=false;
                            }else{
                                System.out.println("Need to Login First.");
                                 login=false;
                            }
                            break;
                    }
                    //client.close();
                }
                catch(Exception ex)
                {
                    System.exit(0);
                }
                System.out.println(in.readUTF());
                out.writeUTF(sc.next());
                if(in.readUTF().equals("quit"))
                {
                    break;
                }
                else
                {
                    continue;
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
