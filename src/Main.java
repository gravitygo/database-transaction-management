import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    Database db;
    Scanner s = new Scanner(System.in);
    public static void main(String[] args) throws SQLException {
        Main m = new Main();
        m.db = new Database();
        int menu;
        while(true){
            System.out.println("Menu");
            System.out.println("========================");
            System.out.println("1.) Create and Order");
            System.out.println("2.) Inquire products");
            System.out.println("3.) Retrieve order info");
            System.out.println("4.) Cancel Order");

            System.out.print("Select an Option: ");
            menu= m.s.nextInt();
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
    //TODO: Chyle
    public void createOrder() throws SQLException {
        s.nextLine();
        System.out.print("Enter Customer Number: ");
        String customerNumber = s.nextLine();
        System.out.print("Enter Required Date: ");
        String requiredDate = s.nextLine();
        Connection conn = db.getConn();

        String query = " INSERT INTO orders(" +
                "       orderDate" +
                "     , requiredDate" +
                "     , status" +
                "     , customerNumber)" +
                " VALUES (" +
                "       NOW()" +
                "     , ?" +
                "     , 'In Process'" +
                "     , ?" +
                " );";
        String queryProducts = " INSERT INTO orderdetails VALUES";
        ArrayList<String> updates = new ArrayList<String>();
        String updateQuery="";

        boolean addProduct = true;
        int i=1;
        do{
            System.out.print("Enter Product Code: ");
            String productCode = s.nextLine();
            System.out.print("Enter Quantity: ");
            int quantity = s.nextInt();
            System.out.print("Enter Price: ");
            float price = s.nextFloat();
            s.nextLine();
            queryProducts += " (? " +
                             " , '" + productCode + "'" +
                             " , " + quantity +
                             " , " + price +
                             " , " + i +
                             " )";
            System.out.println("[Input 'y' if you want to add another product]");
            String yes = s.nextLine();
            if(yes.charAt(0)=='y') queryProducts += ", ";
            else addProduct = false;
            i++;

            updateQuery =  " UPDATE products SET" +
                            "      quantityInStock = quantityInStock - " + quantity + "" +
                            " WHERE productCode = '" + productCode + "';";

            updates.add(updateQuery);
        }while(addProduct);
        queryProducts += ";";
        PreparedStatement pstmtOrder = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        pstmtOrder.setString(1, requiredDate);
        pstmtOrder.setString(2, customerNumber);

        System.out.println("[Press ENTER key to create Order]");
        s.nextLine();


        pstmtOrder.executeUpdate();
        int orderNumber = 0;

        ResultSet rs = pstmtOrder.getGeneratedKeys();
        if (rs.next())
            orderNumber = rs.getInt(1);
        rs.close();

        PreparedStatement pstmtOrderDetail = conn.prepareStatement(queryProducts);
        for(int j = i-1; j>0; j--)
            pstmtOrderDetail.setInt(j, orderNumber);

        System.out.println("[Press ENTER key to create Order Details]");
        s.nextLine();
        pstmtOrderDetail.executeUpdate();

        System.out.println("[Press ENTER key to update Quantity]");
        for(String uQ : updates){
            PreparedStatement pstmtUpdate = conn.prepareStatement(uQ);
            pstmtUpdate.executeUpdate();

            pstmtUpdate.close();
        }

        db.commit();
        System.out.println("[Press ENTER key to end Transaction]");
        s.nextLine();

        pstmtOrder.close();
        pstmtOrderDetail.close();

        db.closeConn();
    }


    //TODO
    public void inquireProducts() throws SQLException {
        System.out.print("Enter Product Code: ");
        String productCode = s.next();
        s.nextLine();
        Connection conn = db.getConn();
        String query =  "SELECT" +
                        "     productName" +
                        "     , productLine" +
                        "     , quantityInStock" +
                        "     , buyPrice" +
                        "     , MSRP" +
                        " FROM products" +
                        " WHERE productCode = ?" +
                        " LOCK IN SHARE MODE";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, productCode);

        System.out.println("[Press ENTER key to start retrieving the data]");
        s.nextLine();

        ResultSet rs = pstmt.executeQuery();

        String productName = "", productLine = "";
        int quantityInStock = 0;
        float buyPrice = 0, MSRP = 0;

        while (rs.next()) {
            productName     = rs.getString("productName");
            productLine     = rs.getString("productLine");
            quantityInStock = rs.getInt("quantityInStock");
            buyPrice        = rs.getFloat("buyPrice");
            MSRP            = rs.getFloat("MSRP");
        }

        rs.close();

        System.out.println("Product Name: " + productName);
        System.out.println("Product Line: " + productLine);
        System.out.println("Quantity:     " + quantityInStock);
        System.out.println("Buy Price:    " + buyPrice);
        System.out.println("MSRP:         " + MSRP);

        db.commit();
        System.out.println("[Press ENTER key to end Transaction]");
        s.nextLine();

        pstmt.close();

        db.closeConn();
    }

    //TODO
    public void getOrderInfo() throws SQLException {
        System.out.print("Enter Order ID: ");
        String orderID = s.next();
        s.nextLine();
        Connection conn = db.getConn();
        String query =  " SELECT " +
                        "     orderDate" +
                        "     , requiredDate" +
                        "     , shippedDate" +
                        "     , customerName" +
                        "     , status" +
                        "     , productName" +
                        "     , quantityOrdered" +
                        "     , priceEach" +
                        "     , orderLineNumber " +
                        " FROM orders o " +
                        " LEFT JOIN orderdetails od ON od.orderNumber   = o.orderNumber" +
                        " LEFT JOIN products      p ON od.productCode   = p.productCode" +
                        " LEFT JOIN customers     c ON c.customerNumber = o.customerNumber" +
                        " WHERE o.orderNumber = ?" +
                        " LOCK IN SHARE MODE";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, orderID);

        System.out.println("[Press ENTER key to start retrieving the data]");
        s.nextLine();

        ResultSet rs = pstmt.executeQuery();

        boolean first = true;

        while (rs.next()) {
            if(first){
                System.out.println("Order Information");
                System.out.println("=====================");
                System.out.println("Order Date:    " + rs.getDate("orderDate"));
                System.out.println("Required Date: " + rs.getDate("requiredDate"));
                System.out.println("Status:        " + rs.getString("status"));
                Date shippedDate = rs.getDate("shippedDate");

                if(shippedDate!=null)
                    System.out.println("Shipped Date:  " + shippedDate);

                System.out.println("Customer Name: " + rs.getString("customerName"));
                first = false;

                System.out.println();
                System.out.println("Order Details");
                System.out.println("=====================");
                System.out.println();
            }
            System.out.println(rs.getString("productName"));
            System.out.println("=====================");
            System.out.println("Quantity:  " + rs.getInt("quantityOrdered"));
            System.out.println("Individual Price:  " + rs.getFloat("priceEach"));
            System.out.println("Order Line Number: " + rs.getInt("orderLineNumber"));
            System.out.println();
        }
        rs.close();

        db.commit();
        System.out.println("[Press ENTER key to end Transaction]");
        s.nextLine();
        pstmt.close();
        db.closeConn();
    }

    //TODO
    public void cancelOrder() throws SQLException {
        System.out.print("Enter Order ID: ");
        String orderID = s.next();
        s.nextLine();
        Connection conn = db.getConn();

        //Lock only the order table, to still allow selecting from the inquire products
        String query =  " SELECT " +
                        "       orderDate" +
                        "     , requiredDate" +
                        "     , shippedDate" +
                        "     , customerNumber" +
                        "     , status" +
                        " FROM orders o " +
                        " WHERE o.orderNumber = ?" +
                        " FOR UPDATE";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, orderID);

        System.out.println("[Press ENTER key to start retrieving the data]");
        s.nextLine();

        ResultSet rs = pstmt.executeQuery();
        String status="";
        while (rs.next()) {
            System.out.println("Order Information");
            System.out.println("=====================");
            System.out.println("Order Date:    " + rs.getDate("orderDate"));
            System.out.println("Required Date: " + rs.getDate("requiredDate"));
            status = rs.getString("status");
            System.out.println("Status:        " + status);
            Date shippedDate = rs.getDate("shippedDate");

            if(shippedDate!=null)
                System.out.println("Shipped Date:  " + shippedDate);

            System.out.println("Customer Number: " + rs.getInt("customerNumber"));
        }
        rs.close();

        System.out.println();
        System.out.println("Order Details");
        System.out.println("=====================");
        System.out.println();

        //Retrieve orders
        query =     " SELECT " +
                    "       productName" +
                    "     , quantityOrdered" +
                    "     , priceEach" +
                    "     , orderLineNumber " +
                    " FROM orderdetails od" +
                    " LEFT JOIN products p ON od.productCode   = p.productCode" +
                    " WHERE od.orderNumber = ?";
        pstmt = conn.prepareStatement(query);
        pstmt.setString(1, orderID);

        rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString("productName"));
            System.out.println("=====================");
            System.out.println("Quantity:  " + rs.getInt("quantityOrdered"));
            System.out.println("Individual Price:  " + rs.getFloat("priceEach"));
            System.out.println("Order Line Number: " + rs.getInt("orderLineNumber"));
            System.out.println();
        }
        rs.close();

        if(status.equals("In Process")){
            query = "UPDATE" +
                    "     orders" +
                    " SET" +
                    "     status = 'Cancelled'" +
                    " WHERE orderNumber = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, orderID);
            System.out.println("[Press ENTER key to cancel the order]");
            s.nextLine();

            pstmt.executeUpdate();
        }else
            System.out.println("Can't cancel. Order has been " + status);

        db.commit();
        System.out.println("[Press ENTER key to end Transaction]");
        s.nextLine();

        pstmt.close();
        db.closeConn();
    }

}
