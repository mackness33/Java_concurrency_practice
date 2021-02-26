// CSD feb 2015 Juansa Sendra

public class Pool3 extends Pool {   //max capacity
    protected int kidsInPool;
    protected int instructorsInPool;
    protected int max_cap;
    protected int max_ki;

    @Override
    public void init(int ki, int cap) {
      kidsInPool = 0;
      instructorsInPool = 0;
      max_ki = ki;
      max_cap = cap;
    }

    @Override
    public synchronized void kidSwims() throws InterruptedException {
      System.out.println("kid want to swim");
      System.out.println("kids presents: " + kidsInPool);
      System.out.println("instructors presents: " + instructorsInPool);

      while ( instructorsInPool <= 0 && max_ki <= kidsInPool/instructorsInPool){
        log.waitingToSwim();
        wait();
      }

      // update state
      kidsInPool++;
      // awake threads if needed

      log.swimming();
    }

    @Override
    public synchronized void kidRests() {
      System.out.println("kid want to rest");
      kidsInPool--;

      if (kidsInPool <= 0)
        notify();   // let the last instructor rest if he was waiting

      log.resting();
    }

    @Override
    public synchronized void instructorSwims() {
      System.out.println("instructor want to swim");
      instructorsInPool++;

      if (kidsInPool <= 0)
        notifyAll();        // let all the kids waiting to start swimming

      log.swimming();
    }

    @Override
    public synchronized void instructorRests() throws InterruptedException {
      System.out.println("instructor want to rest");
      System.out.println("instructors presents: " + instructorsInPool);
      System.out.println("kids presents: " + kidsInPool);
      while ( kidsInPool > 0 && instructorsInPool <= 1 && max_ki <= kidsInPool/instructorsInPool){
        log.waitingToRest();
        wait();
      }

      // update state
      instructorsInPool--;

      // awake threads if needed

      log.resting();
    }
}
