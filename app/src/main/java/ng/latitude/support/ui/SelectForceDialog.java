package ng.latitude.support.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;

/**
 * Created by Ng on 15/6/15.
 */
public class SelectForceDialog extends DialogFragment {

    private OnForceSelectedListener OnForceSelectedListener;

    public static SelectForceDialog newInstance() {
        return new SelectForceDialog();
    }

    public void setOnForceSelectedListener(SelectForceDialog.OnForceSelectedListener onForceSelectedListener) {
        OnForceSelectedListener = onForceSelectedListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_select_force, null);
        final RadioGroup rp = (RadioGroup) v.findViewById(R.id.dialog_select_force_rg);

        final RadioButton rb1 = (RadioButton) v.findViewById(R.id.dialog_select_force_rb_1);
        final RadioButton rb2 = (RadioButton) v.findViewById(R.id.dialog_select_force_rb_2);

        rp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.dialog_select_force_rb_1:
                        rb1.setShadowLayer(25, 0, 0, getResources().getColor(android.R.color.white));
                        rb2.setShadowLayer(0, 0, 0, 0);
                        break;
                    case R.id.dialog_select_force_rb_2:
                        rb2.setShadowLayer(25, 0, 0, getResources().getColor(android.R.color.white));
                        rb1.setShadowLayer(0, 0, 0, 0);
                        break;
                }
            }
        });

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_select_force_title)
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
                        int force = -1;

                        switch (rp.getCheckedRadioButtonId()) {
                            case R.id.dialog_select_force_rb_1:
                                force = Constants.Force.ONE;
                                break;
                            case R.id.dialog_select_force_rb_2:
                                force = Constants.Force.TWO;
                                break;
                        }

                        if (force > -1) {

                            if (OnForceSelectedListener != null)
                                OnForceSelectedListener.onForceSelected(force);

                            d.dismiss();

                        } else {
                            Toast.makeText(getActivity(), R.string.dialog_select_force_select, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return d;
    }


    public interface OnForceSelectedListener {
        void onForceSelected(int force);
    }

}
