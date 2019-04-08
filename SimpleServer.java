
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class SimpleServer
{
    public static void main(String args[])
    {
        int clientNumber = 1;
        try (ServerSocket server = new ServerSocket(8189))
        {
            while (true)
            {
                Socket incoming = server.accept();
                Thread thread = new Thread(new MyServer(incoming, clientNumber));
                thread.start();
                System.out.println("Spawning: " + clientNumber++);
            }
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}


class MyServer implements Runnable
{
    private int count;
    private Socket incoming;

    MyServer(Socket incoming, int clientNumber)
    {
        this.incoming = incoming;
        count = clientNumber;
    }

    public void run()
    {
        try (InputStream inStrem = incoming.getInputStream();
             OutputStream outStream = incoming.getOutputStream())
        {
            Scanner in = new Scanner(inStrem, "UTF-8");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);
            out.println("Hello!!! You are my client number " + count);
            out.println("Please type your name: ");
            String name = in.nextLine();
            out.println(name + " type \"BYE\" for out this server");
            boolean done = false;
            while (!done && in.hasNextLine())
            {
                String line = in.nextLine();
                out.println("Echo: " + line);
                if (line.trim().equals("BYE"))
                {
                    out.println("GOODBYE!!!");
                    done = true;
                }
            }
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}