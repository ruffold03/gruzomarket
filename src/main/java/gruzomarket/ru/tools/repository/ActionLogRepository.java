package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    List<ActionLog> findTop50ByOrderByTimestampDesc();
}
