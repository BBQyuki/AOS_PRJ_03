/**
 * Created by bbq_o on 2015/12/10.
 */
import java.util.Scanner;

public class Entry {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("master node?");
        String temp = sc.nextLine();
        if(temp.equals("1"))
            new Master().logicalFunction();
        else new Site().start();
    }
}
