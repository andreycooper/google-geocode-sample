package by.cooper.android.googlegeocode.helpers;


import java.util.ArrayList;

import by.cooper.android.googlegeocode.model.Location;

public class LocationHelper {

    private ArrayList<Location> mLocations;

    private static LocationHelper sLocationHelper;

    private LocationHelper() {
        mLocations = new ArrayList<Location>();
    }

    public static synchronized LocationHelper get() {
        if (null == sLocationHelper) {
            sLocationHelper = new LocationHelper();
        }
        return sLocationHelper;
    }

    public ArrayList<Location> getLocations() {
        return mLocations;
    }

    public void addLocation(Location location) {
        if (!mLocations.contains(location)) {
            mLocations.add(location);
        }
    }

}
