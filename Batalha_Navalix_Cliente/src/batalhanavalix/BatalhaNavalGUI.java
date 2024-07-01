package batalhanavalix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class BatalhaNavalGUI extends JFrame {
    private JTextField enderecoField, posicaoField;
    private JButton conectarButton, aguardarButton, jogarButton;
    private JLabel jogadasLabel, naviosLabel;
    private JPanel gridPanel;
    private JTextArea logArea;

    private ServerSocket serverSocket;
    private Socket socket;
    private PrintStream out;
    private BufferedReader in;

    private static char[][] tabuleiro = new char[8][8];
    private static int navios = 5;

    public BatalhaNavalGUI() {
        setTitle("Batalha Navalix");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior com a grade
        gridPanel = new JPanel(new GridLayout(9, 9));
        gridPanel.add(new JLabel("")); // canto superior esquerdo vazio
        for (int i = 1; i <= 8; i++) {
            gridPanel.add(new JLabel(String.valueOf(i), SwingConstants.CENTER));
        }
        for (char c = 'a'; c <= 'h'; c++) {
            gridPanel.add(new JLabel(String.valueOf(c), SwingConstants.CENTER));
            for (int i = 1; i <= 8; i++) {
                gridPanel.add(new JButton(""));
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        // Painel direito com informações do jogo
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        jogadasLabel = new JLabel("Nr Jogadas: 0");
        naviosLabel = new JLabel("Navios restantes: 5");
        infoPanel.add(jogadasLabel);
        infoPanel.add(naviosLabel);
        add(infoPanel, BorderLayout.EAST);

        // Painel inferior com campos de entrada e botões
        JPanel inputPanel = new JPanel(new GridLayout(3, 3));
        inputPanel.add(new JLabel("Endereço:"));
        enderecoField = new JTextField();
        inputPanel.add(enderecoField);
        conectarButton = new JButton("Conectar");
        inputPanel.add(conectarButton);

        inputPanel.add(new JLabel("Posição:"));
        posicaoField = new JTextField();
        inputPanel.add(posicaoField);
        jogarButton = new JButton("Jogar");
        inputPanel.add(jogarButton);

        aguardarButton = new JButton("Aguardar jogador");
        inputPanel.add(new JLabel(""));
        inputPanel.add(aguardarButton);
        add(inputPanel, BorderLayout.SOUTH);

        // Área de log para mensagens
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.WEST);

        // Ações dos botões
        aguardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aguardarConexao();
            }
        });

        conectarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conectarServidor();
            }
        });

        jogarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarJogada();
            }
        });
    }

    private void inicializarTabuleiro() {
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

    private void aguardarConexao() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                logArea.append("Aguardando conexão...\n");
                socket = serverSocket.accept();
                logArea.append("Jogador conectado!\n");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintStream(socket.getOutputStream());
                inicializarTabuleiro();
                while (navios > 0) {
                    String posicao = in.readLine();
                    if (posicao == null) break;
                    int resultado = atacar(posicao);
                    out.println(resultado);
                    logArea.append("Ataque na posição: " + posicao + " Resultado: " + resultado + "\n");
                }
            } catch (Exception e) {
                logArea.append("Erro ao aguardar conexão: " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void conectarServidor() {
        new Thread(() -> {
            try {
                socket = new Socket(enderecoField.getText(), 12345);
                logArea.append("Conectado ao servidor!\n");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintStream(socket.getOutputStream());
            } catch (Exception e) {
                logArea.append("Erro ao conectar: " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void enviarJogada() {
        String posicao = posicaoField.getText();
        out.println(posicao);
        logArea.append("Você jogou: " + posicao + "\n");
    }

    private int atacar(String posicao) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BatalhaNavalGUI().setVisible(true));
    }
}
