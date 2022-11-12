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
      int r = (int)(Math.random()*199999)+1000000;//주민 뒷자리 랜덤 값
      String birth = Integer.toString((int)(Math.random()*199999)+800000); 
      String regNo = birth+r;
      
      
      Member m = new Member("user00"+i, AES.Encrypt("pwd000"+i), "닉네임"+i, "이름"+i, birth, regNo);
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

    Member m = new Member("master1", AES.Encrypt("pwd1234"), "운영자", "박진희", "970707","9707072000000");
    m.setStatus(3);
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
      String regNo = split[6];
      
      Member m = new Member(id, pwd, nickname, name, birth, regNo);
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
    String regNo;
    Member m = new Member();
    m.setStatus(0);
    while(true){
      System.out.print("아이디(6자리 이상) : ");
      id = s.nextLine();
      check = m.setId(id);
      if(check) {
        break;
      }
    }
    while(true){
      System.out.print("비밀번호(6자리 이상) : ");
      pwd = AES.Encrypt(s.nextLine());
      check = m.setPwd(pwd);
      if(check){
        break;
      }
    }
    while(true){
      System.out.print("닉네임 : ");
      nickname = s.nextLine();
      check = m.setNickname(nickname);
      if(check){
        break;
      }
    }
    while(true){
      System.out.print("이름 : ");
      name = s.nextLine();
      check = m.setName(name);
      if(check){
        break;
      }
    }
    while(true){
      System.out.print("생년월일(6자리로 입력하세요.) : ");
      birth = s.nextLine();
      check = m.setBirth(birth);
      if(check){
        break;
      }
    }
    while(true){
      System.out.print("주민등록번호(-기호없이 13자리) : ");
      regNo = s.nextLine();
      check = m.setRegNo(regNo);
      if(check) {
        for(Member mem : MemberService.members){
          if(regNo.equals(mem.getRegNo())){
            System.out.println("이미 가입된 회원입니다. ");
            return ;
          }
        }
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
        break;
      }
    }
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
    if(loginMember==null){
      System.out.println("로그인이 되지않아 로그아웃을 실행할 수 없습니다.");
      return;
    }
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
    Member m = new Member();
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
            check =  loginMember.setPwd(pwd);
            if(check){
              members.set(idx, loginMember);
              memberFileCover();
              System.out.println("비밀번호가 수정되었습니다.");
              break;
            }
          }
        }else if(sel==2){
          while(true){
            System.out.print("닉네임 : ");
            nickname = s.nextLine();
            check = loginMember.setNickname(nickname);
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
            check = loginMember.setName(name);
            if(check) {
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
            check = loginMember.setBirth(birth);
            if(check){
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
    }else{
      System.out.println("비밀번호를 잘못입력하셨습니다.");
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
      System.out.println(members.get(idx).getStatus());
      memberFileCover();
      MemberService.loginMember=null;
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
          System.out.println((c.getNestedCmt()!=null?"(답글)":"")+c);
        }
      }
    }
  }
  public static void showMyInfo() {
    if(loginMember==null){
      System.out.println("비회원은 회원정보 기능을 사용할 수 없습니다.");
      return;
    }
    System.out.println("============================회원정보 조회===============================");
    System.out.println(loginMember);
  }
  public static void materMemberBlock() throws Exception {
    if(MemberService.loginMember.getStatus()==3){
      System.out.print("블라인드 할 회원의 아이디를 입력하세요. ");
      String id = s.nextLine();
      int idx = 0;
      for(int i=0;i<members.size();i++){
        if(members.get(i).getId().equals(id)){
          idx = i;
          break;
        }
      }
      System.out.println("해당 회원을 블라인드처리 하시겠습니까?(예-Y,아니오-아무키나 누르세요) :");
      String confirm = s.nextLine();
      if(confirm.equalsIgnoreCase("y")){
        members.get(idx).setStatus(1);
        memberFileCover();
        System.out.println("해당 회원을 정지시켰습니다.");
      }
    }else{
      System.out.println("운영자만 사용 가능한 기능입니다.");
    }
  }

  public static boolean blockMemberList() {
    boolean check = true;
    for(Member m : members){
      if(m.getStatus()==1){
        System.out.println(m);
        check = false;
      }
    }
    if(check){
      System.out.println("정지된 회원이 없습니다.");
      return false;
    }
    return true;
  }
  public static void unBlockMember() throws Exception {
    if(blockMemberList()){
      System.out.println("블라인드 해제할 회원의 아이디를 입력하세요");
      String sel = s.nextLine();
      int idx=0;
      boolean check = false;
      for(int i=0;i<members.size();i++){
        if(members.get(i).getId().equals(sel)){
          idx=i;
          check=true;
          break;
        }
      }
      if(check && members.get(idx).getStatus()!=1){
        System.out.println("정지되지않은 회원입니다. 아이디를 확인해주세요");
        return;
      }
      if(check){
        System.out.println("정말 해당 회원의 블라인드를 해제하시겠습니까? (예-Y,아니오-아무키나 누르세요) :");
        String confirm = s.nextLine();
        if(confirm.equalsIgnoreCase("y")){
          members.get(idx).setStatus(0);
          memberFileCover();
          System.out.println("해당 회원이 블라인드 해제되었습니다.");
        }
      }else{
        System.out.println("해당 해원이 존재하지 않습니다.");
      }
    }
  }
}
