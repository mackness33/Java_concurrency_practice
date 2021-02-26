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
      System.out.println("kid want to swim");
      System.out.println("kids presents: " + kidsInPool);
      System.out.println("instructors presents: " + instructorsInPool);
      while ( instructorsInPool <= 0 ){
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
      notifyAll();
      log.resting();
    }

    @Override
    public synchronized void instructorSwims() {
      System.out.println("instructor want to swim");
      instructorsInPool++;
      notifyAll();
      log.swimming();
    }

    @Override
    public synchronized void instructorRests() throws InterruptedException {
      System.out.println("instructor want to rest");
      System.out.println("instructors presents: " + instructorsInPool);
      System.out.println("kids presents: " + kidsInPool);
      while ( kidsInPool > 0 && instructorsInPool <= 1 ){
        log.waitingToRest();
        wait();
      }

      // update state
      instructorsInPool--;

      // awake threads if needed

      log.resting();
    }
}
