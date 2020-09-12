package com.zipcode.gjblog.blogcontroller;

import com.zipcode.gjblog.blogmodel.Post;
import com.zipcode.gjblog.blogmodel.Profile;
import com.zipcode.gjblog.blogservice.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import sun.net.www.http.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@CrossOrigin
@RequestMapping("/blog")
public class BlogController {

    BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping("/new")
    public @ResponseBody
    Post createAnonymousBlog(@RequestBody Post request){
        try{
            request.setUserName("Anonymous");
            return blogService.postBlog(request);
        } catch(Exception e){
            Logger.getLogger("BlogController - new anonymousPost").log(Level.WARNING,e.toString());
            return null;
        }
    }

    @PostMapping("/authenticatedNew")
    public @ResponseBody
    Post createBlog(@RequestBody Post request, @AuthenticationPrincipal OidcUser user){
        try{
            request.setUserName(user.getFullName());
            return blogService.postBlog(request);
        }catch(Exception e){
            Logger.getLogger("BlogController - new").log(Level.WARNING,e.toString());
            return null;
        }
    }

    @GetMapping("/tag")
    public @ResponseBody
    List <Post> displayBlogByTag(@RequestParam(name = "tag") String searchTag){
        try{
            return blogService.getBlogByTag(searchTag);
        }catch(Exception e){
            Logger.getLogger("Controller-tag").log(Level.WARNING,e.toString());
            return null;
        }
    }

    @GetMapping("/all")
    public @ResponseBody
    List<Post> getPosts(){
        try{
            return blogService.getAllBlog();
        } catch (Exception e){
            Logger.getLogger("Controller-getPosts").log(Level.WARNING,e.toString());
            return new ArrayList<Post>();
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<Profile> newProfile(@RequestBody Profile profile){
        Profile response = null;
        try{
            response = blogService.createProfile(profile);
        }catch (Exception e){
            Logger.getLogger("Controller-profile").log(Level.WARNING,e.toString());
            if(e instanceof HttpClientErrorException){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }else if(e instanceof HttpServerErrorException){
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }
}
