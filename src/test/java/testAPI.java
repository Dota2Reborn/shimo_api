import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class testAPI extends TestInit{

    String email;
    String pwd;
    int desktopFile;
    int desktopFolder;
    String delFiles;
    String delSpace;
    String delFileFromFolder;

    @DataProvider(name = "testData")
    public Object[][] data() {
        ExcelDate excelDate = new ExcelDate();
        return excelDate.testData(new File(TestInit.class.getClassLoader().getResource("file/test.xlsx").getFile()).getPath());
    }

    @Test(dataProvider = "testData")
    public void test(HashMap<String, String> data) {
        email = data.get("email");
        System.out.println("账号：" + email);
        NumberFormat nf = NumberFormat.getInstance();
//        pwd = data.get("password").substring(0,data.get("password").length() - 2);
//        desktopFile = Integer.parseInt(data.get("desktop_file").substring(0,data.get("desktop_file").length() - 2));
//        desktopFolder = Integer.parseInt(data.get("desktop_folder").substring(0,data.get("desktop_folder").length() - 2));
        pwd = StringFormat(data.get("password"));
        desktopFile = Integer.parseInt(StringFormat(data.get("fileCount")));
        desktopFolder = Integer.parseInt(StringFormat(data.get("folderCount")));
        delFileFromFolder = data.get("delFileFromFolder");
        delFiles = data.get("delFiles");
        delSpace = data.get("delSpace");
        if(delFiles.equals("y")){
            delDesktopFiles(delFileFromFolder);
        }else if(delSpace.equals("y")){
            delSpace();
        }

    }

    public void delDesktopFiles(String folderGuid){
        api_login(email,pwd);
        List<String> fileType = api_getFile(folderGuid);
        int countFile = Collections.frequency(fileType, "0");
        int countFolder = Collections.frequency(fileType, "1");
        if(countFile > desktopFile || countFolder > desktopFolder){
            if(countFile == 0){
                System.out.println("没有文件");
            }else{
                countFile = countFile - 1;
            }
            if(countFolder == 0){
                System.out.println("没有文件夹");
            }else{
                countFolder = countFolder - 1;
            }
            api_delFile(countFile,countFolder);
        }else{
            System.out.println("没有多余文件需要删除");
        }
    }

    public void delSpace(){
        api_login(email,pwd);
        List<String> spaceGuid = api_space();
        if(spaceGuid.size() != 0){
            api_delSpace();
        }else {
            System.out.println("没有多余协作空间需要删除");
        }
    }
}