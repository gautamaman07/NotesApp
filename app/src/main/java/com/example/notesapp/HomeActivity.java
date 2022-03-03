package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String userId;
    private RecyclerView rvNote;
    private  NoteAdapter adapter;
    private List<NoteModel> noteModelList = new ArrayList<>();
    private  DatabaseReference mDatabaseNotes;
    private ChildEventListener childEventListener;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes App");
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        userId=currentUser.getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        rvNote =findViewById(R.id.rvNote);
        noteModelList= new ArrayList<>();
        adapter =new NoteAdapter( this,noteModelList);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvNote.setLayoutManager(layoutManager);
        rvNote.setAdapter(adapter);







        loadNote();

    }

    private void loadNote()
    {
        mDatabaseNotes = mRootRef.child("notes").child(userId);
        Query noteQuery = mDatabaseNotes.orderByKey();
        noteModelList.clear();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteModel note =dataSnapshot.getValue(NoteModel.class);
                if(!noteModelList.contains(note))
                {
                    noteModelList.add(note);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            NoteModel note = dataSnapshot.getValue(NoteModel.class);
            noteModelList.remove(note);
            adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        noteQuery.addChildEventListener(childEventListener);
    }
public void btnSave(View view)
{

    EditText mEditText= findViewById(R.id.etNote);
    String strNote =mEditText.getText().toString().trim();
    if(strNote.equals(""))
    {
        mEditText.setError("Enter Note");
        return;
    }
    String current_user_ref = "notes/" + userId;
    DatabaseReference note_push = mRootRef.child("notes").child(userId).push();
    String noteId =note_push.getKey();

    Map noteMap =new HashMap();
    noteMap.put("noteId", noteId);
    noteMap.put("note",strNote);
    noteMap.put("addedOn", ServerValue.TIMESTAMP);
    noteMap.put("addedBy",userId);


    Map noteNodeMap = new HashMap();
    noteNodeMap.put(current_user_ref + "/"+ noteId,noteMap);

    mRootRef.updateChildren(noteNodeMap, new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
            if(databaseError!=null)
            {
                Toast.makeText(HomeActivity.this, "Something Went Wrong :" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(HomeActivity.this, "Note Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        }
    });


}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.mnuProfile)
        {
              startActivity(new Intent(this,ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
