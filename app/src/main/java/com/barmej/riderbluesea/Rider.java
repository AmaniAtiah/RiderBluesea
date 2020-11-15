package com.barmej.riderbluesea;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Rider implements Parcelable {
    private String username;
    private String email;
    private String password;
    private String photo;
    private String status;
    private String id;
    private HashMap<String, Object> reservedTrips;

    public Rider() {

    }

    public Rider(String username,String email,String password,String photo) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.photo = photo;
        reservedTrips = new HashMap<>();
    }

    protected Rider(Parcel in) {
        username = in.readString();
        email = in.readString();
        password = in.readString();
        photo = in.readString();
    }

    public static final Creator<Rider> CREATOR = new Creator<Rider>() {
        @Override
        public Rider createFromParcel(Parcel in) {
            return new Rider(in);
        }

        @Override
        public Rider[] newArray(int size) {
            return new Rider[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setReservedTrips(HashMap<String, Object> reservedTrips) {
        this.reservedTrips = reservedTrips;
    }

    public HashMap<String, Object> getReservedTrips() {
        if(reservedTrips == null) {
            reservedTrips = new HashMap<>();
        }
        return reservedTrips;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(photo);
    }

    public enum Status{
        AVAILABLE,
        BOOK_TRIP,
        ARRIVED
    }
}
