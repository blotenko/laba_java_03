import javax.swing.*;
import java.util.concurrent.locks.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

 class Customer implements Runnable {
    private static int id = 1;

    private String customerName;

    private BarberShop barberShop;

    public Customer(BarberShop bShop, String name) {
        customerName = name + id;
        barberShop = bShop;
        id++;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public void run() {
        barberShop.sitInWorkspace(this);
    }
}

 class BarberShop {
    public static final int NUM_CHAIRS = 3;

    public static final int NUM_WORKSPACES = 1;

    public static final int WORK_TIME = 10000;

    private Customer barberWorkspace;

    private enum BarberState {
        SLEEP, WORK, NOTHING
    }

    BarberState stateFlag;

    private int customersCount;


    private int leftCustomersCount;

    // Парикмахер
    private Barber barberMan;


    private Queue<Customer> customerList = new LinkedList<Customer>();

    public BarberShop() {
        customersCount = 0;
        leftCustomersCount = 0;

        barberMan = new Barber();
    }


    public Queue<Customer> getCustomerList() {
        return customerList;
    }


    public Barber getBarber() {
        return barberMan;
    }

    private void sitInWaitingRoom(Customer customer) {
        if (customerList.size() < NUM_CHAIRS) {
            customerList.add(customer);
            System.out.println(customer.getCustomerName() + " занял место в приемной\n");
        } else {
            leftCustomersCount++;
            System.out.println(customer.getCustomerName() + " ушел из парикмахерской, так как нет мест, количество необслужанных клиентов: " + leftCustomersCount + "\n");
        }
    }

    public synchronized void sitInWorkspace(Customer customer) {
        if (checkBarber(customer) == BarberState.SLEEP) {
            System.out.println(customer.getCustomerName() + " разбудил парикмахера и сел на стрижку\n");
            barberWorkspace = customer;
            stateFlag = BarberState.WORK;
        } else {
            sitInWaitingRoom(customer);
        }

        try {
            notify();
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public BarberState checkBarber(Customer customer) {
        System.out.print(customer.getCustomerName() + " проверяет состояние парикмахера:");

        if (stateFlag == BarberState.SLEEP) {
            System.out.println(" парикмахер спит\n");
        } else {
            System.out.println(" парикмахер занят работой\n");
        }

        return stateFlag;
    }

    public synchronized boolean checkCustomers() {
        System.out.printf("Парикмахер проверяет наличие клиентов: В очереди %d из %d\n\n", customerList.size(), NUM_CHAIRS);
        return !customerList.isEmpty();
    }

    //---------------------------------------------
    public synchronized void work() {
        while (isWorkspaceEmpty()) {
            try {
                sleep();
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (!isWorkspaceEmpty()) {
            if (stateFlag != BarberState.WORK)
                stateFlag = BarberState.WORK;

            System.out.printf("Парикмахер стрижет посетителя: %s\n\n", barberWorkspace.getCustomerName());

            try {
                wait(WORK_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Парикмахер закончил стричь посетителя: %s\n\n", barberWorkspace.getCustomerName());
            customersCount++;
            stateFlag = BarberState.NOTHING;
            resetBarberWorkspace();
            callCustomer();
        }
    }

    //---------------------------------------------
    public synchronized void sleep() {
        if (stateFlag != BarberState.SLEEP) {
            stateFlag = BarberState.SLEEP;

            System.out.println("Парикмахер спит\n");
        }
    }

    //---------------------------------------------
    private boolean isWorkspaceEmpty() {
        return barberWorkspace == null;
    }

    //---------------------------------------------
    private void resetBarberWorkspace() {
        barberWorkspace = null;
    }

    private synchronized void callCustomer() {
        if (checkCustomers()) {
            barberWorkspace = customerList.poll();
        }
    }

    public class Barber implements Runnable {
        public Barber() {
            stateFlag = BarberState.NOTHING;
        }

        @Override
        public void run() {
            while (true) {
                work();
            }
        }

    }

}

public class Main extends Thread{
    public static void main(String[] arg) throws InterruptedException {
        BarberShop barberShopSim = new BarberShop();

        Thread barberThread = new Thread(barberShopSim.getBarber());
        barberThread.start();

        while(true) {
            Thread customerThread = new Thread(new Customer(barberShopSim, "Посетитель "));
            customerThread.start();

            try {
                Thread.sleep( (int)(Math.random()*10000) );
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


