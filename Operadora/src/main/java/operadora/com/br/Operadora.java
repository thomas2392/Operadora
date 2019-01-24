package operadora.com.br;

import br.edu.ifpb.tsi.pd.websocket.Chat;
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

@ServerEndpoint("/operadora/{numero}")
public class Operadora {
    private static List<Linha> linhas = Collections.synchronizedList(new ArrayList<Linha>());
    private static List<String> numeros = Collections.synchronizedList(new ArrayList<String>());
    
    @OnOpen
    public void onOpen(Session ses, @PathParam("numero")String numero){
        //Testa se o numero ja esta cadastrado na sessao
        if(numeros.contains(numero)){
            try {
                ses.getBasicRemote().sendText("Bem vindo de volta, "+numero);
                for(int i = 0; i < linhas.size(); i++){
                    if(linhas.get(i).getTelefone().equals(numero)){
                        linhas.get(i).setSession(ses);
                        if(!linhas.get(i).getUnread().isEmpty()){
                            ses.getBasicRemote().sendText("Voce tem mensagens nao lidas:");
                            for(String mensagem: linhas.get(i).getUnread()){
                             ses.getBasicRemote().sendText(mensagem);
                            }
                            linhas.get(i).getUnread().clear();
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Operadora.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            Linha novalinha = new Linha(numero);
            novalinha.setSession(ses);
            linhas.add(novalinha);//As linhas são cadastradas quando se conectam a primeira vez com o a operadora, com o saldo zerado.
            numeros.add(numero);
            try {
                ses.getBasicRemote().sendText("Seja bem vindo, "+numero);
            } catch (IOException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean checarSaldo(Session session, String numero){
        boolean bol = false;
        for(int i = 0; i < linhas.size(); i++){
            if(linhas.get(i).getTelefone().equals(numero)){
                if(linhas.get(i).getSaldo() >= 0.5){
                  linhas.get(i).setSaldo(linhas.get(i).getSaldo() - 0.5);//Desconta RS 0,50 do saldo
                  bol = true;
                }
                else{
                    try {
                        session.getBasicRemote().sendText("Saldo insuficiente para enviar SMS.");//Alerte que o numero nao tem saldo suficiente
                    } catch (IOException ex) {
                        Logger.getLogger(Operadora.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return bol;
    }
    
    @OnMessage
    public void enviarSMS(Session session, String message, @PathParam("numero")String numero) {
        
        String[] parts = message.split(";");
        if(parts.length <= 1){
            try {
                session.getBasicRemote().sendText("Mensagem incorreta.");
                session.getBasicRemote().sendText("Ex.: Numero: 12345-1234; Mensagem: Olá.");
            } catch (IOException ex) {
                Logger.getLogger(Operadora.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String destinatario = parts[0];
        String mensagem = "Remetente:" + numero + " - Mensagem:" + parts[1];
        if(checarSaldo(session, numero)){
            if(numeros.contains(destinatario)){
                for(Linha linha: linhas){
                    if(linha.getTelefone().equals(destinatario)){
                        if(!linha.isConectado()){
                            linha.setUnread(mensagem);//Salva a mensagem na sessao para quando o telefona se conectar
                        }else{
                            try {
                                linha.getSession().getBasicRemote().sendText(mensagem);//Envia Mensagem mais numero do remetente
                            } catch (IOException ex) {
                                Logger.getLogger(Operadora.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }else{
                try {
                    session.getBasicRemote().sendText("O numero informado nao existe.");//Valida se o numero de destino existe
                } catch (IOException ex) {
                    Logger.getLogger(Operadora.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("numero")String numero) {
        for(int i = 0; i < linhas.size(); i++){
            if(linhas.get(i).getTelefone().equals(numero)){
                linhas.get(i).setConectado(false);//Ao encerrar a sessão setar o status da linha como false, ou seja o telefone está desconectado.
            }
        }
    }
    
}
