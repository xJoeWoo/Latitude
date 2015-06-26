package ng.latitude.support.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ng.latitude.R;

/**
 * Created by Ng on 15/6/15
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class AddMarketDialog extends DialogFragment {

    private static final String TITLE = "title";
    private static final String SNIPPET = "snippet";
    private OnAddMarkerDialogListener onAddMarkerDialogListener;

    public static AddMarketDialog newInstance(String title, String snippet) {
        AddMarketDialog fragment = new AddMarketDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title == null ? "" : title);
        args.putString(SNIPPET, snippet == null ? "" : snippet);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_marker, null);
        final EditText etTitle = (EditText) v.findViewById(R.id.dialog_add_marker_et_title);
        final EditText etSnippet = (EditText) v.findViewById(R.id.dialog_add_marker_et_snippet);

        etTitle.setText(getArguments().getString(TITLE));
        etSnippet.setText(getArguments().getString(SNIPPET));

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_add_marker_title)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setView(v)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                d.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!etTitle.getText().toString().trim().isEmpty()) {

                            if (onAddMarkerDialogListener != null)
                                onAddMarkerDialogListener.onAddMarkerDialogConfirmed(etTitle.getText().toString().trim(), etSnippet.getText().toString().trim(), (Button) v);

                            d.dismiss();

                        } else {
                            InterfaceUtils.shakeEditText(etTitle, false);
                        }
                    }
                });

                d.getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.cancel();
                    }
                });
            }
        });

        return d;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onAddMarkerDialogListener != null)
            onAddMarkerDialogListener.onAddMarkerDialogCancelled();
    }

    public void blankPositiveButton(boolean isBlanking) {

    }

    public void setOnAddMarkerDialogListener(OnAddMarkerDialogListener onAddMarkerDialogListener) {
        this.onAddMarkerDialogListener = onAddMarkerDialogListener;
    }

    /**
     * {@link AddMarketDialog} 的添加据点确认、取消事件监听器
     */
    public interface OnAddMarkerDialogListener {

        /**
         * 用户确认添加据点
         *
         * @param title   将添加据点的标题
         * @param snippet 将添加据点的描述
         * @param btn     占领按钮实例
         */
        void onAddMarkerDialogConfirmed(String title, String snippet, Button btn);

        /**
         * 用户取消添加据点
         */
        void onAddMarkerDialogCancelled();
    }

}
