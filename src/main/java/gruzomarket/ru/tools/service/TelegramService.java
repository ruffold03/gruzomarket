package gruzomarket.ru.tools.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    @Value("${telegram.notifications.enabled:true}")
    private boolean notificationsEnabled;

    private DefaultAbsSender bot;

    public void sendNotification(String message) {
        if (!notificationsEnabled) {
            log.info("Telegram notifications are disabled. Message: {}", message);
            return;
        }

        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sendMessage.enableHtml(true);

            getBot().execute(sendMessage);
            log.info("Telegram notification sent: {}", message);

        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram notification: {}", e.getMessage(), e);
        }
    }

    public void sendOrderNotification(String orderId, String customerName, String customerPhone,
                                      BigDecimal totalAmount, String status) {
        String message = String.format(
                "🚚 <b>Новая заявка на сайте!</b>\n\n" +
                        "📦 <b>Заказ:</b> #%s\n" +
                        "👤 <b>Клиент:</b> %s\n" +
                        "📞 <b>Телефон:</b> %s\n" +
                        "💰 <b>Сумма:</b> %s руб.\n" +
                        "📊 <b>Статус:</b> %s\n\n" +
                        "⏰ <i>%s</i>",
                orderId,
                customerName,
                customerPhone,
                totalAmount != null ? totalAmount.toString() : "0",
                status,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        );

        sendNotification(message);
    }

    public void sendOrderStatusUpdate(String orderId, String oldStatus, String newStatus) {
        String message = String.format(
                "🔄 <b>Обновление статуса заказа!</b>\n\n" +
                        "📦 <b>Заказ:</b> #%s\n" +
                        "📊 <b>Старый статус:</b> %s\n" +
                        "📈 <b>Новый статус:</b> %s\n\n" +
                        "⏰ <i>%s</i>",
                orderId,
                oldStatus,
                newStatus,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        );

        sendNotification(message);
    }

    private synchronized DefaultAbsSender getBot() {
        if (bot == null) {
            DefaultBotOptions botOptions = new DefaultBotOptions();
            botOptions.setMaxThreads(4);
            bot = new DefaultAbsSender(botOptions) {
                @Override
                public String getBotToken() {
                    return botToken;
                }
            };
        }
        return bot;
    }
}
