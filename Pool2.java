// CSD feb 2015 Juansa Sendra

public class Pool2 extends Pool{ //max kids/instructor
  private int kidsInPool;
  private int instructorsInPool;
  private float max_ki;

  @Override
  public void init(int ki, int cap) {
    kidsInPool = 0;
    instructorsInPool = 0;
    max_ki = (float) ki;
  }

  @Override
  public synchronized void kidSwims() throws InterruptedException {
    // if there's no instructor in the pool OR
    //    there's too many kids in the pool
    // then wait
    while ( instructorsInPool <= 0 || (instructorsInPool == 0) ? true : max_ki < ((float) (kidsInPool+1)/instructorsInPool)){
      log.waitingToSwim();
      wait();
    }

    if (((float) (kidsInPool+1)/instructorsInPool) > max_ki)
      this.checks("too many kids pool");

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
    // if there's still kids in the pool AND there's only one instructor OR
    //    there's too many kids in the pool
    // then wait
    while ( (kidsInPool > 0 && instructorsInPool <= 1) || ((instructorsInPool == 0) ? false : (instructorsInPool-1 == 0) ? false : max_ki < ((float) kidsInPool/(instructorsInPool-1))) ){
      log.waitingToRest();
      wait();
    }

    // checks
    if (instructorsInPool == 0)
      this.checks("instructor already resting");

    if (((instructorsInPool-1 == 0) ? 0 : (float) kidsInPool/(instructorsInPool-1)) > max_ki)
      this.checks("too many kids");

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
      System.out.println("ki: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));

      System.out.println("\n\n WARNING:" + err + "\n\n");
  }

}
