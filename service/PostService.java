package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import data.Comment;
import data.Post;

public class PostService {
  public static Scanner s = new Scanner(System.in);
  public static List<Post> posts = new ArrayList<Post>();
  public static SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd HH:mm");
  public static Integer no = 1;
  public static Post selectedPost = null;

  public static void makeDummyPostData(int n) throws Exception {
    for(int i=0;i<n;i++){
      int r = (int)(Math.random()*Post.cate.length-1)+1;
      Post p = new Post(no, "글 제목입니다"+i, "글 내용입니다."+i, r,"닉네임"+i%10,"user00"+i%10,0);
      posts.add(p);

    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(
          new File("posts.dat"),true 
        ), "UTF-8" 
      )
    );
      writer.write(p.makePostData()+"\r\n");
      writer.close();
      no++;
    }

  }
  public static void loadPostData() throws Exception {
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(
        new FileInputStream(
          new File("posts.dat")
        ),"UTF-8"
      )
    );
    while(true){
      String line = reader.readLine();
      if(line == null) break;
      String[] split = line.split(",");
      String title = split[0];
      String content = split[1];
      String nickname = split[2];
      Date createDate = f.parse(split[3]);
      Integer status = Integer.parseInt(split[5]);
      Integer category = Integer.parseInt(split[6]);
      String id = split[7];
      no = Integer.parseInt(split[8]);
      
      Post p = new Post(no, title, content, category, nickname, id, status);
      p.setCreateDate(createDate);
      if(!(split[4].equals("null"))){
        Date modDate = f.parse(split[4]);
        p.setModDate(modDate);
      }
      p.setStatus(status);
      p.setCategory(category);
      posts.add(p);
    }
    no++;
  }

  public static void createPost() throws Exception {
    if(MemberService.loginMember==null){
      System.out.println("비회원은 게시글을 작성할 수 없습니다.");
      return;
    }
    System.out.println("=============게시글을 작성합니다.=============");
    String title;
    String content;
    Integer category; 
    Post p = new Post(no, null, null, 1);
    while(true){
      if(MemberService.loginMember.getStatus()==3){
        System.out.print("카테고리 : (0.공지, 1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈) : ");
      }else if(MemberService.loginMember.getStatus()==0){
        System.out.print("카테고리 : (1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈) : ");
      }
      category = s.nextInt();
      s.nextLine();
      if(p.setCategory(category)){
        System.out.println(p.getCategory());
        break;
      }
      System.out.println(p.getCategory());
    }
    while(true){
      System.out.print("글 제목 : ");
      title = s.nextLine();
      if(p.setTitle(title)){
        break;
      }
    }
    while(true){
      System.out.print("글 내용 : ");
      content = s.nextLine();
      if(p.setContent(content)){
      break;
      }
    }
    posts.add(p);
    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(
          new File("posts.dat"),true 
        ), "UTF-8" 
      )
    );
      writer.write(p.makePostData()+"\r\n");
      writer.close();
      no++;
  }
  
  public static void modifyPost() throws Exception {
    if(MemberService.loginMember==null){
      System.out.println("아직 로그인하지 않으셨습니다. 로그인 먼저 해주세요");
      return;
    }
    System.out.println("=============게시글 수정==============");
    if(selectedPost==null){
      System.out.println("------------------내가 작성한 글 목록------------------");
      MemberService.showMyPost();
      System.out.println("-----------------------------------------------------");
      System.out.print("수정할 게시글의 번호를 입력하세요");
      Integer no = s.nextInt();
      s.nextLine();
      int idx=0;
      for(int i=0;i<posts.size();i++){
        if(posts.get(i).getNo()==no){
          idx=i;
        }
      }
      selectedPost = posts.get(idx);

    }
    String title;
    String content;
    Integer category; //0.공지, 1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈
    Post p = new Post(no, null, null, 0);
    if(selectedPost.getId().equals(MemberService.loginMember.getId())){
      while(true){
        System.out.println("수정할 정보를 선택하세요 : ");
        System.out.print("1.카테고리, 2.글 제목, 3.글 내용, 0.종료");
        int sel = s.nextInt();
        s.nextLine();
        if(sel==0){
          System.out.println("게시글 수정이 종료되었습니다.");
          return;
        }else if(sel==1){
          while(true){
            System.out.print("카테고리 : (1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈)");
            category = s.nextInt();
            s.nextLine();
            if(p.setCategory(category)){
              selectedPost.setCategory(category);
              selectedPost.setModDate(new Date());
              
              postFileCover();
              System.out.println("카테고리가 수정되었습니다.");
              selectedPost=null;
              break;
            }
          }
        }else if(sel==2){
          while(true){
            System.out.print("글 제목 : ");
            title = s.nextLine();
            if(p.setTitle(title)){
              selectedPost.setTitle(title);
              selectedPost.setModDate(new Date());
              
              postFileCover();
              System.out.println("글 제목이 수정되었습니다.");
              selectedPost=null;
              break;
            }
          }
        }else if(sel==3){
          while(true){
            System.out.print("글 내용 : ");
            content = s.nextLine();
            if(p.setContent(content))
              selectedPost.setContent(content);
              selectedPost.setModDate(new Date());
              
              postFileCover();
              System.out.println("글 제목이 수정되었습니다.");
              selectedPost=null;
              break;
            }
          }
        }
      }else{
        System.out.println("작성한 본인이 아니면 글을 수정할 수 없습니다.");
    }
  }
  public static void postFileCover() throws Exception {
    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(
          new File("posts.dat")
        ), "UTF-8" 
      )
    );
    for(Post p : posts){
      writer.write(p.makePostData()+"\r\n");
    }
      writer.close();
  }

  public static void deletePost() throws Exception {
    if(MemberService.loginMember==null){
      System.out.println("아직 로그인하지 않으셨습니다. 로그인 먼저 해주세요");
      return;
    }
    int idx=0;
    boolean check = false;
    System.out.println("=============게시글 삭제==============");
    if(selectedPost==null){
      System.out.println("------------------내가 작성한 글 목록------------------");
      MemberService.showMyPost();
      System.out.println("-----------------------------------------------------");
      System.out.print("삭제할 게시글의 번호를 입력하세요");
      Integer postNo = s.nextInt();
      for(int i=0;i<posts.size();i++){
        if(posts.get(i).getNo()==postNo){
          idx=i;
          check=true;
          break;
        }
      }
      selectedPost.showDetailInfo(idx);
      selectedPost = posts.get(idx);
    }
    s.nextLine();
    if(check || selectedPost.getStatus()!=0){ //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
      System.out.println("존재하지 않는 게시글입니다.");
    }else if(selectedPost.getId().equals(MemberService.loginMember.getId())){
      System.out.println("정말로 삭제하시겠습니까?(삭제-y, 취소-아무키나 누르세요)");
      String sel = s.nextLine();
      if(sel.equalsIgnoreCase("y")){
        selectedPost.setStatus(2);
        postFileCover();
        System.out.println("삭제가 완료되었습니다.");
        selectedPost=null;
      }
    }else {
      System.out.println("본인이 작성한 글만 삭제할 수 있습니다.");
      selectedPost=null;
      return;
    }
  }
  public static boolean selectCate() { //카테고리 선택
    if(MemberService.loginMember==null){
      System.out.println("비회원은 게시판을 조회할 수 없습니다.");
      return false;
    }
    System.out.println("조회할 게시판을 선택하세요.");
    System.out.print("0.공지, 1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈, 6.전체출력, 7.종료: ");
    int sel = s.nextInt();
    s.nextLine();
    if(sel>=0 && sel<=Post.cate.length){
      showPostList(sel);
      return true;
    }else if(sel==7){
      System.out.println("처음으로 돌아갑니다.");
      return false;
    }else{
      System.out.println("번호를 잘못 입력하셨습니다.");
      return false;
    }
  }
  public static void showPostList(int idx) {
    for(Post p : posts){
      if(idx==6){ //6번이면 전체출력
        if(p.getStatus()==0){
          System.out.println(p);
        }
      }else if(idx<0 || idx>Post.cate.length){ 
        System.out.println("입력값이 잘못되었습니다.");
        return;
      }else{
        if(p.getStatus()==0 && p.getCategory()==idx){
          System.out.println(p);
        }
      }
    }
  }
  public static boolean showPostDatail() throws Exception {
    if(MemberService.loginMember==null){
      System.out.println("비회원은 게시글을 조회할 수 있습니다.");
      return false;
    }
    System.out.print("조회할 게시글의 번호를 입력하세요 : ");
    Integer postNo = s.nextInt();
    s.nextLine();
    int idx=0;
    boolean check = true;
    for(int i=0;i<posts.size();i++){
      if(posts.get(i).getNo()==postNo){
        idx=i;
        check=false;
        break;
      }
    }
    selectedPost = posts.get(idx);
    if(check || posts.get(idx).getStatus()!=0){ //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
      System.out.println("존재하지 않는 게시글입니다.");
    }else{
      posts.get(idx).showDetailInfo(idx);
      CommentService.showCmtList();
      if(MemberService.loginMember.getStatus()==3){
        System.out.println("1.게시글 블라인드, 2.댓글 블라인드, 3.회원 블라인드 0.취소");
        int sel=s.nextInt();
        s.nextLine();
        if(sel==0){
          System.out.println("취소되었습니다"); 
        }else if(sel==1){
          materPostBlock(idx);
        }else if(sel==2){
          CommentService.materCmtBlock();
        }else if(sel==3){
          MemberService.materMemberBlock();
        }else{
          System.out.println("번호 입력이 잘못되었습니다.");
        }
      }
      if(posts.get(idx).getId().equals(MemberService.loginMember.getId())){
        System.out.println("1. 게시글 수정, 2.게시글 삭제, 0.취소 : ");
        int sel = s.nextInt();
        if(sel==0){
          System.out.println("카테고리 선택으로 돌아갑니다.");
          selectedPost=null;
          return false;
        }else if(sel==1){
          modifyPost();
        }else if(sel==2){
          deletePost();
          return true;
        }else{
          System.out.println("번호를 잘못입력하셨습니다.");
        }
      }
    }
    // selectedPost=null;
    return false;
  }
  public static void materPostBlock(int idx) throws Exception {
    if(MemberService.loginMember.getStatus()==3){
      System.out.println("해당 게시글을 블라인드처리 하시겠습니까?(예-Y,아니오-아무키나 누르세요) :");
      String confirm = s.nextLine();
      if(confirm.equalsIgnoreCase("y")){
        posts.get(idx).setStatus(1);
        postFileCover();
        System.out.println("게시글이 블라인드 되었습니다.");
      }
    }else{
      System.out.println("해당 메뉴는 운영자만 사용가능합니다.");
    }
  }
  public static void blockPostList() {
    if(MemberService.loginMember.getStatus()==3){
      for(Post p : posts){
        if(p.getStatus()==1){
          System.out.println(p);
        }
      }
    }else{
      System.out.println("해당 기능은 운영자만 사용 가능합니다.");
    }
  }
  public static void unBlockPost() throws Exception {
    System.out.println("블라인드 해제할 게시글의 번호를 입력하세요");
    int sel = s.nextInt();
    s.nextLine();
    int idx=0;
    boolean check = false;
    for(int i=0;i<posts.size();i++){
      if(posts.get(i).getNo()==sel){
        idx=i;
        check=true;
        break;
      }
    }
    if(check){
      System.out.println("정말 해당 게시글의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
      String confirm = s.nextLine();
      if(confirm.equalsIgnoreCase("y")){
        posts.get(idx).setStatus(0);
        postFileCover();
        System.out.println("해당 게시글이 블라인드 해제되었습니다.");
      }
    }else{
      System.out.println("해당 게시글이 존재하지 않습니다.");
    }
  }
  
}
