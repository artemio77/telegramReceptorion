package com.gmail.derevets.artem.bot.service;


import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
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
import java.io.*;
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


    public void sendAudio(File file) throws IOException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException {
        AudioFile audioFile = AudioFileIO.read(file);
        SendAudio sendAudio = new SendAudio()
                .setChatId("@music_by_rec")
                .setAudio(file)
                .setTitle(file.getName())
                .setCaption(file.getName())
                .setDuration(audioFile.getAudioHeader().getTrackLength());

        Thread thread = new Thread(() -> {
            try {
                execute(sendAudio);
            } catch (TelegramApiException e) {
                log.error(e.fillInStackTrace().getMessage());
            }

        });
        thread.start();
        log.info("File " + sendAudio.getAudio().getMediaName()
                + " send in chat " + sendAudio.getChatId() + " successfull");
        if (Thread.currentThread().isAlive()) {
            Thread.currentThread().interrupt();
            log.debug("Thread interrupted");
        }
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
