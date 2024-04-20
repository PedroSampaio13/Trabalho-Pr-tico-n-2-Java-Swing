import javax.swing.*;
import java.awt.*;

public class Janela extends JFrame {

    Janela(){
        this.setTitle("Bookings"); //adiciona um titulo ao GUI
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Permite fechar o GUI no X
        this.setResizable(true); // Permite que o usuario defina o tamanho da janela
        this.setSize(900,600); //Define o tamanho inicial da janela
        this.setVisible(true);

        ImageIcon icon = new ImageIcon("logo.jpg"); // cria um icon
        this.setIconImage(icon.getImage()); // adiciona o icon
        this.getContentPane().setBackground(new Color(123,165,123));// Muda a cor de funso da janela
    }
}
