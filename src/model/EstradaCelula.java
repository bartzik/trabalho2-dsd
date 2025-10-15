package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class EstradaCelula {
    private Carro carro;
    private int direcao;
    private int col;
    private int lin;
    private MalhaTableModel malha;
    private boolean cruzamento;
    private Semaphore mutex;

    public EstradaCelula(int direcao, MalhaTableModel malha, int lin, int col, boolean cruzamento) {
        this.direcao = direcao;
        this.lin = lin;
        this.col = col;
        this.malha = malha;
        this.cruzamento = cruzamento;
        this.mutex = new Semaphore(1);
    }

    public boolean tentarEntrarEstrada(){
        return mutex.tryAcquire();
    }

    public void liberarEstrada(){
        mutex.release();
    }

    public boolean isCruzamento() {
        return cruzamento;
    }

    public EstradaCelula getProximaEstrada(int direcao){
        int novaLinha = lin;
        int novaColuna = col;
        switch (direcao){
            case 1: // Cima
                novaLinha--;
                break;
            case 2: // Direita
                novaColuna++;
                break;
            case 3: // Baixo
                novaLinha++;
                break;
            case 4: // Esquerda
                novaColuna--;
                break;
        }
        return (EstradaCelula) malha.getValueAt(novaLinha, novaColuna);
    }

    public boolean isProximaCelulaLivre(){
        return getProximaEstrada(this.getDirecao()).getCarro() == null;
    }

    public boolean isSaida(){
        if(col == 0){
            if(direcao == 4) return true;
        } else if (lin == 0){
            if (direcao == 1) return true;
        } else if (col == malha.getColumnCount() - 1){
            if (direcao == 2) return true;
        } else if (lin == malha.getRowCount() - 1){
            if (direcao == 3) return true;
        }
        return false;
    }

    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    public int getDirecao() {
        return direcao;
    }

    public void setDirecao(int direcao) {
        this.direcao = direcao;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getLin() {
        return lin;
    }

    public void setLin(int lin) {
        this.lin = lin;
    }

    public MalhaTableModel getMalha() {
        return malha;
    }

    public void setMalha(MalhaTableModel malha) {
        this.malha = malha;
    }

    public void setCruzamento(boolean cruzamento) {
        this.cruzamento = cruzamento;
    }

    @Override
    public String toString() {
        return "EstradaCelula{" +
                "mutex=" + mutex +
                ", cruzamento=" + cruzamento +
                ", lin=" + lin +
                ", col=" + col +
                ", direcao=" + direcao +
                '}';
    }

    public List<EstradaCelula> getCruzamentos() {
        List<EstradaCelula> caminhoCruzamento = new ArrayList<>();
        EstradaCelula proximaEstrada = this;
        caminhoCruzamento.add(this);

        while (proximaEstrada != null && proximaEstrada.isCruzamento()) {
            List<Integer> direcoesPossiveis = new ArrayList<>();

            // Adiciona direções possíveis baseado no valor de direção
            if (proximaEstrada.getDirecao() == 5 || proximaEstrada.getDirecao() == 9 || proximaEstrada.getDirecao() == 10) {
                direcoesPossiveis.add(1); // Cima
            }
            if (proximaEstrada.getDirecao() == 6 || proximaEstrada.getDirecao() == 9 || proximaEstrada.getDirecao() == 11) {
                direcoesPossiveis.add(2); // Direita
            }
            if (proximaEstrada.getDirecao() == 7 || proximaEstrada.getDirecao() == 11 || proximaEstrada.getDirecao() == 12) {
                direcoesPossiveis.add(3); // Baixo
            }
            if (proximaEstrada.getDirecao() == 8 || proximaEstrada.getDirecao() == 10 || proximaEstrada.getDirecao() == 12) {
                direcoesPossiveis.add(4); // Esquerda
            }

            System.out.println("Direções possíveis: " + direcoesPossiveis);
//
            Random random = new Random();
            EstradaCelula novaEstrada = null;
            int direcaoEscolhida = -1;

            // Tenta encontrar uma direção válida que não leve a um loop
            while (!direcoesPossiveis.isEmpty()) {
                direcaoEscolhida = direcoesPossiveis.get(random.nextInt(direcoesPossiveis.size()));
                novaEstrada = proximaEstrada.getProximaEstrada(direcaoEscolhida);

                // Se a nova estrada já foi percorrida, remove essa direção das possibilidades
                if (caminhoCruzamento.contains(novaEstrada)) {
                    System.out.println("Ele tentou ir para uma casa já visitada");
                    direcoesPossiveis.remove(Integer.valueOf(direcaoEscolhida));
                    novaEstrada = null; // Resetar para continuar tentando
                } else {
                    break; // Encontramos uma direção que ainda não foi percorrida
                }
            }

            // Se não encontrou uma direção válida, sai do loop
            if (novaEstrada == null) {
                break;
            }

            // Adiciona a nova estrada ao caminho e continua
            caminhoCruzamento.add(novaEstrada);
            proximaEstrada = novaEstrada;
        }

        // Adiciona a última célula (que não é mais cruzamento) à lista
        if (proximaEstrada != null && !caminhoCruzamento.contains(proximaEstrada)) {
            caminhoCruzamento.add(proximaEstrada);
        }

        return caminhoCruzamento;
    }

}
