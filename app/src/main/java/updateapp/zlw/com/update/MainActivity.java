package updateapp.zlw.com.update;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import updateapp.zlw.com.update.updater.AppUpdater;
import updateapp.zlw.com.update.utils.AppUtils;

public class MainActivity extends AppCompatActivity {

    // 更新app按钮
    private Button updateAppBtn;

    private static final int RESULT_CODE_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 100 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateAppBtn = findViewById(R.id.update_app_btn);
        Log.d("SHA1",AppUtils.getCertificateSHA1Fingerprint(this));
        Log.d("MD5",AppUtils.getCertificateMD5Fingerprint(this));
        initEvent();
    }

    private void initEvent(){
        updateAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUpdater.getInstance().startUpdate(MainActivity.this);
            }
        });
    }
}
