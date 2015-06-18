package ng.latitude.support.ui;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Ng on 15/6/18.
 */
public class LatitudeProgressDialog extends ProgressDialog {

    public LatitudeProgressDialog(Context context, String info) {
        super(context);

        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setIndeterminate(true);
        setCanceledOnTouchOutside(false);
        setMessage(info);
    }


}
