package data;
public class Member{
  private String id;
  private String pwd;
  private String nickname;
  private String name;
  private String birth;
  private Integer status; //0-일반회원, 1-정지회원, 2-탈퇴회원, 3-관리자

  public Member(){}
  public Member(String id, String pwd, String nickname, String name, String birth){
    setId(id);
    setPwd(pwd);
    setNickname(nickname);
    setName(name);
    setBirth(birth);
    setStatus(0);
  }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
  
		this.id = id;
	}
  
	public String getPwd() {
    return this.pwd;
	}
  
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
  
	public String getName() {
    return this.name;
	}
  
	public void setName(String name) {
		this.name = name;
	}
  
	public String getBirth() {
    return this.birth;
	}
  
	public void setBirth(String birth) {

    this.birth = birth;
	}

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String makeMemberData(){
    return id+","+pwd+","+nickname+","+name+","+birth+","+status;
  }
  @Override
  public String toString() {
    return "아이디 : "+id+" / 닉네임 : "+nickname+" / 이름 : "+name+" / 생년월일 : "+birth;
  }



}