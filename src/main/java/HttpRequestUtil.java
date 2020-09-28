import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestUtil
{
    /**
     * HTTP请求
     * @param surl 接口请求url
     * @param json 接口请求body-json字符串
     *
     * @return 接口返回结果
     */
    public static String sendJsonWithHttp(String surl, String json) throws Exception
    {
        URL url = new URL(surl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        conn.setRequestProperty("Referer", "https://release.shimodev.com/login?from=home");
        conn.setRequestProperty("x-requested-with","XmlHttpRequest");
        conn.setRequestProperty("x-source","lizard-desktop");
        conn.setRequestProperty("Content-Length", json.getBytes().length + "");
        conn.setRequestMethod("POST");// 提交模式
        conn.setConnectTimeout(100000);// 连接超时单位毫秒 //
        conn.setReadTimeout(200000);// 读取超时 单位毫秒
        conn.setDoOutput(true);// 是否输入参数
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.connect();
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.write(json.getBytes());
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line);
        }
        reader.close();
        conn.disconnect();

        return sb.toString();
    }
}