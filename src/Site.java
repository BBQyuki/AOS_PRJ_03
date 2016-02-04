import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by bbq_o on 2015/12/6.
 */
public class Site {
    HashMap<String, String> iniPatition;

    static final int PORT = 23339;

    FileInfo theFile;

    public Site() {
        iniPatition = new HashMap<String, String>();
        iniPatition.put("A","dc30.utdallas.edu");
        iniPatition.put("B","dc31.utdallas.edu");
        iniPatition.put("C","dc32.utdallas.edu");
        iniPatition.put("D","dc33.utdallas.edu");
        iniPatition.put("E","dc34.utdallas.edu");
        iniPatition.put("F","dc35.utdallas.edu");
        iniPatition.put("G","dc36.utdallas.edu");
        iniPatition.put("H","dc37.utdallas.edu");
        theFile = new FileInfo();
    }

    class Listener extends Thread{
        public void run() {
            while(true) {
                try {
                    ServerSocket listener = new ServerSocket(PORT);
                    new Handler(listener.accept()).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void start(){
        Thread t = new Listener();

        t.start();
    }

    class Handler extends Thread{
        Socket s;
        String currentPar = "";
        public Handler(Socket s){
            this.s = s;
        }
        public void run(){
            try {
                DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                currentPar = dis.readUTF();
                if(!currentPar.equals("return")) {
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
                    dos.writeInt(theFile.vn);
                    dos.writeInt(theFile.sc);
                    dos.writeUTF(theFile.ds);
                    dos.flush();
                    dos.close();

                }else{
                    theFile.vn = dis.readInt();
                    theFile.sc = dis.readInt();
                    theFile.ds = dis.readUTF();
                    System.out.println("File info on this site is : VN="+theFile.vn+" SC="+theFile.sc+" DS="+theFile.ds);
                }
                dis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}

class FileInfo{
    String name;
    int vn;
    int sc;
    String ds;
    public FileInfo(){
        vn = 1;
        sc = 8;
        ds = "H";
    }
    public void setInfo(int a,int b, String c){
        vn = a;
        sc = b;
        ds = c;
    }
}