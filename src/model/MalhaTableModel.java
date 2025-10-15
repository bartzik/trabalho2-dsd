package model;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

public class MalhaTableModel extends AbstractTableModel {
//    private int[][] malha;
    private EstradaCelula[][] malhaEstrada;
    private int rows;
    private int cols;
    private String[] columnNames;
    private ImageIcon[] directionIcons;

    public MalhaTableModel(String filePath) throws IOException {
        loadMalhaFromFile(filePath);
        columnNames = new String[cols];
        for (int i = 0; i < cols; i++) {
            columnNames[i] = String.valueOf(i);
        }

        directionIcons = new ImageIcon[13];
        directionIcons[0] = null; // No image for empty cells
        directionIcons[1] = new ImageIcon("src/images/up-arrow.png");
        directionIcons[2] = new ImageIcon("src/images/right-arrow.png");
        directionIcons[3] = new ImageIcon("src/images/down-arrow.png");
        directionIcons[4] = new ImageIcon("src/images/left-arrow.png");
    }

    private void loadMalhaFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Lê o número de linhas e colunas
            this.rows = Integer.parseInt(reader.readLine());
            this.cols = Integer.parseInt(reader.readLine());

            // Inicializa a matriz da malha com as dimensões especificadas
            malhaEstrada = new EstradaCelula[rows][cols];

            // Lê cada linha da malha diretamente na matriz
            for (int i = 0; i < rows; i++) {
                String[] line = reader.readLine().split("\\s+");
                for (int j = 0; j < cols; j++) {
                    int cellValue = Integer.parseInt(line[j]);
                    boolean isCruzamento = false;
                    if(Integer.parseInt(line[j]) >= 5 && Integer.parseInt(line[j]) <= 12 ) {
                        isCruzamento = true;
                    }


                    malhaEstrada[i][j] = new EstradaCelula(cellValue, this, i, j, isCruzamento);
                }
            }
        }
    }


    /*TODO: achar melhor local para esse cara*/
    private DirecaoEnum getDirecaoPorValor(int valor) {
        switch (valor) {
            case 1: return DirecaoEnum.CIMA;
            case 2: return DirecaoEnum.DIREITA;
            case 3: return DirecaoEnum.BAIXO;
            case 4: return DirecaoEnum.ESQUERDA;
            case 5: return DirecaoEnum.CRUZAMENTO_CIMA;
            case 6: return DirecaoEnum.CRUZAMENTO_DIREITA;
            case 7: return DirecaoEnum.CRUZAMENTO_BAIXO;
            case 8: return DirecaoEnum.CRUZAMENTO_ESQUERDA;
            case 9: return DirecaoEnum.CRUZAMENTO_CIMA_DIREITA;
            case 10: return DirecaoEnum.CRUZAMENTO_CIMA_ESQUERDA;
            case 11: return DirecaoEnum.CRUZAMENTO_DIREITA_BAIXO;
            case 12: return DirecaoEnum.CRUZAMENTO_BAIXO_ESQUERDA;
            default: return null; // ou trate de outra forma
        }
    }

    @Override
    public int getRowCount() {
        return rows;
    }

    @Override
    public int getColumnCount() {
        return cols;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return malhaEstrada[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public static void createAndShowTable(String filePath) throws IOException {
        MalhaTableModel model = new MalhaTableModel(filePath);
        JTable table = new JTable(model);
        table.setTableHeader(null);
        // Defina o tamanho máximo da malha
        int maxWidth = 650;
        int maxHeight = 650;

        // largura e altura das células com base no número de linhas e colunas
        int cellWidth = maxWidth / model.getColumnCount();
        int cellHeight = maxHeight / model.getRowCount();

        //  células de mesmo tamanho
        int cellSize = Math.min(cellWidth, cellHeight);

        // altura e largura das células
        table.setRowHeight(cellSize);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(cellSize);
        }

        // Centralizar horizontalmente
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);

        // Limitar o tamanho do JScrollPane para 650x650
        scrollPane.setPreferredSize(new Dimension(maxWidth, maxHeight));

        // Adiciona a tabela ao centro do painel
        panel.add(scrollPane, BorderLayout.CENTER);

        JFrame frame = new JFrame("Malha Viária");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Centralizar a tabela no centro horizontalmente
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }


    public List<EstradaCelula> getPontosDeEntrada() {
        List<EstradaCelula> entradas = new ArrayList<>();

        // Verificar a primeira e última linha
        for (int col = 0; col < cols; col++) {
            if (malhaEstrada[0][col].getDirecao() == 3) {
                entradas.add(malhaEstrada[0][col]);
            }
            if (malhaEstrada[rows - 1][col].getDirecao() == 1) {
                entradas.add(malhaEstrada[rows - 1][col]);
            }
        }

        // Verificar a primeira e última coluna (borda esquerda e direita)
        for (int row = 0; row < rows; row++) {
            if (malhaEstrada[row][0].getDirecao() == 2) { // Direção para direita (valor 2)
                entradas.add(malhaEstrada[row][0]);
            }
            if (malhaEstrada[row][cols - 1].getDirecao() == 4) { // Direção para esquerda (valor 4)
                entradas.add(malhaEstrada[row][cols - 1]);
            }
        }

        return entradas;
    }
}
