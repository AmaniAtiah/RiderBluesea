package com.barmej.riderbluesea;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.TripViewHolder> {

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }
    private List<Trip> mTripsList;
    private OnTripClickListener mOnTripClickListener;


    public TripListAdapter(List<Trip> mTripsList,OnTripClickListener mOnTripClickListener) {
        this.mTripsList = mTripsList;
        this.mOnTripClickListener = mOnTripClickListener;
    }

    @NonNull
    @Override
    public TripListAdapter.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripListAdapter.TripViewHolder holder,int position) {
        holder.bind(mTripsList.get(position));

    }

    @Override
    public int getItemCount() {
        return mTripsList.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tripDateTextView;
        TextView tripFromCountry;
        TextView tripToCountry;
        TextView tripAvailableSeats;
        TextView booked;
        Trip trip;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripDateTextView = itemView.findViewById(R.id.text_view_trip_date);
            tripFromCountry = itemView.findViewById(R.id.text_view_from_country);
            tripToCountry = itemView.findViewById(R.id.text_view_to_country);
            tripAvailableSeats = itemView.findViewById(R.id.text_view_available_seats);
            booked = itemView.findViewById(R.id.text_view_booked);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnTripClickListener.onTripClick(trip);
                }
            });

        }

        public void bind(Trip trip) {
            this.trip = trip;
            tripDateTextView.setText(trip.getFormattedDate());
            tripFromCountry.setText(trip.getFromCountry());
            tripToCountry.setText(trip.getToCountry());
            tripAvailableSeats.setText(String.valueOf(trip.getAvailableSeats()));
            if(Global.CURRENT_USER.getReservedTrips().containsKey(trip.getId())) {
                booked.setVisibility(View.VISIBLE);
            } else {
                booked.setVisibility(View.GONE);
            }

        }
    }
}
