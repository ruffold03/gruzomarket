package gruzomarket.ru.tools.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramNotificationService {

    @Value("${telegram.bot.token}") // Сохраните токен в application.properties или .yml
    private String botToken;

    @Value("${telegram.chat.id}") // Сохраните chat ID там же
    private String chatId;

    private final RestTemplate restTemplate;

    public TelegramNotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(String name, String phone, String part) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        // Подготовка параметров (как form-data)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("chat_id", chatId);
        params.add("text", "Новая заявка на автозапчасти!\nИмя: " + name + "\nТелефон: " + phone + "\nЗапчасть: " + part);
        params.add("parse_mode", "Markdown"); // Опционально для форматирования

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Отправка POST-запроса
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Обработка ошибок (опционально)
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Логируйте ошибку
            System.err.println("Ошибка отправки в Telegram: " + response.getBody());
        }
    }
}
