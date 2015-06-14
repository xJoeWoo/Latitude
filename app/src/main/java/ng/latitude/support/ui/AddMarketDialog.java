package ng.latitude.support.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ng.latitude.R;
import ng.latitude.support.conf.Latitude;

/**
 * Created by Ng on 15/6/15.
 */
public class AddMarketDialog {

    private Context context;
    private OnMarkerConfirmedListener onMarkerConfirmedListener;
    private String title;
    private String snippet;

    public AddMarketDialog(Context context, OnMarkerConfirmedListener listener, String title, String snippet) {
        this.context = context;
        this.onMarkerConfirmedListener = listener;
        this.title = title == null ? "" : title;
        this.snippet = snippet == null ? "" : snippet;
    }

    public void show() {
        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_add_marker, null);
        final EditText etTitle = (EditText) v.findViewById(R.id.dialog_add_marker_et_title);
        final EditText etSnippet = (EditText) v.findViewById(R.id.dialog_add_marker_et_snippet);

        if (!title.isEmpty())
            etTitle.setText(title);

        if (!snippet.isEmpty())
            etSnippet.setText(snippet);

        final AlertDialog d = new AlertDialog.Builder(context)
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

                            onMarkerConfirmedListener.onMarkerConfirmed(etTitle.getText().toString().trim(), etSnippet.getText().toString().trim());

                            d.dismiss();

                        } else {
                            Latitude.shakeEditText(etTitle, false);
                        }
                    }
                });
            }
        });


        d.show();
    }

    public interface OnMarkerConfirmedListener {
        void onMarkerConfirmed(String title, String snippet);
    }

}
