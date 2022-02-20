package com.thinkit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CofeeMeetingScheduler {

    public CofeeMeetingScheduler() {}

    public CofeeMeetingScheduler(List<Integer> slots, List<String> participantNames) {
        this.slots = slots;
        this.participantNames = participantNames;
    }

    List<Integer> slots = new ArrayList<>();
    List<String> participantNames = new ArrayList<>();

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    public List<String> getParticipantNames() {
        return participantNames;
    }

    public void setParticipantNames(List<String> participantNames) {
        this.participantNames = participantNames;
    }

    @Override
    public String toString() {
        if (slots.isEmpty()) {
            return "No common slot have been found";
        }
        if (participantNames.isEmpty()) {
            return "No users have been found";
        }
        StringBuilder sb = new StringBuilder("You can have a Virtual Cofee with: "
                + String.join(",", participantNames) + "\n"
                + "at these time slots:\n");
        slots.forEach(slot -> {
            sb.append("from " + slot + " to "
                    + ((slot + 1) % 24 ) + "\n");
        });
        return sb.toString();
    }
}
