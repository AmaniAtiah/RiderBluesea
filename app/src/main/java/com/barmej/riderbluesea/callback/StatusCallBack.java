package com.barmej.riderbluesea.callback;


import com.barmej.riderbluesea.domain.entity.Trip;

public interface StatusCallBack {
    void onUpdate(Trip trip);
}
