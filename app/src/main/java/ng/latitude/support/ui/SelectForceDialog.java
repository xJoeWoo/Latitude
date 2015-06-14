package ng.latitude.support.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;

/**
 * Created by Ng on 15/6/15.
 */
public class SelectForceDialog {

    private Context context;
    private OnForceSelectedListener OnForceSelectedListener;

    public SelectForceDialog(Context context, OnForceSelectedListener OnForceSelectedListener) {
        this.context = context;
        this.OnForceSelectedListener = OnForceSelectedListener;
    }

    public void show() {
        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_select_force, null);
        final RadioGroup rp = (RadioGroup) v.findViewById(R.id.dialog_select_force_rg);

        final AlertDialog d = new AlertDialog.Builder(context)
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
                                force = Constants.FORCE_1;
                                break;
                            case R.id.dialog_select_force_rb_2:
                                force = Constants.FORCE_2;
                                break;
                        }

                        if (force > -1) {

                            if (OnForceSelectedListener != null)
                                OnForceSelectedListener.onForceSelected(force);

                            d.dismiss();

                        } else {
                            Toast.makeText(context, R.string.dialog_select_force_select, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        d.show();
    }

    public interface OnForceSelectedListener {
        void onForceSelected(int force);
    }

}
