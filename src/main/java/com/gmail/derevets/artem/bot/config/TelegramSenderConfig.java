package com.gmail.derevets.artem.bot.config;

import com.gmail.derevets.artem.bot.service.MusicFolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class TelegramSenderConfig {

    @Autowired
    private MusicFolderService musicFolderService;

    @PostConstruct
    public void start() {
        for (; ; ) {
            musicFolderService.monitoringChangesInFolder();
        }
    }
}
