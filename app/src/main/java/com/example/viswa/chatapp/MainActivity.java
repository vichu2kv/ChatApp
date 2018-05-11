package com.example.viswa.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;



public class MainActivity extends AppCompatActivity {
    private static int Sign_In_REQUEST_CODE=1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_main;
    FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_sign_out)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main,"you have been signed out",Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestcode,int resultcode,Intent data)
    {
        super.onActivityResult(requestcode,resultcode,data);
        if(requestcode==Sign_In_REQUEST_CODE)
        {
            if(resultcode==RESULT_OK)
            {
                Snackbar.make(activity_main,"Sucessfully Signed in! Welcome",Snackbar.LENGTH_SHORT).show();
                dispalyChatMessage();
            }
            else
            {
                Snackbar.make(activity_main,"We couldn't sign you in please try again later",Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main=(RelativeLayout)findViewById(R.id.activity_main);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input=(EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });

        //Check if, not signed in then navigate sign in page
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),Sign_In_REQUEST_CODE);
        }
        else
        {
            Snackbar.make(activity_main,"Welcome "+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            dispalyChatMessage();
        }
    }

    private void dispalyChatMessage() {
        ListView listofmessages=(ListView)findViewById(R.id.list_of_messages);
        adapter=new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //get references to the views of list_item.xml
                TextView messageText,messageTime,messageUser;
                messageText=(TextView)v.findViewById(R.id.message_text);
                messageTime=(TextView)v.findViewById(R.id.message_time);
                messageUser=(TextView)v.findViewById(R.id.message_user);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy(HH:mm:ss)",model.getMessageTime()));
            }
        };
        listofmessages.setAdapter(adapter);
    }
}
