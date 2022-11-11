package data;

import java.text.SimpleDateFormat;
import java.util.Date;

import service.MemberService;
import service.PostService;

public class Comment {
  private Integer postNo;
  private Integer commentNo;
  private String id;
  private String nickname;
  private String commentText;
  private String createDate;
  private Integer status;
  //0.조회가능 1.삭제, 2.블라인드. 
  SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm");

  public Integer getPostNo() {
    return this.postNo;
  }

  public void setPostNo(Integer postNo) {
    this.postNo = postNo;
  }

  public Integer getCommentNo() {
    return this.commentNo;
  }

  public void setCommentNo(Integer commentNo) {
    this.commentNo = commentNo;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getCommentText() {
    return this.commentText;
  }

  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }

  public String getCreateDate() {
    return this.createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = f.format(createDate);
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Comment(){}
  public Comment(String commentText, Integer commentNo){
    setPostNo(PostService.selectedPost.getNo());
    setCommentNo(commentNo);
    setId(MemberService.loginMember.getId());
    setNickname(MemberService.loginMember.getNickname());
    setCommentText(commentText);
    setCreateDate(new Date());
    setStatus(0);
  }
  //더미데이터 생성용 생성자
  public Comment(String commentText, Integer commentNo, Integer postNo, String id, String nickname, Date createDate, Integer status){ 
    setPostNo(postNo);
    setCommentNo(commentNo);
    setId(id);
    setNickname(nickname);
    setCommentText(commentText);
    setCreateDate(createDate);
    setStatus(status);
  }
  public String makeCmdData(){
    return postNo+","+commentNo+","+id+","+nickname+","+commentText+","+createDate+","+status;
  }

  @Override
  public String toString() {
    return nickname+","+commentText+","+createDate+","+"(no."+commentNo+")";
  }
}
