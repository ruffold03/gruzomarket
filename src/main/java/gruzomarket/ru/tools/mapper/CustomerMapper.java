package gruzomarket.ru.tools.mapper;

import gruzomarket.ru.tools.dto.RegisterRequest;
import gruzomarket.ru.tools.entity.Customer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(RegisterRequest dto, PasswordEncoder encoder) {
        Customer customer = new Customer();

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());

        customer.setPasswordHash(encoder.encode(dto.getPassword()));

        customer.setIsActive(true);
        customer.setIsBlocked(false);
        customer.setEmailVerified(false);
        customer.setPhoneVerified(false);

        return customer;
    }
}

