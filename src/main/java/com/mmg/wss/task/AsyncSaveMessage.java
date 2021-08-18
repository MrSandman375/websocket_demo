package com.mmg.wss.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Auther: fan
 * @Date: 2021/8/16
 * @Description:
 */
@Component
@Slf4j
public class AsyncSaveMessage {

    @Async
    public void saveMessage(String message) {
        try {
            Thread.sleep(6 * 1000);
            log.info("消息为：{}", message);
        } catch (InterruptedException e) {
            log.info("error：{}", e.getMessage());
        }
    }

}
