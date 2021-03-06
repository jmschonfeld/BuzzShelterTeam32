package edu.gatech.spacebarz.buzzshelter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

import edu.gatech.spacebarz.buzzshelter.model.Shelter;
import edu.gatech.spacebarz.buzzshelter.model.UserInfo;
import edu.gatech.spacebarz.buzzshelter.util.CustomShelterFilter;
import edu.gatech.spacebarz.buzzshelter.util.FirebaseAuthManager;
import edu.gatech.spacebarz.buzzshelter.util.FirebaseDBManager;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int FILTER_MAP_RETURN_REQUEST_CODE = 1002, DEFAULT_ZOOM = 16;

    private static final double EMULATOR_LATITUDE = 33.7776210, EMULATOR_LONGITUDE = -84.4048150;

    private GoogleMap map;
    private FloatingActionButton fab;
    private FusedLocationProviderClient locationProvider;
    private static final int PERMISSIONS_REQUEST_LOCATION = 0470;
    private CustomShelterFilter filter;
    private final Map<String, String> markerToShelter = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab = findViewById(R.id.filter_fab);
        final Context c = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, FilterSheltersActivity.class);
                startActivityForResult(i, FILTER_MAP_RETURN_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILTER_MAP_RETURN_REQUEST_CODE && resultCode == RESULT_OK && data.hasExtra("filter")) {
            filter = (CustomShelterFilter) data.getSerializableExtra("filter");
            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Filtered shelters", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("View All Shelters", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filter = null;
                    final Handler handler = new Handler();
                    new Thread() {
                        @Override
                        public void run() {
                            loadShelters(handler);
                        }
                    }.start();
                    snack.dismiss();
                    fab.setVisibility(View.VISIBLE);
                }
            });
            fab.setVisibility(View.GONE);
            snack.show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("Map", "Map is ready!");
        map = googleMap;
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        loadLocation();
        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                loadShelters(handler);
            }
        }.start();
    }

    private void loadShelters(Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                map.clear();
                markerToShelter.clear();
            }
        });

        UserInfo curUser = FirebaseDBManager.retrieveCurrentUserInfo();
        String resShelter = null;
        if (curUser != null && curUser.getCurrentReservation() != null) {
            resShelter = FirebaseDBManager.retrieveReservation(curUser.getCurrentReservation()).getShelterID();
        }

        Shelter[] shelters = FirebaseDBManager.retrieveAllShelters();
        for (final Shelter shelter : shelters) {
            if (filter != null && !filter.filter(shelter) && (resShelter == null || !resShelter.equals(shelter.getUID()))) {
                continue;
            }

            final MarkerOptions opts = new MarkerOptions();
            opts.position(new LatLng(shelter.getLat(), shelter.getLon()));
            opts.title(shelter.getName());
            opts.snippet(shelter.getName());

            if (resShelter != null && resShelter.equals(shelter.getUID())) {
                opts.icon(BitmapDescriptorFactory.defaultMarker(186.0f));
            } else if (shelter.getVacancyNum() == 0) {
                opts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            } else {
                opts.icon(BitmapDescriptorFactory.defaultMarker(52.0f));
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Marker mark = map.addMarker(opts);
                    markerToShelter.put(mark.getId(), shelter.getUID());
                }
            });
        }
        final Context context = this;
        handler.post(new Runnable() {
            @Override
            public void run() {
               map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                   @Override
                   public boolean onMarkerClick(Marker marker) {
                       Intent intent = new Intent(context, ShelterDetailActivity.class);
                       intent.putExtra("shelterUID", markerToShelter.get(marker.getId()));
                       startActivity(intent);
                       return true;
                   }
               });
            }
        });
    }

    private void loadLocation() throws SecurityException {
        locationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("Location", "Received Location: " + location);
                LatLng latlng = null;
                if (Build.FINGERPRINT.contains("generic")) {
                    latlng = new LatLng(EMULATOR_LATITUDE, EMULATOR_LONGITUDE);
                } else {
                    latlng = new LatLng(location.getLatitude(), location.getLongitude());
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        map.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadLocation();
                }
                break;
            }
        }
    }

    @Override
    public void onRestart() {
        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                loadShelters(handler);
            }
        }.start();

        super.onRestart();
    }

    @Override
    public void finish() {
        FirebaseAuthManager.signout();
        Toast.makeText(getApplicationContext(), R.string.toast_logged_out, Toast.LENGTH_SHORT).show();
        super.finish();
    }
}
