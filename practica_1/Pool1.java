// CSD feb 2015 Juansa Sendra

public class Pool1 extends Pool {   //no kids alone
    private int kidsInPool;
    private int instructorsInPool;


    @Override
    public void init(int ki, int cap) {
      kidsInPool = 0;
      instructorsInPool = 0;
    }


    @Override
    public synchronized void kidSwims() throws InterruptedException {
      // if there's no instructor in the pool then wait
      while ( instructorsInPool <= 0 ){
        log.waitingToSwim();
        wait();
      }

      // checks
      if (instructorsInPool <= 0)
        this.checks("instructors not in the pool");

      // update state
      kidsInPool++;

      log.swimming();
    }


    @Override
    public synchronized void kidRests() {
      // update state
      kidsInPool--;

      // awake thread
      notify();   // notify the last instructor waiting to rest

      log.resting();
    }


    @Override
    public synchronized void instructorSwims() {
      // update state
      instructorsInPool++;

      // awake threads
      notifyAll();        // notify all the kids waiting to swim

      log.swimming();
    }


    @Override
    public synchronized void instructorRests() throws InterruptedException {
      // if there's still kids in the pool and there's only one instructor than wait
      while ( kidsInPool > 0 && instructorsInPool <= 1 ){
        log.waitingToRest();
        wait();
      }

      // checks
      if (kidsInPool > 0 && instructorsInPool <= 1)
        this.checks("kids still in the pool");

      // update state
      instructorsInPool--;

      log.resting();
    }


    // information about the error occured
    private synchronized void checks(String err) throws InterruptedException {
        System.out.println("kids presents: " + kidsInPool);
        System.out.println("instructors presents: " + instructorsInPool);

        System.out.println("\n\n WARNING:" + err + "\n\n");
    }
}
