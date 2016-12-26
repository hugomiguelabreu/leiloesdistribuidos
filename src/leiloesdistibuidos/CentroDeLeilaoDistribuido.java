package leiloesdistibuidos;
import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.*;

/**
 * Esta classe tem que ser capaz de integrar os diferentes
 * leiloes que serao abertos e fechados ao longo do tempo.
 * Esta classe acolha os leiloes, a partir dela consegue-se
 * chegar até ao leilao em questao. A gestao interna dos leiloes nao é feito aqui.
 * O aviso ao utilizador tera que ser investigado para ver se pode,
 * ou nao ser feito aqui.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CentroDeLeilaoDistribuido implements Serializable
{
    // instance variables - replace the example below with your own
    private Map<String,Leilao> leiloes; // Contem todos os leiloes
    
    // Uso de um Map<Integer,Set<Advisador>? 
    // Onde o advisador é o responsavel por advisar
    // o cliente quando o leilao feche. 
    // Problema o socket vai mudando para os mesmos clientes
    // Como dito, a estudar.

    /**
     * Constructor for objects of class CentroDeLeilaoDistribuido
     */
    public CentroDeLeilaoDistribuido()
    {
        leiloes = new HashMap<String,Leilao>();
    }
    
    public CentroDeLeilaoDistribuido( Map<String,Leilao> l){
        leiloes = l;
    }

    /**
     * Os potenciais compradores podem licitar o item indicando o 
     * número de leilão e um valor em euros.
     * O metodo nao é sinchronizado porque o acesso ao centro
     * nao tem que ser limitado. O acesso ao leilao sim.
     * 
     */
    public void licitar(int idLeilao, double valor,Utilizador user){
        if( leiloes.containsKey(idLeilao) ){
          Leilao l = leiloes.get(idLeilao);
          synchronized(l){
                l.licita(valor,user);// Pode dar-se mais coisas, do tipo
                // o nome do utilizador que fez a licitacao.
                // Entre outros i guess...
            }
        }
    }
    
    /**
     * Criar um leilao. Diferentes versoes desta funcao pode ser feita.
     * A versao aqui apresentada recebe um leilao prefeito.
     * Enquanto o leilao nao esta registado este nao esta disponivel.
     * logo nao deve ser necessario o metodo ser synchronized.
     */
    public void registarLeilao(Leilao l){
        if( l == null) return; // A ver
        //long id = l.getId(); A ver se int chega.
        String id = l.getId();
        // Verifica que o id é unico.
        if( leiloes.containsKey(id)) return ;// A ver
        // Id unico, assuma-se que o leilao esta formalizado.
        // Se nao se quer assumir isto, verificaçoes sao necessarias.
        synchronized(l){
            leiloes.put(id,l);
            // Addicionar codigo para o registo dos licitadores?
        }
    }
    
    public String createLeilao(String descricao, float valorInicial,Utilizador vendedor){
        Leilao l = new Leilao(valorInicial,descricao,vendedor);
        registarLeilao(l);
        return l.getId();
    }
    
    /**
     * O sistema deve permitir aos clientes listar os leilões em curso, com as seguintes indicações
     * adicionais por item. Aos vendedores devem ser assinalados os itens por si leiloados (por exemplo
     * com *). Aos compradores deve-se indicar se possuem ou não a licitação mais alta (por exemplo
     * com +).
     * Possibilidades ( deve haver mais lol):
     * Fazer isto aqui. Em funçao do utilizador faz-se de uma maneira ou de outra na mesma funcao.
     * Criar 1 funçao privada que recolha todos os leiloes em curso e usa-la numa funcao publica.
     */
    
    /**
     * Implementacao da hipotese 2.
     */
    private Map<String,Leilao> leiloesEmCurso(){
        Map<String,Leilao> res = new HashMap<String,Leilao>(leiloes.values()
                                                                   .stream()
                                                                   .filter(l-> l.getEstado()==true)
                                                                   .collect(Collectors.toMap(l->l.getId(), l->l)));
        return res;
    }
    /**
     * Os leiloes devolvidos vêm na forma de Strings aqui.
     * Funçao nao testado.
     */
    public List<String> verLeiloes(Utilizador user){
        if( user == null) return null;
        Map<String,Leilao> ativos = this.leiloesEmCurso();
        String header = null;
        List<String> res = new Vector<>();
        // Verificar o tipo de cliente
        boolean vendedor = user.getTipo() == false;
        // Header a ser usado em funçao do client
        if( vendedor) header = "*";
        else header = "+";
        // Percorer os leiloes
        for(Leilao leilao : ativos.values()){
            if(vendedor){
                    if( leilao.getVendedor().equals(user))
                                    res.add(leilao.toString() + header);
                    else res.add(leilao.toString());
            }else{
                synchronized(leilao){
                    if( leilao.getMelhorLicitador().getUsername().equals(user.getUsername()))
                                    res.add(leilao.toString() + header);
                    else res.add(leilao.toString());
                }
            }
            
            // Como se pode ver as partes entre ifs se repetem caso seja possivel cortar, faz-se.
            // O metodo getVendedor() nao é sinchronizado, ja na segunda parte, aquela do comprador,
            // tem que ser synchronizado para que, no momento onde se obtem os dados, nao tenhamos uma mudança
            // de maior licitador.
        }
                            
        return res;           
        }
        
        
    }