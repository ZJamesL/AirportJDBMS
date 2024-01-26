java 16.0.2 2021-07-20

## Table of Contents
1. [General Info](#general-info)
2. [Technologies](#technologies)
3. [Installation and Compilation of Program](#installation)
4. [Collaboration](#collaboration)
5. [Workload Distribution](#workload-distribution)

### General Info
***
This project provides an interface for interacting with an Airport's database. A user
can connect to their oracle account and use their inputs to query, insert, delete,
and update data records within the Airport database.

Notes: 
-all of the queries are pre-set. The user must choose from the query selection.

## Technologies
***
A list of technologies used within the project:
* [Oracle](username@oracle.aloe): Version 11.2g

## Installation and Compilation
***
To compile and execute this program on lectura, run these commands:
```
$ cd ../path/to/the/file
$ export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
$ javac JDBC.java
$ java JDBC
```
Side information: Must have an Oracle account with username and password.

## Collaboration
***
Instructions on how to collaborate with the project.

Once the user runs the program, they will be prompted to input a username and password.
They must enter their oracle username and password. The user will then see a menu that 
looks like this:
    "Type in '1', '2', '3' or '4' to either insert, delete, update, or query 
    data respectively. Type in '-1' to exit the program."
The user must input 1-4, otherwise, they will be prompted again.

Once a user selects an option, they will be taken to another menu where they 
will be prompted to choose which tables/queries they want to interact with.
Depending on the option chosen, the user will be prompted to input specific
data to insert, query, delete, or update. 

If the user inputs -1 at any time, they will be taken back to the previous menu. If 
the user is already at the main menu shown above, inputting -1 would exit the program.

Notes:
- The user should not write any of the SQL, the JDBC will take the user input and 
run the SQL queries. 
- If no exceptions have occurred, the query, insert, delete, or update were successful.

## Workload Distribution
***

Kiana Thatcher
-Skeleton of the JDBC functionality (connection, menus)
-Insertions into the database
-Queries 1 and 2
-ReadMe.txt
-ER diagram

Zach Lopez
-Updates to the database 
-Many of the validation functions
-Queries 4 and 5
-ER diagram

Mario Marquez
-Deletions from the database
-Query 3
-Creation of the database (tables, constraints, etc)
-ER diagram