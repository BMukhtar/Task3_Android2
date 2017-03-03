package mukhtar.exapple.com.task3_android2;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {

    final String LOG_TAG = "myLogs";
    ExecutorService es;
    boolean isConversation = false;
    static int answer_id = 0;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "MyService onCreate");
        es = Executors.newFixedThreadPool(1);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MyService onDestroy");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MyService onStartCommand");

        String message = intent.getStringExtra(MainActivity.PARAM_MESSAGE);
        PendingIntent pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);
        double time = 0.1;

        if(message.equals("hi")&&!isConversation){
            isConversation = true;
        }

        if(isConversation){
            time = ((new Random()).nextInt(15)+5);
            MyRun mr = new MyRun(time, startId, pi, message);
            es.execute(mr);
        }

        if(message.equals("bye")&&isConversation){
            isConversation = false;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    void sendNotif(String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentTitle("My notification")
                        .setContentText("bot has answered: \n"+message);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(MyService.answer_id++, mBuilder.build());
    }

    class MyRun implements Runnable {

        double time;
        int startId;
        String message;
        PendingIntent pi;

        public MyRun(double time, int startId, PendingIntent pi, String message) {
            this.message = message;
            this.time = time;
            this.startId = startId;
            this.pi = pi;
            Log.d(LOG_TAG, "MyRun#" + startId + " create");
        }

        public void run() {

            String answer;
            if(message.equals("hi")){ answer="hi"; time = 0.1;}
            else if(message.equals("bye")){ answer="bye"; time = 0.1;}
            else{
                answer = "random number " + ((new Random()).nextInt(60)+10);
            }
            try {
                Thread.sleep((long)(time*1000));
                sendNotif(answer);
                pi.send( MyService.this, MainActivity.STATUS_MESSAGE, new Intent().
                        putExtra(MainActivity.BACK_MESSAGE, answer));
            } catch (CanceledException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}