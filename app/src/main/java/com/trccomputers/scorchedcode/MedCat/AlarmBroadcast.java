package com.trccomputers.scorchedcode.MedCat;
import android.app.*;
import android.content.*;
import android.media.*;
import android.support.v4.app.*;
import android.util.*;
import android.graphics.*;

public class AlarmBroadcast extends BroadcastReceiver
{
	public static final String EXTRA_MEDICATION = "Meds";
	@Override
	public void onReceive(Context p1, Intent p2)
	{
		NotificationCompat.Builder notif = new NotificationCompat.Builder(p1);
		notif.setContentTitle("Take your meds!").setContentText("Your " + p2.getExtras().getString(EXTRA_MEDICATION) + " is due!")
		.setSmallIcon(R.drawable.notification)
		.setLargeIcon(BitmapFactory.decodeResource(p1.getResources(), R.drawable.icon));
		((NotificationManager)p1.getSystemService(p1.NOTIFICATION_SERVICE)).notify(0, notif.build());
	}
	
}
