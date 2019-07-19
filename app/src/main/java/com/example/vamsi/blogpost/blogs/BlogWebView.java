package com.example.vamsi.blogpost.blogs;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.feed.MainActivity;

public class BlogWebView extends AppCompatActivity {
private Toolbar myToolbar;
Bundle bundle;
String title,link;
private WebView webView;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_web_view);

        bundle=getIntent().getExtras();
        myToolbar = findViewById(R.id.blog_web_view_toolbar);
        setSupportActionBar(myToolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Set the title of the action bar to the PhotoBlog
            if(bundle!=null) {
                link = bundle.getString("weblink");
                title = bundle.getString("title");
                getSupportActionBar().setTitle(title);
            }

            webView=findViewById(R.id.blog_web_view);
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Loading...");
    progressDialog.setCancelable(false);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();
            webView.loadUrl(link);

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    progressDialog.dismiss();
                }

                @SuppressWarnings("deprecation")


                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(getApplicationContext(),"Can;t load website", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                @TargetApi(android.os.Build.VERSION_CODES.M)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                    // Redirect to deprecated method, so you can use it in all SDK versions
                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                    progressDialog.dismiss();
                }
            });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(BlogWebView.this, AdminBlogsActivity.class));
        finish();
    }
}
