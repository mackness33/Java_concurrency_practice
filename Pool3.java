// CSD feb 2015 Juansa Sendra

public class Pool3 extends Pool {   //max capacity
  private int kidsInPool;
  private int instructorsInPool;
  private int max_cap;
  protected float max_ki;

  @Override
  public void init(int ki, int cap) {
    kidsInPool = 0;
    instructorsInPool = 0;
    max_cap = cap;
    max_ki = (float) ki;
  }

  @Override
  public synchronized void kidSwims() throws InterruptedException {
    // if there's no instructor in the pool OR
    //    there are too many kids in the pool OR
    //    there are too many swimmers in the pool
    // then wait
    while (
      instructorsInPool <= 0 ||
      max_ki < this.getCurrentKI(kidsInPool+1, instructorsInPool) ||
      this.getCurrentCapacity() >= max_cap
    ){
      log.waitingToSwim();
      wait();
    }

    // checks
    if ( this.getCurrentKI(kidsInPool+1, instructorsInPool) > max_ki)
      this.checks("too many kids pool");

    if ( this.getCurrentCapacity() >= max_cap)
      this.checks("too many swimmers");

    // update state
    kidsInPool++;

    log.swimming();
  }

  @Override
  public synchronized void kidRests() throws InterruptedException  {
    kidsInPool--;

    notify();   // notify the last instructor waiting to rest

    log.resting();
  }

  @Override
  public synchronized void instructorSwims()  throws InterruptedException  {
    // if there'are too many swimmers in the pool then wait
    while ( this.getCurrentCapacity() >= max_cap ){
      log.waitingToSwim();
      wait();
    }

    // checks
    if ( this.getCurrentCapacity() >= max_cap )
      this.checks("Capacity pool exceeded");

    instructorsInPool++;

    notifyAll();        // notify all the kids waiting to swim

    log.swimming();
  }

  @Override
  public synchronized void instructorRests() throws InterruptedException {
    // if there's still kids in the pool AND there's only one instructor OR
    //    there's too many kids in the pool
    // then wait
    while ( (kidsInPool > 0 && instructorsInPool <= 1) || max_ki < this.getCurrentKI(kidsInPool, instructorsInPool-1) ){
      log.waitingToRest();
      wait();
    }

    // checks
    if (instructorsInPool == 0)
      this.checks("instructor already resting");

    if (this.getCurrentKI(kidsInPool, instructorsInPool-1) > max_ki)
      this.checks("too many kids");

    if (kidsInPool > 0 && instructorsInPool <= 1)
      this.checks("kids still in the pool");

    // update state
    instructorsInPool--;

    log.resting();
  }

  private synchronized int getCurrentCapacity() throws InterruptedException { return kidsInPool + instructorsInPool; }
  private synchronized float getCurrentKI(int kids, int insts) throws InterruptedException { return (insts == 0) ? 0 : (float)kids/insts; }

  // information about the error occured
  private synchronized void checks(String err) throws InterruptedException {
      System.out.println("kids presents: " + kidsInPool);
      System.out.println("instructors presents: " + instructorsInPool);
      System.out.println("ki: " + this.getCurrentKI(kidsInPool, instructorsInPool));
      System.out.println("cap: " + this.getCurrentCapacity());

      System.out.println("\n\n WARNING:" + err + "\n\n");
  }
}
