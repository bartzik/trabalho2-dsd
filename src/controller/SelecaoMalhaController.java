package controller;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import model.ExclusaoMutuaTipo;
import model.MalhaTableModel;
import view.ExecucaoMalha;
import view.SelecaoMalha;
import java.io.File;

public class SelecaoMalhaController {

    private SelecaoMalha telaSelecao;
    private String malhaSelecionada;
    private MalhaTableModel malhaTableModel;
    private ExclusaoMutuaTipo exclusaoMutuaTipo;

    public SelecaoMalhaController(SelecaoMalha tela) {
        this.telaSelecao = tela;
        this.setDefaultRadioButtonSelected();
        inicializarBotoes();
    }

    public void setDefaultRadioButtonSelected(){
        this.telaSelecao.setDefaultRadioButtonSelected();
        this.acaoSelecionarExclusaoMutua(ExclusaoMutuaTipo.SEMAFORO);
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

    private void inicializarBotoes() {
        telaSelecao.adicionarAcaoBotaoConfirmar(acao -> acaoBotaoConfirmar());
        telaSelecao.adicionarAcaoBotaoSelecionar(acao -> acaoSelecionarArquivo());
        telaSelecao.adicionarAcaoRadioExclusaoMutua1(acao -> acaoSelecionarExclusaoMutua(ExclusaoMutuaTipo.SEMAFORO));
        telaSelecao.adicionarAcaoRadioExclusaoMutua2(acao -> acaoSelecionarExclusaoMutua(ExclusaoMutuaTipo.MONITOR));
    }

    private void acaoSelecionarArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            malhaSelecionada = selectedFile.getAbsolutePath();
            telaSelecao.getSelectFileTextField().setText(malhaSelecionada);
            carregarMalha();
        }
    }

    private void carregarMalha() {
        try {
            // Carregar a malha no MalhaTableModel e definir na tabela
            malhaTableModel = new MalhaTableModel(malhaSelecionada);
            telaSelecao.setTableModel(malhaTableModel);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar a malha: " + e.getMessage());
        }
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
