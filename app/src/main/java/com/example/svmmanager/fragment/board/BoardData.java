package com.example.svmmanager.fragment.board;

public class BoardData {

    public BoardData() {}

    //ImageView profile;
    private String userid; //게시물 작성자 ID
    private String code; //게시물 기본키
    private String title; //게시물 제목
    private String nickname; //사용자 이름
    private String contents; //내용
    private String date; //날짜
    private String managercomment; //관리자가 대답했는지
    private String answercontents; //관리자 대답
    private String answerdate; //관리자 대답 날짜


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getManagercomment() {
        return managercomment;
    }

    public void setManagercomment(String managercomment) {
        this.managercomment = managercomment;
    }

    public String getAnswerdate() {
        return answerdate;
    }

    public void setAnswerdate(String answerdate) {
        this.answerdate = answerdate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAnswercontents() {
        return answercontents;
    }

    public void setAnswercontents(String answercontents) {
        this.answercontents = answercontents;
    }
}






























