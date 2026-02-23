package gruzomarket.ru.tools.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendResetCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("no-reply@gruzomarket.ru");
            helper.setTo(to);
            helper.setSubject("Код для восстановления пароля - GruzoMarket");

            String htmlContent = "<html>" +
                    "<body style=\"font-family: 'Montserrat', Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">"
                    +
                    "  <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 40px; border-radius: 15px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">"
                    +
                    "    <div style=\"text-align: center; margin-bottom: 30px;\">" +
                    "      <h2 style=\"color: #050505; font-size: 24px; margin: 0;\">" +
                    "        <span style=\"color: #f17228;\">Gruzo</span>Market" +
                    "      </h2>" +
                    "    </div>" +
                    "    <h1 style=\"color: #333; font-size: 20px; text-align: center; margin-bottom: 20px;\">Восстановление пароля</h1>"
                    +
                    "    <p style=\"color: #666; font-size: 16px; line-height: 1.5; text-align: center;\">Вы запросили восстановление пароля. Пожалуйста, используйте следующий код для продолжения:</p>"
                    +
                    "    <div style=\"background-color: #f8f8f8; border: 2px dashed #f17228; border-radius: 10px; padding: 20px; text-align: center; margin: 30px 0;\">"
                    +
                    "      <span style=\"font-size: 36px; font-weight: bold; color: #f17228; letter-spacing: 5px;\">"
                    + code + "</span>" +
                    "    </div>" +
                    "    <p style=\"color: #999; font-size: 14px; text-align: center; margin-top: 30px;\">Код действителен в течение 15 минут. Если вы не запрашивали восстановление пароля, просто проигнорируйте это письмо.</p>"
                    +
                    "    <div style=\"border-top: 1px solid #eee; margin-top: 40px; padding-top: 20px; text-align: center; color: #999;\">"
                    +
                    "      &copy; 2026 GruzoMarket. Все права защищены." +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке письма", e);
        }
    }
}
