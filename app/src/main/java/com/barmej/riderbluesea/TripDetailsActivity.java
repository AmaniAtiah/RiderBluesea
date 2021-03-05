package com.barmej.riderbluesea;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.barmej.riderbluesea.callback.BookTripCommunicationInterface;
import com.barmej.riderbluesea.callback.StatusCallBack;
import com.barmej.riderbluesea.domain.TripManager;
import com.barmej.riderbluesea.domain.entity.Global;
import com.barmej.riderbluesea.domain.entity.Trip;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class TripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final String TRIP_DATA = "trip_data";

    private TextView mDateTextView;
    private TextView mFromCountryTextView;
    private TextView mToCountryTextView;
    private TextView mAvailableSeatTextViewNum;
    private TextView mAvailableSeatTextView;
    private MapView mMapView;
    private GoogleMap mMap;
    private StatusCallBack statusCallBack = getStatusCallBack();;
    private Trip trip;
    private Button bookButton;
    private TextView NoAvailableSeat;
    private LinearLayout linearLayoutAvailableSeat;
    private BookTripCommunicationInterface tripCommunicationInterface;
    private Button cancelButton;
    private Marker currentMarker;
    private Marker pickUpMarker;
    private Marker destinationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        mDateTextView = findViewById(R.id.text_view_trip_date);
        mFromCountryTextView = findViewById(R.id.text_view_from_country);
        mToCountryTextView = findViewById(R.id.text_view_to_country);
        mAvailableSeatTextViewNum = findViewById(R.id.text_view_available_seats_num);
        mAvailableSeatTextView = findViewById(R.id.text_view_available_seats);
        linearLayoutAvailableSeat = findViewById(R.id.linearLayout_available_seat);
        bookButton = findViewById(R.id.book_button);
        NoAvailableSeat = findViewById(R.id.No_available_seat);
        cancelButton = findViewById(R.id.cancel_button);
        mMapView = findViewById(R.id.map_view);

        if (getIntent() != null && getIntent().getExtras() != null) {
            trip = getIntent().getExtras().getParcelable(TRIP_DATA);
            if (trip != null) {
                mDateTextView.setText(trip.getFormattedDate());
                mFromCountryTextView.setText(trip.getFromCountry());
                mToCountryTextView.setText(trip.getToCountry());
                mAvailableSeatTextViewNum.setText(String.valueOf(trip.getAvailableSeats()));
            }
        }

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        getBookCommunicationInterface();
        setBookTripCommunicationInterface(tripCommunicationInterface);

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookFlight();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFlight();
            }
        });
    }

    private void bookFlight() {
        if (tripCommunicationInterface != null) {
            tripCommunicationInterface.bookFlight();
        }
    }

    private void cancelFlight() {
       if (tripCommunicationInterface != null) {
            tripCommunicationInterface.cancelFlight();
       }
    }

    private void getBookCommunicationInterface() {
        tripCommunicationInterface = new BookTripCommunicationInterface() {
            @Override
            public void bookFlight() {
                TripManager.getInstance().bookFlight();
            }

            @Override
            public void cancelFlight() {
                TripManager.getInstance().cancelFlight();
            }
        };
    }

    private StatusCallBack getStatusCallBack() {
        return new StatusCallBack() {
            @Override
            public void onUpdate(Trip trip) {
                mAvailableSeatTextViewNum.setText(String.valueOf(trip.getAvailableSeats()));

                if(Global.CURRENT_USER.getReservedTrips().containsKey(trip.getId())) {
                    bookButton.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.VISIBLE);
                } else {
                    bookButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.GONE);
                    if (trip.getAvailableSeats() == 0) {
                        hideAllViews();
                        linearLayoutAvailableSeat.setVisibility(View.VISIBLE);
                        mAvailableSeatTextViewNum.setVisibility(View.VISIBLE);
                        mAvailableSeatTextView.setVisibility(View.VISIBLE);
                    }
                }

                if (trip.getStatus().equals(Trip.Status.START_TRIP.name())) {
                    hideAllViews();
                    showOnTripView(trip);
                }
            }

        };
    }

    public void showOnTripView(Trip trip) {
        setCurrentMarker(new LatLng(trip.getCurrentLat(), trip.getCurrentLng()));
        setPickUpMarker(new LatLng(trip.getPickupLat(), trip.getPickupLng()));
        setDestinationMarker(new LatLng(trip.getDestinationLat(), trip.getDestinationLng()));
    }

    public void setCurrentMarker(LatLng target) {
        if (mMap == null)
            return;
        if (currentMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.boat);
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.position(target);
            currentMarker = mMap.addMarker(options);

        } else {
            currentMarker.setPosition(target);
        }
    }

    public void setPickUpMarker(LatLng target) {
        if (mMap == null) return;

        if (pickUpMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.position);
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.position(target);

            pickUpMarker = mMap.addMarker(options);

        } else {
            pickUpMarker.setPosition(target);
        }
    }

    public void setDestinationMarker(LatLng target) {
        if (mMap == null)
            return;
        if (destinationMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.destination);
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.position(target);
            destinationMarker = mMap.addMarker(options);

        } else {
            destinationMarker.setPosition(target);
        }
    }

    public void setBookTripCommunicationInterface(BookTripCommunicationInterface tripCommunicationInterface) {
        this.tripCommunicationInterface = tripCommunicationInterface;
    }

    private void hideAllViews() {
        NoAvailableSeat.setVisibility(View.GONE);
        bookButton.setVisibility(View.GONE);
        mAvailableSeatTextViewNum.setVisibility(View.GONE);
        mAvailableSeatTextView.setVisibility(View.GONE);
        linearLayoutAvailableSeat.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermissionAndSetUpUserLocation();
    }

    public void checkLocationPermissionAndSetUpUserLocation() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setUpUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}
                    , REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpUserLocation();
            } else {
                Toast.makeText(this,R.string.location_permission_needed,Toast.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    private void setUpUserLocation() {
        if (mMap == null)
            return;
        mMap.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f);
                    mMap.moveCamera(update);

                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState,@NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState,outPersistentState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        TripManager.getInstance().startListeningForStatus(statusCallBack, trip.getId());
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        TripManager.getInstance().stopListeningToStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}