package com.thinkit.service;

import com.thinkit.model.CofeeMeetingScheduler;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.net.MalformedURLException;

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.RPC)
public interface VirtualCoffeeService {
    @WebMethod
    CofeeMeetingScheduler getGuestsAvailabilityForCoffee(String startTime, String endTime,
                                                         String timeZoneOffset, String nbParticipants) throws MalformedURLException;
}
