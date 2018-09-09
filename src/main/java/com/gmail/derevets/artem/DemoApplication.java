package com.gmail.derevets.artem;

import com.gmail.derevets.artem.bot.service.MusicMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class DemoApplication {
    @Autowired
    MusicMessageSenderService musicMessageSenderService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

    }
}

