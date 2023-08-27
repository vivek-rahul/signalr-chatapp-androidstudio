package com.example.signalrchat;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class SignalR {
    private static HubConnection hubConnection;

    private SignalR() {
        // Private constructor to prevent instantiation
    }

    public static HubConnection getInstance() {
        if (hubConnection == null) {
            hubConnection = createHubConnection();
        }
        return hubConnection;
    }

    private static HubConnection createHubConnection() {
        hubConnection = HubConnectionBuilder.create("http://172.22.80.1:5000/chatHub")
                .build();
        return hubConnection;
    }
}