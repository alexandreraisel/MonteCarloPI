import java.util.stream.Stream;
import java.util.Arrays;
import java.time.LocalTime;

public class PIMC {
	private final static Object lock = new Object(); // evita problemas de concorrência entre thread
	private final static int QuantPontos = 2000000000;
	private static int total = 0; // valor acumulado

	//estrutura Stream
	private static Stream<Integer> processar(int nunThread) {
		Integer[] Pontos = new Integer[nunThread]; //cria uma coleção Pontos de tamanho especificado (no caso paralelo 8).
		Arrays.fill(Pontos, QuantPontos / nunThread); //Atribui o valor do tipo de dados a cada elemento.
		if (nunThread > 1)
			return Arrays.stream(Pontos).parallel(); //chama o método de paralelismo
		return Arrays.stream(Pontos).sequential(); //método sequencial
	}

	public static void main(String[] args) {
		System.out.println("-------\nSEQUENCIAL\n-------");
		chamaPI(processar(1));

		System.out.println("-------\nPARALELO\n-------");
		chamaPI(processar(8));
	}

	public static void chamaPI(Stream<Integer> stream) {
		long timeInicial = System.currentTimeMillis();
		total = 0;
		stream.forEach(s -> {
			System.out.println(LocalTime.now() + " - QuantPontos: " + s + " - thread: " + Thread.currentThread().getName());
			int n = 0;
			for (int i = 0; i < s; ++i) {
				double x = Math.random();
				double y = Math.random();
				if (x * x + y * y <= 1) { //tamanho do quadrado: 1 x 1
					n++; // conta os pontos acertados
				}
			}
			synchronized (lock) { // proteção de condição de corrida
				total += n;
				System.out.println("PI = " + total * 4.0 / QuantPontos);
			}
		});
		long timeFinal = System.currentTimeMillis();
		System.out.println("--------------------------------------");
		System.out.println((timeFinal - timeInicial) + " ms");
		System.out.println("PI = " + total * 4.0 / QuantPontos);
		System.out.println("DIFERENÇA EXATA DE PI: " + ((total * 4.0 / QuantPontos) - Math.PI));
		System.out.println("ERRO: " + ((total * 4.0 / QuantPontos) - Math.PI) / Math.PI * 100 + " %");
	}
}
