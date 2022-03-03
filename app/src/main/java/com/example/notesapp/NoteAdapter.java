package com.example.notesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    public class NoteHolder extends RecyclerView.ViewHolder{

        TextView tvNote,tvAddedOn;
        ImageView ivDelet;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            tvNote= itemView.findViewById(R.id.tvNote);
            tvAddedOn= itemView.findViewById(R.id.tvAddedOn);
            ivDelet= itemView.findViewById(R.id.ivDelete);

        }
    }
    //to view data in list


    private Context context;
    private List<NoteModel> mNoteList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mUserDatabase;

    public NoteAdapter(Context context, List<NoteModel> mNoteList) {
        this.context = context;
        this.mNoteList = mNoteList;
        this.mAuth= FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_layout, parent,false);
        return new NoteHolder((view));
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteHolder holder, int position) {

        final NoteModel noteModel = mNoteList.get(position);
        holder.tvNote.setText(noteModel.getNote());
        SimpleDateFormat dateFormat =new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String addedOn = dateFormat.format(new Date(noteModel.getAddedOn()));

        holder.tvAddedOn.setText(addedOn);

        //to delete the data


        holder.ivDelet.setTag(noteModel.getNoteId());
        holder.ivDelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userId =currentUser.getUid();
                mRootRef.child("notes").child(userId).child(holder.ivDelet.getTag().toString())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(context, "Note Deleted Successfully", Toast.LENGTH_SHORT).show();
                            mNoteList.remove(noteModel);
                            notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(context, "Failed To Delete data :"
                                    +task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        try {
            return mNoteList.size();
        }
        catch (Exception ex)
        {
            return  0;
        }
    }
}
