// KCL example file, shows main features of the language

// Single-line comment
/*
   multi-line comment
*/

/*
 * Primitive types : int,float,double,long,string
 */
int i
i = 1
? i

// if and while
while (i<7) {
    if (i%2 == 0)
        ? i
    else
        ? "i is odd"

    i = i+1
}

/* 
 * Logical exprs : &&, ||
 * Math exprs +,-,*,/,%
 * Relational Exprs : ==, !=, >, >=, <, <=
 */
? ((i+5)*(3+2) % 2) == 0


// Classes + inheritance
/* 
 * cobject is an example of a system class exported from java
 * this is how the constraint net and other system funcionality is made available
 * in KCL
 */
class BaseUnit extends cobject
{
  string installation
  int width
  constrained int SwitchedWays
  constrained int InterrupterWays
}

BaseUnit bunit 
bunit = new BaseUnit

/*
 * Constraints with explanations
 * Relational constraints : ==, !=, >, >=, <, <=
 * Constrained Math exprs : ,-,*,/,%
 */
constrain(bunit) {
    bunit.SwitchedWays+bunit.InterrupterWays <= 4 
       : "Switched Ways + Interrupter Ways must be <= 4"
}

// this is an example of a system method exported from java
enforce_constraints(bunit)

bunit.installation = "Padmount"
bunit.width = 30
bunit.SwitchedWays = 3
bunit.InterrupterWays = 2

? bunit.Violations

bunit.InterrupterWays = 2

? bunit.Violations

? bunit.installation
? bunit.width
? bunit.SwitchedWays
? bunit.InterrupterWays


// User defined methods 
int twice(int i)
{
    twice = i*2
}

? "twice(10)+10 is:"
? twice(10)+10

class Shape { string name }
class Square extends Shape {}
class Circle extends Shape {}
class Rectangle extends Shape {}

void printName(Shape s) 
{ 
    ? "I'm a shape" 
    ? s.name
}

void printName(Square s) 
{ 
    ? "I'm a square" 
    ? s.name
}

void printName(Circle c) 
{ 
    ? "I'm a circle" 
    ? c.name
}


// methods are dispatched polymorphically

Shape shape1

shape1 = new Square
shape1.name = "square1"
printName(shape1)

shape1 = new Circle
shape1.name = "circle1"
printName(shape1)

shape1 = new Rectangle
shape1.name = "rectangle1"
printName(shape1)

Circle shape2
shape2 = new Circle
shape2.name = "circle2"
printName(shape2)


class ShapeWrapper { Shape s }

ShapeWrapper sw
sw = new ShapeWrapper
sw.s = new Circle
sw.s.name="SubCircle"
printName(sw.s)

sw.s = new Rectangle
sw.s.name="Subrectangle"
printName(sw.s)


// Lists 
// For now they only work with primitive types

list l
l = new list

list_add(l,3+5)
list_add(l,8)
list_add(l,"foobar")

? list_get(l,2)
? list_get(l,1)
? list_get(l,0)

// Dude Example

list names
names = new list
list_add(names,"Joe")
list_add(names,"Jack")
list_add(names,"Hans")

list us_cars
us_cars = new list
list_add(us_cars,"Chevy")
list_add(us_cars,"Ford")

list german_cars
german_cars = new list
list_add(german_cars,"BMW")
list_add(german_cars,"Audi")

list all_cars
all_cars = new list
list_add(all_cars,"Chevy")
list_add(all_cars,"Ford")
list_add(all_cars,"BMW")
list_add(all_cars,"Audi")

class Dude extends cobject
{
    constrained string name
    constrained string car
    constrained int children
}

void constrain_dude(Dude d) {
    constrain(d) {
        enforce member_of(d.name,names)
           : "That is not a valid name for a Dude"

        enforce member_of(d.car,all_cars)
           : "That is not a valid car for a Dude"

        enforce_if (d.name=="Joe" || d.name=="Jack") {
            enforce member_of(d.car,us_cars)
                : "That's not a valid car for Joe or Jack"
        }

        enforce_if (d.name=="Hans") {
            enforce member_of(d.car,german_cars)
                : "That's not a valid car for Hans"
        }

        enforce_if ((d.name=="Hans" && d.car=="BMW")
                    || (d.name=="Joe" && d.car=="Ford")) {
            d.children <= 2 : "Hans-BMW or Joe-Ford cannot have more than 2 kids"
        }

    }
}

void printDude(Dude d)
{
    ? "Name:"
    ? d.name
    ? "Car:"
    ? d.car
    ? "Children:"
    ? d.children
    ? "Valid cars for this dude:"
    ? get_valid_values(d.car)
    ? "Violations:"
    ? d.Violations
}

Dude d
d = new Dude
constrain_dude(d)
enforce_constraints(d)

// Name violations
d.name = "Javier"
printDude(d)

// null is not a good car for Joe
d.name = "Joe"
printDude(d)

// still not a good car for Joe
d.car = "BMW"
printDude(d)

// now we're ok
d.name = "Hans"
printDude(d)

// not a good car for Hans
d.car = "Ford"
printDude(d)

// Can't have more than 2 kids
d.name="Hans"
d.car="BMW"
d.children=3
printDude(d)

// now we're ok
d.car="Audi"
printDude(d)

// now we're back to incorrect name, all enforce_ifs are deactivated
d.name = "Javier"
printDude(d)


