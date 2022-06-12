# Projet de MIF14 - Bases de données déductives

## Some Information

| | File Path | Note |
| --- | --- | --- |
| **Input** | `PROJECT_ROOT/input.txt` | input file for a DL program. Should contain a valid DL code (see examples below) |
| **Output** | `PROJECT_ROOT/output.txt` | output file, which is generated as a result of a call to `DatalogProgram::output` member function (consider calling `DatalogProgram::stratify` before) |

## Syntax
The syntax of the input file follows the same rules as a valid DL code.

🟢 Consider the following example, that is a valid syntax for the input file:
```prolog
% EDB  
person( 1, 'John'    , 'M' , false ).  
person( 2, 'Jutta'   , 'F' , false ).  
person( 3, 'Natalie' , 'F' , true  ).  
  
% IDB  
man(X) :- person(id, X, 'M', m).  
single_man(X) :- man(X), not husband(X).  
married(X) :- person(id, X, s, true).  
husband(X) :- man(X) , married(X).
```
As you can see, there is no difference compairing to a DL code.

---

🔴 The following example **is not** a valid format for the input file:
```prolog
% EDB  
person( 1, 'John'    , 'M' , False ).  
person( 2, 'Jutta'   , 'F' , false ).  
person( 3, 'Natalie' , 'F' , true  ).  
  
% IDB  
man(x) :- person(id, X, 'M', m).
```
The reasons are:
- The last parameter `False` of the first EDB fact `person` is a variable, because it starts with a capital letter.
> 💡 All EDB facts must have **constants** in parameters.

- The projection in the head of the IDB rule `man` is not a variable, but a constant `x`.
> 💡 All IDB rules must have **variables** in the head.

As a result, these invalid lines will not be recognized as by the parser, and will just be ignored.

---

Here are some other constraints for the input file:
 - Every EDB fact or IDB rule should start on new line.
 - Letters and characters other than alphabetical are not allowed as the first letter of variables and constants.
 - For strings, only single quotes are allowed.
 - Facts' and rules' names should be instantly followed by an opening paranthesis.

## Implementation Details
The parser has been implemented using regular expressions for detecting patterns and capturing groups, which are then extracted and used to create corresponding class instances. For example, an EDB fact will end up as an instance of the `Fact` class, and in the case of an IDB rule, it will result in an instance of the `Rule` class. Every `Rule` object has multiple predicates (`Predicate`). And every `Predicate` has parameters, either a `MutableParameter` that corresponds to a **variable**, or an `ImmutableParameter` representing **constants** (literal constants, strings or integers). If a predicate was prefixed with "*not*", the boolean member attribute of the `Predicate` class will indicate it.