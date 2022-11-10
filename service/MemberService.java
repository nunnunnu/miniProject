package service;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import data.Comment;
import data.Member;
import data.Post;
import util.AES;


public class MemberService {
  public static List<Member> members = new ArrayList<Member>();
  public static Member loginMember = null;
  public static Scanner s = new Scanner(System.in);

  
  public static void makeDummymemberData(int n) throws Exception {
    for(int i=0;i<n;i++){
      Member m = new Member("user00"+i, AES.Encrypt("pwd000"+i), "닉네임"+i, "이름"+i, "990101");
      members.add(m);

    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(
          new File("members.dat"),true 
        ), "UTF-8" 
      )
    );
      writer.write(m.makeMemberData()+"\r\n");
      writer.close();
    }
  }
  public static void makeMaster() throws Exception {
    Member m = new Member("master1", AES.Encrypt("pwd1234"), "운영자", "박진희", "970707");
    members.add(m);
    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(
          new File("members.dat"),true 
        ), "UTF-8" 
      )
    );
      writer.write(m.makeMemberData()+"\r\n");
      writer.close();
  }
  public static void loadMemberData() throws Exception {
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(
        new FileInputStream(
          new File("members.dat")
        ),"UTF-8"
      )
    );
    while(true){
      String line = reader.readLine();
      if(line == null) break;
      String[] split = line.split(",");
      String id = split[0];
      String pwd = split[1];
      String nickname = split[2];
      String name = split[3];
      String birth = split[4];
      Integer status = Integer.parseInt(split[5]);
      
      Member m = new Member(id, pwd, nickname, name, birth);
      m.setStatus(status);
      members.add(m);
    }
  }
  public static void addMember() throws Exception {
    
    System.out.println("=============회원가입=============");
    String id;
    String pwd;
    String nickname;
    String name;
    String birth;
    boolean check;
    while(true){
      check=true;
      System.out.print("아이디(6자리 이상) : ");
      id = s.nextLine();
      if(id.length()<6){
        check = false;
        System.out.println("아이디는 6자리 이상으로 등록해주세요.");
      }else{
        for(Member m : members){
          if(id.equals(m.getId())){
            System.out.println("이미 가입된 아이디입니다. ");
            check = false;
          }
        }
      }if(check) break;
    }
    while(true){
      System.out.print("비밀번호(6자리 이상) : ");
      pwd = AES.Encrypt(s.nextLine());
      if(pwd.length()<6){
        System.out.println("비밀번호는 6자리 이상으로 등록해주세요.");
      }else break;
    }
    while(true){
      check = true;
      System.out.print("닉네임 : ");
      nickname = s.nextLine();
      if(nickname.length()==0){
        System.out.println("닉네임을 입력하지 않았습니다.");
      }else {
        for(Member m : members){
          if(nickname.equals(m.getNickname())){
            System.out.println("이미 설정된 닉네임입니다. ");
            check = false;
          }
        }
      }if(check) break;
    }
    while(true){
      System.out.print("이름 : ");
      name = s.nextLine();
      if(name==null){
        System.out.println("이름을 입력하지 않았습니다.");
      }else break;
    }
    while(true){
      System.out.print("생년월일(6자리로 입력하세요.) : ");
      birth = s.nextLine();
      if(birth==null){
        System.out.println("생년월일을 입력하지 않았습니다.");
      }else if(birth.length()!=6){
        System.out.println("생년월일을 6자리로 입력해주세요.");
      }else break;
    }
    
    for(Member m : members){
      if(name.equals(m.getName()) && birth.equals(m.getBirth())){
        System.out.println("이미 존재하는 회원입니다");
        return;
      }
    }
    Member m = new Member(id, pwd, nickname, name, birth);
    members.add(m);
    
    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(
          new File("members.dat"),true 
        ), "UTF-8" 
      )
    );
    writer.write(m.makeMemberData()+"\r\n");
    writer.close();
    System.out.println("회원가입이 완료되었습니다.");
    }
  

  public static void login() throws Exception {
    if(loginMember!=null){
      System.out.println("이미 로그인되어있습니다.");
      return;
    }
    System.out.println("============로그인==============");
    System.out.print("아이디 : ");
    String id = s.nextLine();
    System.out.print("비밀번호 : ");
    String pwd = AES.Encrypt(s.nextLine());
    for(Member m : members){
      if(id.equals(m.getId()) && pwd.equals(m.getPwd()) && m.getStatus()==1){
        System.out.println("해당 회원은 정지되었습니다.");
        return;
      }else if(id.equals(m.getId()) && pwd.equals(m.getPwd()) && m.getStatus()==2){
        System.out.println("탈퇴 한 회원입니다.");
        return;
      }
      if(id.equals(m.getId()) && pwd.equals(m.getPwd())) {
        loginMember = m;
        System.out.println("로그인되었습니다");
      }
    }
    if(loginMember==null){
      System.out.println("로그인 실패. 아이디와 비밀번호를 확인해주세요");
    }
  }
  public static void logout() {
    loginMember = null;
    System.out.println("로그아웃되었습니다.");
  }

  public static void modifyMember() throws Exception {
    if(loginMember==null){
      System.out.println("로그인이 되지않았습니다. 로그인을 먼저 해주세요");
      return;
    }
    String pwd;
    String nickname;
    String name;
    String birth;
    boolean check;
    System.out.println("===============회원정보 수정=================");
    int idx = members.indexOf(loginMember); //로그인 한 회원의 인덱스를 얻어옴
    System.out.println("비밀번호를 다시 입력해주세요");
    System.out.print("비밀번호 : ");
    pwd = AES.Encrypt(s.nextLine());
    if(pwd.equals(loginMember.getPwd())){
      while(true){
        System.out.println("수정 할 회원정보를 입력하세요");
        System.out.println("1.비밀번호, 2.닉네임, 3.이름, 4.생년월일, 0.종료");
        int sel = s.nextInt();
        s.nextLine();
        if(sel==0){
          System.out.println("회원 정보 수정을 종료합니다.");
          return;
        }else if(sel==1){
          while(true){
            System.out.print("비밀번호(6자리 이상) : ");
            pwd = AES.Encrypt(s.nextLine());
            if(pwd.length()<6){
              System.out.println("비밀번호는 6자리 이상으로 등록해주세요.");
            }else {
              loginMember.setPwd(pwd);
              System.out.println(loginMember.getPwd());
              members.set(idx, loginMember);

              memberFileCover();
              System.out.println("비밀번호가 수정되었습니다.");
              break;
            }
          }
        }else if(sel==2){
          while(true){
            check = true;
            System.out.print("닉네임 : ");
            nickname = s.nextLine();
            if(nickname.length()==0){
              System.out.println("닉네임을 입력하지 않았습니다.");
            }else {
              for(Member m : members){
                if(nickname.equals(m.getNickname())){
                  System.out.println("이미 설정된 닉네임입니다. ");
                  check = false;
                }
              }
            }
            if(check) {
              loginMember.setNickname(nickname);
              members.set(idx, loginMember);
              
              memberFileCover();
              System.out.println("닉네임이 변경되었습니다.");
              break;
            }
          }
        }else if(sel==3){
          while(true){
            System.out.print("이름 : ");
            name = s.nextLine();
            if(name==null){
              System.out.println("이름을 입력하지 않았습니다.");
            }else {
              loginMember.setName(name);
              members.set(idx, loginMember);

              memberFileCover();
              System.out.println("이름이 수정되었습니다.");
              break;
            }
          }
        }else if(sel==4){
          while(true){
            System.out.print("생년월일(6자리로 입력하세요.) : ");
            birth = s.nextLine();
            if(birth==null){
              System.out.println("생년월일을 입력하지 않았습니다.");
            }else if(birth.length()!=6){
              System.out.println("생년월일을 6자리로 입력해주세요.");
            }else {
              loginMember.setBirth(birth);
              members.set(idx, loginMember);
              
              memberFileCover();
              System.out.println("생년월일이 수정되었습니다.");
              break;
            }
          }
        }else{
          System.out.println("번호를 잘못 입력하셨습니다.");
        }
      }
    }

    
  }
  public static void leaveMember() throws Exception {
    if(loginMember==null){
      System.out.println("로그인이 되지않았습니다. 로그인을 먼저 해주세요");
      return;
    }
    System.out.println("정말 탈퇴하겠습니까? 탈퇴하려면 Y, 취소하려면 아무키나 누르세요");
    String sel = s.nextLine();
    if(sel.equalsIgnoreCase("y")){
      int idx = members.indexOf(loginMember); //로그인 한 회원의 인덱스를 얻어옴
      loginMember.setStatus(2);
      members.set(idx, loginMember);
      memberFileCover();
      System.out.println("회원 탈퇴가 완료되었습니다.");
    }else{
      System.out.println("회원 탈퇴가 취소되었습니다.");
    }
  }
  public static void memberFileCover() throws Exception{
    BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream(
            new File("members.dat") 
          ), "UTF-8" 
        )
      );
      for(Member m : members){
        writer.write(m.makeMemberData()+"\r\n");
      }
      writer.close();
  }
  public static void showMyPost(){
    if(loginMember==null){
      System.out.println("로그인되지않았습니다. 로그인 먼저 해주세요.");
      return;
    }
    for(Post p : PostService.posts){
      if(loginMember.getId().equals(p.getId())){
        if(p.getStatus()==0){
          System.out.println(p);
        }
      }
    }
  }
  public static void showMyCmt(){
    if(loginMember==null){
      System.out.println("로그인되지않았습니다. 로그인 먼저 해주세요.");
      return;
    }
    for(Comment c : CommentService.comments){
      if(loginMember.getId().equals(c.getId())){
        if(c.getStatus()==0){
          System.out.println(c);
        }
      }
    }
  }
  public static void showMyInfo() {
    System.out.println("============================회원정보 조회===============================");
    System.out.println(loginMember);
  }
}
