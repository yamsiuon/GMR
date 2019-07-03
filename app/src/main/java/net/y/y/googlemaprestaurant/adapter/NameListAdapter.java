package net.y.y.googlemaprestaurant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.MapStyleOptions;

import net.y.y.googlemaprestaurant.R;
import net.y.y.googlemaprestaurant.global.Constant;
import net.y.y.googlemaprestaurant.model.Location;
import net.y.y.googlemaprestaurant.model.MapPlaces;
import net.y.y.googlemaprestaurant.utility.LocationUtility;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class NameListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<MapPlaces> mapPlacesArrayList = new ArrayList<>();
    private ArrayList<MapPlaces> filterArrayList = new ArrayList<>();

    public NameListAdapter(Context context, ArrayList<MapPlaces> mapPlacesArrayList) {
        this.context = context;
        this.mapPlacesArrayList = mapPlacesArrayList;
        this.filterArrayList = mapPlacesArrayList;
    }

    @Override
    public int getCount() {
        return filterArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return filterArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        MapPlaces mapPlaces = filterArrayList.get(i);

        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_name, null);
            viewHolder = new ViewHolder();

            viewHolder.tvName = view.findViewById(R.id.tv_name);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.tvName.setText(mapPlaces.getName());

        return view;
    }

    public void sequenceList(final String type, final double lat, final double lng) {

        Collections.sort(filterArrayList, new Comparator<MapPlaces>() {
            @Override
            public int compare(MapPlaces mapPlaces, MapPlaces t1) {

                int result = 0;
                switch (type) {
                    case (Constant.SEQUENCE_DISTANCE):

                        Location location = mapPlaces.getGeometry().getLocation();
                        Location location1 = t1.getGeometry().getLocation();

                        LocationUtility locationUtility = new LocationUtility(context);

                        float placeDistance = locationUtility.getTwoPointDistance(lat, lng, location.getLat(), location.getLng());
                        float place1Distance = locationUtility.getTwoPointDistance(lat, lng, location1.getLat(), location1.getLng());

                        if (placeDistance < place1Distance) {
                            result = -1;
                        } else {
                            result = 1;
                        }

                        break;
                    case (Constant.SEQUENCE_NAME):
                        result = mapPlaces.getName().compareToIgnoreCase(t1.getName());
                        break;
                }

                return result;
            }
        });

        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = new FilterResults();
                String key = charSequence.toString();
                ArrayList<MapPlaces> filteredList = new ArrayList<>();

                if(key.isEmpty() || key.equals("[]")) {
                    filteredList.addAll(mapPlacesArrayList);
                } else {
                    for (MapPlaces mapPlaces : mapPlacesArrayList) {
                        if (key.contains(Constant.FILTER_OPEN_HOURS) && mapPlaces.getOpeningHours() != null && mapPlaces.getOpeningHours().isOpenNow()) {
                            filteredList.add(mapPlaces);
                        } else if (mapPlaces.getRating() > 0) {
                            if (key.contains(Constant.FILTER_RATING_MORE_4) && mapPlaces.getRating() > 4) {
                                filteredList.add(mapPlaces);
                            } else if (key.contains(Constant.FILTER_RATING_MORE_3) && mapPlaces.getRating() > 3) {
                                filteredList.add(mapPlaces);
                            } else if (key.contains(Constant.FILTER_RATING_MORE_2) && mapPlaces.getRating() > 2) {
                                filteredList.add(mapPlaces);
                            } else if (key.contains(Constant.FILTER_RATING_MORE_1) && mapPlaces.getRating() > 1) {
                                filteredList.add(mapPlaces);
                            }
                        }
                    }
                }

                filterResults.values = filteredList;
                filterResults.count = filteredList.size();

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterArrayList = (ArrayList) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder {
        private TextView tvName;
    }
}
