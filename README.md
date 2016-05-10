# JavaLinkUtilization
My LinkUtilization program rewritten in Java

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
