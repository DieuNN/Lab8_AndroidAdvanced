package com.example.lab8_androidadvanced;

import java.io.File;
import java.util.ArrayList;

public class JavaForTest {
    public ArrayList<File> getAllSongsOnDevice(File file) {
        ArrayList<File> result = new ArrayList<>();

        File[] files = file.listFiles();

        assert files != null;
        for (File singleFile : files) {
            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                result.addAll(getAllSongsOnDevice(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3")) {
                    result.add(singleFile);
                }
            }
        }
        return result;
    }
}
