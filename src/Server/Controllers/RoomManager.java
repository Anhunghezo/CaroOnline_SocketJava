/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Controllers;

import java.util.ArrayList;

/**
 *
 * @author Hoang Tran < hoang at 99.hoangtran@gmail.com >
 */
public class RoomManager {

    ArrayList<Room> rooms;

    public RoomManager() {
        rooms = new ArrayList<>();
    }

    public boolean add(Room r) {
        if (!rooms.contains(r)) {
            rooms.add(r);
            return true;
        }
        return true;
    }
    
    public Room get(String roomID){
        for(Room r : rooms){
            if(r.getId().equalsIgnoreCase(roomID))
                return r;
        }
        return null;
    }

    public boolean remove(Room r) {
        if (rooms.contains(r)) {
            rooms.remove(r);
            return true;
        }
        return false;
    }

    public Room find(String id) {
        for (Room r : rooms) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }
    
    public boolean update(Room room){
        for (Room r : rooms) {
            if (r.getId().equals(room.getId())) {
                r = room;
                return true;
            }
        }
        return false;
    }
    
    public int getSize() {
        return rooms.size();
    }

}
