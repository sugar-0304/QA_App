package jp.techacademy.sugaru.takano.qa_app;

import java.io.Serializable;
import java.util.ArrayList;




public class Favorite implements Serializable {
    private String mTitle;
    private String mBody;
    private String mName;
    //private String mUid;
    //private String mQuestionUid;
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;
    //private int mGenre;

    private String mAnswerSize;



    public Favorite(String title, String body, String name, byte[] bytes,String answerSize){
        mTitle = title;
        mBody = body;
        mName = name;

        mBitmapArray = bytes;
        mAnswerSize = answerSize;

    }





    public String getTitle() {
        return mTitle;
    }

    /*public void setTitle(String title) {
        this.mTitle = title;
    }*/

    public String getBody() {
        return mBody;
    }

    /*public void setBody(String body){
        this.mBody = body;
    }*/

    public String getName() {
        return mName;
    }

    public String getAnswerSize(){
        return mAnswerSize;
    }

    /*public void setName(String name) {
        this.mName = name;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        this.mUid = uid;
    }

    public String getQuestionUid() {
        return mQuestionUid;
    }

    public void setQuestionUid(String questionUid) {
        this.mQuestionUid = questionUid;
    }

    public int getGenre() {
        return mGenre;
    }

    public void setGenre(int genre) {
        this.mGenre = genre;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.mBitmapArray = imageBytes;
    }

    public ArrayList<Answer> getAnswers() {
        return mAnswerArrayList;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.mAnswerArrayList = answers;
    }*/



    public byte[] getImageBytes() {
        return mBitmapArray;
    }
}
