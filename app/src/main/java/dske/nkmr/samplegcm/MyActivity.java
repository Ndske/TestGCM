package dske.nkmr.samplegcm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class MyActivity extends Activity implements MyDialogFragment.DialogButtonOnClickListener, GcmAsyncTask.RegistrationListener {

    /* 定数 */
    private static final String TAG = "MyActivity.class";


    /**
     * WebViewインスタンス
     */
    private WebView mWebView;
    /**
     * 初回起動であるかのフラグ(true:2回目以降の起動 false:初回起動)
     */
    private boolean mIsLaunchedBefore = false;

    /**
     * SharedPreferenceのキー
     */
    public enum SharedPreferenceKey {

        REG_ID("reg_id"),
        URL("url");

        private String key;

        private SharedPreferenceKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "activity");
        mWebView.setWebViewClient(new MyViewClient());

        SharedPreferenceHelper helper = new SharedPreferenceHelper();
        if (StringUtil.isNullOrEmpty(helper.getString(getApplicationContext(), SharedPreferenceKey.REG_ID.getKey()))) {
            showDialog();
        } else {
            registerInBackground();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String showingUrl = mWebView.getUrl();
        SharedPreferenceHelper helper = new SharedPreferenceHelper();
        helper.saveString(getApplicationContext(), SharedPreferenceKey.URL.getKey(), showingUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferenceHelper helper = new SharedPreferenceHelper();
        helper.remove(getApplicationContext(), SharedPreferenceKey.URL.getKey());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.send_message) {
            return true;
        } else if (id == R.id.clear_cookie) {
            CookieSyncManager.createInstance(mWebView.getContext()).startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().stopSync();
            Log.v(TAG, "clear cookie");
            mWebView.loadUrl(REGISTER_URL);
            return true;
        } else if (id == R.id.clear_sp) {
            SharedPreferenceHelper helper = new SharedPreferenceHelper();
            helper.clear(getApplicationContext());
            return true;
        } else if (id == R.id.post) {
            post1();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSugotokuSite() {
        SharedPreferenceHelper helper = new SharedPreferenceHelper();
        String regId = helper.getString(getApplicationContext(), SharedPreferenceKey.REG_ID.getKey());
//        String url = StringUtil.stringCat(REGISTER_URL, "&", "regId", "=", regId); //TODO REGISTERページ作成後にregIDパラメータを付与する
        String url = REGISTER_URL;
        mWebView.loadUrl(url);
    }

    private void registerInBackground() {
        GcmAsyncTask task = new GcmAsyncTask();
        task.setActivity(this);
        task.execute();
    }

    private void showDialog() {
        MyDialogFragment dFragment = MyDialogFragment.newInstance();
        dFragment.setTitle("初回起動設定");
        dFragment.setMsg("PUSH通知を登録しますか？");
        dFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onClickPositiveButton() {
        registerInBackground();
    }

    @Override
    public void onClickNegativeButton() {
        finish();
    }

    @Override
    public void successRegistration(String regId) {
        Log.v(TAG, "Device registered, REGISTRATION ID: " + regId);
        SharedPreferenceHelper helper = new SharedPreferenceHelper();
        String savedRegId = helper.getString(getApplicationContext(), SharedPreferenceKey.REG_ID.getKey());
        if (StringUtil.isNullOrEmpty(savedRegId) || !savedRegId.equals(regId)) {
            helper.saveString(getApplicationContext(), SharedPreferenceKey.REG_ID.getKey(), regId);
        }
        showSugotokuSite();
    }

    @Override
    public void failRegistration() {
        Toast.makeText(getApplicationContext(), "PUSH登録に失敗しました", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * カスタムWebViewClientクラス
     */
    private final class MyViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v(TAG, "showing url is " + url);
            if (url.equals(REGISTER_URL)) {
                try {
                    Thread.sleep(1000); //Registerページ表示のように見せるためにsleep
                } catch (InterruptedException e) {
                }
                Log.v(TAG, "sugotoku top url");
                mWebView.loadUrl(TOP_URL);
            }
        }
    }

    /* ************************************************************************* */



    private void post1() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.v(TAG, "RESULT: " + s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyError e = error;
                        Log.e(TAG, "");
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return s.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("x-Content-id", CONTENTS_ID);
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.v(TAG, "NetworkResponse: " + response);
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(postRequest);
    }

    private void post2() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("x-Content-id", "01cpn036001");
                    PrintStream printStream = new PrintStream(con.getOutputStream());
                    printStream.print(bd);
                    printStream.close();

                    int responseCode = con.getResponseCode();

//                    InputStream inputStream = con.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    StringBuffer bufStr = new StringBuffer();
//                    String temp = null;
//                    while ((temp = bufferedReader.readLine()) != null) {
//                        bufStr.append(temp);
//                    }

                    if (responseCode == 302) {
                        String redirectTo = con.getHeaderField("Location");
                        Uri uri = Uri.parse(redirectTo);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }

                    con.disconnect();
//                    inputStream.close();

                } catch (MalformedURLException e) {
                } catch (ProtocolException e) {
                } catch (IOException e) {
                }
                return null;
            }
        }.execute();
    }
}
