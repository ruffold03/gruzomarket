package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.entity.ActionLog;
import gruzomarket.ru.tools.repository.ActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionLogService {
    private final ActionLogRepository actionLogRepository;

    @Transactional
    public void log(String level, String message) {
        actionLogRepository.save(new ActionLog(level, message));
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void success(String message) {
        log("SUCCESS", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    public List<ActionLog> getLatestLogs() {
        return actionLogRepository.findTop50ByOrderByTimestampDesc();
    }
}
