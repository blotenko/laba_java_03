import java.util.Vector;

public class Vinni {
static int countOfBee = 4;
static int countOfSip = 10;
static Vector<Integer> v = new Vector<Integer>();

public static void setMassive(Vector<Integer> vec){
    v = vec;
    System.out.println(v);
}
public static void addToMassive(){
    v.add(1);
}

public static boolean recieveCommand(Massive mass){
    // вызов потока медведя который почистит котелок
    return true;
}

    public static void main(String[] args) {

        Massive mass = new Massive();
       // beeThread beeTh = new beeThread(mass);


        Thread[] beeThread1 = new beeThread[countOfBee];

        Thread bearThread1 = new Thread(new bearThread(mass));
        bearThread1.start();

        for(int i = 0; i<countOfBee;i++)
         {
             beeThread1[i] = new beeThread(mass);
             beeThread1[i].start();
         }

    }
}


class Massive{
    static Vector<Integer> vec = new Vector<Integer>();
    int counter = 0;
    boolean valueSet = true;
    Massive(){
        valueSet = true;
    }
    public synchronized void clear() {
        while(valueSet){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Vinni.v.clear();
        Vinni.setMassive(Vinni.v);
        valueSet = true;
        notify();
    }
    public synchronized void put(Massive mass) {
        while(!valueSet){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (Vinni.v.size() <10) {
            Vinni.addToMassive();
            valueSet = true;
            System.out.println("puting");
            counter++;
        } else if (Vinni.v.size() == 10) {
            valueSet  = false;
            System.out.println("cleaning");
        }
        notify();
    }

}
class beeThread extends Thread {
    private Massive m;

    public beeThread(Massive mass) {
        this.m = mass;
    }

    int threadCount;
    beeThread(){

    }
    public void run(){
        while(m.counter<=96){
            m.put(m);
        }
    }
}


class bearThread implements Runnable{
    private Massive m;

    public bearThread(Massive mass) {
        this.m = mass;

    }
    int threadCount;
    bearThread(){

    }

    public void run(){
        while(m.counter<=101){
            m.clear();
        }
    }
}