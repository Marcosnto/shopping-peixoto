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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MetodosPrincipais {

    //Constantes
    //utilizada para saber a quantidade de estados do arquivo
    public static int TAMANHO = 13;
    //utilizada para para armazenar o nome do arquivo de estados que será utilizado
    public static String ESTADOS = "Estados1.txt";
    //utilizada para armazenar o nome do arquivo que contém as ações correspondentes as cada estado
    public static String ACOES = "Acoes1.txt";
    //utilizada para armazenar o nome do arquivo que irá conter o aprendizado (a parte inicial)
    public static String ARQUIVO = "ArquivoExterno";

    //utilizada para salvar os estados : nome, valor, e Recompensa.
    public static Estado estados[] = new Estado[TAMANHO];
    //utilizada para salvar os valores das acoes
    public static int acoes[] = new int[8];
    //utilizada para salvar os valores das acoes de cada estado
    public static Dado tabela[][] = new Dado[TAMANHO][8];
    //utilizada para decidir entre Usufruir ou Explorar
    public static ArrayList decisao = new ArrayList<Integer>();
    //utilizada para saber o Total de vezes que Houve de Exploração
    public static int TotalExplorar = 0;
    //utilizada para saber o Total de vezes que Usufruiu
    public static int TotalUsufruir = 0;

    //método utilizado para executar os metodos necessarios para aprendizado
    public static void aprender()throws IOException{
        lerEstados();
        lerAcoes();
        System.out.println("INICIALIZADO APRENDIZADO");
        for (int i = 0; i < estados.length; i++) {
            atualizarRecompensa(i);
            aprendizado(5000, i); //quantas vezes vai rodar e quem é o destino            
        }
        System.out.println("FINALIZADO");
    }
    
    //método utilizado para mostrar o caminho de um ponto origem até um ponto de destino
    public static String caminho(int destino, int origem) throws IOException {
        //ler o arquivo correspondente ao ponto de destino
        lerArquivo(destino);
        String caminho = "";
        while (origem != destino) {
            int estadoAux = -1;
            double maior = -1;
            for (int i = 0; i < acoes.length; i++) {
                if (tabela[origem][i].getValorAcao() > maior && tabela[origem][i].getEstado() != -1) {
                    maior = tabela[origem][i].getValorAcao();
                    estadoAux = tabela[origem][i].getEstado();
                }
            }
            caminho = caminho + " " + estados[origem].getNome() + "->";
            origem = estadoAux;
        }
        return caminho + estados[destino].getNome();
    }

    //método para inicializar a Tabela
    public static void inicializarTabela() {
        for (int l = 0; l < TAMANHO; l++) {
            for (int c = 0; c < 8; c++) {
                tabela[l][c] = new Dado(0, 0);
            }
        }
    }

    //método para alocar o valor da recompensa do estado destino e atualizar, para 0
    //o valor da recompensa do estado destino anterior
    public static void atualizarRecompensa(int destino) {
        estados[destino].setRecompensa(10);
        if (destino > 0) {
            estados[destino - 1].setRecompensa(0); //o estado anterior deixa de ter recompensa
        }
    }

    //método que realiza o aprendizado de todos os pontos para um ponto de destino especifico
    //e chama ao final o método para gravar o resultado em um arquivo
    public static void aprendizado(int totalVezes, int destino) throws IOException {
        TotalUsufruir = 0;
        TotalExplorar = 0;
        int x = 0;
        Random seleciona = new Random();
        int estadoAtual;
        while (x < totalVezes) { //para parar quando chegar ao fim no totalVezes
            do {
                estadoAtual = seleciona.nextInt(TAMANHO); //Escolhe o estado aleatoriamente
                int proximoEstado = -1; //os pra inicializar           
                int acao;
                if (totalAcao(estadoAtual) == 1) { //pra chegar/sair so tem uma acao
                    acao = retornarAcao(estadoAtual);
                    proximoEstado = tabela[estadoAtual][acao].getEstado();
                } else {
                    int decisao = escolha(); //escolher se vai usufruir ou explorar
                    if (decisao < 30) {  // vai usufruir
                        acao = maiorValorAcao(estadoAtual);
                        proximoEstado = tabela[estadoAtual][acao].getEstado();
                        TotalUsufruir++;
                    } else { //Vai explorar
                        TotalExplorar++;
                        acao = seleciona.nextInt(8);
                        boolean ok = true;
                        while (ok) {
                            Dado d = tabela[estadoAtual][acao];
                            if (d.getEstado() != -1) { //se existe estado pra essa acao a partir deste estado 
                                ok = false;
                                proximoEstado = d.getEstado();
                            } else {
                                acao = seleciona.nextInt(8); //Escolhe a Ação  aleatoria  
                            }
                        }
                    }
                }
                double recAt = estados[proximoEstado].getRecompensa() + (0.9 * (maiorValor(proximoEstado))); //Q-Learning                
                tabela[estadoAtual][acao].setValorAcao(recAt);
                estadoAtual = proximoEstado;
            } while (estadoAtual != destino); //estado final
            estadoAtual = seleciona.nextInt(TAMANHO); //escolhe outro ponto de partida aleatorio entre os 3 estados
            x++;
        }
        gravarArquivo(destino);
    }

    //método para a escolher qual a dcisao q será tomada: usufruir ou explorarar
    public static int escolha() {
        if (decisao.isEmpty()) {
            for (int i = 0; i < 100; i++) {
                decisao.add(i);
            }
        }
        Collections.shuffle(decisao); //embaralha
        int tam = decisao.size();
        int retor = (int) decisao.get(tam - 1);
        decisao.remove(tam - 1);
        return retor;
    }

    //método para retornar o maior valor dentre os valores das acoes de um determinado estado
    public static double maiorValor(int estado) {
        double maior = tabela[estado][0].valorAcao;
        for (int i = 1; i < acoes.length; i++) {
            if (tabela[estado][i].getValorAcao() >= maior) {
                maior = tabela[estado][i].getValorAcao();
            }
        }
        return maior;
    }

    //método utilizado pra saber qual a única acao que um determinado estado apresenta
    public static int retornarAcao(int estado) {
        for (int i = 0; i < acoes.length; i++) {
            if (tabela[estado][i].getEstado() != -1) { // se tem essa acao pro estado atual o proximo estado é diferente de -1
                return i;
            }
        }
        return 0;
    }

    //método para saber o total de acoes de um estado, ou seja,
    //quantas acoes tenho a partir de um dado estado
    public static int totalAcao(int estado) {
        int total = 0;
        for (int i = 0; i < acoes.length; i++) {
            if (tabela[estado][i].getEstado() != -1) { // se tem essa acao pro estado atual o proximo estado é diferente de -1
                total++;
            }
        }
        return total;
    }

    //método utilizado para retornar a acao que apresenta maior valor
    public static int maiorValorAcao(int estado) {
        int acao = -1;
        double maior = -1;
        for (int i = 0; i < acoes.length; i++) {
            if (tabela[estado][i].getEstado() != -1) { // se tem essa acao pro estado atual o proximo estado é diferente de -1
                if (tabela[estado][i].getValorAcao() > maior) {
                    maior = tabela[estado][i].getValorAcao();
                    acao = i;
                }
            }
        }
        return acao;
    }

    //método para exibir a tabela, uma matriz que apresenta os estados (linhas) 
    //e suas respectivas acoes juntamente qual o estado é direcionado com a mesma 
    //e o valor dela
    public static void mostrarTabela() {
        System.out.printf("%-10s%-10s%-10s%-10s", "Estado", "Acao", "Destino", "Valor ");
        System.out.printf("%-10s%-10s%-10s", "Acao", "Destino", "Valor ");
        System.out.printf("%-10s%-10s%-10s", "Acao", "Destino", "Valor ");
        System.out.printf("%-10s%-10s%-10s", "Acao", "Destino", "Valor ");
        System.out.printf("%-10s%-10s%-10s", "Acao", "Destino", "Valor ");
        System.out.printf("%-10s%-10s%-10s", "Acao", "Destino", "Valor ");
        System.out.printf("%-10s%-10s%-10s", "Acao", "Destino", "Valor ");
        System.out.printf("%-10s%-10s%-10s", "Acao", "Destino", "Valor ");
        System.out.println("");
        for (int i = 0; i < tabela.length; i++) {
            System.out.printf("%-10s", i);
            for (int j = 0; j < tabela[0].length; j++) {
                if (j == 0) {
                    System.out.printf("%-10s%-10s%-10s", "Esquerda", tabela[i][j].getEstado(), String.format("%.5f", tabela[i][j].getValorAcao()));
                } else if (j == 1) {
                    System.out.printf("%-10s%-10s%-10s", "Direita", tabela[i][j].getEstado(), String.format("%.5f", tabela[i][j].getValorAcao()));
                } else if (j == 2) {
                    System.out.printf("%-10s%-10s%-10s", "Cima", tabela[i][j].getEstado(), String.format("%.5f", tabela[i][j].getValorAcao()));
                } else if (j == 3) {
                    System.out.printf("%-10s%-10s%-10s", "Baixo", tabela[i][j].getEstado(), String.format("%.5f", tabela[i][j].getValorAcao()));
                } else if (j == 4) {
                    System.out.printf("%-10s%-10s%-10s", "L.E.C.", tabela[i][j].getEstado(), String.format("%.5f", tabela[i][j].getValorAcao()));
                } else if (j == 5) {
                    System.out.printf("%-10s%-10s%-10s", "L.E.B.", tabela[i][j].getEstado(), String.format("%.5f", tabela[i][j].getValorAcao()));
                } else if (j == 6) {
                    System.out.printf("%-10s%-10s%-10s", "L.D.C.", tabela[i][j].getEstado(), String.format("%.5f", tabela[i][j].getValorAcao()));
                } else {
                    System.out.printf("%-10s%-10s%-10s", "L.D.B.", tabela[i][j].getEstado(), String.format("%.5f", tabela[i][j].getValorAcao()));
                }
            }
            System.out.println("");
        }
    }

    //método que irá gravar os dados do aprendizado de todos os estados para um estado(destino) 
    //informacoes adquiridas em tabela
    public static void gravarArquivo(int estado) throws FileNotFoundException, IOException {
        String arquivo = ARQUIVO + estado + ".txt";
        File f = new File(arquivo);
        try {
            FileWriter fw = new FileWriter(f);
            PrintWriter pw = new PrintWriter(fw);
            for (int i = 0; i < tabela.length; i++) {
                for (int j = 0; j < tabela[0].length; j++) {
                    pw.print(tabela[i][j].getEstado() + ";");//estado para o qual  vai
                    pw.print(tabela[i][j].getValorAcao() + ";");//valor da acao
                }
                pw.println();
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //método utilizado para ler o arquivo de um estado e passar as informacoes para a tabela
    //que será utilizada para encontrar o caminho de um estado qualquer para esse especifico(destino).
    public static void lerArquivo(int destino) throws FileNotFoundException, IOException {
        String arquivo = ARQUIVO + destino + ".txt";
        try {
            FileReader fr = new FileReader(arquivo);
            BufferedReader br = new BufferedReader(fr);
            String ExpressaoRegular = "[;|]";
            String linha;
            int estado = 0;
            int acao = 0;

            while ((linha = br.readLine()) != null) {
                String parte[] = linha.split(ExpressaoRegular);

                for (int i = 0; i < parte.length; i = i + 2) {
                    int est = Integer.parseInt(parte[i]);
                    double valor = Double.parseDouble(parte[i + 1]);
                    tabela[estado][acao].setEstado(est);
                    tabela[estado][acao].setValorAcao(valor);
                    acao++;
                }
                estado++;
                acao = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //método para carregar o arquivo de estados que serão utilizados no aprendizado
    public static void lerEstados() throws FileNotFoundException, IOException {
        try {
            FileReader fr = new FileReader(ESTADOS);
            BufferedReader br = new BufferedReader(fr);
            String ExpressaoRegular = "[;|]";
            String linha;
            int x = 0;
            while ((linha = br.readLine()) != null) {
                String parte[] = linha.split(ExpressaoRegular);
                estados[x] = new Estado(parte[0], x, 0);
                x++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //método para carregar o arquivo de acoes especifico dos estados 
    //que serão utilizados no aprendizado
    public static void lerAcoes() throws FileNotFoundException, IOException {
        try {
            FileReader fr = new FileReader(ACOES);
            BufferedReader br = new BufferedReader(fr);
            String ExpressaoRegular = "[;|]";
            String linha;
            int estado = 0;
            int acao = 0;
            int proximo;

            while ((linha = br.readLine()) != null) {
                String parte[] = linha.split(ExpressaoRegular);
                for (int i = 0; i < parte.length; i++) {
                    proximo = retornarValorEstado(parte[i]);
                    tabela[estado][acao] = new Dado(proximo, 0);
                    acao++;
                }
                estado++;
                acao = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //método para retornar valor do estado, o identificador dele
    public static int retornarValorEstado(String nome) throws IOException {
        for (int i = 0; i < estados.length; i++) {
            if (estados[i].getNome().equalsIgnoreCase(nome)) {
                return estados[i].getValor();
            }
        }
        return -1;
    }

    //método para exibir todos os estados
    public static void mostrarEstados() throws IOException {
        for (int i = 0; i < estados.length; i++) {
            System.out.println("Estado " + estados[i].getNome() + ":  " + estados[i].getRecompensa());
        }
    }

    //método utilizado para excolher o percurso do drone
    public static void realizarVigilancia() throws IOException {
        lerEstados();
        inicializarTabela();
        ArrayList percorrer = new ArrayList<Integer>();
        for (int i = 0; i < estados.length; i++) {
            percorrer.add(i);
        }
        String percurso = "";
        Collections.shuffle(percorrer); //embaralha        
        //Escolhe o inicio : origem
        int origem = (int) percorrer.get(percorrer.size() - 1);
        //Escolhe o inicio : destino
        int destino = (int) percorrer.get(percorrer.size() - 2);
        percorrer.remove(percorrer.size() - 1); //Remove a origem da lista para não iniciar novamente do mesmo ponto
        do {
            percurso = percurso + " " + caminho(destino, origem);           
            origem = destino;
            Collections.shuffle(percorrer); //embaralha
            destino = (int) percorrer.get(percorrer.size() - 1);
            percorrer.remove(percorrer.size() - 1); //Remove a origem da lista para não iniciar novamente do mesmo ponto
        } while (!percorrer.isEmpty());
        System.out.println("Percurso Gerado : "+percurso);
    }
}
