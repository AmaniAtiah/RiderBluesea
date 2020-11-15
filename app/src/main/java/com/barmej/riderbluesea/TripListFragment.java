package com.barmej.riderbluesea;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripListFragment extends Fragment implements TripListAdapter.OnTripClickListener{
    public static final String TRIP_REF_PATH = "trips";
    private static final String INITIAL_STATUS_EXTRA = "INITIAL_STATUS_EXTRA";
    private RecyclerView mRecyclerViewTrip;
    private TripListAdapter mTripsListAdapter;
    private ArrayList<Trip> mTrips;


//    public static TripListFragment getInstance(FullStatus status) {
//        TripListFragment fragment = new TripListFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(INITIAL_STATUS_EXTRA, status);
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        mRecyclerViewTrip = view.findViewById(R.id.recycler_view_trip);
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
                            Trip trip = dataSnapshot.getValue(Trip.class);
                            if (trip != null) {
                                if (trip.getStatus().equals(Trip.Status.AVAILABLE.name())) {
                                    mTrips.add(trip);
                                }
                            }
                            mTripsListAdapter.notifyDataSetChanged();
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
