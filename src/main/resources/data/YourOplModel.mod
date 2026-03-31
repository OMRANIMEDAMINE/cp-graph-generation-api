// YourOplModel.mod

int numItems = ...; // Define the number of items in your optimization problem

range Items = 1..numItems; // Define a range for items

// Decision variables
dvar boolean x[Items]; // Binary decision variable for each item

// Parameters
int Value[Items] = ...;  // Value of each item
int Weight[Items] = ...; // Weight of each item
int MaxWeight = ...;     // Maximum weight capacity of the knapsack

// Objective function
maximize TotalValue: sum(i in Items) x[i] * Value[i];

// Constraint
subject to {
sum(i in Items) x[i] * Weight[i] <= MaxWeight;
}
