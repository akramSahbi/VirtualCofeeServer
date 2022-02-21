package com.thinkit.service;
import com.thinkit.model.CofeeMeetingScheduler;
import com.thinkit.model.GuestAvailability;
import org.bspfsystems.simplejson.JSONArray;
import org.bspfsystems.simplejson.JSONObject;
import org.bspfsystems.simplejson.parser.JSONParser;
import org.jetbrains.annotations.NotNull;

import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


//Service Implementation
@WebService(endpointInterface = "com.thinkit.service.VirtualCoffeeService")
public class VirtualCoffeeServiceImpl implements VirtualCoffeeService {


    public static final int HALF_DAY_HOURS = 12;
    public static final int DAY_HOURS = 24;
    public static final String AM = "am";
    public static final String PM = "pm";
    public static final String GMT = "gmt";

    @Override
    public CofeeMeetingScheduler getGuestsAvailabilityForCoffee(String startTime, String endTime,
                                                                String timeZoneOffset, String nbParticipants) {
        int begin = convertToMilitaryTime(startTime);
        int end = endTime.equalsIgnoreCase("12pm") || endTime.equalsIgnoreCase("0am") ? 24
                : convertToMilitaryTime(endTime);
        int offset = convertToDecimalOffset(timeZoneOffset);

        System.out.println("offset: " + offset);
        System.out.println("timezoneoffset: " + timeZoneOffset);

        HttpURLConnection conn = null;

        CofeeMeetingScheduler cofeeMeetingScheduler = new CofeeMeetingScheduler();

        try {
            URL url = new URL("https://bbmk31v2s7.execute-api.eu-central-1.amazonaws.com/dev/schedule?guests=" +
                    nbParticipants);

            conn = (HttpURLConnection) url.openConnection();

            initConnection(conn);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Error in response code: " + responseCode);
            } else {
                StringBuilder sb = getColleaguesAvailabilityDataSb(conn);

                JSONObject guestsJson = JSONParser.deserializeObject(sb.toString());
                JSONArray guestsJsonArray = guestsJson.getArray("guestList");
                if (guestsJsonArray != null) {

                    List<GuestAvailability> guests = new ArrayList<>();

                    parseJsonDataOfAvailableColleagues(offset, guestsJsonArray, guests);

                    boolean[][] daySchedule = new boolean[guests.size()][DAY_HOURS];

                    initDaySchedule(daySchedule);

                    fillDayScheduleWithGuestsAvailability(guests, daySchedule);

                    List<Integer> guestSlots = new ArrayList<>();

                    setCommonSlots(begin, end, guests, daySchedule, guestSlots);


                    List<String> participantsName = guests.stream().map(GuestAvailability::getGuestName)
                            .collect(Collectors.toList());

                    cofeeMeetingScheduler = new CofeeMeetingScheduler(guestSlots, participantsName);
                    System.out.println(cofeeMeetingScheduler);
                } else {
                    throw new RuntimeException("error getting guest list from remote API");
                }

            }

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return cofeeMeetingScheduler;


    }

    private void initConnection(HttpURLConnection conn) throws ProtocolException {
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestProperty("x-thinkit-custom", "dasee1213d");
    }

    public void setCommonSlots(int begin, int end, List<GuestAvailability> guests, boolean[][] daySchedule, List<Integer> guestSlots) {
        if (!guests.isEmpty()) {
            for (int startHour = begin; startHour < end; startHour++) {
                boolean isSlot = true;
                for (int currentGuest = 0; currentGuest < guests.size(); currentGuest++) {
                    if (!daySchedule[currentGuest][startHour]) {
                        isSlot = false;
                        break;
                    }
                }
                if (isSlot) {
                    guestSlots.add(startHour);
                }
            }
        }
    }

    public void fillDayScheduleWithGuestsAvailability(List<GuestAvailability> guests, boolean[][] daySchedule) {
        for (int currentGuest = 0; currentGuest < guests.size(); currentGuest++) {
            List<int[]> currentGuestAvailabilities = guests.get(currentGuest).getGuestAvailabilities();

            for (int i = 0; i < currentGuestAvailabilities.size(); i++) {
                for (int j = currentGuestAvailabilities.get(i)[0]; j < currentGuestAvailabilities.get(i)[1] &&
                        j < DAY_HOURS; j++) {
                    daySchedule[currentGuest][(j + DAY_HOURS) % DAY_HOURS] = true;
                }
            }
        }
    }

    public void initDaySchedule(boolean[][] daySchedule) {
        for (int i = 0; i < daySchedule.length; i++) {
            for (int j = 0; j < daySchedule[0].length; j++) {
                daySchedule[i][j] = false;
            }
        }
    }

    private void    parseJsonDataOfAvailableColleagues(int offset, JSONArray guestsJsonArray, List<GuestAvailability> guests) {
        for (int i = 0; i < guestsJsonArray.size(); i++) {
            JSONObject guestJsonObject = guestsJsonArray.getObject(i);
            if (guestJsonObject != null) {
                String guestName = guestJsonObject.getString("name");
                String guestOffset = guestJsonObject.getString("offset");
                JSONArray guestAvailabilitiesJsonArray = guestJsonObject.getArray("availability");
                List<int[]> guestAvailabilities = new ArrayList<>();
                if (guestName != null && guestOffset != null && guestAvailabilitiesJsonArray != null) {
                    for (int j = 0; j < guestAvailabilitiesJsonArray.size(); j++) {
                        JSONArray guestAvailabilityJsonArray = guestAvailabilitiesJsonArray.getArray(j);
                        if (guestAvailabilityJsonArray != null && guestAvailabilityJsonArray.size() == 2) {
                            int guestStart = guestAvailabilityJsonArray.getInteger(0);
                            int guestEnd = guestAvailabilityJsonArray.getInteger(1);
                            int offsetConvertedToMyTimeZone = offset - convertToDecimalOffset(guestOffset);
                            int guestSlotEnd;
                            if (guestEnd == 0) {
                                guestEnd = 24;
                                guestSlotEnd = (guestEnd + offsetConvertedToMyTimeZone);
                            }
                            else {
                                guestSlotEnd = (guestEnd + offsetConvertedToMyTimeZone + DAY_HOURS) % DAY_HOURS;
                            }
                            int[] guestSlot = {(guestStart + offsetConvertedToMyTimeZone + DAY_HOURS) % DAY_HOURS,
                                    guestSlotEnd};
                            guestAvailabilities.add(guestSlot);
                        } else if (guestAvailabilityJsonArray == null) {
                            throw new RuntimeException("error parsing JSON data");
                        }
                    }
                    GuestAvailability guest = new GuestAvailability(guestName,guestOffset,guestAvailabilities);
                    System.out.println(guest);
                    guests.add(guest);
                } else {
                    throw new RuntimeException("error parsing JSON data");
                }
            }

        }
    }

    @NotNull
    private StringBuilder getColleaguesAvailabilityDataSb(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder("");
        String line = "";
        while(line != null)
        {
            line = br.readLine();
            if (line != null) {
                sb.append(line);
            }
        }
        System.out.println("\nJSON data in string format");
        System.out.println(sb.toString());
        br.close();
        return sb;
    }

    public int convertToMilitaryTime(String time) {
        time = time.toLowerCase();
        int timeInt = Integer.parseInt(time.replace(AM, "")
                .replace(PM,""));
        if (time.endsWith(PM)) {
            timeInt = (timeInt + HALF_DAY_HOURS) % DAY_HOURS;
        }
        return timeInt;
    }

    public int convertToDecimalOffset(String timezoneOffset) {
        timezoneOffset = timezoneOffset.toLowerCase();
        int timezoneOffsetInt;
        if (timezoneOffset.equals("gmt")) {
            timezoneOffsetInt = 0;
        } else {
            timezoneOffsetInt = Integer.parseInt(timezoneOffset.replace(GMT, "")
                    .replace(GMT,""));
        }

        return timezoneOffsetInt;
    }
}
