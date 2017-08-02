package utils;

import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Created by yaofly on 2017/3/7.
 */
public class NetUtil {
    public static final MediaType JSON = MediaType.parse("application/json: charset=utf-8");

    static OkHttpClient client = new OkHttpClient();

    public static byte[] get(String url) throws IOException,IllegalArgumentException {
        Request request = new Request.Builder()
                .url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().bytes();
    }

    public static String post(String url,String json) throws IOException {
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return  response.body().string();
    }

}
