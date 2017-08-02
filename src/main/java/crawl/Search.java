package crawl;

import com.alibaba.fastjson.JSON;
import domain.SearchParam;
import domain.WeiXinData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;
import utils.NetUtil;

import java.io.IOException;
import java.util.Base64;

/**
 * Created by ooopic on 2017/7/27.
 */
public class Search {
    static Logger logger = LoggerFactory.getLogger(Search.class);

    public static String search(WeiXinData data){
        String result = "";
        try {
            byte[] imgByte = NetUtil.get(data.getImgUrl());
            new BASE64Encoder().encode(imgByte);
            byte[] base64Image = Base64.getEncoder().encode(imgByte);
            result = search(base64Image);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("base64 search error:" + JSON.toJSONString(data));
        }
        return result;
    }

    public static String search(byte[] base64) throws IOException {
        SearchParam searchParam = new SearchParam();
        searchParam.setImage(new String(base64,0,base64.length,"8859_1"));
        Long startTime = System.currentTimeMillis();
        String response = NetUtil.post(Define.SERVER_URL + "/search", JSON.toJSONString(searchParam));
        Long endTime = System.currentTimeMillis();
        logger.debug("search time consuming"+(endTime - startTime));
        return response;
    }


}
