import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestInit implements shimoAPI{

    public CloseableHttpClient httpClient;
    public CookieStore cookies = new BasicCookieStore();
    public CloseableHttpResponse response=null;
    String entityStr = null;
    public List<String> fileType = new LinkedList<>();
    public List<String> fileGuid = new LinkedList<>();
    public List<String> spaceGuid = new LinkedList<>();
    public List<Boolean> isFileAdmin = new LinkedList<>();
    public List<String> memberList = new LinkedList<>();
    public List<String> roleList = new LinkedList<>();
    public List<String> adminList = new LinkedList<>();

    @BeforeClass
    public void firstMethod(){

    }

    @BeforeMethod
    public void setUp() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build();
        httpClient = HttpClients.custom().setDefaultCookieStore(cookies).setDefaultRequestConfig(defaultRequestConfig).build();


        fileType.clear();
        fileGuid.clear();
        spaceGuid.clear();
        isFileAdmin.clear();
        memberList.clear();
        adminList.clear();
        roleList.clear();
        System.out.println("---------------------------------------");
    }

    @AfterMethod
    public void tearDown() {
        try {
            httpClient.close();
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("---------------------------------------");
    }

    @AfterClass
    public void lastMethod() {

    }


    public class testUrl {
        public final static String URL = "https://release.shimodev.com/lizard-api";
        public final static String Login = "/auth/password/login";
        public final static String getFileList = "/files";
        public final static String delFile = "/files";
        public final static String getSpace = "/spaces";
        public final static String delSpace = "/spaces";
        public final static String getCollaborator = "/v2/files/{guid}/collaborators";
        public final static String addCollaborator = "/files/{guid}/collaborators";
        public final static String delCollaborator = "/files/{guid}/collaborators/{id}";
        public final static String setCollaborator = "/files/{guid}/collaborators/{id}";
        public final static String addAdmin = "/files/{guid}/admins/{id}";
        public final static String delAdmin = "/files/{guid}/admins/{id}";
    }

    public String StringFormat(String s){
        if(s.equals(null) || s.equals("")){
            return "0";
        }else if(s.contains(".0")){
            String result = s.substring(0,s.length() - 2);
            return result;
        }else {
            return s;
        }
    }

    /**
     * 用户邮箱登录
     *
     * @author 刘晨
     * @Time 2020-10-19
     */
    @Override
    public void api_login(String email, String pwd) {
        HttpPost httpPost = new HttpPost(testUrl.URL + testUrl.Login);

        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        httpPost.addHeader("Referer", "https://release.shimodev.com/login?from=home");
        httpPost.addHeader("x-requested-with", "XmlHttpRequest");
        httpPost.addHeader("x-source", "lizard-desktop");

        try {
            //参数封装对象
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", pwd));

            // 使用URL实体转换工具
            UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entityParam);

            // 执行请求
            response = httpClient.execute(httpPost);

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,204,"登录失败");
            System.out.println("登录成功");

        } catch (Exception e) {
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：登录失败");
            assertTrue(false);
        }
    }

    /**
     * 获取用户桌面/指定协作空间/指定文件夹的文件
     * @return List<String>
     *
     * @author 刘晨
     * @Time 2020-10-19
     */
    @Override
    public List<String> api_getFile(String folderGuid){

        HttpGet httpGet = new HttpGet(testUrl.URL + testUrl.getFileList + "?collaboratorCount=true&folder=" + folderGuid);
        httpGet.addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
        httpGet.addHeader("Referer","https://release.shimodev.com/login?from=home");
        httpGet.addHeader("x-requested-with","XmlHttpRequest");
        httpGet.addHeader("x-source","lizard-desktop");
        httpGet.addHeader("Cookie",cookies.getCookies().get(0).getName()+"="+cookies.getCookies().get(0).getValue());

        try{
            // 执行请求
            response = httpClient.execute(httpGet);

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,200,"获取桌面文件失败");
            System.out.println("获取桌面文件成功");

            // 获得响应的实体对象
            HttpEntity entity=response.getEntity();
            entityStr = EntityUtils.toString(entity,"UTF-8");

            String json = "{\"data\":" +  entityStr + "}";
            JSONObject jsonObject = JSONObject.fromObject(json);

            JSONArray array = (JSONArray)jsonObject.get("data");
            for(int i=0;i<array.size();i++){
                JSONObject subObject = (JSONObject)array.get(i);
                fileType.add(subObject.get("is_folder").toString());
                fileGuid.add(subObject.get("guid").toString());
            }

        }catch(Exception e){
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：获取桌面文件失败");
            assertTrue(false);
        }
        return fileType;
    }

    /**
     * 删除文件
     *
     * @author 刘晨
     * @Time 2020-10-19
     */
    @Override
    public void api_delFile(int delFileCount, int delFolderCount) {
        HttpDelete httpDelete = new HttpDelete(testUrl.URL + testUrl.delFile);
        httpDelete.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        httpDelete.addHeader("Referer", "https://release.shimodev.com/login?from=home");
        httpDelete.addHeader("x-requested-with", "XmlHttpRequest");
        httpDelete.addHeader("x-source", "lizard-desktop");
        httpDelete.addHeader("Cookie", cookies.getCookies().get(0).getName() + "=" + cookies.getCookies().get(0).getValue());

        try {
            List<String> delFileList = new LinkedList<>();
            delFileList.addAll(fileGuid);
            if (delFileCount > 0) {
                for (int i = 0; i < fileType.size(); i++) {
                    if (fileType.get(i).equals("1")) {
                        delFileList.remove(fileGuid.get(i));
                    }
                }

                for (int i = 0; i < delFileCount; i++) {
                    httpDelete.setURI(URI.create(testUrl.URL + testUrl.delFile + "/" + delFileList.get(i)));
                    // 执行请求
                    response = httpClient.execute(httpDelete);
                }
            }
            if (delFolderCount > 0) {
                List<String> delFolderList = new LinkedList<>();
                delFolderList.addAll(fileGuid);
                if (delFileCount > 0) {
                    for (int i = 0; i < fileType.size(); i++) {
                        if (fileType.get(i).equals("0")) {
                            delFolderList.remove(fileGuid.get(i));
                        }
                    }
                }

                for (int i = 0; i < delFolderCount; i++) {
                    httpDelete.setURI(URI.create(testUrl.URL + testUrl.delFile + "/" + delFolderList.get(i)));
                    // 执行请求
                    response = httpClient.execute(httpDelete);
                }

            }

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,204,"删除文件失败");
            System.out.println("删除文件成功");
        }catch(Exception e){
                e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：删除文件失败");
            assertTrue(false);
        }
    }

    /**
     * 获取用户所有协作空间
     * @return List<String>
     *
     * @author 刘晨
     * @Time 2020-10-19
     */
    @Override
    public List<String> api_space(){
        HttpGet httpGet = new HttpGet(testUrl.URL+testUrl.getSpace);
        httpGet.addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
        httpGet.addHeader("Referer","https://release.shimodev.com/login?from=home");
        httpGet.addHeader("x-requested-with","XmlHttpRequest");
        httpGet.addHeader("x-source","lizard-desktop");
        httpGet.addHeader("Cookie",cookies.getCookies().get(0).getName()+"="+cookies.getCookies().get(0).getValue());

        try{
            // 执行请求
            response = httpClient.execute(httpGet);

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,200,"获取协作空间列表失败");
            System.out.println("获取协作空间列表成功");

            // 获得响应的实体对象
            HttpEntity entity=response.getEntity();
            entityStr = EntityUtils.toString(entity,"UTF-8");

            JSONObject jsonObject = JSONObject.fromObject(entityStr);
            JSONArray array = (JSONArray)jsonObject.get("nodes");
            for(int i=0;i<array.size();i++){
                JSONObject subObject = (JSONObject)array.get(i);
                spaceGuid.add(subObject.get("guid").toString());
                isFileAdmin.add((Boolean) subObject.get("isFileAdmin"));
            }

        }catch(Exception e){
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：获取协作空间列表失败");
            assertTrue(false);
        }
        return spaceGuid;
    }

    /**
     * 删除协作空间
     *
     * @author 刘晨
     * @Time 2020-10-19
     */
    @Override
    public void api_delSpace(int space_count) {
        HttpDelete httpDelete = new HttpDelete(testUrl.URL + testUrl.delSpace);
        httpDelete.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        httpDelete.addHeader("Referer", "https://release.shimodev.com/login?from=home");
        httpDelete.addHeader("x-requested-with", "XmlHttpRequest");
        httpDelete.addHeader("x-source", "lizard-desktop");
        httpDelete.addHeader("Cookie", cookies.getCookies().get(0).getName() + "=" + cookies.getCookies().get(0).getValue());

        try {
            List<String> delSpaceList = new LinkedList<>();
            delSpaceList.addAll(spaceGuid);

            for (int i = 0; i < spaceGuid.size(); i++) {
                if (isFileAdmin.get(i).equals(false)) {
                    delSpaceList.remove(spaceGuid.get(i));
                }
            }

            if(delSpaceList.size() >= space_count){
                for(int i = 0; i<space_count; i++){
                    delSpaceList.remove(i);
                }
            }

            if(delSpaceList.size() == 0 ){
                System.out.println("没有多余的协作空间需要删除");
                return;
            } else {
                for (int i = 0; i < delSpaceList.size(); i++) {
                    httpDelete.setURI(URI.create(testUrl.URL + testUrl.delSpace + "/" + delSpaceList.get(i)));
                    // 执行请求
                    response = httpClient.execute(httpDelete);
                }
            }

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,204,"删除协作空间失败");
            System.out.println("删除协作空间成功");
        }catch(Exception e){
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：删除协作空间失败");
            assertTrue(false);
        }
    }

    /**
     * 获取协作者列表
     *
     * @author 刘晨
     * @Time 2020-10-23
     */
    public ListOrderedMap api_getCollaborator(String guid){
        memberList.clear();
        adminList.clear();
        roleList.clear();
        ListOrderedMap result = new ListOrderedMap();
        String test = testUrl.getCollaborator.replace("{guid}",guid);
        HttpGet httpGet = new HttpGet(testUrl.URL + test + "?includeInherited=false&includeAdmin=true");
        httpGet.addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
        httpGet.addHeader("Referer","https://release.shimodev.com/login?from=home");
        httpGet.addHeader("x-requested-with","XmlHttpRequest");
        httpGet.addHeader("x-source","lizard-desktop");
        httpGet.addHeader("Cookie",cookies.getCookies().get(0).getName()+"="+cookies.getCookies().get(0).getValue());

        try{
            // 执行请求
            response = httpClient.execute(httpGet);

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,200,"获取协作者列表失败");
            System.out.println("获取协作者列表成功");

            // 获得响应的实体对象
            HttpEntity entity=response.getEntity();
            entityStr = EntityUtils.toString(entity,"UTF-8");

            JSONObject jsonObject = JSONObject.fromObject(entityStr);
            JSONArray array_member = (JSONArray)jsonObject.get("roles");
            for(int i=0;i<array_member.size();i++){
                JSONObject subObject = (JSONObject)array_member.get(i);
                memberList.add(subObject.get("id").toString());
                roleList.add(subObject.get("role").toString());
            }

            JSONArray array_admin = (JSONArray)jsonObject.get("admins");
            for(int i=0;i<array_admin.size();i++){
                JSONObject subObject = (JSONObject)array_admin.get(i);
                adminList.add(subObject.get("id").toString());
            }

            result.put("member",memberList);
            result.put("admin",adminList);
            result.put("role",roleList);

        }catch(Exception e){
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：获取协作者列表失败");
            assertTrue(false);
        }

        return result;
    }

    /**
     * 添加协作者
     *
     * @author 刘晨
     * @Time 2020-10-23
     */
    public void api_addCollaborator(String guid,List<String> id,List<String> role){
        String test = testUrl.addCollaborator.replace("{guid}",guid);

        HttpPost httpPost = new HttpPost(testUrl.URL + test);

        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        httpPost.addHeader("Referer", "https://release.shimodev.com/login?from=home");
        httpPost.addHeader("x-requested-with", "XmlHttpRequest");
        httpPost.addHeader("x-source", "lizard-desktop");
        httpPost.addHeader("Cookie", cookies.getCookies().get(0).getName() + "=" + cookies.getCookies().get(0).getValue());

        try {
            for (int i = 0; i < id.size(); i++) {
                //参数封装对象
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("userId",id.get(i)));
                params.add(new BasicNameValuePair("role",role.get(i)));

                // 使用URL实体转换工具
                UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(params, "UTF-8");
                httpPost.setEntity(entityParam);

                // 执行请求
                response = httpClient.execute(httpPost);
            }

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,200,"添加协作者失败");
            System.out.println("添加协作者成功");

        } catch (Exception e) {
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：添加协作者失败");
            assertTrue(false);
        }

    }


    /**
     * 删除协作者列表
     *
     * @author 刘晨
     * @Time 2020-10-23
     */
    public void api_delCollaborator(String guid,List<String> id){
        try {
            for (int i = 0; i < id.size(); i++) {
                String test = testUrl.delCollaborator.replace("{guid}",guid).replace("{id}",id.get(i));
                HttpDelete httpDelete = new HttpDelete(testUrl.URL + test);
                httpDelete.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                httpDelete.addHeader("Referer", "https://release.shimodev.com/login?from=home");
                httpDelete.addHeader("x-requested-with", "XmlHttpRequest");
                httpDelete.addHeader("x-source", "lizard-desktop");
                httpDelete.addHeader("Cookie", cookies.getCookies().get(0).getName() + "=" + cookies.getCookies().get(0).getValue());

                response = httpClient.execute(httpDelete);
            }

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,204,"协作者删除失败");
            System.out.println("协作者删除成功");
        }catch(Exception e){
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：协作者删除失败");
            assertTrue(false);
        }
    }

    /**
     * 添加管理者
     *
     * @author 刘晨
     * @Time 2020-10-23
     */
    public void api_addAdmin(String guid,List<String> id){
        try{
            for (int i = 0; i < id.size(); i++) {
                String test = testUrl.addAdmin.replace("{guid}", guid);
                HttpPut httpPut = new HttpPut(testUrl.URL + test);
                httpPut.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                httpPut.addHeader("Referer", "https://release.shimodev.com/login?from=home");
                httpPut.addHeader("x-requested-with", "XmlHttpRequest");
                httpPut.addHeader("x-source", "lizard-desktop");
                httpPut.addHeader("Cookie", cookies.getCookies().get(0).getName() + "=" + cookies.getCookies().get(0).getValue());

                response = httpClient.execute(httpPut);

                int StatusCode = response.getStatusLine().getStatusCode();
                assertEquals(StatusCode,204,"管理者添加失败");
                System.out.println("管理者添加成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：管理者添加失败");
            assertTrue(false);
        }
    }

    /**
     * 删除管理者列表
     *
     * @author 刘晨
     * @Time 2020-10-23
     */
    public void api_delAdmin(String guid,List<String> id){
        try {
            for (int i = 0; i < id.size(); i++) {
                String test = testUrl.delAdmin.replace("{guid}",guid).replace("{id}",id.get(i));
                HttpDelete httpDelete = new HttpDelete(testUrl.URL + test);
                httpDelete.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                httpDelete.addHeader("Referer", "https://release.shimodev.com/login?from=home");
                httpDelete.addHeader("x-requested-with", "XmlHttpRequest");
                httpDelete.addHeader("x-source", "lizard-desktop");
                httpDelete.addHeader("Cookie", cookies.getCookies().get(0).getName() + "=" + cookies.getCookies().get(0).getValue());

                response = httpClient.execute(httpDelete);
            }

            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,204,"管理者删除失败");
            System.out.println("管理者删除成功");
        }catch(Exception e){
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：管理者删除失败");
            assertTrue(false);
        }
    }

    /**
     * 修改协作者权限
     *
     * @author 刘晨
     * @Time 2020-10-23
     */
    public void api_setRole(String guid,List<String> id,List<String> role){
        try {
            for (int i = 0; i < id.size(); i++) {
                String test = testUrl.setCollaborator.replace("{guid}", guid).replace("{id}", id.get(i));
                HttpPatch httpPatch = new HttpPatch(testUrl.URL + test);
                httpPatch.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                httpPatch.addHeader("Referer", "https://release.shimodev.com/login?from=home");
                httpPatch.addHeader("x-requested-with", "XmlHttpRequest");
                httpPatch.addHeader("x-source", "lizard-desktop");
                httpPatch.addHeader("Cookie", cookies.getCookies().get(0).getName() + "=" + cookies.getCookies().get(0).getValue());

                //参数封装对象
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("role",role.get(i)));

                // 使用URL实体转换工具
                UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(params, "UTF-8");
                httpPatch.setEntity(entityParam);

                // 执行请求
                response = httpClient.execute(httpPatch);
            }
            int StatusCode = response.getStatusLine().getStatusCode();
            assertEquals(StatusCode,200,"协作者权限修改失败");
            System.out.println("协作者权限修改成功");
        }catch(Exception e){
            e.printStackTrace();
        }catch(AssertionError e){
            System.out.println("Error：协作者权限修改失败");
            assertTrue(false);
        }
    }

}