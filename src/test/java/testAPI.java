import net.sf.json.JSONObject;

public class testAPI {

    public static void main(String[] args) {
        String email="autoTest@shimo.im";//接口入参
        String password="123123";//接口入参
        String res;
        try {
            JSONObject jsonObject= new JSONObject();//new一个json对象
            jsonObject.put("email",email);//将入参添加进去，多个参数就put多个
            jsonObject.put("password",password);//将入参添加进去，多个参数就put多个
            System.out.println(jsonObject.toString());//将json对象转换成字符串
            System.out.println("URL: "+FrmsUtils.URL+ FrmsUtils.Login);//打印组合后的路径；
            res = HttpRequestUtil.sendJsonWithHttp(FrmsUtils.URL+ FrmsUtils.Login,jsonObject.toString());//调用封装的请求方法，实现接口请求
            System.out.println(res);//打印接口返回结果
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FrmsUtils {
        public final static String URL = "https://release.shimodev.com/lizard-api/";
        public final static String Login = "auth/password/login";
    }
}