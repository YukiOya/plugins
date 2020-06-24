package io.flutter.plugins.webviewflutter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import io.flutter.plugin.common.PluginRegistry;

class FlutterWebChromeClient extends WebChromeClient implements PluginRegistry.ActivityResultListener {
    private static final int FILECHOOSER_REQUESTCODE = 1;
    private ValueCallback<Uri[]> filePathCallback;
    private final Activity activity;

    FlutterWebChromeClient(Activity activity) {
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (this.filePathCallback != null) {
            this.filePathCallback.onReceiveValue(null);
        }
        this.filePathCallback = filePathCallback;

        Intent intent = fileChooserParams.createIntent();
        intent.setType("image/*");
        activity.startActivityForResult(intent, FILECHOOSER_REQUESTCODE);

        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_REQUESTCODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri[] results = null;
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            }
            return true;
        } else {
            return false;
        }
    }
}
