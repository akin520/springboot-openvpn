package com.akin.springbootopenvpn.controller;


import com.akin.springbootopenvpn.entity.LoginLogEntity;
import com.akin.springbootopenvpn.entity.OnlineEntity;
import com.akin.springbootopenvpn.entity.UserEntity;
import com.akin.springbootopenvpn.repository.LoginLogRepository;
import com.akin.springbootopenvpn.repository.UserRepository;
import com.akin.springbootopenvpn.util.ConstantUtils;
import com.alibaba.fastjson.JSON;
import com.sun.javafx.scene.shape.PathUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping(path="")
public class UserController {

    //https://www.yijiyong.com/spring/annotation/03-springannprin.html
    @Value("${openvpn.path}")
    private String opath;

    @Value("${openvpn.name}")
    private String oname;

    @Value("${lusername}")
    private String lusername;
    @Value("${lpassword}")
    private String lpassword;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginLogRepository loginLogRepository;
    private OnlineEntity onlineEntity;

    @RequestMapping("/login")
    public String login(Model model){
        String msg = "";
        model.addAttribute("msg", msg);
        return "login";
    }

    @PostMapping("/login")
    public String login(Model model,String username, String password,HttpServletRequest request) {
        String isLogin = "iamcomein";
        String msg = "";
        System.out.printf("%s-%s-%s-%s\n",lusername,lpassword,username,password);
        if (lusername.equals(username) && lpassword.equals(password)) {
            request.getSession().setAttribute(ConstantUtils.USER_SESSION_KEY,isLogin);
            return "redirect:/";
        }else{
            msg="用户或密码错误";
            model.addAttribute("msg", msg);
            return "login";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute(ConstantUtils.USER_SESSION_KEY);
        return "redirect:/login";
    }


    @GetMapping(path="/")
    public String index(Model model){
        List<UserEntity> userEntityList = userRepository.findAll();
        model.addAttribute("userEntityList", userEntityList);
        return "index";
    }

    @GetMapping(path="/modify/")
    public String modify(Model model,Long uid){
        Optional<UserEntity> userEntity= userRepository.findById(uid);
        System.out.println(userEntity);
        if (userEntity.equals(Optional.empty())) {
            model.addAttribute("msg", "找不到uid:"+uid);
            return "404";
        }
        model.addAttribute("userEntity", userEntity.get());
        return "edit";
    }

    @GetMapping(path="/addtime/")
    public String addtime(Model model,Long uid){
        try {
            userRepository.addTimeById(uid);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("msg", "找不到uid:"+uid);
            return "404";
        }

    }

    @GetMapping(path="/del/")
    public String del(Model model,Long uid){
        try {
            userRepository.deleteById(uid);
            return "redirect:/";
        }catch (Exception e) {
            model.addAttribute("msg", "找不到uid:"+uid);
            return "404";
        }
    }
    @GetMapping(path="/updateall")
    public String updateall(Model model){
        userRepository.updateAll();
        return "redirect:/";
    }

    @CrossOrigin
    @PostMapping(path="/user")
    public String modify(@RequestParam("id") Long id,@RequestParam("username") String username,@RequestParam("password") String password,Model model){
        System.out.println(id);
        System.out.println(username);
        System.out.println(password);
        String md5str = DigestUtils.md5DigestAsHex(password.getBytes());
        if (id == null) {
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH,1);
            UserEntity userEntity = new UserEntity();
            userEntity.setName(username);
            userEntity.setPassword(md5str);
            userEntity.setActive(1);
            userEntity.setExpired_time(new Timestamp(cal.getTimeInMillis()));
            try {
                UserEntity userEntity1 = userRepository.save(userEntity);
                System.out.println(JSON.toJSONString(userEntity1));
            }catch (Exception e) {
                model.addAttribute("msg", "错误信息："+e.toString());
                return "404";
            }
            return "redirect:/";
        } else {
            try {
                UserEntity userEntity = userRepository.findByIdAndName(id,username);
                if (userEntity != null) {
                    userEntity.setPassword(md5str);
                    userRepository.save(userEntity);
                    return "redirect:/";
                }
            } catch (Exception e) {
                return "id错误，返回";
            }
            return "redirect:/modify/?uid="+id;
        }

    }

    @GetMapping(path="/logs")
    public String logs(Model model){
        List<LoginLogEntity> loginLogEntityList = loginLogRepository.findId();
        model.addAttribute("loginLogEntityList", loginLogEntityList);
        return "logs";
    }
    @GetMapping(path="/online")
    public String online(Model model) throws IOException {
        System.out.printf("%s-%s\n",opath,oname);
        List<OnlineEntity> onlineEntityList = new ArrayList<OnlineEntity>();
        List<String> allLines = new ArrayList<>();
        if (opath.isEmpty()){
            //按行读取
//            File file = ResourceUtils.getFile("classpath:"+oname);
//            FileInputStream inputStream = new FileInputStream(file);
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            String str = null;
//            while((str = bufferedReader.readLine()) != null) {
//                System.out.println(str);
//            }
//            inputStream.close();
//            bufferedReader.close()

            File file = ResourceUtils.getFile("classpath:"+oname);
            allLines = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
        }else{
            System.out.println("-----------进入配置目录-------------");
            allLines = Files.readAllLines(Paths.get(opath+oname), StandardCharsets.UTF_8);
        }
        int start = allLines.indexOf("Common Name,Real Address,Bytes Received,Bytes Sent,Connected Since");
        int end = allLines.indexOf("ROUTING TABLE");
        System.out.println(start);
        System.out.println(end);
        if (start == -1 || end == -1) {
            model.addAttribute("msg", "找不到日志信息");
            return "404";
        }
        allLines.subList(start+1,end).forEach((line) ->{
            String[] split = line.split(",");
            OnlineEntity onlineEntity = new OnlineEntity();
            onlineEntity.setUsername(split[0]);
            onlineEntity.setIp(split[1]);
            onlineEntity.setReceived(split[2]);
            onlineEntity.setSent(split[3]);
            onlineEntity.setDate(split[4]);
            onlineEntityList.add(onlineEntity);
        });
        System.out.println(onlineEntityList);
        model.addAttribute("onlineEntityList", onlineEntityList);
        return "online";
    }
}
