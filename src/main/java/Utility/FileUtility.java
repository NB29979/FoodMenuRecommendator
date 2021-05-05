package Utility;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FileUtility {

    public static void WriteLog(String _dialogId, String _speaker, String _comments){
        try(
                final BufferedWriter bw = Files.newBufferedWriter(
                        Paths.get(System.getProperty("user.dir")+"/Logs/LogFile["+_dialogId+"].txt"),
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                final PrintWriter pw = new PrintWriter(bw, true)
        ){
            Arrays.asList(_comments.split("\\\\"))
                    .forEach(comment_ -> pw.println("[" + ZonedDateTime.now() + "]" + _speaker + ":" + comment_));

        }
        catch (Exception e){
            System.out.println("FileUtil: "+e);
        }
    }

    public static List<String> ReadFile(String _dir, String _filename) throws Exception {
        Path path_ = Paths.get(System.getProperty("user.dir") + "/" + _dir + "/" + _filename);
        return Files.readAllLines(path_, StandardCharsets.UTF_8);
    }

    public static String ReadAllLines(String _dir, String _filename) throws Exception {
        return Files.lines(
                Paths.get(System.getProperty("user.dir") + "/" + _dir + "/" + _filename),
                Charset.forName("UTF-8"))
                .collect(Collectors.joining(System.getProperty("line.separator")));
    }
}
