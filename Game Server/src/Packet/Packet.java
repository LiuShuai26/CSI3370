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
    
        public enum pack_type{
        chat_message,
        kick_pack,
        file_pack,
        username
    }
    
    public Packet(String Payload){ // Creates the payload object
        this.payload = payload;
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
