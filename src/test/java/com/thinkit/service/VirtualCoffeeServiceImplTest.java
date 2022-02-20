package com.thinkit.service;


import com.thinkit.model.GuestAvailability;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VirtualCoffeeServiceImplTest {

    static VirtualCoffeeServiceImpl service;

    @BeforeAll
    static void init() {
        service = new VirtualCoffeeServiceImpl();
    }

    @DisplayName("Testing convertToMilitaryTime")
    @Test
    void should_convert_am_pm_time_to_military_time() {
        assertEquals(11, service.convertToMilitaryTime("11am"));
        assertEquals(12, service.convertToMilitaryTime("12am"));
        assertEquals(13, service.convertToMilitaryTime("1pm"));
        assertEquals(0, service.convertToMilitaryTime("12pm"));
    }

    @DisplayName("Testing convertToDecimalOffset")
    @Test
    void should_convert_time_zoned_offset_to_decimal_offset() {
        assertEquals(1, service.convertToDecimalOffset("GMT+1"));
        assertEquals(-1, service.convertToDecimalOffset("GMT-1"));
        assertEquals(0, service.convertToDecimalOffset("gmt"));
    }

    @DisplayName("Testing fillDayScheduleWithGuestsAvailability")
    @Test
    void should_fill_day_schedule_with_guests_availability() {
        List<GuestAvailability> guestAvailabilities = buildGuestAvailability();
        boolean[][] daySchedule = new boolean[guestAvailabilities.size()][VirtualCoffeeServiceImpl.DAY_HOURS];

        service.initDaySchedule(daySchedule);
        service.fillDayScheduleWithGuestsAvailability(guestAvailabilities,daySchedule);

        for (int i = 0; i < VirtualCoffeeServiceImpl.DAY_HOURS; i++) {
            if ((i >= 8 && i < 12) || (i >= 14 && i < 19)) {
                assertEquals(true, daySchedule[0][i]);
            } else {
                assertEquals(false, daySchedule[0][i]);
            }
        }
    }

    @DisplayName("Testing fillDayScheduleWithGuestsAvailability")
    @Test
    void setCommonSlots() {
        List<GuestAvailability> guestAvailabilities = buildGuestAvailability();
        boolean[][] daySchedule = new boolean[guestAvailabilities.size()][VirtualCoffeeServiceImpl.DAY_HOURS];
        List<Integer> guestSlots = new ArrayList<>();

        service.initDaySchedule(daySchedule);
        service.fillDayScheduleWithGuestsAvailability(guestAvailabilities,daySchedule);

        service.setCommonSlots(8, 10, guestAvailabilities, daySchedule, guestSlots);

        for (int i = 0; i < VirtualCoffeeServiceImpl.DAY_HOURS; i++) {
            if (i >= 8 && i < 10) {
                assertEquals(true,guestSlots.contains(i));
            } else {
                assertEquals(false,guestSlots.contains(i));
            }
        }


    }

    private List<GuestAvailability> buildGuestAvailability() {
        int[] morningAvailability = {8, 12};
        int[] afternoonAvailability = {14, 19};
        List<int[]> availabilities = new ArrayList<>();
        availabilities.add(morningAvailability);
        availabilities.add(afternoonAvailability);
        GuestAvailability guestAvailability = new GuestAvailability("Anis", "gmt+1", availabilities);
        return Arrays.asList(guestAvailability);
    }
}
