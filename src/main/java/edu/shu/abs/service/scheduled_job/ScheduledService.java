package edu.shu.abs.service.scheduled_job;

import edu.shu.abs.service.algorithm.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class ScheduledService {
    @Autowired
    private RecommendService recommendService;

    private boolean canExecuteFirstTask = true;

    @Scheduled(fixedRate = 30 * 60 * 1000)  // 每30分钟执行一次
    public void updateTrainEvery45Minutes() {
        if (canExecuteFirstTask) {
            String newlyUpdateTime = calculateNewlyUpdateTime(30);
            try {
                recommendService.updateTrainWithoutVerifyAdmin(newlyUpdateTime);
            } catch (ResourceAccessException re) {
                String message = re.getMessage();
                if (message.contains("Read timed out"))
                    log.info("每30分钟增量训练已开始执行");
                else if (message.contains("Connection refused"))
                    log.error("后端无法通过HTTP请求访问其他服务, 请等待后端开启服务");
                else
                    re.printStackTrace();
            }  catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行一次
    public void updateTrainDaily() {
        canExecuteFirstTask = false; // 设置标志，确保在执行此任务时不执行上一个任务
        String newlyUpdateTime = calculateNewlyUpdateTime(24 * 60); // 1天前
        try {
            recommendService.updateTrainWithoutVerifyAdmin(newlyUpdateTime);
        } catch (ResourceAccessException re) {
            String message = re.getMessage();
            if (message.contains("Read timed out"))
                log.info("每日增量训练已开始执行");
            else if (message.contains("Connection refused"))
                log.error("后端无法通过HTTP请求访问其他服务, 请等待后端开启服务");
            else
                re.printStackTrace();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            canExecuteFirstTask = true; // 恢复标志，以便下次执行第一个任务
        }
    }

    private String calculateNewlyUpdateTime(int minutesBefore) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime updateTime = currentTime.minusMinutes(minutesBefore);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return updateTime.format(formatter);
    }
}
