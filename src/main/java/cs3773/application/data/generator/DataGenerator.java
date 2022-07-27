package cs3773.application.data.generator;

import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import cs3773.application.data.Role;
import cs3773.application.data.entity.Customer;
import cs3773.application.data.entity.DiscountCode;
import cs3773.application.data.entity.Item;
import cs3773.application.data.entity.Orders;
import cs3773.application.data.entity.Sale;
import cs3773.application.data.entity.User;
import cs3773.application.data.service.CustomerRepository;
import cs3773.application.data.service.DiscountCodeRepository;
import cs3773.application.data.service.ItemRepository;
import cs3773.application.data.service.OrdersRepository;
import cs3773.application.data.service.SaleRepository;
import cs3773.application.data.service.UserRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.plaf.nimbus.State;

@SpringComponent
public class DataGenerator {

    public Connection connectDB() throws SQLException {
        final String DB_URL = "jdbc:h2:~/src/shopDB";
        final String USER = "user";
        final String PASS = "password";

        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

        return conn;
    }



    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository,
            ItemRepository itemRepository, CustomerRepository customerRepository, OrdersRepository ordersRepository,
            DiscountCodeRepository discountCodeRepository, SaleRepository saleRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 2 Test User entities...");
            User user = new User();
            user.setName("user");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);


            User admin = new User();
            admin.setName("admin");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);



            Connection conn = connectDB();

            Statement itemStatement = conn.createStatement();
            ResultSet itemRs = itemStatement.executeQuery("SELECT * FROM ITEM");

            while(itemRs.next()){
                Item item = new Item();
                item.setName(itemRs.getString(1));
                item.setItemType(itemRs.getString(2));
                item.setStock(itemRs.getInt(3));
                item.setPrice(itemRs.getInt(4));
                item.setImgURL(itemRs.getString(5));
                itemRepository.save(item);
            }

            Statement customerStatement = conn.createStatement();
            ResultSet customerRs = customerStatement.executeQuery("SELECT * FROM CUSTOMER");

            while(customerRs.next()){
                Customer customer = new Customer();
                customer.setName(customerRs.getString(1));
                customer.setState(customerRs.getString(2));
                customer.setBirthDate(customerRs.getString(3));
                customer.setCreateDate(LocalDate.parse(customerRs.getString(4)));
                customer.setGender(customerRs.getString(5));
                customerRepository.save(customer);
            }

            Statement orderStatement = conn.createStatement();
            ResultSet orderRs = orderStatement.executeQuery("SELECT * FROM ORDERS");

            while(orderRs.next()){
                Orders orders = new Orders();
                orders.setCustId(orderRs.getInt(1));
                orders.setTotalPrice(orderRs.getInt(2));
                orders.setStatus(orderRs.getString(3));
                orders.setDiscountCode(orderRs.getString(4));
                orders.setOrderDate(LocalDate.parse(orderRs.getString(5)));
                orders.setDeliveryDate(LocalDate.parse(orderRs.getString(6)));
                ordersRepository.save(orders);
            }


            Statement discountStatement = conn.createStatement();
            ResultSet discountRs = discountStatement.executeQuery("SELECT * FROM DISCOUNT");

            while(discountRs.next()){
                DiscountCode discountCode = new DiscountCode();
                discountCode.setCode(discountRs.getInt(1));
                discountCode.setPercentOff(discountRs.getInt(2));
                discountCode.setMaxDollarAmount(discountRs.getInt(3));
                discountCode.setStatus(discountRs.getInt(4));
                discountCode.setExpirationDate(LocalDate.parse(discountRs.getString(5)));
            }

            Statement saleStatement = conn.createStatement();
            ResultSet saleRs = saleStatement.executeQuery("SELECT * FROM SALE");

            while(saleRs.next()){
                Sale sale = new Sale();
                sale.setItemId(saleRs.getInt(1));
                sale.setPercentOff(saleRs.getInt(2));
                sale.setStartDate(LocalDate.parse(saleRs.getString(3)));
                sale.setExpirationDate(LocalDate.parse((saleRs.getString(4))));
            }


        };
    }

}