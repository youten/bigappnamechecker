
package youten.redo.bigappnamechecker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PackageReceiver extends BroadcastReceiver {
    private static final String TAG = "BigAppNameChecker";
    private static int APPNAME_BIG_LENGTH = 387000;
    private static int ID_APPNAME_LENGTH = 10000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((context == null) || (intent == null) || (intent.getData() == null)) {
            return;
        }
        Uri packageName = intent.getData();
        Log.d(TAG, "added/changed " + packageName.toString());

        // get appname len
        PackageManager pm = context.getPackageManager();
        int appNameLength = -1;
        try {
            ApplicationInfo info = pm.getApplicationInfo(
                    packageName.getEncodedSchemeSpecificPart(), 0);
            String label = pm.getApplicationLabel(info).toString();
            appNameLength = label.length();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // notify me
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.ic_launcher);
        String lengthText = "AppName Length:" + Integer.toString(appNameLength);
        if (appNameLength > APPNAME_BIG_LENGTH) {
            lengthText += " TOO BIG!";
        }
        builder.setTicker(lengthText);

        builder.setContentTitle(lengthText);
        builder.setContentText(packageName.getEncodedSchemeSpecificPart());
        Intent contentIntent = new Intent(Intent.ACTION_DELETE, packageName);
        PendingIntent pi = PendingIntent.getActivity(context, 0, contentIntent, 0);
        builder.setContentIntent(pi);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_APPNAME_LENGTH, builder.build());
    }
}
