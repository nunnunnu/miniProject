package service;

import java.util.Scanner;

import data.Comment;
import data.Member;
import data.Post;

public class MasterService {

    static Scanner s = new Scanner(System.in);

    public static boolean blockMemberList() {
        boolean check = true;
        for (Member m: MemberService.members) {
            if (m.getStatus() == 1) {
                System.out.println(m);
                check = false;
            }
        }
        if (check) {
            System.out.println("정지된 회원이 없습니다.");
            return false;
        }
        return true;
    }
    public static void unBlockMember() throws Exception {
        if (blockMemberList()) {
            System.out.println("블라인드 해제할 회원의 아이디를 입력하세요");
            String sel = s.nextLine();
            int idx = 0;
            boolean check = false;
            for (int i = 0; i < MemberService.members.size(); i++) {
                if (MemberService.members.get(i).getId().equals(sel)) {
                    idx = i;
                    check = true;
                    break;
                }
            }
            if (check && MemberService.members.get(idx).getStatus() != 1) {
                System.out.println("정지되지않은 회원입니다. 아이디를 확인해주세요");
                return;
            }
            if (check) {
                System.out.println("정말 해당 회원의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
                String confirm = s.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    MemberService.members.get(idx).setStatus(0);
                    MemberService.memberFileCover();
                    System.out.println("해당 회원이 블라인드 해제되었습니다.");
                }
            } else {
                System.out.println("해당 해원이 존재하지 않습니다.");
            }
        }
    }

    public static void materPostBlock(int idx) throws Exception {
        if (MemberService.loginMember.getStatus() == 3) {
            System.out.println("해당 게시글을 블라인드처리 하시겠습니까?(예-Y,아니오-아무키나 누르세요) :");
            String confirm = s.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                PostService.posts.get(idx).setStatus(1);
                PostService.postFileCover();
                System.out.println("게시글이 블라인드 되었습니다.");
            }
        } else {
            System.out.println("해당 메뉴는 운영자만 사용가능합니다.");
        }
    }
    public static boolean blockPostList() {
        boolean check = true;
        if (MemberService.loginMember.getStatus() == 3) {
            for (Post p: PostService.posts) {
                if (p.getStatus() == 1) {
                    System.out.println(p);
                    check = false;
                }
            }
        } else {
            System.out.println("해당 기능은 운영자만 사용 가능합니다.");
            return false;
        }
        if (check) {
            System.out.println("블라인드된 게시글이 없습니다.");
            return false;
        }
        return true;
    }
    public static void unBlockPost() throws Exception {
        if (blockPostList()) {
            System.out.println("블라인드 해제할 게시글의 번호를 입력하세요");
            int sel = s.nextInt();
            s.nextLine();
            int idx = 0;
            boolean check = false;
            for (int i = 0; i < PostService.posts.size(); i++) {
                if (PostService.posts.get(i).getNo() == sel) {
                    idx = i;
                    check = true;
                    break;
                }
            }
            if (check && PostService.posts.get(idx).getStatus() != 1) {
                System.out.println("블라인드되지않은 게시글입니다. 게시글 번호를 확인해주세요");
                return;
            }
            if (check) {
                System.out.println("정말 해당 게시글의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
                String confirm = s.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    PostService.posts.get(idx).setStatus(0);
                    PostService.postFileCover();
                    System.out.println("해당 게시글이 블라인드 해제되었습니다.");
                }
            } else {
                System.out.println("해당 게시글이 존재하지 않습니다.");
            }
        }
    }
    public static void materCmtBlock() throws Exception {
        if (MemberService.loginMember.getStatus() == 3) {
            System.out.print("블라인드 할 댓글의 번호를 입력하세요 : ");
            int no = s.nextInt();
            s.nextLine();
            boolean check = true;
            int idx = 0;
            for (int i = 0; i < CommentService.comments.size(); i++) {
                if (CommentService.comments.get(i).getCommentNo() == no) {
                    idx = i;
                    check = false;
                    break;
                }
            }
            if (check || CommentService.comments.get(idx).getStatus() != 0) { //존재하지 않거나 존재하는데 상태가 삭제or블라인드됐을경우
                System.out.println("존재하지 않는 댓글입니다.");
            } else {
                System.out.println(CommentService.comments.get(idx));
                System.out.println("정말로 블라인드처리 하시겠습니까?(예-Y, 아니오-아무키나 누르세요)");
                String sel = s.nextLine();
                if (sel.equalsIgnoreCase("y")) {
                    CommentService.comments.get(idx).setStatus(1);
                    CommentService.CmtFileCover();
                    System.out.println("해당 댓글이 블라인드 완료되었습니다.");
                }
            }
        } else {
            System.out.println("해당 기능은 운영자만 사용가능합니다.");
        }
    }
    public static boolean blockCmtList() {
        if (MemberService.loginMember.getStatus() != 3) {
            System.out.println("해당 기능은 운영자만 사용가능합니다.");
            return false;
        }
        boolean check = true;
        for (Comment c: CommentService.comments) {
            if (c.getStatus() == 1) {
                check = false;
                System.out.println((c.getNestedCmt() != null ? "(답글)" : "") + c + " " + c.getId());
            }
        }
        if (check) {
            System.out.println("블라인드된 댓글이 없습니다.");
            return false;
        }
        return true;
    }
    public static void unBlockCmd() throws Exception {
        if (blockCmtList()) {
            System.out.println("블라인드 해제할 댓글의 번호를 입력하세요");
            int sel = s.nextInt();
            s.nextLine();
            int idx = 0;
            boolean check = false;
            for (int i = 0; i < CommentService.comments.size(); i++) {
                if (CommentService.comments.get(i).getCommentNo() == sel) {
                    idx = i;
                    check = true;
                    break;
                }
            }
            if (check && CommentService.comments.get(idx).getStatus() != 1) {
                System.out.println("블라인드되지않은 댓글입니다. 댓글 번호를 확인해주세요");
                return;
            }
            if (check) {
                System.out.println("정말 해당 게시글의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
                String confirm = s.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    CommentService.comments.get(idx).setStatus(0);
                    CommentService.CmtFileCover();
                    System.out.println("해당 게시글이 블라인드 해제되었습니다.");
                }
            } else {
                System.out.println("해당 댓글이 존재하지 않습니다.");
            }
        }
    }

}