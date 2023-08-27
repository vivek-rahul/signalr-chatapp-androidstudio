package com.example.signalrchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.signalrchat.domain.models.ChatMessage;
import com.example.signalrchat.ui.adapter.ChatAdapter;
import com.microsoft.signalr.HubConnection;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SignalR signalR;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private EditText etMessage, etName;
    private Button btnSend;
    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HubConnection hubConnection = signalR.getInstance();
//        String groupName = "hub";

        recyclerView = findViewById(R.id.recycler_view);
        etMessage = findViewById(R.id.et_message);
        etName = findViewById(R.id.et_username);
        btnSend = findViewById(R.id.btn_send);
        btnConnect = findViewById(R.id.btn_connect);

        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnConnect.setOnClickListener(view -> {
            if (hubConnection.getConnectionState() == hubConnection.getConnectionState().DISCONNECTED) {
                hubConnection.start().blockingAwait();
                if (hubConnection.getConnectionState() == hubConnection.getConnectionState().CONNECTED) {
                    showToast("Connected to the hub.");
                } else {
                    showToast("Connection to the hub failed.");
                }
            }
        });

        hubConnection.on("ReceiveMessage", (user, message) -> {
            runOnUiThread(() -> {
                messageList.add(new ChatMessage(user, message));
                chatAdapter.notifyDataSetChanged();
            });
        }, String.class, String.class);

        btnSend.setOnClickListener(view -> {
            String message = etMessage.getText().toString().trim();
            String sender = etName.getText().toString().trim();
            if (!message.isEmpty() && hubConnection.getConnectionState() == hubConnection.getConnectionState().CONNECTED) {
                hubConnection.invoke("SendMessage", sender, message);
                etMessage.getText().clear();

//                messageList.add(new ChatMessage(sender, message));
//                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
