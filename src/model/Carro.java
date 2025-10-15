/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import controller.ExecucaoMalhaController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Pichau
 */
public class Carro extends Thread {
    private Random r = new Random();
    private int velocidade;
    private EstradaCelula estrada;
    private ExclusaoMutuaTipo exclusaoMutuaTipo;
    private ExecucaoMalhaController controller;

    public Carro(EstradaCelula estrada, ExclusaoMutuaTipo exclusaoMutuaTipo, ExecucaoMalhaController controller){
        this.velocidade = r.nextInt(500) + 500;
        this.estrada = estrada;
        this.exclusaoMutuaTipo = exclusaoMutuaTipo;
        this.controller = controller;
    }

    @Override
    public void run() {
        while (!estrada.isSaida() && !this.isInterrupted()) {
                
            if (estrada.getProximaEstrada(estrada.getDirecao()).isCruzamento()){
                percorrerCruzamento();
            } else if (estrada.isProximaCelulaLivre()) {
                moverParaProximaCelula();
            }

            atualizarInterfaceGrafica();

            try {
                Thread.sleep(velocidade);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
            
        if (estrada.isSaida()){
            if (!this.isInterrupted()) this.interrupt();
            removerCarroMalha();
        }
    }
    
//  private void removerCarroMalha() {
    public void removerCarroMalha() {
        estrada.setCarro(null);

        //this.interrupt();
        controller.removerCarroMalha(this);
        estrada.liberarEstrada();
        atualizarInterfaceGrafica();
    }

    private void percorrerCruzamento() {
        // Obter a próxima estrada, que é a primeira célula do cruzamento
        EstradaCelula primeiraEstradaCruzamento = estrada.getProximaEstrada(estrada.getDirecao());

        // Verificar se a próxima estrada é realmente um cruzamento antes de continuar
        if (primeiraEstradaCruzamento.isCruzamento()) {
            // Obter o caminho completo do cruzamento
            List<EstradaCelula> cruzamentoEstradas = primeiraEstradaCruzamento.getCruzamentos();
            List<EstradaCelula> cruzamentosReservados = getCruzamentosReservados(cruzamentoEstradas);

            System.out.println("Carro: "+this.getName()+"cruzamentos: "+cruzamentoEstradas.size()+"reservados: "+cruzamentosReservados.size());

            if (cruzamentoEstradas.size() == cruzamentosReservados.size()) {
                for (EstradaCelula e : cruzamentoEstradas) {
                    moverParaCelula(e, false);
                    // TODO: ver melhor forma de resolver isso, talvez retirar a ultima estrada
                    // da lista de cruzamentoEstradas, que é a primeira estrada pós cruzamento
                    if (e.isCruzamento()) { // Resolver delay duplo ao sair do cruzamento
                        atualizarInterfaceGrafica();
                        
                        try {
                            Thread.sleep(this.velocidade);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }
    }

    private List<EstradaCelula> getCruzamentosReservados(List<EstradaCelula> cruzamentoEstradas) {
        ArrayList<EstradaCelula> cruzamentosReservados = new ArrayList<>();
        for (EstradaCelula cruzamentoTentaReservar : cruzamentoEstradas) {
            if (cruzamentoTentaReservar.tentarEntrarEstrada()) {
                cruzamentosReservados.add(cruzamentoTentaReservar);
            } else {
                this.liberarEstradaList(cruzamentosReservados);
                break;
            }
        }
        return cruzamentosReservados;
    }

    private void liberarEstradaList(ArrayList<EstradaCelula> cruzamentosReservados) {
        for (EstradaCelula estrada : cruzamentosReservados) {
            estrada.liberarEstrada();
        }
    }


    private void moverParaProximaCelula() {
        EstradaCelula proximaEstrada = estrada.getProximaEstrada(estrada.getDirecao());
//        if(proximaEstrada.tentarEntrarEstrada()){
        moverParaCelula(proximaEstrada, true);
//        }
    }

    private void moverParaCelula(EstradaCelula est, boolean testar){
//        atualizarInterfaceGrafica();

        boolean reservado = false;
        if (testar) {
            do {
                //Tenta "reservar/adquirir" a estrada
                if (est.tentarEntrarEstrada()) {
                    reservado = true;
                }
            } while (!reservado);
        }

        estrada.setCarro(null);
        est.setCarro(this);
        estrada.liberarEstrada();
        estrada = est;
//        atualizarInterfaceGrafica();
    }


    public void atualizarInterfaceGrafica() {
        // Atualizar a célula onde o carro está (pintar o carro)
        estrada.getMalha().fireTableCellUpdated(estrada.getLin(), estrada.getCol());
        estrada.getMalha().fireTableDataChanged();
    }

    @Override
    public String toString() {
        return "Carro{" +
                "velocidade=" + velocidade +
                '}';
    }
}
