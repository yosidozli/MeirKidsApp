package com.yosidozli.meirkidsapp.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.yosidozli.meirkidsapp.R;

/**
 * Created by yosid on 30/06/2017.
 */

public class ForceUpdateDialog extends UpdateDialog {
    private static final String TAG= "ForceUpdateDialog";
    private ForceUpdateDialogListener mForceUpdateDialogListener;

    public interface ForceUpdateDialogListener{
        public void onForceDialogPositiveClick(DialogFragment dialog);
        public void onForceDialogNegativeClick(DialogFragment dialog);

    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        setStrings(R.string.force_update_dialog_title, R.string.force_update_dialog_message, R.string.force_update_dialog_update, R.string.force_update_dialog_cancel);


        return createDialog( new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d(TAG, "onClick: update");
                mForceUpdateDialogListener.onForceDialogPositiveClick(ForceUpdateDialog.this);
            }},new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d(TAG, "onClick: cancel");
                mForceUpdateDialogListener.onForceDialogNegativeClick(ForceUpdateDialog.this);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mForceUpdateDialogListener = (ForceUpdateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ConnectDialogListener");
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mForceUpdateDialogListener.onForceDialogNegativeClick(this);
        super.onCancel(dialog);
    }
}
