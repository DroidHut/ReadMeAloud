package comfolioreader.android.sample;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

       // TextView textView = (TextView) findViewById(R.id.app_text);
       // textView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, HomeActivity.class);
                startActivity(mainIntent);
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
