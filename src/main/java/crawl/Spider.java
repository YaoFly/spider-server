package crawl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dao.impl.CompareDaoImpl;
import domain.WeiXinData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import utils.NetUtil;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * Created by yaofly on 2017/3/7.
 */
public class Spider {
    private static Logger logger = LoggerFactory.getLogger(Spider.class);
    private CompareDaoImpl compareDao = new CompareDaoImpl();

    void HandleStringByHtml(String str, String url) {
        String json = Jsoup.parse(str).text();
        prey(json, url);
    }

    void HandleStringByJson(String str, String url) {
        prey(str, url);
    }

    void prey(String json, String url) {
        JSONObject object = JSON.parseObject(json);
        JSONArray array = object.getJSONArray("list");
        String content_url = null;
        String title = null;
        String imgurl = null;
        Long datetime = 0L;
        for (int i = 0; i < array.size(); i++) {
            JSONObject o = array.getJSONObject(i);
            JSONObject app_msg_ext = o.getJSONObject("app_msg_ext_info");
            JSONObject comm_msg_info = o.getJSONObject("comm_msg_info");
            if (app_msg_ext != null) {
                title = app_msg_ext.getString("title");
                content_url = app_msg_ext.getString("content_url");
                imgurl = app_msg_ext.getString("cover");
            }
            if (comm_msg_info != null) {
                datetime = Long.valueOf(comm_msg_info.getString("datetime") + "000");
            }
            if (content_url != null&&!content_url.equals("")){
                WeiXinData data = new WeiXinData();
                data.setUrl(content_url);
                data.setImgUrl(imgurl);
                data.setDatetime(datetime);
                try {
                    data.setTitle(new String(title.getBytes(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    logger.error("translate error:" + JSON.toJSONString(data));
                }
                SpiderGlobal.getInstance().spiderQueuePut(data);
            }
        }
    }

    @Transactional
    void preyDetil(WeiXinData d) {
        SpiderGlobal.getInstance().article++;
        logger.info("prey article count:" + SpiderGlobal.getInstance().article + ", spiderThread count:" + SpiderGlobal.getInstance().spiderQueueSize() +
                ", searchThread count:" + SpiderGlobal.getInstance().searchQueueSize()+",downloadThread count:"+SpiderGlobal.getInstance().downloadQueueSize());
        String content = "";
        try {
            byte[] b = NetUtil.get(d.getUrl().replace("\\/", "/"));
            if (b != null) {
                content = new String(b);
            }
        } catch (Exception e) {
            logger.error(JSON.toJSONString(d));
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(content, "utf-8");
        Elements elements = doc.getElementsByTag("img");
        Element element = doc.getElementById("post-user");
        String tag = element == null ? "other" : element.text();
        String jpg = "http://mmbiz.qpic.cn/mmbiz_jpg/";
        String png = "http://mmbiz.qpic.cn/mmbiz_png/";
        // 列表缩略图
        d.setTag(tag);
        SpiderGlobal.getInstance().searchQueuePut(d);
        for (Iterator iterator = elements.iterator(); iterator.hasNext(); ) {
            Element e = (Element) iterator.next();
            String imgUrl = e.attr("data-src");
            if (imgUrl != null && !"".equals(imgUrl) && (imgUrl.contains(jpg) || imgUrl.contains(png))) {
                WeiXinData data = new WeiXinData(d.getUrl(), imgUrl, d.getTitle(), tag, d.getDatetime());
                SpiderGlobal.getInstance().searchQueuePut(data);
            }
        }
    }
}
