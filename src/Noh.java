import java.util.ArrayList;
import java.util.List;


public class Noh {
	private Integer nome = -1;
	private List<Caminho> caminhos = new ArrayList<Caminho>();
	private List<Noh> e_closure = new ArrayList<Noh>();
	private boolean aceitacao = false;
	
	public List<Noh> testString(String test){
	    List<Noh> result = new ArrayList<Noh>();
	    //System.out.println("Noh:"+this.getName()+" - String: "+test);
	    for(int j=0;j<caminhos.size();j++){
	        //System.out.println(!test.isEmpty() +" arco:"+caminhos.get(j).getArco()+ " substring:" + test.substring(0,1));
    	    if(!test.isEmpty()){
    	        if(caminhos.get(j).getArco().equals(test.substring(0,1))){
    	        //System.out.println("Call:"+caminhos.get(j).getDestino().getName()+" subst:"+test.substring(1));
    	        result.addAll(caminhos.get(j).getDestino().testString(test.substring(1)));
    	       }
    	    }
    	    if(caminhos.get(j).getArco()=="&"){
    	        //System.out.println("Call:"+caminhos.get(j).getDestino().getName()+" subst:"+test);
    	        result.addAll(caminhos.get(j).getDestino().testString(test));
    	    }
        }
        if(test.isEmpty())
            result.add(this);
	    return result;
	}
	
	public boolean isAceitacao(){
	    return aceitacao;
	}
	
	public void setAceitacao(){
	    aceitacao = true;
	}
	public void setNotAceitacao(){
	    aceitacao = false;
	}
	
	public List<Noh> updateEClosure(){
	    int j=0;
	    e_closure = new ArrayList<Noh>();
	    
	    while(j<caminhos.size()){
	        if(caminhos.get(j).getArco()=="&"){
	           e_closure.add(caminhos.get(j).getDestino());
	        }
	        j++;
	    }
	    return e_closure;
	}
	
	public List<Noh> getEClosure(){
	    return e_closure;
	}
	
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
		this.updateEClosure();
	}
	
	public void removeCaminho(Caminho caminho ){
		caminhos.remove(caminho);
		this.updateEClosure();
	}
	
	public List<Caminho> getCaminhos(){
		return caminhos;
	}
	
	public void printCaminhos(){
		for(int i=0; i<caminhos.size(); i++)
			System.out.println(this.nome + "->" + caminhos.get(i).getDestino().getName() + ": " + caminhos.get(i).getArco());
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
