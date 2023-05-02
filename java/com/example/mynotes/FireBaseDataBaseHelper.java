package com.example.mynotes;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireBaseDataBaseHelper {

    private FirebaseDatabase mDatabase;

    private DatabaseReference mReferenceNotes;
    private List<Note> notes= new ArrayList<>();


    public interface DataStatus{
        void dataIsLoaded(List<Note> notes,List<String> keys);
        void dataIsInserted();
        void dataIsDeleted();
        void DataIsUpdated();

    }


    public FireBaseDataBaseHelper()
    {
        mDatabase =FirebaseDatabase.getInstance();
        mReferenceNotes=mDatabase.getReference("notes");

    }

    public void readNotes(final DataStatus dataStatus)
    {
        mReferenceNotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                notes.clear();
                List<String> key= new ArrayList<>();
                for(DataSnapshot keyNode: snapshot.getChildren())
                {

                    key.add(keyNode.getKey());
                    Note note= keyNode.getValue(Note.class);
                    notes.add(note);

                }

                dataStatus.dataIsLoaded(notes,key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
