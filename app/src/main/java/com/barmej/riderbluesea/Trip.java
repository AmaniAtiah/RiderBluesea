package com.barmej.riderbluesea;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Trip implements Parcelable{
    private String fromCountry;
    private String toCountry;
    private Date date;
    private int availableSeats;
    private int reservedSeats;
    private String id;
    private String status;
    private double currentLat;
    private double currentLng;
    private double pickupLat;
    private double pickupLng;
    private double destinationLat;
    private double destinationLng;
    private HashMap<String, Object> reservedTripsUser;



    public Trip(){

    }

    public Trip(String fromCountry,String toCountry,Date date,int availableSeats,int reservedSeats,String id,String status) {
        this.fromCountry = fromCountry;
        this.toCountry = toCountry;
        this.date = date;
        this.availableSeats = availableSeats;
        this.reservedSeats = reservedSeats;
        this.id = id;
        this.status = status;
    }


    protected Trip(Parcel in) {
        fromCountry = in.readString();
        toCountry = in.readString();
        availableSeats = in.readInt();
        reservedSeats = in.readInt();
        id = in.readString();
        status = in.readString();
        date = (Date) in.readSerializable();
        currentLat = in.readDouble();
        currentLng = in.readDouble();
        pickupLat = in.readDouble();
        pickupLng = in.readDouble();
        destinationLat = in.readDouble();
        destinationLng = in.readDouble();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public com.barmej.riderbluesea.Trip createFromParcel(Parcel in) {
            return new com.barmej.riderbluesea.Trip(in);
        }

        @Override
        public com.barmej.riderbluesea.Trip[] newArray(int size) {
            return new com.barmej.riderbluesea.Trip[size];
        }
    };

    public String getFromCountry() {
        return fromCountry;
    }

    public void setFromCountry(String fromCountry) {
        this.fromCountry = fromCountry;
    }

    public String getToCountry() {
        return toCountry;
    }

    public void setToCountry(String toCountry) {
        this.toCountry = toCountry;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(int reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public HashMap<String, Object> getReservedTripsUser() {
        return reservedTripsUser;
    }

    public void setReservedTripsUser(HashMap<String, Object> reservedTripsUser) {
        this.reservedTripsUser = reservedTripsUser;
    }

    public String getFormattedDate() {
        if (date != null) {
            String dateFormat = "dd MMM";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
            return simpleDateFormat.format(date.getTime());
           // return DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.getDefault()).format(date.getTime());
        } else {
            return "";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags) {
        dest.writeString(fromCountry);
        dest.writeString(toCountry);
        dest.writeInt(availableSeats);
        dest.writeInt(reservedSeats);
        dest.writeString(id);
        dest.writeString(status);
        dest.writeSerializable(date);
        dest.writeDouble(currentLat);
        dest.writeDouble(currentLng);
        dest.writeDouble(pickupLat);
        dest.writeDouble(pickupLng);
        dest.writeDouble(destinationLat);
        dest.writeDouble(destinationLng);

    }

    public enum Status {
        AVAILABLE,
        START_TRIP,
        ARRIVED,
        FINISH_TRIP

    }
}
