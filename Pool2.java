// CSD feb 2015 Juansa Sendra

public class Pool2 extends Pool{ //max kids/instructor
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
    System.out.println("kid want to swim");
    System.out.println("kids presents: " + kidsInPool);
    System.out.println("instructors presents: " + instructorsInPool);
    System.out.println("bef ki: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));
    System.out.println("aft ki: " + ((instructorsInPool == 0) ? 0 : (float) (kidsInPool+1)/instructorsInPool));

    while ( instructorsInPool <= 0 || (instructorsInPool == 0) ? true : max_ki < ((float) (kidsInPool+1)/instructorsInPool)){
      log.waitingToSwim();
      wait();
    }


    // update state
    // update state
    if (((float) (kidsInPool+1)/instructorsInPool) > max_ki)
      System.out.println("\n\n WARNING too many kids in the pool \n\n");

    kidsInPool++;
    System.out.println("ki after kid start swim: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));

    // awake threads if needed

    log.swimming();

    // awake threads if needed

  }

  @Override
  public synchronized void kidRests() {
    System.out.println("kid want to rest");
    System.out.println("ki before kid go to rest: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));
    kidsInPool--;

    // if (kidsInPool <= 0)
    notify();   // let the last instructor rest if he was waiting

    System.out.println("ki after kid go to rest: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));
    log.resting();
  }

  @Override
  public synchronized void instructorSwims() {
    System.out.println("instructor want to swim");
    System.out.println("ki before instr start swim: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));
    instructorsInPool++;

    notifyAll();        // let all the kids waiting to start swimmingAll

    System.out.println("ki after notifyAll the kids: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));

    log.swimming();
  }

  @Override
  public synchronized void instructorRests() throws InterruptedException {
    System.out.println("instructor want to rest");
    System.out.println("instructors presents: " + instructorsInPool);
    System.out.println("kids presents: " + kidsInPool);
    System.out.println("bef ki: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));
    System.out.println("aft ki: " + ((instructorsInPool-1 == 0) ? 0 : (float) kidsInPool/(instructorsInPool-1)));

    while ( (kidsInPool > 0 && instructorsInPool <= 1) || ((instructorsInPool == 0) ? false : (instructorsInPool-1 == 0) ? false : max_ki < ((float) kidsInPool/(instructorsInPool-1))) ){
      log.waitingToRest();
      wait();
    }

    // update state
    if (instructorsInPool == 0)
      System.out.println("\n\n WARNING instructor already resting \n\n");

    if (((instructorsInPool-1 == 0) ? 0 : (float) kidsInPool/(instructorsInPool-1)) > max_ki)
        System.out.println("\n\n WARNING too many kids \n\n");

    if (kidsInPool > 0 && instructorsInPool <= 1)
      System.out.println("\n\n WARNING kids still in the pool \n\n");

    instructorsInPool--;
    System.out.println("ki after instructor go to rest: " + ((instructorsInPool == 0) ? 0 : (float) kidsInPool/instructorsInPool));

    // awake threads if needed

    log.resting();
    // }
    // else


  }
}
