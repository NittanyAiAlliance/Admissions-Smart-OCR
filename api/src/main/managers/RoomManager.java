package main.managers;

import main.types.Room;
import main.types.WebSocketClient;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {

    private Map<String, Room> roomMap = new HashMap<>();

    /**
     * Get a room or create a new one based off the specified entity id
     * @param entity entity id to create
     * @param client connection that needs a room
     * @return Room that the connection was assigned to
     */
    public Room assignRoom(String entity, WebSocketClient client) {
        Room thisRoom = getRoom(entity);
        if(thisRoom == null) {
            //This room does not exist and must be created
            thisRoom = new Room(entity);
            roomMap.put(entity, thisRoom);
            System.out.println("Created a room for entity " + entity);
        }
        //Add the subscriber to the created or found room
        thisRoom.addSubscriber(client);
        return thisRoom;
    }

    /**
     * Get a room by an entity ID
     * @param entity entity id
     * @return Room object matching the entity id
     */
    public Room getRoom(String entity){
        return roomMap.get(entity);
    }

    /**
     * Get which room a user is in from their user identifier
     * @param uid user identifier
     * @return room object the user is in
     */
    public Room getUserRoom(String uid) {
        for(Map.Entry<String, Room> roomEntry : roomMap.entrySet()) {
            Room room = roomEntry.getValue();
            if(room.hasUser(uid)){
                return room;
            }
        }
        return null;
    }
}
