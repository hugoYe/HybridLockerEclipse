package com.cooeeui.lock.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2016/1/5.
 */
public class ModelNotification implements Parcelable {


    protected ModelNotification(Parcel in) {
    }

    public static final Creator<ModelNotification> CREATOR = new Creator<ModelNotification>() {
        @Override
        public ModelNotification createFromParcel(Parcel in) {
            return new ModelNotification(in);
        }

        @Override
        public ModelNotification[] newArray(int size) {
            return new ModelNotification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
