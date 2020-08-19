package mitm.college;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import static android.view.View.GONE;

public class WebsiteActivity extends AppCompatActivity {
    WebView webView;
    private String webUrl="https://mitmuzaffarpur.org";
    ProgressBar progressBarWeb;
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;
    Button btnNoInternetConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.website_activity);

        webView = findViewById(R.id.myWebView);
        progressBarWeb=findViewById(R.id.progressBar);
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Loading..\nPlease Wait !!");
        btnNoInternetConnection=findViewById(R.id.btnNoConnection);
        relativeLayout=findViewById(R.id.relativeLayout);
//rotate krne pr state loss na ho , issliiye , neeche ekk onSaveInstanceState function bhi hai
        if(savedInstanceState !=null)
        {
            webView.restoreState(savedInstanceState);
        }
        else
        {
            //video ya add chalane hetu ek line of code
            webView.getSettings().setJavaScriptEnabled(true);
            //few more settings
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setLoadsImagesAutomatically(true);

        }







        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String s, final String s1, final String s2, final String s3, long l) {
                Dexter.withActivity(WebsiteActivity.this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


                                DownloadManager.Request request= new DownloadManager.Request(Uri.parse(s));
                                request.setMimeType(s3);

                                String cookies = CookieManager.getInstance().getCookie(s);
                                request.addRequestHeader("cookie",cookies);
                                request.addRequestHeader("User-Agent",s1);
                                request.setDescription("Downloading File...");
                                request.setTitle(URLUtil.guessFileName(s,s2,s3));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(
                                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                                s,s2,s3));
                                DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);
                                Toast.makeText(WebsiteActivity.this,"Downloading File...",Toast.LENGTH_SHORT).show();







                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        checkConnection();


        webView.setWebViewClient(new WebViewClient()
        {
            public boolean shouldOverrideUrlLoading(WebView view,String url){
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
        public void onProgressChanged(WebView view, int newProgress){

               progressBarWeb.setVisibility(View.VISIBLE);
               progressBarWeb.setProgress(newProgress);

               progressDialog.show();
               if(newProgress == 100)
               {
                   progressBarWeb.setVisibility(GONE);
                   progressDialog.dismiss();

               }
                super.onProgressChanged(view, newProgress);

         }
     } );

   btnNoInternetConnection.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           checkConnection();
       }
   });
    }


    @Override
    public void onBackPressed() {
     //agar back jaane yogye haai to bhhej do vapas
        if(webView.canGoBack()){
            webView.goBack();
        }
        //nhi to agr ye first page hai  tb dialog box show kra de bro
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Muzaffarpur Institute of Technology");
            builder.setIcon(R.drawable.mitlogo);

            builder.setMessage("Are you sure you want to exit?")
                    .setNegativeButton("NO",null)
                    .setPositiveButton("YES",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface,int i){
                            finishAffinity();
                        }

                    }).show();
        }

    }

    public void checkConnection(){
        ConnectivityManager connectivityManager=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(wifi.isConnected()) {
            webView.loadUrl(webUrl);
        webView.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
        }
        else if(mobileNetwork.isConnected()){
            webView.loadUrl(webUrl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }
        else {
            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

                case R.id.nav_previous:
                onBackPressed();
                break;

                case R.id.nav_next:
                    if(webView.canGoForward()){
                        webView.goForward();
                    }

                break;
                case R.id.nav_reload:
                    checkConnection();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);

    }
}