package gruzomarket.ru.tools.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "action_logs")
@Data
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String level; // INFO, WARN, SUCCESS, ERROR

    @Column(nullable = false, length = 1000)
    private String message;

    public ActionLog() {
        this.timestamp = LocalDateTime.now();
    }

    public ActionLog(String level, String message) {
        this.timestamp = LocalDateTime.now();
        this.level = level;
        this.message = message;
    }
}
