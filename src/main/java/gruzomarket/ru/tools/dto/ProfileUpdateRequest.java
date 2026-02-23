package gruzomarket.ru.tools.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String city;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;

    private String currentPassword;

    private String email;
    private String phone;
    private String socialLink;
}
