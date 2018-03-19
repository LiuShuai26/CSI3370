package Packet;


import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mike
 */
public class Packet implements Serializable {
    private pack_type type;
    private String payload;
    // Types of packets that will be sent
        public enum pack_type{
        chat_message, // A chat message
        kick_pack, // A kick user packet
        file_pack, // Packet is a file
        connected, // connected
        disconnected, // disconnected
        whisper, // private message
        adminMessage, // Message from administrator
        ban_pack, // Ban pack
    }
    
    public Packet(String payload, pack_type type){ // Creates the payload object
        this.payload = payload;
        this.type = type;
    }
    public void setPackType(pack_type type){
        this.type = type;
    }
    public pack_type getPackType(){
        return this.type;
    }
    public void setPayload(String text){
        payload = text;
    }
    public String getPayload(){
        return payload;
    }
}
