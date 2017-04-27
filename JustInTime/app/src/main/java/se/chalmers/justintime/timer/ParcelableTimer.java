package se.chalmers.justintime.timer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Nieo on 25/04/17.
 */

public class ParcelableTimer implements Parcelable {

    private Integer id;
    private ArrayList<Long> durations;

    public ParcelableTimer(Integer id, ArrayList<Long> durations){
        this.id = id;
        this.durations = durations;
    }
    public ParcelableTimer(Parcel in){
        id = in.readInt();
        durations = (ArrayList<Long>) in.readSerializable();
    }


    public Integer getId() {
        return id;
    }

    public ArrayList<Long> getDurations() {
        return durations;
    }

    public static final Creator<ParcelableTimer> CREATOR = new Creator<ParcelableTimer>() {
        @Override
        public ParcelableTimer createFromParcel(Parcel in) {
            return new ParcelableTimer(in);
        }

        @Override
        public ParcelableTimer[] newArray(int size) {
            return new ParcelableTimer[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeSerializable(durations);
    }
}
