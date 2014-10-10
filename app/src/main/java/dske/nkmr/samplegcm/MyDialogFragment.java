package dske.nkmr.samplegcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nakamura_d on 2014/09/30.
 */
public class MyDialogFragment extends DialogFragment {

    private static String title = "PUSH通知登録";
    private static String msg = "PUSH通知を登録しますか？";
    private DialogButtonOnClickListener listener;

    public static MyDialogFragment newInstance() {
        MyDialogFragment dFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("msg", msg);
        dFragment.setArguments(bundle);
        return dFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DialogButtonOnClickListener) {
            listener = (DialogButtonOnClickListener) activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title"));
        builder.setMessage(getArguments().getString("msg"));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                listener.onClickPositiveButton();
            }
        });
        builder.setNegativeButton("NG", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                listener.onClickNegativeButton();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Layout Inflate
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setTitle(String title) {
        MyDialogFragment.title = title;
    }

    public void setMsg(String msg) {
        MyDialogFragment.msg = msg;
    }

    /**
     * Dialogのボタン押下リスナ
     */
    public interface DialogButtonOnClickListener {

        void onClickPositiveButton();

        void onClickNegativeButton();
    }
}
