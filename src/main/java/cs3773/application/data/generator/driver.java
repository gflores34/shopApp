package cs3773.application.data.generator;

import cs3773.application.data.entity.Item;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

public class driver {
    public static Connection connectDB() throws SQLException {
        final String DB_URL = "jdbc:h2:~/src/shopDB";
        final String USER = "user";
        final String PASS = "password";

        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

        return conn;
    }


    public static void main(String[] args) throws SQLException {


        Connection conn = connectDB();

        Statement itemStatement = conn.createStatement();
        Statement ordersStatement = conn.createStatement();
        Statement saleStatement = conn.createStatement();
        Statement discountCodeStatement = conn.createStatement();
        Statement customerStatement = conn.createStatement();

        itemStatement.executeUpdate("drop table if exists Item;" +
                "CREATE TABLE `Item` (" +
                "  `id`              integer     NOT NULL," +
                "  `name`            text        NOT NULL," +
                "  `itemType`        integer     DEFAULT NULL," +
                "  `stock`           integer     DEFAULT 0 NOT NULL, " +
                "  `pricecents`      integer     NOT NULL," +
                "  `image`           blob        DEFAULT NULL," +
                "  PRIMARY KEY (`id`)" +
                ")");

        saleStatement.executeUpdate("drop table if exists Sale;" +
                "CREATE TABLE `Sale` (" +
                "  `id`              integer     NOT NULL," +
                "  `itemId`          integer     NOT NULL," +
                "  `percentOff`      integer     NOT NULL," +
                "  `startDt`         text        NOT NULL," +
                "  `expireDt`        text        NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  FOREIGN KEY (`itemId`) REFERENCES Item (`id`)" +
                ")");

        discountCodeStatement.executeUpdate("drop table if exists Discount;" +
                "CREATE TABLE `Discount` (" +
                "  `code`            text        NOT NULL," +
                "  `percentOff`      integer     NOT NULL," +
                "  `maxDollarAmount` integer     NOT NULL," +
                "  `status`          integer     NOT NULL," +
                "  `expireDt`        text        NOT NULL," +
                "  PRIMARY KEY (`code`)" +
                ")");
        customerStatement.executeUpdate("drop table if exists Customer;" +
                "CREATE TABLE `Customer` (" +
                "  `id`              integer     NOT NULL," +
                "  `name`            text        DEFAULT NULL," +
                "  `state`           text        DEFAULT NULL, " +
                "  `birthDt`         text        DEFAULT NULL," +
                "  `creatDt`         text        NOT NULL," +
                "  `gender`          text        DEFAULT 'U',  " +
                "  PRIMARY KEY (`id`)" +
                ")");

        ordersStatement.executeUpdate("drop table if exists Orders;" +
                "CREATE TABLE `Orders` (" +
                "  `id`              integer     NOT NULL," +
                "  `custId`          integer     NOT NULL," +
                "  `totalPriceCents` integer     NOT NULL," +
                "  `status`          integer     NOT NULL," +
                "  `discountCode`    text        DEFAULT NULL," +
                "  `orderDt`         text        NOT NULL," +
                "  `deliverDt`       text        DEFAULT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  FOREIGN KEY (`custId`) REFERENCES Customer (`id`)," +
                "  FOREIGN KEY (`discountCode`) REFERENCES Discount (`code`)" +
                ")");

        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM ITEM");

        while(rs.next()){
            Item item = new Item();
            item.setName(rs.getString(1));
            item.setItemType(rs.getString(2));
            item.setStock(rs.getInt(3));
            item.setPrice(rs.getInt(4));
            item.setImgURL(rs.getString(5));
        }
    }
}
