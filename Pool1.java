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

      if (instructorsInPool <= 0)
        this.checks("instructors not in the pool");

      // update state
      kidsInPool++;

      log.swimming();
    }

    @Override
    public synchronized void kidRests() {
      kidsInPool--;

      notify();   // notify the last instructor waiting to rest

      log.resting();
    }

    @Override
    public synchronized void instructorSwims() {
      instructorsInPool++;

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

      if (kidsInPool > 0 && instructorsInPool <= 1)
        this.checks("kids still in the pool");

      // update state
      instructorsInPool--;

      log.resting();
    }

    private synchronized void checks(String err) throws InterruptedException {
        System.out.println("kids presents: " + kidsInPool);
        System.out.println("instructors presents: " + instructorsInPool);

        System.out.println("\n\n WARNING:" + err + "\n\n");
    }
}
