package ng.latitude.support.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import ng.latitude.R;
import ng.latitude.support.conf.Latitude;

/**
 * Created by Ng on 15/6/15.
 */
public class AddMarketDialog extends DialogFragment {

    private static final String TITLE = "title";
    private static final String SNIPPET = "snippet";
    private OnAddMarkerDailogListener onAddMarkerDailogListener;

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

                            if (onAddMarkerDailogListener != null)
                                onAddMarkerDailogListener.onAddMarkerDialogConfirmed(etTitle.getText().toString().trim(), etSnippet.getText().toString().trim());

                            d.dismiss();

                        } else {
                            Latitude.shakeEditText(etTitle, false);
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
        if (onAddMarkerDailogListener != null)
            onAddMarkerDailogListener.onAddMarkerDialogCancelled();
    }

    public void setOnAddMarkerDailogListener(OnAddMarkerDailogListener onAddMarkerDailogListener) {
        this.onAddMarkerDailogListener = onAddMarkerDailogListener;
    }

    public interface OnAddMarkerDailogListener {
        void onAddMarkerDialogConfirmed(String title, String snippet);

        void onAddMarkerDialogCancelled();
    }

}
