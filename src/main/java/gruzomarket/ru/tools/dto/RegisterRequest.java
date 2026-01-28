package gruzomarket.ru.tools.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phone;

    @Size(min = 6)
    private String password;

    private String city;

    private String confirmPassword;
}
