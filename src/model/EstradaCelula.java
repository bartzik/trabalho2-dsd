package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class EstradaCelula {
    private Random random;
    private Carro carro;
    private int direcao;
    private int col;
    private int lin;
    private MalhaTableModel malha;
    private boolean cruzamento;
    private Semaphore mutex;
    private final ReentrantLock lock = new ReentrantLock(); // MONITOR

    public EstradaCelula(int direcao, MalhaTableModel malha, int lin, int col, boolean cruzamento) {
        this.direcao = direcao;
        this.lin = lin;
        this.col = col;
        this.malha = malha;
        this.cruzamento = cruzamento;
        this.mutex = new Semaphore(1);
        this.random = new Random();
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public List<EstradaCelula> getListaEstradaAtrevessarCruzamento() {
        List<EstradaCelula> caminhoCruzamento = new ArrayList<>();
        EstradaCelula proximaEstrada = this;
        caminhoCruzamento.add(this);

        while (proximaEstrada != null && proximaEstrada.isCruzamento()) {
            List<Integer> direcoesPossiveis = obterDirecoesPossiveis(proximaEstrada);
            EstradaCelula novaEstrada = escolherNovaEstrada(direcoesPossiveis, proximaEstrada, caminhoCruzamento);

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

    private List<Integer> obterDirecoesPossiveis(EstradaCelula estrada) {
        List<Integer> direcoesPossiveis = new ArrayList<>();
        int direcao = estrada.getDirecao();

        // Adiciona direções possíveis baseado no valor de direção
        if (direcao == 5 || direcao == 9 || direcao == 10) {
            direcoesPossiveis.add(1); // Cima
        }
        if (direcao == 6 || direcao == 9 || direcao == 11) {
            direcoesPossiveis.add(2); // Direita
        }
        if (direcao == 7 || direcao == 11 || direcao == 12) {
            direcoesPossiveis.add(3); // Baixo
        }
        if (direcao == 8 || direcao == 10 || direcao == 12) {
            direcoesPossiveis.add(4); // Esquerda
        }

        return direcoesPossiveis;
    }

    private EstradaCelula escolherNovaEstrada(List<Integer> direcoesPossiveis, EstradaCelula proximaEstrada, List<EstradaCelula> caminhoCruzamento) {
        EstradaCelula novaEstrada = null;
        int direcaoEscolhida = -1;

        // Tenta encontrar uma direção válida que não leve a um loop
        while (!direcoesPossiveis.isEmpty()) {
            direcaoEscolhida = direcoesPossiveis.get(random.nextInt(direcoesPossiveis.size()));
            novaEstrada = proximaEstrada.getProximaEstrada(direcaoEscolhida);

            // Se a nova estrada já foi percorrida, remove essa direção das possibilidades
            if (caminhoCruzamento.contains(novaEstrada)) {
                direcoesPossiveis.remove(Integer.valueOf(direcaoEscolhida));
                novaEstrada = null; // Resetar para continuar tentando
            } else {
                break; // Encontramos uma direção que ainda não foi percorrida
            }
        }

        return novaEstrada;
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

    // Sobrecarga de métodos para melhor legibilidade
    public EstradaCelula getProximaEstrada() {
        return getProximaEstrada(this.direcao);
    }

    public boolean isProximaCelulaLivre(){
        return getProximaEstrada().getCarro() == null;
    }

    public boolean isSaida(){
        if(col == 0){
            return (direcao == 4);
        } else if (lin == 0){
            return (direcao == 1);
        } else if (col == malha.getColumnCount() - 1){
            return (direcao == 2);
        } else if (lin == malha.getRowCount() - 1){
            return (direcao == 3);
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
}
