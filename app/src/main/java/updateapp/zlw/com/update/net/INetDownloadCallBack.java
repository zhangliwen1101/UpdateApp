package updateapp.zlw.com.update.net;

import java.io.File;

public interface INetDownloadCallBack {
    void onstart();

    void success(File apkFile);

    void progress(long progress,long total);

    void failed(Throwable throwable);
}
