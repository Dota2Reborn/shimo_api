import net.sf.json.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Test;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



public class NewApiTest {

    private CookieStore cookies = new BasicCookieStore();
    CloseableHttpResponse response=null;
    String entityStr = null;
    List<String> fileType = new LinkedList<>();
    List<String> fileGuid = new LinkedList<>();

    @Test
    public void login() throws IOException {
        // 获取连接客户端工具
//        CloseableHttpClient httpClient=HttpClients.createDefault();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookies).build();
        // 创建POST请求对象
        HttpPost httpPost = new HttpPost(testUrl.URL+testUrl.Login);

        // httpPost.addHeader post请求 header

        httpPost.addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
        httpPost.addHeader("Referer","https://release.shimodev.com/login?from=home");
        httpPost.addHeader("x-requested-with","XmlHttpRequest");
        httpPost.addHeader("x-source","lizard-desktop");


//        List<Header> headerList= Lists.newArrayList();
//        headerList.add(new BasicHeader(HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded; charset=UTF-8"));
//        headerList.add(new BasicHeader(HttpHeaders.USER_AGENT,"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36"));

        //参数封装对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        try{
            params.add(new BasicNameValuePair("email","autoTest@shimo.im"));
            params.add(new BasicNameValuePair("password","123123"));


            // 使用URL实体转换工具
            UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(params, "UTF-8");

            httpPost.setEntity(entityParam);
            // 执行请求
            response = httpClient.execute(httpPost);

            int StatusCode = response.getStatusLine().getStatusCode();
            Assert.assertEquals(204,StatusCode);
            System.out.println("登录成功");



        }catch(Exception e){
            e.printStackTrace();
        }finally {

            //释放资源
            if(httpClient!=null) {
                httpClient.close();
            }
            if (response!=null){
                response.close();
            }
        }


        httpClient = HttpClients.custom().setDefaultCookieStore(cookies).build();
//        HttpPost httpPost1 = new HttpPost(testUrl.URL+testUrl.getFileList);
        HttpGet httpGet = new HttpGet(testUrl.URL+testUrl.getFileList);

        httpGet.addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
        httpGet.addHeader("Referer","https://release.shimodev.com/login?from=home");
        httpGet.addHeader("x-requested-with","XmlHttpRequest");
        httpGet.addHeader("x-source","lizard-desktop");



        try{
            params.clear();
            params.add(new BasicNameValuePair("collaboratorCount","true"));
            System.out.println(params);
            // 使用URL实体转换工具
            UrlEncodedFormEntity entityParam1 = new UrlEncodedFormEntity(params, "UTF-8");
            System.out.println(entityParam1);

//            httpGet.setParams(entityParam1).setEntity(entityParam1);
            httpGet.addHeader("Cookie",cookies.getCookies().get(0).getName()+"="+cookies.getCookies().get(0).getValue());
            System.out.println(httpGet);
            // 执行请求
            response = httpClient.execute(httpGet);

            // 获得响应的实体对象
            HttpEntity entity=response.getEntity();

            entityStr = EntityUtils.toString(entity,"UTF-8");
            System.out.println("接口返回结果是:="+entityStr);

            String stringToJson = trimstart(entityStr,"[");
            stringToJson = trimend(stringToJson,"]");
            System.out.println("接口返回结果是(stringToJson):="+stringToJson);

            String json = "{\"data\":" +  entityStr + "}";

            System.out.println("接口返回结果是(json):="+json);
            JSONObject jsonObject = JSONObject.fromObject(json);


            System.out.println("接口返回结果是(jsonObject):="+jsonObject);
            System.out.println("接口返回结果是(size):="+jsonObject.size());

//            JSONArray jsonArray = JSONArray.fromObject(entityStr);
//            System.out.println("接口返回结果是(jsonArray):="+jsonArray);
//            jsonArray.getJSONArray(0)
            //todo
            JSONArray array = (JSONArray)jsonObject.get("data");

            System.out.println("接口返回结果是(size):="+array.size());
            System.out.println("接口返回结果是(size):="+array);
            for(int i=0;i<array.size();i++){
                System.out.println("---------------");
                JSONObject subObject = (JSONObject)array.get(i);
                System.out.println("id="+subObject.get("is_folder"));
                System.out.println("id="+subObject.get("guid"));
                fileType.add(subObject.get("is_folder").toString());
                fileGuid.add(subObject.get("guid").toString());

            }
            System.out.println("fileType="+fileType);
            System.out.println("fileGuid="+fileGuid);
            int countFile = Collections.frequency(fileType, "0");
            int countFolder = Collections.frequency(fileType, "1");
            System.out.println("countFile="+countFile);
            System.out.println("countFolder="+countFolder);
            if(array.size()==2||fileType.get(0)=="0"||fileType.get(1)=="1"){
                System.out.println("数据正常");
            }else{
                for(int i = 0; i<fileType.size();i++){
                    if(fileType.get(i).equals("1")){
                        fileGuid.remove(i);
                    }
                }
            }


            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine().getReasonPhrase());
        }catch(Exception e){
            e.printStackTrace();
        }finally {

            //释放资源
            if(httpClient!=null) {
                httpClient.close();
            }
            if (response!=null){
                response.close();
            }
        }



        httpClient = HttpClients.custom().setDefaultCookieStore(cookies).build();
//        HttpPost httpPost1 = new HttpPost(testUrl.URL+testUrl.getFileList);
        HttpDelete httpDelete = new HttpDelete(testUrl.URL+testUrl.delFile);

        httpDelete.addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
        httpDelete.addHeader("Referer","https://release.shimodev.com/login?from=home");
        httpDelete.addHeader("x-requested-with","XmlHttpRequest");
        httpDelete.addHeader("x-source","lizard-desktop");
        httpDelete.addHeader("Cookie", cookies.getCookies().get(0).getName() + "=" + cookies.getCookies().get(0).getValue());
        params.clear();


        try {

            for(int i = 0; i<fileGuid.size()-1; i++){
                httpDelete.setURI(URI.create(testUrl.URL+ testUrl.delFile + "/" + fileGuid.get(i)));
                System.out.println(httpDelete.getURI());
                // 执行请求
                response = httpClient.execute(httpDelete);
            }
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine().getReasonPhrase());
        }catch(Exception e){
            e.printStackTrace();
        }finally {

            //释放资源
            if(httpClient!=null) {
                httpClient.close();
            }
            if (response!=null){
                response.close();
            }
        }

    }



    public class testUrl {
        public final static String URL = "https://release.shimodev.com/lizard-api";
        public final static String Login = "/auth/password/login";
        public final static String getFileList = "/files";
        public final static String delFile = "/files";

    }

    /*
     * 删除开头字符串
     */
    public static String trimstart(String inStr, String prefix) {
        if (inStr.startsWith(prefix)) {
            return (inStr.substring(prefix.length()));
        }
        return inStr;
    }
    /*
     * 删除末尾字符串
     */
    public static String trimend(String inStr, String suffix) {
        if (inStr.endsWith(suffix)) {
            return (inStr.substring(0,inStr.length()-suffix.length()));
        }
        return inStr;
    }
}