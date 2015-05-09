## VIEWREKA ##

Viewreka is a script-based data visualization tool built on JavaFX. It introduces a Groovy-based domain-specific language (DSL) for configuring your projects. Viewreka lets you create dynamic, interactive and animated charts using data retrieved from various data sources.

Get in touch with us by joining the **[Viewreka mailing list](https://groups.google.com/forum/#!forum/viewreka)**.

**The first release is planned for July 2015** and it will only support relational databases accessed through JDBC. Other data sources (such as CSV or XML files) will be added in the next releases.

You can already see Viewreka in action by building the application yourself, as described below:

Clone the repository:

    git clone https://github.com/viewreka/viewreka.git

Make sure your `JAVA_HOME` environment variable correctly points to a JDK 8u40 or later.

Go to the `projects` directory and install the applicaton:

    cd viewreka/projects
    ./gradlew installDist


Go to the installation's `bin` directory and start the GUI:

    cd build/install/viewreka/bin
    ./viewreka

In the GUI, open the sample projects found in the directory `samples/worldbank.derby`. The projects are numbered in increasing order of complexity. It is recommended to start with the simplest one (`wb01.viewreka`) and progress incrementally to more complex projects. However, if you're really impatient, go straight to the `wb09.viewreka` project, which is the most complex one.

The project to be open can also be specified as a command line argument:

    ./viewreka ../samples/worldbank.derby/wb09.viewreka
