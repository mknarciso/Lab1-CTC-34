import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LabForster {
	
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
			
			//Para cada caminho dentro do noh
			int i=0;
			while(i<noh.getCaminhos().size()){
				Caminho caminho = noh.getCaminhos().get(i);
				String aresta = caminho.getArco();
				//System.out.println(noh.getName() + "->" + caminho.getDestino().getName() + ": " + aresta);

				//	2º passo - Quebra de Uniões
				if(regexIsUnion(aresta)){
					String lang1, lang2;
					Noh nohDestino = caminho.getDestino();
					noh.removeCaminho(caminho);
					int indexQuebra = getIndexQuebraUniao(aresta);
					lang1 = aresta.substring(0,indexQuebra);
					lang2 = aresta.substring(indexQuebra+1);
					noh.addCaminho(nohDestino, lang1);
					noh.addCaminho(nohDestino, lang2);
				}
			
				//3º passo - Conferir fecho de Kleene
				else if(regexIsKleene(aresta)){
					String arestaSemAsterisco = aresta.substring(0,aresta.length()-1);
					Noh nohIntermediario = new Noh(indexNohs++);
					Noh nohDestino = caminho.getDestino();
					noh.removeCaminho(caminho);
					noh.addCaminho(nohIntermediario, "&");
					nohIntermediario.addCaminho(nohDestino, "&");
					nohIntermediario.addCaminho(nohIntermediario, arestaSemAsterisco);
					nohs.add(nohIntermediario);
				}
				
				//4º passo - remover parênteses, se houver
				else if(regexIsInParenthesis(aresta)){
					String novoArco = aresta.substring(1,aresta.length()-1);
					caminho.changeArco(novoArco);
				}	
				
				//5º passo - Quebra de Concatenações
				else if (aresta.length() > 1){
					String lang1, lang2;
					Noh nohIntermediario = new Noh(indexNohs++);
					Noh nohDestino = caminho.getDestino();
					noh.removeCaminho(caminho);
					int index = getIndexQuebraConcat(aresta);
					lang1 = aresta.substring(0,index);
					lang2 = aresta.substring(index,aresta.length());
					noh.addCaminho(nohIntermediario, lang1);
					nohIntermediario.addCaminho(nohDestino, lang2);
					nohs.add(nohIntermediario);
				}
				
				if(!caminho.arcoTemSimbolo())
					i++;
			}
			
			if(!noh.temArcoComSimbolo() || noh.isNohFinal())
				j++;
		}	
	}
	
	
	public int getIndexQuebraUniao(String aresta){
		int indexQuebra = -1;
		Stack<Character> pilhaChar = new Stack<Character>();
		Stack<Integer> pilhaIndex = new Stack<Integer>();
		for (int i=0; i < aresta.length();i++){
			if(aresta.charAt(i) == ')'){
				char topo = pilhaChar.pop();
				while(topo!='(')
				{
					topo = pilhaChar.pop();
					pilhaIndex.pop();
				}	
			}	
			if(aresta.charAt(i) == '+' || aresta.charAt(i) == '(' ){
				pilhaChar.push(aresta.charAt(i));
				pilhaIndex.push(i);			
			}			
		}		
		if(!pilhaChar.isEmpty())
			indexQuebra = pilhaIndex.pop();
		return indexQuebra;	
	}
	
	public int getIndexQuebraConcat(String aresta){
		int i=1;
		boolean encontrouQuebra = false;
		while(!encontrouQuebra){
			if(aresta.charAt(i)=='('){
				if(aresta.charAt(i-1) != '(')
					encontrouQuebra = true;
				else 
					i++;
			}
			else if (aresta.charAt(i) == '*'){
				if(aresta.charAt(i-1) != ')'){
					encontrouQuebra = true;
					i++;
				}
			}
			else if (aresta.charAt(i) == '+')
				i++;			
			else if (aresta.charAt(i) == ')'){
				if(aresta.charAt(i-1) != '(' && aresta.charAt(i-1) != '+' ){
					encontrouQuebra = true;
					i++;
				}
			}
			else{
				if(aresta.charAt(i-1) != '(' && aresta.charAt(i-1) != '+' )
					encontrouQuebra = true;
				else
					i++;
			}	
		}
		
		if(i<aresta.length() && aresta.charAt(i)=='*' && aresta.charAt(i-1)==')')
			i++;	
		
		return i;
	}
	
	public boolean regexIsInParenthesis(String aresta){
		int tamanho = aresta.length();
		if(aresta.charAt(0) == '(' && aresta.charAt(tamanho-1) == ')')
			return true;
		else 
			return false;
	}
	
	public boolean regexIsKleene(String aresta){
		int tamanho = aresta.length();
		boolean isKleene = true;
		if(aresta.charAt(tamanho-1) == '*'){
			if(tamanho==2)
				isKleene = true;
			else if(aresta.charAt(tamanho-2)==')' && aresta.charAt(0)=='('  ){
				System.out.println("entrou");
				Stack<Character> pilhaChar = new Stack<Character>();
				for (int i=1; i < aresta.length()-2;i++){
					if(aresta.charAt(i) == '(')
						pilhaChar.push(aresta.charAt(i));	
					if(aresta.charAt(i) == ')'){
						if(aresta.charAt(i+1) != '*')
							pilhaChar.pop();
						else
							pilhaChar.push(aresta.charAt(i));	
					}
					if(aresta.charAt(i) == '*'){
						if(aresta.charAt(i-1)==')'){
							pilhaChar.pop();
							if(pilhaChar.isEmpty())
								isKleene = false;
							else
								pilhaChar.pop();
						}
					}
				}
				if(!pilhaChar.isEmpty())
					isKleene = false;
			}
			else
				isKleene = false;
		}
		else
			isKleene = false;
		return isKleene;
	}
	
	public boolean regexIsConcat(String aresta){
		boolean isConcat = false;
		Stack<Character> pilhaChar = new Stack<Character>();
		for (int i=0; i < aresta.length();i++){
			if(aresta.charAt(i) == '(' || aresta.charAt(i)==')' || aresta.charAt(i)=='+')
				pilhaChar.push(aresta.charAt(i));	
			else if(aresta.charAt(i) == '*'){
				if(!pilhaChar.isEmpty() && pilhaChar.peek()!='+'){
					pilhaChar.pop();
					pilhaChar.pop();
					pilhaChar.pop();
				}
			}
		}
		
		if(pilhaChar.isEmpty())
			isConcat = true;
		
		return isConcat;
	}

	public boolean regexIsUnion(String aresta){
		Stack<Character> pilhaChar = new Stack<Character>();
		for (int i=0; i < aresta.length();i++){
			//System.out.println(i);
			if(aresta.charAt(i) == ')'){
				char topo = pilhaChar.pop();
				while(topo!='(')
					topo = pilhaChar.pop();
			}	
			if(aresta.charAt(i) == '+' || aresta.charAt(i) == '(' )
				pilhaChar.push(aresta.charAt(i));						
		}
		
		return !pilhaChar.isEmpty();	
	}
	
	public static void main(String[] args){
		LabForster lab = new LabForster();	
		 //Inserir aqui a expressão desejada
		lab.getAFNDfromRegex("A*B*C*");
		for(int i =0; i<nohs.size(); i++)
			nohs.get(i).printCaminhos();
	
	}
}
