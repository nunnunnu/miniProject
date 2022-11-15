

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import data.Comment;
import data.Member;
import data.Post;
import service.CommentService;
import service.MemberService;
import service.PostService;

public class JsonFileTest {
  public static void main(String[] args) throws IOException {

    MemberService.loadMemberData();
    PostService.loadPostData();
    CommentService.loadCmtData();

    JSONObject json = new JSONObject();
    JSONArray memarr = new JSONArray();
    JSONArray postarr = new JSONArray();
    JSONArray cmtarr = new JSONArray();

    for(Member m : MemberService.members){
      JSONObject data = new JSONObject();
      data.put("id", m.getId());
      data.put("pwd", m.getPwd());
      data.put("nickname", m.getNickname());
      data.put("name", m.getName());
      data.put("birth", m.getBirth());
      data.put("status", m.getStatus());
      data.put("regNo", m.getRegNo());
      memarr.add(data);
    }
    json.put("member", memarr);
    
    for(Post p : PostService.posts){
      JSONObject data = new JSONObject();
      data.put("postNo", p.getNo());
      data.put("title", p.getTitle());
      data.put("content", p.getContent());
      data.put("nickname", p.getNickname());
      data.put("creatDt", p.getCreateDate());
      data.put("modDt", p.getModDate());
      data.put("status", p.getStatus());
      data.put("category", p.getCategory());
      data.put("id", p.getId());
      data.put("view", p.getView());
      data.put("like", p.getLike());
      postarr.add(data);
    }
    json.put("post", postarr);

    for(Comment c : CommentService.comments){
      JSONObject data = new JSONObject();
      data.put("postNo", c.getPostNo());
      data.put("cmdNo", c.getCommentNo());
      data.put("id", c.getId());
      data.put("nickname", c.getNickname());
      data.put("cmtContent", c.getCommentText());
      data.put("CreatDate", c.getCreateDate());
      data.put("status", c.getStatus());
      data.put("nestedCmt", c.getNestedCmt());
      cmtarr.add(data);
    }
    json.put("comment", cmtarr);

    String jsonstr = json.toString()+"\n";
    
    File jsonfile = new File("data_file/jsonfile.json");

    BufferedWriter writer = new BufferedWriter(new FileWriter(jsonfile));
    writer.write(jsonstr);
    writer.close();
  }
}
