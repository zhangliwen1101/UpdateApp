package updateapp.zlw.com.update.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.util.Log;

public class NetSpeed {
	private final static String TAG = "NetSpeed";
	private long preRxBytes = 0;
	private Context mContext;
	private static NetSpeed mNetSpeed;

	private NetSpeed(Context mContext) {
		this.mContext = mContext;
	}

	public static NetSpeed getInstant(Context mContext) {
		if (mNetSpeed == null) {
			mNetSpeed = new NetSpeed(mContext);
		}
		return mNetSpeed;
	}

	public long getNetworkRxBytes() {
		int currentUid = getUid();
		Log.d(TAG, "currentUid =" + currentUid);
		if (currentUid < 0) {
			return 0;
		}
		long rxBytes = TrafficStats.getUidRxBytes(currentUid);
		/* 下句中if里的一般都为真，只能得到全部的网速 */
		if (rxBytes == TrafficStats.UNSUPPORTED) {
			Log.d(TAG, "getUidRxBytes fail !!!");/* 本函数可以只用下面一句即可 */
			rxBytes = TrafficStats.getTotalRxBytes();
		}
		return rxBytes;
	}

	public int getNetSpeed() {

		long curRxBytes = getNetworkRxBytes();
		long bytes = curRxBytes - preRxBytes;
		preRxBytes = curRxBytes;
		int kb = (int) Math.floor(bytes / 1024 + 0.5);
		return kb;
	}

	private int getUid() {
		try {
			PackageManager pm = mContext.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(
					mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
			return ai.uid;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
