package skyworth.eyes_world;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;


public class MainActivity extends AppCompatActivity {

    private int periodCount = 1;
    private int rate = 0;
    private XWalkView mXWalkView;
    private RelativeLayout loading_page;
    private TextView progress;
    private AnimationSet fade = new AnimationSet(false);
    private mThread load;
    private boolean isFirstLoad = true;
    private RelativeLayout container;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlphaAnimation fade_out = new AlphaAnimation(1f, 0f);
        fade_out.setDuration(500);
        fade_out.setFillAfter(true);
        fade_out.setInterpolator(new LinearInterpolator());
        fade_out.setRepeatCount(0);

        ScaleAnimation scale = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(500);
        scale.setFillAfter(true);
        scale.setInterpolator(new LinearInterpolator());
        scale.setRepeatCount(0);

        fade.addAnimation(fade_out);
        fade.addAnimation(scale);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loading_page.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        loading_page = (RelativeLayout) findViewById(R.id.loading_page);
        progress = (TextView) findViewById(R.id.progress);

        mXWalkView = (XWalkView) findViewById(R.id.mXWalkView);
        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true);
        mXWalkView.setInitialScale(90);
        mXWalkView.setResourceClient(new mClient(mXWalkView));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mXWalkView.loadUrl("file:///android_asset/index.html");
            }
        }, 500);
        load = new mThread();
        load.start();
    }


    private class mHandler extends Handler{
        public mHandler(){
            super();
        }
        public mHandler(Looper l){
            super(l);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 0){
                setProgress();
            }
        }
    }

    public void setProgress() {
        String period = "";
        for (int i = 0; i < periodCount; i++) {
            period += ".";
        }
        if (periodCount >= 3) {
            periodCount = 1;
        } else {
            periodCount++;
        }
        progress.setText("加载中    "+rate + "%" + period);
    }

    private class mThread extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(500);
                    Message m = new Message();
                    m.what = 0;
                    new mHandler(getApplicationContext().getMainLooper()).sendMessage(m);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class mClient extends XWalkResourceClient {
        public mClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onProgressChanged(XWalkView view, int progressInPercent) {

            if(isFirstLoad) {
                String period = "";
                for (int i = 0; i < periodCount; i++) {
                    period += ".";
                }

                rate = progressInPercent;
                progress.setText("加载中    " + progressInPercent + "%" + period);
                if (progressInPercent == 100) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loading_page.startAnimation(fade);
                        }
                    }, 500);
                    load.interrupt();
                    isFirstLoad = false;
                }
            }
            super.onProgressChanged(view, progressInPercent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXWalkView != null) {
            mXWalkView.resumeTimers();
            mXWalkView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXWalkView != null) {
            mXWalkView.onDestroy();
            XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mXWalkView != null) {
            mXWalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mXWalkView != null) {
            mXWalkView.onNewIntent(intent);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK && mXWalkView.canGoBack())) {
//            mXWalkView.goBack();
//            return true;
//        }
        mXWalkView.evaluateJavascript("var app = document.getElementById(\"app\")", null);
        mXWalkView.evaluateJavascript("function fireKeyEvent(el, keyCode){" +
                "    var doc = el.ownerDocument," +
                "        win = doc.defaultView || doc.parentWindow," +
                "        evtObj;" +
                "    if(doc.createEvent){" +
                "        if(win.KeyEvent) {" +
                "            evtObj = doc.createEvent('KeyEvents');" +
                "            evtObj.initKeyEvent( \"keydown\", true, true, win, false, false, false, false, keyCode, 0 );" +
                "        }" +
                "        else {" +
                "            evtObj = doc.createEvent('UIEvents');" +
                "            Object.defineProperty(evtObj, 'keyCode', {" +
                "                get : function() { return this.keyCodeVal; }" +
                "            });" +
                "            Object.defineProperty(evtObj, 'which', {" +
                "                get : function() { return this.keyCodeVal; }" +
                "            });" +
                "            evtObj.initUIEvent( \"keydown\", true, true, win, 1 );" +
                "            evtObj.keyCodeVal = keyCode;" +
                "            if (evtObj.keyCode !== keyCode) {" +
                "                console.log(\"keyCode \" + evtObj.keyCode + \" 和 (\" + evtObj.which + \") 不匹配\");  \n" +
                "            }" +
                "        }" +
                "        el.dispatchEvent(evtObj);  " +
                "    }" +
                "    else if(doc.createEventObject){" +
                "        evtObj = doc.createEventObject();" +
                "        evtObj.keyCode = keyCode;" +
                "        el.fireEvent('on' + \"keydown\", evtObj);" +
                "    }" +
                "}" +
                "fireKeyEvent(app, " + keyCode + ");", null);


        if ((keyCode == KeyEvent.KEYCODE_BACK && mXWalkView.getNavigationHistory().canGoBack())) {
//            mXWalkView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
