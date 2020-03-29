/*
 * CONTACT CUSTOM OBJECT
 *
 * Here we store basic info of a contact
 *
 */

package si.uni_lj.fri.pbd.miniapp1.ui.contacts;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    private String contactName;
    private String contactEmailAddress;
    private String contactPhoneNumber;
    private boolean contactState;

    public Contact() {
        this.contactName = null;
        this.contactEmailAddress = null;
        this.contactPhoneNumber = null;
        this.contactState = false;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactEmailAddress(String contactEmailAddress) {
        this.contactEmailAddress = contactEmailAddress;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public void setContactState(boolean contactState) {
        this.contactState = contactState;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmailAddress() {
        return contactEmailAddress;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public boolean getContactState() {
        return contactState;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(contactName);
        if (contactPhoneNumber != null) {
            builder.append(" " + contactPhoneNumber);
        }
        if (contactEmailAddress != null) {
            builder.append(" " + contactEmailAddress);
        }
        builder.append(" Selected: " + String.valueOf(contactState));
        return builder.toString();
    }

    // this is needed for parcelable, generated with http://www.parcelabler.com/

    protected Contact(Parcel in) {
        contactName = in.readString();
        contactEmailAddress = in.readString();
        contactPhoneNumber = in.readString();
        contactState = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactName);
        dest.writeString(contactEmailAddress);
        dest.writeString(contactPhoneNumber);
        dest.writeByte((byte) (contactState ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}

