/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpb.tsi.pd.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author 2420625
 */
@ServerEndpoint("/chat/{nome}")
public class Chat {
    private static List<Session> usuarios = 
            Collections.synchronizedList(new ArrayList<Session>());
    
    @OnOpen
    public void abrir(Session ses, @PathParam("nome")String nome){
        try {
            ses.getBasicRemote().sendText("Seja bem vindo,"+nome);
            usuarios.add(ses);
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    @OnMessage
    public void onMessage(String message, @PathParam("nome")String nome) {
        for(Session ses:usuarios){
            try {
                ses.getBasicRemote().sendText(nome+" disse: "+message);
            } catch (IOException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
      
    @OnClose
    public void sair(Session ses){
        usuarios.remove(ses);
    }
}
