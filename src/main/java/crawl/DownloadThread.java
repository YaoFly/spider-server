package crawl;

import domain.WeiXinData;
import domain.WxImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtil;
import utils.MD5Util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ooopic on 2017/8/2.
 */
public class DownloadThread extends Thread implements BaseQueue{
    private static Logger logger = LoggerFactory.getLogger(DownloadThread.class);

    private BlockingQueue<WxImage> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        super.run();
        boolean quit = false;
        while (!quit) {
            try {
                WxImage wxImage = consume();
                Long l = System.nanoTime();
                FileUtil.outPutFile(wxImage.getBytes(), Define.DIR + wxImage.getName(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void put(WxImage e) throws InterruptedException {
        queue.put(e);
    }

    private WxImage consume() throws InterruptedException {
        return queue.take();
    }

    public int queueSize(){
        return queue.size();
    }
}
