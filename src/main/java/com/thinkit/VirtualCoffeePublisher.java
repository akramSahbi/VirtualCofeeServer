package com.thinkit;

import com.thinkit.service.VirtualCoffeeServiceImpl;

import javax.xml.ws.Endpoint;

//Endpoint publisher
public class VirtualCoffeePublisher {
    private static String HOST = "localhost";
    public static void main(String[] args) {
        String endpointAddress = "http://" + HOST + ":7779/ws/coffee";
        Endpoint.publish(endpointAddress, new VirtualCoffeeServiceImpl());
        System.out.println("Started server endpoint address at " + endpointAddress);
    }
}
