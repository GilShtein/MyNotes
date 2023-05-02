package com.example.mynotes;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NoteDetailsActivity extends AppCompatActivity {


    EditText titleEditText,contentEditText,addressText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId,address;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn  = findViewById(R.id.delete_note_text_view_btn);
        addressText = findViewById(R.id.address_text);

        //receive data
        title = getIntent().getStringExtra("title");
        content= getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");
        address = getIntent().getStringExtra("location");

        if(docId!=null && !docId.isEmpty() && address!=null){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        addressText.setText(address);
        if(isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener( (v)-> saveNote());

        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase() );

    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        String noteAddress = addressText.getText().toString();


        double lat =0;
        double lng=0;
        if(noteTitle==null || noteTitle.isEmpty() ){
            titleEditText.setError("Title is required");
            return;
        }
        if(noteAddress.isEmpty())
        {
            titleEditText.setError("address is required");
            return;
        }

        Geocoder geocoder = new Geocoder(NoteDetailsActivity.this) ;
        List<Address> list = new ArrayList<>() ;
        try {
            list = geocoder.getFromLocationName(noteAddress,1);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (list.size()>0) {
            Address n = list.get(0) ;
            Log.d(TAG,"location" +n.toString()) ;
            lat = n.getLatitude() ;
            lng = n.getLongitude() ;

        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        note.setLocation(noteAddress);
        note.setLatitude(lat);
        note.setLonglitude(lng);


        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }



        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is added
                    Utility.showToast(NoteDetailsActivity.this,"Note added successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");
                }
            }
        });

    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");
                }
            }
        });
    }


}