import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Client {
    public static void main(String[] args) throws IOException {

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String url;

        System.out.println("Please type the URL you want to access.");
        url = stdIn.readLine();

        while(true){

            Browse b = new Browse(url);
            
            //URLを解析し、ポート番号とホスト名を得る
            b.analyze_url();

            //得られたポート番号とホスト名でアクセスする
            b.connect();

            //アクセス先からリンク先を取得しリストアップ
            b.show_list();
        
            //コマンドを受け付けてリンクする
            url = b.link();

        }

    }
}