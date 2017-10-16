package com.yosidozli.meirkidsapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.yosidozli.meirkidsapp.R;

/**
 * Created by yosid on 27/06/2017.
 */

public class UpdateDialog extends DialogFragment {
    private static final String TAG= "UpdateDialog";

    private UpdateDialogListener mUpdateDialogListener;
    protected String title;
    protected String message;
    protected String positiveButtonsMessage;
    protected String negativeButtonsMessage;

    public interface UpdateDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStrings(R.string.update_dialog_title,R.string.update_dialog_message,R.string.update_dialog_update,R.string.update_dialog_cancel);

        return  createDialog( new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d(TAG, "onClick: update");
                mUpdateDialogListener.onDialogPositiveClick(UpdateDialog.this);
            }},new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d(TAG, "onClick: cancel");
                mUpdateDialogListener.onDialogNegativeClick(UpdateDialog.this);
            }
        });

    }

    protected void setStrings( int title, int message, int positiveButtonMessage, int negativeButtonMessage){
        Activity activity = getActivity();
        this.title = activity.getString(title);
        this.message = activity.getString(message);
        positiveButtonsMessage =  activity.getString(  positiveButtonMessage);
        negativeButtonsMessage =  activity.getString(  negativeButtonMessage);

    }

    protected AlertDialog createDialog(DialogInterface.OnClickListener positive,DialogInterface.OnClickListener negative ){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message).setPositiveButton(positiveButtonsMessage,positive)
                .setNegativeButton(negativeButtonsMessage, negative);

        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mUpdateDialogListener = (UpdateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ConnectDialogListener");
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mUpdateDialogListener.onDialogNegativeClick(this);
        super.onCancel(dialog);
    }
}
