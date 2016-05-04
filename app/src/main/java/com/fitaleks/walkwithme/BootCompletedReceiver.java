package com.fitaleks.walkwithme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Intent startStepService = new Intent(context, StepCounterService.class);
			context.startService(startStepService);
		}
	}

}