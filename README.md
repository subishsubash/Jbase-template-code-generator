<h1>Jbase Template Code Generator</h1>
<h2>What it does</h2>

<p>Jbase Template Code Generator is an automation tool for Temenos Template programming development.  It allows the developer to input the formatted excel to generate its .component, .table, .b, .fields and help text (.xml) files in to the corresponding componentized folder structure and the complete module folder will be downloaded as a zip file.</p>
<p>In Previous methodology, TS owner documents all the attributes required for the application and developer creates an application manually, which is almost taking 1-2 day (approx.) to create the componentized code for an application.</p>
<p>In this approach, we can enable business analyst, TS writer to format the excel as required and get the componentized code in a single hit. Now Developer has to just concentrate on the core business logic.
</p>

----
<h2>Format of Excel</h2>

There are two sheets named Component & Table and Fields
-  Component & Table contains attributes and its values for creation of .b, .component files
-  Fields contains attributes and its values for creation of .fields, .table and helptext files.
1. System will generates empty value for an attribute when it is left blank.
1. In the Fields sheet, Property1 column contains the value for NoInput, Mandatory, NoChange.
1. Property2 column contains the field type (Options, Lookup, SetCheckFile and VirtualFile) and corresponding field values are set into Property Value column.
1. Each term of Field Description and Field Validation contains a corresponding paragraph in Helptext file.
1. Formatted excel consists of various options to enable reserve fields, audit fields, local reference, override fields and modification history if required.

>Refer Sample [Excel](https://github.com/subishsubash/Jbase-template-code-generator/blob/master/Template%20Format.xlsx)

----
<h2>Functional Flow</h2>

1. Uploaded excel file will be processed and converted in to JSON object based on corresponding mapping of attribute names.
1. Obtained JSON object will be given as an input to the another class which will then create a folder structure for required component, along with its .component, .table, .b, .field and help text files in respective folders.

----
<h2>Depolyment</h2>

- Deploy the war in the path \jboss\standalone\deployments and hit the URL 
```
http://localhost:9089/template/
````
- Once the Template programming UI displayed, upload the formatted excel to generate the componentized zip code.

----
<h2>Limitations</h2>

- Only NoInput, Mandatory, NoChange, Lookup, Options, SetCheckFile, VirtualFile properties of T array can be created.
- No validation can happen against T24 standards like field type, application name and SetCheckFile or Lookup options.

----
