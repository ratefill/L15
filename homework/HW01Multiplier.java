package by.epam.l15.homework;

import java.util.Random;

/*Необходимо разработать многопоточное приложение, позволяющее
перемножать квадратные матрицы одного (любого) порядка. При
необходимости для синхронизации использовать только инструменты,
доступные в версии Java 1.4.
Приведите решение задачи, когда количество дочерних потоков не
ограничено.*/

public class HW01Multiplier
{
	private static final int rank = 6;
	private static int threadcount = 8;// must be at least 1

	public static void main(String[] args)
	{
		int[][] matrixA = new int[rank][rank];
		int[][] matrixB = new int[rank][rank];
		int[][] resultAB = new int[rank][rank];//результат

		if (threadcount > rank * rank)
		{
			threadcount = rank * rank;//кол-во потоков не может быть больше минимального вычисления (1 элемент)
		}

		Thread[] threads = new Thread[threadcount];//пул потоков

		int jobSize = rank * rank / threadcount;//кол-во элементов для расчета на один поток (кроме последнего)

		gen(matrixA);//генерация случайных данных 
		gen(matrixB);

		print(matrixA);
		System.out.println();
		print(matrixB);
		System.out.println();

		for (int i = 0; i < threadcount - 1; i++)
		{
			//стартуем потоки на выполнение
			threads[i] = new Thread(
					new MultiplierRangeMatrix(i + 1, rank, i * jobSize, (i + 1) * jobSize, matrixA, matrixB, resultAB));
			threads[i].start();
		}

		//последний поток берет себе jobSize плюс оставшиеся элементы
		threads[threadcount - 1] = new Thread(new MultiplierRangeMatrix(threadcount, rank, (threadcount - 1) * jobSize,
				rank * rank, matrixA, matrixB, resultAB));
		threads[threadcount - 1].start();

		try
		{
			for (Thread t : threads)
			{
				t.join();
				//ждем все потоки
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		print(resultAB);//печатаем результат

	}

	public static void gen(int[][] matrix)
	{
		Random randomGenerator = new Random();
		for (int i = 0; i < rank; i++)
		{
			for (int j = 0; j < rank; j++)
			{
				matrix[i][j] = randomGenerator.nextInt(5);
			}
		}
	}

	public static void print(int[][] matrix)
	{
		for (int[] i : matrix)
		{
			for (int j : i)
			{
				System.out.print(j + "\t");
			}
			System.out.println();
		}
	}
}

class MultiplierRangeMatrix implements Runnable
{
	private int threadid;
	private int rank;
	private int startRow;
	private int finishRow;
	private int[][] matrixA;
	private int[][] matrixB;
	private int[][] matrixAB;

	// представим матрицы в виде одномерного массива
	public MultiplierRangeMatrix(int id, int rank, int startRow, int finishRow, int[][] matrixA, int[][] matrixB,
			int[][] matrixAB)
	{
		super();
		this.threadid = id;
		this.rank = rank;
		this.startRow = startRow;
		this.finishRow = finishRow;
		this.matrixA = matrixA;
		this.matrixB = matrixB;
		this.matrixAB = matrixAB;
	}

	@Override
	public void run()
	{
		System.out.println("Thread #" + threadid + " get job size from " + startRow + " to " + finishRow + "...");
		int result;
		int row;
		int column;
		for (int i = startRow; i < finishRow; i++)
		{
			result = 0;
			row = i / rank;
			column = i % rank;
			for (int j = 0; j < rank; j++)
			{
				result += matrixA[row][j] * matrixB[j][column];
			}
			matrixAB[row][column] = result;
			System.out.println("Thread #" + threadid + " matrix[" + row + "][" + column + "]=" + matrixAB[row][column]);
		}
	}
}