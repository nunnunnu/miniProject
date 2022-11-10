import service.CommentService;
import service.MemberService;
import service.PostService;

public class Main {
  public static void main(String[] args) throws Exception {
    MemberService.loadMemberData();
    PostService.loadPostData();
    CommentService.loadCmtData();
    
    






  }
}
