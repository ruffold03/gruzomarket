package gruzomarket.ru.tools.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    private Boolean phoneVerified = false;
    private Boolean emailVerified = false;

    @Column(nullable = false)
    private String passwordHash;

    private String passwordResetToken;
    private OffsetDateTime passwordResetExpires;
    private String emailVerificationToken;

    private Boolean isActive = true;
    private Boolean isBlocked = false;

    private OffsetDateTime lastLoginAt;

    @Column
    private String city;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Customer createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Customer updatedBy;
}
