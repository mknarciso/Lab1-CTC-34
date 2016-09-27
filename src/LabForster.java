import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LabForster {
	
	List<Noh> nohs = new ArrayList<Noh>();
	
	public void getAFNDfromRegex(String regex){
		// 1� passo
		int indexNohs = 0;
		Noh nohInicial = new Noh(indexNohs++);
		Noh nohFinal = new Noh(indexNohs++);
		nohFinal.setAceitacao();
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

				//	2� passo - Quebra de Uni�es
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
			
				//3� passo - Conferir fecho de Kleene
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
				
				//4� passo - remover par�nteses, se houver
				else if(regexIsInParenthesis(aresta)){
					String novoArco = aresta.substring(1,aresta.length()-1);
					caminho.changeArco(novoArco);
				}	
				
				//5� passo - Quebra de Concatena��es
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
				pilhaIndex.pop();
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
				//System.out.println("entrou");
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
	
	public void printEClosure(){
	    int j=0;
	    System.out.println("e-Closures:");
	    while(j<nohs.size()){
	        System.out.print(nohs.get(j).getName()+": ");
	        int i=0;
	        while(i<nohs.get(j).getEClosure().size()){
	            System.out.print(nohs.get(j).getEClosure().get(i).getName() + " ;");
	            i++;
	        }
	        System.out.print("\n");
	        j++;
	    }
	}
	
	public void printAllCaminhos(){
	   for(int i =0; i<nohs.size(); i++)
			nohs.get(i).printCaminhos();
	}
	
	public Noh getFirstNoh(){
	    Noh result = new Noh(-1);
	    for(int j=0;j<nohs.size();j++){
	        if(nohs.get(j).getName()==0)
	           result = nohs.get(j);
	    }
	    return result;
	}
	
	public void printNohsList(List<Noh> lista){
	    System.out.println("Estados Finais:");
	    for(int j=0;j<lista.size();j++){
	        System.out.print(lista.get(j).getName()+"; ");
	    } 
	    System.out.print("\n");
	}
	public void prettyPrintNohsList(List<Noh> lista){
	    boolean aceita = false;
	    System.out.print("Estados Finais: ");
	    List<Integer> numberList = new ArrayList<Integer>();
	    for(int j=0;j<lista.size();j++){
	        if(!numberList.contains(lista.get(j).getName()))
    	        numberList.add(lista.get(j).getName());
    	    if(lista.get(j).isAceitacao())
    	       aceita = true;
	    } 
	    for(int j=0;j<numberList.size();j++){
	        System.out.print(numberList.get(j)+"; ");
	    } 
	    if(aceita)
	       System.out.print(" -> Cadeia Aceita!\n");
	    else
	       System.out.print(" -> Cadeia Não Aceita!\n");
	       
	    
	}
	
	public void doAllTests(){
        System.out.println("Test: ab :");
        prettyPrintNohsList(getFirstNoh().testString("ab"));
        System.out.println("Test: abb :");
        prettyPrintNohsList(getFirstNoh().testString("abb"));
        System.out.println("Test: bba :");
        prettyPrintNohsList(getFirstNoh().testString("bba"));
        System.out.println("Test: abba :");
        prettyPrintNohsList(getFirstNoh().testString("abba"));
	}
	
	public static void main(String[] args){
		LabForster ex1 = new LabForster();
		System.out.println("\nRegex: ab+(b+c)*");
		ex1.getAFNDfromRegex("ab+(b+c)*");
        ex1.printAllCaminhos();
		//ex1.printEClosure();
		// Teste 1
		LabForster bat1 = new LabForster();
		System.out.println("\nRegex: (a+b)*bb(b+a)*");
		bat1.getAFNDfromRegex("(a+b)*bb(b+a)*");
        bat1.printAllCaminhos();
        bat1.doAllTests();
		//bat1.printEClosure();	
		// Teste 2
		LabForster bat2 = new LabForster();
		System.out.println("\nRegex: (a(b+c))*");
		bat2.getAFNDfromRegex("(a(b+c))*");
        bat2.printAllCaminhos();
        bat2.doAllTests();
		//bat2.printEClosure();	
		// Teste 3
		LabForster bat3 = new LabForster();
		System.out.println("\nRegex: a*b+b*a");
		bat3.getAFNDfromRegex("a*b+b*a");
        bat3.printAllCaminhos();
        bat3.doAllTests();
		//bat3.printEClosure();	
		// Teste 4
		LabForster bat4 = new LabForster();
		System.out.println("\nRegex: a*b*c*");
		bat4.getAFNDfromRegex("a*b*c*");
        bat4.printAllCaminhos();
        bat4.doAllTests();
		//bat4.printEClosure();	
	}
}
