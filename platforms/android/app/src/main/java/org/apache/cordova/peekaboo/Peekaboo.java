package org.apache.cordova.peekaboo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.WindowManager;

import com.facebook.react.ReactApplication;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * This class echoes a string called from JavaScript.
 */
public class Peekaboo extends CordovaPlugin {
    public static final String TAG = "PeekabooConnect";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    private int APP_REQUEST_CODE = 72;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == APP_REQUEST_CODE) {
            // Please make sure you're adding these lines to destroy react instances before starting next one
            ReactApplication reactApplication = (ReactApplication) cordova.getActivity().getApplication();;
            reactApplication.getReactNativeHost().getReactInstanceManager().destroy();
            if (resultCode == CordovaActivity.RESULT_OK && intent != null && intent.hasExtra("type")) {
                // Your code for displaying login screen
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            final JSONObject options = (args.length() > 0) ? args.getJSONObject(0) : new JSONObject();
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    executePeekaboo(options);
                } catch (Throwable t) {
                    Log.e(TAG, Log.getStackTraceString(t));
                }
            });
            return true;
        }
        return false;
    }

    public final void executePeekaboo(JSONObject options) throws JSONException {
        Intent intent = new Intent(this.cordova.getActivity(), com.peekaboosdk.MainActivity.class);
        Log.e(TAG, options.toString(2));
        intent.putExtras(getData(options));
        cordova.setActivityResultCallback (this);
        cordova.startActivityForResult(this, intent, APP_REQUEST_CODE);
    }

    public Bundle getData(JSONObject options) {
        Bundle bundle = new Bundle();

        for (Iterator<String> it = options.keys(); it.hasNext();) {
            String key = it.next();
            JSONArray arr = options.optJSONArray(key);
            Double num = options.optDouble(key);
            String str = options.optString(key);

            if (arr != null && arr.length() <= 0)
                bundle.putStringArray(key, new String[] {});

            else if (arr != null && !Double.isNaN(arr.optDouble(0))) {
                double[] newArray = new double[arr.length()];
                for (int i = 0; i < arr.length(); i++)
                    newArray[i] = arr.optDouble(i);
                bundle.putDoubleArray(key, newArray);
            } else if (arr != null && arr.optString(0) != null) {
                String[] newArray = new String[arr.length()];
                for (int i = 0; i < arr.length(); i++)
                    newArray[i] = arr.optString(i);
                bundle.putStringArray(key, newArray);
            } else if (!num.isNaN())
                bundle.putDouble(key, num);

            else if (str != null)
                bundle.putString(key, str);

            else
                System.err.println("unable to transform json to bundle " + key);
        }

        return bundle;
    }
}
