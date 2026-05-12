package gruzomarket.ru.tools.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    public void sendOrderNotification(gruzomarket.ru.tools.dto.OrderDTO order) {
        StringBuilder productsList = new StringBuilder();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            productsList.append("🛒 <b>Товары в заказе:</b>\n");
            for (gruzomarket.ru.tools.dto.OrderItemDTO item : order.getItems()) {
                productsList.append(String.format("• %s | %s шт. x %s ₽\n",
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice() != null ? item.getUnitPrice().toString() : "0"));
            }
            productsList.append("\n");
        }

        String message = String.format(
                "🚚 <b>Новая заявка на сайте!</b>\n\n" +
                        "📦 <b>Заказ:</b> #%s\n" +
                        "👤 <b>Клиент:</b> %s\n" +
                        "📞 <b>Телефон:</b> %s\n" +
                        "✉️ <b>Email:</b> %s\n" +
                        "🔗 <b>Соцсеть/Ссылка:</b> %s\n" +
                        "💬 <b>Комментарий:</b> %s\n\n" +
                        "%s" +
                        "💰 <b>Сумма:</b> %s руб.\n" +
                        "📊 <b>Статус:</b> %s\n\n" +
                        "⏰ <i>%s</i>",
                order.getId(),
                order.getCustomerName(),
                order.getPhone(),
                order.getEmail() != null ? order.getEmail() : "не указан",
                order.getSocialLink() != null ? order.getSocialLink() : "не указана",
                order.getNotes() != null ? order.getNotes() : "отсутствует",
                productsList.toString(),
                order.getTotalAmount() != null ? order.getTotalAmount().toString() : "0",
                order.getStatus(),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

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
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

        sendNotification(message);
    }

    public void sendContactMessage(String name, String email, String messageBody) {
        String msg = String.format(
                "💬 <b>Новый вопрос с сайта!</b>\n\n" +
                        "👤 <b>Имя:</b> %s\n" +
                        "✉️ <b>Email:</b> %s\n" +
                        "📝 <b>Сообщение:</b>\n<i>%s</i>\n\n" +
                        "⏰ <i>%s</i>",
                name,
                email,
                messageBody,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        sendNotification(msg);
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
