package org.xd.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xd.server.WebSocket;
import org.xd.service.WSMessageService;

@Controller
public class MessageController {

    //websocket服务层调用类
    @Autowired
    private WSMessageService wsMessageService;



    //请求入口
    @RequestMapping(value="/chat",method= RequestMethod.GET)
    public String TestWS(Model model){
        return "/chat.html";
    }


    @ResponseBody
    @RequestMapping("/chat/")
    public String sendMessage(String msg){


        return "";
    }
}
