import java.util.ArrayList;
import java.util.List;


public class Noh {
	private Integer nome = -1;
	private List<Caminho> caminhos = new ArrayList<Caminho>();
	
	public Noh (int nome){
		this.nome = nome;
	}
	
	public boolean isNohFinal(){
		return caminhos.isEmpty();
	}
	
	public int getName(){
		return nome;
	}
		
	public void addCaminho (Noh destino, String arco){
		Caminho caminho = new Caminho (arco, destino);
		this.caminhos.add(caminho);
	}
	
	public void removeCaminho(Caminho caminho ){
		caminhos.remove(caminho);
	}
	
	public List<Caminho> getCaminhos(){
		return caminhos;
	}
	
	public void printCaminhos(){
		for(int i=0; i<caminhos.size(); i++)
			System.out.println(this.nome + "->" + caminhos.get(i).getDestino().getName() + ": " + caminhos.get(i).getArco() + "\n");
	}
	
	public boolean temArcoComSimbolo(){
		boolean temArcoComSimbolo = false;
		for(int i=0; i<caminhos.size() && !temArcoComSimbolo; i++){
			if(caminhos.get(i).arcoTemSimbolo())
				temArcoComSimbolo = true;
		}
		return temArcoComSimbolo;
	}
	
}
