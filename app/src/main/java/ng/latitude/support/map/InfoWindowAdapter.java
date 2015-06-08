package ng.latitude.support.map;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;

import ng.latitude.R;
import ng.latitude.support.conf.Latitude;

/**
 * Created by Joe on 2015/5/26.
 */
public class InfoWindowAdapter implements AMap.InfoWindowAdapter {

    @Override
    public View getInfoWindow(Marker marker) {

        View v = LayoutInflater.from(Latitude.getContext()).inflate(R.layout.widget_marker_info_window, null);

        render(marker, v);
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {
        TextView tvTitle = (TextView) view.findViewById(R.id.widget_marker_info_window_title);
        TextView tvSnippet = (TextView) view.findViewById(R.id.widget_marker_info_window_snippet);

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
    }
}
