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

    @Async(value = "asyncSaveMessage")
    public void saveMessage(String from,String to,String message) {
        log.info("保存从" + from + "发给" + to + "的消息：" + message);
    }

}
