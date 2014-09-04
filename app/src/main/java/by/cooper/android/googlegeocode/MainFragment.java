package by.cooper.android.googlegeocode;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.cooper.android.googlegeocode.helpers.DataBaseHelper;
import by.cooper.android.googlegeocode.helpers.LocationHelper;
import by.cooper.android.googlegeocode.model.Location;


public class MainFragment extends Fragment {
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private EditText mSearchEditText;
    private GridView mLocationGridView;
    private GridLocationAdapter mGridLocationAdapter;
    private DataBaseHelper dataBaseHelper = null;

    private List<Location> mLocations = new ArrayList<Location>();
    private Handler mSearchHandler;
    private Runnable mSearchQuery;


    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mLocations = LocationHelper.get().getLocations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSearchHandler = new Handler();
        mSearchQuery = new Runnable() {
            @Override
            public void run() {
                addAddresses();
                hideKeyboard();
            }
        };

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSearchEditText = (EditText) rootView.findViewById(R.id.search_editText);
        mSearchEditText.requestFocus();
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchHandler.removeCallbacks(mSearchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchHandler.postDelayed(mSearchQuery, 2000);
            }
        });

        mGridLocationAdapter = new GridLocationAdapter(getActivity(), R.layout.gridview_item, mLocations);
        mLocationGridView = (GridView) rootView.findViewById(R.id.gridview);
        mLocationGridView.setAdapter(mGridLocationAdapter);
        mLocationGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchHandler.removeCallbacks(mSearchQuery);
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
        if (mSearchHandler != null) {
            mSearchHandler.removeCallbacks(mSearchQuery);
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

            populateGridView(locations);

        } catch (IOException e) {
            Log.e(LOG_TAG, "error getting the location from GeoCoder", e);
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "error with LocationDataDao", e);
        }

    }

    private void populateGridView(List<Location> locations) {
        for (Location location : locations) {
            if (!mLocations.contains(location)) {
                mLocations.add(location);
                LocationHelper.get().addLocation(location);
            }
        }
        mGridLocationAdapter.notifyDataSetChanged();
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

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
        mSearchEditText.setText("");
        mSearchHandler.removeCallbacks(mSearchQuery);
    }

}
