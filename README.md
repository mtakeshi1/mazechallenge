# MAZERUNNER challenge
## Welcome to this code challenge !

### What is expected ?
Write code and send it to me or leave a pullrequest

### What will happen with it ?
I will compare the code with others both by
- cpu benchmark
- minimum moves average
- maybe memory
- 
## Where do i start ?
- GraphicalLauncher: starts the code with a view (the view won't be there when i bench it)
- HeadlessLauncher: is there for you to test faster, and is more similar to the way i run it
- Test both to get a feel of what it does
- rename the : myname_copythis map (choose something [a-z] and unique)
  - open the ChallengeImpl within that package
  - setMap(IMaze maze) is called by the program by default and you should implement it as is done
  - here you have access to : maze.getLineOfSight
  - 'public Direction getMove() {' is called for every move, here you must supply a Direction
  - ONLY use the Imaze interface and the enums to work with from the supplied code

## What should it do ?
You will be presented a maze, every iteration a 'Direction' (enum) will be asked from you as response. \n
You must choose your new direction by storing and computing input from : maze.getLineOfSight. \n
That input will be : a 13x13 grid with 'your' position in the middle. The values are enums(CellType) \n  

Now you must find your way from Start to finish tile !

## Important 
- the mazes are random and not tested that thoroughly. If you get an exception (rare) just restart
- Customize the runners in the : Configuration file you can specify:
  - pizel size: so that you can see more or better
  - Screensize
  - some more
  - Choose what example mazes are run so that you can try and test/debug better.


I hope you will enjoy this challenge ! 
for questions you can ask 'deboder'
