package service;

import java.util.InputMismatchException;
import java.util.Scanner;

import data.Comment;
import data.Member;
import data.Post;

public class MasterService {

    static Scanner s = new Scanner(System.in);

    public static boolean blockMemberList() {
        boolean check = true;
        for (Member m: MemberService.members) {
            if (m.getStatus() == 1) { //정지된 회원의 리스트 출력
                System.out.println(m);
                check = false;
            }
        }
        if (check) { //한번도 출력되지않았다면 메세지 출력
            System.out.println("정지된 회원이 없습니다.");
            return false;
        }
        return true;
    }
    public static void unBlockMember() { 
        if (blockMemberList()) { //정지 회원 리스트가 정상적으로 실행되었을때
            System.out.println("블라인드 해제할 회원의 아이디를 입력하세요"); 
            String sel = s.nextLine();
            int idx = 0;
            boolean check = false;
            for (int i = 0; i < MemberService.members.size(); i++) { //해당 아이디의 멤버 인덱스 번호를 받아옴
                if (MemberService.members.get(i).getId().equals(sel)) {
                    idx = i;
                    check = true;
                    break;
                }
            }
            if (!check || MemberService.members.get(idx).getStatus() != 1) { //존재하지 않거나 정지된 회원이 아닐 경우 메세지 출력
                System.out.println("정지되지않은 회원이거나 존재하지 않는 회원입니다. 아이디를 확인해주세요");
                return;
            }
            if (check) { 
                System.out.println("정말 해당 회원의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
                String confirm = s.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    MemberService.members.get(idx).setStatus(0); //정지 회원의 상태를 일반 회원으로 변경
                    MemberService.memberFileCover(); //파일 덮어쓰기
                    System.out.println("해당 회원이 블라인드 해제되었습니다.");
                }
            }
        }
    }

    public static void materPostBlock(int idx) { //게시글 블라인드
        if (MemberService.loginMember.getStatus() == 3) { //로그인 계정이 운영자 계정일 경우에만 실행
            System.out.println("해당 게시글을 블라인드처리 하시겠습니까?(예-Y,아니오-아무키나 누르세요) :");
            String confirm = s.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                PostService.posts.get(idx).setStatus(1); //게시글의 상태를 블라인드로 수정
                PostService.postFileCover(); //파일 덮어쓰기
                System.out.println("게시글이 블라인드 되었습니다.");
                PostService.selectedPost = null;
            }
        } else {
            System.out.println("해당 메뉴는 운영자만 사용가능합니다.");
        }
    }
    public static boolean blockPostList() { //블라인드 게시글 리스트 출력
        boolean check = true;
        if (MemberService.loginMember.getStatus() == 3) { //로그인 계정이 운영자 계정일때만 실행
            for (Post p: PostService.posts) { 
                if (p.getStatus() == 1) { //상태가 블라인드인것만 출력
                    System.out.println(p);
                    check = false;
                }
            }
        } else {
            System.out.println("해당 기능은 운영자만 사용 가능합니다.");
            return false;
        }
        if (check) { //한번도 출력하지 못했다면 메세지 출력
            System.out.println("블라인드된 게시글이 없습니다.");
            return false;
        }
        return true;
    }
    public static void unBlockPost() {
        if (blockPostList()) { //블라인드 게시글 조회가 정상적으로 이루어졌다면
            System.out.println("블라인드 해제할 게시글의 번호를 입력하세요");
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
            }
            int idx = 0;
            boolean check = false;
            for (int i = 0; i < PostService.posts.size(); i++) {
                if (PostService.posts.get(i).getNo() == sel) { //입력한 게시글번호와 일치하는 게시글의 인덱스 번호 받아오기
                    idx = i;
                    check = true;
                    break;
                }
            }
            if (!check || PostService.posts.get(idx).getStatus() != 1) { //존재하지 않거나 블라인드 상태가 아닐경우 메세지 출력 후 종료
                System.out.println("블라인드되지않은 게시글이거나 존재하지 않는 게시글입니다. 게시글 번호를 확인해주세요");
                return;
            }
            if (check) { 
                System.out.println("정말 해당 게시글의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
                String confirm = s.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    PostService.posts.get(idx).setStatus(0); //일반 상태로 변경
                    PostService.postFileCover(); //파일 덮어쓰기
                    System.out.println("해당 게시글이 블라인드 해제되었습니다.");
                }
            } else {
                System.out.println("해당 게시글이 존재하지 않습니다.");
            }
        }
    }
    public static void materCmtBlock() {
        if (MemberService.loginMember.getStatus() == 3) { //로그인 계정이 운영자일때만
            System.out.print("블라인드 할 댓글의 번호를 입력하세요 : ");
            int no;
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
            boolean check = true;
            int idx = 0;
            for (int i = 0; i < CommentService.comments.size(); i++) { 
                if (CommentService.comments.get(i).getCommentNo() == no) { //입력한 번호의 댓글 인덱스 찾기
                    idx = i;
                    check = false;
                    break;
                }
            }
            if (check || CommentService.comments.get(idx).getStatus() != 0) { //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
                System.out.println("존재하지 않는 댓글입니다.");
            } else {
                System.out.println(CommentService.comments.get(idx)); //블라인드 할 댓글을 출력
                System.out.println("정말로 블라인드처리 하시겠습니까?(예-Y, 아니오-아무키나 누르세요)"); 
                String sel = s.nextLine();
                if (sel.equalsIgnoreCase("y")) {
                    CommentService.comments.get(idx).setStatus(1); //블라인드 상태로 변경
                    CommentService.CmtFileCover(); //파일 덮어쓰기
                    System.out.println("해당 댓글이 블라인드 완료되었습니다.");
                }
            }
        } else {
            System.out.println("해당 기능은 운영자만 사용가능합니다.");
        }
    }
    public static boolean blockCmtList() { //블라인드 댓글 리스트 조회
        if (MemberService.loginMember.getStatus() != 3) {
            System.out.println("해당 기능은 운영자만 사용가능합니다.");
            return false;
        }
        boolean check = true;
        for (Comment c: CommentService.comments) {
            if (c.getStatus() == 1) { //블라인드 상태인 댓글만 조회
                check = false;
                System.out.println((c.getNestedCmt() != null ? "(답글)" : "") + c + " " + c.getId());
            }
        }
        if (check) { //한번도 출력하지 못했다면 메세지 출력후 false반환
            System.out.println("블라인드된 댓글이 없습니다.");
            return false;
        }
        return true;
    }
    public static void unBlockCmd() { //블라인드 댓글 해제
        if (blockCmtList()) {
            System.out.println("블라인드 해제할 댓글의 번호를 입력하세요");
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
            }
            int idx = 0;
            boolean check = false;
            for (int i = 0; i < CommentService.comments.size(); i++) {
                if (CommentService.comments.get(i).getCommentNo() == sel) { //해당 댓글의 인덱스 찾기
                    idx = i;
                    check = true;
                    break;
                }
            }
            if (!check || CommentService.comments.get(idx).getStatus() != 1) { //존재하지 않거나 블라인드 상태가 아닐경우 메세지 출력후 종료
                System.out.println("블라인드되지않은 댓글이거나 존재하지 않는 댓글입니다. 댓글 번호를 확인해주세요");
                return;
            }
            if (check) { 
                System.out.println("정말 해당 게시글의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
                String confirm = s.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    CommentService.comments.get(idx).setStatus(0); //블라인드 상태로 변경
                    CommentService.CmtFileCover(); //파일 덮어쓰기
                    System.out.println("해당 게시글이 블라인드 해제되었습니다.");
                }
            } else {
                System.out.println("해당 댓글이 존재하지 않습니다.");
            }
        }
    }
    public static void materMemberBlock() { //회원 정지 메소드
        if (MemberService.loginMember.getStatus() == 3) { //운영자 계정일때만
            System.out.print("블라인드 할 회원의 아이디를 입력하세요. ");
            String id = s.nextLine();
            int idx = 0;
            for (int i = 0; i < MemberService.members.size(); i++) {
                if (MemberService.members.get(i).getId().equals(id)) { //일치하는 아이디의 인덱스 번호 찾기
                    idx = i;
                    break;
                }
            }
            if(MemberService.members.get(idx).getStatus()==1){
                System.out.println("해당 회원은 이미 정지된 상태입니다.");
                return;
            }else if(MemberService.members.get(idx).getStatus()==2){
                System.out.println("해당 회원은 탈퇴한 회원입니다.");
                return;
            }
            System.out.println("해당 회원을 블라인드처리 하시겠습니까?(예-Y,아니오-아무키나 누르세요) :");
            String confirm = s.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                MemberService.members.get(idx).setStatus(1); //상태 변경
                MemberService.memberFileCover(); //덮어쓰기
                System.out.println("해당 회원을 정지시켰습니다.");
            }
        } else {
            System.out.println("운영자만 사용 가능한 기능입니다.");
        }
    }

}