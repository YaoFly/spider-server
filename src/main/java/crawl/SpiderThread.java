package crawl;

import com.alibaba.fastjson.JSON;
import domain.WeiXinData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ooopic on 2017/4/14.
 */
public class SpiderThread extends Thread implements BaseQueue{
    private static Logger logger = LoggerFactory.getLogger(SpiderThread.class);
    private Spider spider;

    private BlockingQueue<WeiXinData> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        boolean quit = false;
        spider = new Spider();
        while (!quit) {
            WeiXinData weiXinData = null;
            try {
                weiXinData = consume();
                if (weiXinData != null&&weiXinData.getUrl()!=null) {
                    spider.preyDetil(weiXinData);
                }
            } catch (InterruptedException e) {
                logger.error(JSON.toJSONString(weiXinData));
                e.printStackTrace();
            }
        }
    }

    void put(WeiXinData e) throws InterruptedException {
        queue.put(e);
    }

    private WeiXinData consume() throws InterruptedException {
        return queue.take();
    }

    public int queueSize(){
        return queue.size();
    }
}
