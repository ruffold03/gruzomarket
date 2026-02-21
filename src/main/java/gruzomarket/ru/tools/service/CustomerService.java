package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.dto.ProfileUpdateRequest;
import gruzomarket.ru.tools.dto.RegisterRequest;
import gruzomarket.ru.tools.entity.Customer;
import gruzomarket.ru.tools.exception.UnauthorizedException;
import gruzomarket.ru.tools.mapper.CustomerMapper;
import gruzomarket.ru.tools.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        // Проверяем email
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже зарегистрирован");
        }
        
        // Проверяем телефон (если он указан)
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            // Очищаем телефон от всего, кроме цифр для проверки
            String cleanPhone = request.getPhone().replaceAll("\\D+", "");
            if (customerRepository.existsByPhone(cleanPhone.substring(1))) { // Ищем по номеру без +7
                throw new IllegalArgumentException("Пользователь с таким номером телефона уже зарегистрирован");
            }
        }

        Customer customer = customerMapper.toEntity(request, passwordEncoder);
        customerRepository.save(customer);
    }

    @Transactional
    public Customer updateProfile(ProfileUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isEmpty()) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), customer.getPasswordHash())) {
                throw new UnauthorizedException("Неверный текущий пароль");
            }
            if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
                customer.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            }
        }

        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getCity() != null) {
            customer.setCity(request.getCity());
        }

        return customerRepository.save(customer);
    }

    public Customer getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
}

