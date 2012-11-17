import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverS = null;
        boolean end = true;

        try {
            serverS = new ServerSocket(Integer.parseInt(args[0]));
        } catch(IOException e) {
            System.out.println("ポート番号にアクセスできません");
            System.exit(1);
        }

        while(end){
            new MultipleThread(serverS.accept()).start();
        }
        
        serverS.close();
       
    }

}
