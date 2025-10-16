package model;

import controller.ExecucaoMalhaController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Carro extends Thread {
    private Random random = new Random();
    private int velocidade;
    private EstradaCelula estrada;
    private ExclusaoMutuaTipo exclusaoMutuaTipo;
    private ExecucaoMalhaController controller;
    private static final Object cruzamentoMonitor = new Object();
    private static final Object moverEstradaNormal = new Object();

    public Carro(EstradaCelula estrada, ExclusaoMutuaTipo exclusaoMutuaTipo, ExecucaoMalhaController controller){
        this.velocidade = random.nextInt(500) + 500;
        this.estrada = estrada;
        this.exclusaoMutuaTipo = exclusaoMutuaTipo;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(velocidade);
            while (!estrada.isSaida() && !this.isInterrupted()) {

                if (estrada.getProximaEstrada().isCruzamento()) {
                    try {
                        percorrerCruzamento();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (estrada.isProximaCelulaLivre()) {
                    moverParaCelula(estrada.getProximaEstrada(), true);
                }

                atualizarInterfaceGrafica();

                Thread.sleep(velocidade);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (estrada.isSaida()) {
            if (!this.isInterrupted()) this.interrupt();
            removerCarroMalha();
        }
    }

    public void removerCarroMalha() {
        estrada.setCarro(null);
        controller.removerCarroMalha(this);
        if(exclusaoMutuaTipo == ExclusaoMutuaTipo.SEMAFORO) {
            estrada.liberarEstrada();
        } else {
            estrada.getLock().unlock();
        }
        atualizarInterfaceGrafica();
    }

    public List<EstradaCelula> monitorPercorrerCruzamento(List<EstradaCelula> estradasAtravessarCruzamento) {
        List<EstradaCelula> cruzamentosReservados = new ArrayList<>();
        for (EstradaCelula e : estradasAtravessarCruzamento) {
            if (e.getLock().tryLock()) {
                cruzamentosReservados.add(e);
            } else {
                liberarCruzamentosReservados(cruzamentosReservados);
                break;
            }
        }
        return cruzamentosReservados;
    }

    private void liberarCruzamentosReservados(List<EstradaCelula> cruzamentosReservados) {
        for (EstradaCelula e : cruzamentosReservados) {
            e.getLock().unlock();
        }
        cruzamentosReservados.clear();
    }

    private void percorrerCruzamento() throws InterruptedException {
        EstradaCelula primeiraEstradaCruzamento = estrada.getProximaEstrada();

        if (primeiraEstradaCruzamento.isCruzamento()) {
            List<EstradaCelula> estradasAtravessarCruzamento = primeiraEstradaCruzamento.getListaEstradaAtrevessarCruzamento();

            List<EstradaCelula> estradasCruzamentoReservados = null;

            if(exclusaoMutuaTipo == ExclusaoMutuaTipo.MONITOR){
                estradasCruzamentoReservados = monitorPercorrerCruzamento(estradasAtravessarCruzamento);
            } else {
                estradasCruzamentoReservados = getCruzamentosReservados(estradasAtravessarCruzamento);
            }

            if (estradasAtravessarCruzamento.size() == estradasCruzamentoReservados.size()) {
                for (EstradaCelula e : estradasAtravessarCruzamento) {
                    moverParaCelula(e, false);
                    if (e.isCruzamento()) {
                        atualizarInterfaceGrafica();
                        Thread.sleep(this.velocidade);
                    }
                }
            }
        }
    }

    private List<EstradaCelula> getCruzamentosReservados(List<EstradaCelula> estradasAtravessarCruzamento) {
        ArrayList<EstradaCelula> cruzamentosReservados = new ArrayList<>();
        for (EstradaCelula cruzamentoTentaReservar : estradasAtravessarCruzamento) {
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

    private void moverParaCelula(EstradaCelula est, boolean testar) {
        boolean reservado = false;

        if (testar) {
            try {
                do {
                    if (exclusaoMutuaTipo == ExclusaoMutuaTipo.MONITOR) {
                        // MONITOR
                        if (est.getLock().tryLock()) {
                            reservado = true;
                        } else {
                            sleep(random.nextInt(500));
                        }
                    } else {
                        // SEMAFORO
                        if (est.tentarEntrarEstrada()) {
                            reservado = true;
                        } else {
                            sleep(random.nextInt(500));
                        }
                    }
                } while (!reservado);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        estrada.setCarro(null);
        est.setCarro(this);

        if (exclusaoMutuaTipo == ExclusaoMutuaTipo.MONITOR && estrada.getLock().isHeldByCurrentThread()) {
            estrada.getLock().unlock();
        } else if(exclusaoMutuaTipo == ExclusaoMutuaTipo.SEMAFORO) {
            estrada.liberarEstrada();
        }

        estrada = est;

    }


    public void atualizarInterfaceGrafica() {
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
