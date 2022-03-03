package com.example.notesapp;

public class NoteModel {

    private  String noteId,note,addedBy;
    private  long addedOn;

    public NoteModel() {
    }

    public NoteModel(String noteId, String note, String addedBy, long addedOn) {
        this.noteId = noteId;
        this.note = note;
        this.addedBy = addedBy;
        this.addedOn = addedOn;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public long getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(long addedOn) {
        this.addedOn = addedOn;
    }
}
