import org.apache.commons.collections.map.ListOrderedMap;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

public class testAPI extends TestInit{

    String email;
    String pwd;
    int desktopFile;
    int desktopFolder;
    int space;
    String delFiles;
    String delSpace;
    String setCollaborator;
    String fileGuid;
    List<String> memberList;
    List<String> memberRole;
    List<String> adminList;

    @DataProvider(name = "testData")
    public Object[][] data() {
        ExcelDate excelDate = new ExcelDate();
        return excelDate.testData(new File(TestInit.class.getClassLoader().getResource("file/test.xlsx").getFile()).getPath());
    }

    @Test(dataProvider = "testData")
    public void test(HashMap<String, String> data) {
        email = data.get("email");
        System.out.println("账号：" + email);

        pwd = StringFormat(data.get("password"));
        desktopFile = Integer.parseInt(StringFormat(data.get("fileCount")));
        desktopFolder = Integer.parseInt(StringFormat(data.get("folderCount")));
        space = Integer.parseInt(StringFormat(data.get("space")));
        fileGuid = data.get("fileGuid");
        delFiles = data.get("delFiles");
        setCollaborator = data.get("setCollaborator");
        delSpace = data.get("delSpace");

        memberList = Arrays.asList(StringFormat(data.get("memberID")).replace(" ", "").split(","));
        memberRole = Arrays.asList(StringFormat(data.get("role")).replace(" ", "").split(","));
        adminList = Arrays.asList(StringFormat(data.get("adminID")).replace(" ", "").split(","));

        if(delFiles.equals("y")){
            delDesktopFiles(fileGuid);
        }
        if(delSpace.equals("y")){
            delSpace();
        }
        if(setCollaborator.equals("y")){
            setCollaborator();
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
                countFile = countFile - desktopFile;
            }
            if(countFolder == 0){
                System.out.println("没有文件夹");
            }else{
                countFolder = countFolder - desktopFolder;
            }
            api_delFile(countFile,countFolder);
        }else{
            System.out.println("没有多余文件需要删除");
        }
    }

    public void delSpace(){
        api_login(email,pwd);
        List<String> spaceGuid = api_space();

        if(spaceGuid.size() > space){
            api_delSpace(space);
        }else {
            System.out.println("没有多余协作空间需要删除");
        }
    }

    public void setCollaborator(){
        List<String> member;
        List<String> admin;
        List<String> role;
        ListOrderedMap collaboratorList;
        List<String> addMemberList = new ArrayList<>();
        List<String> delMemberList = new ArrayList<>();
        List<String> addAdminList = new ArrayList<>();
        List<String> delAdminList = new ArrayList<>();
        List<String> roleList = new ArrayList<>();

        api_login(email,pwd);
        collaboratorList = api_getCollaborator(fileGuid);
        member = (List<String>) collaboratorList.get("member");
        admin = (List<String>) collaboratorList.get("admin");

        for(String str : memberList){
            if(!member.contains(str)){
                addMemberList.add(str);
                roleList.add(memberRole.get(memberList.indexOf(str)));
            }
        }
        for(String str : member){
            if(!memberList.contains(str)){
                delMemberList.add(str);
            }
        }

        for(String str : adminList){
            if(!admin.contains(str)){
                addAdminList.add(str);
            }
        }
        for(String str : admin){
            if(!adminList.contains(str)){
                delAdminList.add(str);
            }
        }

        if(addAdminList.size()==0 || adminList.get(0).equals("0")){
            System.out.println("管理者列表成员不需要新增");
        }else{
            api_addAdmin(fileGuid,addAdminList);
        }

        if(delAdminList.size()==0 || adminList.get(0).equals("0")){
            System.out.println("管理者列表成员不需要删除");
        }else {
            api_delAdmin(fileGuid,delAdminList);
        }

        if(addMemberList.size()==0){
            System.out.println("协作者列表成员不需要新增");
        }else{
            api_addCollaborator(fileGuid,addMemberList,roleList);
        }
        if(delMemberList.size()==0){
            System.out.println("协作者列表成员不需要删除");
        }else {
            api_delCollaborator(fileGuid,delMemberList);
        }


        collaboratorList = api_getCollaborator(fileGuid);
        role = (List<String>) collaboratorList.get("role");
        roleList.clear();
        addMemberList.clear();

        for(String str : memberRole){
                if(!role.contains(str)){
                    roleList.add(str);
                    addMemberList.add(memberList.get(memberRole.indexOf(str)));
                }
        }

        if(addMemberList.size()==0){
            System.out.println("协作者列表成员权限不需要变更");
        }else {
            api_setRole(fileGuid,addMemberList,roleList);
        }

    }
}