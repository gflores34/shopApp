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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

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

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);
            logger.info("... generating 100 Item entities...");
            ExampleDataGenerator<Item> itemRepositoryGenerator = new ExampleDataGenerator<>(Item.class,
                    LocalDateTime.of(2022, 7, 27, 0, 0, 0));
            itemRepositoryGenerator.setData(Item::setName, DataType.FOOD_PRODUCT_NAME);
            itemRepositoryGenerator.setData(Item::setStock, DataType.NUMBER_UP_TO_1000);
            itemRepositoryGenerator.setData(Item::setItemType, DataType.SENTENCE);
            itemRepositoryGenerator.setData(Item::setPrice, DataType.NUMBER_UP_TO_100);
            itemRepositoryGenerator.setData(Item::setImgURL, DataType.BOOK_IMAGE_URL);
            itemRepository.saveAll(itemRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Customer entities...");
            ExampleDataGenerator<Customer> customerRepositoryGenerator = new ExampleDataGenerator<>(Customer.class,
                    LocalDateTime.of(2022, 7, 27, 0, 0, 0));
            customerRepositoryGenerator.setData(Customer::setName, DataType.FULL_NAME);
            customerRepositoryGenerator.setData(Customer::setState, DataType.STATE);
            customerRepositoryGenerator.setData(Customer::setBirthDate, DataType.PHONE_NUMBER);
            customerRepositoryGenerator.setData(Customer::setCreateDate, DataType.DATE_OF_BIRTH);
            customerRepositoryGenerator.setData(Customer::setGender, DataType.WORD);
            customerRepository.saveAll(customerRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Orders entities...");
            ExampleDataGenerator<Orders> ordersRepositoryGenerator = new ExampleDataGenerator<>(Orders.class,
                    LocalDateTime.of(2022, 7, 27, 0, 0, 0));
            ordersRepositoryGenerator.setData(Orders::setCustId, DataType.NUMBER_UP_TO_100);
            ordersRepositoryGenerator.setData(Orders::setTotalPrice, DataType.NUMBER_UP_TO_100);
            ordersRepositoryGenerator.setData(Orders::setStatus, DataType.WORD);
            ordersRepositoryGenerator.setData(Orders::setOrderDate, DataType.DATE_LAST_7_DAYS);
            ordersRepositoryGenerator.setData(Orders::setDeliveryDate, DataType.DATE_NEXT_7_DAYS);
            ordersRepository.saveAll(ordersRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Discount Code entities...");
            ExampleDataGenerator<DiscountCode> discountCodeRepositoryGenerator = new ExampleDataGenerator<>(
                    DiscountCode.class, LocalDateTime.of(2022, 7, 27, 0, 0, 0));
            discountCodeRepositoryGenerator.setData(DiscountCode::setCode, DataType.NUMBER_UP_TO_100);
            discountCodeRepositoryGenerator.setData(DiscountCode::setPercentOff, DataType.NUMBER_UP_TO_100);
            discountCodeRepositoryGenerator.setData(DiscountCode::setMaxDollarAmount, DataType.NUMBER_UP_TO_100);
            discountCodeRepositoryGenerator.setData(DiscountCode::setStatus, DataType.NUMBER_UP_TO_100);
            discountCodeRepositoryGenerator.setData(DiscountCode::setExpirationDate, DataType.DATE_OF_BIRTH);
            discountCodeRepository.saveAll(discountCodeRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Sale entities...");
            ExampleDataGenerator<Sale> saleRepositoryGenerator = new ExampleDataGenerator<>(Sale.class,
                    LocalDateTime.of(2022, 7, 27, 0, 0, 0));
            saleRepositoryGenerator.setData(Sale::setItemId, DataType.NUMBER_UP_TO_100);
            saleRepositoryGenerator.setData(Sale::setPercentOff, DataType.NUMBER_UP_TO_100);
            saleRepositoryGenerator.setData(Sale::setStartDate, DataType.DATE_LAST_7_DAYS);
            saleRepositoryGenerator.setData(Sale::setExpirationDate, DataType.DATE_NEXT_30_DAYS);
            saleRepository.saveAll(saleRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}