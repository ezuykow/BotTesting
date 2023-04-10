package com.example.bottesting.excel.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author ezuykow
 */
@Component
public class FileDownloader {

    Logger logger = LoggerFactory.getLogger(FileDownloader.class);

    @Value("${telegram.bot.token}")
    private String token;

    public File getFile(String fileId) {
        try {
            return downloadFile(fileUrl(fileId), newTempFile());
        } catch (IOException e) {
            logger.error("Не получилось стянуть файл \uD83E\uDD74");
            throw new RuntimeException(e);
        }
    }

    private String getFilePath(String fileId) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fileInfoUrl(fileId).openStream()))){
            return new JSONObject(in.readLine()).getJSONObject("result").getString("file_path");
        } catch (JSONException e) {
            logger.error("Кривой JSON от Дурова \uD83E\uDD74");
            return null;
        }
    }

    private File downloadFile(URL fileUrl, File target) throws IOException {
        try (ReadableByteChannel rbc = Channels.newChannel(fileUrl.openStream());
             FileOutputStream fos = new FileOutputStream(target)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            return target;
        }
    }

    private URL fileInfoUrl(String fileId) throws IOException {
        return new URL("https://api.telegram.org/bot" + token + "/getFile?file_id=" + fileId);
    }

    private URL fileUrl(String fileId) throws IOException {
        return new URL("https://api.telegram.org/file/bot" + token + "/" + getFilePath(fileId));
    }

    private File newTempFile() throws IOException {
        File file = File.createTempFile("question", ".xlsx");
        file.deleteOnExit();
        return file;
    }
}
