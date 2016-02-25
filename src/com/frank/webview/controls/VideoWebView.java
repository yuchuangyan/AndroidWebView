package com.frank.webview.controls;

import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;

/**
 * 
 * @author san
 *
 */
public class VideoWebView extends WebView
{
    public interface ToggledFullscreenCallback
    {
        public void toggledFullscreen(boolean fullscreen);
    }

    private VideoChromeClient videoEnabledWebChromeClient;
    private boolean addedJavascriptInterface;

    public VideoWebView(Context context)
    {
        super(context);
        addedJavascriptInterface = false;
    }

    public VideoWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        addedJavascriptInterface = false;
    }

    public VideoWebView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        addedJavascriptInterface = false;
    }
    
    /**
     * init WebView 
     */
    public void initVideoWebView(){
    	getSettings().setJavaScriptEnabled(true);
		getSettings().setLoadWithOverviewMode(true);
		getSettings().setPluginsEnabled(true);
		getSettings().setPluginState(PluginState.ON);
		getSettings().setDomStorageEnabled(true);
		
		getSettings().setBuiltInZoomControls(true);
		getSettings().setSupportZoom(true);
		getSettings().setLoadWithOverviewMode(true);
		getSettings().setUseWideViewPort(true);
    }

    /**
     * Pass only a VideoEnabledWebChromeClient instance.
     */
    @Override @SuppressLint("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client)
    {
        getSettings().setJavaScriptEnabled(true);

        if (client instanceof VideoChromeClient)
        {
            this.videoEnabledWebChromeClient = (VideoChromeClient) client;
        }

        super.setWebChromeClient(client);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding)
    {
        addJavascriptInterface();
        super.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl)
    {
        addJavascriptInterface();
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public void loadUrl(String url)
    {
        addJavascriptInterface();
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders)
    {
        addJavascriptInterface();
        super.loadUrl(url, additionalHttpHeaders);
    }

    private void addJavascriptInterface()
    {
        if (!addedJavascriptInterface)
        {
            // Add javascript interface to be called when the video ends (must be done before page load)
            addJavascriptInterface(new Object()
            {
                public void notifyVideoEnd() // Must match Javascript interface method of VideoEnabledWebChromeClient
                {
                    // This code is not executed in the UI thread, so we must force it to happen
                    new Handler(Looper.getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (videoEnabledWebChromeClient != null)
                            {
                                videoEnabledWebChromeClient.onHideCustomView();
                            }
                        }
                    });
                }
            }, "_VideoEnabledWebView"); // Must match Javascript interface name of VideoEnabledWebChromeClient

            addedJavascriptInterface = true;
        }
    }

}