package updateapp.zlw.com.update.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

import updateapp.zlw.com.update.R;
import updateapp.zlw.com.update.bean.DownloadBean;
import updateapp.zlw.com.update.net.INetDownloadCallBack;
import updateapp.zlw.com.update.updater.AppUpdater;
import updateapp.zlw.com.update.utils.AppUtils;
import updateapp.zlw.com.update.utils.NetSpeed;

public class UpdateVersionShowDialog extends DialogFragment {
    private static final String KEY_DOANLOW_BEAN = "download_bean";

    private DownloadBean mDownLoadBean;

    //    private TextView tvUpdate;
    private TextView tv_ok;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private NetSpeed netSpeed;
    private PackageInfo packInfo;

    public static void show(FragmentActivity activity, DownloadBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DOANLOW_BEAN, bean);
        UpdateVersionShowDialog dialog = new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(), "updateVersionShowDialog");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownLoadBean = (DownloadBean) getArguments().getSerializable(KEY_DOANLOW_BEAN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.with_update_dialog, container, false);
        bindEvents(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void bindEvents(View view) {
        RelativeLayout layout_title = (RelativeLayout) view.findViewById(R.id.layout_title);
        TextView tv_title = (TextView) view.findViewById(R.id.title);
        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        // tv_ok.setTextColor(color.hongse);
        tv_ok.setText("升级");
        tv_ok.setTextColor(Color.rgb(7, 125, 255));
        TextView tv_no = (TextView) view.findViewById(R.id.tv_no);
        if (mDownLoadBean.title.equals("") || mDownLoadBean.title == null) {
            layout_title.setVisibility(View.GONE);
        } else {
            tv_title.setText(mDownLoadBean.title);
        }
        tv_message.setText(mDownLoadBean.content);

        tv_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tv_ok.setEnabled(false);
                startDownload(mDownLoadBean.url);
//                 getDialog().dismiss();
            }
        });

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getDialog().dismiss();
            }
        });

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUpdater.getInstance().getNetManager().cancel(UpdateVersionShowDialog.this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        AppUpdater.getInstance().getNetManager().cancel(UpdateVersionShowDialog.this);
    }

    /**
     * 开始下载
     *
     * @param url
     */
    public void startDownload(String url) {
        final File targetFile = new File(getActivity().getCacheDir(), "target.apk");
        manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        AppUpdater.getInstance().getNetManager().download(url, targetFile, new INetDownloadCallBack() {

            @Override
            public void onstart() {
                Toast.makeText(getActivity(),"开始下载",Toast.LENGTH_LONG).show();
                //通知栏
                showUpdateNotify();
                netSpeed = NetSpeed.getInstant(getActivity());
                PackageManager pares = getActivity().getPackageManager();
                try {
                    packInfo = pares.getPackageInfo(getActivity().getPackageName(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("正在链接。。。", "正在链接。。。");
            }

            @Override
            public void success(File apkFile) {
                // 安装app
                Log.d("download", "success");
                builder.setTicker("apk下载完成").setContentText("下载完成")
                        .setContentTitle("优美兴" + packInfo.versionCode).setProgress(0, 0, false)
                        .setContentInfo("");
                manager.notify(0, builder.build());
                tv_ok.setEnabled(true);
                dismiss();
                String fileMd5 = AppUtils.getFileMd5(targetFile);
                Log.d("startDownload", "md5 = " + fileMd5);
                if (fileMd5 != null && fileMd5.equals(mDownLoadBean.md5)) {
                    AppUtils.installApk(getActivity(), apkFile);
                } else {
                    Toast.makeText(getContext(), "Md5检测失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void progress(long finalCurLen, long total) {
                 Log.d("download", "progress " + finalCurLen +"dsasd"+total);
                //tv_ok.setText(progress + "%");
                final int progress = (Double.valueOf(finalCurLen * 100 / total)).intValue();
                DecimalFormat  df = new DecimalFormat("0.00");
                final int net = netSpeed.getNetSpeed();
                builder.setProgress(100, progress, false)
                        .setContentText(df.format(finalCurLen / 1024.0 / 1024.0) + " M / "
                                + df.format(total / 1024.0 / 1024.0) + " M")
                        .setContentInfo(net + " KB/s");
                Log.d("download", "1:  " + netSpeed.getNetSpeed() + "");
                manager.notify(0, builder.build());
            }

            @Override
            public void failed(Throwable throwable) {
                tv_ok.setEnabled(true);
                Log.d("download", "falied " + throwable.getMessage());
            }
        }, UpdateVersionShowDialog.this);
    }

    /**
     * 状态栏显示下载的通知
     */
    protected void showUpdateNotify() {
        //第二个参数是聚到ID
        builder =new NotificationCompat.Builder(getActivity(), "SOSHENG");
        builder.setTicker("正在下载优美兴.apk").setContentTitle("优美兴.apk").setSmallIcon(R.drawable.logosmall).setProgress(100, 0,
                false).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jctlogo)).setColor(Color.parseColor("#EAA935"));
        manager.notify(0, builder.build());
    }
}
