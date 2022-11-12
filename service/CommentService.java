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

import javax.swing.text.AbstractDocument.Content;

import data.Comment;

public class CommentService {

  public static List<Comment> comments = new ArrayList<Comment>();
  public static Integer commentNo=1;
  public static SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm");
  public static Scanner s = new Scanner(System.in);

  public static void makeDummyCmtData(int n) throws Exception {
    for(int i=0;i<n;i++){
      int r = (int)(Math.random()*PostService.posts.size());
      int r2 = (int)(Math.random()*10);
      PostService.selectedPost = PostService.posts.get(r);
      Date now = new Date();
      Comment c = new Comment("댓글내용입니다."+i, commentNo,r,"user00"+r2,"닉네임"+r2, now,0 );

      BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream(
            new File("comments.dat"),true 
          ), "UTF-8" 
        )
      );
        writer.write(c.makeCmdData()+"\r\n");
        writer.close();
        commentNo++;
      }
      PostService.selectedPost=null;
  }

  public static void loadCmtData() throws Exception {
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(
        new FileInputStream(
          new File("comments.dat")
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
      if(!split[7].equals("null")){
        c.setNestedCmt(Integer.parseInt(split[7]));
      }
      comments.add(c);
    }
    commentNo++;
  }

  public static void deleteCmd() throws Exception {
    if(MemberService.loginMember==null){
      System.out.println("로그인 후 이용해주세요");
      return;
    }else if(PostService.selectedPost==null){
      MemberService.showMyCmt();
      System.out.print("삭제할 댓글 번호를 입력하세요 : ");
      Integer no = s.nextInt();
      s.nextLine();
      int idx=0;
      boolean check = true;
      for(int i=0;i<comments.size();i++){
        if(comments.get(i).getCommentNo()==no){
          idx=i;
          check=false;
          break;
        }
      }
      if(check || comments.get(idx).getStatus()!=0){ //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
        System.out.println("존재하지 않는 댓글입니다.");
      }else{
        System.out.println(comments.get(idx));
        System.out.println("정말로 삭제하시겠습니까?(삭제-y, 취소-아무키나 누르세요)");
        String sel = s.nextLine();
        if(sel.equalsIgnoreCase("y")){
        comments.get(idx).setStatus(2);
        CmtFileCover();
        System.out.println("삭제가 완료되었습니다.");
        }
      }
    }
  }
  public static void CmtFileCover() throws Exception {
    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(
          new File("comments.dat")
        ), "UTF-8" 
      )
    );
    for(Comment c : comments){
      writer.write(c.makeCmdData()+"\r\n");
    }
      writer.close();
  }

  public static void showCmtList() {
    if(PostService.selectedPost==null){ //선택 글이 없으면 모두 조회
      for(Comment c : CommentService.comments){
        if(c.getStatus()==0 && c.getNestedCmt()==null){
          System.out.print(c);
          if(MemberService.loginMember.getStatus()==3){
            System.out.print(" - "+c.getId());
          }
          for(Comment co : CommentService.comments){
            if(c.getNestedCmt()==co.getNestedCmt() && c.getNestedCmt()!=null){
              System.out.println(c);
            }
          }
          System.out.println();
        }
      }
    }
    for(Comment c : CommentService.comments){
      if(c.getPostNo()==PostService.selectedPost.getNo() && c.getStatus()==0){
        if(c.getNestedCmt()==null){
          System.out.println(c+(MemberService.loginMember.getStatus()==3?(" - "+c.getId()):""));
        }
        for(Comment co : CommentService.comments){
          if(c.getCommentNo()==co.getNestedCmt()){
            System.out.println(co+(MemberService.loginMember.getStatus()==3?(" - "+c.getId()):""));
          }
        }
      }
    }
  }
  public static void createCmt() throws Exception {
    if(MemberService.loginMember==null){
      System.out.println("로그인 후 이용해주시길 바랍니다.");
      return;
    }
    System.out.println("==============댓글을 작성합니다.=============");
    Comment c = new Comment(null, commentNo);
    String content;
    while(true){
      int idx =-1;
      System.out.println("답글을 다시려면 해당 댓글의 댓글 번호를 입력하세요, 일반 댓글을 다시려면 -1을 입력하세요. : > ");
      int nested = s.nextInt();
      s.nextLine();
      if(nested!=-1){
        for(int i=0;i<comments.size();i++){
          if(comments.get(i).getCommentNo()==nested && comments.get(i).getStatus()==0){
            idx=i;
            c.setNestedCmt(nested);
          }
        }
        if(comments.get(idx).getNestedCmt()!=null){
          System.out.println("답글의 답글은 현재 지원하지 않습니다.");
          return;
        }
        if(idx==-1){
          System.out.println("해당 댓글이 존재하지 않습니다.");
        }
      }
      System.out.print("댓글 내용 : ");
      content = s.nextLine();
      if(c.setCommentText(content)){
        System.out.println("정말로 댓글을 등록하시겠습니까?(예-Y,아니오-아무키나 입력하세요) : ");
        String confirm = s.nextLine();
        if(confirm.equalsIgnoreCase("y")){
          comments.add(c);
          commentNo++;
          CmtFileCover();
          System.out.println("댓글이 등록되었습니다.");
          return;
        }else{
          System.out.println("댓글 등록이 취소되었습니다.");
        }
      }
    } 
  }
  public static void materCmtBlock() throws Exception {
    if(MemberService.loginMember.getStatus()==3){
      System.out.print("블라인드 할 댓글의 번호를 입력하세요 : ");
      int no = s.nextInt();
      s.nextLine();
      boolean check = true;
      int idx=0;
      for(int i=0;i<comments.size();i++){
        if(comments.get(i).getCommentNo()==no){
          idx=i;
          check=false;
          break;
        }
      }
      if(check || comments.get(idx).getStatus()!=0){ //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
        System.out.println("존재하지 않는 댓글입니다.");
      }else{
        System.out.println(comments.get(idx));
        System.out.println("정말로 블라인드처리 하시겠습니까?(예-Y, 아니오-아무키나 누르세요)");
        String sel = s.nextLine();
        if(sel.equalsIgnoreCase("y")){
        comments.get(idx).setStatus(1);
        CmtFileCover();
        System.out.println("해당 댓글이 블라인드 완료되었습니다.");
        }
      }
    }else{
      System.out.println("해당 기능은 운영자만 사용가능합니다.");
    }
  }
  public static void blockCmtList() {
    if(MemberService.loginMember.getStatus()!=3){
      System.out.println("해당 기능은 운영자만 사용가능합니다.");
      return;
    }
    for(Comment c : comments){
      if(c.getStatus()==1){
        System.out.println(c);
      }
    }
  }
  public static void unBlockCmd() throws Exception {
    System.out.println("블라인드 해제할 댓글의 번호를 입력하세요");
    int sel = s.nextInt();
    s.nextLine();
    int idx=0;
    boolean check = false;
    for(int i=0;i<comments.size();i++){
      if(comments.get(i).getCommentNo()==sel){
        idx=i;
        check=true;
        break;
      }
    }
    if(check){
      System.out.println("정말 해당 게시글의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
      String confirm = s.nextLine();
      if(confirm.equalsIgnoreCase("y")){
        comments.get(idx).setStatus(0);
        CmtFileCover();
        System.out.println("해당 게시글이 블라인드 해제되었습니다.");
      }
    }else{
      System.out.println("해당 댓글이 존재하지 않습니다.");
    }
  }
  public static void modifyCmt() throws Exception {
    while(true){
      System.out.print("수정 할 댓글 번호를 입력하세요. : ");
      int sel = s.nextInt();
      s.nextLine();
      int idx = -1;
      for(int i=0;i<comments.size();i++){
        if(comments.get(i).getCommentNo()==sel && comments.get(i).getStatus()==0){
          idx=i;
          if(!(comments.get(i).getId().equals(MemberService.loginMember.getId()))){
            System.out.println("자신이 작성한 글만 수정할 수 있습니다.");
            return;
          }
        }
      }
      if(idx==-1){
        System.out.println("해당 댓글 번호가 존재하지 않습니다.");
        break;
      }else{
        System.out.print("댓글 내용 : ");
        String content = s.nextLine();
        comments.get(idx).setCommentText(content);
        CmtFileCover();
        System.out.println("댓글이 수정되었습니다.");
        return;
      }
    }
    System.out.println("");

  }
}
