package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.entity.Customer;
import gruzomarket.ru.tools.entity.PasswordResetToken;
import gruzomarket.ru.tools.repository.CustomerRepository;
import gruzomarket.ru.tools.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void generateResetCode(String email) {
        if (!customerRepository.existsByEmail(email)) {
            // В целях безопасности не сообщаем, существует ли email
            return;
        }

        // Удаляем старые коды для этого email
        tokenRepository.deleteByEmail(email);

        // Генерируем 6-значный код
        String code = String.format("%06d", new Random().nextInt(1000000));

        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .code(code)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        tokenRepository.save(token);
        emailService.sendResetCode(email, code);
    }

    public boolean verifyCode(String email, String code) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndCode(email, code);
        return tokenOpt.isPresent() && !tokenOpt.get().isExpired();
    }

    @Transactional
    public boolean resetPassword(String email, String code, String newPassword) {
        if (!verifyCode(email, code)) {
            return false;
        }

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        customer.setPasswordHash(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);

        tokenRepository.deleteByEmail(email);
        return true;
    }
}
