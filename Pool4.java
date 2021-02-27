// CSD feb 2013 Juansa Sendra

public class Pool4 extends Pool { //kids cannot enter if there are instructors waiting to exit
  protected int kidsInPool;
  protected int instructorsInPool;
  protected int max_cap;
  protected int cur_cap;
  protected float max_ki;
  protected int cur_ki;

  @Override
  public void init(int ki, int cap) {
    kidsInPool = 0;
    instructorsInPool = 0;
    cur_ki = 0;
    max_ki = (float) ki;
    cur_cap = 0;
    max_cap = cap;
  }

  @Override
  public synchronized void kidSwims() throws InterruptedException {

    while ( instructorsInPool <= 0 || (instructorsInPool == 0) ? true : max_ki < ((float) (kidsInPool+1)/instructorsInPool) || cur_cap >= max_cap){
      log.waitingToSwim();
      wait();
    }


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

    notify();   // let the last instructor rest if he was waiting

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

    notifyAll();        // let all the kids waiting to start swimmingAll

    log.swimming();
  }

  @Override
  public synchronized void instructorRests() throws InterruptedException {
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

    // awake threads if needed

    log.resting();
  }

  public synchronized void checks(String err) throws InterruptedException {
      System.out.println("kids presents: " + kidsInPool);
      System.out.println("instructors presents: " + instructorsInPool);
      System.out.println("ki: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));

      System.out.println("\n\n WARNING:" + err + "\n\n");
  }
}
