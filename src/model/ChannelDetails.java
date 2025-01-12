package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelDetails {
    private String channelName;
    private int subscribers;
    private Map<String, String> socialLinks; // Usamos Map para almacenar plataforma -> URL
    private String description;

    // Constructor
    public ChannelDetails(String channelName, String description, int subscribers) {
        this.channelName = channelName;
        this.subscribers = subscribers;
        this.socialLinks = new HashMap<>(); // Usamos un HashMap para enlaces sociales
        this.description = description;
    }

    // Getters
    public String getChannelName() {
        return channelName;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public Map<String, String> getSocialLinks() {
        return socialLinks;
    }

    public String getDescription() {
        return description;
    }

    // Métodos para agregar y quitar enlaces sociales
    public void addSocialLink(String platform, String link) {
        socialLinks.put(platform, link); // Corregido: Usamos 'put' para agregar la red social y el enlace
    }

    public void removeSocialLink(String platform) {
        socialLinks.remove(platform);
    }

    // Método para obtener un enlace social por plataforma
    public String getSocialLink(String platform) {
        return socialLinks.get(platform);
    }

    // Método para verificar si el canal tiene un enlace de una plataforma específica
    public boolean hasSocialLink(String platform) {
        return socialLinks.containsKey(platform);
    }

    // Método para representar el canal como una cadena legible
    @Override
    public String toString() {
        return "Channel: " + channelName + ", Subscribers: " + subscribers + ", Description: " + description;
    }
}
