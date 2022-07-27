# MAZERUNNER challenge
## Welcome to this code challenge !

### What is expected ?
Write code to find your way through a maze, while only getting feedback through a 13x13 line of sight array  
that is returned after each move


Basicly you start by:
- running: GraphicalLauncher.java
- implement: ChallengeImpl.java (current code will only run to see something moving on the screen)
- repeat until done

### What will happen with it ?
I will compare the code with others by:
- cpu benchmark
- minimum moves average
- maybe memory

## Where do I start ?
- clone and import into IntelliJ idea (others not tested)
- GraphicalLauncher: starts the code with a view (the view won't be there when I bench it)
  - try this first just to get a feel what it does
- HeadlessLauncher: is there for you to test faster, and is more similar to the way I run it
  - try this seconds and see it's the same as first, but headless
- Test both to get a feel of what it does
- open mc.renamebeforepr package
  - open the **ChallengeImpl** within that package
  - public void handleLineOfSightUpdate(CellType[][] los) { 
    - is called after every move supplying you with the current 360 degree line of sight with you in the middle
    - create you own datastructure and process the (CellType[][] los) that is given
  - public Direction getMove()
    - is called before every move
    - needs you to choose/return a Direction enum (NORTH, SOUTH, EAST, WEST)
- The **ChallengeImpl** should be the only file you **need** to change
  - You can add other files within the same package


## What should it do ?
You will be presented a maze, every iteration a 'Direction' (enum) will be asked from you as response.  
You must choose your new direction by storing and computing input from : maze.getLineOfSight.  
That input will be : a 13x13 grid with 'your' position in the middle. The values are enums(CellType)

Now you must find your way from Start to finish tile !

## When i am done
Rename your folder to something with your (fake)name preferably
Best would be if you could leave a Pull Request to github
If you are really not able you could send me your renamed package with the **file implementing Challenge**

## Important 
- The mazes are random and not tested that thoroughly. If you get an exception (rare) just restart
- Customize the runners in the : Configuration file you can specify:
  - pixel size: so that you can see more or better
  - Screensize
  - FPS to control the speed of the simulation (Frames Per Second)
  - some more
  - Choose what example mazes are run so that you can try and test/debug better.


I hope you will enjoy this challenge ! 
For questions you can ask 'deboder'
