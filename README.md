# JavaLinkUtilization
My LinkUtilization program rewritten in Java (JavaFX).
This program has the ability to analyze two SNMP walk files, show all available interfaces (NICs), allow the user to click on one, and show the statistics for that interface.  It is designed to show the delta between the two SNMP walks, specifically link utilization.


ToDo:
- Port to Android.
- Simplify the Controller.BuildCompleteSNMPInterface() method.


Done:
- Converted ifIndex to an integer.
- Add a handler for selecting a row in the table.
- Generate two SNMPInterface class objects for the selected row (one for each walk).
- Calculate utilization for the selected row.
- Output the utilization to a ListView or other JavaFX text output object.
- The ListView is cleared when the 'Show Interfaces' button is pressed.  This is useful when new walk files are chosen.
- Added two new buttons, styled with ellipses, that launch FileChooser windows and accept new input files.
- Added the ability for the input files to be in the wrong order.  My code will use the file with the shorter sysUpTime as the first file.
- Added outbound utilization and total utilization.  The total value seems to be slightly off.  I would like to get more SNMP walk files to test this further.
- Added other stats (discards, errors) for the selected row.
- Changed FileChooser to default to the PWD.
- Added a Label to show a better error when the input files are from different machines.
- Added an icon from https://www.iconfinder.com/
- I have the FXML file nearly ready to use.  Presently, I am doing all layout in Java.
- Changed the ListView to a TableView, and separated the labels and data into their own columns.
- Added an option to save the output in JSON formatted text.
- Added SLF4J logging for most errors and some informational.
- Switched from JSON Simple to Gson.
- Eliminated the "Unchecked generics array creation for varargs parameter" warning by switching from one .setAll() to two .add() lines.
- Switch to a true FXML layout.


Commit history:

1 - Initial Commit

2 - fixed the .gitignore file

3 - I have the UI setup and accepting file names. I have those files being read into ArrayLists. Now I need to parse those ArrayLists and populate a list of valid SNMP interfaces.

4 - More layout work. I now have a ListView that populates with the discovered interfaces. I have not vetted those interfaces yet, as I am just taking the return from walk 1.

5 - The UI is really coming along. I still need to use the output from FindInterfaces to create a collection of SNMPInterface objects that will then populate the TableView object.

6 - I have added a TableView example from http://docs.oracle.com/javafx/2/ui_controls/table-view.htm to this code, and removed my existing table.  I plan on modifying this example to use my data instead.

7 - I cannot clear the error on line 204. I do not see any ObservableList that could be affecting that .setItems() call. I am definitely sending it an ObservableMap, and I do not see why it would need an ObservableList vs. an ObservableMap. I expect any Observable to be valid. Is this correct?

8 - Trying ObservableList

9 - One more change to ObservableList.

Added toString for SNMPInterface

10 - Changed SNMPInterface back to just strings.

11 - Removed one of the sample code sections. I still need to get my data into my TableView. Not sure what I am missing.

12 - I brought my SNMPInterface class into Main.java and renamed the external file (and class), in an effort to minimize the differences between that and the table I imported from the tutorial at http://docs.oracle.com/javafx/2/ui_controls/table-view.htm.  I even tried to populate the table outside of the button event handler.  It still does not populate the table properly.  As best I can tell, the differences between the tutorial table and my table are inconsequential.

13 - I am unable to get my own TableView to work. It is likely something that I am overlooking, but not caught by IntelliJ Idea. This commit will be what I use to post my StackOverflow request for assistance.

14 - The getters and setters in the class need to be public for this to work.
I have removed the tutorial code from this project, and am now moving forward with my own code which populates from the input files.

15 - I have modified my code to use an external class file.
Next, I will convert the ifIndex from a String to an int, so it can be used later in the project.
Once that is done, I will work on making the rows in my table selectable, and calculate the utilization for that selected SNMP interface.  That information will likely be displayed in a ListView.

16 - I have the BuildCompleteSNMPInterface method created, and all of its required code in place.
I now need to take those two objects, and calculate the utilization.

17 - I have the utilization working now. It even accommodates counter 'wrap'.
I now want to output other stats for the selected SNMPInterface, like discards and errors.

18 - I have implemented most of the features that I want this program to be able to do.
Additions to this commit:
-The ListView is cleared when the 'Show Interfaces' button is pressed.  This is useful when new walk files are chosen.
-Added two new buttons, styled with ellipses, that launch FileChooser windows and accept new input files.
-Added the ability for the input files to be in the wrong order.  My code will use the file with the shorter sysUpTime as the first file.
-Added outbound utilization and total utilization.  The total value seems to be slightly off.  I would like to get more SNMP walk files to test this further.
-Added other stats (discards, errors) for the selected row.

19 - I am partway through altering CalculateStatistics to return a <key, value> object instead of a list of strings. Currently, I have a SNMPInterfaceDelta class to represent the calculations. However, I do not know how to get this class object into an ObservableList.
I am undecided on what container to use.  ObservableList is almost a requirement, but it will have to be <String, String>, or something like that.  I feel like that is not the proper way to do this, but I cannot come up with a logical reason for that feeling.   Perhaps I will just commit to <String, String>.

20 - I haven't made any major changes, but I have most of the comments complete.
I think that I will ditch the SNMPInterfaceDelta class, and just return an ObservableList of <String, String>.

21 - Added an icon for the application.
Moved some files into 'model' and 'view' subdirectories.
I have nearly finished the FXML layout.

22 - I've tinkered around with switching to FXML instead of Java layout. I still have not made the switch.

23 - I've rounded the utilization output to 3 decimal places.

24 - I finally have the stats displayed in a TableView.
I think that I may have this program nearly finished.
I still want to switch to true FXML, and to have the option to save the output.

25 - Put the parseLong() in a try/catch block.

26 - Reordered the output. Minor cosmetic changes.

27 - Added totals for discards and errors.

28 - Added option to save output.
You will need JSON Simple in your classpath due to the save functionality.
I also added SLF4J logging for most errors, warning, and even some informational level events.
You will need SLF4J and JSON-Simple libraries in your classpath due to the logging functionality.  I used slf4j-api-1.7.21 and slf4j-jdk14-1.7.21

29 - Added Maven, fixed comments.
I want to move my code to the Maven folder, or move Maven to the existing source location.
I added a number of comments (mostly to the model classes) and fixed a few others.

30 - Maven broke a lot of things.
I am still trying to fix them all.

31 - Rollback of Maven.
And re-instantiation of Maven.
This time, I am slowly implementing Maven.  I will commit more frequently, to make errors easier to repair.

32 - Fixed Maven
I may have fixed Maven.
The project compiles and runs, so I will merge this branch.

33 - Maven is mostly in place.
Maven builds up to 'install' and 'site'.
Files are now in the correct folders.
I want to switch from JSON-simple to GSON, since it supports generics

34 - Added Gson.
This fixed the generics warning that I was getting earlier with JSON Simple.

35 - Eliminated the "Unchecked generics array creation for varargs parameter" warning.

[![CodeFactor](https://www.codefactor.io/repository/github/adamjhowell/javalinkutilization/badge)](https://www.codefactor.io/repository/github/adamjhowell/javalinkutilization)
