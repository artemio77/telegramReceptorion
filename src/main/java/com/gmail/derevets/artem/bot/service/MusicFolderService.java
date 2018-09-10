package com.gmail.derevets.artem.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class MusicFolderService {


    private final Path myDir = Paths.get("C:\\Users\\Artem\\Downloads\\VK audio");

    private Logger logger = LoggerFactory.getLogger(MusicFolderService.class);

    private List<InputFile> listAudio = new ArrayList<>();

    private String fileName;

    @Autowired
    private MusicMessageSenderService musicMessageSenderService;


    public void monitoringChangesInFolder() {

        try {
            WatchService watcher = myDir.getFileSystem().newWatchService();
            myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watckKey = watcher.take();

            List<WatchEvent<?>> events = watckKey.pollEvents();
            for (WatchEvent event : events) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    logger.info("Modify: " + event.context().toString());
                    fileName = event.context().toString();
                    logger.info(myDir + "\\" + fileName);
                    logger.info("File " + event.context().toString() + " added to Audio List");
                    File file = new File(myDir + "\\" + fileName);
                    fileName = file.getName();

                    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("mp3")) {
                            logger.info("Start upload, {}", file.getName());
                            musicMessageSenderService.sendAudio(file);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }
}
