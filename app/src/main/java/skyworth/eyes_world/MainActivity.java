package skyworth.eyes_world;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;


import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;


public class MainActivity extends AppCompatActivity {

    private XWalkView mXWalkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mXWalkView = (XWalkView) findViewById(R.id.mXWalkView);
        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true);
        mXWalkView.setInitialScale(90);

        mXWalkView.loadUrl("file:///android_asset/index.html");
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
        mXWalkView.evaluateJavascript("var app = document.getElementById(\"app\")",null);
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
                "fireKeyEvent(app, "+keyCode+");", null);

//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            mXWalkView.evaluateJavascript("menudown()", null);
//        }

        if ((keyCode == KeyEvent.KEYCODE_BACK && mXWalkView.getNavigationHistory().canGoBack())) {
//            mXWalkView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
//        return true;
    }

}
