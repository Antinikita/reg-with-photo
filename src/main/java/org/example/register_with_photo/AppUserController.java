package org.example.register_with_photo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class AppUserController {
    @Value("${upload.dir}")
    private String uploadDir;
    @Autowired
    AppUserRepository appUserRepository;
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("photo") MultipartFile photo) throws IOException {
        if(!photo.isEmpty()) {
            String fileName = photo.getOriginalFilename();
            Path path= Paths.get(uploadDir, fileName);
            photo.transferTo(path);
            AppUser appUser=new AppUser(username,password,fileName);
            appUserRepository.save(appUser);
        }
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username,
                            @RequestParam("photoName") String photoName,
                            Model model){
        Optional<AppUser> user=appUserRepository.findByUsernameAndPhoto(username,photoName);
        if(user.isPresent()){
            model.addAttribute("username",user.get().getUsername());
            return "dashboard";
        } else {
            model.addAttribute("error","error");
            return "login";
        }
    }
}
