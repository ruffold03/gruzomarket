package gruzomarket.ru.tools.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String customerName;
    private String phone;
    private String email;
    private String notes;
    private String socialLink;
}

