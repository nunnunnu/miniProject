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

public class CommentService {

  public static List<Comment> comments = new ArrayList<Comment>();
  public static Integer commentNo=1;
  public static SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm");
  public static Scanner s = new Scanner(System.in);

  public static void makeDummyCmtData(int n) throws Exception {
    for(int i=0;i<n;i++){
      int r = (int)(Math.random()*PostService.posts.size());
      int r2 = (int)(Math.random()*MemberService.members.size());
      PostService.selectedPost = PostService.posts.get(r);
      System.out.println(r);
      Comment c = new Comment("댓글내용입니다."+i, commentNo);
      c.setId("user00"+r2);
      c.setPostNo(r);
      c.setNickname("닉네임"+r2);
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
        System.out.println(c);
      }
      
    }
    for(Comment c : CommentService.comments){
      if(c.getPostNo()==PostService.selectedPost.getNo()){
        System.out.println(c);
      }
    }
  }
}
