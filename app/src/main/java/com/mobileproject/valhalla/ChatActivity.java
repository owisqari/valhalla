package com.mobileproject.valhalla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.TextMessage;
import com.mobileproject.valhalla.utils.Models.MessageBridge;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    // get the components from the view
    MessagesList chatMessageList;
    MessageInput chatMessageInput;
    TextView chatHeaderText;

    //  a holder for the group id since this activity have dynamic content based on the group id
    String chatGroupID;

    // an adapter that control the message flow on the layout using the chatMessageList component
    MessagesListAdapter<MessageBridge> messagesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // remove top action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Make sure to run your application only in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get the components from the view
        chatMessageInput = findViewById(R.id.chatMessageInput);
        chatMessageList = findViewById(R.id.chatMessageList);
        chatHeaderText = findViewById(R.id.chatHeaderText);

        // get the group id from the intent
        chatGroupID = getIntent().getStringExtra("GROUP_ID");

        // set up the header text of the view
        chatHeaderText.setText(String.format("%s Chat", chatGroupID));

        // fire up the methods to set up the chat page and connect everything together
        setUpChatInputListener();
        setUpChatIncomingListener();
        setAdapterForChatList();
    }

    // using CometChat API, set up a listener for incoming messages
    private void setUpChatIncomingListener() {
        CometChat.addMessageListener(chatGroupID, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                // when the messages arrive, check if they are for the same group we are in, based on the group id
                if (textMessage.getConversationId().contains(chatGroupID)){
                    // if they are, pass in the TextMessage object to add it to the UI
                    addMessageToUI(textMessage);
                }
            }
        });
    }

    // the MessagesList object is a recycler view, so it needs an adapter, the adapter is provided from the ChatKit library
    // we just need to hook it up with the message bridge we've created.
    private void setAdapterForChatList() {
        // get the user ID to identify the sender from the receiver
        String senderID = CometChat.getLoggedInUser().getUid();

        // set an image loader using Picasso library to load the user image in the view
        ImageLoader imageLoader = (imageView, url, payload) -> Picasso.get().load(url).into(imageView);

        // init the adapter using the IS and the image loader
        messagesListAdapter = new MessagesListAdapter<>(senderID, imageLoader);

        // set the adapter to the component
        chatMessageList.setAdapter(messagesListAdapter);
    }

    // set up an input listener to the input field from the ChatKit library to send the messages
    private void setUpChatInputListener() {
        chatMessageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                // when the user submits the message, use the sendMessage method to send the message to the backend by passing the message as a string
                sendMessage(input.toString());
                return true;
            }
        });
    }

    // using CometChat API, send the messages to the backend to store it and fetch it
    private void sendMessage(String message) {
        // convert the string message in the parameter to a TextMessage from CometChat library
        // specifying the group id, the string message and the type of the receiver
        TextMessage textMessage = new TextMessage(chatGroupID, message, CometChatConstants.RECEIVER_TYPE_GROUP);

        // send the meesage
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                // if the send is success, pass in the TextMessage object to add it to the UI
                addMessageToUI(textMessage);
            }

            @Override
            public void onError(CometChatException e) {
                System.err.println(e);
            }
        });
    }

    // this method will receive a CometChat TextMessage object
    // add it the messageListAdapter by converting it to a MessageBridge object and adding it to the start
    // and the adapter will add it to the UI
    private void addMessageToUI(TextMessage textMessage) {
        messagesListAdapter.addToStart(new MessageBridge(textMessage), true);
    }
}