package by.cooper.android.googlegeocode;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowMapFragment extends Fragment {
    private static final String LAT = "by.cooper.android.lat";
    private static final String LNG = "by.cooper.android.lng";
    private static final int ZOOM_LEVEL = 12;

    private SupportMapFragment mMapFragment;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (null == mMapFragment) {
            mMapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.map, mMapFragment).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMap();
    }

    private void setUpMap() {
        if (null == mMap) {
            mMap = mMapFragment.getMap();
            if (null != mMap) {
                mMap.addMarker(new MarkerOptions().position(mCoordinates));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mCoordinates)
                        .zoom(ZOOM_LEVEL)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMap = null;
    }
}
