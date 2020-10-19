import java.util.List;

public interface shimoAPI {

    void api_login(String email, String pwd);
    List<String> api_getFile(String folderGuid);
    void api_delFile(int delFileCount, int delFolderCount);
    List<String> api_space();
    void api_delSpace();
}
