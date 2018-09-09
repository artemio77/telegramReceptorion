package com.gmail.derevets.artem.bot.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


@Component
@PropertySource("classpath:application.properties")
@Slf4j
public class MusicMessageSenderService extends TelegramLongPollingBot {


    @Value("${bot.token}")
    private String token;
    @Value("${bot.name}")
    private String name;

    private Long THREAD_DELAY = 18000L;

    public void sendAudio(InputFile file) throws InterruptedException {

        SendAudio sendAudio = new SendAudio()
                .setChatId("@music_by_rec")
                .setAudio(file);

        Thread thread = new Thread(() -> {
            try {
                execute(sendAudio);
                if (Thread.currentThread().isAlive())
                    Thread.currentThread().interrupt();
                log.debug("Thread interrupted");
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }

        });
        thread.start();


        log.info("File " + sendAudio.getAudio().getMediaName()
                + " send in chat " + sendAudio.getChatId() + " successfull");
    }


    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String textMessage = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            log.info("Chat ID " + chatId.toString() + "message " + textMessage);
            SendMessage message = new SendMessage()
                    .setChatId(chatId)
                    .setText(textMessage);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @PostConstruct
    public void start() {
        log.info("username: {}, token: {}", name, token);
    }


}
