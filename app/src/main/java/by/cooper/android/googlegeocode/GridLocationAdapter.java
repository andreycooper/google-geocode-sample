package by.cooper.android.googlegeocode;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import by.cooper.android.googlegeocode.model.Location;


public class GridLocationAdapter extends ArrayAdapter<Location> {
    private static final String BEGIN_URL = "https://maps.googleapis.com/maps/api/staticmap?center=";
    private static final String END_URL = "&zoom=12&size=400x400&sensor=false";

    private Context mContext;
    private int mLayoutResourceId;
    private List<Location> mLocations;

    public GridLocationAdapter(Context context, int resource, List<Location> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mLocations = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) view.findViewById(R.id.text);
            holder.image = (ImageView) view.findViewById(R.id.picture);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Location location = mLocations.get(position);
        String url = BEGIN_URL
                + location.getCoordinatesForURL()
                + END_URL;
        holder.imageTitle.setText(location.getFullAddress());
        // I use Picasso library for downloading images because it provides disk cache and memory cache
        Picasso.with(mContext).load(url).into(holder.image);

        return view;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}
