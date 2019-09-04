package updateapp.zlw.com.update.net;

import java.io.File;

/**
 * 网络请求接口
 */
public interface INetManager {
    void get(String url, INetCallBack callback, Object tag);
    void download(String url, File targetFile, INetDownloadCallBack callback, Object tag);
    void cancel(Object tag);
}
