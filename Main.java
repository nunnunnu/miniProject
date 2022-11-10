import java.util.Scanner;

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
        System.out.print("1.글 등록, 2.글 목록 3.글 상세조회, 97.로그인, 0.종료 : >> ");
      }else{
        System.out.println("### "+MemberService.loginMember.getNickname()+"님 환영합니다.");
        System.out.print("1.글 등록, 2.글 목록, 3.글 상세조회, 98.회원정보, 99.로그아웃, 0.종료 : >> ");
      }
      int sel = s.nextInt();
      s.nextLine();
      if(sel==0){
        System.out.println("프로그램을 종료합니다.");
        s.close();
        break;
      }else if(sel==97){
        MemberService.login();
      }else if(sel==98){
        MemberService.showMyInfo();
        while(true){
          System.out.print("1.작성 글 보기, 2.작성 댓글 보기, 0.메인화면으로 : ");
          sel=s.nextInt();
          if(sel==0){
            System.out.println("메인화면으로 돌아갑니다.");
            break;
          }else if(sel==1){
            MemberService.showMyPost();
            while(true){
              System.out.print("1.글 수정, 2.글 삭제, 0.취소");
              sel=s.nextInt();
              if(sel==1){
                PostService.modifyPost();
              }else if(sel==2){
                PostService.deletePost();
              }else{
                System.out.println("돌아갑니다.");
                break;
              }
            }
          }else if(sel==2){
            MemberService.showMyCmt();
            System.out.print("1.댓글 수정(안만들었음!!), 2.댓글 삭제, 0.취소");
            sel=s.nextInt();
            if(sel==1){
              System.out.println("아직 지원하지않는 기능입니다.");
              
            }else if(sel==2){
              CommentService.deleteCmd();
            }else if(sel==0){
              System.out.println("돌아갑니다.");
              break;
            }else{
              System.out.println("번호를 잘못 선택하셨습니다");
            }
          }else if(sel==3){
            
          }
        
        }
      }else if(sel==99){
        MemberService.logout();
      }
      
      
      else if(sel==1){
        PostService.createPost();
      }

    }





  }
}
