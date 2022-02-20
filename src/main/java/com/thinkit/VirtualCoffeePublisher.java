package com.thinkit;

import com.thinkit.service.VirtualCoffeeServiceImpl;

import javax.xml.ws.Endpoint;

//Endpoint publisher
public class VirtualCoffeePublisher {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:7779/ws/coffee", new VirtualCoffeeServiceImpl());
    }
}
