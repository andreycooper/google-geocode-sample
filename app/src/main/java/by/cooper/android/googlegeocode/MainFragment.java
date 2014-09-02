package by.cooper.android.googlegeocode;

import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.cooper.android.googlegeocode.helpers.DataBaseHelper;
import by.cooper.android.googlegeocode.model.Location;


public class MainFragment extends Fragment {
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private Button mSearchBtn;
    private EditText mSearchEditText;
    private GridView mLocationGridView;
    private GridLocationAdapter mGridLocationAdapter;
    private DataBaseHelper dataBaseHelper = null;

    private List<Location> mLocations;


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSearchEditText = (EditText) rootView.findViewById(R.id.search_editText);
        mLocations = new ArrayList<Location>();

        mGridLocationAdapter = new GridLocationAdapter(getActivity(), R.layout.gridview_item, mLocations);
        mLocationGridView = (GridView) rootView.findViewById(R.id.gridview);
        mLocationGridView.setAdapter(mGridLocationAdapter);

        mSearchBtn = (Button) rootView.findViewById(R.id.button);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddresses();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
    }

    private void addAddresses() {
        Geocoder gc = new Geocoder(getActivity(), Locale.US);

        try {
            String shortAddress = mSearchEditText.getText().toString();
            List<Address> addresses = gc.getFromLocationName(shortAddress, 10);
            for (Address address : addresses) {
                getHelper().getLocationDataDao().createIfNotExists(getLocationFromAddress(shortAddress, address));
            }

            List<Location> locations = getHelper().getLocationDataDao().queryForEq("shortAddress", shortAddress);

            for (Location location : locations) {
                if (!mLocations.contains(location)) {
                    mLocations.add(location);
                }
            }
            mGridLocationAdapter.notifyDataSetChanged();

        } catch (IOException e) {
            Log.e(LOG_TAG, "error getting the location from GeoCoder", e);
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "error with LocationDataDao", e);
        }

    }

    private Location getLocationFromAddress(String shortAddress, Address address) {
        Location location = new Location();
        if (address.hasLatitude() & address.hasLongitude()) {
            location.setLatitude(address.getLatitude());
            location.setLongitude(address.getLongitude());
        }
        location.setShortAddress(shortAddress);
        location.setFullAddress(getAddressLine(address));
        return location;
    }


    private String getAddressLine(Address address) {
        StringBuilder addressLine = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressLine.append(address.getAddressLine(i));
            addressLine.append(" ");
        }
        return addressLine.toString().trim();
    }

    private DataBaseHelper getHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);
        }
        return dataBaseHelper;
    }

}
