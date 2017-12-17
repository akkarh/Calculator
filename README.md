# Calculator

## Part 1 : Implementing the Data Structures
We implemented the data structures we will be needing for part 2 of this project. We implemented a DoubleLinkedList and an ArrayDictionary that implemented the IList interface in Java.

### DoubleLinkedList
Implemented the basic list operations: insert, delete, add, etc. The IList interface can be found in the `src/main/java/datastructures/interfaces/` directory.

#### Design Decisions
* We were required to use a linked list (rather than an array) and we found this to be a reasonable constraint because considering our use case, we would have to frequently resize the array.
* We determined that DoubleLinkedLists offer better runtime over a single linked list because of the front and back pointers.
* We implemented an iterator which allows for an O(1) runtime when traversing the list.
* We used JUnit testing for immediate feedback on the runtime and accuracy of our implementation.

### ArrayDictionary
Implemented the IDictionary interface that can be found in `src/main/java/datastructures/interfaces/` directory.

#### Design Decisions
* Since our underlying data structure is an Array, we double the size of the array once its full. This required us to copy all the elements over to a new array (O(n) runtime) but the amortized analysis of the resize is O(1).
* We implemented a dictionary (map) using an array. This is highly inefficient because of the O(n) runtime, but we were constrained by the requirements in the spec.
* We used JUnit testing for immediate feedback on the runtime and accuracy of our implementation.

## Part 2 : Abstract Syntax Trees
We implemented a part of a simple algebraic calculator. A symbolic algebra calculator is a kind of calculator that does not immediately evaluate the expressions you type in but instead lets you manipulate them symbolically.

![alt text](https://courses.cs.washington.edu/courses/cse373/17au/project1/diagrams/calculator-screenshot.png "Screenshot of the calculator")

### Representing Expressions
* We represented input expressions as trees and this helped us preserve nesting and order of operations in the expression.
* We utlized the methods in the `src/main/java/calculator/ast/AstNode.java` to build the syntax tree for the calculator.

### Simplify
The `simplify` method takes an AstNode of type either variable, number, or operation and simplifies it (only if it produces an exact value). We implemented a recursive method that performs a post-order traversal of the tree. Here is a screenshot of our implementation.





