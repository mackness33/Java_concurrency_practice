// CSD feb 2013 Juansa Sendra

public class Pool4 extends Pool { //kids cannot enter if there are instructors waiting to exit
  private int kidsInPool;
  private int instructorsInPool;
  private int max_cap;
  private float max_ki;
  private int instructorsWaitingToRest;


  @Override
  public void init(int ki, int cap) {
    kidsInPool = 0;
    instructorsInPool = 0;
    max_ki = (float) ki;
    max_cap = cap;
    instructorsWaitingToRest = 0;
  }


  private synchronized int getCurrentCapacity() throws InterruptedException { return kidsInPool + instructorsInPool; }
  private synchronized float getCurrentKI(int kids, int insts) throws InterruptedException { return (insts == 0) ? 0 : (float)kids/insts; }


  @Override
  public synchronized void kidSwims() throws InterruptedException {
    // if there's no instructor in the pool OR
    //    there are too many kids in the pool OR
    //    there are too many swimmers in the pool OR
    //    there are instructor waiting to rest
    // then wait
    while (
      instructorsInPool <= 0 ||
      max_ki < this.getCurrentKI(kidsInPool+1, instructorsInPool) ||
      this.getCurrentCapacity() >= max_cap ||
      instructorsWaitingToRest > 0
    ){
      log.waitingToSwim();
      wait();
    }

    // checks
    if (instructorsWaitingToRest > 0)
      this.checks("instructor in waiting to rest");

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
    // update state
    kidsInPool--;

    // awake threads
    notifyAll();   // notify instructors waiting to rest and kids waiting to swim

    log.resting();
  }


  @Override
  public synchronized void instructorSwims()  throws InterruptedException  {
    // if there's too many kids in the pool then wait
    while ( this.getCurrentCapacity() >= max_cap ){
      log.waitingToSwim();
      wait();
    }

    // checks
    if ( this.getCurrentCapacity() >= max_cap )
      this.checks("Capacity pool exceeded");

    // update state
    instructorsInPool++;

    // awake threads
    notifyAll();        // notify all the kids waiting to swim

    log.swimming();
  }


  @Override
  public synchronized void instructorRests() throws InterruptedException {
    // update state
    instructorsWaitingToRest++;

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
    instructorsWaitingToRest--;

    // awake threads
    notifyAll();        // notify all the swimmers waiting to swim

    log.resting();
  }


  // information about the error occured
  private synchronized void checks(String err) throws InterruptedException {
      System.out.println("kids presents: " + kidsInPool);
      System.out.println("instructors presents: " + instructorsInPool);
      System.out.println("ki: " + this.getCurrentKI(kidsInPool, instructorsInPool));
      System.out.println("cap: " + this.getCurrentCapacity());
      System.out.println("instructors waiting to rest: " + instructorsWaitingToRest);

      System.out.println("\n\n WARNING:" + err + "\n\n");
  }
}
