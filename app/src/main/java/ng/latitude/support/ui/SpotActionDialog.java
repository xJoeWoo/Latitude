package ng.latitude.support.ui;

import android.app.DialogFragment;

/**
 * Created by Ng on 15/6/18.
 */
public class SpotActionDialog extends DialogFragment {

//    private static final String SPOT_BEAN = "spot";
//    private OnSpotForceChangedListener onSpotForceChangedListener;
//
//    public static SpotActionDialog getInstance(SpotBean spotBean) {
//        SpotActionDialog fragment = new SpotActionDialog();
//        Bundle args = new Bundle();
//        args.putParcelable(SPOT_BEAN, spotBean);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public SpotActionDialog setOnSpotForceChangedListener(OnSpotForceChangedListener onSpotForceChangedListener) {
//        this.onSpotForceChangedListener = onSpotForceChangedListener;
//        return this;
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        final SpotBean spotBean = getArguments().getParcelable(SPOT_BEAN);
//
//        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_spot_action, null);
//        ((TextView)(v.findViewById(R.id.dialog_spot_action_title))).setText(spotBean.getTitle());
//
//        final AlertDialog d = new AlertDialog.Builder(getActivity())
//                .setTitle(R.string.dialog_spot_action_title)
//                .setView(v)
//                .create();
//
//        TextView tvSnippet = (TextView)(v.findViewById(R.id.dialog_spot_action_snippet));
//        if(spotBean.getSnippet()!=null&&!spotBean.getSnippet().isEmpty())
//            tvSnippet.setText(spotBean.getSnippet());
//        else
//            tvSnippet.setVisibility(View.GONE);
//
//        Button btnCapture = (Button)v.findViewById(R.id.dialog_spot_action_capture);
//        if(Latitude.getUserInfo().getForce()==spotBean.getForce())
//            btnCapture.setVisibility(View.GONE);
//        else
//            btnCapture.setBackgroundResource(Latitude.getUserInfo().getForce()== Constants.Force.ONE?R.drawable.bg_dialog_spot_action_capture_force_1:R.drawable.bg_dialog_spot_action_capture_force_2);
//        btnCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
//
//        return d;
//
//    }

//    public interface OnSpotForceChangedListener {
//        void onSpotForceChanged(int state, int spotId);
//    }
}
