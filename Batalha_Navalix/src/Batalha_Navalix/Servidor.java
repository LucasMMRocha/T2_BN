package Batalha_Navalix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Servidor {
    private static char[][] tabuleiro = new char[8][8];
    private static int navios = 5;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Aguardando jogador...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Jogador conectado!");
        PrintStream out = new PrintStream(clientSocket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        inicializarTabuleiro();
        while (navios > 0) {
            String posicao = in.readLine();
            if (posicao == null) break;
            int resultado = atacar(posicao);
            out.println(resultado);
        }
        serverSocket.close();
    }

    private static void inicializarTabuleiro() {
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tabuleiro[i][j] = '-';
            }
        }
        for (int i = 0; i < navios; i++) {
            int x, y;
            do {
                x = random.nextInt(8);
                y = random.nextInt(8);
            } while (tabuleiro[x][y] == 'N');
            tabuleiro[x][y] = 'N';
        }
    }

    private static int atacar(String posicao) {
        int x = posicao.charAt(0) - 'a';
        int y = posicao.charAt(1) - '1';
        if (tabuleiro[x][y] == 'N') {
            tabuleiro[x][y] = 'X';
            navios--;
            return 1; // Acertou
        } else if (tabuleiro[x][y] == '-') {
            tabuleiro[x][y] = 'O';
            return 0; // Água
        } else {
            return -1; // Já atacou aqui
        }
    }
}
