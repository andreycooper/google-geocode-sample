package by.cooper.android.googlegeocode.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import by.cooper.android.googlegeocode.model.Location;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String LOG_TAG = DataBaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "locations.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Location, String> mLocationDao = null;
    private RuntimeExceptionDao<Location, String> mLocationRuntimeDao = null;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Location.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Can't create db", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Location.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Can't drop db", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Location, String> getDao() throws SQLException {
        if (mLocationDao == null) {
            mLocationDao = getDao(Location.class);
        }
        return mLocationDao;
    }

    public RuntimeExceptionDao<Location, String> getLocationDataDao() {
        if (mLocationRuntimeDao == null) {
            mLocationRuntimeDao = getRuntimeExceptionDao(Location.class);
        }
        return mLocationRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        mLocationDao = null;
        mLocationRuntimeDao = null;
    }
}
