package com.barmej.riderbluesea.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.riderbluesea.R;
import com.barmej.riderbluesea.TripDetailsActivity;
import com.barmej.riderbluesea.TripListAdapter;
import com.barmej.riderbluesea.domain.entity.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TripListFragment extends Fragment implements TripListAdapter.OnTripClickListener {
    public static final String TRIP_REF_PATH = "trips";
    private RecyclerView mRecyclerViewTrip;
    private TripListAdapter mTripsListAdapter;
    private ArrayList<Trip> mTrips;
    private TextView noTripAvailable;
    private Trip trip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        mRecyclerViewTrip = view.findViewById(R.id.recycler_view_trip);
        noTripAvailable = view.findViewById(R.id.no_trip_available);
        mRecyclerViewTrip.setLayoutManager(new LinearLayoutManager(getContext()));

        mTrips = new ArrayList<>();
        mTripsListAdapter = new TripListAdapter(mTrips,TripListFragment.this);
        mRecyclerViewTrip.setAdapter(mTripsListAdapter);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference(TRIP_REF_PATH).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mTrips.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                             trip = dataSnapshot.getValue(Trip.class);
                            if (trip != null) {
                                if (trip.getStatus().equals(Trip.Status.AVAILABLE.name())) {
                                    mTrips.add(trip);
                                    noTripAvailable.setVisibility(View.INVISIBLE);

                                }
                            }
                            mTripsListAdapter.notifyDataSetChanged();
                        }

                    if (trip == null) {
                        noTripAvailable.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    @Override
    public void onTripClick(Trip trip) {
        Intent intent = new Intent(getContext(), TripDetailsActivity.class);
        intent.putExtra(TripDetailsActivity.TRIP_DATA, trip);
        startActivity(intent);

    }
}
