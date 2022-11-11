import java.util.Scanner;

import data.Post;
import service.CommentService;
import service.MemberService;
import service.PostService;

public class Main {
  public static Scanner s = new Scanner(System.in);
  public static void main(String[] args) throws Exception {
    MemberService.loadMemberData();
    PostService.loadPostData();
    // CommentService.makeDummyCmtData(100);
    CommentService.loadCmtData();
    
    while(true){
      System.out.println("======================메뉴를 선택하세요======================");
      if(MemberService.loginMember==null){
        System.out.print("1.글 등록, 2.글 목록, 97.로그인, 0.종료 : >> ");
      }else{
        System.out.println("### "+MemberService.loginMember.getNickname()+"님 환영합니다.");
        System.out.print("1.글 등록, 2.글 목록, 98.회원정보, 99.로그아웃, 0.종료 : >> ");
      }
      int sel = s.nextInt();
      s.nextLine();
      if(sel==0){ //메인메뉴 - 종료
        System.out.println("프로그램을 종료합니다.");
        s.close();
        break;
      }else if(sel==97){ //메인메뉴 - 로그인
        MemberService.login();
      }else if(sel==98){ //메인메뉴 - 회원정보
        MemberService.showMyInfo();
        while(true){
          if(MemberService.loginMember==null){
            break;
          }
          System.out.print("1.작성 글 보기, 2.작성 댓글 보기, 3.회원정보 수정, 4.회원 탈퇴 0.메인화면으로 : ");
          sel=s.nextInt();
          if(sel==0){ //회원정보 - 종료
            System.out.println("메인화면으로 돌아갑니다.");
            break;
          }else if(sel==1){ //회원정보 - 작성 글 보기
            MemberService.showMyPost();
            while(true){
              System.out.print("1.글 수정, 2.글 삭제, 0.취소"); 
              sel=s.nextInt();
              if(sel==1){ //작성 글 보기 - 글 수정
                PostService.modifyPost();
              }else if(sel==2){ //작성 글 보기 - 글 삭제
                PostService.deletePost();
              }else if(sel==0){ //작성 글 보기 - 취소(회원정보로 이동)
                System.out.println("돌아갑니다.");
                break;
              }else{ //작성 글 보기 - 번호 잘못 선택
                System.out.println("번호를 잘못 선택하셨습니다.");
              }
            }// 작성 글 보기 while문 종료
          }else if(sel==2){ //회원 정보 - 작성 댓글 보기
            MemberService.showMyCmt();
            while(true){
              System.out.print("1.댓글 수정(안만들었음), 2.댓글 삭제, 0.취소");
              sel=s.nextInt();
              if(sel==1){ // 작성 댓글 보기 - 댓글 수정(미구현)
                System.out.println("아직 지원하지않는 기능입니다.");
                
              }else if(sel==2){ //작성 댓글 보기 - 댓글 삭제
                CommentService.deleteCmd();
              }else if(sel==0){ //작성 댓글 보기 - 취소(회원정보로 이동)
                System.out.println("돌아갑니다.");
                break;
              }else{ // 작성댓글 - 번호 잘못 선택
              System.out.println("번호를 잘못 선택하셨습니다");
              }
            }// 작성 댓글보기 while문 종료
          }else if(sel==3){
            MemberService.modifyMember();
          }else if(sel==4){
            MemberService.leaveMember();
            if(MemberService.loginMember==null){
              break;
            }
          }else{ //회원정보 - 번호 잘못 입력
            System.out.println("번호를 잘못 선택하셨습니다.");
          }
        }// 회원정보 while문 종료
      }else if(sel==99){ //메인메뉴 - 로그아웃
        MemberService.logout();
      }else if(sel==1){ //메인메뉴 - 글 작성
        PostService.createPost();
      }else if(sel==2){ //메인메뉴 - 글 목록 조회
        while(true){ //글 목록 조회 while문
          boolean check = PostService.selectCate();
          if(check){ //카테고리 선택이 정상적으로 이루어졌을때만 조회가능
            while(true){
              System.out.print("1.글 상세 조회, 0.카테고리 선택으로 : ");
              sel = s.nextInt();
              s.nextLine();
              if(sel==1){ //글 목록 조회 - 글 상세 조회
                PostService.showPostDatail();
                System.out.print("댓글 달기-Y, 나가기-아무키나 누르세요 : ");
                String confirm = s.nextLine();
                if(confirm.equalsIgnoreCase("y")){
                  CommentService.createCmt();
                  break;
                }else{
                  System.out.println("댓글 달기를 취소하셨습니다.");
                  break;
                }
              }else if(sel==0){ //글 목록 조회 - 메인화면으로
                System.out.println("돌아갑니다.");
                break;
              }else{ //글 목록 조회 - 번호 잘못입력
                System.out.println("번호를 잘못 입력하셨습니다.");
              }
            }// 글 상세조회 while문 종료
          }else{ //카테고리 선택 실패시
            break;
          }
        }//글 목록 조회 while문 종료
      }else{
        System.out.println("번호를 잘못 선택하셨습니다.");
      }
    }//메인 while문 종료
  }
}
