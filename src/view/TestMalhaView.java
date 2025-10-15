package view;

import model.MalhaCellRenderer;
import model.MalhaTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestMalhaView extends JFrame {
    private JPanel mainPanel;
    private JPanel tablePanel;
    private JRadioButton malha1Button;
    private JRadioButton malha2Button;
    private ButtonGroup malhaGroup;
    private MalhaTableModel malhaModel;

    public TestMalhaView() {
        setTitle("Simulador de Tráfego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel principal
        mainPanel = new JPanel(new BorderLayout());

        // Painel de seleção
        JPanel selectionPanel = new JPanel(new FlowLayout());
        malha1Button = new JRadioButton("Malha 1");
        malha2Button = new JRadioButton("Malha 2");
        malhaGroup = new ButtonGroup();
        malhaGroup.add(malha1Button);
        malhaGroup.add(malha2Button);

        selectionPanel.add(new JLabel("Escolha a Malha:"));
        selectionPanel.add(malha1Button);
        selectionPanel.add(malha2Button);

        // Painel da tabela
        tablePanel = new JPanel(new BorderLayout());
        mainPanel.add(selectionPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Listener para os botões
        malha1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTable("src/files/malha1.txt");
            }
        });

        malha2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTable("src/files/malha2.txt");
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Método para carregar e exibir a tabela com a malha
    private void loadTable(String filePath) {
        try {
            malhaModel = new MalhaTableModel(filePath);
            JTable table = new JTable(malhaModel);
            table.setRowHeight(50); // Altura de cada linha

            // Configurar o renderer personalizado para cada célula
            MalhaCellRenderer cellRenderer = new MalhaCellRenderer();
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }

            // Remover o cabeçalho da tabela
            table.setTableHeader(null);

            // Redimensionar as colunas para ajustar ao tamanho da imagem
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(50);
            }

            // Adicionar a tabela ao painel
            tablePanel.removeAll();
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            tablePanel.revalidate();
            tablePanel.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar a malha: " + ex.getMessage());
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TestMalhaView();
            }
        });
    }
}
