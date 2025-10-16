package application;


import controller.SelecaoMalhaController;
import view.SelecaoMalha;

public class Main {
    public static void main(String[] args) {
        SelecaoMalhaController controller = new SelecaoMalhaController(new SelecaoMalha());
        controller.exibirTela();
    }
}
