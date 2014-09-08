package by.cooper.android.googlegeocode;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.ArrayList;
import java.util.List;

import by.cooper.android.googlegeocode.helpers.DataBaseHelper;
import by.cooper.android.googlegeocode.model.Location;


public class MainFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Location>> {
    private static final long SEARCH_DELAY = 500;
    private static final int LOADER_ID = 1;

    private EditText mSearchEditText;
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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSearchHandler = new Handler();
        mSearchQuery = new Runnable() {
            @Override
            public void run() {
                useLoader();
            }
        };

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSearchEditText = (EditText) rootView.findViewById(R.id.search_editText);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mSearchHandler.removeCallbacks(mSearchQuery);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchHandler.removeCallbacks(mSearchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSearchEditEmpty()) {
                    mSearchHandler.postDelayed(mSearchQuery, SEARCH_DELAY);
                }
            }
        });
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchHandler.removeCallbacks(mSearchQuery);
                    hideKeyboard();
                    if (!isSearchEditEmpty()) {
                        useLoader();
                    }
                    return true;
                }
                return false;
            }
        });

        mGridLocationAdapter = new GridLocationAdapter(getActivity(), R.layout.gridview_item, mLocations);
        GridView locationGridView = (GridView) rootView.findViewById(R.id.gridview);
        locationGridView.setAdapter(mGridLocationAdapter);
        locationGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchHandler.removeCallbacks(mSearchQuery);
                hideKeyboard();
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

    private boolean isSearchEditEmpty() {
        return mSearchEditText.getText().toString().trim().length() == 0;
    }

    private void populateGridView(List<Location> locations) {
        mLocations.clear();
        for (Location location : locations) {
            if (!mLocations.contains(location)) {
                mLocations.add(location);
            }
        }
        mGridLocationAdapter.notifyDataSetChanged();
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    private void useLoader() {
        Bundle args = new Bundle();
        String shortAddress = mSearchEditText.getText().toString().trim().toLowerCase();
        args.putSerializable(SearchLocationsLoader.ADDRESS_TAG, shortAddress);
        Loader<List<Location>> loader = getActivity().getSupportLoaderManager().getLoader(LOADER_ID);
        if (null == loader) {
            loader = getActivity().getSupportLoaderManager().initLoader(LOADER_ID, args, this);
        } else {
            loader = getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, args, this);
        }
        loader.forceLoad();
    }

    @Override
    public Loader<List<Location>> onCreateLoader(int i, Bundle bundle) {
        Loader<List<Location>> loader = null;
        if (i == LOADER_ID) {
            loader = new SearchLocationsLoader(getActivity(), bundle);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Location>> listLoader, List<Location> locations) {
        populateGridView(locations);
    }

    @Override
    public void onLoaderReset(Loader<List<Location>> listLoader) {

    }
}
