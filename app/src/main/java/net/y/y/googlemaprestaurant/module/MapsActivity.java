package net.y.y.googlemaprestaurant.module;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.y.y.googlemaprestaurant.R;
import net.y.y.googlemaprestaurant.adapter.NameListAdapter;
import net.y.y.googlemaprestaurant.global.Constant;
import net.y.y.googlemaprestaurant.model.MapPlaces;
import net.y.y.googlemaprestaurant.network.RetrofitCallback;
import net.y.y.googlemaprestaurant.utility.DialogUtility;
import net.y.y.googlemaprestaurant.utility.LocationUtility;
import net.y.y.googlemaprestaurant.utility.PermissionUtility;
import net.y.y.googlemaprestaurant.utility.RetrofitUtility;
import net.y.y.googlemaprestaurant.utility.UiUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentCameraPosition = new LatLng(0, 0);
    private float minZoomLevel = 17;

    private Marker selfMarker;
    private Marker selectMarker;

    private ImageView ivExpand;
    private ExpandableLayout eltSetting;
    private ExpandableLayout eltReOrder;
    private LinearLayout lltReOrder;
    private ImageView ivReOrderExpand;
    private RadioGroup rgReOrder;
    private LinearLayout lltFilter;
    private ExpandableLayout eltFilter;
    private ImageView ivFilter;
    private CheckBox cbOpen;
    private CheckBox cbRating4;
    private CheckBox cbRating3;
    private CheckBox cbRating2;
    private CheckBox cbRating1;

    private ListView lvName;
    private NameListAdapter adapter;
    private ArrayList<MapPlaces> mapPlacesArrayList = new ArrayList<>();
    private String selectType = Constant.SEQUENCE_DISTANCE;
    private ArrayList<String> filterMapsPlaceList = new ArrayList<>();

    // location
    private PermissionUtility permissionUtility;
    private LocationUtility locationUtility;
    private RetrofitUtility retrofitUtility;

    // checking
    private boolean isLoading = false;
    private int selfPositionUpdateCount = 0;
    private int maxSelfPositionUpdateCount = 2;

    // place info
    private AlertDialog infoDialog;
    private ImageView iv;
    private TextView tvName;
    private TextView tvVicinity;
    private TextView tvClose;

    // animation
    private int rotate_duration = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        retrofitUtility = new RetrofitUtility(MapsActivity.this);

        findId();

        initInfoDialog();
        requestLocationPermission();
        setNameList();
        setOnClick();
        setRadioGroupChange();
        setCheckedChange();
    }

    private void findId() {
        lvName = findViewById(R.id.lv_name);
        ivExpand = findViewById(R.id.iv_expand);
        eltSetting = findViewById(R.id.elt_setting);
        eltReOrder = findViewById(R.id.elt_re_order);
        lltReOrder = findViewById(R.id.llt_re_order);
        ivReOrderExpand = findViewById(R.id.iv_re_order_expand);
        rgReOrder = findViewById(R.id.rg_re_order);
        cbOpen = findViewById(R.id.cb_open);
        cbRating4 = findViewById(R.id.cb_rating_4);
        cbRating3 = findViewById(R.id.cb_rating_3);
        cbRating2 = findViewById(R.id.cb_rating_2);
        cbRating1 = findViewById(R.id.cb_rating_1);
        lltFilter = findViewById(R.id.llt_filter);
        eltFilter = findViewById(R.id.elt_filter);
        ivFilter = findViewById(R.id.iv_filter);
    }

    private void setOnClick() {
        ivExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationControl(ivExpand, 180, ivExpand);
                eltSetting.toggle();
            }
        });

        lltReOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (eltReOrder.isExpanded()) {
                    animationControl(ivReOrderExpand, -90, lltReOrder);
                } else {
                    animationControl(ivReOrderExpand, 90, lltReOrder);
                }
                eltReOrder.toggle();
            }
        });

        lltFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eltFilter.isExpanded()) {
                    animationControl(ivFilter, -90, lltFilter);
                } else {
                    animationControl(ivFilter, 90, lltFilter);
                }
                eltFilter.toggle();
            }
        });
    }

    private void setCheckedChange() {
        cbOpen.setTag(Constant.FILTER_OPEN_HOURS);
        cbRating4.setTag(Constant.FILTER_RATING_MORE_4);
        cbRating3.setTag(Constant.FILTER_RATING_MORE_3);
        cbRating2.setTag(Constant.FILTER_RATING_MORE_2);
        cbRating1.setTag(Constant.FILTER_RATING_MORE_1);

        cbOpen.setOnCheckedChangeListener(onCheckedChangeListener);
        cbRating4.setOnCheckedChangeListener(onCheckedChangeListener);
        cbRating3.setOnCheckedChangeListener(onCheckedChangeListener);
        cbRating2.setOnCheckedChangeListener(onCheckedChangeListener);
        cbRating1.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if(b && compoundButton.getTag() != null && !filterMapsPlaceList.contains(((String) compoundButton.getTag()))) {
                filterMapsPlaceList.add((String) compoundButton.getTag());
            } else {
                filterMapsPlaceList.remove((String) compoundButton.getTag());
            }

            JSONArray jsonArray = new JSONArray(filterMapsPlaceList);
            adapter.getFilter().filter(jsonArray.toString());
        }
    };

    private void animationControl(final ImageView iv, int rotate, final View view) {

        view.setEnabled(false);

        iv.animate()
                .rotationBy(rotate)
                .setDuration(rotate_duration)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(false);
                    }
                })
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                })
                .setInterpolator(new LinearInterpolator())
                .start();
    }

    private void setRadioGroupChange() {
        rgReOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = radioGroup.findViewById(i);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    int index = radioGroup.indexOfChild(checkedRadioButton);

                    switch (index) {
                        case 0:
                            selectType = Constant.SEQUENCE_DISTANCE;
                            adapter.sequenceList(Constant.SEQUENCE_DISTANCE, selfMarker.getPosition().latitude, selfMarker.getPosition().longitude);
                            break;
                        case 1:
                            selectType = Constant.SEQUENCE_NAME;
                            adapter.sequenceList(Constant.SEQUENCE_NAME, selfMarker.getPosition().latitude, selfMarker.getPosition().longitude);
                            break;
                    }
                }
            }
        });
    }

    private void initInfoDialog() {

        View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.dialog_place_detail, null);
        infoDialog = DialogUtility.customDialog(
                MapsActivity.this,
                view
        );

        iv = view.findViewById(R.id.iv);
        tvName = view.findViewById(R.id.tv_name);
        tvVicinity = view.findViewById(R.id.tv_vicinity);
        tvClose = view.findViewById(R.id.tv_close);
    }

    private void requestLocationPermission() {

        permissionUtility = new PermissionUtility(
                this,
                new PermissionUtility.PermissionCallBack() {
                    @Override
                    public void onPermissionAllow() {
                        locationUtility = new LocationUtility(MapsActivity.this);

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MapsActivity.this);
                    }

                    @Override
                    public void onPermissionReject() {
                        errorDialog(getString(R.string.please_allow_permission_for_continuing));
                    }
                }
        );

        permissionUtility.checkLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initMarker();
        mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
        positioningMySelf();
        updateMarkerOnMapCameraMove();

    }

    private void initMarker() {
        selfMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(getString(R.string.self)));

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icon_marker);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        selectMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("").icon(smallMarkerIcon));
        selectMarker.setVisible(false);
    }

    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            if (marker.getTag() != null) {

                MapPlaces mapPlaces = (MapPlaces) marker.getTag();

                Glide
                        .with(MapsActivity.this)
                        .applyDefaultRequestOptions(new RequestOptions().centerInside())
                        .load(mapPlaces.getIcon())
                        .into(iv);

                tvName.setText(mapPlaces.getName());
                tvVicinity.setText(mapPlaces.getVicinity());
                tvClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        infoDialog.dismiss();
                    }
                });

                infoDialog.show();
            }
        }
    };

    private void positioningMySelf() {
        if (permissionUtility.allowLocationPermission()) {

            locationUtility.getLocation(
                    LocationUtility.Method.NETWORK_THEN_GPS,
                    new LocationUtility.Listener() {
                        @Override
                        public void onLocationFound(Location location) {
                            selfMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

                            if (selfPositionUpdateCount < maxSelfPositionUpdateCount) {
                                selfPositionUpdateCount++;

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selfMarker.getPosition(), minZoomLevel));
                                mMap.setMaxZoomPreference(minZoomLevel);

                                if (!isLoading) {
                                    isLoading = true;
                                    getMapPlacesApi(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                                }
                            }
                        }

                        @Override
                        public void onLocationNotFound() {

                        }
                    }
            );
        }
    }

    private void updateMarkerOnMapCameraMove() {
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latestPosition = mMap.getCameraPosition().target;
                float distance = locationUtility.getTwoPointDistance(
                        currentCameraPosition.latitude,
                        currentCameraPosition.longitude,
                        latestPosition.latitude,
                        latestPosition.longitude
                );

                if (distance > Constant.DISTANCE_UPDATE) {
                    currentCameraPosition = latestPosition;

                    if (!isLoading) {
                        isLoading = true;
                        getMapPlacesApi(Double.toString(currentCameraPosition.latitude), Double.toString(currentCameraPosition.longitude));
                    }
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtility.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getMapPlacesApi(String latitude, String longitude) {
        UiUtility.showProgressDialog(this);
        retrofitUtility.apiGetMapPlaces(
                String.format("%s,%s", latitude, longitude),
                "150",
                "restaurant",
                new RetrofitCallback.RequestListener() {
                    @Override
                    public void onFailure(String errorMessage) {
                        UiUtility.dismissProgressDialog();
                        errorDialog(errorMessage);

                        isLoading = false;
                    }

                    @Override
                    public void onResult(String status, JSONObject jsonObject) {
                        UiUtility.dismissProgressDialog();
                        isLoading = false;
                        try {
                            if (status.equals("OK")) {
                                ArrayList<MapPlaces> list = new Gson().fromJson(jsonObject.get("results").toString(), new TypeToken<ArrayList<MapPlaces>>() {
                                }.getType());

                                mapPlacesArrayList.clear();
                                mapPlacesArrayList.addAll(list);
                                adapter.notifyDataSetChanged();
                                adapter.sequenceList(selectType, selfMarker.getPosition().latitude, selfMarker.getPosition().longitude);

                            } else {
                                if (status.equals("ZERO_RESULTS")) {

                                } else if (status.equals("OVER_QUERY_LIMIT")) {
                                    errorDialog(jsonObject.getString("error_message"));
                                } else {
                                    errorDialog(getString(R.string.error_happen));
                                }
                            }
                        } catch (JSONException e) {
                            errorDialog(getString(R.string.error_happen));
                        }
                    }
                }
        );
    }

    private void setNameList() {
        adapter = new NameListAdapter(this, mapPlacesArrayList);
        lvName.setAdapter(adapter);

        lvName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MapPlaces mapPlaces = mapPlacesArrayList.get(i);

                selectMarker.setPosition(new LatLng(
                        mapPlaces.getGeometry().getLocation().getLat(),
                        mapPlaces.getGeometry().getLocation().getLng()
                ));
                selectMarker.setTitle(mapPlaces.getName());
                selectMarker.setVisible(true);

                selectMarker.setTag(mapPlaces);
                selectMarker.showInfoWindow();

                mMap.moveCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(
                                mapPlaces.getGeometry().getLocation().getLat(),
                                mapPlaces.getGeometry().getLocation().getLng())
                ));
            }
        });
    }

    private void errorDialog(String error) {
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle(getString(R.string.message))
                .setMessage(error)
                .setPositiveButton(
                        getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                moveTaskToBack(true);
                            }
                        }
                ).show();
    }
}
