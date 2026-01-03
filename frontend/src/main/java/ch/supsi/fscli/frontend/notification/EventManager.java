package ch.supsi.fscli.frontend.notification;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {
    private final HashMap<EventType, List<Subscriber>> listeners = new HashMap<>();

    public void subscribe(EventType eventType, Subscriber subscriber) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    public void unsubscribe(EventType eventType, Subscriber subscriber){
        listeners.get(eventType).remove(subscriber);
    }

    public void notify(EventType eventType, String message){
        for (Subscriber subscriber : listeners.get(eventType))
            subscriber.update(message);
    }

    public void notify(EventType eventType){
        notify(eventType, null);
    }

    public void notifyAllSubscribers(){
        for (EventType eventType : EventType.values())
            notify(eventType, null);
    }

    public void notifyAllSubscribers(String message){
        for (EventType eventType : EventType.values())
            notify(eventType, message);
    }
}
