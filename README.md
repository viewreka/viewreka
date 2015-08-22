## VIEWREKA ##

Viewreka is a script-based data visualization tool built on JavaFX. It introduces a Groovy-based domain-specific language (DSL) for configuring your projects. Viewreka lets you create dynamic, interactive and animated charts using data retrieved from various data sources.

Get in touch with us by joining the **[Viewreka mailing list](https://groups.google.com/forum/#!forum/viewreka)**.

The current release supports only relational databases accessed through JDBC. Other data sources (such as CSV or XML files) will be added in the next releases.

### Installation ###

Download [the latest release](https://github.com/viewreka/viewreka/releases/download/v0.1/viewreka-0.1.0.zip) and unzip it into a directory of your choice.

Make sure your `JAVA_HOME` environment variable correctly points to a JDK 8u40 or later.

Start the GUI by executing 'viewreka.sh' or 'viewreka.bat' in the `bin` directory .

In the GUI, open the sample projects found in the directory `samples/worldbank.derby`. The projects are numbered in increasing order of complexity. It is recommended to start with the simplest one (`wb01.viewreka`) and progress incrementally to more complex projects. However, if you're really impatient, go straight to the `wb09.viewreka` project, which is the most complex one.

![](https://github.com/viewreka/viewreka/raw/master/doc/img/screenshot.jpg)
