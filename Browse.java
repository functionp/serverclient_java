import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Browse {

    public String url;
    public int port;
    public String host;
    public String filepath;

    public Socket soc = null;
    public BufferedReader in = null;
    public PrintWriter out = null;

    public ArrayList<String> ref_list = new ArrayList<String>();

    //Browse クラスのコンストラクタ インスタンス作成時にURLを受け取ってインスタンス変数として保持
    public Browse(String url){
        this.url = url;
    }

    //インスタンス変数のURLを解析してホスト名とポート番号を得る
    public void analyze_url(){

        String[] split_host;
        String[] split_url;

        split_url = url.split("/",4);
        
        //ホスト名に:が含まれていればポート番号を抽出、なければ80に設定
        if(split_url[2].indexOf(":") != -1){
            split_host = split_url[2].split(":");
            host = split_host[0];
            port = Integer.parseInt(split_host[1]);
        }else{
            host = split_url[2];
            port = 80;
        }

        System.out.println("host:" + host);
        System.out.println("port:" + port);

        filepath = "/" + split_url[3];
        System.out.println("filepath:" + filepath);
    }
    
    //ポート番号とホスト名を使って接続
    public void connect(){

        try{
            if(host.equals("localhost")){
                soc = new Socket(InetAddress.getLocalHost(), port); //ローカルホストでsソケット生成
            }else{
                soc = new Socket(host, port); //ホスト名でsソケット生成
            }

            in = new BufferedReader(new InputStreamReader(soc.getInputStream())); //出力ストリーム生成
            out = new PrintWriter(soc.getOutputStream(), true); //入力ストリーム生成
            out.println("GET " + filepath + " HTTP/1.0\r\n"); //改行コードなしで改行と判断される
            out.println("Host: " + host + ":" + port + "\r\n");
        }catch(UnknownHostException e) {
            System.out.println("Cannot connect to the host.");
            System.exit(1);
        }catch(IOException e){
            System.out.println("Cannot get IO connection");
            System.exit(1);
        }

    }

    //接続先のサーバからのHTMLを一行づつ読み込みリンク先をリストアップ
    public void show_list() throws IOException {
        Pattern p1 = Pattern.compile("<a ", Pattern.CASE_INSENSITIVE);
        Pattern p2 = Pattern.compile("href=\"(.*?)\"", Pattern.CASE_INSENSITIVE);
        Pattern p3 = Pattern.compile("/([^/])*$");
        Pattern p4 = Pattern.compile("^http://");
        Pattern p5 = Pattern.compile("/.*/../");
        Pattern p6 = Pattern.compile("\\./");
        Pattern p7 = Pattern.compile("<!--");
        Pattern p8 = Pattern.compile("-->");
        Pattern p9 = Pattern.compile("<head>", Pattern.CASE_INSENSITIVE);
        Pattern p10 = Pattern.compile("</head>", Pattern.CASE_INSENSITIVE);
        Matcher m1, m2, m3, m4, m5, m6, m7, m8, m9, m10;
        String ref; //href内のアドレス
        String line;
        Boolean avoid_flag = false;

        //現在のアドレスの相対位置を得る
        m3 = p3.matcher(url);
        String current = m3.replaceAll("/");

        System.out.println("current:" + current);

        //サーバからの送信を一行づつ読み込み、出力
        while((line = in.readLine()) != null){
            System.out.println(line);
            m2 = p2.matcher(line);
            m7 = p7.matcher(line); //コメントのマッチャを作成
            m8 = p8.matcher(line);
            m9 = p9.matcher(line); //ヘッドタグのマッチャを作成
            m10 = p10.matcher(line);

            //読込中の行に"<!--"を含むか
            if(m7.find() | m9.find()){
                avoid_flag = true;
            }

            //読込中の行に"-->"を含むか
            if(m8.find() | m10.find()){
                avoid_flag = false;
            }

            //コメント及び<head>内は検索しない
            if(avoid_flag == false) {

                //読込中の行にhref="~~"を含むか
                if(m2.find()){
                    ref = m2.group(1); //href内のアドレスを取得
                    m4 = p4.matcher(ref);

                    //先頭がhttp://から始まっているか
                    if (m4.find()) {
                        ref_list.add(ref);
                    }else{
                        m6 = p6.matcher(ref);
                        ref = m6.replaceFirst(""); //"./"を消去
                        ref_list.add(current + ref);
                    }

                }
            }
        }
    }

    //リンク先へのリンクを受け付け、次のアドレスを返す
    String link() throws IOException {
        int target; //次にアクセスするリンク番号
        String command ="";

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        //リンクがなかったらアクセスを要求しない
        if(ref_list.size() == 0){
            System.out.println("There are no anchor tag.");
        }else{

            //リンク先をリストアップ
            for (int i = 0; i < ref_list.size(); i++) {
                System.out.println(i + ": " + ref_list.get(i));
            }
                
            System.out.println("Please type the number of url which you want to access.");
        }

        System.out.println("q:Quit, r:Reload, o:Open");
        command = stdIn.readLine(); //入力を受け付ける

        if (command.equals("q")){
            System.out.println("See you!");

            in.close();
            out.close();
            stdIn.close();
            soc.close();
        }else if(command.equals("r")){
            return url;
        }else if (command.equals("o")){

            System.out.println("Please type the URL you want to access.");
            url = stdIn.readLine(); //入力を受け付ける
            System.out.println("url:" + url);

            return url;
        }

        command = stdIn.readLine(); //入力を受け付ける
        target = Integer.parseInt(command);
        url = ref_list.get(target);
        System.out.println("url:" + url);

        return url;
        
    }

}
