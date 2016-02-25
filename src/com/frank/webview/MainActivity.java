package com.frank.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.frank.helper.JumpActivityHelper;
import com.frank.webview.controls.VideoChromeClient;
import com.frank.webview.controls.VideoWebView;

public class MainActivity extends Activity {
	private VideoWebView webView;
	GestureDetector mGestureDetector;
	VideoChromeClient v;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context c = this;
		setContentView(R.layout.activity_main);

		webView = (VideoWebView) findViewById(R.id.webView1);
		webView.initVideoWebView();
		View nonVideoLayout = findViewById(R.id.activity_main); // 
		
		ViewGroup videoLayout = (ViewGroup) findViewById(R.id.fullscreen_custom_content); 
		
		View loadingView = getLayoutInflater().inflate(R.layout.activity_main, null);
		v = new VideoChromeClient(nonVideoLayout, videoLayout, loadingView, webView, c) // See all available constructors...
		{

			@Override
			public void onProgressChanged(WebView view, int progress) {
				// Your code...
			}
		};
		v.setOnToggledFullscreen(new VideoChromeClient.ToggledFullscreenCallback() {
			@Override
			public void toggledFullscreen(boolean fullscreen) {
				/*
				 * if(fullscreen) onBackPressed();
				 */
				// Your code to handle the full-screen change, for example
				// showing and hiding the title bar
			}
		});
		webView.setWebChromeClient(v);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				if (url.contains("share")) {
					view.goBack();
					JumpActivityHelper.share(webView.getUrl(), c);
					url = webView.getUrl();

				}
				if (url.contains("youtube")) {
					view.goBack();
					JumpActivityHelper.share(webView.getUrl(), c);
					url = webView.getUrl();
				}
				if (url.contains("exit")) {
					view.goBack();
					finish();
					AlertDialog.Builder builder = new AlertDialog.Builder(c);
					builder.setMessage("Are you sure to exit?")
							.setPositiveButton("Yes", dialogClickListener)
							.setNegativeButton("No", dialogClickListener)
							.show();

				} else {
					view.loadUrl(url);
				}
				return false;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {

				Toast.makeText(c, webView.getUrl() + description,
						Toast.LENGTH_SHORT).show();

				webView.loadUrl("http://www.parts-search.com/");

			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
			}

		});
		webView.setPadding(0, 0, 0, 0);
		webView.setBackgroundColor(Color.WHITE);
		webView.loadUrl("http://v.youku.com/v_show/id_XMTQ1MTU0OTI4MA==.html?from=y1.6-96.1.1.a60ebb1e46f311e5b2ad");
	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				// webView.clearView();
				dialog.dismiss();
				finish();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				dialog.dismiss();
				break;
			}
		}
	};

	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * // TODO Auto-generated method stub webView.loadUrl(webView.getUrl());
	 * super.onConfigurationChanged(newConfig); }
	 */

	@Override
	public void onBackPressed() {
		// Notify the VideoEnabledWebChromeClient, and handle it ourselves if it
		// doesn't handle it
		if (!v.onBackPressed()) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				// Close app (presumably)
				// super.onBackPressed();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();

			return true;
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (webView.canGoBack() != true)) {
			finish();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

}
