package com.barmej.riderbluesea;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.barmej.riderbluesea.SignUpActivity.USER_REF_PATH;
import static com.barmej.riderbluesea.TripListFragment.TRIP_REF_PATH;

public class TripManager {

    private FirebaseDatabase database;
    private static TripManager instance;
    private Trip trip;
    private  StatusCallBack statusCallBack;
    private ValueEventListener tripListener;

    public TripManager() {
        database = FirebaseDatabase.getInstance();
    }

    public static TripManager getInstance() {
        if (instance == null) {
            instance = new TripManager();
        }
        return instance;
    }


    public void startListeningForStatus(StatusCallBack statusCallBack, String id) {
        System.out.println("startListeningForStatus");
        this.statusCallBack = statusCallBack;
        tripListener = database.getReference(TRIP_REF_PATH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("Change@@");
                        trip = dataSnapshot.getValue(Trip.class);
                        if (trip != null) {
                            System.out.println("Trip Change");
                            FullStatus fullStatus = new FullStatus();
                            fullStatus.setTrip(trip);
                            notifyListener(fullStatus);
                        } else {
                            System.out.println("Null trip");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }


    private void notifyListener(FullStatus fullStatus) {
        if (statusCallBack != null) {
            System.out.println("notify");
            statusCallBack.onUpdate(fullStatus);
        }
    }

    public void bookFlight() {

        int reservedSeats = 0;
        int availableSeat = trip.getAvailableSeats();

        if (reservedSeats < availableSeat) {
            reservedSeats = trip.getReservedSeats();
            trip.setReservedSeats(++reservedSeats);
            trip.setAvailableSeats(--availableSeat);
            updateAvailableAnReservedSeat();


            Global.CURRENT_USER.getReservedTrips().put(trip.getId(), trip.getId());
            updateTrip();
//            database.getReference(USER_REF_PATH).child(Global.CURRENT_USER.getId()).setValue(Global.CURRENT_USER);

//            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            FirebaseDatabase.getInstance().getReference(USER_REF_PATH).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//            //       rider = snapshot.getValue(Rider.class);
//                 //   String booked = "محجوز";
//              //      trip.setBooked(booked);
//                    Global.CURRENT_USER.getReservedTrips().put(trip.getId(), trip.getId());
//                   // rider.getReservedTrips().remove(trip.getId());
//                    database.getReference(USER_REF_PATH).child(userId).setValue(Global.CURRENT_USER).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });


            // database.getReference(USER_REF_PATH).child(userId);


        }





    }

    public void cancelFlight() {
        int reservedSeats;
        int availableSeat = trip.getAvailableSeats();

      //  if (reservedSeats < availableSeat) {
        if(Global.CURRENT_USER.getReservedTrips().containsKey(trip.getId())) {
            reservedSeats = trip.getReservedSeats();
            trip.setReservedSeats(--reservedSeats);
            trip.setAvailableSeats(++availableSeat);
            updateAvailableAnReservedSeat();

            Global.CURRENT_USER.getReservedTrips().remove(trip.getId());
//            database.getReference(USER_REF_PATH).child(Global.CURRENT_USER.getId()).setValue(Global.CURRENT_USER);
            updateTrip();
        }


//            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            FirebaseDatabase.getInstance().getReference(USER_REF_PATH).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    //       rider = snapshot.getValue(Rider.class);
//                    //   String booked = "محجوز";
//                    //      trip.setBooked(booked);
//                   // Global.CURRENT_USER.getReservedTrips().put(trip.getId(), trip.getId());
//                     Global.CURRENT_USER.getReservedTrips().remove(trip.getId());
//                    database.getReference(USER_REF_PATH).child(userId).setValue(Global.CURRENT_USER).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });


            // database.getReference(USER_REF_PATH).child(userId);


       // }

    }

    private void updateAvailableAnReservedSeat() {
        database.getReference(TRIP_REF_PATH).child(trip.getId()).setValue(trip);
        FullStatus fullStatus = new FullStatus();
        fullStatus.setTrip(trip);
        notifyListener(fullStatus);
    }

    private void updateTrip() {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference(USER_REF_PATH).child(userId).setValue(Global.CURRENT_USER);
//        FullStatus fullStatus = new FullStatus();
//        fullStatus.setRider(Global.CURRENT_USER);
//        notifyListener(fullStatus);
    }


    public void stopListeningToStatus() {
        if (tripListener != null) {
            database.getReference().child(trip.getId()).removeEventListener(tripListener);
        }
        statusCallBack = null;
    }

}
