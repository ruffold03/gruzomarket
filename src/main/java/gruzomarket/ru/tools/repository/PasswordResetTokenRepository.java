package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmailAndCode(String email, String code);

    void deleteByEmail(String email);

    Optional<PasswordResetToken> findTopByEmailOrderByExpiryDateDesc(String email);
}
