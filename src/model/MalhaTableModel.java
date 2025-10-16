package model;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.util.*;
import java.util.List;

public class MalhaTableModel extends AbstractTableModel {
    private EstradaCelula[][] malhaEstrada;
    private int rows;
    private int cols;
    private String[] columnNames;
    private ImageIcon[] directionIcons;

    public MalhaTableModel(String filePath) throws IOException {
        carregarMalhaDeArquivo(filePath);
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

    private void carregarMalhaDeArquivo(String filePath) throws IOException {
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
