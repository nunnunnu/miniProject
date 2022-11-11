package data;

import service.MemberService;
import util.AES;

public class Member{
  private String id;
  private String pwd;
  private String nickname;
  private String name;
  private String birth;
  private Integer status; //0-일반회원, 1-정지회원, 2-탈퇴회원, 3-관리자
  private String regNo;
  //주민번호

  public Member(){}
  public Member(String id, String pwd, String nickname, String name, String birth, String regNo) throws Exception {
    setId(id);
    setPwd(pwd);
    setNickname(nickname);
    setName(name);
    setBirth(birth);
    setStatus(0);
    setRegNo(regNo);
  }

	public String getId() {
    
		return this.id;
	}

	public boolean setId(String id) {
    if(id.length()<6){
      System.out.println("아이디는 6자리 이상으로 등록해주세요.");
      return false;
    }else{
      for(Member m : MemberService.members){
        if(id.equals(m.getId())){
          System.out.println("이미 가입된 아이디입니다. ");
          return false;
        }
      }
      this.id = id;
      return true;
    }
	}
  
	public String getPwd() {
    return this.pwd;
	}
  
	public boolean setPwd(String pwd) throws Exception {
    if(AES.Decrypt(pwd).length()<6){
      System.out.println("비밀번호는 6자리 이상으로 등록해주세요.");
      return false;
    }
    
		this.pwd = pwd;
    return true;
	}

	public String getNickname() {
		return this.nickname;
	}

	public boolean setNickname(String nickname) {
    if(nickname.length()==0){
      System.out.println("닉네임을 입력하지 않았습니다.");
      return false;
    }else {
      for(Member m : MemberService.members){
        if(nickname.equals(m.getNickname())){
          System.out.println("이미 설정된 닉네임입니다. ");
          return false;
        }
      }
      this.nickname = nickname;
      return true;
    }
	}
  
	public String getName() {
    return this.name;
	}
  
	public boolean setName(String name) {
    if(name==""){
      System.out.println("이름을 입력하지 않았습니다.");
      return false;
    }
    this.name = name;
    return true;
	}
  
	public String getBirth() {
    return this.birth;
	}
  
	public boolean setBirth(String birth) {
    if(birth==""){
      System.out.println("생년월일을 입력하지 않았습니다.");
      return false;
    }else if(birth.length()!=6){
      System.out.println("생년월일을 6자리로 입력해주세요.");
      return false;
    }
    this.birth = birth;
    return true;
	}

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getRegNo() {
    return this.regNo;
  }

  public boolean setRegNo(String regNo) {
    if(regNo.length()!=13){
      System.out.println("주민등록번호는 기호없이 13자리로 입력해주세요.");
      return false;
    }else{
      for(Member m : MemberService.members){
        if(regNo.equals(m.getRegNo())){
          System.out.println("이미 가입된 회원입니다. ");
          return false;
        }
      }
      this.regNo = regNo;
      return true;
    }
  }

  public String makeMemberData(){
    return id+","+pwd+","+nickname+","+name+","+birth+","+status+","+regNo;
  }
  @Override
  public String toString() {
    return "아이디 : "+id+" / 닉네임 : "+nickname+" / 이름 : "+name+" / 생년월일 : "+birth;
  }



}