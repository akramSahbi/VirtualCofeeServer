package com.thinkit.model;

import java.util.ArrayList;
import java.util.List;

public class GuestAvailability {

    public GuestAvailability(String guestName, String guestOffset, List<int[]> guestAvailabilities) {
        this.guestName = guestName;
        this.guestOffset = guestOffset;
        this.guestAvailabilities = guestAvailabilities;
    }

    public GuestAvailability() {}

    String guestName;
    String guestOffset;
    List<int[]> guestAvailabilities = new ArrayList<>();

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestOffset() {
        return guestOffset;
    }

    public void setGuestOffset(String guestOffset) {
        this.guestOffset = guestOffset;
    }

    public List<int[]> getGuestAvailabilities() {
        return guestAvailabilities;
    }

    public void setGuestAvailabilities(List<int[]> guestAvailabilities) {
        this.guestAvailabilities = guestAvailabilities;
    }

    @Override
    public String toString() {
        return "GuestAvailability{" +
                "guestName='" + guestName + '\'' +
                ", guestOffset='" + guestOffset + '\'' +
                ", guestAvailabilities=" + guestAvailabilities +
                '}';
    }
}
