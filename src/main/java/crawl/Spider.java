package crawl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dao.impl.CompareDaoImpl;
import domain.Compare;
import domain.WxData;
import domain.WxImage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import utils.FileUtil;
import utils.NetUtil;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

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

    private void prey(String json, String url) {
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
            if (content_url != null && content_url.trim().length()!=0&&imgurl!=null&&imgurl.trim().length()!=0) {
                WxData data = new WxData();
                data.setUrl(content_url.replace("\\/", "/"));
                data.setImgUrl(imgurl.replace("\\/", "/"));
                data.setDatetime(datetime);
                try {
                    data.setTitle(new String(title.getBytes(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    logger.error("translate error:" + JSON.toJSONString(data));
                }
                SpiderGlobal.getInstance().spiderThreads.execute(() -> preyDetil(data));
            }
        }
    }

    @Transactional
    private void preyDetil(WxData d) {
        SpiderGlobal.getInstance().articleInc();
        logger.info("prey article count:" + SpiderGlobal.getInstance().getArticle() +
                ", spiderQueue count:" + SpiderGlobal.getInstance().spiderThreads.getQueue().size() +
                ", searchQueue count:" + SpiderGlobal.getInstance().searchThreads.getQueue().size() +
                ",downloadQueue count:" + SpiderGlobal.getInstance().downloadThreads.getQueue().size());
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
        SpiderGlobal.getInstance().searchThreads.execute(() -> searchAndSave(d));
        for (Element e : elements) {
            String imgUrl = e.attr("data-src");
            if (imgUrl != null && !"".equals(imgUrl) && (imgUrl.contains(jpg) || imgUrl.contains(png))) {
                if (checkRepeat(imgUrl.replace("\\/", "/"))) {
                    continue;
                }
                WxData data = new WxData(d.getUrl(), imgUrl.replace("\\/", "/"), d.getTitle(), tag, d.getDatetime());
                SpiderGlobal.getInstance().searchThreads.execute(() -> searchAndSave(data));
            }
        }
    }

    private void searchAndSave(WxData data) {
        try {
            WxImage wxImage = saveToLocal(data.getImgUrl());
            String result = Search.search(wxImage.getBytes());
            if (!result.equals("") && !result.equals("{}")) {
                JSONObject o = JSON.parseObject(result).getJSONArray("results").getJSONObject(0);
//                        JSONObject oo = JSON.parseObject(result).getJSONArray("results").getJSONObject(1);
                Compare c = new Compare();
                c.setTargetFile(wxImage.getName());
                c.setSourceFile(o.getString("id"));
                c.setTargetUrl(data.getUrl().replace("\\/", "/"));
                c.setSourceUrl("http://699pic.com/tupian-" + o.getString("id").replace("699pic.", "") + ".html");
                c.setTag(data.getTag());
                c.setSourceTitle(data.getTitle());
                c.setScore(o.getString("score"));
                c.setDatetime(data.getDatetime());
                c.setTargetImgUrl(data.getImgUrl());
                compareDao.save(c);
            }
        } catch (Exception e) {
            logger.error(JSON.toJSONString(data));
            e.printStackTrace();
        }
    }

    private WxImage saveToLocal(String imgUrl) throws IOException, NoSuchAlgorithmException {
        byte[] imgByte = NetUtil.get(imgUrl);
        String filename = UUID.randomUUID().toString();
        WxImage wxImage = new WxImage(filename, imgByte);
        SpiderGlobal.getInstance().downloadThreads.execute(() -> FileUtil.outPutFile(wxImage.getBytes(), Define.DIR + wxImage.getName(), false));
        return wxImage;
    }

    private boolean checkRepeat(String imgUrl) {
        List t = compareDao.findByTargetImgUrl(imgUrl);
        return t != null && t.size() != 0;
    }
}
