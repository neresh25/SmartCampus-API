/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus;

import com.mycompany.smartcampus.models.Room;
import com.mycompany.smartcampus.models.Sensor;
import com.mycompany.smartcampus.models.SensorReading;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ranji
 */
public class Database {
    
    private static Map<String, Room> rooms = new HashMap<>();
    private static Map<String, Sensor> sensors = new HashMap<>();
    
    
    private static Map<String, List<SensorReading>> readingsHistory = new HashMap<>();
    static {
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LEC-302", "Main Lecture Hall", 300);
        
        // Add a default sensor so you always have data to test filtering
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 21.5, "LIB-301");
        
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        
        // Save the sensor and link it to the room
        sensors.put(s1.getId(), s1);
        r1.getSensorIds().add(s1.getId());
    }
    public static Map<String, Room> getRooms() {
        return rooms;
    }
    public static Map<String, Sensor> getSensors() {
        return sensors;
    }
    
  
    public static Map<String, List<SensorReading>> getReadingsHistory() {
        return readingsHistory;
    }
}

