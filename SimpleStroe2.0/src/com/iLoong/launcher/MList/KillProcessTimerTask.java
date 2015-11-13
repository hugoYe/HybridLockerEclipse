package com.iLoong.launcher.MList;

import java.util.TimerTask;

import android.content.Context;

public class KillProcessTimerTask extends TimerTask {

	Context mContext = null;

	public KillProcessTimerTask(Context mContext) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (!MeGeneralMethod.IsDownloadTaskRunning(mContext)
				&& !MeGeneralMethod.IsForegroundRunning(mContext)) {
			MELOG.v("ME_RTFSC", "KillProcessTimerTask  killProcess"
					+ android.os.Process.myPid());
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
