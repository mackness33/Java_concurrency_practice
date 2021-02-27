// CSD feb 2013 Juansa Sendra

public class Pool4 extends Pool { //kids cannot enter if there are instructors waiting to exit
  protected int kidsInPool;
  protected int instructorsInPool;
  protected int max_cap;
  protected int instructorsWaitingToRest;
  protected float max_ki;
  protected int cur_cap;

  @Override
  public void init(int ki, int cap) {
    kidsInPool = 0;
    instructorsInPool = 0;
    cur_cap = 0;
    max_ki = (float) ki;
    instructorsWaitingToRest = 0;
    max_cap = cap;
  }

  @Override
  public synchronized void kidSwims() throws InterruptedException {

    while ( instructorsInPool <= 0 || (instructorsInPool == 0) ? true : max_ki < ((float) (kidsInPool+1)/instructorsInPool) || cur_cap >= max_cap || instructorsWaitingToRest > 0){
      log.waitingToSwim();
      wait();
    }

    if (instructorsWaitingToRest > 0)
      this.checks("instructor in waiting to rest");

    if (((float) (kidsInPool+1)/instructorsInPool) > max_ki)
      this.checks("too many kids pool");

    if (cur_cap >= max_cap)
      this.checks("too many swimmers");

    // update state
    kidsInPool++;
    cur_cap++;

    // awake threads if needed

    log.swimming();
  }

  @Override
  public synchronized void kidRests() throws InterruptedException  {
    kidsInPool--;
    cur_cap--;

    notifyAll();   // notify instructors waiting to rest and kids waiting to swim

    log.resting();
  }

  @Override
  public synchronized void instructorSwims()  throws InterruptedException  {
    while ( cur_cap >= max_cap ){
      log.waitingToSwim();
      wait();
    }

    if ( cur_cap >= max_cap )
      this.checks("Capacity pool exceeded");

    instructorsInPool++;
    cur_cap++;

    notifyAll();        // notify all the kids waiting to swim

    log.swimming();
  }

  @Override
  public synchronized void instructorRests() throws InterruptedException {
    instructorsWaitingToRest++;
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
    cur_cap--;
    instructorsWaitingToRest--;

    // awake threads if needed
    notifyAll();        // notify all the swimmers waiting to swim

    log.resting();
  }

  public synchronized void checks(String err) throws InterruptedException {
      System.out.println("kids presents: " + kidsInPool);
      System.out.println("instructors presents: " + instructorsInPool);
      System.out.println("ki: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));

      System.out.println("\n\n WARNING:" + err + "\n\n");
  }
}
