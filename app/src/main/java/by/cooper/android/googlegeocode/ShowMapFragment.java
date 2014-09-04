package by.cooper.android.googlegeocode;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowMapFragment extends Fragment {
    public static final String LAT = "by.cooper.android.lat";
    public static final String LNG = "by.cooper.android.lng";

    private GoogleMap mMap;
    private LatLng mCoordinates;

    public static ShowMapFragment newInstance(double lat, double lng) {
        Bundle args = new Bundle();
        args.putDouble(LAT, lat);
        args.putDouble(LNG, lng);
        ShowMapFragment fragment = new ShowMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        double lat = getArguments().getDouble(LAT);
        double lng = getArguments().getDouble(LNG);
        mCoordinates = new LatLng(lat, lng);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        setUpMap();
        return rootView;
    }

    private void setUpMap() {
        if (null == mMap) {
            mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (null != mMap) {
                mMap.addMarker(new MarkerOptions().position(mCoordinates));
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(mCoordinates);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMap = null;
    }
}
