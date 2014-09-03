package by.cooper.android.googlegeocode;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private EditText mSearchEditText;
    private GridView mLocationGridView;
    private GridLocationAdapter mGridLocationAdapter;
    private DataBaseHelper dataBaseHelper = null;

    private List<Location> mLocations;


    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Handler searchHandler = new Handler();
        final Runnable searchQuery = new Runnable() {
            @Override
            public void run() {
                addAddresses();
            }
        };

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSearchEditText = (EditText) rootView.findViewById(R.id.search_editText);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchHandler.postDelayed(searchQuery, 2000);
            }
        });

        mLocations = new ArrayList<Location>();

        mGridLocationAdapter = new GridLocationAdapter(getActivity(), R.layout.gridview_item, mLocations);
        mLocationGridView = (GridView) rootView.findViewById(R.id.gridview);
        mLocationGridView.setAdapter(mGridLocationAdapter);
        mLocationGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                double lat = mLocations.get(position).getLatitude();
                double lng = mLocations.get(position).getLongitude();
                FragmentManager fm = getFragmentManager();
                ShowMapFragment fragment = ShowMapFragment.newInstance(lat, lng);
                if (fm != null) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.container, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
//                String uri = "geo:" + lat + "," + lng + "?z=12";
//                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
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
                getHelper().getLocationDataDao()
                        .createIfNotExists(getLocationFromAddress(shortAddress, address));
            }

            List<Location> locations = getHelper().getLocationDataDao()
                    .queryForEq("shortAddress", shortAddress);

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
