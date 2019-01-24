package operadora.com.br;

import java.util.ArrayList;
import javax.websocket.Session;

public class Linha {
    
    public String telefone;
    public double saldo;
    public boolean conectado;
    public Session session;
    public ArrayList<String> unread = new ArrayList<String>();

    public Linha(String telefone) {
        this.telefone = telefone;
        this.saldo = 100;
        this.conectado = true;//true representa que o telefone está conectado.
    }

    Linha() {
        
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public ArrayList<String> getUnread() {
        return unread;
    }

    public void setUnread(String mensagem) {
        this.unread.add(mensagem);
    }

    
}
