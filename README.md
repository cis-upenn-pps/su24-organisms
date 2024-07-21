# Overview

Consider an electronic world consisting of an _m_ by _n_ grid. 

Virtual "organisms" can exist on this grid, with an organism able to occupy a cell on the grid. Organisms have energy that can be gained or lost
in a variety of ways. 

When an organism runs out of energy it dies, and vacates the cell it formerly occupied. An organism can have at most _M_ units of energy. An organism may do one of several
things during a virtual time cycle:
* Move one cell horizontally or vertically in any direction. The world wraps, so that an organism traveling off the right edge of the grid appears on the left edge, and similarly for the top and bottom edges. A move uses some energy.
* Stay put and do nothing. This move uses a small amount of energy.
* Reproduce. An organism can split in two, placing a replica of itself on an adjacent square. Each resultant organism has slightly less than half of the initial energy of the original organism since reproduction costs some energy.

An illegal move (such as trying to move or reproduce onto an occupied square) results in a "stay put" outcome.

There will be food scattered over the grid. One unit of food corresponds to u units of energy. Food reproduces according to the following rules:
* An empty square has a small probability _p_ of having a single unit of food "blow in".
* For cells already containing food, but no organisms, every food unit on a nonempty square has a small probability _q_ of doubling. This doubling is independent of other food units on the cell. So, a
cell with three units of food may have anywhere between three and six units of food on the next cycle. Nevertheless, no cell may have more than _K_ units of food at any one time, due to space
constraints.
* Cells with organisms never obtain additional food. In fact, an organism that is on a cell with food, and which has energy no larger than _M-u_, will consume a unit of food and add _u_ units of energy to its store. If an organism is hungry, it can eat one unit of food per cycle until either the food runs out, or it achieves energy greater than _M-u_.

An organism has an external state that is an integer between 0 and 255. This state may be changed by the organism during the course of the simulation. The external state is visible to other organisms, as described below.

An organism can "see" in the four orthogonal directions. An organism gets information about:
* Whether there is food or not on a neighboring square (but not how much food).
* Whether there is another organism on a neighboring square. If there is, then the external state of the neighboring organism is also available.
* The amount of remaining food on the organism's current cell.
* The amount of energy currently possessed by the organism.
* Values of the simulator parameters _s_, _v_, _u_, _M_, and _K_ (see below), but not _p_, _q_, _m_, or _n_.

In this project, you will implemement the behavior of a species of organism. Since there will be many organisms on the grid simultaneously, each will run a separate instance of the code, i.e. each organism is a separate Java object. Each instance will have access only to the local environment of the organism. Organisms are placed randomly on the grid, and don't know their coordinates. The organism can keep a history of local events for the organism if you think that's useful.

Organisms cannot identify their neighbors. Neighbors may be of the same species (i.e., have the same programmed behavior), or of a different species. This will be important for simulations in which multiple organisms from different groups are placed on the same grid. You may want to use the external state to help in identifying organisms of the same species.

Organisms act one-at-a-time from top-left to bottom-right, row by row. We number the top-left cell as (1,1) and the bottom-right cell as (m,n). That means that the state of the virtual world seen by an organism at (x,y) reflects the situation in which all organisms in positions (x',y') lexicographically less than (x,y) have already made their moves, while organisms in positions lexicographically after (x,y) have not yet moved. This convention allows all operations to happen without any need for resolving conflicts between organisms (for example trying to move to the same cell). However, it leads to some
slightly unintuitive effects:
* An organism that is sensed to the west is usually in its final position for the cycle, while an organism sensed to the east may or may not be in its final position.
* An organism moving east can be sensed from the west, while an organism moving west typically cannot be sensed from the east.

There are several goals for this project:
1. Your organism should be able to survive and replicate in an environment where it is the only kind of organism present. This may not be as easy as it sounds. Overpopulation may lead to consumption of all the food. If _p_ is sufficiently small, extinction may ensue. The goal is to achieve the highest long-term stable population.
2. Your organism will be tested in environments containing other organisms. The goal here is primarily to survive, and secondarily to survive in higher numbers than competing organisms. Can your organisms populate the grid faster than their competitors? (Is that even the right strategy given the possibility of everybody going extinct?) How might you program your organisms to "recognize" different organisms based on their state and/or behavior? If you can distinguish members of your species, how might you behave differently to members of another species? Your goal here is not necessarily to obliterate other species, but to maximize the fraction of the population containing your species.

We will provide a Java program that simulates the environment in which the organisms live. The following summarizes the configuration parameters and their typical values:
* _m/n_: horizontal/vertical size (5 to simulator screen width)
* _p_: probability of spontaneous appearance of food (0.001-0.1)
* _q_: probability of food doubling (0.002-0.2)
* _s_: energy consumed in staying put (1; other parameters scale to this)
* _v_: energy consumed in moving or reproducing (2-20)
* _u_: energy per unit of food (10-500)
* _M_: maximum energy per organism (100-1000)
* _K_: maximum food units per cell (10-50)

# Running the Simulator
Once you have downloaded the code in this repo, use IntelliJ to create a new project with the GitHub repo root as the root of the IntelliJ project.

To start the simulator, simply execute the `organisms.ui.GUI.main()` method and you should see the simulator environment appear. 

To run a simulation:
* Press **Reset** to start a new simulation
* Press **Step** to advance one time unit. This will call the _move_ method on each organism in the simulation, as described below.
* Press **100 steps** to advance 100 steps at a time.
* Press **Play** to continuously step through the simulation, which will stop until either the _maxRounds_ configuration has been reached, or until there are no remaining organisms.
* Press **Stop** to end the simulation at any time.

If a cell in the environment is occupied, you will see a colored square with "s" indicating that organism's external state and "e" indicating its energy level.
If the cell is unoccupied, the number that is shown indicates how much food is available there.

To configure the simulation, modify gamemodel.properties; note that this is only read when the simulator is started, and not while it is running.

If you'd like to change the configuration after having started the simulator, click the "Configuration" tab in the top right and modify the value you'd like to change. **Be sure to press Enter/Return** after you change the value, otherwise it will not be modified.

# Creating Your Own Player
Create a class called organisms.gX.GroupXPlayer where X is your group number. This class must implement organisms.OrganismsPlayer, which defines the following methods:
* _register_: this is called when the instance of the organism is first created; it has a reference to the OrganismsGame and also gets its ID/state value from its parent
* _name_: returns a String indicating this species' name
* _color_: returns a Color for use in display in the simulator
* _interactive_: not used; return false
* _externalState_: returns an int that is observable to organisms in adjacent squares
* _move_: returns a Move object that indicates the direction in which this organism would like to move, or if it would like to reproduce

Clearly the _move_ method is most important. This is called by the simulator on each time step of the simulation:
* The first parameter (_foodHere_) indicates the amount of food in the space currently occupied by the organism.
* The second parameter (_energyLeft_) indicates this organism's current energy level.
* The next four parameters (_foodN_, _foodE_, _foodS_, _foodW_) indicate whether or not food is present in adjacent squares. Note that you cannot know how much food is there, only whether there is food present.
* Likewise, the four parameters that follow (_neighborN_, _neighborE_, _neighborS_, _neighborW_) indicate the external state of any organism in an adjacent square, e.g. _neighborN_ returns the value of calling _externalState()_ on the organism above this one. The value will be -1 if there is no organism present in that space.

If your player is _not_ going to reproduce, return the result of calling the static `Move.movement(action)` method, where _action_ is one of the following constants: _Action.STAY_PUT_, _Action.WEST_, _Action.EAST_, _Action.NORTH_, or _Action.SOUTH_, depending on whether the organism wants to stay where it is or whether it wants to move to an adjacent square.

If your player _is_ going to reproduce, return the result of calling `Move.reproduce(action, state)`, where _action_ is one of the _Action_ constants listed above (except for _STAY_PUT_, which is not allowed), and _state_ is the initial value for the offspring's state variable (passed to the new instance's _register_ method).

If your player would like to know the configuration parameters of the environment, call the _s()_, _v()_, _u()_, _M()_, or _K()_ method on the OrganismsGame object passed to the register method.

**You may not use static variables to communicate between instances of your species.** The only communication can be done using the _externalState_ method, keeping in mind that organisms of other species will be able to observe that value as well.

**Also, please do not change any simulator code**, i.e. code in the `organisms` or `organisms.ui` packages. Please notify the Instructor if you feel that a change is necessary.

To add your player to the simulation, list it in the CLASS_LIST and PLAYER_LIST entries in gamemodel.properties; you can have multiple entries separated by commas.

Note: To do logging/debugging, you may use System.out.println or System.err.println to write to the console, but please comment these out before submitting players for the tournaments. You may also call the _print_ or _println_ method in the OrganismsGame object to have text appear in the “messages” console in the simulator.
