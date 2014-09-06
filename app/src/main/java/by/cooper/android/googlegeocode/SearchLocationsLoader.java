package by.cooper.android.googlegeocode;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.cooper.android.googlegeocode.helpers.DataBaseHelper;
import by.cooper.android.googlegeocode.model.Location;

public class SearchLocationsLoader extends AsyncTaskLoader<List<Location>> {
    public static final String ADDRESS_TAG = "short_address";
    private static final String LOG_TAG = SearchLocationsLoader.class.getSimpleName();
    private static final String SPACE = " ";
    private static final int ADDRESS_COUNT = 100;
    private static final String EMPTY_STRING = "";

    private DataBaseHelper dataBaseHelper = null;
    private List<Location> mLocations;
    private Context mContext;
    private String mAddress;

    public SearchLocationsLoader(Context context, Bundle args) {
        super(context);
        mContext = context;
        if (null != args) {
            mAddress = args.getString(ADDRESS_TAG);
        } else {
            mAddress = EMPTY_STRING;
        }
    }

    @Override
    public List<Location> loadInBackground() {
        mLocations = getLocationList(mAddress);
        return mLocations;
    }

    private List<Location> getLocationList(String shortAddress) {
        Geocoder gc = new Geocoder(mContext, Locale.US);
        List<Location> locations = new ArrayList<Location>();

        try {
            locations = getHelper().getLocationDataDao()
                    .queryForEq(Location.SHORT_ADDRESS, shortAddress);

            if (locations.isEmpty()) {
                List<Address> addresses = gc.getFromLocationName(shortAddress, ADDRESS_COUNT);
                for (Address address : addresses) {
                    Location location = getLocationFromAddress(shortAddress, address);
                    getHelper().getLocationDataDao()
                            .createIfNotExists(location);
                    locations.add(location);
                }
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "error getting the location from GeoCoder", e);
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "error with LocationDataDao", e);
        }

        return locations;
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
            addressLine.append(SPACE);
        }
        return addressLine.toString().trim();
    }

    private DataBaseHelper getHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(mContext, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }
}
