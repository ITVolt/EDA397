package se.chalmers.justintime.timer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nieo on 25/04/17.
 */

public class ParcelableLong implements Parcelable {
    private Long l;

    public ParcelableLong(Long l){
        this.l = l;
    }
    protected ParcelableLong(Parcel in) {
        l = in.readLong();
    }
    public Long getL(){
        return l;
    }

    public static final Creator<ParcelableLong> CREATOR = new Creator<ParcelableLong>() {
        @Override
        public ParcelableLong createFromParcel(Parcel in) {
            return new ParcelableLong(in);
        }

        @Override
        public ParcelableLong[] newArray(int size) {
            return new ParcelableLong[size];
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
        dest.writeLong(l);
    }
}
