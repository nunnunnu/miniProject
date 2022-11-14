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
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import data.Comment;
import data.Post;

public class CommentService {

  public static List<Comment> comments = new ArrayList<Comment>();
  public static Integer commentNo=1;
  public static SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm");
  public static Scanner s = new Scanner(System.in);

  public static void makeDummyCmtData(int n) { //댓글 더미데이터 생성 메소드
    for(int i=0;i<n;i++){
      int r = (int)(Math.random()*PostService.posts.size()); //댓글 달 게시글 인덱스 랜덤생성
      int r2 = (int)(Math.random()*10); //닉네임 랜덤생성
      PostService.selectedPost = PostService.posts.get(r); //선택 게시글 랜덤으로 지정
      Comment c = new Comment("댓글내용입니다."+i, commentNo,r,"user00"+r2,"닉네임"+r2, new Date(),0 );
      
      cmtFileAdd(c);
      commentNo++;
    }
      PostService.selectedPost=null; //선택 게시글 초기화
  }

  public static void loadCmtData() { //데이터 로드
    try{
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(
          new FileInputStream(
            new File("data_file/comments.dat")
          ),"UTF-8"
        )
      );
      while(true){ 
        String line = reader.readLine();
        if(line == null) break;
        String[] split = line.split(",");
        Integer postNo = Integer.parseInt(split[0]);
        Integer cmtNo = Integer.parseInt(split[1]);
        String id = split[2];
        String nickname = split[3];
        String commentText = split[4];
        Date createDate = f.parse(split[5]);
        Integer status = Integer.parseInt(split[6]);
        commentNo=cmtNo;
        
        Comment c = new Comment(commentText, cmtNo, postNo, id, nickname, createDate, status);
        c.setCreateDate(createDate);
        if(!split[7].equals("null")){ //답댓글을 단 댓글번호가 있을때만 설정, 없으면 통과
          c.setNestedCmt(Integer.parseInt(split[7]));
        }
        comments.add(c);
      }
      commentNo++;
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public static void deleteCmd() { //댓글 삭제
    if(MemberService.loginMember==null){
      System.out.println("로그인 후 이용해주세요");
      return;
    }
    System.out.print("삭제할 댓글 번호를 입력하세요 : "); 
    Integer no;
    while(true){
      try{
        no = s.nextInt();
        s.nextLine();
        break;
      }catch(InputMismatchException e){
        System.out.println("숫자를 입력해주세요.");
        s.nextLine();
      }
    }
    int idx=0;
    boolean check = true;
    for(int i=0;i<comments.size();i++){ //댓글번호와 일치하는 댓글의 인덱스 찾기
      if(comments.get(i).getCommentNo()==no){
        idx=i;
        check=false;
        break;
      }
    }
    if(check || comments.get(idx).getStatus()!=0){ //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
      System.out.println("존재하지 않는 댓글입니다.");
    }else{
      System.out.println(comments.get(idx)); //식제할 댓글 한번 더 표시
      System.out.println("정말로 삭제하시겠습니까?(삭제-y, 취소-아무키나 누르세요)");
      String sel = s.nextLine();
      if(sel.equalsIgnoreCase("y")){
      comments.get(idx).setStatus(2);//해당 댓글의 상태 삭제로 변경
      CmtFileCover(); //파일 덮어쓰기
      System.out.println("삭제가 완료되었습니다.");
      }
    }
  }

  public static void cmtFileAdd(Comment c)  {
    try{
      BufferedWriter writer = new BufferedWriter( //댓글 파일에 추가
      new OutputStreamWriter(
        new FileOutputStream(
          new File("data_file/comments.dat"),true 
        ), "UTF-8" 
      )
    );
      writer.write(c.makeCmdData()+"\r\n");
      writer.close();
    } catch(Exception e){
      e.printStackTrace();
    }
  }
  public static void CmtFileCover() { //댓글 파일 덮어쓰기 메소드
    try{
      BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream(
            new File("data_file/comments.dat")
          ), "UTF-8" 
        )
      );
      for(Comment c : comments){
        writer.write(c.makeCmdData()+"\r\n");
      }
        writer.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public static void showCmtList() { //댓글조회 메소드
    for(Comment c : comments){
      if(c.getNestedCmt()==null && c.getPostNo()==PostService.selectedPost.getNo()){ //선택 게시글 번호와 같고, 답글이 아닌 일반 댓글만 조회
        if(c.getStatus()==0){ //조회 가능 상태일때
          System.out.println(c+(MemberService.loginMember.getStatus()==3?(" - "+c.getId()):"")); //운영자 계정으로 로그인 시 회원아이디 표시
        }else if(c.getStatus()==1){ //블라인드 된 댓글은 메세지 출력
          System.out.println("(블라인드 된 댓글입니다.)");
        }else if(c.getStatus()==2){ //삭제 된 댓글일경우 메세지 출력
          System.out.println("(삭제 된 댓글입니다.)");
        }
        for(Comment co : comments){ 
          if(c.getCommentNo()==co.getNestedCmt()){ //위에서 출력한 댓글에 달린 답 댓글 조회
            if(co.getStatus()==0){ //상태가 조회 가능한 상태인것만
              System.out.println("  ㄴ"+co+(MemberService.loginMember.getStatus()==3?(" - "+c.getId()):"")); //운영자 계정으로 로그인 시 회원아이디 표시
            }else if(co.getStatus()==1){ //블라인드 된 댓글은 메세지 출력
              System.out.println("  ㄴ(블라인드 된 댓글입니다.)");
            }else if(co.getStatus()==2){ //삭제 된 댓글일경우 메세지 출력
              System.out.println("(삭제 된 댓글입니다.)");
            }
          }
        }
      }
    }
  }
  public static void createCmt() { //댓글 달기 메소드
    if(MemberService.loginMember==null){
      System.out.println("로그인 후 이용해주시길 바랍니다.");
      return;
    }
    System.out.println("==============댓글을 작성합니다.=============");
    Comment c = new Comment(null, commentNo); //임시 객체 생성
    String content;
    while(true){
      int idx =-1;
      System.out.println("답글을 다시려면 해당 댓글의 댓글 번호를 입력하세요, 일반 댓글을 다시려면 -1을 입력하세요. : > ");
      int nested;
      while(true){
        try{
          nested = s.nextInt();
          s.nextLine();
          break;
        }catch(InputMismatchException e){
          System.out.println("숫자를 입력해주세요.");
          s.nextLine();
        }
      }
      if(nested!=-1){ //답댓글 작성
        for(int i=0;i<comments.size();i++){
          if(comments.get(i).getCommentNo()==nested && comments.get(i).getStatus()==0){ //답글을 달 댓글이 조회가능상태일때 인덱스 번호를 받아옴 
            idx=i;
            c.setNestedCmt(nested); //임시 객체에 답댓글 번호 설정
          }
        }
        if(comments.get(idx).getNestedCmt()!=null){ //답댓글을 달 댓글이 이미 답글인 상태라면 지원하지않음 메세지 출력
          System.out.println("답글의 답글은 현재 지원하지 않습니다.");
          return;
        }
        if(idx==-1){ //해당 댓글번호의 댓글이 존재하지 않을 때 메세지 출력
          System.out.println("해당 댓글이 존재하지 않습니다.");
        }else{ 
          for(Post p : PostService.posts){ 
            if(p.getNo()==comments.get(idx).getPostNo()){ 
              if(p.getStatus()!=0){ //댓글이 달린 페이지의 상태가 조회할 수 없는 상태라면 메세지 출력(댓글 번호 잘못입력했을때를 대비)
                System.out.println("해당 댓글에는 답댓글을 달 수 없습니다.");
                return;
              }
            }
            
          }
        }
      }
      System.out.print("댓글 내용 : ");
      content = s.nextLine();
      if(c.setCommentText(content)){ //댓글 내용이 정상적으로 설정됐을 경우
        System.out.println("정말로 댓글을 등록하시겠습니까?(예-Y,아니오-아무키나 입력하세요) : ");
        String confirm = s.nextLine();
        if(confirm.equalsIgnoreCase("y")){
          comments.add(c); //임시 객체를 comments list에 등록
          commentNo++; //댓글 번호 증가
          cmtFileAdd(c);
          System.out.println("댓글이 등록되었습니다.");
          return;
        }else{
          System.out.println("댓글 등록이 취소되었습니다.");
          return;
        }
      }
    } 
  }
  public static void modifyCmt() { //댓글 수정
    while(true){
      System.out.print("수정 할 댓글 번호를 입력하세요. : ");
      int sel;
      while(true){
        try{
          sel = s.nextInt();
          s.nextLine();
          break;
        }catch(InputMismatchException e){
          System.out.println("숫자를 입력해주세요.");
          s.nextLine();
        }
      };
      int idx = -1;
      for(int i=0;i<comments.size();i++){
        if(comments.get(i).getCommentNo()==sel && comments.get(i).getStatus()==0){ //수정할 댓글의 상태가 조회가능상태일때, 댓글의 인덱스 번호 받아오기
          if(!(comments.get(i).getId().equals(MemberService.loginMember.getId()))){ //만약 해당 댓글이 자신이 작성한 댓글이 아닐경우 메세지 출력후 메소드 종료
            System.out.println("자신이 작성한 댓글만 수정할 수 있습니다.");
            return;
          }
          idx=i;
        }
      }
      if(idx==-1 || comments.get(idx).getStatus()!=0){ //댓글 번호가 존재하지 않거나 조회가능 상태가 아닐 때
        System.out.println("해당 댓글 번호가 존재하지 않습니다.");
        break;
      }else{
        System.out.print("댓글 내용 : ");
        String content = s.nextLine();
        comments.get(idx).setCommentText(content); //변경한 댓글 내용 적용
        CmtFileCover(); //파일 덮어쓰기
        System.out.println("댓글이 수정되었습니다.");
        return;
      }
    }
  }
}
