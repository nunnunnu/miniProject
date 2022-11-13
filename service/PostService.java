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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import data.Post;

public class PostService {
  public static Scanner s = new Scanner(System.in);
  public static List<Post> posts = new ArrayList<Post>();
  public static SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd HH:mm");
  public static Integer no = 1;
  public static Post selectedPost = null;

  public static void makeDummyPostData(int n) throws Exception { //회원 더미데이터 생성 메소드. 매개변수로 넣은 수만큼 만들어짐
    for(int i=0;i<n;i++){
      int r = (int)(Math.random()*Post.cate.length-1)+1;
      int ranview = (int)(Math.random()*150);
      Post p = new Post(no, "글 제목입니다"+i, "글 내용입니다."+i, r,"닉네임"+i%10,"user00"+i%10,0);
      p.setView(ranview);
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
  public static void loadPostData() throws Exception { //회원 데이터 로드를 위한 메소드
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
      Integer view = Integer.parseInt(split[9]);
      
      Post p = new Post(no, title, content, category, nickname, id, status);
      p.setCreateDate(createDate);
      if(!(split[4].equals("null"))){
        Date modDate = f.parse(split[4]);
        p.setModDate(modDate);
      }
      p.setStatus(status);
      p.setCategory(category);
      p.setView(view);
      posts.add(p);
    }
    no++;
  }

  public static void createPost() throws Exception { //게시글 작성 메소드
    if(MemberService.loginMember==null){
      System.out.println("비회원은 게시글을 작성할 수 없습니다.");
      return;
    }
    System.out.println("=============게시글을 작성합니다.=============");
    String title;
    String content;
    Integer category; 
    Post p = new Post(no, null, null, null);
    while(true){
      if(MemberService.loginMember.getStatus()==3){ //운영자 메뉴(공지 작성 가능)
        System.out.print("카테고리 : (0.공지, 1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈) : ");
      }else if(MemberService.loginMember.getStatus()==0){ //일반회원 메뉴(공지 작성 불가능)
        System.out.print("카테고리 : (1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈) : ");
      }
      category = s.nextInt();
      s.nextLine();
      if(p.setCategory(category)){ //카테고리가 제대로 들어갔다면 while문 종료
        break;
      }
    }
    while(true){
      System.out.print("글 제목 : ");
      title = s.nextLine();
      if(p.setTitle(title)){ //제목이 제대로 설정되었으면 while문 종료
        break;
      }
    }
    while(true){
      System.out.print("글 내용 : ");
      content = s.nextLine();
      if(p.setContent(content)){ //글 내용이 제대로 설정 되었으면 while문 종료
      break;
      }
    }
    p.setView(0); //조회수 0으로 시작
    posts.add(p); //posts list에 추가
    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(
          new File("posts.dat"),true 
        ), "UTF-8" 
      )
    );
      writer.write(p.makePostData()+"\r\n");
      writer.close();  //파일에 데이터 추가
      no++; //글 번호 1 추가
  }
  
  public static void modifyPost() throws Exception { //글 수정 메소드
    if(MemberService.loginMember==null){
      System.out.println("아직 로그인하지 않으셨습니다. 로그인 먼저 해주세요");
      return;
    }
    System.out.println("=============게시글 수정==============");
    if(selectedPost==null){ //수정 할 게시글이 선택되지 않았다면 선택 먼저
      System.out.print("수정 할 게시글 번호를 입력하세요 : ");
      Integer no = s.nextInt();
      s.nextLine();
      int idx=-1;
      for(int i=0;i<posts.size();i++){ //입력받은 게시글 번호와 일치하는 인덱스번호 찾기
        if(posts.get(i).getNo()==no){
          idx=i;
        }
      }
      selectedPost = posts.get(idx); //수정 할 게시글 설정(주소값 받아옴. 수정 시 posts도 같이 수정됨)
      if(idx!=-1 || selectedPost.getStatus()!=0){ //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
        System.out.println("존재하지 않는 게시글입니다.");
        return;
      }
    }
    String title;
    String content;
    Integer category; //0.공지, 1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈
    Post p = new Post(no, null, null, null); //임시 객체 생성
    if(selectedPost.getId().equals(MemberService.loginMember.getId())){ //수정 할 게시글의 작성자 아이디와 로그인한 아이디가 같은 경우만 수정가능
      while(true){
        System.out.println("수정할 정보를 선택하세요");
        System.out.print("1.카테고리, 2.글 제목, 3.글 내용, 0.종료 : ");
        int sel = s.nextInt();
        s.nextLine();
        if(sel==0){
          selectedPost=null;
          System.out.println("게시글 수정이 종료되었습니다.");
          return;
        }else if(sel==1){
          while(true){
            System.out.print("카테고리 : (1.정보, 2.잡담, 3.유머, 4.팁, 5.이슈)");
            category = s.nextInt();
            s.nextLine();
            if(p.setCategory(category)){ //카테고리가 제대로 설정되었다면
              selectedPost.setCategory(category); //선택 게시물 카테고리 수정
              selectedPost.setModDate(new Date()); //선택 게시물 수정일 수정
              
              postFileCover(); //post게시물 덮어쓰기
              System.out.println("카테고리가 수정되었습니다.");
              break; 
            }
          }
        }else if(sel==2){
          while(true){
            System.out.print("글 제목 : ");
            title = s.nextLine();
            if(p.setTitle(title)){ //글제목이 제대로 설정되었다면
              selectedPost.setTitle(title); //선택 게시글 수정
              selectedPost.setModDate(new Date()); //선택 게시글 수정일자 설정
              
              postFileCover(); //파일 덮어쓰기
              System.out.println("글 제목이 수정되었습니다.");
              break;
            }
          }
        }else if(sel==3){
          while(true){
            System.out.print("글 내용 : ");
            content = s.nextLine();
            if(p.setContent(content)) //글 내용이 제대로 설정되었다면
              selectedPost.setContent(content); //선택 게시물 수정
              selectedPost.setModDate(new Date()); //선택 게시물 수정일자 변경
              
              postFileCover(); //파일 덮어쓰기
              System.out.println("글 제목이 수정되었습니다.");
              break;
            }
          }
        }
      }else{ //게시글의 아이디와 로그인한 아이디가 다르다면
        System.out.println("작성한 본인이 아니면 글을 수정할 수 없습니다.");
    }
  }
  public static void postFileCover() throws Exception { //파일 덮어쓰기
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

  public static void deletePost() throws Exception { //게시글 삭제
    if(MemberService.loginMember==null){
      System.out.println("아직 로그인하지 않으셨습니다. 로그인 먼저 해주세요");
      return;
    }
    int idx=-1;
    System.out.println("=============게시글 삭제==============");
    if(selectedPost==null){
      System.out.print("삭제할 게시글의 번호를 입력하세요 : ");
      Integer postNo = s.nextInt();
      for(int i=0;i<posts.size();i++){ //입력한 게시글 번호의 인덱스를 찾음
        if(posts.get(i).getNo()==postNo){
          idx=i;
          selectedPost = posts.get(i); //선택 게시물에 posts의 주소값을 받아와 선택게시물이 수정되면 posts도 함께 수정됨
          break;
        }
      }
      selectedPost.showDetailInfo(idx);
    }
    s.nextLine();
    if(idx!=-1 || selectedPost.getStatus()!=0){ //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
      System.out.println("존재하지 않는 게시글입니다.");
    }else if(selectedPost.getId().equals(MemberService.loginMember.getId())){ //게시글의 아이디와 로그인 아이디가 같다면
      System.out.println("정말로 삭제하시겠습니까?(삭제-y, 취소-아무키나 누르세요)");
      String sel = s.nextLine();
      if(sel.equalsIgnoreCase("y")){
        selectedPost.setStatus(2); //선택 게시물의 상태를 삭제 상태(2)로 변경
        postFileCover(); //파일 덮어쓰기
        System.out.println("삭제가 완료되었습니다.");
      }
    }else { //게시글의 아이디와 로그인 아이디가 다를경우
      System.out.println("본인이 작성한 글만 삭제할 수 있습니다.");
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
    if(sel==7){ //종료
      System.out.println("처음으로 돌아갑니다.");
      return false;
    }else if(sel==6){  //6번이면 전체출력
      for(Post p : posts){
        if(p.getStatus()==0){ //일반 게시글일경우, 출력 
          System.out.println(p);
        }else if(p.getStatus()==1){ //블라인드 된 게시물일 경우 메세지 표시, 삭제게시글은 표시안함
          System.out.println("(블라인드된 게시글입니다.)");
        }
      }
    }else if(sel<0 || sel>Post.cate.length){ //입력값이 잘못되었을경우
        System.out.println("입력값이 잘못되었습니다.");
        return false;
    }else{
      for(Post p : posts){
        if(sel==p.getCategory()){ //입력 카테고리와 같은 것만 검사
          if(p.getStatus()==0){ //일반 게시글일경우, 출력 
            System.out.println(p);
          }else if(p.getStatus()==1){ //블라인드 된 게시물일 경우 메세지 표시, 삭제게시글은 표시안함
            System.out.println("(블라인드된 게시글입니다.)");
          }
        }
      }
    }
    return true; //중간에 return되지 않고 게시글 리스트가 출력되었다면 true 반환
  }

  public static boolean showPostDatail() throws Exception { //게시글 상세 조회
    if(MemberService.loginMember==null){
      System.out.println("비회원은 게시글을 조회할 수 없습니다.");
      return false;
    }
    System.out.print("조회할 게시글의 번호를 입력하세요 : ");
    Integer postNo = s.nextInt();
    s.nextLine();
    int idx=0;
    boolean check = true;
    for(int i=0;i<posts.size();i++){ //입력 게시글번호와 같은 인덱스를 찾음
      if(posts.get(i).getNo()==postNo){
        idx=i;
        check=false;
        selectedPost=posts.get(i); //선택 게시글에 해당 인덱스의 주소값을 복사
        break;
      }
    }
    if(check || posts.get(idx).getStatus()!=0){ //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
      System.out.println("존재하지 않는 게시글입니다.");
      return false;
    }else{
      posts.get(idx).showDetailInfo(idx); //Post 클래스의 메소드 실행 (조회수 1 올라감) 
      postFileCover();  //1 올라간 조회수 반영을 위해 파일 덮어쓰기
      CommentService.showCmtList(); //해당 게시글의 댓글 조회
      if(MemberService.loginMember.getStatus()==3){ //운영자일때 추가로 보여질 블라인드메뉴
        System.out.println("1.게시글 블라인드, 2.댓글 블라인드, 3.회원 블라인드 0.취소");
        int sel=s.nextInt();
        s.nextLine();
        if(sel==0){
          System.out.println("취소되었습니다"); 
        }else if(sel==1){
          PostService.selectedPost=null;
          MasterService.materPostBlock(idx);
        }else if(sel==2){
          MasterService.materCmtBlock();
        }else if(sel==3){
          MasterService.materMemberBlock();
        }else{
          System.out.println("번호 입력이 잘못되었습니다.");
        }
      }
      if(posts.get(idx).getId().equals(MemberService.loginMember.getId())){ //본인 작성 게시글일때 보여줄 게시글 수정, 삭제 메뉴
        System.out.print("1. 게시글 수정, 2.게시글 삭제, 0.취소 : ");
        int sel = s.nextInt();
        if(sel==0){
          System.out.println("취소.");
          selectedPost=null;
          return false;
        }else if(sel==1){
          modifyPost();
        }else if(sel==2){
          deletePost(); 
          selectedPost=null; //삭제됐을때 댓글 달기 메뉴를 보이지 않기 위해 선택 게시글 초기화해줌
          return true;
        }else{
          System.out.println("번호를 잘못입력하셨습니다.");
        }
      }
    }
    return false;
  }
  public static void bestPostList() { //조회수 상위 10개를 조회하는 메소드
    List<Post> temp = new ArrayList<Post>(posts); //posts를 temp에 복사
    
    //클래스 내의 조회수로 정렬을 하기 위해 Comparator 인터페이스를 사용(복사한 배열 내림차순 정렬)
    Comparator<Post> bestPost = new Comparator<Post>() { 
      @Override
      public int compare(Post p1, Post p2){ //Comparator의 compare메소드를 실행/ p1와 p2의 값을 비교(0:같음, 양수:왼쪽이큼, 음수:오른쪽이 큼)
        //정렬 기준을 결정하는 메소드임
        int a = p1.getView();
        int b =p2.getView();
        if(a<b){ //내림차순 비교를 위해 오른쪽 값이 클때 자리 바꿈 실행
          return 1; 
        }else{
          return-1;
        }
      }
    };
    Collections.sort(temp, bestPost); //temp를 bestPost에서 설정한 정렬기준으로 정렬을 함 
    //view를 기준으로 내림차순 정렬을 실행
    System.out.println("====================베스트 게시물=====================");
    for(int i=0;i<10;i++){ //10개만 출력
      if(temp.get(i).getStatus()==1){ //블라인드된 글은 메세지 표시
        System.out.println("(블라인드 된 게시글입니다.)");
        continue;
      }else if(temp.get(i).getStatus()==2){ //삭제된 글은 메세지 표시
        System.out.println("(삭제 된 게시글입니다.)");
        continue;
      }
      System.out.println(temp.get(i)); //둘다 아니라면 게시물 출력
    }
  }
}
