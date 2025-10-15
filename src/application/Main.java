package application;


import controller.SelecaoMalhaController;
import view.SelecaoMalha;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Pichau
 */
public class Main {
    public static void main(String[] args) {
        SelecaoMalhaController controller = new SelecaoMalhaController(new SelecaoMalha());
        controller.exibirTela();
    }
}
