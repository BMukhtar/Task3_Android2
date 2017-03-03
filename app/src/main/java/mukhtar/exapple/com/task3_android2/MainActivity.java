package mukhtar.exapple.com.task3_android2;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final int TASK_CODE = 1;
    public final static int STATUS_MESSAGE = 2;
    public final static String PARAM_MESSAGE = "message";
    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String BACK_MESSAGE = "pendingIntent";


    EditText editText;
    LinearLayout ll;
    String LOG_TAG = "myLogs";
    String currentMessage = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        ll = (LinearLayout)findViewById(R.id.linLayoutForMessage);
    }

    public void send(View v){
        currentMessage = editText.getText().toString();
        if(!currentMessage.equals("")){
            PendingIntent pi = createPendingResult(TASK_CODE, new Intent(),0);
            Intent intent = new Intent(this, MyService.class)
                    .putExtra(PARAM_MESSAGE,currentMessage)
                    .putExtra(PARAM_PINTENT,pi);
            setMessage(currentMessage, false);
            startService(intent);
        }

    }


    public void setMessage(String message, boolean isBot){
        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextSize(20);
        if(isBot){
            tv.setBackgroundColor(Color.YELLOW);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        else {
            tv.setBackgroundColor(Color.GREEN);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }

        relativeLayout.addView(tv,layoutParams);
        ll.addView(relativeLayout);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "requestCode = " + requestCode + ", resultCode = "
                + resultCode);
        if(requestCode==TASK_CODE){
            if(resultCode==STATUS_MESSAGE){
                setMessage(data.getStringExtra(BACK_MESSAGE),true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,MyService.class));
    }
}
