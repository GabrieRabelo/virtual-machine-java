import enums.Interrupts;
import enums.Opcode;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Console extends Thread {

    private LinkedList<ChamadaConsole> pedidos;
    private Memory memory;
    private CPU cpu;
    private Scanner in;
    private Semaphore appSemaforo;
    private Integer appEntrada;

    public Console(LinkedList<ChamadaConsole> pedidos, Memory memory, CPU cpu, Semaphore appSemaforo) {
        in = new Scanner(System.in);
        this.pedidos = pedidos;
        this.memory = memory;
        this.cpu = cpu;
        this.appSemaforo = appSemaforo;
    }

    public void setEntrada(int entrada ){
        appEntrada = entrada;
    }

    public int translateMemory(int[] allocatedPages, int address) {
        return (allocatedPages[(address / 16)] * 16) + (address % 16);
    }

    public void run() {
        while (true) {
            if (pedidos.isEmpty()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            ChamadaConsole chamadaConsole = pedidos.remove(0);
            String type = chamadaConsole.getType();
            if (type.equals("IN")) {
                System.out.println("esperando in");
               /* int arg = in.nextInt();
                in.nextLine();*/
                appSemaforo.release();
                System.out.println("in foi dado");
                int position = translateMemory(chamadaConsole.getAllocatedPages(), chamadaConsole.getMemoryAddress());
                memory.mem[position] = new Word(Opcode.DADO, -1, -1, arg);
                cpu.callIOInterrupt();
            } else if (type.equals("OUT")) {
                System.out.println("esperando out");
                int position = translateMemory(chamadaConsole.getAllocatedPages(), chamadaConsole.getMemoryAddress());
                System.out.println(memory.mem[position]);
                cpu.callIOInterrupt();
            }
        }
    }
}