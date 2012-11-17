import java.io.*;
import java.net.*;

public class MultipleThread extends Thread {
    Socket socket = null;
    public MultipleThread(Socket s){
        super("MultipleThread");
        socket = s;
    }

    public void run(){
        try {

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //出力ストリーム
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //入力ストリーム
            BufferedReader file;

            String request_line, request_header;
            String line;
            String filename;
            String[] split_line;
            
            request_line = in.readLine();
            request_header = in.readLine();

            System.out.println("Request Line:" + request_line);
            System.out.println("Request Header:" + request_header);

            split_line = request_line.split(" ");
            filename = split_line[1].substring(1,split_line[1].length()); //スラッシュを取り除く

            //ファイルを1行づつ読み込んでストリームに出力
            file = new BufferedReader(new FileReader(filename));
            while((line = file.readLine()) != null) {
                out.println(line);
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){}
                
            }
            file.close();

            in.close();
            out.close();
            socket.close();
        
        }catch (IOException e) {
            System.out.println("runメソッド実行中例外" + e);
            System.exit(1);
        }
    }

}