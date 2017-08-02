package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaofly on 2017/2/21.
 */
public class FileUtil {
    public static List<String> getFileName(String filePath) {
        String path = filePath; // 路径
        List<String> list = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }

        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                list.add(fs.getName());
            }
        }
        return list;
    }

    public synchronized static void outPutFile(String content, String path,boolean append) {
        FileOutputStream opt = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                file.createNewFile();
            }
            opt = new FileOutputStream(file,append);
            opt.write(content.getBytes());
            opt.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (opt != null) {
                try {
                    opt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
