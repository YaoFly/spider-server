package crawl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dao.impl.CompareDaoImpl;
import domain.Compare;
import domain.WeiXinData;
import domain.WxImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtil;
import utils.MD5Util;
import utils.NetUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ooopic on 2017/7/27.
 */
public class SearchThread extends Thread implements BaseQueue{
    private static Logger logger = LoggerFactory.getLogger(SearchThread.class);
    private CompareDaoImpl compareDao = new CompareDaoImpl();

    private BlockingQueue<WeiXinData> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        super.run();
        boolean quit = false;
        while (!quit) {
            WeiXinData data = null;
            try {
                data = consume();
                if (data != null&&data.getUrl()!=null) {
                    WxImage wxImage = saveToLocal(data.getImgUrl());
                    String result = Search.search(wxImage.getBytes());
                    if(!result.equals("")&&!result.equals("{}")){
                        JSONObject o = JSON.parseObject(result).getJSONArray("results").getJSONObject(0);
//                        JSONObject oo = JSON.parseObject(result).getJSONArray("results").getJSONObject(1);
                        Compare c = new Compare();
                        c.setTargetFile(wxImage.getName());
                        c.setSourceFile(o.getString("id"));
                        c.setTargetUrl(data.getUrl().replace("\\/","/"));
                        c.setSourceUrl("http://699pic.com/tupian-" + o.getString("id").replace("699pic.", "") + ".html" );
                        c.setTag(data.getTag());
                        c.setSourceTitle(data.getTitle());
                        c.setScore(o.getString("score"));
                        c.setDatetime(data.getDatetime());
                        compareDao.save(c);
                    }
                }
            } catch (Exception e) {
                logger.error(JSON.toJSONString(data));
                e.printStackTrace();
            }
        }
    }

    private WxImage saveToLocal(String imgUrl) throws IOException, NoSuchAlgorithmException {
        byte[] imgByte = NetUtil.get(imgUrl);
        String filename = UUID.randomUUID().toString();
        WxImage wxImage = new WxImage(filename,imgByte);
        SpiderGlobal.getInstance().downloadQueuePut(wxImage);
        return wxImage;
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
