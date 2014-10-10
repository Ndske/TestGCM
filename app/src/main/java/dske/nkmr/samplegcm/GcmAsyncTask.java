package dske.nkmr.samplegcm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by nakamura_d on 2014/10/02.
 */
public class GcmAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "GcmAsyncTask.class";
    private Context context;
    private RegistrationListener regListener;
    private ProgressDialog dialog;

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("通信中");
        dialog.setMessage("PUSH通知の登録をおこなっています");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        String regId = "";
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            regId = gcm.register("479297509484");
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return regId;
    }

    @Override
    protected void onPostExecute(String regId) {
        dialog.dismiss();
        if (!StringUtil.isNullOrEmpty(regId)) {
            regListener.successRegistration(regId);
        } else {
            regListener.failRegistration();
        }
    }

    public void setActivity(Activity activity) {
        this.context = activity;
        this.regListener = (RegistrationListener) activity;
    }

    /**
     * GCM登録完了を通知するリスナ
     */
    public interface RegistrationListener {
        void successRegistration(String regId);

        void failRegistration();
    }
}
