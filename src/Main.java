import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Main m = new Main();
        int menu;
        while(true){
            System.out.println("Menu");
            System.out.println("========================");
            System.out.println("1.) Create and Order");
            System.out.println("2.) Inquire products");
            System.out.println("3.) Retrieve order info");
            System.out.println("4.) Cancel Order");

            System.out.print("Select an Option: ");
            menu= s.nextInt();
            System.out.print("\033[H\033[2J");
            System.out.flush();
            switch(menu){
                case 1:
                    m.createOrder();
                    break;
                case 2:
                    m.inquireProducts();
                    break;
                case 3:
                    m.getOrderInfo();
                    break;
                case 4:
                    m.cancelOrder();
                    break;
                default:
                    System.out.println("No option "+menu);
                    break;
            }
        }
    }
    public void createOrder(){
        System.out.println("1.) Create and Order");
    }
    public void inquireProducts(){
        System.out.println("2.) Inquire products");
    }
    public void getOrderInfo(){
        System.out.println("3.) Retrieve order info");
    }
    public void cancelOrder(){
        System.out.println("4.) Cancel Order");
    }

}
