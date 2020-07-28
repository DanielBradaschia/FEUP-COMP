# Java-- Compiler

### Group: 2D

* André Mamprin Mori, up201700493@fe.up.pt
* Daniel Gazola Bradaschia, up201700494@fe.up.pt

## Summary:

Our tool converts ``jmm`` code to a ``jasmin`` file by first checking the syntactic errors, building an AST, building the symbol table, checking semantic errors and generating java byte code instructions corresponding to the ``.jmm`` file code.

## Execution:
Please put the test files in the folder ``test/fixtures/public``.

To compile and run the program, run gradle build if you wish to test the program with a specific file

To run you have two options:
```sh
java -cp "./build/classes/java/main/" Main <arguments>
```
or
```sh
java -jar comp2020-2d.jar <arguments>
```
in which ``<arguments>`` is the path to the file you want to test.

More information about setup/execution can be found [here](INSTRUCTIONS.md).


## Dealing With Syntatic Errors:
In order to deal with syntactic errors, our tool only looks for them inside while loop conditions so when it encounters one syntactic malfunction inside a while condition, it will then advance to the next Right Parentheses, increment the global error count variable by 1 and print the error through a ``try{} catch()``. The program will only exit when it reaches the count of 10 errors.


## Semantic Analysis:
For the semantic analysis, our tool checks the semantic rules on expressions. As a consequence, we also check variable initialization, since expressions are only correct if the variables used are initialized and defined.
For the ``+``, ``-``, ``*``, ``/`` operators, we check that both sides of the operation are of type int. These can be variables of type int, constants, function calls that return an integer, array lengths, array accesses since there’s only int arrays or one of the 4 previous operators for nested expressions.
For the ``&&``, ``<``, ``>`` operators, the procedure is similar but we check that both sides are of type boolean, one of these two operators with neste expressions and function calls that return booleans. The ``!`` operator is the same but only has one child which must also be a boolean.
For the array access, we check that the variable used is in fact of type int array and we check if the expression inside the brackets is of type int.
For the dot operator, we check the variable on the left side, if the variable is defined, we check the type. If it’s an int array, then the right side is length field access. If it’s a variable of the class type or the class itself, the right side is a function of the class and semantic analysis is applied to the function call. If the left side is unknown, then we assume that the right side is a static function call to a function of another class or a function that belongs to the superclass. Otherwise, it’s a semantic error.
To apply semantic analysis to a function call, we need to check that the function is being called correctly with the right parameter types in the right order.


## Intermediate Representations (IRs):
For the intermediate representations (IRs), our tool only uses High-Level Intermediate Representation (HLIR),  which is our Abstract Syntactic Tree (AST). The AST is generated on the syntactic analysis phase and further used for the code generation.
The AST generation has in mind the expressions’ precedences that come from the proposed language lexer. This is fundamental to preserve operation order based on priorities when we create the AST. For example, the ``!`` operator has priority over function calls to prevent preceding problems as otherwise, it would generate incorrect operation orders in the AST. The ``*`` operator takes priority over ``+`` as if this wasn’t the case, operations like ``1 + 2 * 3`` would result in 9 and not 7. All precedences can be confirmed on the ``parser.jjt`` file.
This type of precedence definitions allows us to generate correctly the required AST for further use in other phases of our tool.


## Code Generation:
The tool’s code generation iterates the syntactic tree following these steps:
* Library imports
* Class stub definition (name, super class)
* Class global variables definition (name, type)
* Class default constructor (invokes super class’ constructor if there is such, else it calls the constructor of ``java/lang/Object``)
* For each class method:
    * Method stub definition (name, parameter types, return type)
    * Auxiliary register allocation guidelines (to which register each variable belongs)
    * Code processing (explained in depth after)
    * After the method is processed, the stack and local limits are assigned.

On each method’s body, a handler function is called for each node (handle). This handle function checks the node type and handles it accordingly.
To save stack space, when using a binary operation such as ``+`` or ``<``, if the LHS is an Identifier or constant and RHS one is a complex node, the second one is processed first so that the first’s value isn’t kept in the stack when it isn’t needed. In the case of operators where the operator order is important, such as ``<`` and ``/``, a swap operation is called.
On the function calls, if they are from an unknown type object, a static call is done and its parameters and return type signature are assumed based on passed parameters and its usage, for example, on the following code: ``i = unk.call(j)``, where ``i`` and ``j`` are ints, the stub will consider 1 parameter of type int and a return type of int. If the call is alone in a line, void return type is assumed. If unk is of the file’s class, all information is already known, unless the method isn’t defined in the file.
If that happens, if there is a superclass it is assumed that the function is implemented on it and the virtual call is done assuming value as if it was a static call. If the call is alone in a line, if it has the same name as a method implemented on this class but doesn’t have the same signature, the return type is considered the same as the implemented function.
All code generation cases are considered and handled (we believe) as we managed to run all files. Polymorphism, function overload, type assumptions, precedences, stack and local limits and minimization (on the stack), etc...
We believe we could’ve modularized the code a bit handling each node on the respective class using the MULTI option. That wasn’t done due to initial workload separation between members and lack of knowledge of future needs for the development. Anyway, we consider the code generation to be well produced.

## Overview:
To apply semantic analysis on each node we use the subclasses of the class SimpleNode that represent each type of node in our AST. We defined a method called ``applySemanticAnalysis`` on the ``SimpleNode`` class which calls the same method for his children if there are any. The subclasses we wish to apply semantic analysis, we override this method to suit our needs.
To generate the ``jasmin`` code, the AST is iterated via the ``CodeParser`` class’ method called ``generate``, generating the class code followed by each method’s code. On the method body, there is the ``handle`` function that handles each statement following up on its children. Each node type has its own handler. On conditions, a different method is used auxiliating the ``handle`` function, it’s the ``getCondition`` method that focuses on condition-specific operators.
When a function call is being handled, the ``functionCall`` method is called, this function handles all regarding function signatures, parameter and return types.

### Pros:
* Minimizes stack size
* Has a functional and easy to understand AST

### Cons:
* It is not as modularized as it should for code generation

## Task Distribution:
During the implementation of sintatic and semantic analysis the group developed the code together, while each member debugged the code in their own time.
During ``jasmin`` implementation the group worked together to develop and debug a ``.j`` code generator.
