import java.util.InputMismatchException;
import java.util.Scanner;

import service.CommentService;
import service.MasterService;
import service.MemberService;
import service.PostService;

public class Main {
  public static Scanner s = new Scanner(System.in);
  public static void main(String[] args) {
    // MemberService.makeMaster();
    // MemberService.makeDummymemberData(10);

    MemberService.loadMemberData();
    PostService.loadPostData();
    // PostService.makeDummyPostData(90);
    // CommentService.makeDummyCmtData(100);
    CommentService.loadCmtData();
    while (true) {
      showMenu();
      int sel;
      try {
        sel = s.nextInt();
        s.nextLine();
      } catch (InputMismatchException e) {
        System.out.println("숫자를 입력해주세요.");
        s.nextLine();
        continue;
      }
      if (sel == 0) { //메인메뉴 - 종료
        System.out.println("프로그램을 종료합니다.");
        s.close();
        break;
      } else if (sel == 96) {
        MemberService.addMember();
      } else if (sel == 97) { //메인메뉴 - 로그인
        MemberService.login();
      } else if (sel == 98) { //메인메뉴 - 회원정보
        MemberService.showMyInfo();
        while (true) {
          if (MemberService.loginMember == null) { //로그인하지 않았다면 while문 종료
            break;
          }
          System.out.print("1.작성 글 보기, 2.작성 댓글 보기, 3.회원정보 수정, 4.회원 탈퇴 0.메인화면으로 : ");
          try {
            sel = s.nextInt();
            s.nextLine();
          } catch (InputMismatchException e) {
            System.out.println("숫자를 입력해주세요.");
            s.nextLine();
            continue;
          }
          if (sel == 0) { //회원정보 - 종료
            System.out.println("메인화면으로 돌아갑니다.");
            break;
          } else if (sel == 1) { //회원정보 - 작성 글 보기
            if (MemberService.showMyPost()) {
              while (true) {
                System.out.print("1.글 수정, 2.글 삭제, 0.취소 : ");
                try {
                  sel = s.nextInt();
                  s.nextLine();
                } catch (InputMismatchException e) {
                  System.out.println("숫자를 입력해주세요.");
                  s.nextLine();
                  continue;
                }
                if (sel == 1) { //작성 글 보기 - 글 수정
                  PostService.modifyPost();
                  break;
                } else if (sel == 2) { //작성 글 보기 - 글 삭제
                  PostService.deletePost();
                  break;
                } else if (sel == 0) { //작성 글 보기 - 취소(회원정보로 이동)
                  PostService.selectedPost = null;
                  System.out.println("돌아갑니다.");
                  break;
                } else { //작성 글 보기 - 번호 잘못 선택
                  System.out.println("번호를 잘못 선택하셨습니다.");
                }
              } // 작성 글 보기 while문 종료
            }
          } else if (sel == 2) { //회원 정보 - 작성 댓글 보기
            if (MemberService.showMyCmt()) {
              while (true) {
                System.out.print("1.댓글 수정, 2.댓글 삭제, 0.취소 : ");
                int sel2;
                try {
                  sel2 = s.nextInt();
                  s.nextLine();
                } catch (InputMismatchException e) {
                  System.out.println("숫자를 입력해주세요.");
                  s.nextLine();
                  continue;
                }
                if (sel2 == 1) { // 작성 댓글 보기 - 댓글 수정
                  CommentService.modifyCmt();
                  break;
                } else if (sel2 == 2) { //작성 댓글 보기 - 댓글 삭제
                  CommentService.deleteCmd();
                  break;
                } else if (sel2 == 0) { //작성 댓글 보기 - 취소(회원정보로 이동)
                  System.out.println("돌아갑니다.");
                  break;
                } else { // 작성댓글 - 번호 잘못 선택
                  System.out.println("번호를 잘못 선택하셨습니다");
                }
              } // 작성 댓글보기 while문 종료
            } else {
              break;
            }
          } else if (sel == 3) { //회원정보 - 회원정보 수정
            MemberService.modifyMember();
          } else if (sel == 4) { //회원정보 - 회원 탈퇴
            MemberService.leaveMember();
            if (MemberService.loginMember == null) {
              break;
            }
          } else { //회원정보 - 번호 잘못 입력
            System.out.println("번호를 잘못 선택하셨습니다.");
          }
        } // 회원정보 while문 종료
      } else if (sel == 99) { //메인메뉴 - 로그아웃
        MemberService.logout();
      } else if (sel == 1) { //메인메뉴 - 글 작성
        PostService.createPost();
      } else if (sel == 2) { //메인메뉴 - 글 목록 조회
        while (true) { //글 목록 조회 while문
          if (PostService.selectCate()) { //카테고리 선택이 정상적으로 이루어졌을때만 조회가능
            while (true) {
              System.out.print("1.글 상세 조회, 0.카테고리 선택으로 : ");
              try {
                sel = s.nextInt();
                s.nextLine();
              } catch (InputMismatchException e) {
                System.out.println("숫자를 입력해주세요.");
                s.nextLine();
                continue;
              }
              if (sel == 1) { //글 목록 조회 - 글 상세 조회
                if (PostService.showPostDatail()) { //글 상세조회가 성공적으로 이루어졌을때만 실행
                  postMenu();
                }
                PostService.selectedPost = null;
              } else if (sel == 0) { //글 목록 조회 - 메인화면으로
                PostService.selectedPost = null;
                System.out.println("돌아갑니다.");
                break;
              } else { //글 목록 조회 - 번호 잘못입력
                System.out.println("번호를 잘못 입력하셨습니다.");
              }
            } // 글 상세조회 while문 종료
          } else { //카테고리 선택 실패시
            break;
          }
        } //글 목록 조회 while문 종료
      } else if (sel == 4) { //블라인드 관리
        while (true) {
          if (MemberService.loginMember == null || MemberService.loginMember.getStatus() != 3) {
            System.out.println("해당 메뉴는 운영자만 사용가능합니다.");
            break;
          }
          System.out.print("1,블라인드 게시글관리, 2.블라인드 댓글 관리, 3.블라인드 회원관리, 0.메인화면으로 이동 : ");
          try {
            sel = s.nextInt();
            s.nextLine();
          } catch (InputMismatchException e) {
            System.out.println("숫자를 입력해주세요.");
            s.nextLine();
            continue;
          }
          if (sel == 0) { //메인화면으로 이동
            System.out.println("메인화면으로 이동합니다.");
            break;
          } else if (sel == 1) { //블라인드 게시글 관리
            MasterService.unBlockPost();
          } else if (sel == 2) { //블라인드 댓글 관리
            MasterService.unBlockCmd();
          } else if (sel == 3) { //정지회원관리
            MasterService.unBlockMember();
          }
        }
      } else if (sel == 3) { //베스트 게시글 10개 조회
        PostService.bestPostList();
        PostService.showPostDatail();
        if (PostService.selectedPost != null) {
          postMenu();
        }
      } else {
        System.out.println("번호를 잘못 선택하셨습니다.");
      }
    } //메인 while문 종료
  }
  public static void showMenu() { //메인메뉴
    System.out.println("======================메뉴를 선택하세요======================");
    if (MemberService.loginMember == null) { //로그아웃 상태
      System.out.print("96,회원가입, 97.로그인, 0.종료 : >> ");
    } else if (MemberService.loginMember.getStatus() == 3) { //운영자 계정 접속시 메뉴
      System.out.println("### " + MemberService.loginMember.getNickname() + "님 환영합니다.");
      System.out.print("1.글 등록, 2.글 목록, 3.베스트 게시물, 4.블라인드 관리 99.로그아웃, 0.종료 : >> ");
    } else { //일반회원 접속시 메뉴
      System.out.println("### " + MemberService.loginMember.getNickname() + "님 환영합니다.");
      System.out.print("1.글 등록, 2.글 목록, 3.베스트 게시물, 98.회원정보, 99.로그아웃, 0.종료 : >> ");
    }
  }
  public static void postMenu() {
    System.out.print("댓글 달기-Y, 추천하기-L, 나가기-아무키나 누르세요 : ");
    String confirm = s.nextLine();
    if (confirm.equalsIgnoreCase("y")) {
      CommentService.createCmt();
    } else if (confirm.equalsIgnoreCase("L")) {
      PostService.like();
    } else {
      System.out.println("댓글 달기를 취소하셨습니다.");
    }
  }
}