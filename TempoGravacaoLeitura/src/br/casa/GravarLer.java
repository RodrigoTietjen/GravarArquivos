package br.casa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class GravarLer {
	
	CyclicBarrier gate;
//								      419430400 / 800 = 524288
	private char[] char400mb = new char[419430400];
	private char[] char4k = new char[4000];
	private char [] char500k = new char[512000];
	
	static long antes = 0;
	static long depois = 0;
	
	public static void main(String[] args) throws IOException, InterruptedException, BrokenBarrierException {
		GravarLer g = new GravarLer();
		g.testa1();
		g.testa64();
		g.gravaVariosSequencial();
	}
	
	public void calculaTotalRecebido(char[] any, String nomeArquivo){
		  new Thread() {
		     
		    @Override
		    public void run() {
			    FileWriter fileWriter;
				try {
					gate.await();
					fileWriter = new FileWriter(nomeArquivo);
					PrintWriter printWriter = new PrintWriter(fileWriter);
				    printWriter.write(any);  
				    printWriter.close();
				    depois = System.currentTimeMillis();
				} catch (IOException | InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
		       
		    }
		  }.start();
		}
	
	public synchronized void testa1() throws InterruptedException, BrokenBarrierException, IOException {
		System.out.println("Gravando um arquivo de 400MB");
		gate = new CyclicBarrier(2);
		calculaTotalRecebido(char400mb, "testando");
		antes = System.currentTimeMillis();
		gate.await();
		wait(10000L);
		System.out.println("Tempo de gravação em Millisegundos: " + (depois - antes) + "\n");
		lerArquivo();
	}
	
	public synchronized void testa64() throws InterruptedException, BrokenBarrierException {
		System.out.println("Gravando 64 arquivos 4k simultâneos");
		gate = new CyclicBarrier(65);
		for (int i = 0; i < 64; i++) {
			calculaTotalRecebido(char4k, "arquivo_" + i);
		}
		antes = System.currentTimeMillis();
		gate.await();
		wait(10000L);
		System.out.println("Tempo de gravação em Millisegundos: " + (depois - antes) + "\n");
	}
	
	public synchronized void gravaVariosSequencial() throws InterruptedException,
	BrokenBarrierException, IOException {
		System.out.println("Gravando 800 arquivos de 500k sequencialmente");
		FileWriter fileWriter;
		antes = System.currentTimeMillis();
		for (int i = 0; i < 800; i++) {
			fileWriter = new FileWriter("arquivo" + i);
			PrintWriter printWriter = new PrintWriter(fileWriter);
		    printWriter.write(char500k);  
		    printWriter.close();
		}
		depois = System.currentTimeMillis();
		System.out.println("Tempo de gravação em Millisegundos: " + (depois - antes) + "\n");
	}
	
	public void lerArquivo() throws IOException {
		System.out.println("Lendo o arquivo de 4k");
		antes = System.currentTimeMillis();
	    FileReader fileReader = new FileReader("testando");
        BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
        String linha = bufferedReader.readLine();
        System.out.println("Tempo de leitura em Millisegundos: " 
        + (System.currentTimeMillis() - antes) + "\n");
        
        bufferedReader.close();
	}
}
