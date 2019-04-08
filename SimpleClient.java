
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class SimpleClient
{
    public static void main(String[] args)
    {
        try (Scanner input = new Scanner(System.in))
        {
            while (true)
            {
                System.out.print("Do You want connect with server in localhost: Y/N _ ");
                String answer = input.nextLine();
                if (!answer.equals("Y"))
                {
                    System.out.println("Client program closed");
                    break;
                }
                try (SocketChannel channel = SocketChannel.open(
                        new InetSocketAddress("localhost", 8189)))
                {
                    Scanner in = new Scanner(channel, "UTF-8");
                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(
                                    channel.socket().getOutputStream(), StandardCharsets.UTF_8), true);
                    Runnable handler = new InputHandler(in);
                    Thread thread = new Thread(handler);
                    thread.start();
                    Thread.sleep(300);
                    while (true)
                    {
                        System.out.print("Client: ");
                        String str = input.nextLine();
                        out.println(str);
                        Thread.sleep(300);
                        if (!((InputHandler) handler).getState())
                            break;
                    }
                } catch (IOException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}


class InputHandler implements Runnable
{
    private Scanner in;
    private boolean state;

    InputHandler(Scanner in)
    {
        state = true;
        this.in = in;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                String line = in.nextLine();
                System.out.println("Server: " + line);
            } catch (NoSuchElementException e)
            {
                System.out.println("lost contact with server");
                state = false;
                break;
            }
        }
    }

    boolean getState()
    {
        return state;
    }
}