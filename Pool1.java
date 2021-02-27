// CSD feb 2015 Juansa Sendra

public class Pool1 extends Pool {   //no kids alone
    protected int kidsInPool;
    protected int instructorsInPool;

    @Override
    public void init(int ki, int cap) {
      kidsInPool = 0;
      instructorsInPool = 0;
    }

    @Override
    public synchronized void kidSwims() throws InterruptedException {
      while ( instructorsInPool <= 0 ){
        log.waitingToSwim();
        wait();
      }


      if (instructorsInPool <= 0)
        this.checks("instructors not in the pool");

      // update state
      kidsInPool++;
      // awake threads if needed

      log.swimming();
    }

    @Override
    public synchronized void kidRests() {
      kidsInPool--;

      notify();   // let the last instructor rest if he was waiting

      log.resting();
    }

    @Override
    public synchronized void instructorSwims() {
      instructorsInPool++;

      notifyAll();        // let all the kids waiting to start swimming

      log.swimming();
    }

    @Override
    public synchronized void instructorRests() throws InterruptedException {
      while ( kidsInPool > 0 && instructorsInPool <= 1 ){
        log.waitingToRest();
        wait();
      }

      if (kidsInPool > 0 && instructorsInPool <= 1)
        this.checks("kids still in the pool");

      // update state
      instructorsInPool--;

      // awake threads if needed

      log.resting();
    }

    public synchronized void checks(String err) throws InterruptedException {
        System.out.println("kids presents: " + kidsInPool);
        System.out.println("instructors presents: " + instructorsInPool);

        System.out.println("\n\n WARNING:" + err + "\n\n");
    }
}
