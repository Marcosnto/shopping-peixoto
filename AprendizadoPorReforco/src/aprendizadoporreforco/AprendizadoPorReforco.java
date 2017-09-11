/*==========================================================//
 * UNIVERSIDADE FEDERAL DE SERGIPE                         ||
 * CAMPUS ALBERTO DE CARVALHO - ITABAIANA/SE               ||
 * CURSO: SISTEMAS DE INFORMAÇÃO                           ||
 * DISCIPLINA: INTELIGÊNCIA ARTIFICIAL                     ||
 * TURMA: 2017.01                                          ||
 * DOCENTE: DR. ALCIDES XAVIER BENICASA                    ||
 * DISCENTE: EDNA DE CARVALHO ANDRADE                      ||
 *           MARCOS NETO SANTOS                            ||
 *           KAIC DE OLIVEIRA BARROS                       ||
 */
package aprendizadoporreforco;

import java.io.IOException;
import java.util.Scanner;

public class AprendizadoPorReforco {

    public static void main(String[] args) throws IOException {
        MetodosPrincipais metodos = new MetodosPrincipais();
        int operador;
        Scanner entrada = new Scanner(System.in);
        boolean ok = false;
        String opcao;
        do {
            System.out.println("\n|---------------------------------------------|");
            System.out.println("|____________ 1 - INICIAR APRENDIZADO __________|");
            System.out.println("|_______________ 2- MOSTRAR PERCURSO_____________|");
            System.out.println("|________________ 0 - SAIR _____________________|");
            System.out.println("|-----------------------------------------------|\n");
            System.out.println("Execulte a sua escolha: ");
            operador = entrada.nextInt();
            switch (operador) {
                case 1:
                    metodos.aprender();
                    System.out.println("Total de Exploracoes: " + metodos.TotalExplorar);
                    System.out.println("Total de Vezes que Usufruiu: " + metodos.TotalUsufruir);
                    ok = true;
                    break;
                case 2:
                    if (ok) {
                        metodos.realizarVigilancia();
                    } else {
                        System.out.println("Realize o aprendizado!!");
                    }
                    break;
                case 0:
                    System.out.println("TEM CERTEZA QUE DESEJA SAIR? (S/N)");
                    opcao = entrada.next();
                    if (opcao.equalsIgnoreCase("S")) {
                        System.out.println("OBRIGADO POR ACESSAR NOSSO SISTEMA...");
                    } else {
                        operador = -1;
                    }
                    break;
                default:
                    System.out.println("OPERADOR INVÁLIDO!");
            }
        } while (operador != 0);

    }
}
