package Batalha_Navalix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 12345);
        PrintStream out = new PrintStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Posição para atacar: ");
            String posicao = stdIn.readLine();
            out.println(posicao);
            String resposta = in.readLine();
            System.out.println("Resultado do ataque: " + resposta);
        }
    }
}
