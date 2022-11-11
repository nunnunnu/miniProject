package data;

import java.text.SimpleDateFormat;
import java.util.Date;

import service.MemberService;

public class Post {
  private Integer no;
  private String title;
  private String content;
  private String nickname; // 현재 로그인된 작성자를 넣어야함
  private String createDate;
  private String modDate;
  private Integer status; // 0-조회가능 글, 1-블라인드 글, 2-삭제 글
  private Integer category; //0.공지, 1.정보, 2.잡담, 3.유머, 4.팁
  public static String[] cate = {"공지","정보","잡담","유머","  팁","이슈"};
  SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd HH:mm");
  private String id;

  public Integer getNo() {
    return this.no;
  }

  public void setNo(Integer no) {
    this.no=no;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return this.title;
  }

  public boolean setTitle(String title) {
    if(title == ""){
      System.out.println("제목이 입력되지 않았습니다.");
      return false;
    }
    this.title = title;
    return true;
  }
  
  public String getContent() {
    return this.content;
  }
  
  public boolean setContent(String content) {
    if(content == ""){
      System.out.println("글 내용이 입력되지 않았습니다.");
      return false;
    }
    this.content = content;
    return true;
  }

  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getCreateDate() {
    return this.createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = f.format(createDate);
  }

  public String getModDate() {
    return this.modDate;
  }

  public void setModDate(Date modDate) {
    this.modDate = f.format(modDate);
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getCategory() {
    return this.category;
  }

  public boolean setCategory(Integer category) {
    if(MemberService.loginMember==null){
      this.category = category;
      return true;
    }else if(MemberService.loginMember.getStatus()==0){
      if(category>= Post.cate.length || category<=0){ //일반회원은 공지 작성 불가능
        System.out.println("번호를 잘못입력하셨습니다??.");
        return false;
      }
    }else if(MemberService.loginMember.getStatus()==3){
      if(category>= Post.cate.length || category<0){ //관리자는 공지 작성 가능
        System.out.println("번호를 잘못입력하셨습니다.");
        return false;
      }
    }
    this.category = category;
    return true;
  }

  public Post(){}
  public Post(Integer no, String title, String content, Integer category){
    setTitle(title);
    setContent(content);
    setCreateDate(new Date());
    setCategory(category);
    setStatus(0);
    setNickname(MemberService.loginMember.getNickname());
    setId(MemberService.loginMember.getId());
    setNo(no);
  }

  //더미데이터용 생성자
  public Post(Integer no, String title, String content, Integer category, String nickname, String id, Integer status){ 
    setTitle(title);
    setContent(content);
    setCreateDate(new Date());
    setCategory(category);
    setStatus(status);
    setNickname(nickname);
    setId(id);
    setNo(no);
  }

	
  public String makePostData(){
    return title+","+content+","+nickname+","+createDate+","+modDate+","+status+","+category+","+id+","+no;
  }

  public void showDetailInfo(int idx) {
    System.out.println("["+cate[category]+"] "+title+" "+createDate+"(no."+no+")");
    System.out.println("작성자 : "+nickname);
    System.out.println("---------------------------------------------------------");
    System.out.println(content);
    System.out.println();
    if(modDate!=null){
      System.out.println("마지막 수정일 : "+modDate);
    }
    if(MemberService.loginMember.getStatus()==3){
      System.out.println("게시글 작성자 아이디 "+id);
    }
    System.out.println("---------------------------------------------------------");

  }

  @Override
  public String toString() {
    return 
      "["+cate[category]+"] "+title+" "+createDate+"(no."+no+")";
  }


}