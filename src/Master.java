import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by bbq_o on 2015/12/6.
 * This mater node dose not participate in any partition.
 * Contain metadata and control
 */
public class Master {
    HashMap<String, String> iniPatition;
    String time0;
    String time1;
    String time2;
    String time3;
    static final int PORT = 23339;
    String currentPar = "";


    public Master() {
        iniPatition = new HashMap<String, String>();
        time0 = "ABCDEFGH";
        time1 = "ABCD EFGH";
        time2 = "A BCD EFG H";
        time3 = "BCDEFG";
        iniPatition.put("A", "dc30.utdallas.edu");
        iniPatition.put("B", "dc31.utdallas.edu");
        iniPatition.put("C", "dc32.utdallas.edu");
        iniPatition.put("D", "dc33.utdallas.edu");
        iniPatition.put("E", "dc34.utdallas.edu");
        iniPatition.put("F", "dc35.utdallas.edu");
        iniPatition.put("G", "dc36.utdallas.edu");
        iniPatition.put("H", "dc37.utdallas.edu");
    }

    /*
    * TODO didnt actually implement file update,but the algorithm, that check VN SC and SD before updating a file, and update them after updating.
    * TODO Suppose file updating function is in other program.
    *
    *
    *
    * */
    void logicalFunction() {
        Scanner sc = new Scanner(System.in);
        Master master = new Master();
        while (true) {
            System.out.println("Select a time instant.");
            int a = sc.nextInt();
            System.out.println("The partition of this time instant: ");
            String currentState;
            switch (a) {
                case (0): {
                    currentState = master.time0;
                    break;
                }
                case (1): {
                    currentState = master.time1;
                    break;
                }
                case (2): {
                    currentState = master.time2;
                    break;
                }
                case (3): {
                    currentState = master.time3;
                    break;
                }
                default:
                    currentState = master.time0;
            }
            System.out.println(currentState);
            System.out.println("Chose a site to perform update.");
            String site = sc.next();
            String[] temp = currentState.split(" ");
            for (String eachPar : temp)
                if (eachPar.contains(site))
                    currentPar = eachPar;
            HashMap<String, FileInfo> voteTable = new HashMap<>();
            int maxVN = 0;
            int relatedSC = 0;
            String dds = "";
            for (String s : currentPar.split("")) {
                try {
                    Socket socket = new Socket(master.iniPatition.get(s), PORT);
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dos.writeUTF(currentPar);
                    dos.flush();
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    int aa = dis.readInt();
                    int b = dis.readInt();
                    String c = dis.readUTF();
                    if(aa>maxVN) {maxVN = aa; relatedSC = b; dds = c;}
                    voteTable.put(s, new FileInfo(aa, b, c));
                    dos.close();
                    dis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            FileInfo ttemp = algorithm(voteTable, maxVN, relatedSC, dds);
            for (String s : currentPar.split("")) {
                try {
                    Socket socket = new Socket(master.iniPatition.get(s), PORT);
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dos.writeUTF("return");
                    dos.flush();
                    dos.writeInt(ttemp.vn);
                    dos.writeInt(ttemp.sc);
                    dos.writeUTF(ttemp.ds);
                    dos.flush();

                    dos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    FileInfo algorithm(HashMap table, int max, int related, String dds) {
        int parSize = table.size();
//        TODO 这里的 nested if 逻辑很乱，有重复语句，还要在想想！
        if(parSize > (related/2)){
            if(related == 3){
                int f = 0;
                for(String ttt:dds.split("")){
                    if(currentPar.contains(ttt)){
                        f++;
                    }
                }
                if(f >= 2){
                    if(parSize == 2){
                        return new FileInfo(++max,related,dds);
                    }else if(parSize == 3){
                        return new FileInfo(++max,parSize,currentPar);
                    }else if(parSize > 3){
                        return new FileInfo(++max,parSize,currentPar.substring(parSize-1));
                    }
                }
            }else{
                return  new FileInfo(++max,parSize,currentPar.substring(parSize-1));
            }
        }else if(parSize == related/2){
            if(currentPar.contains(dds))
                return new FileInfo(++max,parSize,currentPar.substring(parSize-1));
        }
        return new FileInfo(-1,-1,"");
    }


    class FileInfo {
        String name;
        int vn;
        int sc;
        String ds;

        public FileInfo(int a, int b, String c) {
            vn = a;
            sc = b;
            ds = c;
        }
    }


}
