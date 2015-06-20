package ng.latitude.support.map;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;

import ng.latitude.R;
import ng.latitude.support.bean.SpotBean;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;

/**
 * Created by Joe on 2015/5/26.
 */
public class InfoWindowAdapter implements AMap.InfoWindowAdapter {

    private OnInfoWindowButtonClickedListener onInfoWindowButtonClickedListener;

    @Override
    public View getInfoWindow(Marker marker) {

        View v = LayoutInflater.from(Latitude.getContext()).inflate(R.layout.widget_marker_info_window, null);

        render(marker, v);
        return v;
    }

    public InfoWindowAdapter setOnInfoWindowButtonClickedListener(OnInfoWindowButtonClickedListener onInfoWindowButtonClickedListener) {
        this.onInfoWindowButtonClickedListener = onInfoWindowButtonClickedListener;
        return this;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(final Marker marker, View view) {

        final SpotBean spotBean = (SpotBean) marker.getObject();

        TextView tvTitle = (TextView) view.findViewById(R.id.widget_marker_info_window_title);
        TextView tvSnippet = (TextView) view.findViewById(R.id.widget_marker_info_window_snippet);
        Button btnCapture = (Button) view.findViewById(R.id.widget_marker_info_window_capture);
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.widget_marker_info_window_container);

        container.setBackgroundResource(spotBean.getForce() == Constants.Force.ONE ? R.drawable.marker_info_window_force_1 : R.drawable.marker_info_window_force_2);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onInfoWindowButtonClickedListener != null)
                    onInfoWindowButtonClickedListener.onInfoWindowButtonClicked(marker);
            }
        });

        if (marker.getSnippet().isEmpty() && marker.getTitle().isEmpty())
            view.setVisibility(View.GONE);
        else {
            if (marker.getTitle().isEmpty())
                tvTitle.setVisibility(View.GONE);
            else
                tvTitle.setText(marker.getTitle());

            if (marker.getSnippet().isEmpty())
                tvSnippet.setVisibility(View.GONE);
            else
                tvSnippet.setText(marker.getSnippet());
        }

        if (spotBean.getForce() == Latitude.getUserInfo().getForce())
            btnCapture.setVisibility(View.GONE);
        else {
            btnCapture.setBackgroundResource(Latitude.getUserInfo().getForce() == Constants.Force.ONE ? R.drawable.bg_dialog_spot_action_capture_force_1 : R.drawable.bg_dialog_spot_action_capture_force_2);
        }
    }

    public interface OnInfoWindowButtonClickedListener {
        void onInfoWindowButtonClicked(Marker marker);
    }
}
