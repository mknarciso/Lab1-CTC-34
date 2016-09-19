
public class Caminho {
	
	public Caminho(String arco, Noh destino){
		this.arco = arco;
		this.destino = destino;
	}
	
	private Noh destino;
	private String arco;
	
	public void changeArco(String novoArco){
		this.arco = novoArco;
	}
	
	public boolean arcoTemSimbolo(){
		if(arco.contains("+") || arco.contains("(") || arco.contains(")") || arco.contains("*"))
			return true;
		else
			return false;
	}
	
	public String getArco(){
		return arco;
	}
	
	public Noh getDestino(){
		return destino;
	}
}
