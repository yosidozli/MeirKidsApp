package com.yosidozli.meirkidsapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.yosidozli.meirkidsapp.R;

/**
 * Created by yosid on 27/06/2017.
 */

public class ConnectDialog extends DialogFragment {
    private static final String TAG= "ConnectDialog";

    private ConnectDialogListener mConnecrDialogListener;

    public interface ConnectDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogNetuarlClick(DialogFragment dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.connect_dialog_title).setMessage(R.string.connect_dialog_message).setPositiveButton(R.string.connect_dialog_connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // Log.d(TAG, "onClick: connect");
                mConnecrDialogListener.onDialogPositiveClick(ConnectDialog.this);
            }
        }).setNeutralButton(R.string.connect_diaolg_register, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              //  Log.d(TAG, "onClick: Register");
                mConnecrDialogListener.onDialogNetuarlClick(ConnectDialog.this);

            }
        }).setNegativeButton(R.string.connect_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             //   Log.d(TAG, "onClick: Guest");
                mConnecrDialogListener.onDialogNegativeClick(ConnectDialog.this);
            }
        });
        return  builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mConnecrDialogListener = (ConnectDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ConnectDialogListener");
        }
    }
}
