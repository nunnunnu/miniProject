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

  
  public static void makeDummymemberData(int n) throws Exception { //회원 더미데이터 생성
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
  public static void makeMaster() throws Exception { //운영자 계정 생성

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
  public static void loadMemberData() throws Exception { //회원 파일 로드
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(
        new FileInputStream(
          new File("members.dat")
        ),"UTF-8"
      )
    );
    while(true){
      String line = reader.readLine();
      if(line == null) break; //파일의 끝에 도달하면 while문 종료
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
  public static void addMember() throws Exception { //회원가입
    
    System.out.println("=============회원가입=============");
    String id;
    String pwd;
    String nickname;
    String name;
    String birth;
    String regNo;
    Member m = new Member();
    m.setStatus(0);
    while(true){
      System.out.print("아이디(6자리 이상) : ");
      id = s.nextLine();
      if(m.setId(id)) { //아이디가 제대로 설정되었다면 while문 종료
        break;
      }
    }
    while(true){
      System.out.print("비밀번호(6자리 이상) : ");
      pwd = AES.Encrypt(s.nextLine()); //비밀번호 암호화
      if(m.setPwd(pwd)){ //비밀번호가 제대로 설정되었다면 while문 종료
        break;
      }
    }
    while(true){
      System.out.print("닉네임 : ");
      nickname = s.nextLine();
      if(m.setNickname(nickname)){//닉네임이 제대로 설정되었다면 while문 종료
        break;
      }
    }
    while(true){
      System.out.print("이름 : ");
      name = s.nextLine();
      if(m.setName(name)){ //이름이 제대로 설정되었다면 while문 종료
        break;
      }
    }
    while(true){
      System.out.print("생년월일(6자리로 입력하세요.) : ");
      birth = s.nextLine();
      if(m.setBirth(birth)){//생년월일이 제대로 설정되었다면 while문 종료
        break;
      }
    }
    while(true){
      System.out.print("주민등록번호(-기호없이 13자리) : ");
      regNo = s.nextLine();
      if(m.setRegNo(regNo)) { //주민번호가 제대로 설정되었다면
        for(Member mem : MemberService.members){ //이미 가입된 회원인지 검사
          if(regNo.equals(mem.getRegNo())){ 
            System.out.println("이미 가입된 회원입니다. ");
            return ; //이미 가입된 회원이라면 회원가입 메소드 종료
          }
        }
        members.add(m); //가입된 회원이 아니라면 members list에 추가
        BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(
            new FileOutputStream(
              new File("members.dat"),true 
            ), "UTF-8" 
          )
        ); 
        writer.write(m.makeMemberData()+"\r\n");
        writer.close(); //회원 파일에 새로운 데이터 추가
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
    String pwd = AES.Encrypt(s.nextLine()); //비밀번호 암호화
    for(Member m : members){
      if(id.equals(m.getId()) && pwd.equals(m.getPwd()) && m.getStatus()==1){ //정지된 회원이면 메세지를 표시하고 로그인 제한
        System.out.println("해당 회원은 정지되었습니다.");
        return;
      }else if(id.equals(m.getId()) && pwd.equals(m.getPwd()) && m.getStatus()==2){ //탈퇴한 회원이면 메세지를 표시하고 로그인 제한
        System.out.println("탈퇴 한 회원입니다.");
        return;
      }
      if(id.equals(m.getId()) && pwd.equals(m.getPwd())) {
        loginMember = m; //현재 로그인한 아이디에 설정
        System.out.println("로그인되었습니다");
      }
    }
    if(loginMember==null){ //모든 members를 검사했는데 일치하는 아이디와 비밀번호가 없다면(loginMember가 설정되지 않았다면)
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
    System.out.println("===============회원정보 수정=================");
    System.out.println("비밀번호를 다시 입력해주세요");
    System.out.print("비밀번호 : ");
    pwd = AES.Encrypt(s.nextLine());
    if(pwd.equals(loginMember.getPwd())){ //비밀번호가 일치한다면
      int idx = members.indexOf(loginMember); //로그인 한 회원의 인덱스를 얻어옴
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
            if(members.get(idx).setPwd(pwd)){ //비밀번호가 설정되었을때만 
              memberFileCover(); //파일 덮어쓰기
              System.out.println("비밀번호가 수정되었습니다.");
              break;
            }
          }
        }else if(sel==2){
          while(true){
            System.out.print("닉네임 : ");
            nickname = s.nextLine();
            if(members.get(idx).setNickname(nickname)) { //닉네임이 설정되었을때만
              memberFileCover(); //파일 덮어쓰기
              System.out.println("닉네임이 변경되었습니다.");
              break;
            }
          }
        }else if(sel==3){
          while(true){
            System.out.print("이름 : ");
            name = s.nextLine();
            if(members.get(idx).setName(name)) { //이름이 설정되었을때만
              memberFileCover(); //파일 덮어쓰기
              System.out.println("이름이 수정되었습니다.");
              break;
            }
          }
        }else if(sel==4){
          while(true){
            System.out.print("생년월일(6자리로 입력하세요.) : ");
            birth = s.nextLine();
            if(members.get(idx).setBirth(birth)){ //생년월일이 설정되었을때만
              memberFileCover(); //파일 덮어쓰기
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
  public static void leaveMember() throws Exception { //회원 탈퇴
    if(loginMember==null){
      System.out.println("로그인이 되지않았습니다. 로그인을 먼저 해주세요");
      return;
    }
    System.out.println("정말 탈퇴하겠습니까? 탈퇴하려면 Y, 취소하려면 아무키나 누르세요");
    String sel = s.nextLine();
    if(sel.equalsIgnoreCase("y")){
      int idx = members.indexOf(loginMember); //로그인 한 회원의 인덱스를 얻어옴
      members.get(idx).setStatus(2); // 해당 인덱스의 값을 변경
      memberFileCover(); //파일 덮어쓰기
      MemberService.loginMember=null; //로그인 해제
      System.out.println("회원 탈퇴가 완료되었습니다.");
    }else{
      System.out.println("회원 탈퇴가 취소되었습니다.");
    }
  }
  public static void memberFileCover() throws Exception{ //회원 파일 덮어쓰기 메소드
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
    boolean check = false;
    for(Post p : PostService.posts){
      if(loginMember.getId().equals(p.getId())){ //게시물의 아이디와 로그인 아이디가 같은 경우
        if(p.getStatus()==0){ //상태가 조회가능한 상태인 게시글만 출력
          System.out.println(p);
          check = true;
        }
      }
    }
    if(!check){ //작성한 게시물이 없거나 있어도 조회가능한 상태가 아닐때 메세지 출력
      System.out.println("작성하신 게시글이 없습니다.");
    }
  }
  public static void showMyCmt(){
    if(loginMember==null){
      System.out.println("로그인되지않았습니다. 로그인 먼저 해주세요.");
      return;
    }
    boolean check = false;
    for(Comment c : CommentService.comments){ //댓글의 아이디와 로그인 아이디가 같은 경우
      if(loginMember.getId().equals(c.getId())){//상태가 조회가능한 상태인 댓글만 출력
        if(c.getStatus()==0){
          System.out.println((c.getNestedCmt()!=null?"(답글)":"")+c);  
          check = true;
        }
      }
    }
    if(!check){ //작성한 댓글이 없거나 있어도 조회가능한 상태가 아닐때 메세지 출력
      System.out.println("작성하신 댓글이 없습니다."); 
    }
  }
  public static void showMyInfo() { //로그인한 회원의 회원정보 조회
    if(loginMember==null){
      System.out.println("비회원은 회원정보 기능을 사용할 수 없습니다.");
      return;
    }
    System.out.println("============================회원정보 조회===============================");
    System.out.println(loginMember);
  }
}
