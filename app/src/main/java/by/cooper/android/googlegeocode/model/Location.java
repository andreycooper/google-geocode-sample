package by.cooper.android.googlegeocode.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * POJO for location
 */
@DatabaseTable(tableName = "locations")
public class Location {
    public static final String SHORT_ADDRESS = "shortAddress";

    @DatabaseField(dataType = DataType.DOUBLE, defaultValue = "0.0")
    private double latitude;
    @DatabaseField(dataType = DataType.DOUBLE, defaultValue = "0.0")
    private double longitude;

    @DatabaseField(dataType = DataType.STRING, columnName = SHORT_ADDRESS, canBeNull = false, defaultValue = "")
    private String shortAddress;

    @DatabaseField(id = true, dataType = DataType.STRING, canBeNull = false, defaultValue = "")
    private String fullAddress;

    public Location() {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getCoordinatesForURL() {
        return getLatitude() + "," + getLongitude();
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (Double.compare(location.latitude, latitude) != 0) return false;
        if (Double.compare(location.longitude, longitude) != 0) return false;
        if (fullAddress != null ? !fullAddress.equals(location.fullAddress) : location.fullAddress != null)
            return false;
        if (shortAddress != null ? !shortAddress.equals(location.shortAddress) : location.shortAddress != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (shortAddress != null ? shortAddress.hashCode() : 0);
        result = 31 * result + (fullAddress != null ? fullAddress.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", shortAddress='" + shortAddress + '\'' +
                ", fullAddress='" + fullAddress + '\'' +
                '}';
    }
}
