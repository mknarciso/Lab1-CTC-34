import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LabForster {
	
	int indexQuebra=-1;
	static List<Noh> nohs = new ArrayList<Noh>();
	
	public void getAFNDfromRegex(String regex){
		// 1º passo
		int indexNohs = 0;
		Noh nohInicial = new Noh(indexNohs++);
		Noh nohFinal = new Noh(indexNohs++);
		nohInicial.addCaminho(nohFinal, regex);
		nohs.add(nohInicial);
		nohs.add(nohFinal);
		int j=0;
		//Para cada noh na lista de nohs, verifica se 
		while (j < nohs.size()){
			Noh noh = nohs.get(j);
			
			//noh.printCaminhos();
			//Para cada caminho dentro do noh
			int i=0;
			while(i<noh.getCaminhos().size()){
				Caminho caminho = noh.getCaminhos().get(i);
				String aresta = caminho.getArco();
				System.out.println(noh.getName() + "->" + caminho.getDestino().getName() + ": " + aresta);
				
				//	2º passo - Quebra de Uniões
				if(regexIsUnion(aresta)){
					System.out.println("Entrando no passo 2");
					//System.out.println(indexQuebra + "\n");
					String lang1, lang2;
					Noh nohDestino = caminho.getDestino();
					noh.removeCaminho(caminho);
					lang1 = aresta.substring(0,indexQuebra);
					lang2 = aresta.substring(indexQuebra+1);
					//System.out.println(lang1 + "\n" + lang2);
					noh.addCaminho(nohDestino, lang1);
					noh.addCaminho(nohDestino, lang2);
				}
			
				//3º passo - Quebra de Concatenações
				if(regexIsConcat(aresta)){
					System.out.println("Entrando no passo 3");
					//System.out.println(indexQuebra + "\n");
					String lang1, lang2;
					Noh nohIntermediario = new Noh(indexNohs++);
					Noh nohDestino = caminho.getDestino();
					noh.removeCaminho(caminho);
					lang1 = aresta.substring(0,1);
					lang2 = aresta.substring(1,aresta.length());
					noh.addCaminho(nohIntermediario, lang1);
					nohIntermediario.addCaminho(nohDestino, lang2);
					//System.out.println(lang1 + "\n" + lang2);
					nohs.add(nohIntermediario);
				}
				
				//4º passo - Conferir fecho de Kleene
				if(regexIsKleene(aresta)){
					System.out.println("Entrando no passo 4");
					String arestaSemAsterisco = aresta.substring(0,aresta.length()-1);
					Noh nohIntermediario = new Noh(indexNohs++);
					Noh nohDestino = caminho.getDestino();
					noh.removeCaminho(caminho);
					noh.addCaminho(nohIntermediario, "&");
					nohIntermediario.addCaminho(nohDestino, "&");
					nohIntermediario.addCaminho(nohIntermediario, arestaSemAsterisco);
					nohs.add(nohIntermediario);
					//System.out.println(arestaSemAsterisco + "\n");
				}
				
				//5º passo - remover parênteses, se houver
				if(regexIsInParenthesis(aresta)){
					System.out.println("Entrando no passo 5");
					String novoArco = aresta.substring(1,aresta.length()-1);
					caminho.changeArco(novoArco);
				}	
				
				if(!caminho.arcoTemSimbolo())
					i++;
			}
			
			if(!noh.temArcoComSimbolo() || noh.isNohFinal())
				j++;
		}	
		System.out.println("Sai do While");
	}
	
	
	public boolean regexIsInParenthesis(String aresta){
		indexQuebra = -1;
		int tamanho = aresta.length();
		if(aresta.charAt(0) == '(' && aresta.charAt(tamanho-1) == ')')
			return true;
		else 
			return false;
	}
	
	public boolean regexIsKleene(String aresta){
		indexQuebra = -1;
		int tamanho = aresta.length();
		if(aresta.charAt(tamanho-1) == '*' && aresta.charAt(0) == '(' && aresta.charAt(tamanho-2) == ')' )
			return true;
		else 
			return false;
	}
	
	public boolean regexIsConcat(String aresta){
		indexQuebra = -1;
		if(!aresta.contains("+") && !aresta.contains("*") && aresta.length() > 1 ){
			indexQuebra = 2;
			return true;
		}
		else
			return false;
	}

	public boolean regexIsUnion(String aresta){
		indexQuebra = -1;
		Stack<Character> pilhaChar = new Stack<Character>();
		Stack<Integer> pilhaIndex = new Stack<Integer>();
		for (int i=0; i < aresta.length();i++){
			if(aresta.charAt(i) == ')'){
				pilhaChar.pop();
				pilhaIndex.pop();
			}
				
			if(aresta.charAt(i) == '+'){
				pilhaChar.push(aresta.charAt(i));
				pilhaIndex.push(i);			
			}			
		}
		
		boolean isUnionOfLanguages = false;
		
		if(!pilhaChar.isEmpty()){
			isUnionOfLanguages = true;
			indexQuebra = pilhaIndex.pop();
		}
		
		return isUnionOfLanguages;		
	}
	
	public static void main(String[] args){
		LabForster lab = new LabForster();
		
		
		lab.getAFNDfromRegex("abc+(b+c)*");
		for(int i =0; i<nohs.size(); i++)
			nohs.get(i).printCaminhos();
				
		
	}
}
