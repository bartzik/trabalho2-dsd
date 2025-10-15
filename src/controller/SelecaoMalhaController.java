/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import javax.swing.JOptionPane;

import model.ExclusaoMutuaTipo;
import model.MalhaTableModel;
import view.ExecucaoMalha;
import view.SelecaoMalha;

/**
 *
 * @author Pichau
 */
public class SelecaoMalhaController {

    private SelecaoMalha telaSelecao;
    private String malhaSelecionada;
    private MalhaTableModel malhaTableModel;
    private ExclusaoMutuaTipo exclusaoMutuaTipo;

    public SelecaoMalhaController(SelecaoMalha tela) {
        this.telaSelecao = tela;
        this.telaSelecao.getRadioMalha1().setSelected(true);
        acaoSelecionarMalha(1);
        this.telaSelecao.setDefaultRadioButtonSelected();
        inicializarBotoes();
    }

    public void exibirTela() {
        telaSelecao.exibirTela();
    }

    public void fecharTela() {
        telaSelecao.fecharTela();
    }

    public void acaoBotaoConfirmar() {
        ExecucaoMalhaController execucaoMalhaController = new ExecucaoMalhaController(new ExecucaoMalha(), malhaSelecionada, exclusaoMutuaTipo);
        execucaoMalhaController.exibirTela();
        fecharTela();
    }

    public void acaoSelecionarMalha(int opcao) {
        try {
            switch (opcao) {
                case 1:
                    malhaSelecionada = "src/files/malha1.txt";
                    break;
                case 2:
                    malhaSelecionada = "src/files/malha2.txt";
                    break;
                case 3:
                    malhaSelecionada = "src/files/malha3.txt";
                    break;
                default:
                    throw new IllegalArgumentException("Opção inválida de malha");
            }

            // Atualiza a malha selecionada
            setMalhaSelecionada(malhaSelecionada);

            // Carregar a malha no MalhaTableModel e definir na tabela
            malhaTableModel = new MalhaTableModel(malhaSelecionada);
            telaSelecao.setTableModel(malhaTableModel);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar a malha: " + e.getMessage());
        }
    }

private void inicializarBotoes() {
        telaSelecao.adicionarAcaoBotaoConfirmar(acao -> acaoBotaoConfirmar());
        telaSelecao.adicionarAcaoRadioMalha1(acao -> acaoSelecionarMalha(1));
        telaSelecao.adicionarAcaoRadioMalha2(acao -> acaoSelecionarMalha(2));
        telaSelecao.adicionarAcaoRadioMalha3(acao -> acaoSelecionarMalha(3));
        telaSelecao.adicionarAcaoRadioExclusaoMutua1(acao -> acaoSelecionarExclusaoMutua(ExclusaoMutuaTipo.MONITOR));
        telaSelecao.adicionarAcaoRadioExclusaoMutua2(acao -> acaoSelecionarExclusaoMutua(ExclusaoMutuaTipo.MONITOR));
    }

    private void acaoSelecionarExclusaoMutua(ExclusaoMutuaTipo tipo) {
        setExclusaoMutuaTipo(tipo);
    }

    public String getMalhaSelecionada() {
        return malhaSelecionada;
    }

    public void setMalhaSelecionada(String malhaSelecionada) {
        this.malhaSelecionada = malhaSelecionada;
    }
    
    public void setMalhaTableModel(MalhaTableModel malhaTableModel) {
    this.malhaTableModel = malhaTableModel;
    
    // Define o modelo da tabela
    telaSelecao.setTableModel(malhaTableModel);
    telaSelecao.getTableMalha().setTableHeader(null);
}

    public ExclusaoMutuaTipo getExclusaoMutuaTipo() {
        return exclusaoMutuaTipo;
    }

    public void setExclusaoMutuaTipo(ExclusaoMutuaTipo exclusaoMutuaTipo) {
        this.exclusaoMutuaTipo = exclusaoMutuaTipo;
    }
}
