package re.adjustme.de.readjustme.Persistance.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import re.adjustme.de.readjustme.Configuration.PersitenceConfiguration;

/**
 * Created by Semmel on 18.11.2017.
 */

public class TextFilePersistor {
    private final File persistenceDir = PersitenceConfiguration.getPersitanceDirectory();

    /**
     * Save Object information ( referring to toString()) to a named File, returns true on success, otherwise false
     * Important -> Object:File = 1:1
     *
     * @param object
     * @param fileName
     * @return Boolean
     */
    protected boolean save(final Object object, final String fileName) {

        try(FileWriter fw = new FileWriter(persistenceDir.getAbsolutePath() + fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(object.toString());

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
        return true;
    }

    /**
     * Returns the last saved Line of the given File
     */
    protected String loadLine(String fileName) {
        File file = new File(persistenceDir.getAbsolutePath() + fileName);
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile(file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();
                if (readByte == 0xA) {
                    if (filePointer == fileLength) {
                        continue;
                    }
                    break;
                } else if (readByte == 0xD) {
                    if (filePointer == fileLength - 1) {
                        continue;
                    }
                    break;
                }
                sb.append((char) readByte);
            }
            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileHandler != null)
                try {
                    fileHandler.close();
                } catch (IOException e) {
                /* ignore */
                }
        }

    }

    /**
     * Returns the all saved Lines of the given File
     */
    protected List<String> loadLines(String fileName) {
        File file = new File(persistenceDir.getAbsolutePath() + fileName);
        List<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {

        }
        return result;
    }

}


