package com.qyl.common.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: qyl
 * @Date: 2021/3/21 17:21
 */
public class HttpUtil {

    /**
     * 连接对象
     */
    private HttpURLConnection connection;

    /**
     * 字符编码
     */
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * 读取超时时间
     */
    private int readTimeout = 10000;

    /**
     * 连接超时时间
     */
    private int connectTimeout = 10000;

    /**
     * 请求方式（GET, POST）
     */
    private String method = "GET";

    /**
     * 是否接受输入流
     */
    private boolean doInput = true;

    /**
     * 请求头
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 数据
     */
    private Map<String, String> data = new HashMap<>();

    /**
     * 实例化对象
     */
    public static HttpUtil connect(String url) throws IOException {
        return new HttpUtil((HttpURLConnection) new URL(url).openConnection());
    }

    /**
     * 禁止 new 实例
     */
    private HttpUtil() {
    }

    private HttpUtil(HttpURLConnection connection) {
        this.connection = connection;
    }

    /**
     * 获取 HttpURLConnection
     */
    public HttpURLConnection getConnection() {
        return this.connection;
    }

    /**
     * 设置读去超时时间/ms
     */
    public HttpUtil setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    /**
     * 设置链接超时时间/ms
     */
    public HttpUtil setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    /**
     * 设置请求方式
     */
    public HttpUtil setMethod(String method) {
        this.method = method;
        return this;
    }

    /**
     * 添加 Headers（例如 token）
     */
    public HttpUtil setHeaders(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * 是否接受输入流
     * 默认 true
     */
    public HttpUtil setDoInput(boolean accept) {
        this.doInput = accept;
        return this;
    }

    /**
     * 设置请求响应的编码
     */
    public HttpUtil setCharset(String charset) {
        this.charset = Charset.forName(charset);
        return this;
    }

    /**
     * 写入参数（不支持 GET 方式，GET 参数需自行在 URL 追加）
     * POST 的参数：param1=1&param2=2...
     */
    public HttpUtil setData(String key,String value) {
        data.put(key,value);
        return this;
    }

    /**
     * 发起请求
     */
    public HttpUtil execute() throws IOException {
        // 直接关闭请求
        headers.put("Connection", "close");
        // 添加请求头
        for (String key : headers.keySet()) {
            connection.setRequestProperty(key, headers.get(key));
        }
        // 设置读取超时时间为 10 秒
        connection.setReadTimeout(readTimeout);
        // 设置连接超时时间为 10 秒
        connection.setConnectTimeout(connectTimeout);
        // 设置请求方法 (GET, POST)
        connection.setRequestMethod(method.toUpperCase());
        // 接收输入流
        connection.setDoInput(doInput);

        // 写入参数（请求方法不为 GET）
        if (!data.isEmpty() && !method.equalsIgnoreCase("GET")) {
            // 启动输出流，当需要传递参数时需要开启
            connection.setDoOutput(true);
            // 添加请求参数
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, charset));
            // 写入参数
            bufferedWriter.write(getDataString());
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        // 发起请求
        connection.connect();
        return this;
    }

    private String getDataString() {
        // param1=a&param2=b...
        StringBuilder sb = new StringBuilder();
        List<Map.Entry<String, String>> list = new ArrayList<>(data.entrySet());
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, String> entry = list.get(i);
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            // 最后一个不需要 '&'
            if (i < list.size() - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * 获取响应字符串
     */
    public String getBody() {
        // 读取输入流
        try {
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String body = HttpUtil.connect("http://localhost:9090/user/login?phone=13860220001&password=123456")
                .setMethod("GET")
                .execute()
                .getBody();
        System.out.println(body);
    }
}
