package updateapp.zlw.com.update.net;

public interface INetCallBack {
    void success(String response);

    void failed(Throwable throwable);
}
