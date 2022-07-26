# MAZERUNNER challenge
## Welcome to this code challenge !

### What is expected ?
Write code and send it to me or leave a pullrequest

Basicly you start by:
- implementing: ChallengeImpl.java (current code will only run to see something moving on the screen)
- running: GraphicalLauncher.java

### What will happen with it ?
I will compare the code with others both by
- cpu benchmark
- minimum moves average
- maybe memory

## Where do I start ?
- GraphicalLauncher: starts the code with a view (the view won't be there when I bench it)
  - try this first just to get a feel what it does
- HeadlessLauncher: is there for you to test faster, and is more similar to the way I run it
  - try this seconds and see it's the same as first, but headless
- Test both to get a feel of what it does
- Rename the : myname_renamethis map (choose something [a-z] and unique)
  - open the **ChallengeImpl** within that package
  - setMap(IMaze maze) is called by the program by default and you should implement it as is done
  - here you have access to : maze.getLineOfSight
  - you should implement the 'Direction getMove()' for this
  - 'public Direction getMove() {' is called for every move, here you must supply a Direction
  - ONLY use the Imaze interface and the enums to work with from the supplied code
- The **ChallengeImpl** should be the only file you **need** to change
- You can add other files within your self named package


## What should it do ?
You will be presented a maze, every iteration a 'Direction' (enum) will be asked from you as response.  
You must choose your new direction by storing and computing input from : maze.getLineOfSight.  
That input will be : a 13x13 grid with 'your' position in the middle. The values are enums(CellType)

Now you must find your way from Start to finish tile !

## When i am done
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
