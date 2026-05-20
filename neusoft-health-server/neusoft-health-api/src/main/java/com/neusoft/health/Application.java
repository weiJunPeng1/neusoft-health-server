package com.neusoft.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 东软健康咨询系统启动类
 * <p>
 * Spring Boot应用主入口，启用定时任务调度功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    /**
     * 应用程序启动入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
